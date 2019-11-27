package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import static org.junit.Assert.*;

import org.uma.jmetal.util.point.PointSolution;
/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointSolutionTest {
  @Test
  public void shouldDefaultConstructorCreateTheObjectCorrectly() {
    int numberOfObjectives = 10;
    PointSolution solution = new PointSolution(numberOfObjectives);
    assertEquals(numberOfObjectives, solution.getNumberOfObjectives());
    assertEquals(0, solution.getNumberOfConstraints());
    for (int i = 0; i < numberOfObjectives; ++i) {
      assertEquals(0.0, solution.getObjective(i), 0);
    }
    assertEquals(numberOfObjectives, solution.getObjectives().length);
    assertEquals(0, solution.getConstraints().length);
    assertArrayEquals(new double[numberOfObjectives], solution.getObjectives(), 0);
    assertArrayEquals(new double[0], solution.getConstraints(), 0);
  }

  @Test
  public void shouldCopyConstructorCreateAnIdenticalObject() {
    PointSolution solution = new PointSolution(3, 2);
    solution.setObjective(0, 1);
    solution.setObjective(1, 2);
    solution.setObjective(2, 3);
    solution.setConstraint(0, 4);
    solution.setConstraint(1, 5);

    PointSolution newSolution = new PointSolution(solution) ;
    assertEquals(solution.getNumberOfObjectives(), newSolution.getNumberOfObjectives());
    assertEquals(solution.getNumberOfConstraints(), newSolution.getNumberOfConstraints());

    assertArrayEquals(solution.getObjectives(), newSolution.getObjectives(), 0);
    assertArrayEquals(solution.getConstraints(), newSolution.getConstraints(), 0);
    assertNotSame(solution.getObjectives(), newSolution.getObjectives());
    assertNotSame(solution.getConstraints(), newSolution.getConstraints());
  }

  @Test
  public void shouldGetObjectiveReturnTheCorrectValue() {
    PointSolution solution = new PointSolution(3, 2);
    solution.setObjective(0, 1);
    solution.setObjective(1, 2);
    solution.setObjective(2, 3);
    solution.setConstraint(0, 4);
    solution.setConstraint(1, 5);
    assertEquals(1.0, solution.getObjective(0), 0);
    assertEquals(2.0, solution.getObjective(1), 0);
    assertEquals(3.0, solution.getObjective(2), 0);
    assertEquals(4.0, solution.getConstraint(0), 0);
    assertEquals(5.0, solution.getConstraint(1), 0);
  }

  @Test
  public void shouldCopyReturnACopyOfTheSolution() {
    PointSolution solution = new PointSolution(3);
    solution.setObjective(0, 1);
    solution.setObjective(1, 2);
    solution.setObjective(2, 3);

    PointSolution newSolution = solution.copy();
    assertEquals(solution, newSolution);
    assertNotSame(solution, newSolution);
  }

  @Test
  public void testVariablesAreGracefullyUnsupported() {
    PointSolution solution = new PointSolution(3) ;

    solution.setVariable(0, 0.0);
    solution.setAttribute(null, null);

    assertNull(solution.getVariable(0)) ;
    assertEquals(0, solution.getNumberOfVariables()) ;
    assertNull(solution.getAttribute(null)) ;
  }


  @Test
  public void shouldEqualsReturnTrueIfTheSolutionsAreIdentical() {
    PointSolution solutionA = new PointSolution(3);
    solutionA.setObjective(0, 1);
    solutionA.setObjective(1, 2);
    solutionA.setObjective(2, 3);

    PointSolution solutionB = new PointSolution(3);
    solutionB.setObjective(0, 1);
    solutionB.setObjective(1, 2);
    solutionB.setObjective(2, 3);

    assertEquals(solutionA, solutionB);
  }

  @Test
  public void shouldEqualsReturnFalseIfTheTwoSolutionsHaveDifferentNumberOfObjectives() {
    PointSolution solution1 = new PointSolution(10);
    PointSolution solution2 = new PointSolution(4);
    assertNotEquals(solution1, solution2);
  }

  @Test
  public void shouldEqualsReturnTrueIfTheTwoPointsAreTheSame() {
    PointSolution solution = new PointSolution(5);
    assertEquals(solution, solution);
  }

  @Test
  public void shouldEqualsReturnFalseIfThePointsAreNotIdentical() {
    PointSolution solution = new PointSolution(3);
    solution.setObjective(0, 1);
    solution.setObjective(1, 2);
    solution.setObjective(2, 3);

    PointSolution newSolution = solution.copy();
    newSolution.setObjective(0, 23424);

    assertNotEquals(solution, newSolution);
  }

  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions"})
  @Test
  public void shouldEqualsReturnFalseIfTheSolutionIsNull() {
    PointSolution solution = new PointSolution(3) ;
    assertFalse(solution.equals(null));
  }

  @SuppressWarnings({"unlikely-arg-type", "SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes"})
  @Test
  public void shouldEqualsReturnFalseIfTheClassIsNotAPoint() {
    PointSolution solution = new PointSolution(3);
    assertFalse(solution.equals(""));
  }

  @Test
  public void shouldGetAttributesReturnAnNoAttributesWhenInitiateAnPointSolution() {
	PointSolution solution = new PointSolution(3);
	assertTrue(solution.getAttributes().isEmpty());
  }

  @Test
  public void shouldReturnTheCorrectAttributesWhenGetAllAttributes() {
	PointSolution solution = new PointSolution(3);

	solution.setAttribute("fake-attribute-1", 1);
	solution.setAttribute("fake-attribute-2", 2);

	assertFalse(solution.getAttributes().isEmpty());
	assertEquals((int) solution.getAttributes().get("fake-attribute-1"), 1);
	assertEquals((int) solution.getAttributes().get("fake-attribute-2"), 2);
  }
}
