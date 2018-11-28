package org.uma.jmetal.util.distance.impl;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ajnebro on 12/2/16.
 */
public class CosineDistanceBetweenSolutionsInObjectiveSpaceTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  public void shouldIdenticalPointsHaveADistanceOfOne() {
    Solution<?> idealPoint = mock(Solution.class) ;
    when(idealPoint.getObjective(0)).thenReturn(0.0) ;
    when(idealPoint.getObjective(1)).thenReturn(0.0) ;
    when(idealPoint.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getObjective(0)).thenReturn(1.0) ;
    when(point1.getObjective(1)).thenReturn(1.0) ;
    when(point1.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;
    when(point2.getNumberOfObjectives()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.getDistance(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldPointsInTheSameDirectionHaveADistanceOfOne() {
    Solution<?> idealPoint = mock(Solution.class) ;
    when(idealPoint.getObjective(0)).thenReturn(0.0) ;
    when(idealPoint.getObjective(1)).thenReturn(0.0) ;
    when(idealPoint.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getObjective(0)).thenReturn(1.0) ;
    when(point1.getObjective(1)).thenReturn(1.0) ;
    when(point1.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getObjective(0)).thenReturn(2.0) ;
    when(point2.getObjective(1)).thenReturn(2.0) ;
    when(point2.getNumberOfObjectives()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.getDistance(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldTwoPerpendicularPointsHaveADistanceOfZero() {
    Solution<?> idealPoint = mock(Solution.class) ;
    when(idealPoint.getObjective(0)).thenReturn(0.0) ;
    when(idealPoint.getObjective(1)).thenReturn(0.0) ;
    when(idealPoint.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(1.0) ;
    when(point1.getNumberOfObjectives()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(0.0) ;
    when(point2.getNumberOfObjectives()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.getDistance(point1, point2) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }
}