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

package org.uma.jmetal.encoding.variable;

import org.uma.jmetal.core.Variable;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.BitSet;

/**
 * This class implements a generic binary string encoding.variable.It can be used as
 * a base class other binary string based classes (e.g., binary coded integer
 * or real variables).
 */
public class Binary implements Variable {
  private static final long serialVersionUID = -3553927010701150789L;

  protected int numberOfBits;
  private BitSet bits;

  public Binary() {
  }

  public Binary(int numberOfBits) {
    this.numberOfBits = numberOfBits;

    bits = new BitSet(this.numberOfBits);
    for (int i = 0; i < this.numberOfBits; i++) {
      if (PseudoRandom.randDouble() < 0.5) {
        bits.set(i, true);
      } else {
        bits.set(i, false);
      }
    }
  }

  public Binary(Binary variable) {
    numberOfBits = variable.numberOfBits;

    bits = new BitSet(numberOfBits);
    for (int i = 0; i < numberOfBits; i++) {
      bits.set(i, variable.bits.get(i));
    }
  }

  public BitSet getBits() {
    return bits;
  }

  public void setBits(BitSet bits) {
    this.bits = bits;
  }

  public Variable copy() {
    return new Binary(this);
  }

  public int getNumberOfBits() {
    return numberOfBits;
  }

  public boolean getIth(int bit) {
    return bits.get(bit);
  }

  public void setIth(int bit, boolean value) {
    bits.set(bit, value);
  }

  public int hammingDistance(Binary other) {
    int distance = 0;
    int i = 0;
    while (i < bits.size()) {
      if (bits.get(i) != other.bits.get(i)) {
        distance++;
      }
      i++;
    }
    return distance;
  }

  public String toString() {
    String result;

    result = "";
    for (int i = 0; i < numberOfBits; i++) {
      if (bits.get(i)) {
        result = result + "1";
      } else {
        result = result + "0";
      }
    }

    return result;
  }

	@Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + ((bits == null) ? 0 : bits.hashCode());
	  result = prime * result + numberOfBits;
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
	  if (!(obj instanceof Binary)) {
		  return false;
	  }
	  
	  Binary other = (Binary) obj;
	  if (bits == null) {
		  if (other.bits != null) {
			  return false;
		  }
	  } else if (!bits.equals(other.bits)) {
		  return false;
	  }
	  if (numberOfBits != other.numberOfBits) {
		  return false;
	  }
	  return true;
  }
}
