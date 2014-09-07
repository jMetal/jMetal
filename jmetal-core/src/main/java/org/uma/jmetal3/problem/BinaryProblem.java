package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;

/** Interface representing binary problems */
public interface BinaryProblem extends Problem {
  public int getNumberOfBits(int index) ;
}
