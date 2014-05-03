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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.cellde.CellDE;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm CellDE
 */
public class CellDE_Settings extends Settings{
  
  public double CR_           ;
  public double F_            ;

  public int populationSize_  ;
  public int archiveSize_     ;
  public int maxEvaluations_  ;
  public int archiveFeedback_ ;
 
  /**
   * Constructor
   */
  public CellDE_Settings(String problemName) {
    super(problemName) ;
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      

    // Default experiments.settings
    CR_          = 0.5;
    F_           = 0.5    ;
    
    populationSize_ = 100   ;
    archiveSize_    = 100   ;
    maxEvaluations_ = 25000 ;
    archiveFeedback_= 20    ;
  } // CellDE_Settings
  
  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator  selection ;
    Operator  crossover ;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    Object [] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);      
    algorithm = new CellDE(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);
    algorithm.setInputParameter("archiveFeedBack", archiveFeedback_);
    
    // Crossover operator 
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
    
    // Add the operators to the algorithm
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ; 

    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("selection",selection);
    
    return algorithm ;
  } // configure

  /**
   * Configure CellDE with user-defined parameter experiments.settings
   * @return A CellDE algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Operator  selection ;
    Crossover crossover ;

    HashMap  parameters ; // Operator parameters

    // Creating the algorithm.
    algorithm = new CellDE(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveFeedBack", archiveFeedback_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    CR_ = Double.parseDouble(configuration.getProperty("CR",String.valueOf(CR_)));
    F_ = Double.parseDouble(configuration.getProperty("F",String.valueOf(F_)));
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

    // Add the operators to the algorithm
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("selection",selection);

    return algorithm ;
  }
} // CellDE_Settings
