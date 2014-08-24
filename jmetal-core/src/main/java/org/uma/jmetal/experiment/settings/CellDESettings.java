//  CellDESettings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro
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
import org.uma.jmetal.metaheuristic.multiobjective.cellde.CellDE;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.selection.BinaryTournament;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.util.Properties;
import java.util.logging.Level;

/** Settings class of algorithm CellDE */
public class CellDESettings extends Settings {
  private double cr;
  private double f;

  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;
  private int numberOfFeedbackSolutionsFromArchive;

  /** Constructor */
  public CellDESettings(String problemName) {
    super(problemName);

    Object[] problemParams = {"Real"};
    try {
      problem = (new ProblemFactory()).getProblem(this.problemName, problemParams);
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Unable to get problem", e);
      throw new JMetalException(e);
    }

    cr = 0.5;
    f = 0.5;

    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;
    numberOfFeedbackSolutionsFromArchive = 20;
  }

  /** Configure CellDE with default parameter settings */
  @Override
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;

    crossover = new DifferentialEvolutionCrossover.Builder()
            .setCr(cr)
            .setF(f)
            .build() ;

    selection = new BinaryTournament.Builder()
            .build() ;

    algorithm = new CellDE.Builder(problem)
            .populationSize(populationSize)
            .archiveSize(archiveSize)
            .maxEvaluations(maxEvaluations)
            .numberOfFeedbackSolutionsFromArchive(numberOfFeedbackSolutionsFromArchive)
            .crossover(crossover)
            .selection(selection)
            .build() ;

    return algorithm;
  }

  /** Configure CellDE method from a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
            .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
            .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize =
            Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    numberOfFeedbackSolutionsFromArchive = Integer
            .parseInt(configuration.getProperty("archiveFeedback", String.valueOf(numberOfFeedbackSolutionsFromArchive)));

    cr = Double.parseDouble(configuration.getProperty("cr", String.valueOf(cr)));
    f = Double.parseDouble(configuration.getProperty("f", String.valueOf(f)));

    return configure();
  }
} 
