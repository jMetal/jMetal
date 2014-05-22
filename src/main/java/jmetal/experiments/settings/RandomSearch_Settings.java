//  RandomSearch_Settings.java 
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
import jmetal.experiments.Settings;
import jmetal.metaheuristics.randomSearch.RandomSearch;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm RandomSearch
 */
public class RandomSearch_Settings extends Settings {
  // Default experiments.settings
  private int maxEvaluations_ = 25000;

  /**
   * Constructor
   * @param problem Problem to solve
   * @throws JMException 
   */
  public RandomSearch_Settings(String problem) throws JMException {
    super(problem);

    Object [] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
  } 

  /**
   * Configure the random search algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;

    // Creating the problem
    algorithm = new RandomSearch(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    return algorithm;
  } // Constructor

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));

    return configure() ;
  }
} 
