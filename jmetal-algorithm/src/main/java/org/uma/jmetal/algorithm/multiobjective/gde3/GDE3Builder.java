//  GDE3.java
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

package org.uma.jmetal.algorithm.multiobjective.gde3;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * This class implements the GDE3 algorithm
 */
public class GDE3Builder implements AlgorithmBuilder<GDE3> {
  private DoubleProblem problem;
  protected int populationSize;
  protected int maxEvaluations;

  protected DifferentialEvolutionCrossover crossoverOperator;
  protected DifferentialEvolutionSelection selectionOperator;

  protected SolutionListEvaluator<DoubleSolution> evaluator;

  /** Constructor */
  public GDE3Builder(DoubleProblem problem) {
    this.problem = problem;
    maxEvaluations = 25000 ;
    populationSize = 100 ;
    selectionOperator = new DifferentialEvolutionSelection();
    crossoverOperator = new DifferentialEvolutionCrossover() ;
    evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
  }

  /* Setters */
  public GDE3Builder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public GDE3Builder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public GDE3Builder setCrossover(DifferentialEvolutionCrossover crossover) {
    crossoverOperator = crossover;

    return this;
  }

  public GDE3Builder setSelection(DifferentialEvolutionSelection selection) {
    selectionOperator = selection;

    return this;
  }

  public GDE3Builder setSolutionSetEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public GDE3 build() {
    return new GDE3(problem, populationSize, maxEvaluations, selectionOperator, crossoverOperator, evaluator) ;
  }

  /* Getters */
  public CrossoverOperator<DoubleSolution> getCrossoverOperator() {
    return crossoverOperator;
  }

  public SelectionOperator<List<DoubleSolution>, List<DoubleSolution>> getSelectionOperator() {
    return selectionOperator;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

}

