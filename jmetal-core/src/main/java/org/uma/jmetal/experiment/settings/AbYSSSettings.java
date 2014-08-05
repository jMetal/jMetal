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
import org.uma.jmetal.metaheuristic.multiobjective.abyss.AbYSS;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.SBXCrossover;
import org.uma.jmetal.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;

import java.util.Properties;

/** Settings class of algorithm NSGA-II (real encoding) */
public class AbYSSSettings extends Settings {
  private int maxEvaluations;
  private int populationSize ;
  private int refSet1Size ;
  private int refSet2Size ;
  private int archiveSize ;
  private Archive archive ;
  private int improvementRounds ;
  private int numberOfSubranges ;

  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;

  /** Constructor */
  public AbYSSSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    /* Default settings */
    refSet1Size = 10;
    refSet2Size = 10 ;
    populationSize = 20 ;
    archiveSize = 100 ;
    maxEvaluations = 25000;
    numberOfSubranges = 4 ;

    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20.0;
    crossoverDistributionIndex = 20.0;
    improvementRounds = 1 ;
  }

  /** Configure AbYSS with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;
    MutationLocalSearch localSearch ;

    crossover = new SBXCrossover.Builder()
            .distributionIndex(crossoverDistributionIndex)
            .probability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .distributionIndex(mutationDistributionIndex)
            .probability(mutationProbability)
            .build();

    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives()) ;

    localSearch = new MutationLocalSearch.Builder(problem)
            .mutationOperator(mutation)
            .improvementRounds(improvementRounds)
            .archive(archive)
            .build() ;

    algorithm = new AbYSS.Builder(problem)
            .maxEvaluations(maxEvaluations)
            .populationSize(populationSize)
            .refSet1Size(refSet1Size)
            .refSet2Size(refSet2Size)
            .numberOfSubranges(numberOfSubranges)
            .archive(archive)
            .crossover(crossover)
            .localSearch(localSearch)
            .build() ;

    return algorithm;
  }

  /** Configure AbYSS from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {

    // Algorithm parameters
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
            .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize =
            Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    refSet1Size =
            Integer.parseInt(configuration.getProperty("refSet1Size", String.valueOf(refSet1Size)));
    refSet2Size =
            Integer.parseInt(configuration.getProperty("refSet2Size", String.valueOf(refSet2Size)));
    improvementRounds = Integer
            .parseInt(configuration.getProperty("improvementRounds", String.valueOf(improvementRounds)));
    numberOfSubranges = Integer
            .parseInt(configuration.getProperty("numberOfSubranges", String.valueOf(numberOfSubranges)));

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
