//  UniformMutation.java
//
//  Author:
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

package jmetal.operators.mutation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;

/**
 * This class implements a uniform mutation operator.
 */
public class UniformMutation extends Mutation{
  /**
   * Valid solution types to apply this operator 
   */
  private static List VALID_TYPES = Arrays.asList(RealSolutionType.class, 
  		                                            ArrayRealSolutionType.class) ;
  /**
   * Stores the value used in a uniform mutation operator
   */
  private Double perturbation_;
  
  private Double mutationProbability_ = null;

  /** 
   * Constructor
   * Creates a new uniform mutation operator instance
   */
  public UniformMutation(HashMap<String, Object> parameters) {
  	super(parameters) ;
  	
  	if (parameters.get("probability") != null)
  		mutationProbability_ = (Double) parameters.get("probability") ;  		
  	if (parameters.get("perturbation") != null)
  		perturbation_ = (Double) parameters.get("perturbation") ;  		

  } // UniformMutation


  /**
   * Constructor
   * Creates a new uniform mutation operator instance
   */
  //public UniformMutation(Properties properties) {
  //  this();
  //} // UniformMutation


  /**
  * Performs the operation
  * @param probability Mutation probability
  * @param solution The solution to mutate
   * @throws JMException 
  */
  public void doMutation(double probability, Solution solution) throws JMException {  
  	XReal x = new XReal(solution) ; 

    for (int var = 0; var < solution.getDecisionVariables().length; var++) {
      if (PseudoRandom.randDouble() < probability) {
        double rand = PseudoRandom.randDouble();
        double tmp = (rand - 0.5)*perturbation_.doubleValue();
                                
        tmp += x.getValue(var);
                
        if (tmp < x.getLowerBound(var))
          tmp = x.getLowerBound(var);
        else if (tmp > x.getUpperBound(var))
          tmp = x.getUpperBound(var);
                
        x.setValue(var, tmp) ;
      } // if
    } // for
  } // doMutation
  
  /**
  * Executes the operation
  * @param object An object containing the solution to mutate
   * @throws JMException 
  */
  public Object execute(Object object) throws JMException {
    Solution solution = (Solution )object;
    
		if (!VALID_TYPES.contains(solution.getType().getClass())) {
      Configuration.logger_.severe("UniformMutation.execute: the solution " +
          "is not of the right type. The type should be 'Real', but " +
          solution.getType() + " is obtained");

      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;
    } // if 
    
    doMutation(mutationProbability_,solution);
        
    return solution;
  } // execute                  
} // UniformMutation
