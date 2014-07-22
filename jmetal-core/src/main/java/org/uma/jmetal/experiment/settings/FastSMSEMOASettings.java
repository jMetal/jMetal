//  fastSMSEMA2_Settings.java
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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.smsemoa.FastSMSEMOA;
import org.uma.jmetal.operator.crossover.Crossover;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm FastSMSEMOA. This algorithm is just SMS-EMOA but using the FastHypervolume class
 */
public class FastSMSEMOASettings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double crossoverDistributionIndex;
  private double mutationDistributionIndex;
  private double offset;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public FastSMSEMOASettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    populationSize = 100;
    maxEvaluations = 25000;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    crossoverDistributionIndex = 20.0;
    mutationDistributionIndex = 20.0;
    offset = 100.0;
  }


  /**
   * Configure FastSMSEMOA with user-defined parameter experiment.settings
   *
   * @return A FastSMSEMOA algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;
    Mutation mutation;

    // Creating the algorithm. 
    algorithm = new FastSMSEMOA();
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);
    algorithm.setInputParameter("offset", offset);

    // Mutation and Crossover for Real codification 
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", crossoverProbability);
    parameters.put("distributionIndex", crossoverDistributionIndex);
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability);
    parameters.put("distributionIndex", mutationDistributionIndex);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    // Selection Operator
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm;
  }

  /**
   * Configure FastSMSEMOA with user-defined parameter experiment.settings
   *
   * @return A FastSMSEMOA algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));
    offset = Double.parseDouble(configuration.getProperty("offset", String.valueOf(offset)));

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
