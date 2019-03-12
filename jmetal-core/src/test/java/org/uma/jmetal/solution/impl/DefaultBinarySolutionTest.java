package org.uma.jmetal.solution.impl;

import org.junit.Test;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DefaultBinarySolutionTest {

  @Test public void shouldTheSumOfGetNumberOfBitsBeEqualToTheSumOfBitsPerVariable() {
    DefaultBinarySolution solution = new DefaultBinarySolution(Arrays.asList(2, 2, 2),2);

    assertEquals(6, solution.getTotalNumberOfBits());
  }

  @Test public void shouldGetNumberOfBitsBeEqualToTheNumberOfOfBitsPerVariable() {
    DefaultBinarySolution solution = new DefaultBinarySolution(Arrays.asList(2, 2, 2), 3);

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      assertEquals(solution.getNumberOfBits(i), solution.getNumberOfBits(i));
    }
  }

  @Test public void shouldCopyReturnAnIdenticalVariable() {
    BinarySolution expectedSolution = new DefaultBinarySolution(Arrays.asList(2, 2, 2), 3);
    BinarySolution newSolution = (BinarySolution) expectedSolution.copy();

    assertEquals(expectedSolution, newSolution);
  }

  @Test public void shouldTheHashCodeOfTwoIdenticalSolutionsBeTheSame() {
    BinarySolution solutionA = new DefaultBinarySolution(Arrays.asList(5), 2);
    BinarySolution solutionB = new DefaultBinarySolution(Arrays.asList(5),2);

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


  @Test public void shouldGetVariableValueStringReturnARightStringRepresentation() {
    BinarySolution solution = new DefaultBinarySolution(Arrays.asList(5), 2);
    solution.getVariableValue(0).set(0, 5) ;

    assertEquals("11111", solution.getVariableValueString(0)) ;

    solution.getVariableValue(0).clear(2) ;
    assertEquals("11011", solution.getVariableValueString(0)) ;
  }
}
