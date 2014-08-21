//  MOEADDRA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.ArrayList;
import java.util.List;

public class MOEADDRA extends MOEADTemplate {
  private static final long serialVersionUID = -4289052728188335534L;

  private Solution[] savedValues;
  private double[] utility;
  private int[] frequency;
  
  /** Constructor */
  protected MOEADDRA(Builder builder) {
    super(builder) ;
    
    savedValues = new Solution[populationSize];
    utility = new double[populationSize];
    frequency = new int[populationSize];
    for (int i = 0; i < utility.length; i++) {
      utility[i] = 1.0;
      frequency[i] = 0;
    }
  }

  /** Execute() method */
  public SolutionSet execute() throws ClassNotFoundException {
    evaluations = 0 ;

    initializeUniformWeight();
    initializeNeighborhood();
    initializePopulation();
    initializeIdealPoint();

    int generation = 0 ;
    do {
      int[] permutation = new int[populationSize];
      Utils.randomPermutation(permutation, populationSize);
      List<Integer> order = tourSelection(10);

      for (int i = 0; i < order.size(); i++) {
        int subProblemId = order.get(i);
        frequency[subProblemId]++;
        
        NeighborType neighborType = chooseNeighborType() ;
        Solution[] parents = parentSelection(subProblemId, neighborType) ;
        Solution child = (Solution) crossover.execute(new Object[] {population.get(subProblemId), parents});

        mutation.execute(child);
        problem.evaluate(child);

        evaluations++;

        updateIdealPoint(child);
        updateNeighborhood(child, subProblemId, neighborType);
      }
      
      generation++;
      if (generation % 30 == 0) {
        utilityFunction();
      }

    } while (!stoppingCondition());

    return selectSpreadSolutions(problem, population, resultPopulationSize);
  }
  
  @Override
  public void initializePopulation() throws JMetalException, ClassNotFoundException {
    for (int i = 0; i < populationSize; i++) {
      Solution newSolution = new Solution(problem);

      problem.evaluate(newSolution);
      problem.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
      savedValues[i] = new Solution(newSolution);
    }
  }
  
  public void utilityFunction() throws JMetalException {
    double f1, f2, uti, delta;
    for (int n = 0; n < populationSize; n++) {
      f1 = fitnessFunction(population.get(n), lambda[n]);
      f2 = fitnessFunction(savedValues[n], lambda[n]);
      delta = f2 - f1;
      if (delta > 0.001) {
        utility[n] = 1.0;
      } else {
        uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
        utility[n] = uti < 1.0 ? uti : 1.0;
      }
      savedValues[n] = new Solution(population.get(n));
    }
  }

  public List<Integer> tourSelection(int depth) {
    List<Integer> selected = new ArrayList<Integer>();
    List<Integer> candidate = new ArrayList<Integer>();

    for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
      // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL (NOT SORTED!!!!)
      selected.add(k);
    }

    for (int n = problem.getNumberOfObjectives(); n < populationSize; n++) {
      // set of unselected weights
      candidate.add(n);
    }

    while (selected.size() < (int) (populationSize / 5.0)) {
      int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
      int i2;
      int best_sub = candidate.get(best_idd);
      int s2;
      for (int i = 1; i < depth; i++) {
        i2 = (int) (PseudoRandom.randDouble() * candidate.size());
        s2 = candidate.get(i2);
        if (utility[s2] > utility[best_sub]) {
          best_idd = i2;
          best_sub = s2;
        }
      }
      selected.add(best_sub);
      candidate.remove(best_idd);
    }
    return selected;
  }
}

