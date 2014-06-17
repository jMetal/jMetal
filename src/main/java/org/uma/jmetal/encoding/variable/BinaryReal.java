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
  private double value_;
  private double lowerBound_;
  private double upperBound_;

  public BinaryReal() {
    super();
  }

  public BinaryReal(int numberOfBits, double lowerBound, double upperBound) {
    super(numberOfBits);
    lowerBound_ = lowerBound;
    upperBound_ = upperBound;

    decode();
  }

  public BinaryReal(BitSet bits, int nbBits, double lowerBound, double upperBound) {
    super(nbBits);
    setBits(bits);
    lowerBound_ = lowerBound;
    upperBound_ = upperBound;
    
    decode();
  }

  public BinaryReal(BinaryReal variable) {
    super(variable);

    lowerBound_ = variable.lowerBound_;
    upperBound_ = variable.upperBound_;

    value_ = variable.value_;
  }

  public void decode() {
    double value = 0.0;
    for (int i = 0; i < numberOfBits_; i++) {
      if (getBits().get(i)) {
        value += Math.pow(2.0, i);
      }
    }

    value_ = value * (upperBound_ - lowerBound_) /
      (Math.pow(2.0, numberOfBits_) - 1.0);
    value_ += lowerBound_;
  }

  public double getValue() {
    return value_;
  }

  @Override
  public void setValue(double value) throws JMetalException {
    if (numberOfBits_ <= 24 && lowerBound_ >= 0) {
      BitSet bitSet;
      if (value <= lowerBound_) {
        bitSet = new BitSet(numberOfBits_);
        bitSet.clear();
      } else if (value >= upperBound_) {
        bitSet = new BitSet(numberOfBits_);
        bitSet.set(0, numberOfBits_);
      } else {
        bitSet = new BitSet(numberOfBits_);
        bitSet.clear();
        // value is the integerToCode-th possible value, what is integerToCode?
        int integerToCode = 0;
        double tmp = lowerBound_;
        double path = (upperBound_ - lowerBound_) / (Math.pow(2.0, numberOfBits_) - 1);
        while (tmp < value) {
          tmp += path;
          integerToCode++;
        }
        int remain = integerToCode;
        for (int i = numberOfBits_ - 1; i >= 0; i--) {
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
      if (lowerBound_ < 0) {
        throw new JMetalException("Unsupported lowerbound: " + lowerBound_ + " > 0");
      }
      if (numberOfBits_ >= 24) {
        throw new JMetalException("Unsupported bit string length"
          + numberOfBits_ + " is > 24 bits");
      }
    }
  }

  public Variable deepCopy() {
    return new BinaryReal(this);
  }

  public double getLowerBound() {
    return lowerBound_;
  }

  public void setLowerBound(double lowerBound) {
    lowerBound_ = lowerBound;
  }

  public double getUpperBound() {
    return upperBound_;
  }

  public void setUpperBound(double upperBound) {
    upperBound_ = upperBound;
  }

  @Override
  public String toString() {
    return value_ + "";
  }
}
