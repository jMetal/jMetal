//  ArrayReal.java
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

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;


/**
 * Class implementing a decision encoding.variable representing an array of real values.
 * The real values of the array have their own bounds.
 */
public class ArrayReal extends Variable {
  private static final long serialVersionUID = -731434436787291959L;

  private Problem problem_;
  private double[] array_;
  private int size_;

  public ArrayReal() {
    problem_ = null;
    size_ = 0;
    array_ = null;
  }

  public ArrayReal(int size, Problem problem) {
    problem_ = problem;
    size_ = size;
    array_ = new double[size_];

    for (int i = 0; i < size_; i++) {
      array_[i] = PseudoRandom.randDouble() * (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) +
        problem_.getLowerLimit(i);
    }
  }

  private ArrayReal(ArrayReal arrayReal) {
    problem_ = arrayReal.problem_;
    size_ = arrayReal.size_;
    array_ = new double[size_];

    System.arraycopy(arrayReal.array_, 0, array_, 0, size_);
  }

  public double[] getArray() {
    return array_;
  }

  @Override
  public Variable deepCopy() {
    return new ArrayReal(this);
  }

  public int length() {
    return size_;
  }

  public double getValue(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return array_[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ".ArrayReal: index value (" + index
          + ") invalid"
      );
    }
  }

  public void setValue(int index, double value) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      array_[index] = value;
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ": index value (" + index + ") invalid");
    }
  }

  public double getLowerBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return problem_.getLowerLimit(index);
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public double getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return problem_.getUpperLimit(index);
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ".getUpperBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public String toString() {
    String string;

    string = "";
    for (int i = 0; i < (size_ - 1); i++) {
      string += array_[i] + " ";
    }

    string += array_[size_ - 1];
    return string;
  }
}
