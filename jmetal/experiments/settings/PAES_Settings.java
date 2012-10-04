//  PAES_Settings.java 
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

import jmetal.metaheuristics.paes.*;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;

import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Settings class of algorithm PAES
 */
public class PAES_Settings extends Settings{

	public int maxEvaluations_ ;
	public int archiveSize_    ;
	public int biSections_     ;
	public double mutationProbability_ ;
	public double distributionIndex_   ;

	/**
	 * Constructor
	 */
	public PAES_Settings(String problem) {
		super(problem) ;
		
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      
  	// Default settings
  	maxEvaluations_ = 25000 ;
  	archiveSize_    = 100   ;
  	biSections_     = 5     ;
  	mutationProbability_ = 1.0/problem_.getNumberOfVariables() ;
  	distributionIndex_   = 20.0 ;
	} // PAES_Settings

	/**
	 * Configure the MOCell algorithm with default parameter settings
	 * @return an algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm ;
		Mutation  mutation   ;

		QualityIndicator indicators ;

    HashMap  parameters ; // Operator parameters

		// Creating the problem
		algorithm = new PAES(problem_) ;

		// Algorithm parameters
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
		algorithm.setInputParameter("biSections", biSections_);
		algorithm.setInputParameter("archiveSize",archiveSize_ );

    // Mutation (Real variables)
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", distributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
    
    // Mutation (BinaryReal variables)
    //mutation = MutationFactory.getMutationOperator("BitFlipMutation");                    
    //mutation.setParameter("probability",0.1);
    
    // Add the operators to the algorithm
    algorithm.addOperator("mutation", mutation);

		// Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
			indicators = new QualityIndicator(problem_, paretoFrontFile_);
			algorithm.setInputParameter("indicators", indicators) ;  
		} // if
		return algorithm ;
	} // configure
} // PAES_Settings
