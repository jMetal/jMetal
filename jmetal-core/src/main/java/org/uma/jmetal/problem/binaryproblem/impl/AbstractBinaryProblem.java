package org.uma.jmetal.problem.binaryproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

import java.util.stream.IntStream;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem extends AbstractGenericProblem<BinarySolution>
        implements BinaryProblem {

  @Override
  public int getBitsFromVariable(int index) {
    return getListOfBitsPerVariable().get(index);
  }

  @Override
  public int getTotalNumberOfBits() {
    int count = IntStream.range(0, this.getNumberOfVariables()).map(i -> this.getListOfBitsPerVariable().get(i)).sum();

    return count;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives(), getNumberOfConstraints());
  }
}
