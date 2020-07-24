package org.uma.jmetal.util.distance;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.impl.CosineDistanceBetweenSolutionsInObjectiveSpace;

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
    when(idealPoint.objectives().get(0)).thenReturn(0.0) ;
    when(idealPoint.objectives().get(1)).thenReturn(0.0) ;
    when(idealPoint.objectives().size()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.objectives().get(0)).thenReturn(1.0) ;
    when(point1.objectives().get(1)).thenReturn(1.0) ;
    when(point1.objectives().size()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.objectives().get(0)).thenReturn(1.0) ;
    when(point2.objectives().get(1)).thenReturn(1.0) ;
    when(point2.objectives().size()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldPointsInTheSameDirectionHaveADistanceOfOne() {
    Solution<?> idealPoint = mock(Solution.class) ;
    when(idealPoint.objectives().get(0)).thenReturn(0.0) ;
    when(idealPoint.objectives().get(1)).thenReturn(0.0) ;
    when(idealPoint.objectives().size()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.objectives().get(0)).thenReturn(1.0) ;
    when(point1.objectives().get(1)).thenReturn(1.0) ;
    when(point1.objectives().size()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.objectives().get(0)).thenReturn(2.0) ;
    when(point2.objectives().get(1)).thenReturn(2.0) ;
    when(point2.objectives().size()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldTwoPerpendicularPointsHaveADistanceOfZero() {
    Solution<?> idealPoint = mock(Solution.class) ;
    when(idealPoint.objectives().get(0)).thenReturn(0.0) ;
    when(idealPoint.objectives().get(1)).thenReturn(0.0) ;
    when(idealPoint.objectives().size()).thenReturn(3) ;

    Solution<?> point1 = mock(Solution.class) ;
    when(point1.objectives().get(0)).thenReturn(0.0) ;
    when(point1.objectives().get(1)).thenReturn(1.0) ;
    when(point1.objectives().size()).thenReturn(3) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.objectives().get(0)).thenReturn(1.0) ;
    when(point2.objectives().get(1)).thenReturn(0.0) ;
    when(point2.objectives().size()).thenReturn(3) ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }
}