//  ArrayInt.java
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

package jmetal.encodings.variable;

import jmetal.core.Problem;
import jmetal.core.Variable;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/**
 * Class implementing a decision variable representing an array of integers.
 * The integer values of the array have their own bounds.
 */
public class ArrayInt extends Variable {
	
	/**
	 * Problem using the type
	 */
	Problem problem_ ;
	
	/**
   * Stores an array of integer values
   */
  public int [] array_;
  
  /**
   * Stores the length of the array
   */
  public int size_;
    
  /**
   * Store the lower and upper bounds of each int value of the array in case of
   * having each one different limits
   */
  private int []lowerBounds_ ;
  private int []upperBounds_ ;

  /**
   * Constructor
   */
  public ArrayInt() {
    lowerBounds_ = null ;
    upperBounds_ = null ;
    size_   = 0;
    array_ = null;
    problem_ = null ;
  } // Constructor

  /**
   * Constructor
   * @param size Size of the array
   */
  public ArrayInt(int size) {
		size_   = size;
		array_ = new int[size_];
		
    lowerBounds_ = new int[size_] ;
    upperBounds_ = new int[size_] ;
  } // Constructor

  /**
   * Constructor 
   * @param size The size of the array
   * @param lowerBound Lower bounds
   * @param upperBound Upper bounds
   */
  public ArrayInt(int size, double[] lowerBounds, double [] upperBounds) {
		size_   = size;
		array_ = new int[size_];
		
    lowerBounds_ = new int[size_] ;
    upperBounds_ = new int[size_] ;
    
    for (int i = 0; i < size_ ; i++) {
    	lowerBounds_[i] = (int)lowerBounds[i] ;
    	upperBounds_[i] = (int)upperBounds[i] ;
    	array_[i] = PseudoRandom.randInt(lowerBounds_[i], upperBounds_[i]) ;
    } // for
  } // Constructor
  
  /** 
   * Copy Constructor
   * @param arrayInt The arrayInt to copy
   */
  public ArrayInt(ArrayInt arrayInt) {
    size_   = arrayInt.size_;
    array_ = new int[size_];
        	
    lowerBounds_ = new int[size_] ;
   	upperBounds_ = new int[size_] ;
    	
    for (int i = 0; i < size_; i++) {
      array_[i] = arrayInt.array_[i];
      lowerBounds_[i] = arrayInt.lowerBounds_[i] ;
      upperBounds_[i] = arrayInt.upperBounds_[i] ;
    } // for
  } // Copy Constructor

  @Override
  public Variable deepCopy() {
    return new ArrayInt(this);
  } // deepCopy
  
  /**
   * Returns the length of the arrayInt.
   * @return The length
   */
  public int getLength(){
    return size_;
  } // getLength
 	  
   /**
    * getValue
    * @param index Index of value to be returned
    * @return the value in position index
    */
  public int getValue(int index) throws JMException {
 	  if ((index >= 0) && (index < size_))
 		  return array_[index] ;
 	  else {
      Configuration.logger_.severe("jmetal.base.variable.ArrayInt.getValue: index value (" + index + ") invalid");
      throw new JMException("jmetal.base.variable.ArrayInt: index value (" + index + ") invalid") ;
    } // if
  } // getValue
   
   /**
    * setValue
    * @param index Index of value to be returned
    * @param value The value to be set in position index
    */
  public void setValue(int index, int value) throws JMException {
    if ((index >= 0) && (index < size_))
      array_[index] = value;
   else {
     Configuration.logger_.severe("jmetal.base.variable.ArrayInt.setValue: index value (" + index + ") invalid");
     throw new JMException("jmetal.base.variable.ArrayInt: index value (" + index + ") invalid") ;
    } // else
  } // setValue
  

	/**
	 * Get the lower bound of a value
	 * @param index The index of the value
	 * @return the lower bound
	 */
	public double getLowerBound(int index) throws JMException {
		if ((index >= 0) && (index < size_))
			return lowerBounds_[index] ;
		else {
			Configuration.logger_.severe("jmetal.base.variable.ArrayInt.getLowerBound: index value (" + index + ") invalid");
			throw new JMException("jmetal.base.variable.getLowerBound: index value (" + index + ") invalid") ;
		} // else	
	} // getLowerBound

	/**
	 * Get the upper bound of a value
	 * @param index The index of the value
	 * @return the upper bound
	 */
	public double getUpperBound(int index) throws JMException {
		if ((index >= 0) && (index < size_))
			return upperBounds_[index];
		else {
			Configuration.logger_.severe("jmetal.base.variable.ArrayInt.getUpperBound: index value (" + index + ") invalid");
			throw new JMException("jmetal.base.variable.getUpperBound: index value (" + index + ") invalid") ;
		} // else
	} // getLowerBound

   
   /**
    * Returns a string representing the object
    * @return The string
    */ 
   public String toString(){
     String string ;
      
     string = "" ;
     for (int i = 0; i < size_ ; i ++)
       string += array_[i] + " " ;
       
     return string ;
   } // toString  
} // ArrayInt
