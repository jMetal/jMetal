package org.uma.jmetal.problem;

import org.uma.jmetal.solution.BinarySolution;

/**
 * Interface representing binary problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BinaryProblem extends Problem<BinarySolution> {
  public int getNumberOfBits(int index) ;
  public int getTotalNumberOfBits() ;
}
