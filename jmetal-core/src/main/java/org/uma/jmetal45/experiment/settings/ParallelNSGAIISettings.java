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

package org.uma.jmetal45.experiment.settings;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.nsgaII.NSGAIITemplate;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.operator.selection.BinaryTournament2;
import org.uma.jmetal45.operator.selection.Selection;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.evaluator.MultithreadedSolutionSetEvaluator;
import org.uma.jmetal45.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;

/** Settings class of algorithm ParallelNSGAIISettings (real encoding) */
public class ParallelNSGAIISettings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;
  private int numberOfThreads;

  /** Constructor */
  public ParallelNSGAIISettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    populationSize = 100;
    maxEvaluations = 25000;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    // 0 - number of available cores
    numberOfThreads = 2;
  }

  /** Configure ParallelNSGAII with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    SolutionSetEvaluator evaluator = new MultithreadedSolutionSetEvaluator(numberOfThreads,
            problem) ;

    crossover = new SBXCrossover.Builder()
            .setDistributionIndex(crossoverDistributionIndex)
            .setProbability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(mutationDistributionIndex)
            .setProbability(mutationProbability)
            .build();

    selection = new BinaryTournament2.Builder()
            .build();

    algorithm = new NSGAIITemplate.Builder(problem, evaluator)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build("NSGAII") ;

    return algorithm;
  }

  /** Configure ParallelNSGAII from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    numberOfThreads = Integer
            .parseInt(configuration.getProperty("numberOfThreads", String.valueOf(numberOfThreads)));

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
