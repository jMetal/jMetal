package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;

/** Interface representing binary problems */
public interface BinaryProblem<S extends Solution<?>> extends Problem<S> {
  public int getNumberOfBits(int index) ;
}
