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

package org.uma.jmetal.experiment.settings;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.metaheuristic.multiobjective.omopso.OMOPSO;
import org.uma.jmetal.operator.mutation.Mutation;
import org.uma.jmetal.operator.mutation.NonUniformMutation;
import org.uma.jmetal.operator.mutation.UniformMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.util.JMetalException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm OMOPSO
 */
public class OMOPSOSettings extends Settings{
  private int    swarmSize_         ;
  private int    maxIterations_     ;
  private int    archiveSize_       ;
  private double perturbationIndex_ ;
  private double mutationProbability_ ;

  /**
   * Constructor
   * @throws org.uma.jmetal.util.JMetalException
   */
  public OMOPSOSettings(String problem) throws JMetalException {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
	    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);

	    // Default experiment.settings
    swarmSize_         = 100 ;
    maxIterations_     = 250 ;
    archiveSize_       = 100 ;
    perturbationIndex_ = 0.5 ;
    mutationProbability_ = 1.0/problem_.getNumberOfVariables() ;
  } 

  /**
   * Configure OMOPSO with user-defined parameter experiment.settings
   *
   * @return A OMOPSO algorithm object
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Algorithm configure() throws JMetalException {
    Algorithm algorithm;
    Mutation uniformMutation;
    Mutation nonUniformMutation;

    // Creating the problem
    algorithm = new OMOPSO();
    algorithm.setProblem(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);


    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("perturbation", perturbationIndex_);
    uniformMutation = new UniformMutation(parameters);

    parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("perturbation", perturbationIndex_);
    parameters.put("maxIterations", maxIterations_);
    nonUniformMutation = new NonUniformMutation(parameters);

    // Add the operator to the algorithm
    algorithm.addOperator("uniformMutation", uniformMutation);
    algorithm.addOperator("nonUniformMutation", nonUniformMutation);

    return algorithm ;
  }

  /**
   * Configure dMOPSO with user-defined parameter experiment.settings
   *
   * @return A dMOPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMetalException {
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize",String.valueOf(swarmSize_)));
    maxIterations_  = Integer.parseInt(configuration.getProperty("maxIterations",String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    perturbationIndex_ = Double.parseDouble(configuration.getProperty("perturbationIndex",String.valueOf(mutationProbability_)));

    return configure() ;
  }
} 
