//  pSMPSO_Settings.java 
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
import jmetal.metaheuristics.smpso.pSMPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;
import jmetal.util.parallel.MultithreadedEvaluator;
import jmetal.util.parallel.SynchronousParallelTaskExecutor;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm pSMPSO
 */
public class pSMPSO_Settings extends Settings{
  private int    swarmSize_                 ;
  private int    maxIterations_             ;
  private int    archiveSize_               ;
  private double mutationDistributionIndex_ ;
  private double mutationProbability_       ;
  private int    numberOfThreads_           ;

  /**
   * Constructor
   * @throws JMException 
   */
  public pSMPSO_Settings(String problem) throws JMException {
    super(problem) ;

    Object [] problemParams = {"Real"};
    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    swarmSize_                 = 100 ;
    maxIterations_             = 250 ;
    archiveSize_               = 100 ;
    mutationDistributionIndex_ = 20.0 ;
    mutationProbability_       = 1.0/problem_.getNumberOfVariables() ;
    // 0 - number of available cores
    numberOfThreads_           = 8 ; 
  }

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   *
   * @return A SMPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Mutation mutation;

    SynchronousParallelTaskExecutor parallelEvaluator = new MultithreadedEvaluator(numberOfThreads_);

    algorithm = new pSMPSO() ;
    algorithm.setProblem(problem_);

    ((pSMPSO)algorithm).setEvaluator(parallelEvaluator);
    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
    algorithm.setInputParameter("archiveSize", archiveSize_);

    HashMap<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

    algorithm.addOperator("mutation", mutation);

    return algorithm;
  } 

  /**
   * Configure pSMPSO with user-defined parameter experiments.settings
   *
   * @return A pSMPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    numberOfThreads_  = Integer.parseInt(configuration.getProperty("numberOfThreads",String.valueOf(numberOfThreads_)));
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize",String.valueOf(swarmSize_)));
    maxIterations_  = Integer.parseInt(configuration.getProperty("maxIterations",String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));
    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));

    return configure() ;
  }
}
