//  NSGAIISettings.java
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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/**
 * Settings class of algorithm NSGA-II (real encoding)
 */
public class NSGAIISettings extends Settings {
  private int populationSize_;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;
  private SolutionSetEvaluator evaluator;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public NSGAIISettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default experiment.settings
    populationSize_ = 100;
    maxEvaluations = 25000;
    mutationProbability = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  /**
   * Configure NSGAII with default parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    crossover = new SBXCrossover.Builder()
      .distributionIndex(crossoverDistributionIndex)
      .probability(crossoverProbability)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(mutationDistributionIndex)
      .probability(mutationProbability)
      .build();

    selection = new BinaryTournament2.Builder()
      .build();

    algorithm = new NSGAII.Builder(problem_, evaluator)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .maxEvaluations(maxEvaluations)
      .populationSize(populationSize_)
      .build("NSGAII") ;

    return algorithm;
  }

  /**
   * Configure NSGAII with user-defined parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    crossoverDistributionIndex = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex)));

    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));

    return configure();
  }
} 
