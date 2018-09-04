package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.util.point.PointSolution;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointSolutionTest {
  private static double EPSILON = 0.0000001 ;

  @Test public void shouldDefaultConstructorCreateTheObjectCorrectly() {
    int numberOfObjectives = 10 ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;

    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(solution, "numberOfObjectives"));
    assertNotNull(ReflectionTestUtils.getField(solution, "objectives")) ;
  }

  @Test public void shouldCopyConstructorCreateAnIdenticalObject() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    PointSolution newSolution = new PointSolution(solution) ;

    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(newSolution, "numberOfObjectives"));

    double [] resultValues = (double [])ReflectionTestUtils.getField(solution, "objectives") ;
    assertArrayEquals(values, resultValues, EPSILON) ;
  }

  @Test public void shouldGetObjectiveReturnTheCorrectValue() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    assertEquals(values[0], solution.getObjective(0), EPSILON) ;
    assertEquals(values[1], solution.getObjective(1), EPSILON) ;
    assertEquals(values[2], solution.getObjective(2), EPSILON) ;
  }

  @Test public void shouldSetObjectiveAssignTheTheCorrectValue() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;

    PointSolution solution = new PointSolution(numberOfObjectives) ;
    solution.setObjective(0, values[0]);
    solution.setObjective(1, values[1]);
    solution.setObjective(2, values[2]);

    double [] resultValues = (double [])ReflectionTestUtils.getField(solution, "objectives") ;
    assertArrayEquals(values, resultValues, EPSILON) ;
  }

  @Test public void shouldGetNumberOfObjectivesReturnTheCorrectValue() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    assertEquals(numberOfObjectives, solution.getNumberOfObjectives()) ;
  }

  @Test public void shouldCopyReturnACopyOfTheSolution() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    PointSolution newSolution = (PointSolution)solution.copy() ;
    assertEquals(solution, newSolution) ;
  }

  @Test public void idleTestToCoverTheUnusedMethods() {
    PointSolution solution = new PointSolution(3) ;

    solution.setVariableValue(0, 0.0);
    solution.setAttribute(null, null);

    assertNull(solution.getVariableValue(0)) ;
    assertNull(solution.getVariableValueString(0)) ;
    assertEquals(0, solution.getNumberOfVariables()) ;
    assertNull(solution.getAttribute(null)) ;
  }


  @Test
  public void shouldEqualsReturnTrueIfTheSolutionsAreIdentical() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    PointSolution newSolution = (PointSolution)solution.copy() ;
    assertTrue(solution.equals(newSolution));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheTwoSolutionsHaveDifferentNumberOfObjectives() {
    PointSolution solution1 = new PointSolution(10) ;
    PointSolution solution2 = new PointSolution(4) ;

    assertFalse(solution1.equals(solution2));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheTwoPointsAreTheSame() {
    int numberOfObjectives = 5 ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;

    assertTrue(solution.equals(solution));
  }

  @Test
  public void shouldEqualsReturnFalseIfThePointsAreNotIdentical() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    PointSolution newSolution = (PointSolution)solution.copy() ;
    newSolution.setObjective(0, 23424);

    assertFalse(solution.equals(newSolution));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheSolutionIsNull() {
    int numberOfObjectives = 3 ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;

    assertFalse(solution.equals(null));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void shouldEqualsReturnFalseIfTheClassIsNotAPoint() {
    int numberOfObjectives = 3 ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;

    assertFalse(solution.equals(new String("")));
  }

  @Test
  public void shouldHashCodeReturnTheCorrectValue() {
    int numberOfObjectives = 3 ;
    double [] values = {1.0, 2.0, 3.0} ;
    PointSolution solution = new PointSolution(numberOfObjectives) ;
    ReflectionTestUtils.setField(solution, "objectives", values);

    assertEquals(Arrays.hashCode(values), solution.hashCode());
  }
}
