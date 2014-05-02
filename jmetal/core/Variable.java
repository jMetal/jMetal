//  SolutionType.java
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

package jmetal.core;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.Serializable;

/**
 * This abstract class is the base for defining new types of variables.
 * Many methods of <code>Variable</code> (<code>getValue</code>,
 * <code>setValue</code>,<code>
 * getLowerLimit</code>,<code>setLowerLimit</code>,<code>getUpperLimit</code>,
 * <code>setUpperLimit</code>)
 * are not applicable to all the subclasses of <code>Variable</code>.
 * For this reason, they are defined by default as giving a fatal error.
 */
public abstract class Variable implements Serializable {

  /** 
   * Creates an exact copy of a <code>Variable</code> object.
   * @return the copy of the object.
   */
  public abstract Variable deepCopy();

  /**
   * Gets the double value representating the encodings.variable.
   * It is used in subclasses of <code>Variable</code> (i.e. <code>Real</code> 
   * and <code>Int</code>).
   * As not all objects belonging to a subclass of <code>Variable</code> have a 
   * double value, a call to this method it is considered a fatal error by 
   * default, and the program is terminated. Those classes requiring this method 
   * must redefine it.
   */
  public double getValue() throws JMException {
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + " does not implement " +
        "method getValue");
    throw new JMException("Exception in " + name + ".getValue()") ;
  } // getValue
  
  /**
  * Sets a double value to a encodings.variable in subclasses of <code>Variable</code>.
  * As not all objects belonging to a subclass of <code>Variable</code> have a 
  * double value, a call to this method it is considered a fatal error by 
  * default, and the program is terminated. Those classes requiring this method 
  * must redefine it.
  */
  public void setValue(double value) throws JMException {
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + " does not implement " +
        "method setValue");
    throw new JMException("Exception in " + name + ".setValue()") ;
  } // setValue

  /**
   * Gets the lower bound value of a encodings.variable. As not all
   * objects belonging to a subclass of <code>Variable</code> have a lower bound,
   * a call to this method is considered a fatal error by default,
   * and the program is terminated.
   * Those classes requiring this method must redefine it.
   */
  public double getLowerBound() throws JMException { 
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + 
                       " does not implement method getLowerBound()");
    throw new JMException("Exception in " + name + ".getLowerBound()") ;
  } // getLowerBound
  
  /**
   * Gets the upper bound value of a encodings.variable. As not all
   * objects belonging to a subclass of <code>Variable</code> have an upper 
   * bound, a call to this method is considered a fatal error by default, and the 
   * program is terminated. Those classes requiring this method must redefine it.
   */
  public double getUpperBound() throws JMException {
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + 
                       " does not implement method getUpperBound()");
    throw new JMException("Exception in " + name + ".getUpperBound()") ;
  } // getUpperBound
  
  /**
   * Sets the lower bound for a encodings.variable. As not all objects belonging to a
   * subclass of <code>Variable</code> have a lower bound, a call to this method 
   * is considered a fatal error by default and the program is terminated.
   * Those classes requiring this method must to redefine it.
   */
  public void setLowerBound(double lowerBound) throws JMException {
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + 
                       " does not implement method setLowerBound()");
    throw new JMException("Exception in " + name + ".setLowerBound()") ;
  } // setLowerBound
  
  /**
   * Sets the upper bound for a encodings.variable. As not all objects belonging to a
   * subclass of <code>Variable</code> have an upper bound, a call to this method
   * is considered a fatal error by default, and the program is terminated. 
   * Those classes requiring this method must redefine it.
   */
  public void setUpperBound(double upperBound) throws JMException {
    Class cls = java.lang.String.class;
    String name = cls.getName(); 
    Configuration.logger_.severe("Class " + name + 
                       " does not implement method setUpperBound()");
    throw new JMException("Exception in " + name + ".setUpperBound()") ;
  } // setUpperBound

  /**
   * Gets the type of the encodings.variable. The types are defined in class Problem.
   * @return The type of the encodings.variable
   */
  
  public Class getVariableType() {
    return this.getClass() ;
  } // getVariableType
} // Variable
