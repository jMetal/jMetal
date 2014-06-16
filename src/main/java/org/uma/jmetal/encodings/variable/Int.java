//  Int.java
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

package org.uma.jmetal.encodings.variable;

import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.logging.Level;

/**
 * This class implements an integer decision variable
 */
public class Int extends Variable {
  private static final long serialVersionUID = -220713455225959427L;

  private int value_;      
  private int lowerBound_;  
  private int upperBound_;  

  public Int() {
    lowerBound_ = java.lang.Integer.MIN_VALUE;
    upperBound_ = java.lang.Integer.MAX_VALUE;
    value_ = 0;
  }

  public Int(int lowerBound, int upperBound) {
    lowerBound_ = lowerBound;
    upperBound_ = upperBound;
    value_ = PseudoRandom.randInt(lowerBound, upperBound);
  }

  public Int(int value, int lowerBound, int upperBound) {
    super();

    value_ = value;
    lowerBound_ = lowerBound;
    upperBound_ = upperBound;
  }

  public Int(Variable variable) throws JMetalException {
    lowerBound_ = (int) variable.getLowerBound();
    upperBound_ = (int) variable.getUpperBound();
    value_ = (int) variable.getValue();
  }

  public double getValue() {
    return value_;
  }

  public void setValue(double value) {
    value_ = (int) value;
  }

  public Variable deepCopy() {
    try {
      return new Int(this);
    } catch (JMetalException e) {
      Configuration.logger_.log(Level.SEVERE, "Int.deepCopy.execute: JMException", e);
      return null;
    }
  }

  public double getLowerBound() {
    return lowerBound_;
  }

  public void setLowerBound(double lowerBound) {
    lowerBound_ = (int) lowerBound;
  } 

  public double getUpperBound() {
    return upperBound_;
  }

  public void setUpperBound(double upperBound) {
    upperBound_ = (int) upperBound;
  }
  
  public String toString() {
    return value_ + "";
  }
}
