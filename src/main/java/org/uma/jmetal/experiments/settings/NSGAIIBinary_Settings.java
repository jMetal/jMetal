//  NSGAIIBinary_Settings.java 
//
//  Authors:
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

package org.uma.jmetal.experiments.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiments.Settings;
import org.uma.jmetal.metaheuristics.nsgaII.NSGAII;
import org.uma.jmetal.operators.crossover.CrossoverFactory;
import org.uma.jmetal.operators.mutation.MutationFactory;
import org.uma.jmetal.operators.selection.SelectionFactory;
import org.uma.jmetal.problems.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm NSGA-II (binary encoding)
 */
public class NSGAIIBinary_Settings extends Settings {
  private int populationSize_;
  private int maxEvaluations_;

  private double mutationProbability_;
  private double crossoverProbability_;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public NSGAIIBinary_Settings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Binary"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    populationSize_ = 100;
    maxEvaluations_ = 25000;

    mutationProbability_ = 1.0 / problem_.getNumberOfBits();
    crossoverProbability_ = 0.9;
  }

  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   *
   * @return A NSGAII algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;
    Operator mutation;

    HashMap<String, Object> parameters = new HashMap<String, Object>();

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    // Creating the algorithm.
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem_);
    

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);


    // Mutation and Crossover Binary codification
    parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability_);
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

    // Selection Operator 
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm;
  }

  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   *
   * @return A NSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));

    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));

    return configure();
  }
}
