//  NSGAIIPermutation_Settings.java
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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm NSGA-II (permutation encoding)
 */
public class NSGAIIPermutationSettings extends Settings {
  public int populationSize_;
  public int maxEvaluations_;

  public double mutationProbability_;
  public double crossoverProbability_;

  /**
   * Constructor
   * @throws org.uma.jmetal.util.JMetalException
   */

  public NSGAIIPermutationSettings(String problem) {
    super(problem);

    Object[] problemParams = {"Permutation"};
    try {
      problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Unable to get problem", e);
    }

    // Default experiment.settings
    populationSize_ = 100;
    maxEvaluations_ = 25000;

    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
  }

  /**
   * Configure NSGAII with user-defined parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;
    Operator mutation;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    // Creating the algorithm.
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);


    // Mutation and Crossover Permutation codification
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);

    // Selection Operator 
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm;
  }

  /**
   * Configure NSGAII with user-defined parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    // Creating the algorithm.
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);

    // Algorithm parameters
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);

    // Selection Operator
    parameters = new HashMap<String, Object>();
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm ;
  }
}
