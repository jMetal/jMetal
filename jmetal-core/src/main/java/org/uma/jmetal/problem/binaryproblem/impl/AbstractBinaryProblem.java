package org.uma.jmetal.problem.binaryproblem.impl;

import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

import java.util.stream.IntStream;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem implements BinaryProblem {

  @Override
  public int totalNumberOfBits() {
    return IntStream.range(0, this.numberOfVariables())
        .map(i -> this.numberOfBitsPerVariable().get(i))
        .sum();
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(
        numberOfBitsPerVariable(), numberOfObjectives(), numberOfConstraints());
  }
}
