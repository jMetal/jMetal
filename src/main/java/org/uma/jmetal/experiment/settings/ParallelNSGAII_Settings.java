//  ParallelNSGAIISettings.java
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
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament2;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.MultithreadedSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/**
 * Settings class of algorithm ParallelNSGAIISettings (real encoding)
 */
public class ParallelNSGAII_Settings extends Settings {
  public int populationSize_;
  public int maxEvaluations_;
  public double mutationProbability_;
  public double crossoverProbability_;
  public double mutationDistributionIndex_;
  public double crossoverDistributionIndex_;
  public int numberOfThreads_;

  /**
   * Constructor
   */
  public ParallelNSGAII_Settings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiment.settings
    populationSize_ = 100;
    maxEvaluations_ = 25000;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
    mutationDistributionIndex_ = 20.0;
    crossoverDistributionIndex_ = 20.0;
    // 0 - number of available cores
    numberOfThreads_ = 2;
  }


  /**
   * Configure ParallelNSGAII with user-defined parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(numberOfThreads_, problem_) ;

    crossover = new SBXCrossover.Builder()
      .distributionIndex(crossoverDistributionIndex_)
      .probability(crossoverProbability_)
      .build() ;

    mutation = new PolynomialMutation.Builder()
      .distributionIndex(mutationDistributionIndex_)
      .probability(mutationProbability_)
      .build();

    selection = new BinaryTournament2.Builder()
      .build();

    algorithm = new NSGAII.Builder(problem_, evaluator)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .maxEvaluations(25000)
      .populationSize(100)
      .build("NSGAII") ;

    return algorithm;
  }

  /**
   * Configure NSGAIISettings with user-defined parameter experiment.settings
   *
   * @return A NSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    numberOfThreads_ = Integer
      .parseInt(configuration.getProperty("numberOfThreads", String.valueOf(numberOfThreads_)));

    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(numberOfThreads_, problem_) ;

    // Algorithm parameters
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxEvaluations_ = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));

    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex_)));

    mutationProbability_ = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));

    return configure();
  }
}
