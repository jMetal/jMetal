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

package org.uma.jmetal.algorithm.multiobjective.mgpso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.selection.impl.BestSolutionSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class implementing a Standard PSO 2007 algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MGPSOSubSwarm extends
    AbstractSubSwarmParticleSwarmOptimization<DoubleSolution, DoubleSolution> {

  private DoubleProblem problem;
  private List<DoubleSolution> swarm ;
  private SolutionListEvaluator<DoubleSolution> evaluator;
  private Operator<List<DoubleSolution>, DoubleSolution> findBestSolution;
  private Comparator<DoubleSolution> fitnessComparator;
  private int swarmSize;
  private int maxEvaluations;
  private int evaluations;
  private DoubleSolution[] localBest;
  private DoubleSolution globalBest;
  private double[][] speed;
  private Neighborhood<DoubleSolution> neighborhood;
  private JMetalRandom randomGenerator = JMetalRandom.getInstance();
  private int objectiveId;
  private BoundedArchive<DoubleSolution> archive;

  /**
   * Constructor
   *
   * @param problem
   * @param objectiveId   This field indicates which objective, in the case of a multi-objective
   *                      problem, is selected to be optimized.
   * @param swarmSize
   */
  public MGPSOSubSwarm(DoubleProblem problem, int objectiveId, int swarmSize, int maxEvaluations,
      Neighborhood<DoubleSolution> neighborhood,
      BoundedArchive<DoubleSolution> archive) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.maxEvaluations = maxEvaluations;
    this.evaluator = new SequentialSolutionListEvaluator<>();
    this.objectiveId = objectiveId;
    this.archive = archive;

    System.out.println("Swarm " + objectiveId + ". MaxEvals: " + maxEvaluations + ". Size: " + swarmSize);

    fitnessComparator = new ObjectiveComparator<>(objectiveId);
    findBestSolution = new BestSolutionSelection<>(fitnessComparator);

    localBest = new DoubleSolution[swarmSize];
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    this.neighborhood = neighborhood;
  }

  @Override
  public void initProgress() {
    evaluations = swarmSize;
  }

  @Override
  public void updateProgress() {
    evaluations += swarmSize;
  }

  @Override
  public boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  public void createInitialSwarm() {
    swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }
  }

  @Override
  public void evaluateSwarm() {
    swarm = evaluator.evaluate(swarm, problem);
    for (DoubleSolution particle: swarm) {
      archive.add((DoubleSolution) particle.copy()) ;
    }
  }

  @Override
  public void initializeGlobalBest() {
    updateGlobalBest();
  }

  @Override
  public void initializeParticleBest() {
    for (int i = 0; i < swarm.size(); i++) {
      localBest[i] = (DoubleSolution) swarm.get(i).copy();
    }
  }

  @Override
  public void initializeVelocity() {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] =
            (randomGenerator.nextDouble(particle.getBounds(j).getLowerBound(),
                particle.getBounds(j).getUpperBound())
                - particle.variables().get(j)) / 2.0;
      }
    }
  }

  @Override
  public void updateVelocity() {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);

      double r1 = randomGenerator.nextDouble(0.0, 1.0);
      double r2 = randomGenerator.nextDouble(0.0, 1.0);
      double r3 = randomGenerator.nextDouble(0.0, 1.0);

      double c1 = 1.8;
      double c2 = 1.1;
      double c3 = 1.8;

      double weight = 0.5 ;
      double lambda = 0.5;

      DoubleSolution archiveGuide = selectArchiveGuide() ;

      for (int j = 0; j < particle.variables().size(); j++) {
        speed[i][j] = weight * speed[i][j] +
            r1 * c1*(localBest[i].variables().get(j) - particle.variables().get(j)) +
            lambda * r2 * c2 * (globalBest.variables().get(j) - particle.variables().get(j) +
                (1 - lambda) * r3 * c3 * (archiveGuide.variables().get(j) - particle.variables().get(j))) ;
      }
    }
  }

  protected DoubleSolution selectArchiveGuide() {
    DoubleSolution winnerParticle;
    int pos1 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    var one = archive.getSolutionList().get(pos1);
    var two = archive.getSolutionList().get(pos2);

    if (archive.getComparator().compare(one, two) < 1) {
      winnerParticle = (DoubleSolution) one.copy();
    } else {
      winnerParticle = (DoubleSolution) two.copy();
    }

    return winnerParticle;
  }

  @Override
  public void updatePosition() {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.variables().size(); j++) {
        particle.variables().set(j, particle.variables().get(j) + speed[i][j]);

        if (particle.variables().get(j) < problem.getVariableBounds().get(j).getLowerBound()) {
          particle.variables().set(j, problem.getVariableBounds().get(j).getLowerBound());
          speed[i][j] = 0;
        }
        if (particle.variables().get(j) > problem.getVariableBounds().get(j).getUpperBound()) {
          particle.variables().set(j, problem.getVariableBounds().get(j).getUpperBound());
          speed[i][j] = 0;
        }
      }
    }
  }

  @Override
  public void perturbation() {
    /*
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    for (DoubleSolution particle : swarm) {
      mutation.execute(particle) ;
    }
    */
  }

  @Override
  public void updateGlobalBest() {
    globalBest = findBestSolution.execute(swarm);
  }

  @Override
  public void updateParticleBest() {
    for (int i = 0; i < swarm.size(); i++) {
      List<DoubleSolution> neighbours = neighborhood.getNeighbors(swarm, i) ;
      var localBestSolution = localBest[i] ;
      neighbours.add(swarm.get(i)) ;

      for (DoubleSolution particle: neighbours) {
        if (fitnessComparator.compare(particle, localBestSolution) < 0) {
          localBestSolution = particle ;
        }
      }

      if ((swarm.get(i).objectives()[objectiveId] < localBest[i].objectives()[0])) {
        localBest[i] = (DoubleSolution) localBestSolution.copy() ;
      }
    }
  }

  @Override
  public DoubleSolution getResult() {
    return globalBest;
  }

  /* Getters */
  public double[][] getSwarmSpeedMatrix() {
    return speed;
  }

  public DoubleSolution[] getLocalBest() {
    return localBest;
  }

  @Override
  public String getName() {
    return "MGPSO";
  }

  @Override
  public String getDescription() {
    return "MGPSO";
  }
}