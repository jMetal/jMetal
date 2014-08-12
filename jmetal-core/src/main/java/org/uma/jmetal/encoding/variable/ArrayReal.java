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

import java.util.Arrays;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;


/**
 * Class implementing a decision encoding.variable representing an array of real values.
 * The real values of the array have their own bounds.
 */
public class ArrayReal implements Variable {
  private static final long serialVersionUID = -731434436787291959L;

  private Problem problem;
  private double[] array;
  private int size;

  public ArrayReal() {
    problem = null;
    size = 0;
    array = null;
  }

  public ArrayReal(int size, Problem problem) {
    this.problem = problem;
    this.size = size;
    array = new double[this.size];

    for (int i = 0; i < this.size; i++) {
      array[i] = PseudoRandom.randDouble() * (this.problem.getUpperLimit(i) -
        this.problem.getLowerLimit(i)) +
        this.problem.getLowerLimit(i);
    }
  }

  public ArrayReal(ArrayReal arrayReal) {
    problem = arrayReal.problem;
    size = arrayReal.size;
    array = new double[size];

    System.arraycopy(arrayReal.array, 0, array, 0, size);
  }

  public double[] getArray() {
    return array;
  }

  @Override
  public Variable copy() {
    return new ArrayReal(this);
  }

  public int length() {
    return size;
  }

  public double getValue(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return array[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ".ArrayReal: index value (" + index
          + ") invalid"
      );
    }
  }

  public void setValue(int index, double value) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      array[index] = value;
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ": index value (" + index + ") invalid");
    }
  }

  public double getLowerBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return problem.getLowerLimit(index);
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayReal.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public double getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return problem.getUpperLimit(index);
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
    for (int i = 0; i < (size - 1); i++) {
      string += array[i] + " ";
    }

    string += array[size - 1];
    return string;
  }

  @Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + Arrays.hashCode(array);
	  result = prime * result + size;
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
	  if (!(obj instanceof ArrayReal)) {
		  return false;
	  }
	  ArrayReal other = (ArrayReal) obj;
	  if (!Arrays.equals(array, other.array)) {
		  return false;
	  }
	  if (size != other.size) {
		  return false;
	  }
	  return true;
  }
}
