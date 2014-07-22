//  NSGAIIAdaptive_Settings.java 
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIIAdaptive;
import org.uma.jmetal.operator.selection.Selection;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.offspring.DifferentialEvolutionOffspring;
import org.uma.jmetal.util.offspring.Offspring;
import org.uma.jmetal.util.offspring.PolynomialMutationOffspring;
import org.uma.jmetal.util.offspring.SBXCrossoverOffspring;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm NSGAIIAdaptive
 * Reference: Antonio J. Nebro, Juan José Durillo, Mirialys Machin Navas, Carlos A. Coello Coello, Bernabé Dorronsoro:
 * A Study of the Combination of Variation Operators in the NSGA-II Algorithm.
 * CAEPIA 2013: 269-278
 * DOI: http://dx.doi.org/10.1007/978-3-642-40643-0_28
 */
public class NSGAIIAdaptiveSettings extends Settings {
  private int populationSize;
  private int maxEvaluations;
  private double mutationProbability;
  private double crossoverProbability;
  private double mutationDistributionIndex;
  private double crossoverDistributionIndex;
  private double cr;
  private double f;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public NSGAIIAdaptiveSettings(String problem) throws JMetalException {
    super(problem);

    Object[] problemParams = {"Real"};
    this.problem = (new ProblemFactory()).getProblem(problemName, problemParams);

    // Default settings
    populationSize = 100;
    maxEvaluations = 150000;
    mutationProbability = 1.0 / this.problem.getNumberOfVariables();
    crossoverProbability = 0.9;
    mutationDistributionIndex = 20;
    crossoverDistributionIndex = 20;
    cr = 1.0;
    f = 0.5;
  }

  /**
   * Configure NSGAIIAdaptive with user-defined parameter settings
   *
   * @return A NSGAIIAdaptive algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;

    HashMap<String, Object> parameters = new HashMap<String, Object>();

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    algorithm = new NSGAIIAdaptive(evaluator);
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations);

    Offspring[] getOffspring = new Offspring[3];
    getOffspring[0] = new DifferentialEvolutionOffspring(cr, f);

    getOffspring[1] = new SBXCrossoverOffspring(crossoverProbability, crossoverDistributionIndex);

    getOffspring[2] =
      new PolynomialMutationOffspring(mutationProbability, mutationDistributionIndex);

    algorithm.setInputParameter("offspringsCreators", getOffspring);

    // Selection Operator 
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("selection", selection);

    return algorithm;
  }

  /**
   * Configure NSGAIIAdaptive with user-defined parameter experiment.settings
   *
   * @return A NSGAIIAdaptive algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize = Integer
      .parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize)));
    maxEvaluations = Integer
      .parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations)));

    crossoverProbability = Double.parseDouble(
      configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability)));
    crossoverDistributionIndex = Double.parseDouble(configuration
      .getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex)));
    mutationProbability = Double.parseDouble(
      configuration.getProperty("mutationProbability", String.valueOf(mutationProbability)));
    mutationDistributionIndex = Double.parseDouble(configuration
      .getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex)));
    cr = Double.parseDouble(configuration.getProperty("CR", String.valueOf(cr)));
    f = Double.parseDouble(configuration.getProperty("F", String.valueOf(f)));

    return configure();
  }
}
