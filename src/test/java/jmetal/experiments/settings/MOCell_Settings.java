//  MOCell_Settings.java 
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
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm MOCell
 */
public class MOCell_Settings extends Settings{

  public int populationSize_                ;
  public int maxEvaluations_                ;
  public int archiveSize_                   ;
  public int feedback_                      ;
  public double mutationProbability_        ;
  public double crossoverProbability_       ;
  public double crossoverDistributionIndex_ ;
  public double mutationDistributionIndex_  ;

  /**
   * Constructor
   */
  public MOCell_Settings(String problemName) {
    super(problemName) ;

    Object [] problemParams = {"Real"};
    try {
      problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }      

    // Default experiments.settings
    populationSize_             = 100   ;
    maxEvaluations_             = 25000 ;
    archiveSize_                = 100   ;
    feedback_                   = 20    ;
    mutationProbability_        = 1.0/problem_.getNumberOfVariables();
    crossoverProbability_       = 0.9   ;
    crossoverDistributionIndex_ = 20.0  ;
    mutationDistributionIndex_  = 20.0  ;
  } // MOCell_Settings

  /**
   * Configure the MOCell algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;

    Crossover crossover ;
    Mutation  mutation  ;
    Operator  selection ;

    HashMap  parameters ; // Operator parameters

    // Selecting the algorithm: there are six MOCell variants
    //algorithm = new sMOCell1(problem_) ;
    //algorithm = new sMOCell2(problem_) ;
    //algorithm = new aMOCell1(problem_) ;
    //algorithm = new aMOCell2(problem_) ;
    //algorithm = new aMOCell3(problem_) ;
    algorithm = new MOCell(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("archiveSize",archiveSize_ );
    algorithm.setInputParameter("feedBack", feedback_);


    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                         

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;   

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm ;
  } // configure

  /**
   * Configure MOCell with user-defined parameter experiments.settings
   * @return A MOCell algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Selection selection ;
    Crossover  crossover ;
    Mutation   mutation  ;

    HashMap  parameters ; // Operator parameters

    // Selecting the algorithm: there are six MOCell variants
    //algorithm = new sMOCell1(problem_) ;
    //algorithm = new sMOCell2(problem_) ;
    //algorithm = new aMOCell1(problem_) ;
    //algorithm = new aMOCell2(problem_) ;
    //algorithm = new aMOCell3(problem_) ;
    algorithm = new MOCell(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    archiveSize_  = Integer.parseInt(configuration.getProperty("archiveSize",String.valueOf(archiveSize_)));
    feedback_  = Integer.parseInt(configuration.getProperty("feedback",String.valueOf(feedback_)));
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);
    algorithm.setInputParameter("archiveSize",archiveSize_);
    algorithm.setInputParameter("feedback",feedback_);

    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex",String.valueOf(crossoverDistributionIndex_)));
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    // Selection Operator
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    return algorithm ;
  }
} // MOCell_Settings
