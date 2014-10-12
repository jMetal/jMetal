package org.uma.jmetal.problem.impl;

import org.uma.jmetal.encoding.BinarySolution;
import org.uma.jmetal.problem.BinaryProblem;

public abstract class BinaryProblemImpl extends GenericProblemImpl<BinarySolution>
  implements BinaryProblem {

  protected int [] bitsPerVariable ;

  public int getNumberOfBits(int index) {
    return bitsPerVariable[index] ;
  }

}
