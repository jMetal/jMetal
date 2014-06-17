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

/**
 * Class implementing a permutation of integer decision encoding.variable
 */
public class Permutation extends Variable {
  private static final long serialVersionUID = 2657470069340722041L;

  private int[] vector_;

  private int size_;

  public Permutation() {
    size_ = 0;
    vector_ = null;
  }

  public Permutation(int size) {
    size_ = size;
    vector_ = new int[size_];

    ArrayList<Integer> randomSequence = new ArrayList<>(size_);

    for (int i = 0; i < size_; i++) {
      randomSequence.add(i);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int j = 0; j < randomSequence.size(); j++) {
      vector_[j] = randomSequence.get(j);
    }
  }

  public Permutation(Permutation permutation) {
    size_ = permutation.size_;
    vector_ = new int[size_];

    System.arraycopy(permutation.vector_, 0, vector_, 0, size_);
  }

  public int[] getVector() {
    return vector_;
  }

  public int getSize() {
    return size_;
  }


  public Variable deepCopy() {
    return new Permutation(this);
  }

  public int getLength() {
    return size_;
  }

  public String toString() {
    String string;

    string = "";
    for (int i = 0; i < size_; i++) {
      string += vector_[i] + " ";
    }

    return string;
  }
}
