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

import jmetal.metaheuristics.omopso.*;

import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.util.PropUtils;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.UniformMutation;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * Settings class of algorithm OMOPSO
 */
public class OMOPSO_Settings extends Settings{
  
  public int    swarmSize_         ;
  public int    maxIterations_     ;
  public int    archiveSize_       ;
  public double perturbationIndex_ ;
  
  /**
   * Constructor
   */
  public OMOPSO_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      
    // Default settings
    swarmSize_         = 100 ;
    maxIterations_     = 250 ;
    archiveSize_       = 100 ;
    perturbationIndex_ = 0.5 ;
  } // OMOPSO_Settings
  
  /**
   * Configure OMOPSO with user-defined parameter settings
   * @return A OMOPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Mutation  uniformMutation ;
    Mutation nonUniformMutation ;
    
    QualityIndicator indicators ;
    
    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new OMOPSO(problem_) ;
    
    Integer maxIterations = 250 ;
    Double perturbationIndex = 0.5 ;
    Double mutationProbability = 1.0/problem_.getNumberOfVariables() ;
    
    // Algorithm parameters
    algorithm.setInputParameter("swarmSize",swarmSize_);
    algorithm.setInputParameter("archiveSize",archiveSize_);
    algorithm.setInputParameter("maxIterations",maxIterations_);
    
    
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability) ;
    parameters.put("perturbation", perturbationIndex) ;
    uniformMutation = new UniformMutation(parameters);
    
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability) ;
    parameters.put("perturbation", perturbationIndex) ;
    parameters.put("maxIterations", maxIterations) ;
    nonUniformMutation = new NonUniformMutation(parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("uniformMutation",uniformMutation);
    algorithm.addOperator("nonUniformMutation",nonUniformMutation);
   // Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
      indicators = new QualityIndicator(problem_, paretoFrontFile_);
      algorithm.setInputParameter("indicators", indicators) ;  
   } // if
    return algorithm ;
  } // configure
} // OMOPSO_Settings
