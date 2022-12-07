package org.uma.jmetal.problem.binaryproblem;

import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

/**
 * Interface representing binary problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BinaryProblem extends Problem<BinarySolution> {
  List<Integer> listOfBitsPerVariable() ;
  int bitsFromVariable(int index) ;
  int totalNumberOfBits() ;
}
