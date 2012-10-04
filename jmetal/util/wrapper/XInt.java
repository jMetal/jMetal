//  XInt.java
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

package jmetal.util.wrapper;

import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * Wrapper class for accessing integer-coded solutions
 */
public class XInt {
	Solution solution_ ;
	SolutionType type_ ;

	/**
	 * Constructor
	 */
	public XInt() {
	} // Constructor

	/**
	 * Constructor
	 * @param solution
	 */
	public XInt(Solution solution) {
		this() ;
		type_ = solution.getType() ;
		solution_ = solution ;
	}

	/**
	 * Gets value of a variable
	 * @param index Index of the variable
	 * @return The value of the variable
	 * @throws JMException
	 */
	public int getValue(int index) throws JMException {
		if (type_.getClass() == IntSolutionType.class){
			return (int)solution_.getDecisionVariables()[index].getValue() ;			
		} 
		else if (type_.getClass() == ArrayIntSolutionType.class) {
			return ((ArrayInt)(solution_.getDecisionVariables()[0])).array_[index] ;
		}
		else {
			Configuration.logger_.severe("jmetal.util.wrapper.XInt.getValue, solution type " +
					type_ + "+ invalid") ;		
		}
		return 0;
	} // Get value

	/**
	 * Sets the value of a variable
	 * @param index Index of the variable
	 * @param value Value to be assigned
	 * @throws JMException
	 */
	public void setValue(int index, int value) throws JMException {
		if (type_.getClass() == IntSolutionType.class)
			solution_.getDecisionVariables()[index].setValue(value) ;
		else if (type_.getClass() == ArrayIntSolutionType.class)
			((ArrayInt)(solution_.getDecisionVariables()[0])).array_[index]=value ;
		else
			Configuration.logger_.severe("jmetal.util.wrapper.XInt.setValue, solution type " +
					type_ + "+ invalid") ;		
	} // setValue	

	/**
	 * Gets the lower bound of a variable
	 * @param index Index of the variable
	 * @return The lower bound of the variable
	 * @throws JMException
	 */
	public int getLowerBound(int index) throws JMException {
		if (type_.getClass() == IntSolutionType.class)
			return (int)solution_.getDecisionVariables()[index].getLowerBound() ;
		else if (type_.getClass() == ArrayIntSolutionType.class) 
			return (int)((ArrayInt)(solution_.getDecisionVariables()[0])).getLowerBound(index) ;
		else {
			Configuration.logger_.severe("jmetal.util.wrapper.Xreal.getLowerBound, solution type " +
					type_ + "+ invalid") ;		
		}
		return 0 ;
	} // getLowerBound

	/**
	 * Gets the upper bound of a variable
	 * @param index Index of the variable
	 * @return The upper bound of the variable
	 * @throws JMException
	 */
	public int getUpperBound(int index) throws JMException {
		if (type_.getClass() == IntSolutionType.class)		
			return (int)solution_.getDecisionVariables()[index].getUpperBound() ;
		else if (type_.getClass() == ArrayIntSolutionType.class) 
			return (int)((ArrayInt)(solution_.getDecisionVariables()[0])).getUpperBound(index) ;
		else
			Configuration.logger_.severe("jmetal.util.wrapper.Xreal.getUpperBound, solution type " +
					type_ + "+ invalid") ;		

		return 0 ;
	} // getUpperBound

	/**
	 * Returns the number of variables of the solution
	 * @return
	 */
	public int getNumberOfDecisionVariables() {
		if (type_.getClass() == IntSolutionType.class)		
			return solution_.getDecisionVariables().length ;
		else if (type_.getClass() == ArrayIntSolutionType.class) 
			return ((ArrayInt)(solution_.getDecisionVariables()[0])).getLength() ;
		else
			Configuration.logger_.severe("jmetal.util.wrapper.XInt.size, solution type " +
					type_ + "+ invalid") ;		
		return 0 ;
	} // size
} // XInt