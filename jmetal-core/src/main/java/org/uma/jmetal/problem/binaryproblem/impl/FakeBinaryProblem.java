package org.uma.jmetal.problem.binaryproblem.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

/**
 * Fake implementation of {@link BinaryProblem} that does nothing. Intended to be used in unit tests
 */

public class FakeBinaryProblem extends AbstractBinaryProblem {

  private int[] bitsPerVariable;
  private int numberOfVariables;

  /**
   * Constructor
   */
  public FakeBinaryProblem(Integer numberOfVariables, int numberOfBitsPerVariable) {
    this.numberOfVariables = numberOfVariables;
    bitsPerVariable = new int[numberOfVariables];

    for (int var = 0; var < numberOfVariables; var++) {
      bitsPerVariable[var] = numberOfBitsPerVariable;
    }
  }

  @Override
  public int numberOfVariables() {
    return numberOfVariables ;
  }

 @Override
 public int numberOfObjectives() {
    return 2 ;
 }

  @Override
  public int numberOfConstraints() {
    return 0;
  }

  @Override
  public String name() {
    return "Fake binary problem";
  }

  @Override
  public int bitsFromVariable(int index) {
    return bitsPerVariable[index];
  }

  @Override
  public List<Integer> listOfBitsPerVariable() {
    return Arrays.stream(bitsPerVariable).boxed().collect(Collectors.toList());
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(listOfBitsPerVariable(), numberOfObjectives());
  }

  /**
   * Evaluate() method
   */
  @Override
  public BinarySolution evaluate(BinarySolution solution) {
    solution.objectives()[0] = 0;
    solution.objectives()[1] = 1;

    return solution;
  }
}
