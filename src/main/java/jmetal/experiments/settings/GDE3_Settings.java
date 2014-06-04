//  GDE3_Settings.java 
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.gde3.GDE3;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm GDE3
 */
public class GDE3_Settings extends Settings {
  private double cr_;
  private double f_;
  private int populationSize_;
  private int maxIterations_;

  /**
   * Constructor
   *
   * @throws JMException
   */
  public GDE3_Settings(String problemName) throws JMException {
    super(problemName);

    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    cr_ = 0.5;
    f_ = 0.5;
    populationSize_ = 100;
    maxIterations_ = 250;
  }

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   *
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator selection;
    Operator crossover;

    // Creating the problem
    Object[] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    algorithm = new GDE3();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);

    // Crossover operator 
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("CR", cr_);
    parameters.put("F", f_);
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    parameters = null;
    selection = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("selection", selection);

    return algorithm;
  } // configure

  /**
   * Configure GDE3 with user-defined parameter experiments.settings
   *
   * @return A GDE3 algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    populationSize_ = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
    maxIterations_ =
      Integer.parseInt(configuration.getProperty("maxIterations", String.valueOf(maxIterations_)));

    cr_ = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr_)));
    f_ = Double.parseDouble(configuration.getProperty("F", String.valueOf(f_)));

    return configure();
  }
}
