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

package org.uma.jmetal45.metaheuristic.singleobjective.particleswarmoptimization;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Operator;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.operator.selection.BestSolutionSelection;
import org.uma.jmetal45.util.AdaptiveRandomNeighborhood;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.comparator.ObjectiveComparator;
import org.uma.jmetal45.util.random.PseudoRandom;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * Class implementing a Standard PSO 2007 algorithm
 */
public class StandardPSO2007 implements Algorithm {
  private Problem problem ;

  private Operator findBestSolution;
  private Comparator fitnessComparator ;
  private SolutionSet swarm;
  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private int numberOfParticlesToInform; 
  private Solution[] localBest;
  private Solution[] neighborhoodBest;
  private double[][] speed;
  private AdaptiveRandomNeighborhood neighborhood;
  private double weight;
  private double c;

  /** Constructor */
  public StandardPSO2007(Builder builder) {
    super();
    this.problem = builder.problem ;
    this.swarmSize = builder.swarmSize ;
    this.maxIterations = builder.maxIterations ;
    this.numberOfParticlesToInform = builder.numberOfParticlesToInform ;

    weight = 1.0 / (2.0 * Math.log(2)); 
    c = 1.0 / 2.0 + Math.log(2); 

    fitnessComparator = new ObjectiveComparator(0) ; 
    findBestSolution = new BestSolutionSelection.Builder(fitnessComparator).build();

    swarm = new SolutionSet(swarmSize);
    localBest = new Solution[swarmSize];
    neighborhoodBest = new Solution[swarmSize];

    speed = new double[swarmSize][problem.getNumberOfVariables()];
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
  
  public double getWeight() {
    return weight;
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

    neighborhood = new AdaptiveRandomNeighborhood(swarm, numberOfParticlesToInform);

    iterations = 1;

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

    while (iterations < maxIterations) {
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

      iterations++;

      Double bestCurrentFitness = swarm.best(fitnessComparator).getObjective(0);

      if (bestCurrentFitness == bestFoundFitness) {
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
          speed[i][var] = weight * speed[i][var] +
            r1 * (localBest.getValue(var) - particle.getValue(var)) +
            r2 * (neighborhoodBest.getValue(var) - particle.getValue(var));
        }
      } else {
        for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
          speed[i][var] = weight * speed[i][var] +
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
