//  MOCellSettings.java
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

package org.uma.jmetal45.experiment.settings;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.core.Operator;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.mocell.MOCellTemplate;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.operator.selection.BinaryTournament;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.JMetalLogger;

import java.util.Properties;
import java.util.logging.Level;

/** Settings class of algorithm MOCell */
public class MOCellSettings extends Settings {

  private int populationSize;
  private int maxEvaluations;
  private int archiveSize;
  private int numberOfFeedbackSolutionsFromArchive;
  private double mutationProbability;
  private double crossoverProbability;
  private double crossoverDistributionIndex;
  private double mutationDistributionIndex;
  private String mocellVariant ;

  /** Constructor */
  public MOCellSettings(String problemName) {
    super(problemName);

    Object[] problemParams = {"Real"};
    try {
      problem = (new ProblemFactory()).getProblem(this.problemName, problemParams);
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Unable to get problem", e);
    }

    populationSize = 100;
    maxEvaluations = 25000;
    archiveSize = 100;
    numberOfFeedbackSolutionsFromArchive = 20;
    mutationProbability = 1.0 / problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    crossoverDistributionIndex = 20.0;
    mutationDistributionIndex = 20.0;

    mocellVariant = "SyncMOCell1" ;
  }

  /** Configure MOCell with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;

    Crossover crossover;
    Mutation mutation;
    Operator selection;

    crossover = new SBXCrossover.Builder()
      .setProbability(crossoverProbability)
      .setDistributionIndex(crossoverDistributionIndex)
      .build();

    mutation = new PolynomialMutation.Builder()
      .setProbability(mutationProbability)
      .setDistributionIndex(mutationDistributionIndex)
      .build();

    selection = new BinaryTournament.Builder()
      .build();

    algorithm = new MOCellTemplate.Builder(problem)
      .setPopulationSize(populationSize)
      .setArchiveSize(archiveSize)
      .setMaxEvaluations(maxEvaluations)
      .setNumberOfFeedbackSolutionsFromArchive(numberOfFeedbackSolutionsFromArchive)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setSelection(selection)
      .build(mocellVariant);

    return algorithm;
  }

  public Algorithm configure(String mocellVariant) {
    this.mocellVariant = mocellVariant ;
    //FIXME: need to test that the values are valid
    return configure() ;
  }

  /** Configure MOCell (variant: SyncMOCell1) from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    numberOfFeedbackSolutionsFromArchive = Integer.parseInt(configuration.getProperty("feedback", String.valueOf(
      numberOfFeedbackSolutionsFromArchive)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    crossoverDistributionIndex = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex)));

    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));

    mocellVariant = configuration.getProperty("mocellVariant", mocellVariant) ;

    return configure();
  }
} 
