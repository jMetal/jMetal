//  OMOPSO_Settings.java 
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
import jmetal.metaheuristics.omopso.OMOPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm OMOPSO
 */
public class OMOPSO_Settings extends Settings{
  
  public int    swarmSize_         ;
  public int    maxIterations_     ;
  public int    archiveSize_       ;
  public double perturbationIndex_ ;
  public double mutationProbability_ ;
  
  /**
   * Constructor
   */
  public OMOPSO_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    e.printStackTrace();
    }      
    // Default experiments.settings
    swarmSize_         = 100 ;
    maxIterations_     = 250 ;
    archiveSize_       = 100 ;
    perturbationIndex_ = 0.5 ;
    mutationProbability_ = 1.0/problem_.getNumberOfVariables() ;
  } // OMOPSO_Settings
  
  /**
   * Configure OMOPSO with user-defined parameter experiments.settings
   * @return A OMOPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Mutation  uniformMutation ;
    Mutation nonUniformMutation ;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new OMOPSO(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("swarmSize",swarmSize_);
    algorithm.setInputParameter("archiveSize",archiveSize_);
    algorithm.setInputParameter("maxIterations",maxIterations_);
    
    
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("perturbation", perturbationIndex_) ;
    uniformMutation = new UniformMutation(parameters);
    
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("perturbation", perturbationIndex_) ;
    parameters.put("maxIterations", maxIterations_) ;
    nonUniformMutation = new NonUniformMutation(parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("uniformMutation",uniformMutation);
    algorithm.addOperator("nonUniformMutation",nonUniformMutation);

    return algorithm ;
  } // configure

  /**
   * Configure dMOPSO with user-defined parameter experiments.settings
   * @return A dMOPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Mutation  uniformMutation ;
    Mutation nonUniformMutation ;

    HashMap  parameters ; // Operator parameters

    // Creating the algorithm.
    algorithm = new OMOPSO(problem_) ;

    // Algorithm parameters
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize",String.valueOf(swarmSize_)));
    maxIterations_  = Integer.parseInt(configuration.getProperty("maxIterations",String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));

    algorithm.setInputParameter("swarmSize",swarmSize_);
    algorithm.setInputParameter("maxIterations",maxIterations_);
    algorithm.setInputParameter("archiveSize",archiveSize_);

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    perturbationIndex_ = Double.parseDouble(configuration.getProperty("perturbationIndex",String.valueOf(mutationProbability_)));
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("perturbation", perturbationIndex_) ;
    uniformMutation = new UniformMutation(parameters);

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("perturbation", perturbationIndex_) ;
    parameters.put("maxIterations", maxIterations_) ;
    nonUniformMutation = new NonUniformMutation(parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("uniformMutation",uniformMutation);
    algorithm.addOperator("nonUniformMutation",nonUniformMutation);
    return algorithm ;
  }
} // OMOPSO_Settings
