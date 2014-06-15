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

package jmetal.encodings.variable;

import jmetal.core.Problem;
import jmetal.core.Variable;
import jmetal.util.Configuration;
import jmetal.util.JMetalException;
import jmetal.util.random.PseudoRandom;


/**
 * Class implementing a decision encodings.variable representing an array of real values.
 * The real values of the array have their own bounds.
 */
public class ArrayReal extends Variable {
  /**
   *
   */
  private static final long serialVersionUID = -731434436787291959L;

  /**
   * Problem using the type
   */
  private Problem problem_;

  /**
   * Stores an array of real values
   */
  private double[] array_;
  /**
   * Stores the length of the array
   */
  private int size_;

  /**
   * Constructor
   */
  public ArrayReal() {
    problem_ = null;
    size_ = 0;
    array_ = null;
  }

  /**
   * Constructor
   *
   * @param size Size of the array
   */
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

  /**
   * Copy Constructor
   *
   * @param arrayReal The arrayReal to copy
   */
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

  /**
   * Returns the length of the arrayReal.
   *
   * @return The length
   */
  public int getLength() {
    return size_;
  }

  /**
   * getValue
   *
   * @param index Index of value to be returned
   * @return the value in position index
   */
  public double getValue(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return array_[index];
    } else {
      Configuration.logger_.severe(
        jmetal.encodings.variable.ArrayReal.class + ".getValue(): index value (" + index
          + ") invalid"
      );
      throw new JMetalException(
        jmetal.encodings.variable.ArrayReal.class + ".ArrayReal: index value (" + index
          + ") invalid"
      );
    }
  }

  /**
   * setValue
   *
   * @param index Index of value to be returned
   * @param value The value to be set in position index
   */
  public void setValue(int index, double value) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      array_[index] = value;
    } else {
      Configuration.logger_.severe(
        jmetal.encodings.variable.ArrayReal.class + ".setValue(): index value (" + index
          + ") invalid"
      );
      throw new JMetalException(
        jmetal.encodings.variable.ArrayReal.class + ": index value (" + index + ") invalid");
    }
  }

  /**
   * Get the lower bound of a value
   *
   * @param index The index of the value
   * @return the lower bound
   */
  public double getLowerBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return problem_.getLowerLimit(index);
    } else {
      Configuration.logger_.severe(
        jmetal.encodings.variable.ArrayReal.class + ".getLowerBound(): index value (" + index
          + ") invalid"
      );
      throw new JMetalException(
        jmetal.encodings.variable.ArrayReal.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public double getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return problem_.getUpperLimit(index);
    } else {
      throw new JMetalException(
        jmetal.encodings.variable.ArrayReal.class + ".getUpperBound: index value (" + index
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
