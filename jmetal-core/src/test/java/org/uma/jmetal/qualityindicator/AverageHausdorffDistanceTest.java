package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.AverageHausdorffDistance;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

public class AverageHausdorffDistanceTest {

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    assertThrows(NullParameterException.class, () -> new AverageHausdorffDistance(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    double[][] referenceFront = new double[0][0];
    double[][] front = null;
    assertThrows(NullParameterException.class, () -> new AverageHausdorffDistance(referenceFront).compute(front));
  }

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    assertEquals("AHD", new AverageHausdorffDistance().name());
  }

  @Test
  public void shouldComputeReturnTheCorrectValue() {
    double[][] front = { { 1.0, 1.0 }, { 2.0, 2.0 } };
    double[][] referenceFront = { { 3.0, 3.0 }, { 4.0, 4.0 } };

    AverageHausdorffDistance averageHausdorffDistance = new AverageHausdorffDistance(referenceFront);
    double value = averageHausdorffDistance.compute(front);

    assertEquals(2.1213203435596424, value, 0.0000001);
  }
}
