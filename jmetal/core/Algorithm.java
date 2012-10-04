//  Algorithm.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//// 
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

package jmetal.core ;

import java.io.Serializable;
import java.util.*;

import jmetal.util.JMException;

/** 
 *  This class implements a generic template for the algorithms developed in
 *  jMetal. Every algorithm must have a mapping between the parameters and 
 *  and their names, and another mapping between the operators and their names. 
 *  The class declares an abstract method called <code>execute</code>, which 
 *  defines the behavior of the algorithm.
 */ 
public abstract class Algorithm implements Serializable {
   
  /**
   * Stores the problem to solve
   */
  protected Problem problem_;        

 /** 
  * Stores the operators used by the algorithm, such as selection, crossover,
  * etc.
  */
  protected Map<String,Operator> operators_ = null;
  
 /** 
  * Stores algorithm specific parameters. For example, in NSGA-II these
  * parameters include the population size and the maximum number of function
  * evaluations.
  */
  protected Map<String,Object> inputParameters_ = null;  
  
  /** 
   * Stores output parameters, which are retrieved by Main object to 
   * obtain information from an algorithm.
   */
  protected Map<String,Object> outPutParameters_ = null;
  
  /**
   * Constructor
   * @param problem
   */
  public Algorithm(Problem problem) {
  	problem_ = problem ;
  }
  
 /**   
  * Launches the execution of an specific algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
  */
  public abstract SolutionSet execute() throws JMException, ClassNotFoundException ;   
  
 /**
  * Offers facilities for add new operators for the algorithm. To use an
  * operator, an algorithm has to obtain it through the 
  * <code>getOperator</code> method.
  * @param name The operator name
  * @param operator The operator
  */
  public void addOperator(String name, Operator operator){
    if (operators_ == null) {
      operators_ = new HashMap<String,Operator>();
    }        
    operators_.put(name,operator);
  } // addOperator 
  
 /**
  * Gets an operator through his name. If the operator doesn't exist or the name 
  * is wrong this method returns null. The client of this method have to check 
  * the result of the method.
  * @param name The operator name
  * @return The operator if exists, null in another case.
  */
  public Operator getOperator(String name){
    return operators_.get(name);
  } // getOperator   
  
 /**
  * Sets an input parameter to an algorithm. Typically,
  * the method is invoked by a Main object before running an algorithm. 
  * The parameters have to been inserted using their name to access them through 
  * the <code>getInputParameter</code> method.
  * @param name The parameter name
  * @param object Object that represent a parameter for the
  * algorithm.
  */
  public void setInputParameter(String name, Object object){
    if (inputParameters_ == null) {
      inputParameters_ = new HashMap<String,Object>();
    }        
    inputParameters_.put(name,object);
  } // setInputParameter  
  
 /**
  * Gets an input parameter through its name. Typically,
  * the method is invoked by an object representing an algorithm
  * @param name The parameter name
  * @return Object representing the parameter or null if the parameter doesn't
  * exist or the name is wrong
  */
  public Object getInputParameter(String name){
    return inputParameters_.get(name);
  } // getInputParameter
  
 /**
  * Sets an output parameter that can be obtained by invoking 
  * <code>getOutputParame</code>. Typically this algorithm is invoked by an
  * algorithm at the end of the <code>execute</code> to retrieve output 
  * information
  * @param name The output parameter name
  * @param object Object representing the output parameter
  */  
  public void setOutputParameter(String name, Object object) {
    if (outPutParameters_ == null) {
      outPutParameters_ = new HashMap<String,Object>();
    }        
    outPutParameters_.put(name,object);
  } // setOutputParameter  
  
 /**
  * Gets an output parameter through its name. Typically,
  * the method is invoked by a Main object after the execution of an algorithm.
  * @param name The output parameter name
  * @return Object representing the output parameter, or null if the parameter
  * doesn't exist or the name is wrong.
  */
  public Object getOutputParameter(String name) {
    if (outPutParameters_ != null) 
      return outPutParameters_.get(name);
    else
      return null ;
  } // getOutputParameter   
  
  /**
   * Returns the problem to solve
  * @return Problem The problem to solve
   */
  public Problem getProblem() {
  	return problem_ ;
  }
} // Algorithm
