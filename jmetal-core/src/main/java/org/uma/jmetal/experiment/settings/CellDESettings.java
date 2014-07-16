//  CellDE_Settings.java 
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
import org.uma.jmetal.metaheuristic.multiobjective.cellde.CellDE;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/** Settings class of algorithm CellDE */
public class CellDESettings extends Settings {
  private double cr;
  private double f;

  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;
  private int archiveFeedback;

  /** Constructor */
  public CellDESettings(String problemName) {
    super(problemName);

    Object[] problemParams = {"Real"};
    try {
      problem_ = (new ProblemFactory()).getProblem(this.problemName, problemParams);
    } catch (JMetalException e) {
      Configuration.logger.log(Level.SEVERE, "Unable to get problem", e);
      throw new JMetalException(e);
    }

    // Default experiment.settings
    cr = 0.5;
    f = 0.5;

    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;
    archiveFeedback = 20;
  }

  /** configure() method */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;

    // Creating the problem
    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);
    algorithm = new CellDE();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("archiveSize", archiveSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);
    algorithm.setInputParameter("archiveFeedBack", archiveFeedback);

    // Crossover operator 
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("CR", cr);
    parameters.put("F", f);
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    // Add the operator to the algorithm
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("selection", selection);

    return algorithm;
  }

  /** configure() method using a properties file */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    archiveSize =
      Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize)));
    archiveFeedback = Integer
      .parseInt(configuration.getProperty("archiveFeedback", String.valueOf(archiveFeedback)));

    cr = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr)));
    f = Double.parseDouble(configuration.getProperty("F", String.valueOf(f)));

    return configure();
  }
} 
