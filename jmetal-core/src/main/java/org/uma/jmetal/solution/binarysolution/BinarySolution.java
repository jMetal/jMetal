package org.uma.jmetal.solution.binarysolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.binarySet.BinarySet;

/**
 * Interface representing binary (bitset) solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BinarySolution extends Solution<BinarySet> {
  int numberOfBitsPerVariable(int index) ;
  int totalNumberOfBits() ;
}
