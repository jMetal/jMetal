package org.uma.jmetal.problem.binaryproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem extends AbstractGenericProblem<BinarySolution>
        implements BinaryProblem {

  @Override
  public int getBitsFromVariable(int index) {
    return getListOfBitsPerVariable().get(index);
  }

  @Override
  public int getTotalNumberOfBits() {
    var count = 0;
    var bound = this.getNumberOfVariables();
    for (var i = 0; i < bound; i++) {
      int i1 = this.getListOfBitsPerVariable().get(i);
      count += i1;
    }

    return count;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives(), getNumberOfConstraints());
  }
}
