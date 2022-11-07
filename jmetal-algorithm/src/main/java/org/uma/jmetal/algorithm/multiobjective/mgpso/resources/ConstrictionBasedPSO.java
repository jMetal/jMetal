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

package org.uma.jmetal.algorithm.multiobjective.mgpso.resources;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

/**
 * Class implementing a simple PSO algorithm incorporating the constriction factor
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ConstrictionBasedPSO extends AbstractParticleSwarmOptimization<DoubleSolution, DoubleSolution> {
  private DoubleProblem problem;
  private SolutionListEvaluator<DoubleSolution> evaluator;

  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private DoubleSolution[] localBest;
  private double[][] speed;
  private GenericSolutionAttribute<DoubleSolution, Integer> positionInSwarm;
  private double weight;
  private JMetalRandom randomGenerator = JMetalRandom.getInstance();
  private DoubleSolution globalBestParticle;

  private int objectiveId;

  /**
   * Constructor
   *
   * @param problem
   * @param objectiveId This field indicates which objective, in the case of a multi-objective problem,
   *                    is selected to be optimized.
   * @param swarmSize
   * @param maxIterations
   * @param evaluator
   */
  public ConstrictionBasedPSO(DoubleProblem problem, int objectiveId, int swarmSize, int maxIterations,
                              SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.maxIterations = maxIterations;
    this.evaluator = evaluator;
    this.objectiveId = objectiveId;

    weight = 1.0 / (2.0 * Math.log(2));

    /* Posible error, comentar a Antonio y Juanjo */
    localBest = new DoubleSolution[swarmSize];
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    positionInSwarm = new GenericSolutionAttribute<DoubleSolution, Integer>();

    globalBestParticle = null;
  }

  /**
   * Constructor
   *
   * @param problem
   * @param swarmSize
   * @param maxIterations
   * @param evaluator
   */
  public ConstrictionBasedPSO(DoubleProblem problem, int swarmSize, int maxIterations,
                              SolutionListEvaluator<DoubleSolution> evaluator) {
    this(problem, 0, swarmSize, maxIterations, evaluator);
  }

  @Override
  public void initProgress() {
    iterations = 1;
  }

  @Override
  public void updateProgress() {
    iterations += 1;
  }

  @Override
  public boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  public List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      positionInSwarm.setAttribute(newSolution, i);
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  public List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override
  public void initializeGlobalBest(List<DoubleSolution> swarm) {
    globalBestParticle = swarm.get(0) ;
    for (int i = 0; i < swarm.size(); i++) {
      if (globalBestParticle.objectives()[objectiveId] > swarm.get(i).objectives()[objectiveId]) {
        globalBestParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }

  @Override
  public void initializeParticleBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      localBest[i] = (DoubleSolution) swarm.get(i).copy();
    }
  }

  @Override
  public void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override
  public void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2 ;
    double c1, c2 ;
    double constrictionFactor ;

    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);

      r1 = randomGenerator.nextDouble(0, 1.0);
      r2 = randomGenerator.nextDouble(0, 1.0);

      c1 = 2.05 ;
      c2 = 2.05 ;
      constrictionFactor = 0.729 ;

      for (int var = 0; var < particle.variables().size(); var++) {
        speed[i][var] = constrictionFactor * (weight * speed[i][var] +
                c1 * r1 * (localBest[i].variables().get(var) - particle.variables().get(var)) +
                c2 * r2 * (globalBestParticle.variables().get(var) - particle.variables().get(var)));
      }
    }
  }

  @Override
  public void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int var = 0; var < particle.variables().size(); var++) {
        particle.variables().set(var, particle.variables().get(var) + speed[i][var]);

        if (particle.variables().get(var) < problem.getVariableBounds().get(var).getLowerBound()) {
          particle.variables().set(var, problem.getVariableBounds().get(var).getLowerBound());
          speed[i][var] = 0;
        }
        if (particle.variables().get(var) > problem.getVariableBounds().get(var).getUpperBound()) {
          particle.variables().set(var, problem.getVariableBounds().get(var).getUpperBound());
          speed[i][var] = 0;
        }
      }
    }
  }

  @Override
  public void perturbation(List<DoubleSolution> swarm) {
  }

  @Override
  public void updateGlobalBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if (globalBestParticle.objectives()[objectiveId] > swarm.get(i).objectives()[objectiveId]) {
        globalBestParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }

  @Override
  public void updateParticleBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((swarm.get(i).objectives()[objectiveId] < localBest[i].objectives()[0])) {
        localBest[i] = (DoubleSolution) swarm.get(i).copy();
      }
    }
  }

  @Override
  public DoubleSolution getResult() {
    return globalBestParticle;
  }

  /* Getters */
  public double[][]getSwarmSpeedMatrix() {
    return speed ;
  }

  public DoubleSolution[] getLocalBest() {
    return localBest ;
  }

  @Override
  public String getName() {
    return "CPSO";
  }

  @Override
  public String getDescription() {
    return "Constriction based PSO";
  }
}