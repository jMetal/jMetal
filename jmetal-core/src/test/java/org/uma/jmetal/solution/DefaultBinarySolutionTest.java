package org.uma.jmetal.solution;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

public class DefaultBinarySolutionTest {

  @Test public void shouldTheSumOfGetNumberOfBitsBeEqualToTheSumOfBitsPerVariable() {
    DefaultBinarySolution solution = new DefaultBinarySolution(Arrays.asList(2, 2, 2),2);

    assertEquals(6, solution.getTotalNumberOfBits());
  }

  @Test public void shouldGetNumberOfBitsBeEqualToTheNumberOfOfBitsPerVariable() {
    DefaultBinarySolution solution = new DefaultBinarySolution(Arrays.asList(2, 2, 2), 3);

    for (int i = 0; i < solution.variables().size(); i++) {
      assertEquals(solution.getNumberOfBits(i), solution.getNumberOfBits(i));
    }
  }

  @Test public void shouldCopyReturnAnIdenticalVariable() {
    BinarySolution expectedSolution = new DefaultBinarySolution(Arrays.asList(2, 2, 2), 3);
    BinarySolution newSolution = (BinarySolution) expectedSolution.copy();

    assertEquals(expectedSolution, newSolution);
  }

  @Test public void shouldTheHashCodeOfTwoIdenticalSolutionsBeTheSame() {
    BinarySolution solutionA = new DefaultBinarySolution(List.of(5), 2);
    BinarySolution solutionB = new DefaultBinarySolution(List.of(5),2);

    solutionA.variables().get(0).set(0) ;
    solutionA.variables().get(0).clear(1) ;
    solutionA.variables().get(0).set(2) ;
    solutionA.variables().get(0).clear(3) ;
    solutionA.variables().get(0).set(4) ;

    solutionB.variables().get(0).set(0) ;
    solutionB.variables().get(0).clear(1) ;
    solutionB.variables().get(0).set(2) ;
    solutionB.variables().get(0).clear(3) ;
    solutionB.variables().get(0).set(4) ;

    assertEquals(solutionA.hashCode(), solutionB.hashCode());
  }


  @Test public void shouldGetVariableValueStringReturnARightStringRepresentation() {
    BinarySolution solution = new DefaultBinarySolution(Arrays.asList(5), 2);
    solution.variables().get(0).set(0, 5) ;

    assertEquals("11111", solution.variables().get(0).toString()) ;

    solution.variables().get(0).clear(2) ;
    assertEquals("11011", solution.variables().get(0).toString()) ;
  }
}
