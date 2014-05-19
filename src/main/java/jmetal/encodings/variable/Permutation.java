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

package jmetal.encodings.variable;

import jmetal.core.Variable;

/**
 * Class implementing a permutation of integer decision encodings.variable
 */
public class Permutation extends Variable {

  /**
   *
   */
  private static final long serialVersionUID = 2657470069340722041L;

  /**
   * Stores a permutation of <code>int</code> values
   */
  private int [] vector_;

  public int[] getVector() {
    return vector_ ;
  }


  /**
   * Stores the length of the permutation
   */
  private int size_;

  public int getSize() {
    return size_ ;
  }

  /**
   * Constructor
   */
  public Permutation() {
    size_   = 0;
    vector_ = null;
  }

  /**
   * Constructor
   * @param size Length of the permutation
   * This constructor has been contributed by Madan Sathe
   */
  public Permutation(int size) {
    size_   = size;
    vector_ = new int[size_];

    java.util.ArrayList<Integer> randomSequence = new
            java.util.ArrayList<Integer>(size_);

    for (int i = 0; i < size_; i++) {
      randomSequence.add(i);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int j = 0; j < randomSequence.size(); j++) {
      vector_[j] = randomSequence.get(j);
    }
  }

  /**
   * Copy Constructor
   * @param permutation The permutation to copy
   */
  public Permutation(Permutation permutation) {
    size_   = permutation.size_;
    vector_ = new int[size_];

    System.arraycopy(permutation.vector_, 0, vector_, 0, size_);
  }

  /**
   * Create an exact copy of the <code>Permutation</code> object.
   * @return An exact copy of the object.
   */
  public Variable deepCopy() {
    return new Permutation(this);
  } //deepCopy

  /**
   * Returns the length of the permutation.
   * @return The length
   */
  public int getLength(){
    return size_;
  } //getNumberOfBits

  /**
   * Returns a string representing the object
   * @return The string
   */
  public String toString(){
    String string ;

    string = "" ;
    for (int i = 0; i < size_ ; i ++) {
      string += vector_[i] + " ";
    }

    return string ;
  }
}
