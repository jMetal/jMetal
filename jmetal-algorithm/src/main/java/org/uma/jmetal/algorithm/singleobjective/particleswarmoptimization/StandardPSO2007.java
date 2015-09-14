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

package org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.selection.BestSolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.AdaptiveRandomNeighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class implementing a Standard PSO 2007 algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StandardPSO2007 extends AbstractParticleSwarmOptimization<DoubleSolution, DoubleSolution> {
  private DoubleProblem problem;
  private SolutionListEvaluator<DoubleSolution> evaluator ;

  private Operator<List<DoubleSolution>, DoubleSolution> findBestSolution;
  private Comparator<DoubleSolution> fitnessComparator ;
  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private int numberOfParticlesToInform;
  private LocalBestAttribute localBest;
  private NeighborhoodBestAttribute neighborhoodBest;
  private GenericSolutionAttribute<DoubleSolution, double[]> speed;
  private AdaptiveRandomNeighborhood<DoubleSolution> neighborhood;
  private double weight;
  private double c;
  private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
  private DoubleSolution bestFoundParticle ;

  public StandardPSO2007(DoubleProblem problem, int swarmSize, int maxIterations,
                         int numberOfParticlesToInform, SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem ;
    this.swarmSize = swarmSize ;
    this.maxIterations = maxIterations ;
    this.numberOfParticlesToInform = numberOfParticlesToInform ;
    this.evaluator = evaluator ;

    weight = 1.0 / (2.0 * Math.log(2));
    c = 1.0 / 2.0 + Math.log(2);

    fitnessComparator = new ObjectiveComparator<DoubleSolution>(0) ;
    findBestSolution = new BestSolutionSelection<DoubleSolution>(fitnessComparator) ;

    //speed = new double[swarmSize][problem.getNumberOfVariables()];
    speed = new GenericSolutionAttribute<DoubleSolution, double[]>() ;
    localBest = new LocalBestAttribute() ;
    neighborhoodBest = new NeighborhoodBestAttribute() ;

    bestFoundParticle = null ;
    neighborhood = new AdaptiveRandomNeighborhood<DoubleSolution>(swarmSize, this.numberOfParticlesToInform);
  }

  @Override protected void initProgress() {
    iterations = 1;
  }

  @Override protected void updateProgress() {
    iterations += 1;
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }


  @Override protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      //speed.setAttribute(newSolution, new double[problem.getNumberOfVariables()]);
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override
  protected void initializeLeaders(List<DoubleSolution> swarm) {

  }

  @Override
  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      localBest.setAttribute(swarm.get(i), swarm.get(i));
    }

    for (int i = 0; i < swarm.size(); i++) {
      neighborhoodBest.setAttribute(swarm.get(i), getNeighborBest(i));
    }
  }

  @Override
  protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      speed.setAttribute(particle, new double[problem.getNumberOfVariables()]);
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed.getAttribute(particle)[j] =
                (randomGenerator.nextDouble(particle.getLowerBound(j), particle.getUpperBound(j))
                        - particle.getVariableValue(j)) / 2.0;
      }
    }
  }

  @Override
  protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2;

    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);

      r1 = randomGenerator.nextDouble(0, c);
      r2 = randomGenerator.nextDouble(0, c);

      if (localBest.getAttribute(particle) != neighborhoodBest.getAttribute(particle)) {
        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
          speed.getAttribute(particle)[var] = weight * speed.getAttribute(particle)[var] +
                  r1 * (localBest.getAttribute(particle).getVariableValue(var) - particle.getVariableValue(var)) +
                  r2 * (neighborhoodBest.getAttribute(particle).getVariableValue(var) - particle.getVariableValue(var));
        }
      } else {
        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
          speed.getAttribute(particle)[var] = weight * speed.getAttribute(particle)[var] +
                  r1 * (localBest.getAttribute(particle).getVariableValue(var) -
                          particle.getVariableValue(var));
        }
      }
    }
  }

  @Override
  protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        particle.setVariableValue(var, particle.getVariableValue(var) + speed.getAttribute(particle)[var]);

        if (particle.getVariableValue(var) < problem.getLowerBound(var)) {
          particle.setVariableValue(var, problem.getLowerBound(var));
          speed.getAttribute(particle)[var] = 0;
        }
        if (particle.getVariableValue(var) > problem.getUpperBound(var)) {
          particle.setVariableValue(var, problem.getUpperBound(var));
          speed.getAttribute(particle)[var] = 0;
        }
      }
    }
  }

  @Override
  protected void perturbation(List<DoubleSolution> swarm) {
  }

  @Override
  protected void updateLeaders(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      neighborhoodBest.setAttribute(swarm.get(i), getNeighborBest(i));
    }

    DoubleSolution bestSolution = findBestSolution.execute(swarm) ;

    if (bestFoundParticle == null) {
      bestFoundParticle = bestSolution ;
    } else {
      if (bestSolution.getObjective(0) == bestFoundParticle.getObjective(0)) {
        neighborhood.recompute();
      }
      if (bestSolution.getObjective(0) < bestFoundParticle.getObjective(0)) {
        bestFoundParticle = bestSolution;
      }
    }
  }

  @Override
  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      if ((particle.getObjective(0) < localBest.getAttribute(particle).getObjective(0))) {
        localBest.setAttribute(particle, (DoubleSolution)particle.copy()) ;
      }
    }
  }

  @Override
  public DoubleSolution getResult() {
    return bestFoundParticle;
  }

  private DoubleSolution getNeighborBest(int i) {
    DoubleSolution bestLocalBestSolution = null;

    for (DoubleSolution neighbor : neighborhood.getNeighbors(getSwarm(), i)) {
      if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0)
              > localBest.getAttribute(neighbor).getObjective(0))) {
        bestLocalBestSolution = localBest.getAttribute(neighbor);
      }
    }
    return bestLocalBestSolution;
  }

  /**
   * Solution attribute representing the local best of a particle
   */
  private class LocalBestAttribute extends GenericSolutionAttribute<DoubleSolution,DoubleSolution> {
  }

  /**
   * Solution attribute representing the neighborhood best of a particle
   */
  private class NeighborhoodBestAttribute extends GenericSolutionAttribute<DoubleSolution,DoubleSolution> {
  }
}
