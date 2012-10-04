//  AbYSS_Settings.java 
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

import java.util.HashMap;

import jmetal.metaheuristics.abyss.*;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.experiments.Settings;


import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * Settings class of algorithm AbYSS
 */
public class AbYSS_Settings extends Settings {

	public int populationSize_ ;
  public int maxEvaluations_ ;
  public int archiveSize_ ;
  public int refSet1Size_ ;
  public int refSet2Size_ ;
  public double mutationProbability_ ;
  public double crossoverProbability_ ;
  public double crossoverDistributionIndex_ ;
  public double mutationDistributionIndex_  ;
  public int improvementRounds_ ;
  
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public AbYSS_Settings(String problemName) {
    super(problemName);

    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      
    populationSize_ = 20;
    maxEvaluations_ = 25000;
    archiveSize_ = 100;
    refSet1Size_ = 10;
    refSet2Size_ = 10;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 1.0;
    crossoverDistributionIndex_ = 20.0  ;
    mutationDistributionIndex_  = 20.0  ;
    improvementRounds_ = 1;
    
  } // AbYSS_Settings

  /**
   * Configure the MOCell algorithm with default parameter settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator improvement; // Operator for improvement

    HashMap  parameters ; // Operator parameters

    QualityIndicator indicators;

    // Creating the problem
    algorithm = new AbYSS(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("refSet1Size", refSet1Size_);
    algorithm.setInputParameter("refSet2Size", refSet2Size_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);    
    
    parameters = new HashMap() ;
    parameters.put("improvementRounds", 1) ;
    parameters.put("problem",problem_) ;
    parameters.put("mutation",mutation) ;
    improvement = new MutationLocalSearch(parameters);

    // Adding the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("improvement", improvement);

    // Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
      indicators = new QualityIndicator(problem_, paretoFrontFile_);
      algorithm.setInputParameter("indicators", indicators);
    } // if
    return algorithm;
  } // Constructor
} // AbYSS_Settings
