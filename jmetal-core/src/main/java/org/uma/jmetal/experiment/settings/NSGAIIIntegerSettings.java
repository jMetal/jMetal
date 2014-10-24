//  NSGAIIIntegerSettings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.Properties;
import java.util.logging.Level;

/** Settings class of algorithm NSGA-II (integer encoding) */
public class NSGAIIIntegerSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;
  private SolutionSetEvaluator evaluator;

  /** Constructor */
  public NSGAIIIntegerSettings(String problemName) throws JMetalException, ClassNotFoundException {
    Object[] problemParams = {"Integer"};

    try {
      this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Unable to get problem", e);
    }
    
    //this.problem = new NMMin("Integer") ;

    populationSize = 100;
    maxEvaluations = 250000;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  /** Configure NSGAII with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

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

    algorithm = new NSGAII.Builder(problem, evaluator)
            .setCrossover(crossover)
            .setMutation(mutation)
            .setSelection(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .build("NSGAII") ;

    return algorithm;
  }

  /** Configure NSGAII from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
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
