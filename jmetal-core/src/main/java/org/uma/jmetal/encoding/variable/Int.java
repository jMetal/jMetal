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

package org.uma.jmetal.encoding.variable;

import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.logging.Level;

/**
 * This class implements an integer decision variable
 */
public class Int implements GenericIntVariable {
  private static final long serialVersionUID = -220713455225959427L;

  private int value;
  private int lowerBound;
  private int upperBound;

  public Int() {
    lowerBound = java.lang.Integer.MIN_VALUE;
    upperBound = java.lang.Integer.MAX_VALUE;
    value = PseudoRandom.randInt(lowerBound, upperBound);
  }

  public Int(int lowerBound, int upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    value = PseudoRandom.randInt(lowerBound, upperBound);
  }

  public Int(int value, int lowerBound, int upperBound) {
    super();

    this.value = value;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  public Int(Int variable) throws JMetalException {
    lowerBound = variable.getLowerBound();
    upperBound = variable.getUpperBound();
    value = variable.getValue();
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public Variable copy() {
    return new Int(this);
  }

  public int getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(int lowerBound) {
    this.lowerBound = lowerBound;
  }

  public int getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(int upperBound) {
    this.upperBound = upperBound;
  }

  public String toString() {
    return value + "";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + lowerBound;
    result = prime * result + upperBound;
    result = prime * result + value;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Int)) {
      return false;
    }
    Int other = (Int) obj;
    if (lowerBound != other.lowerBound) {
      return false;
    }
    if (upperBound != other.upperBound) {
      return false;
    }
    if (value != other.value) {
      return false;
    }
    return true;
  }
}
