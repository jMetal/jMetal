//  PESA2Settings.java
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
import org.uma.jmetal.metaheuristic.multiobjective.pesa2.PESA2;
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

/** Settings class of algorithm PESA2 */
public class PESA2Settings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private int archiveSize ;
  private int biSections ;
  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;

  /** Constructor */
  public PESA2Settings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    /* Default settings */
    populationSize = 10;
    maxEvaluations = 25000;
    archiveSize = 100 ;
    biSections = 5 ;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
  }

  /** Configure() method */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
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

    algorithm = new PESA2.Builder(problem)
            .maxEvaluations(maxEvaluations)
            .populationSize(populationSize)
            .archiveSize(archiveSize)
            .biSections(biSections)
            .crossover(crossover)
            .mutation(mutation)
            .build() ;

    return algorithm;
  }

  /** Configure() method */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
            .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize = Integer
            .parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    biSections = Integer
            .parseInt(configuration.getProperty("biSections", String.valueOf(biSections)));

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
