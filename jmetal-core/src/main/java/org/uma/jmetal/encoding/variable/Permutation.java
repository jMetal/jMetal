//  Permutation.java
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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class implementing a permutation of integer decision encoding.variable
 */
public class Permutation implements Variable {
  private static final long serialVersionUID = 2657470069340722041L;

  private int[] vector;

  private int size;

  public Permutation() {
    size = 0;
    vector = null;
  }

  public Permutation(int size) {
    this.size = size;
    vector = new int[this.size];

    ArrayList<Integer> randomSequence = new ArrayList<>(this.size);

    for (int i = 0; i < this.size; i++) {
      randomSequence.add(i);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int j = 0; j < randomSequence.size(); j++) {
      vector[j] = randomSequence.get(j);
    }
  }

  public Permutation(Permutation permutation) {
    size = permutation.size;
    vector = new int[size];

    System.arraycopy(permutation.vector, 0, vector, 0, size);
  }

  public int[] getVector() {
    return vector;
  }

  public int getSize() {
    return size;
  }


  public Variable copy() {
    return new Permutation(this);
  }

  public int getLength() {
    return size;
  }

  public String toString() {
    String string;

    string = "";
    for (int i = 0; i < size; i++) {
      string += vector[i] + " ";
    }

    return string;
  }

	@Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + size;
	  result = prime * result + Arrays.hashCode(vector);
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
	  if (!(obj instanceof Permutation)) {
		  return false;
	  }
	  Permutation other = (Permutation) obj;
	  if (size != other.size) {
		  return false;
	  }
	  if (!Arrays.equals(vector, other.vector)) {
		  return false;
	  }
	  return true;
  }
}
