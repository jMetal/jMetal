//  BinaryReal.java
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
import org.uma.jmetal.util.JMetalException;

import java.util.BitSet;

/**
 * This class extends the Binary class to represent a Real encoding.variable encoded by
 * a binary string
 */
public class BinaryReal extends Binary {
  private static final long serialVersionUID = 7465120044544948197L;

  public static final int DEFAULT_PRECISION = 30;
  
  private double value;
  private double lowerBound;
  private double upperBound;

  public BinaryReal() {
    super();
  }

  public BinaryReal(int numberOfBits, double lowerBound, double upperBound) {
    super(numberOfBits);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;

    decode();
  }

  public BinaryReal(BitSet bits, int nbBits, double lowerBound, double upperBound) {
    super(nbBits);
    setBits(bits);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    
    decode();
  }

  public BinaryReal(BinaryReal variable) {
    super(variable);

    lowerBound = variable.lowerBound;
    upperBound = variable.upperBound;

    value = variable.value;
  }

  public void decode() {
    double value = 0.0;
    for (int i = 0; i < numberOfBits; i++) {
      if (getBits().get(i)) {
        value += Math.pow(2.0, i);
      }
    }

    this.value = value * (upperBound - lowerBound) /
      (Math.pow(2.0, numberOfBits) - 1.0);
    this.value += lowerBound;
  }

  public double getValue() {
    return value;
  }

  @Override
  public void setValue(double value) throws JMetalException {
    if (numberOfBits <= 24 && lowerBound >= 0) {
      BitSet bitSet;
      if (value <= lowerBound) {
        bitSet = new BitSet(numberOfBits);
        bitSet.clear();
      } else if (value >= upperBound) {
        bitSet = new BitSet(numberOfBits);
        bitSet.set(0, numberOfBits);
      } else {
        bitSet = new BitSet(numberOfBits);
        bitSet.clear();
        // value is the integerToCode-th possible value, what is integerToCode?
        int integerToCode = 0;
        double tmp = lowerBound;
        double path = (upperBound - lowerBound) / (Math.pow(2.0, numberOfBits) - 1);
        while (tmp < value) {
          tmp += path;
          integerToCode++;
        }
        int remain = integerToCode;
        for (int i = numberOfBits - 1; i >= 0; i--) {
          int ithPowerOf2 = (int) Math.pow(2, i);

          if (ithPowerOf2 <= remain) {
            bitSet.set(i);
            remain -= ithPowerOf2;
          } else {
            bitSet.clear(i);
          }
        }
      }
      setBits(bitSet);
      this.decode();

    } else {
      if (lowerBound < 0) {
        throw new JMetalException("Unsupported lowerbound: " + lowerBound + " > 0");
      }
      if (numberOfBits >= 24) {
        throw new JMetalException("Unsupported bit string length"
          + numberOfBits + " is > 24 bits");
      }
    }
  }

  public Variable deepCopy() {
    return new BinaryReal(this);
  }

  public double getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(double lowerBound) {
    this.lowerBound = lowerBound;
  }

  public double getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(double upperBound) {
    this.upperBound = upperBound;
  }

  @Override
  public String toString() {
    return value + "";
  }

	@Override
  public int hashCode() {
	  final int prime = 31;
	  int result = super.hashCode();
	  long temp;
	  temp = Double.doubleToLongBits(lowerBound);
	  result = prime * result + (int) (temp ^ (temp >>> 32));
	  temp = Double.doubleToLongBits(upperBound);
	  result = prime * result + (int) (temp ^ (temp >>> 32));
	  temp = Double.doubleToLongBits(value);
	  result = prime * result + (int) (temp ^ (temp >>> 32));
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
	  if (!(obj instanceof BinaryReal)) {
		  return false;
	  }
	  BinaryReal other = (BinaryReal) obj;
	  if (lowerBound != other.lowerBound) {
		  return false;
	  }
	  if (upperBound != other.upperBound) {
		  return false;
	  }
	  if (value != other.value) {
		  return false;
	  }
	  return true;
	}
}
