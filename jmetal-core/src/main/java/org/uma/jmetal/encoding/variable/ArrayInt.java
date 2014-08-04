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
public class ArrayInt extends Variable {
  private static final long serialVersionUID = 2165010259190501390L;

  private Problem problem;
  private int[] array;
  private int size;
  private int[] lowerBounds;
  private int[] upperBounds;

  public ArrayInt() {
    lowerBounds = null;
    upperBounds = null;
    size = 0;
    array = null;
    problem = null;
  }

  public ArrayInt(int size) {
    this.size = size;
    array = new int[this.size];

    lowerBounds = new int[this.size];
    upperBounds = new int[this.size];
  }

  public ArrayInt(int size, Problem problem) {
    this.problem = problem;
    this.size = size;
    array = new int[this.size];
    lowerBounds = new int[this.size];
    upperBounds = new int[this.size];

    for (int i = 0; i < this.size; i++) {
      lowerBounds[i] = (int) this.problem.getLowerLimit(i);
      upperBounds[i] = (int) this.problem.getUpperLimit(i);
      array[i] = PseudoRandom.randInt(lowerBounds[i], upperBounds[i]);
    }
  }

  public ArrayInt(int size, int[] lowerBounds, int[] upperBounds) {
    this.size = size;
    array = new int[this.size];

    this.lowerBounds = Arrays.copyOf(lowerBounds, lowerBounds.length); 
    this.upperBounds = Arrays.copyOf(upperBounds, upperBounds.length); 

    for (int i = 0; i < this.size; i++) {
      this.lowerBounds[i] = (int) lowerBounds[i];
      this.upperBounds[i] = (int) upperBounds[i];
      array[i] = PseudoRandom.randInt(this.lowerBounds[i], this.upperBounds[i]);
    }
  }

  private ArrayInt(ArrayInt arrayInt) {
    size = arrayInt.size;
    array = new int[size];

    lowerBounds = new int[size];
    upperBounds = new int[size];

    for (int i = 0; i < size; i++) {
      array[i] = arrayInt.array[i];
      lowerBounds[i] = arrayInt.lowerBounds[i];
      upperBounds[i] = arrayInt.upperBounds[i];
    }
  }

  public int[] getArray() {
    return array;
  }

  @Override
  public Variable deepCopy() {
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
      return lowerBounds[index];
    } else {
      throw new JMetalException(
        org.uma.jmetal.encoding.variable.ArrayInt.class + ".getLowerBound: index value (" + index
          + ") invalid"
      );
    }
  }

  public int getUpperBound(int index) throws JMetalException {
    if ((index >= 0) && (index < size)) {
      return upperBounds[index];
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
    for (int i = 0; i < size; i++) {
      string += array[i] + " ";
    }

    return string;
  }

	@Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + Arrays.hashCode(array);
	  result = prime * result + Arrays.hashCode(lowerBounds);
	  result = prime * result + ((problem == null) ? 0 : problem.hashCode());
	  result = prime * result + size;
	  result = prime * result + Arrays.hashCode(upperBounds);
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
	  if (!Arrays.equals(lowerBounds, other.lowerBounds)) {
		  return false;
	  }
	  if (problem == null) {
		  if (other.problem != null) {
			  return false;
		  }
	  } else if (!problem.equals(other.problem)) {
		  return false;
	  }
	  if (size != other.size) {
		  return false;
	  }
	  if (!Arrays.equals(upperBounds, other.upperBounds)) {
		  return false;
	  }
	  return true;
  }
}
