//  MOEAD_Settings.java 
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

import jmetal.metaheuristics.moead.*;

import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 * Settings class of algorithm MOEA/D
 */
public class MOEAD_Settings extends Settings {
  public double CR_ ;
  public double F_  ;
  public int populationSize_ ;
  public int maxEvaluations_ ;
 
  public double mutationProbability_          ;
  public double distributionIndexForMutation_ ;

  public String dataDirectory_  ;

  public int numberOfThreads  ; // Parameter used by the pMOEAD version
  public String moeadVersion  ;
  
  /**
   * Constructor
   */
  public MOEAD_Settings(String problem) {
    super(problem);
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      

    // Default settings
    CR_ = 1.0 ;
    F_  = 0.5 ;
    populationSize_ = 300;
    maxEvaluations_ = 150000;
   
    mutationProbability_ = 1.0/problem_.getNumberOfVariables() ;
    distributionIndexForMutation_ = 20;
    
    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    dataDirectory_ =  "/Users/antonio/Softw/pruebas/data/MOEAD_parameters/Weight" ;

    numberOfThreads = 2 ; // Parameter used by the pMOEAD version
    moeadVersion = "MOEAD" ; // or "pMOEAD"
  } // MOEAD_Settings

  /**
   * Configure the algorithm with the specified parameter settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;

    QualityIndicator indicators ;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    if (moeadVersion.compareTo("MOEAD") == 0 )
      algorithm = new MOEAD(problem_);
    else { // pMOEAD
      algorithm = new pMOEAD(problem_); 
      algorithm.setInputParameter("numberOfThreads", numberOfThreads);
    } // else
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("dataDirectory", dataDirectory_) ;
    
    // Crossover operator 
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
    
    // Mutation operator
    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem_.getNumberOfVariables()) ;
    parameters.put("distributionIndex", distributionIndexForMutation_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);         
    
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    
    // Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
      indicators = new QualityIndicator(problem_, paretoFrontFile_);
      algorithm.setInputParameter("indicators", indicators);
    } // if

    return algorithm;
  } // configure
} // MOEAD_Settings
