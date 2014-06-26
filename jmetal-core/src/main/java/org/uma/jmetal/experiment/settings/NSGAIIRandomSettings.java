//  NSGAIIRandom_Settings.java
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
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAIIRandom;
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
 * Settings class of algorithm NSGAIIRandom
 * Reference: Antonio J. Nebro, Juan José Durillo, Mirialys Machin Navas, Carlos A. Coello Coello, Bernabé Dorronsoro:
 * A Study of the Combination of Variation Operators in the NSGA-II Algorithm.
 * CAEPIA 2013: 269-278
 * DOI: http://dx.doi.org/10.1007/978-3-642-40643-0_28
 */
public class NSGAIIRandomSettings extends Settings {
  private int populationSize_                 ;
  private int maxEvaluations_                 ;
  private double mutationProbability_         ;
  private double crossoverProbability_        ;
  private double mutationDistributionIndex_   ;
  private double crossoverDistributionIndex_  ;
  private double cr_                          ;
  private double f_                           ;

  /**
   * Constructor
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public NSGAIIRandomSettings(String problem) throws JMetalException {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default settings
    populationSize_              = 100   ;
    maxEvaluations_              = 150000 ;
    mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
    crossoverProbability_        = 0.9 ;
    mutationDistributionIndex_   = 20 ;
    crossoverDistributionIndex_  = 20 ;
    cr_                          = 1.0 ;
    f_                           = 0.5 ;
  } 

  /**
   * Configure NSGAII with user-defined parameter settings
   *
   * @return A NSGAII algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Selection selection;

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator() ;

    algorithm = new NSGAIIRandom(evaluator);
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    Offspring[] getOffspring = new Offspring[3];

    getOffspring[0] = new DifferentialEvolutionOffspring(cr_, f_);

    getOffspring[1] = new SBXCrossoverOffspring(crossoverProbability_, crossoverDistributionIndex_);

    getOffspring[2] =
      new PolynomialMutationOffspring(mutationProbability_, mutationDistributionIndex_);

    algorithm.setInputParameter("offspringsCreators", getOffspring);

    // Selection Operator 

    HashMap<String, Object> parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("selection",selection);
    
    return algorithm ;
  } 

  /**
   * Configure NSGAIIRandom with user-defined parameter experiment.settings
   *
   * @return A NSGAIIRandom algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));

    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex",String.valueOf(crossoverDistributionIndex_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));
    cr_ = Double.parseDouble(configuration.getProperty("CR",String.valueOf(cr_)));
    f_ = Double.parseDouble(configuration.getProperty("F",String.valueOf(f_)));

    return configure() ;
  }
} 
