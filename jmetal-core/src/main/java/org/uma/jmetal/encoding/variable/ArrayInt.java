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

package org.uma.jmetal.encoding.variable;

import java.util.Arrays;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;


/**
 * Class implementing a decision variable representing an array of integers.
 * The integer values of the array have their own bounds.
 */
public class ArrayInt implements Variable {
  private static final long serialVersionUID = 2165010259190501390L;

  private Problem problem;
  private int[] array;
  private int size;

  public ArrayInt() {
    size = 0;
    array = null;
    problem = null;
  }

  public ArrayInt(int size) {
    this.size = size;
    array = new int[this.size];
  }

  public ArrayInt(int size, Problem problem) {
    this.problem = problem;
    this.size = size;
    array = new int[this.size];
    for (int i = 0; i < this.size; i++) {
      array[i] = PseudoRandom.randInt((int)problem.getLowerLimit(i), (int)problem.getUpperLimit(i));
    }
  }

  public ArrayInt(ArrayInt arrayInt) {
    problem = arrayInt.problem;
    size = arrayInt.size;
    array = new int[size];

    System.arraycopy(arrayInt.array, 0, array, 0, size);
  }

  public int[] getArray() {
    return array;
  }

  @Override
  public Variable copy() {
    return new ArrayInt(this);
  }

  public int length() {
    return size;
  } 

  public int getValue(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return array[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayInt.class + ": index value (" + index + ") invalid");
    }
  }

  public void setValue(int index, int value) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      array[index] = value;
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayInt.class + ": index value (" + index + ") invalid");
    }
  }

  public int getLowerBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return (int)problem.getLowerLimit(index);
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayInt.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public int getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return (int)problem.getUpperLimit(index);
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayInt.class + ".getUpperBound: index value (" + index
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
	  if (!(obj instanceof ArrayInt)) {
		  return false;
	  }
	  ArrayInt other = (ArrayInt) obj;
	  if (!Arrays.equals(array, other.array)) {
		  return false;
	  }
	  if (size != other.size) {
		  return false;
	  }
	  return true;
  }	
}
