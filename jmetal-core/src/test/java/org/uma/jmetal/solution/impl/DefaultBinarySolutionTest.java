package org.uma.jmetal.solution.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;

import static org.junit.Assert.assertEquals;

public class DefaultBinarySolutionTest {
  private static final int NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM = 5 ;
  BinaryProblem problem ;

  @Before public void setUp(){
    problem = new MockBinaryProblem();
  }

  @After public void tearDown(){
    problem = null ;
  }

  @Test public void shouldTheSumOfGetNumberOfBitsBeEqualToTheSumOfBitsPerVariable() {
    DefaultBinarySolution solution = (DefaultBinarySolution) problem.createSolution();

    assertEquals(problem.getTotalNumberOfBits(), solution.getTotalNumberOfBits());
  }

  @Test public void shouldGetNumberOfBitsBeEqualToTheNumberOfOfBitsPerVariable() {
    DefaultBinarySolution solution = (DefaultBinarySolution) problem.createSolution();

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      assertEquals(problem.getNumberOfBits(i), solution.getNumberOfBits(i));
    }
  }

  @Test public void shouldCopyReturnAnIdenticalVariable() {
    BinarySolution expectedSolution = problem.createSolution();
    BinarySolution newSolution = (BinarySolution) expectedSolution.copy();

    assertEquals(expectedSolution, newSolution);
  }

  @Test public void shouldTheHashCodeOfTwoIdenticalSolutionsBeTheSame() {
    BinaryProblem problem = new MockBinaryProblem(1) ;
    BinarySolution solutionA = problem.createSolution();
    BinarySolution solutionB = problem.createSolution();

    solutionA.getVariableValue(0).set(0) ;
    solutionA.getVariableValue(0).clear(1) ;
    solutionA.getVariableValue(0).set(2) ;
    solutionA.getVariableValue(0).clear(3) ;
    solutionA.getVariableValue(0).set(4) ;

    solutionB.getVariableValue(0).set(0) ;
    solutionB.getVariableValue(0).clear(1) ;
    solutionB.getVariableValue(0).set(2) ;
    solutionB.getVariableValue(0).clear(3) ;
    solutionB.getVariableValue(0).set(4) ;

    assertEquals(solutionA.hashCode(), solutionB.hashCode());
  }

  @Test public void shouldGetTotalNumberOfBitsBeEqualToTheSumOfBitsPerVariable() {
    assertEquals(NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM*problem.getNumberOfVariables(),
        problem.getTotalNumberOfBits());
  }

  @Test public void shouldGetVariableValueStringReturnARightStringRepresentation() throws Exception {
    BinarySolution solution = problem.createSolution();
    solution.getVariableValue(0).set(0, NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM) ;

    assertEquals("11111", solution.getVariableValueString(0)) ;

    solution.getVariableValue(0).clear(2) ;
    assertEquals("11011", solution.getVariableValueString(0)) ;
  }

  /**
   * Mock class representing a binary problem
   */
  @SuppressWarnings("serial")
  private class MockBinaryProblem extends AbstractBinaryProblem {
    private int[] bitsPerVariable ;
    /** Constructor */
    public MockBinaryProblem() {
      this(3);
    }

    /** Constructor */
    public MockBinaryProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      bitsPerVariable = new int[numberOfVariables] ;

      for (int var = 0; var < numberOfVariables; var++) {
        bitsPerVariable[var] = NUMBER_OF_BITS_OF_MOCKED_BINARY_PROBLEM;
      }
    }

    @Override
    protected int getBitsPerVariable(int index) {
      return bitsPerVariable[index] ;
    }

    @Override
    public BinarySolution createSolution() {
      return new DefaultBinarySolution(this) ;
    }

    /** Evaluate() method */
    @Override
    public void evaluate(BinarySolution solution) {
      solution.setObjective(0, 0);
      solution.setObjective(1, 1);
    }
  }
}
