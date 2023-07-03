package org.uma.jmetal.solution.binarysolution;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.binarySet.BinarySet;

/**
 * Interface representing binary (bitset) solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BinarySolution extends Solution<BinarySet> {
  List<Integer> numberOfBitsPerVariable() ;
  int totalNumberOfBits() ;
}
