//  Binary.java
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
import jmetal.util.random.PseudoRandom;

import java.util.BitSet;

/**
 * This class implements a generic binary string encodings.variable.It can be used as
 * a base class other binary string based classes (e.g., binary coded integer
 * or real variables).
 */
public class Binary extends Variable {
  private static final long serialVersionUID = -3553927010701150789L;

  protected int numberOfBits_;
  private BitSet bits_;

  public Binary() {
  }

  public Binary(int numberOfBits) {
    numberOfBits_ = numberOfBits;

    bits_ = new BitSet(numberOfBits_);
    for (int i = 0; i < numberOfBits_; i++) {
      if (PseudoRandom.randDouble() < 0.5) {
        bits_.set(i, true);
      } else {
        bits_.set(i, false);
      }
    }
  }

  public Binary(Binary variable) {
    numberOfBits_ = variable.numberOfBits_;

    bits_ = new BitSet(numberOfBits_);
    for (int i = 0; i < numberOfBits_; i++) {
      bits_.set(i, variable.bits_.get(i));
    }
  }

  public BitSet getBits() {
    return bits_;
  }

  public void setBits(BitSet bits) {
    bits_ = bits;
  }

  public Variable deepCopy() {
    return new Binary(this);
  }

  public int getNumberOfBits() {
    return numberOfBits_;
  }

  public boolean getIth(int bit) {
    return bits_.get(bit);
  }

  public void setIth(int bit, boolean value) {
    bits_.set(bit, value);
  }

  public int hammingDistance(Binary other) {
    int distance = 0;
    int i = 0;
    while (i < bits_.size()) {
      if (bits_.get(i) != other.bits_.get(i)) {
        distance++;
      }
      i++;
    }
    return distance;
  }

  public String toString() {
    String result;

    result = "";
    for (int i = 0; i < numberOfBits_; i++) {
      if (bits_.get(i)) {
        result = result + "1";
      } else {
        result = result + "0";
      }
    }

    return result;
  }
}
