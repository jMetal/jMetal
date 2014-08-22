//  SPEA2_Settings.java 
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
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.spea2.SPEA2;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.Properties;

/**
 * Settings class of algorithm SPEA2
 */
public class SPEA2Settings extends Settings {
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double crossoverDistributionIndex;
  private double mutationDistributionIndex;

  /** Constructor */
  public SPEA2Settings(String problem) throws JMetalException {
    super(problem) ;

    Object [] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    populationSize = 100   ;
    archiveSize = 100   ;
    maxEvaluations = 25000 ;
    mutationProbability = 1.0/ this.problem.getNumberOfVariables() ;
    crossoverProbability = 0.9   ;
    crossoverDistributionIndex = 20.0  ;
    mutationDistributionIndex = 20.0  ;
  }

  /** Configure() method */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator selection;

    crossover = new SBXCrossover.Builder()
            .distributionIndex(crossoverDistributionIndex)
            .probability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .distributionIndex(mutationDistributionIndex)
            .probability(mutationProbability)
            .build();

    selection = new BinaryTournament.Builder()
            .build();

    algorithm = new SPEA2.Builder(problem)
            .populationSize(populationSize)
            .archiveSize(archiveSize)
            .maxEvaluations(maxEvaluations)
            .crossover(crossover)
            .mutation(mutation)
            .selection(selection)
            .build() ;

    return algorithm ;
  }

  /**
   * Configure SPEA2 with user-defined parameter settings
   *
   * @return A SPEA2 algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(
            populationSize)));
    maxEvaluations = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(
            maxEvaluations)));
    archiveSize = Integer.parseInt(configuration.getProperty("archiveSize",String.valueOf(
            archiveSize)));
    crossoverProbability = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(
            crossoverProbability)));
    crossoverDistributionIndex = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex",String.valueOf(
            crossoverDistributionIndex)));
    mutationProbability = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(
            mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(
            mutationDistributionIndex)));

    return configure() ;
  }
} 
