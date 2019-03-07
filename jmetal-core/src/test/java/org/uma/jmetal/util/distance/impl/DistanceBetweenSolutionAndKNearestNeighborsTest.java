package org.uma.jmetal.util.distance.impl;

import org.junit.Test;
import org.uma.jmetal.solution.DoubleSolution;

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
    when(solution.getVariableValue(0)).thenReturn(1.0) ;
    when(solution.getVariableValue(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution) ;

    int k = 1  ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k) ;

    double receivedValue = distance.getDistance(solution, solutionList) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsOnlyASolution() {

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getVariableValue(0)).thenReturn(1.0) ;
    when(solution.getVariableValue(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    when(solution2.getVariableValue(0)).thenReturn(2.0) ;
    when(solution2.getVariableValue(1)).thenReturn(2.0) ;
    when(solution2.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    int k = 1 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k) ;
    double receivedValue = distance.getDistance(solution, solutionList) ;
    assertEquals(Math.sqrt(2)/k, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsThreeSolutionAndKIsEqualToTwo() {

    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getVariableValue(0)).thenReturn(1.0) ;
    when(solution.getVariableValue(1)).thenReturn(1.0) ;
    when(solution.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution2 = mock(DoubleSolution.class) ;
    when(solution2.getVariableValue(0)).thenReturn(2.0) ;
    when(solution2.getVariableValue(1)).thenReturn(2.0) ;
    when(solution2.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution3 = mock(DoubleSolution.class) ;
    when(solution3.getVariableValue(0)).thenReturn(3.0) ;
    when(solution3.getVariableValue(1)).thenReturn(3.0) ;
    when(solution3.getNumberOfVariables()).thenReturn(2) ;

    DoubleSolution solution4 = mock(DoubleSolution.class) ;
    when(solution4.getVariableValue(0)).thenReturn(4.0) ;
    when(solution4.getVariableValue(1)).thenReturn(4.0) ;
    when(solution4.getNumberOfVariables()).thenReturn(2) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;
    solutionList.add(solution3) ;
    solutionList.add(solution4) ;

    int k = 2 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution, List<DoubleSolution>> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k) ;

    double receivedValue = distance.getDistance(solution, solutionList) ;
    assertEquals((Math.sqrt(1+1) + Math.sqrt(4+4))/k, receivedValue, EPSILON) ;
  }
}