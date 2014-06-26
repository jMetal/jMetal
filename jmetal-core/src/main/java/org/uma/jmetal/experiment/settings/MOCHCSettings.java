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
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.mochc.MOCHC;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 17/06/13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class MOCHCSettings extends Settings {
  private int populationSize_;
  private int maxEvaluations_;

  private double initialConvergenceCount_;
  private double preservedPopulation_;
  private int convergenceValue_;
  private double crossoverProbability_;
  private double mutationProbability_;

  public MOCHCSettings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Binary"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiment.settings
    populationSize_ = 100;
    maxEvaluations_ = 25000;
    initialConvergenceCount_ = 0.25;
    preservedPopulation_ = 0.05;
    convergenceValue_ = 3;

    crossoverProbability_ = 1.0;
    mutationProbability_ = 0.35;
  }

  /**
   * Configure MOCHC with user-defined parameter experiment.settings
   *
   * @return A MOCHC algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator parentsSelection;
    Operator newGenerationSelection;

    // Creating the problem
    algorithm = new MOCHC();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("initialConvergenceCount", initialConvergenceCount_);
    algorithm.setInputParameter("preservedPopulation", preservedPopulation_);
    algorithm.setInputParameter("convergenceValue", convergenceValue_);
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    // Crossover operator
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);

    parameters = null;
    parentsSelection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("problem", problem_);
    newGenerationSelection =
      SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters);

    // Mutation operator
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("cataclysmicMutation", mutation);
    algorithm.addOperator("parentSelection", parentsSelection);
    algorithm.addOperator("newGenerationSelection", newGenerationSelection);

    return algorithm;
  }

  /**
   * Configure MOCHC with user-defined parameter experiment.settings
   *
   * @return A MOCHC algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    initialConvergenceCount_ = Double.parseDouble(configuration
      .getProperty("initialConvergenceCount", String.valueOf(initialConvergenceCount_)));
    preservedPopulation_ = Double.parseDouble(
      configuration.getProperty("preservedPopulation", String.valueOf(preservedPopulation_)));
    convergenceValue_ = Integer
      .parseInt(configuration.getProperty("convergenceValue", String.valueOf(convergenceValue_)));

    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));

    return configure();
  }
}
