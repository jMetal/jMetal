package org.uma.jmetal.problem.binaryproblem;

import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

/**
 * Interface representing binary problems
 *
 * @author Antonio J. Nebro
 */
public interface BinaryProblem extends Problem<BinarySolution> {
  List<Integer> numberOfBitsPerVariable() ;
  int totalNumberOfBits() ;
}
