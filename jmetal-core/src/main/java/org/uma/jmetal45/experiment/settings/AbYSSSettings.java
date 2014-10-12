//  AbYSSSettings.java
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
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.abyss.AbYSS;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.SBXCrossover;
import org.uma.jmetal45.operator.localSearch.MutationLocalSearch;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.mutation.PolynomialMutation;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.archive.Archive;
import org.uma.jmetal45.util.archive.CrowdingArchive;

import java.util.Properties;

/** Settings class of algorithm AbYSS  */
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
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Crossover crossover;
    Mutation mutation;
    MutationLocalSearch localSearch ;

    crossover = new SBXCrossover.Builder()
            .setDistributionIndex(crossoverDistributionIndex)
            .setProbability(crossoverProbability)
            .build() ;

    mutation = new PolynomialMutation.Builder()
            .setDistributionIndex(mutationDistributionIndex)
            .setProbability(mutationProbability)
            .build();

    archive = new CrowdingArchive(archiveSize, problem.getNumberOfObjectives()) ;

    localSearch = new MutationLocalSearch.Builder(problem)
            .setMutationOperator(mutation)
            .setImprovementRounds(improvementRounds)
            .setArchive(archive)
            .build() ;

    algorithm = new AbYSS.Builder(problem)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setRefSet1Size(refSet1Size)
            .setRefSet2Size(refSet2Size)
            .setNumberOfSubranges(numberOfSubranges)
            .setArchive(archive)
            .setCrossover(crossover)
            .setLocalSearch(localSearch)
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
            .parseInt(configuration.getProperty("setImprovementRounds", String.valueOf(improvementRounds)));
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
