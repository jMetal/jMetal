//  MOCHCSettings.java
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
//

package org.uma.jmetal45.experiment.settings;

import org.uma.jmetal45.core.Algorithm;
import org.uma.jmetal45.experiment.Settings;
import org.uma.jmetal45.metaheuristic.multiobjective.mochc.MOCHC;
import org.uma.jmetal45.operator.crossover.Crossover;
import org.uma.jmetal45.operator.crossover.HUXCrossover;
import org.uma.jmetal45.operator.mutation.BitFlipMutation;
import org.uma.jmetal45.operator.mutation.Mutation;
import org.uma.jmetal45.operator.selection.RandomSelection;
import org.uma.jmetal45.operator.selection.RankingAndCrowdingSelection;
import org.uma.jmetal45.operator.selection.Selection;
import org.uma.jmetal45.problem.ProblemFactory;
import org.uma.jmetal45.util.JMetalException;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 17/06/13
 * Time: 23:40
 */
public class MOCHCSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;

  private double initialConvergenceCount;
  private double preservedPopulation;
  private int convergenceValue;
  private double crossoverProbability;
  private double mutationProbability;

  public MOCHCSettings(String problemName) throws JMetalException {
    super(problemName);

    Object[] problemParams = {"Binary"};
    problem = (new ProblemFactory()).getProblem(this.problemName, problemParams);

    populationSize = 100;
    maxEvaluations = 25000;
    initialConvergenceCount = 0.25;
    preservedPopulation = 0.05;
    convergenceValue = 3;

    crossoverProbability = 1.0;
    mutationProbability = 0.35;
  }

  /** Configure MOCHC with default parameter settings */
  public Algorithm configure() throws JMetalException {
    Crossover crossoverOperator;
    Mutation mutationOperator;
    Selection parentsSelection;
    Selection newGenerationSelection;
    Algorithm algorithm ;

    crossoverOperator = new HUXCrossover.Builder()
            .probability(crossoverProbability)
            .build() ;

    parentsSelection = new RandomSelection.Builder()
            .build() ;

    newGenerationSelection = new RankingAndCrowdingSelection.Builder(populationSize)
            .build() ;

    mutationOperator = new BitFlipMutation.Builder()
            .setProbability(mutationProbability)
            .build() ;

    algorithm = new MOCHC.Builder(problem)
            .setInitialConvergenceCount(initialConvergenceCount)
            .setPreservedPopulation(preservedPopulation)
            .setPopulationSize(populationSize)
            .setMaxEvaluations(maxEvaluations)
            .setConvergenceValue(convergenceValue)
            .setCrossover(crossoverOperator)
            .setNewGenerationSelection(newGenerationSelection)
            .setCataclysmicMutation(mutationOperator)
            .setParentSelection(parentsSelection)
            .build() ;

    return algorithm ;
  }

  /** Configure MOCHC from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
            .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    initialConvergenceCount = Double.parseDouble(configuration
            .getProperty("initialConvergenceCount", String.valueOf(initialConvergenceCount)));
    preservedPopulation = Double.parseDouble(
            configuration.getProperty("preservedPopulation", String.valueOf(preservedPopulation)));
    convergenceValue = Integer
            .parseInt(configuration.getProperty("convergenceValue", String.valueOf(convergenceValue)));

    crossoverProbability = Double.parseDouble(
            configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    mutationProbability = Double.parseDouble(
            configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));

    return configure();
  }
}
