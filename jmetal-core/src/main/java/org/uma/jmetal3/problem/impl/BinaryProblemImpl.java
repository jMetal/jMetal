package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.problem.BinaryProblem;

public abstract class BinaryProblemImpl extends GenericProblemImpl<BinarySolution>
  implements BinaryProblem {

  protected int [] bitsPerVariable ;

  public int getNumberOfBits(int index) {
    return bitsPerVariable[index] ;
  }

}
