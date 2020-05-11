package org.uma.jmetal.util.distance;

import org.junit.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInSolutionSpace;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DistanceBetweenSolutionAndKNearestNeighborsTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  public void shouldGetDistanceReturnZeroIfTheSolutionListContainsOnlyTheSolution() {

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getVariable(0)).thenReturn(1.0) ;
    when(solution.getVariable(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution) ;

    int k = 1  ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    double receivedValue = distance.compute(solution, solutionList) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsOnlyASolution() {

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getVariable(0)).thenReturn(1.0) ;
    when(solution.getVariable(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    when(solution2.getVariable(0)).thenReturn(2.0) ;
    when(solution2.getVariable(1)).thenReturn(2.0) ;
    when(solution2.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    int k = 1 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;
    double receivedValue = distance.compute(solution, solutionList) ;
    assertEquals(Math.sqrt(2), receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsThreeSolutionAndKIsEqualToTwo() {

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getVariable(0)).thenReturn(1.0) ;
    when(solution.getVariable(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    when(solution2.getVariable(0)).thenReturn(2.0) ;
    when(solution2.getVariable(1)).thenReturn(2.0) ;
    when(solution2.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution3 = mock(DoubleSolution.class) ;
    when(solution3.getVariable(0)).thenReturn(3.0) ;
    when(solution3.getVariable(1)).thenReturn(3.0) ;
    when(solution3.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution4 = mock(DoubleSolution.class) ;
    when(solution4.getVariable(0)).thenReturn(4.0) ;
    when(solution4.getVariable(1)).thenReturn(4.0) ;
    when(solution4.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;
    solutionList.add(solution3) ;
    solutionList.add(solution4) ;

    int k = 2 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    double receivedValue = distance.compute(solution, solutionList) ;
    assertEquals((Math.sqrt(4+4)), receivedValue, EPSILON) ;
  }
}