package org.uma.jmetal.problem;

import org.uma.jmetal.solution.BinarySolution;

/** Interface representing binary problems */
public interface BinaryProblem extends Problem<BinarySolution> {
  public int getNumberOfBits(int index) ;
  public int getTotalNumberOfBits() ;
}
