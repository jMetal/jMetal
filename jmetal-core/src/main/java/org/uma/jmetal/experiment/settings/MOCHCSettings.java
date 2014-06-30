//  MOCell_Settings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
//

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.mochc.MOCHC;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.HUXCrossover;
import org.uma.jmetal.operator.mutation.BitFlipMutation;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.selection.RandomSelection;
import org.uma.jmetal.operator.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 17/06/13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class MOCHCSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;

  private double initialConvergenceCount;
  private double preservedPopulation;
  private int convergenceValue;
  private double crossoverProbability;
  private double mutationProbability;

  public MOCHCSettings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Binary"};
    problem_ = (new ProblemFactory()).getProblem(this.problemName, problemParams);

    // Default experiment.settings
    populationSize = 100;
    maxEvaluations = 25000;
    initialConvergenceCount = 0.25;
    preservedPopulation = 0.05;
    convergenceValue = 3;

    crossoverProbability = 1.0;
    mutationProbability = 0.35;
  }

  /**
   * Configure MOCHC with user-defined parameter experiment.settings
   *
   * @return A MOCHC algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Crossover crossoverOperator;
    Mutation mutationOperator;
    Selection parentsSelection;
    Selection newGenerationSelection;
    Algorithm algorithm ;

    crossoverOperator = new HUXCrossover.Builder()
      .probability(1.0)
      .build() ;

    parentsSelection = new RandomSelection.Builder()
      .build() ;

    newGenerationSelection = new RankingAndCrowdingSelection.Builder(problem_)
      .populationSize(100)
      .build() ;

    mutationOperator = new BitFlipMutation.Builder()
      .probability(0.35)
      .build() ;

    algorithm = new MOCHC.Builder(problem_)
      .initialConvergenceCount(0.25)
      .preservedPopulation(0.05)
      .populationSize(100)
      .maxEvaluations(25000)
      .convergenceValue(3)
      .crossover(crossoverOperator)
      .newGenerationSelection(newGenerationSelection)
      .cataclysmicMutation(mutationOperator)
      .parentSelection(parentsSelection)
      .build() ;

    return algorithm ;
  }

  /**
   * Configure MOCHC with user-defined parameter experiment.settings
   *
   * @return A MOCHC algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    initialConvergenceCount = Double.parseDouble(configuration
      .getProperty("initialConvergenceCount", String.valueOf(initialConvergenceCount)));
    preservedPopulation = Double.parseDouble(
      configuration.getProperty("preservedPopulation", String.valueOf(preservedPopulation)));
    convergenceValue = Integer
      .parseInt(configuration.getProperty("convergenceValue", String.valueOf(convergenceValue)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));

    return configure();
  }
}
