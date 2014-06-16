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

package org.uma.jmetal.encodings.variable;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;


/**
 * Class implementing a decision variable representing an array of integers.
 * The integer values of the array have their own bounds.
 */
public class ArrayInt extends Variable {
  private static final long serialVersionUID = 2165010259190501390L;
  private Problem problem_;
  private int[] array_;
  private int size_;
  private int[] lowerBounds_;
  private int[] upperBounds_;

  public ArrayInt() {
    lowerBounds_ = null;
    upperBounds_ = null;
    size_ = 0;
    array_ = null;
    problem_ = null;
  }

  public ArrayInt(int size) {
    size_ = size;
    array_ = new int[size_];

    lowerBounds_ = new int[size_];
    upperBounds_ = new int[size_];
  }

  public ArrayInt(int size, Problem problem) {
    problem_ = problem;
    size_ = size;
    array_ = new int[size_];
    lowerBounds_ = new int[size_];
    upperBounds_ = new int[size_];

    for (int i = 0; i < size_; i++) {
      lowerBounds_[i] = (int) problem_.getLowerLimit(i);
      upperBounds_[i] = (int) problem_.getUpperLimit(i);
      array_[i] = PseudoRandom.randInt(lowerBounds_[i], upperBounds_[i]);
    }
  }

  public ArrayInt(int size, double[] lowerBounds, double[] upperBounds) {
    size_ = size;
    array_ = new int[size_];

    lowerBounds_ = new int[size_];
    upperBounds_ = new int[size_];

    for (int i = 0; i < size_; i++) {
      lowerBounds_[i] = (int) lowerBounds[i];
      upperBounds_[i] = (int) upperBounds[i];
      array_[i] = PseudoRandom.randInt(lowerBounds_[i], upperBounds_[i]);
    }
  }

  private ArrayInt(ArrayInt arrayInt) {
    size_ = arrayInt.size_;
    array_ = new int[size_];

    lowerBounds_ = new int[size_];
    upperBounds_ = new int[size_];

    for (int i = 0; i < size_; i++) {
      array_[i] = arrayInt.array_[i];
      lowerBounds_[i] = arrayInt.lowerBounds_[i];
      upperBounds_[i] = arrayInt.upperBounds_[i];
    }
  }

  public int[] getArray() {
    return array_;
  }

  @Override
  public Variable deepCopy() {
    return new ArrayInt(this);
  }

  public int length() {
    return size_;
  } 

  public int getValue(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return array_[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encodings.variable.ArrayInt.class + ": index value (" + index + ") invalid");
    }
  }

  public void setValue(int index, int value) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      array_[index] = value;
    } else {
      throw new JMetalException(
        org.uma.jmetal.encodings.variable.ArrayInt.class + ": index value (" + index + ") invalid");
    }
  }

  public double getLowerBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return lowerBounds_[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encodings.variable.ArrayInt.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public double getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size_)) {
      return upperBounds_[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encodings.variable.ArrayInt.class + ".getUpperBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public String toString() {
    String string;

    string = "";
    for (int i = 0; i < size_; i++) {
      string += array_[i] + " ";
    }

    return string;
  }
}
