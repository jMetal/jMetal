//  pNSGAII_Settings.java 
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.pNSGAII;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;
import jmetal.util.parallel.IParallelEvaluator;
import jmetal.util.parallel.MultithreadedEvaluator;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm pNSGA-II (real encoding)
 */
public class pNSGAII_Settings extends Settings {
  public int populationSize_                 ; 
  public int maxEvaluations_                 ;
  public double mutationProbability_         ;
  public double crossoverProbability_        ;
  public double mutationDistributionIndex_   ;
  public double crossoverDistributionIndex_  ;
  public int    numberOfThreads_             ;
  
  /**
   * Constructor
   */
  public pNSGAII_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    e.printStackTrace();
    }  
    // Default experiments.settings
    populationSize_              = 100   ; 
    maxEvaluations_              = 25000 ;
    mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
    crossoverProbability_        = 0.9   ;
    mutationDistributionIndex_   = 20.0  ;
    crossoverDistributionIndex_  = 20.0  ;
    numberOfThreads_             = 8 ; // 0 - number of available cores
  } // pNSGAII_Settings

  
  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   * @return A NSGAII algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Selection  selection ;
    Crossover  crossover ;
    Mutation   mutation  ;

    HashMap  parameters ; // Operator parameters

    IParallelEvaluator parallelEvaluator = new MultithreadedEvaluator(numberOfThreads_) ;

    // Creating the algorithm. 
    algorithm = new pNSGAII(problem_, parallelEvaluator) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

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
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;     

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
    
    return algorithm ;
  } // configure

  /**
   * Configure pNSGAII with user-defined parameter experiments.settings
   * @return A pNSGAII algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Selection  selection ;
    Crossover  crossover ;
    Mutation   mutation  ;

    HashMap  parameters ; // Operator parameters

    numberOfThreads_ = Integer.parseInt(configuration.getProperty("numberOfThreads",String.valueOf(numberOfThreads_)));

    IParallelEvaluator parallelEvaluator = new MultithreadedEvaluator(numberOfThreads_) ;

    // Creating the algorithm.
    algorithm = new pNSGAII(problem_, parallelEvaluator) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

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
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    return algorithm ;
  }
} //pNSGAII_Settings
