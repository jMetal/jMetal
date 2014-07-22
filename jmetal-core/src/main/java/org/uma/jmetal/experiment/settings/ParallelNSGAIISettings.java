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
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
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
public class ParallelNSGAIISettings extends Settings {
  public int populationSize;
  public int maxEvaluations;
  public double mutationProbability;
  public double crossoverProbability;
  public double mutationDistributionIndex;
  public double crossoverDistributionIndex;
  public int numberOfThreads;

  /**
   * Constructor
   */
  public ParallelNSGAIISettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default experiment.settings
    populationSize = 100;
    maxEvaluations = 25000;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    // 0 - number of available cores
    numberOfThreads = 2;
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

    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(numberOfThreads,
      problem) ;

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

    algorithm = new NSGAII.Builder(problem, evaluator)
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
    numberOfThreads = Integer
      .parseInt(configuration.getProperty("numberOfThreads", String.valueOf(numberOfThreads)));

    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(numberOfThreads,
      problem) ;

    // Algorithm parameters
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));

    // Mutation and Crossover for Real codification
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
