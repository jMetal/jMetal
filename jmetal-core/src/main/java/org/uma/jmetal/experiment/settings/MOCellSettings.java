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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.mocell.MOCellTemplate;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm MOCell
 */
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

  /**
   * Constructor
   */
  public MOCellSettings(String problemName) {
    super(problemName);

    Object[] problemParams = {"Real"};
    try {
      problem_ = (new ProblemFactory()).getProblem(this.problemName, problemParams);
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Unable to get problem", e);
    }

    // Default experiment.settings
    populationSize = 100;
    maxEvaluations = 25000;
    archiveSize = 100;
    numberOfFeedbackSolutionsFromArchive = 20;
    mutationProbability = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability = 0.9;
    crossoverDistributionIndex = 20.0;
    mutationDistributionIndex = 20.0;

    mocellVariant = "AsyncMOCell1" ;
  }

  /**
   * Configure the MOCell algorithm with default parameter settings
   *
   * @return an algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;

    Crossover crossover;
    Mutation mutation;
    Operator selection;

    // Selecting the algorithm: there are six MOCell variants
    //algorithm = new sMOCell1(problem_) ;
    //algorithm = new sMOCell2(problem_) ;
    //algorithm = new aMOCell1(problem_) ;
    //algorithm = new aMOCell2(problem_) ;
    //algorithm = new aMOCell3(problem_) ;
    //algorithm = new aMOCell4(problem_) ;

    crossover = new SBXCrossover.Builder()
      .probability(crossoverProbability)
      .distributionIndex(crossoverDistributionIndex)
      .build();

    mutation = new PolynomialMutation.Builder()
      .probability(mutationProbability)
      .distributionIndex(mutationDistributionIndex)
      .build();

    selection = new BinaryTournament.Builder()
      .build();

    algorithm = new MOCellTemplate.Builder(problem_)
      .populationSize(populationSize)
      .archiveSize(archiveSize)
      .maxEvaluations(maxEvaluations)
      .numberOfFeedbackSolutionsFromArchive(numberOfFeedbackSolutionsFromArchive)
      .crossover(crossover)
      .mutation(mutation)
      .selection(selection)
      .build(mocellVariant);

    return algorithm;
  }

  /**
   * Configure MOCell with user-defined parameter settings
   *
   * @return A MOCell algorithm object
   */
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

    mocellVariant = configuration.getProperty("MOCellVariant", mocellVariant) ;

    return configure();
  }
} 
