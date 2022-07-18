package org.uma.jmetal.util.binarySet;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

/**
 * Class representing a bit set including a method to get the total number of bits
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class BinarySet extends BitSet {
  private final int numberOfBits;

  /**
   * Constructor
   *
   * @param numberOfBits Number of bits of the binary string
   */
  public BinarySet(int numberOfBits) {
    super(numberOfBits);
    this.numberOfBits = numberOfBits;
  }

  /**
   * Returns the total number of bits
   *
   * @return The number of bits of the binary set
   */
  public int getBinarySetLength() {
    return numberOfBits;
  }

  @Override
  public @NotNull String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < numberOfBits; i++) {
      if (get(i)) {
        result.append("1");
      } else {
        result.append("0");
      }
    }
    return result.toString();
  }
}
