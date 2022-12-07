package org.uma.jmetal.problem.binaryproblem.impl;

import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem implements BinaryProblem {

  @Override
  public int bitsFromVariable(int index) {
    return listOfBitsPerVariable().get(index);
  }

  @Override
  public int totalNumberOfBits() {
    int count = 0;
    for (int i = 0; i < this.numberOfVariables(); i++) {
      count += this.listOfBitsPerVariable().get(i);
    }

    return count;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(listOfBitsPerVariable(), numberOfObjectives(), numberOfConstraints());
  }
}
