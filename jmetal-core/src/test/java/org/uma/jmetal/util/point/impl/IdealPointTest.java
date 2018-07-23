package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.point.impl.IdealPoint;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ajnebro on 12/2/16.
 */
public class IdealPointTest {
  private static final double EPSILON = 0.00000000001 ;
  private static final double DEFAULT_INITIAL_VALUE = Double.POSITIVE_INFINITY ;

  private IdealPoint referencePoint ;

  @Test
  public void shouldConstructorCreateAnIdealPointWithAllObjectiveValuesCorrectlyInitialized() {
    int numberOfObjectives = 4 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    assertEquals(numberOfObjectives, referencePoint.getDimension()) ;

    for (int i = 0 ; i < numberOfObjectives; i++) {
      assertEquals(DEFAULT_INITIAL_VALUE, referencePoint.getValue(i), EPSILON) ;
    }
  }

  @Test
  public void shouldUpdateWithOneSolutionMakeTheIdealPointHaveTheSolutionValues() {
    int numberOfObjectives = 2 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    //when(solution.getObjective(0)).thenReturn(1.0) ;
    //when(solution.getObjective(1)).thenReturn(2.0) ;
    when(solution.getObjectives()).thenReturn(new double[]{1.0, 2.0}) ;

    referencePoint.update(solution.getObjectives());

    assertEquals(1.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(2.0, referencePoint.getValue(1), EPSILON) ;
    assertArrayEquals(new double[]{1.0, 2.0}, solution.getObjectives(), EPSILON);
  }

  @Test
  public void shouldUpdateWithTwoSolutionsLeadToTheCorrectIdealPoint() {
    int numberOfObjectives = 2 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution1.getObjective(0)).thenReturn(0.0) ;
    when(solution1.getObjective(1)).thenReturn(1.0) ;
    when(solution1.getObjectives()).thenReturn(new double[]{0.0, 1.0}) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution2.getObjective(0)).thenReturn(1.0) ;
    when(solution2.getObjective(1)).thenReturn(0.0) ;
    when(solution2.getObjectives()).thenReturn(new double[]{1.0, 0.0}) ;

    referencePoint.update(solution1.getObjectives());
    referencePoint.update(solution2.getObjectives());

    assertEquals(0.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(0.0, referencePoint.getValue(1), EPSILON) ;
  }

  @Test
  public void shouldUpdateWithThreeSolutionsLeadToTheCorrectIdealPoint() {
    int numberOfObjectives = 3 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution1.getObjective(0)).thenReturn(3.0) ;
    when(solution1.getObjective(1)).thenReturn(1.0) ;
    when(solution1.getObjective(2)).thenReturn(2.0) ;
    when(solution1.getObjectives()).thenReturn(new double[]{3.0, 1.0, 2.0}) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution2.getObjective(0)).thenReturn(0.2) ;
    when(solution2.getObjective(1)).thenReturn(4.0) ;
    when(solution2.getObjective(2)).thenReturn(5.5) ;
    when(solution2.getObjectives()).thenReturn(new double[]{0.2, 4.0, 5.5}) ;

    IntegerSolution solution3 = mock(IntegerSolution.class) ;
    when(solution3.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution3.getObjective(0)).thenReturn(5.0) ;
    when(solution3.getObjective(1)).thenReturn(6.0) ;
    when(solution3.getObjective(2)).thenReturn(1.5) ;
    when(solution3.getObjectives()).thenReturn(new double[]{5.0, 6.0, 1.5}) ;

    referencePoint.update(solution1.getObjectives());
    referencePoint.update(solution2.getObjectives());
    referencePoint.update(solution3.getObjectives());


    assertEquals(0.2, referencePoint.getValue(0), EPSILON) ;
    assertEquals(1.0, referencePoint.getValue(1), EPSILON) ;
    assertEquals(1.5, referencePoint.getValue(2), EPSILON) ;
  }}