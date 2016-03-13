package org.uma.jmetal.referencePoint.impl;

import org.junit.Test;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.referencePoint.impl.NadirPoint;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ajnebro on 12/2/16.
 */
public class NadirPointTest {
  private static final double EPSILON = 0.00000000001 ;
  private static final double DEFAULT_INITIAL_VALUE = Double.NEGATIVE_INFINITY ;
  private NadirPoint referencePoint ;

  @Test
  public void shouldConstructorCreateANadirPointWithAllObjectiveValuesCorrectlyInitialized() {
    int numberOfObjectives = 4 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    assertEquals(numberOfObjectives, referencePoint.getNumberOfObjectives()) ;

    for (int i = 0 ; i < numberOfObjectives; i++) {
      assertEquals(DEFAULT_INITIAL_VALUE, referencePoint.getObjective(i), EPSILON) ;
    }
  }

  @Test
  public void shouldUpdateWithOneSolutionMakeTheIdealPointHaveTheSolutionValues() {
    int numberOfObjectives = 2 ;
    double defaultValue = 1.0 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    for (int i = 0; i < numberOfObjectives; i++) {
      when(solution.getObjective(i)).thenReturn(defaultValue) ;
    }

    referencePoint.update(solution);
    for (int i = 0 ; i < numberOfObjectives; i++) {
      assertEquals(defaultValue, referencePoint.getObjective(i), EPSILON) ;
    }
  }

  @Test
  public void shouldUpdateWithTwoSolutionsLeadToTheCorrectIdealPoint() {
    int numberOfObjectives = 2 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution1.getObjective(0)).thenReturn(0.0) ;
    when(solution1.getObjective(1)).thenReturn(1.0) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution2.getObjective(0)).thenReturn(1.0) ;
    when(solution2.getObjective(1)).thenReturn(0.0) ;

    referencePoint.update(solution1);
    referencePoint.update(solution2);

    assertEquals(1.0, referencePoint.getObjective(0), EPSILON) ;
    assertEquals(1.0, referencePoint.getObjective(1), EPSILON) ;
  }

  @Test
  public void shouldUpdateWithThreeSolutionsLeadToTheCorrectIdealPoint() {
    int numberOfObjectives = 3 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution1.getObjective(0)).thenReturn(3.0) ;
    when(solution1.getObjective(1)).thenReturn(1.0) ;
    when(solution1.getObjective(2)).thenReturn(2.0) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution2.getObjective(0)).thenReturn(0.2) ;
    when(solution2.getObjective(1)).thenReturn(4.0) ;
    when(solution2.getObjective(2)).thenReturn(5.5) ;

    IntegerSolution solution3 = mock(IntegerSolution.class) ;
    when(solution3.getNumberOfObjectives()).thenReturn(numberOfObjectives) ;
    when(solution3.getObjective(0)).thenReturn(5.0) ;
    when(solution3.getObjective(1)).thenReturn(6.0) ;
    when(solution3.getObjective(2)).thenReturn(1.5) ;

    referencePoint.update(solution1);
    referencePoint.update(solution2);
    referencePoint.update(solution3);

    assertEquals(5.0, referencePoint.getObjective(0), EPSILON) ;
    assertEquals(6.0, referencePoint.getObjective(1), EPSILON) ;
    assertEquals(5.5, referencePoint.getObjective(2), EPSILON) ;
  }
}