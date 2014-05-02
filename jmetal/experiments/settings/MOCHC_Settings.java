//  MOCell_Settings.java
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.mochc.MOCHC;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 17/06/13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class MOCHC_Settings extends Settings {
  int populationSize_  ;
  int maxEvaluations_  ;

  double initialConvergenceCount_ ;
  double preservedPopulation_     ;
  int    convergenceValue_        ;
  double crossoverProbability_    ;
  double mutationProbability_     ;

  public MOCHC_Settings(String problemName) {
    super(problemName);

    Object [] problemParams = {"Binary"};
    try {
      problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      e.printStackTrace();
    }
    // Default experiments.settings
    populationSize_ = 100 ;
    maxEvaluations_ = 25000 ;
    initialConvergenceCount_ = 0.25 ;
    preservedPopulation_ = 0.05 ;
    convergenceValue_ = 3 ;

    crossoverProbability_ = 1.0 ;
    mutationProbability_ = 0.35 ;

  }

  /**
   * Configure MOCHC with user-defined parameter experiments.settings
   * @return A MOCHC algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator crossover      ;
    Operator mutation       ;
    Operator parentsSelection       ;
    Operator newGenerationSelection ;

    HashMap parameters ; // Operator parameters

    // Creating the problem
    algorithm = new MOCHC(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("initialConvergenceCount",initialConvergenceCount_);
    algorithm.setInputParameter("preservedPopulation",preservedPopulation_);
    algorithm.setInputParameter("convergenceValue",convergenceValue_);
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

    // Crossover operator
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);

    parameters = null ;
    parentsSelection = SelectionFactory.getSelectionOperator("RandomSelection", parameters) ;

    parameters = new HashMap() ;
    parameters.put("problem", problem_) ;
    newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;

    // Mutation operator
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("cataclysmicMutation",mutation);
    algorithm.addOperator("parentSelection",parentsSelection);
    algorithm.addOperator("newGenerationSelection",newGenerationSelection);

    return algorithm ;
  } // configure

  /**
   * Configure MOCHC with user-defined parameter experiments.settings
   * @return A MOCHC algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Operator crossover      ;
    Operator mutation       ;
    Operator parentsSelection       ;
    Operator newGenerationSelection ;

    HashMap  parameters ; // Operator parameters

    algorithm = new MOCHC(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    initialConvergenceCount_ = Double.parseDouble(configuration.getProperty("initialConvergenceCount", String.valueOf(initialConvergenceCount_)));
    preservedPopulation_ = Double.parseDouble(configuration.getProperty("preservedPopulation", String.valueOf(preservedPopulation_)));
    convergenceValue_  = Integer.parseInt(configuration.getProperty("convergenceValue", String.valueOf(convergenceValue_)));

    algorithm.setInputParameter("initialConvergenceCount",initialConvergenceCount_);
    algorithm.setInputParameter("preservedPopulation",preservedPopulation_);
    algorithm.setInputParameter("convergenceValue",convergenceValue_);
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);

    parameters = null ;
    parentsSelection = SelectionFactory.getSelectionOperator("RandomSelection", parameters)  ;

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

    // Selection Operator
    parameters = new HashMap() ;
    parameters.put("problem", problem_) ;
    newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("cataclysmicMutation",mutation);
    algorithm.addOperator("parentSelection",parentsSelection);
    algorithm.addOperator("newGenerationSelection",newGenerationSelection);

    return algorithm ;
  }
}
