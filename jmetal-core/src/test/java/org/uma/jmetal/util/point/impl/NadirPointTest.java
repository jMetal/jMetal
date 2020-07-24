package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.point.Point;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    assertEquals(numberOfObjectives, referencePoint.getDimension()) ;

    for (int i = 0 ; i < numberOfObjectives; i++) {
      assertEquals(DEFAULT_INITIAL_VALUE, referencePoint.getValue(i), EPSILON) ;
    }
  }

  @Test
  public void shouldUpdateWithOneSolutionMakeTheNadirPointHaveTheSolutionValues() {
    int numberOfObjectives = 2 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.objectives().size()).thenReturn(numberOfObjectives) ;
    for (int i = 0; i < numberOfObjectives; i++) {
      when(solution.objectivesArray()).thenReturn(new double[]{1, 2}) ;
    }

    referencePoint.update(solution.objectivesArray());
    assertEquals(1, referencePoint.getValue(0), EPSILON) ;
    assertEquals(2, referencePoint.getValue(1), EPSILON) ;
  }

  @Test
  public void shouldUpdateWithTwoSolutionsLeadToTheCorrectNadirPoint() {
    int numberOfObjectives = 2 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution1.objectives().get(0)).thenReturn(0.0) ;
    when(solution1.objectives().get(1)).thenReturn(1.0) ;
    when(solution1.objectivesArray()).thenReturn(new double[]{0.0, 1.0}) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution2.objectives().get(0)).thenReturn(1.0) ;
    when(solution2.objectives().get(1)).thenReturn(0.0) ;
    when(solution2.objectivesArray()).thenReturn(new double[]{1.0, 0.0}) ;

    referencePoint.update(solution1.objectivesArray());
    referencePoint.update(solution2.objectivesArray());

    assertEquals(1.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(1.0, referencePoint.getValue(1), EPSILON) ;
  }

  @Test
  public void shouldUpdateWithThreeSolutionsLeadToTheCorrectNadirPoint() {
    int numberOfObjectives = 3 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution1.objectives().get(0)).thenReturn(3.0) ;
    when(solution1.objectives().get(1)).thenReturn(1.0) ;
    when(solution1.objectives().get(2)).thenReturn(2.0) ;
    when(solution1.objectivesArray()).thenReturn(new double[]{3.0, 1.0, 2.0}) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution2.objectives().get(0)).thenReturn(0.2) ;
    when(solution2.objectives().get(1)).thenReturn(4.0) ;
    when(solution2.objectives().get(2)).thenReturn(5.5) ;
    when(solution2.objectivesArray()).thenReturn(new double[]{0.2, 4.0, 5.5}) ;

    IntegerSolution solution3 = mock(IntegerSolution.class) ;
    when(solution3.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution3.objectives().get(0)).thenReturn(5.0) ;
    when(solution3.objectives().get(1)).thenReturn(6.0) ;
    when(solution3.objectives().get(2)).thenReturn(1.5) ;
    when(solution3.objectivesArray()).thenReturn(new double[]{5.0, 6.0, 1.5}) ;

    referencePoint.update(solution1.objectivesArray());
    referencePoint.update(solution2.objectivesArray());
    referencePoint.update(solution3.objectivesArray());

    assertEquals(5.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(6.0, referencePoint.getValue(1), EPSILON) ;
    assertEquals(5.5, referencePoint.getValue(2), EPSILON) ;
  }

  @Test
  public void shouldUpdateAListOfSolutionsLeadToTheCorrectNadirPoint() {
    int numberOfObjectives = 3 ;

    referencePoint = new NadirPoint(numberOfObjectives) ;

    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    when(solution1.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution1.objectives().get(0)).thenReturn(3.0) ;
    when(solution1.objectives().get(1)).thenReturn(1.0) ;
    when(solution1.objectives().get(2)).thenReturn(2.0) ;
    when(solution1.objectivesArray()).thenReturn(new double[]{3.0, 1.0, 2.0}) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution2.objectives().get(0)).thenReturn(0.2) ;
    when(solution2.objectives().get(1)).thenReturn(4.0) ;
    when(solution2.objectives().get(2)).thenReturn(5.5) ;
    when(solution2.objectivesArray()).thenReturn(new double[]{0.2, 4.0, 5.5}) ;

    IntegerSolution solution3 = mock(IntegerSolution.class) ;
    when(solution3.objectives().size()).thenReturn(numberOfObjectives) ;
    when(solution3.objectives().get(0)).thenReturn(5.0) ;
    when(solution3.objectives().get(1)).thenReturn(6.0) ;
    when(solution3.objectives().get(2)).thenReturn(1.5) ;
    when(solution3.objectivesArray()).thenReturn(new double[]{5.0, 6.0, 1.5}) ;

    List<IntegerSolution> solutionList = Arrays.asList(solution1, solution2, solution3) ;

    referencePoint.update(solutionList);

    assertEquals(5.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(6.0, referencePoint.getValue(1), EPSILON) ;
    assertEquals(5.5, referencePoint.getValue(2), EPSILON) ;
  }

  @Test
  public void shouldSetAssignTheRightValues() {
    Point point = new ArrayPoint(new double[]{2, 3, 3}) ;

    point.set(new double[]{5, 6, 7}) ;
    assertEquals(5, point.getValue(0), EPSILON);
    assertEquals(6, point.getValue(1), EPSILON);
    assertEquals(7, point.getValue(2), EPSILON);
  }
}