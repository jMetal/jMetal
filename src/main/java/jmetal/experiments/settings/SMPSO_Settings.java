//  SMPSO_Settings.java 
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
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Settings class of algorithm SMPSO
 */
public class SMPSO_Settings extends Settings{
  
  private int    swarmSize_                 ;
  private int    maxIterations_             ;
  private int    archiveSize_               ;
  private double mutationDistributionIndex_ ;
  private double mutationProbability_       ;

  private double C1Max_;
  private double C1Min_;
  private double C2Max_;
  private double C2Min_;
  private double WMax_;
  private double WMin_;
  private double ChVel1_;
  private double ChVel2_;

  /**
   * Constructor
   * @throws JMException 
   */
  public SMPSO_Settings(String problem) throws JMException {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);

    // Default experiments.settings
    swarmSize_                 = 100 ;
    maxIterations_             = 250 ;
    archiveSize_               = 100 ;
    mutationDistributionIndex_ = 20.0 ;
    mutationProbability_       = 1.0/problem_.getNumberOfVariables() ;

    C1Max_ = 2.5;
    C1Min_ = 1.5;
    C2Max_ = 2.5;
    C2Min_ = 1.5;
    WMax_ = 0.1;
    WMin_ = 0.1;
    ChVel1_ = -1;
    ChVel2_ = -1;
  } 
  
  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Mutation  mutation ;

    // Creating the problem
    algorithm = new SMPSO(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("swarmSize", swarmSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("C1Min",1.5);
    algorithm.setInputParameter("C1Max",2.5);
    algorithm.setInputParameter("C2Min",1.5);
    algorithm.setInputParameter("C2Max",2.5);
    algorithm.setInputParameter("WMin",0.1);
    algorithm.setInputParameter("WMax",0.1);
    algorithm.setInputParameter("ChVel1",-1.0);
    algorithm.setInputParameter("ChVel2",-1.0);

    HashMap<String, Object> parameters = new HashMap<String, Object>() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

    algorithm.addOperator("mutation",mutation);

		return algorithm ;
  } 

  /**
   * Configure SMPSO with user-defined parameter experiments.settings
   * @return A SMPSO algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    swarmSize_ = Integer.parseInt(configuration.getProperty("swarmSize",String.valueOf(swarmSize_)));
    maxIterations_  = Integer.parseInt(configuration.getProperty("maxIterations",String.valueOf(maxIterations_)));
    archiveSize_ = Integer.parseInt(configuration.getProperty("archiveSize", String.valueOf(archiveSize_)));

    C1Min_ = Double.parseDouble(configuration.getProperty("C1Min", String.valueOf(C1Min_)));
    C1Max_ = Double.parseDouble(configuration.getProperty("C1Max", String.valueOf(C1Max_)));
    C2Min_ = Double.parseDouble(configuration.getProperty("C2Min", String.valueOf(C2Min_)));
    C2Min_ = Double.parseDouble(configuration.getProperty("C2Max", String.valueOf(C2Max_)));
    WMin_ = Double.parseDouble(configuration.getProperty("WMin", String.valueOf(WMin_)));
    WMax_ = Double.parseDouble(configuration.getProperty("WMax", String.valueOf(WMax_)));

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));

    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability",String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex",String.valueOf(mutationDistributionIndex_)));

    return configure() ;
  }
}
