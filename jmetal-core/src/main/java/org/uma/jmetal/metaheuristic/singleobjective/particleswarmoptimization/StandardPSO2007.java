//  StandardPSO2007.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.metaheuristic.singleobjective.particleswarmoptimization;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.operator.selection.BestSolutionSelection;
import org.uma.jmetal.util.AdaptiveRandomNeighborhood;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class implementing a Standard PSO 2007 algorithm
 */
public class StandardPSO2007 extends Algorithm {
  Operator findBestSolution;
  Comparator fitnessComparator ;
  private SolutionSet swarm;
  private int swarmSize;
  private int maxIterations;
  private int iteration;
  private int numberOfParticlesToInform; 
  private Solution[] localBest;
  private Solution[] neighborhoodBest;
  private double[][] speed;
  private AdaptiveRandomNeighborhood neighborhood;
  private double w;
  private double c;

  /** Constructor */
  public StandardPSO2007(Builder builder) {
    super();
    this.problem = builder.problem ;
    this.swarmSize = builder.swarmSize ;
    this.maxIterations = builder.maxIterations ;
    this.numberOfParticlesToInform = builder.numberOfParticlesToInform ;

    w = 1.0 / (2.0 * Math.log(2)); 
    c = 1.0 / 2.0 + Math.log(2); 

    fitnessComparator = new ObjectiveComparator(0) ; 
    findBestSolution = new BestSolutionSelection.Builder(fitnessComparator).build();

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];
    neighborhoodBest = new Solution[swarmSize];

    speed = new double[swarmSize][problem.getNumberOfVariables()];
    neighborhood = new AdaptiveRandomNeighborhood(swarm, numberOfParticlesToInform);
  }

  /* Getters */
  public int getSwarmSize() {
  	return swarmSize ;
  }
  
  public int getNumberOfParticlesToInform() {
  	return numberOfParticlesToInform ;
  }
  
  public int getMaxIterations() {
  	return maxIterations ;
  }
  
  public double getW() {
    return w;
  }

  public double getC() {
    return c;
  }

  /** Builder class */
  public static class Builder {
  	private Problem problem ;
    private int swarmSize;
    private int maxIterations;
    private int numberOfParticlesToInform; 
    
    public Builder(Problem problem) {
    	this.problem = problem ;
    	swarmSize = 10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())) ;
    	maxIterations = 80000 ;
    	numberOfParticlesToInform = 3 ;
    }
    
    public Builder setSwarmSize(int swarmSize) {
    	this.swarmSize = swarmSize ;
    	
    	return this ;
    }
    
    public Builder setMaxIterations(int maxIterations) {
    	this.maxIterations = maxIterations ;
    	
    	return this ;
    }
    
    public Builder setNumberOfParticlesToInform(int numberOfParticlesToInform) {
    	this.numberOfParticlesToInform = numberOfParticlesToInform ;
    	
    	return this ;
    }
    
    public StandardPSO2007 build() {
    	return new StandardPSO2007(this) ;
    }
  }
  
  /** Execute() method */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < swarmSize; i++) {
      Solution particle = new Solution(problem);
      problem.evaluate(particle);
      swarm.add(particle);
    }

    JMetalLogger.logger.info("SwarmSize: " + swarmSize);
    JMetalLogger.logger.info("Swarm size: " + swarm.size());
    JMetalLogger.logger.info("list size: " + neighborhood.getNeighborhood().size());

    iteration = 1;

    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] =
          (PseudoRandom.randDouble(particle.getLowerBound(j), particle.getUpperBound(j))
            - particle.getValue(j)) / 2.0;
      }
    }

    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      localBest[i] = particle;
    }

    for (int i = 0; i < swarm.size(); i++) {
      neighborhoodBest[i] = getNeighborBest(i);
    }

    swarm.printObjectives();
    Double b = swarm.best(fitnessComparator).getObjective(0);
   
    double bestFoundFitness = Double.MAX_VALUE;

    while (iteration < maxIterations) {
      //Compute the speed
      computeSpeed();

      //Compute the new positions for the swarm
      computeNewPositions();

      //Evaluate the new swarm in new positions
      for (int i = 0; i < swarm.size(); i++) {
        Solution particle = swarm.get(i);
        problem.evaluate(particle);
      }

      //Update the memory of the particles
      for (int i = 0; i < swarm.size(); i++) {
        if ((swarm.get(i).getObjective(0) < localBest[i].getObjective(0))) {
          Solution particle = new Solution(swarm.get(i));
          localBest[i] = particle;
        }
      }
      for (int i = 0; i < swarm.size(); i++) {
        neighborhoodBest[i] = getNeighborBest(i);
      }

      iteration++;

      Double bestCurrentFitness = swarm.best(fitnessComparator).getObjective(0);
      JMetalLogger.logger.info("Best: " + bestCurrentFitness);

      if (bestCurrentFitness == bestFoundFitness) {
        JMetalLogger.logger.info("Recomputing");
        neighborhood.recompute();
      }

      if (bestCurrentFitness < bestFoundFitness) {
        bestFoundFitness = bestCurrentFitness;
      }
    }

    // Return a population with the best individual
    SolutionSet resultPopulation = new SolutionSet(1);
    resultPopulation.add(swarm.get((Integer) findBestSolution.execute(swarm)));

    return resultPopulation;
  }

  private Solution getNeighborBest(int i) {
    Solution bestLocalBestSolution = null;

    try {
      for (int index : neighborhood.getNeighbors(i)) {
        if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0)
          > localBest[index].getObjective(0))) {
          bestLocalBestSolution = localBest[index];
        }
      }
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }

    return bestLocalBestSolution;
  }

  private void computeSpeed() throws JMetalException {
    double r1, r2;

    for (int i = 0; i < swarmSize; i++) {

      XReal particle = new XReal(swarm.get(i));
      XReal localBest = new XReal(this.localBest[i]);
      XReal neighborhoodBest = new XReal(this.neighborhoodBest[i]);

      r1 = PseudoRandom.randDouble(0, c);
      r2 = PseudoRandom.randDouble(0, c);

      if (this.localBest[i] != this.neighborhoodBest[i]) {
        for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
          speed[i][var] = w * speed[i][var] +
            r1 * (localBest.getValue(var) - particle.getValue(var)) +
            r2 * (neighborhoodBest.getValue(var) - particle.getValue(var));
        }
      } else {
        for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
          speed[i][var] = w * speed[i][var] +
            r1 * (localBest.getValue(var) - particle.getValue(var));
        }
      }
    }
  }

  private void computeNewPositions() throws JMetalException {
    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      for (int var = 0; var < particle.size(); var++) {
        particle.setValue(var, particle.getValue(var) + speed[i][var]);

        if (particle.getValue(var) < problem.getLowerLimit(var)) {
          particle.setValue(var, problem.getLowerLimit(var));
          speed[i][var] = 0;
        }
        if (particle.getValue(var) > problem.getUpperLimit(var)) {
          particle.setValue(var, problem.getUpperLimit(var));
          speed[i][var] = 0;
        }
      }
    }
  }
}
