//  ConstraintMOEAD.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.metaheuristic.multiobjective.moead;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.IConstraintViolationComparator;
import org.uma.jmetal.util.comparator.ViolationThresholdComparator;

import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 02/08/14.
 */
public class ConstraintMOEAD extends MOEADTemplate implements Algorithm {
  private static final long serialVersionUID = 6039274453532436343L;

  IConstraintViolationComparator comparator = new ViolationThresholdComparator();

  /** Constructor */
  protected ConstraintMOEAD(Builder builder) {
    super(builder) ;
  }

  /** Execute() method */
  @Override
  public SolutionSet execute() throws JMetalException, ClassNotFoundException {
    evaluations = 0 ;

    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();

    ((ViolationThresholdComparator) this.comparator).updateThreshold(this.population);

    initializeIdealPoint();

    do {
      int[] permutation = new int[populationSize];
      Utils.randomPermutation(permutation, populationSize);

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];

        NeighborType neighborType = chooseNeighborType() ;
        Solution[] parents = parentSelection(subProblemId, neighborType) ;
        Solution child = (Solution) crossover.execute(new Object[] {population.get(subProblemId), parents});

        mutation.execute(child);
        problem.evaluate(child);
        problem.evaluateConstraints(child);

        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }

      ((ViolationThresholdComparator) comparator).updateThreshold(population);
    } while (!stoppingCondition());

    return population;
  }

  @Override
  public void initializePopulation() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < populationSize; i++) {
      Solution newSolution = new Solution(problem);

      problem.evaluate(newSolution);
      problem.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    }
  }

  @Override
  void updateNeighborhood(Solution individual, int subproblemId, NeighborType neighborType) throws JMetalException {
    int size;
    int time;

    time = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subproblemId].length;
    } else {
      size = population.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subproblemId][perm[i]];
      } else {
        k = perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population.get(k), lambda[k]);
      f2 = fitnessFunction(individual, lambda[k]);

      if (comparator.needToCompare(population.get(k), individual)) {
        int flag = comparator.compare(population.get(k), individual);
        if (flag == 1) {
          population.replace(k, new Solution(individual));
        } else if (flag == 0) {
          if (f2 < f1) {
            population.replace(k, new Solution(individual));
            time++;
          }
        }
      } else {
        if (f2 < f1) {
          population.replace(k, new Solution(individual));
          time++;
        }
      }

      if (time >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }
}

