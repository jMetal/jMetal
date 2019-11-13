package org.uma.jmetal.util.neighborhood.impl;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KNearestNeighborhoodTest {

  /**
   * Case A: The solution list has two solutions and the neighbor size is 1
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseA() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(1) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 0) ;

    assertEquals(1, neighbors.size());
    assertSame(point2, neighbors.get(0));
  }

  /**
   * Case B: The solution list has three solutions, the index of the solution is 0, and the neighbor size is 2
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseB() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getNumberOfObjectives()).thenReturn(2) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getNumberOfObjectives()).thenReturn(2) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    Solution<?> point3 = mock(Solution.class) ;
    when(point3.getNumberOfObjectives()).thenReturn(2) ;
    when(point3.getObjective(0)).thenReturn(2.0) ;
    when(point3.getObjective(1)).thenReturn(2.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2, point3) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(2) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 0) ;

    assertEquals(2, neighbors.size());
    assertSame(point2, neighbors.get(0));
    assertSame(point3, neighbors.get(1));
  }

  /**
   * Case C: The solution list has three solutions, the index of the solution is 1, and the neighbor size is 2
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseC() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getNumberOfObjectives()).thenReturn(2) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getNumberOfObjectives()).thenReturn(2) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    Solution<?> point3 = mock(Solution.class) ;
    when(point3.getNumberOfObjectives()).thenReturn(2) ;
    when(point3.getObjective(0)).thenReturn(2.0) ;
    when(point3.getObjective(1)).thenReturn(2.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2, point3) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(2) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 1) ;

    assertEquals(2, neighbors.size());
    assertSame(point1, neighbors.get(0));
    assertSame(point3, neighbors.get(1));
  }

  /**
   * Case D: The solution list has three solutions, the index of the solution is 2, and the neighbor size is 2
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseD() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getNumberOfObjectives()).thenReturn(2) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getNumberOfObjectives()).thenReturn(2) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    Solution<?> point3 = mock(Solution.class) ;
    when(point3.getNumberOfObjectives()).thenReturn(2) ;
    when(point3.getObjective(0)).thenReturn(2.0) ;
    when(point3.getObjective(1)).thenReturn(2.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2, point3) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(2) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 2) ;

    assertEquals(2, neighbors.size());
    assertSame(point2, neighbors.get(0));
    assertSame(point1, neighbors.get(1));
  }

  /**
   * Case E: The solution list has five solutions, the index of the solution is 0, and the neighbor size is 3
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseE() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getNumberOfObjectives()).thenReturn(2) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getNumberOfObjectives()).thenReturn(2) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    Solution<?> point3 = mock(Solution.class) ;
    when(point3.getNumberOfObjectives()).thenReturn(2) ;
    when(point3.getObjective(0)).thenReturn(2.0) ;
    when(point3.getObjective(1)).thenReturn(2.0) ;

    Solution<?> point4 = mock(Solution.class) ;
    when(point4.getNumberOfObjectives()).thenReturn(2) ;
    when(point4.getObjective(0)).thenReturn(3.0) ;
    when(point4.getObjective(1)).thenReturn(3.0) ;

    Solution<?> point5 = mock(Solution.class) ;
    when(point5.getNumberOfObjectives()).thenReturn(2) ;
    when(point5.getObjective(0)).thenReturn(4.0) ;
    when(point5.getObjective(1)).thenReturn(4.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2, point3, point4, point5) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(3) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 0) ;

    assertEquals(3, neighbors.size());
    assertSame(point2, neighbors.get(0));
    assertSame(point3, neighbors.get(1));
    assertSame(point4, neighbors.get(2));
  }

  /**
   * Case F: The solution list has five solutions, the index of the solution is 2, and the neighbor size is 3
   */
  @Test
  public void shouldGetNeighborsWorkProperlyCaseF() {
    Solution<?> point1 = mock(Solution.class) ;
    when(point1.getNumberOfObjectives()).thenReturn(2) ;
    when(point1.getObjective(0)).thenReturn(0.0) ;
    when(point1.getObjective(1)).thenReturn(0.0) ;

    Solution<?> point2 = mock(Solution.class) ;
    when(point2.getNumberOfObjectives()).thenReturn(2) ;
    when(point2.getObjective(0)).thenReturn(1.0) ;
    when(point2.getObjective(1)).thenReturn(1.0) ;

    Solution<?> point3 = mock(Solution.class) ;
    when(point3.getNumberOfObjectives()).thenReturn(2) ;
    when(point3.getObjective(0)).thenReturn(2.0) ;
    when(point3.getObjective(1)).thenReturn(2.0) ;

    Solution<?> point4 = mock(Solution.class) ;
    when(point4.getNumberOfObjectives()).thenReturn(2) ;
    when(point4.getObjective(0)).thenReturn(3.0) ;
    when(point4.getObjective(1)).thenReturn(3.0) ;

    Solution<?> point5 = mock(Solution.class) ;
    when(point5.getNumberOfObjectives()).thenReturn(2) ;
    when(point5.getObjective(0)).thenReturn(4.0) ;
    when(point5.getObjective(1)).thenReturn(4.0) ;

    List<Solution<?>> solutionList = Arrays.asList(point1, point2, point3, point4, point5) ;

    KNearestNeighborhood<Solution<?>> neighborhood = new KNearestNeighborhood<>(3) ;
    List<Solution<?>> neighbors = neighborhood.getNeighbors(solutionList, 2) ;

    assertEquals(3, neighbors.size());
    assertSame(point2, neighbors.get(0));
    assertSame(point4, neighbors.get(1));
    assertSame(point1, neighbors.get(2));
  }
}