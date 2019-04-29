package org.uma.jmetal.util.aggregativefunction.impl;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;

import static org.junit.Assert.assertEquals;

public class WeightedSumTest {

  private AggregativeFunction aggregativeFunction ;
  private static final double EPSILON = 0.00000000001 ;

  @Before
  public void init() {
    aggregativeFunction = new WeightedSum() ;
  }

  @Test
  public void shouldComputeWorkCorrectlyWithAWeightVectorOfDimensionTwo() {

    double result = aggregativeFunction.compute(new double[]{1.3, 0.5}, new double[]{1.0, 0.0}) ;
    assertEquals(1.3, result, EPSILON);

    result = aggregativeFunction.compute(new double[]{1.3, 0.5}, new double[]{0.0, 1.0}) ;
    assertEquals(0.5, result, EPSILON);

    result = aggregativeFunction.compute(new double[]{1.3, 0.5}, new double[]{0.5, 0.5}) ;
    assertEquals(1.3/2.0 + 0.5/2.0, result, EPSILON);
  }

  @Test
  public void shouldComputeWorkCorrectlyWithAWeightVectorOfDimensionThree() {
    double result = aggregativeFunction.compute(new double[]{1.3, 3.5, -1.5}, new double[]{1.0, 0.0, 0.0}) ;
    assertEquals(1.3, result, EPSILON);

    result = aggregativeFunction.compute(new double[]{1.3, 3.5, -1.5}, new double[]{0.0, 1.0, 0.0}) ;
    assertEquals(3.5, result, EPSILON);

    result = aggregativeFunction.compute(new double[]{1.3, 3.5, -1.5}, new double[]{0.0, 0.0, 1.0}) ;
    assertEquals(-1.5, result, EPSILON);

    result = aggregativeFunction.compute(new double[]{1.3, 3.5, -1.5}, new double[]{0.1, 0.5, 0.4}) ;
    assertEquals(1.3*0.1 + 3.5*0.5 -1.5*0.4, result, EPSILON);
  }
}