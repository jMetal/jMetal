package org.uma.jmetal.util.aggregativefunction.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;

public class WeightedSumTest {

  private AggregativeFunction<DoubleSolution> aggregativeFunction ;
  private static final double EPSILON = 0.00000000001 ;

  @Before
  public void init() {
    aggregativeFunction = new WeightedSum<>() ;
  }

  @Test
  public void shouldComputeWorkCorrectlyWithAWeightVectorOfDimensionTwo() {
    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.3) ;
    when(solution.getObjective(1)).thenReturn(3.5) ;

    double result = aggregativeFunction.compute(solution, new double[]{1.0, 0.0}) ;
    assertEquals(1.3, result, EPSILON);

    result = aggregativeFunction.compute(solution, new double[]{0.0, 1.0}) ;
    assertEquals(3.5, result, EPSILON);

    result = aggregativeFunction.compute(solution, new double[]{0.5, 0.5}) ;
    assertEquals(1.3/2.0 + 3.5/2.0, result, EPSILON);
  }

  @Test
  public void shouldComputeWorkCorrectlyWithAWeightVectorOfDimensionThree() {
    DoubleSolution solution = mock(DoubleSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(3) ;
    when(solution.getObjective(0)).thenReturn(1.3) ;
    when(solution.getObjective(1)).thenReturn(3.5) ;
    when(solution.getObjective(2)).thenReturn(-1.5) ;

    double result = aggregativeFunction.compute(solution, new double[]{1.0, 0.0, 0.0}) ;
    assertEquals(1.3, result, EPSILON);

    result = aggregativeFunction.compute(solution, new double[]{0.0, 1.0, 0.0}) ;
    assertEquals(3.5, result, EPSILON);

    result = aggregativeFunction.compute(solution, new double[]{0.0, 0.0, 1.0}) ;
    assertEquals(-1.5, result, EPSILON);

    result = aggregativeFunction.compute(solution, new double[]{0.1, 0.5, 0.4}) ;
    assertEquals(1.3*0.1 + 3.5*0.5 -1.5*0.4, result, EPSILON);
  }
}