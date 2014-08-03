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

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.IConstraintViolationComparator;
import org.uma.jmetal.util.comparator.ViolationThresholdComparator;
import org.uma.jmetal.util.random.PseudoRandom;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Antonio J. Nebro on 02/08/14.
 */
public class ConstraintMOEAD extends MOEADTemplate {
  private static final long serialVersionUID = 6039274453532436343L;

  IConstraintViolationComparator comparator = new ViolationThresholdComparator();

  /** Constructor */
  protected ConstraintMOEAD(Builder builder) {
    super(builder) ;
  }

  /** Execute() method */
  @Override
  public SolutionSet execute()
    throws JMetalException, ClassNotFoundException, IOException {
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
        problem_.evaluate(child);
        problem_.evaluateConstraints(child);

        evaluations++;

        updateIdealPoint(child);
        updateProblem(child, subProblemId, neighborType);
      }

      ((ViolationThresholdComparator) comparator).updateThreshold(population);
    } while (evaluations < maxEvaluations);

    return population;
  }

  /**
   *
   */
  @Override
  public void initializePopulation() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < populationSize; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    }
  }

  /**
   *
   */
  void initializeIdealPoint() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      idealPoint[i] = 1.0e+30;
      evaluations++;
    }

    for (int i = 0; i < populationSize; i++) {
      updateReference(population.get(i));
    }
  }

  /**
   *
   */
  public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
    // list : the set of the indexes of selected mating parents
    // cid  : the id of current subproblem
    // size : the number of selected mating parents
    // type : 1 - neighborhood; otherwise - whole population
    int ss;
    int r;
    int p;

    ss = neighborhood[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood[cid][r];
        //p = population[cid].table[r];
      } else {
        p = PseudoRandom.randInt(0, populationSize - 1);
      }
      boolean flag = true;
      for (Integer aList : list) {

        if (aList == p) {
          // p is in the list
          flag = false;
          break;
        }
      }

      if (flag) {
        list.addElement(p);
      }
    }
  }

  /**
   * @param individual
   */

  void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < idealPoint[n]) {
        idealPoint[n] = individual.getObjective(n);
      }
    }
  }

  /**
   * @param individual
   * @param id
   * @param type
   */
  void updateProblem(Solution individual, int id, NeighborType type) throws JMetalException {
    // indiv: child solutiontype
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == NeighborType.NEIGHBOR) {
      size = neighborhood[id].length;
    } else {
      size = population.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == NeighborType.NEIGHBOR) {
        k = neighborhood[id][perm[i]];
      } else {
        k =
          perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(population.get(k), lambda[k]);
      f2 = fitnessFunction(individual, lambda[k]);


      /***** This part is new according to the violation of constraints *****/
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
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }

  double fitnessFunction(Solution individual, double[] lambda) throws JMetalException {
    double fitness;
    fitness = 0.0;

    if ("_TCHE1".equals(functionType)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - idealPoint[n]);

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      }

      fitness = maxFun;
    }
    else {
      throw new JMetalException("cMOEAD.fitnessFunction: unknown type " + functionType);
    }
    return fitness;
  }
}

