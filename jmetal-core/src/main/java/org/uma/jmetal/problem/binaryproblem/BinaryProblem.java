package org.uma.jmetal.problem.binaryproblem;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

import java.util.List;

/**
 * Interface representing binary problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BinaryProblem extends Problem<BinarySolution> {
  List<Integer> getListOfBitsPerVariable() ;
  int getBitsFromVariable(int index) ;
  int getTotalNumberOfBits() ;
}
