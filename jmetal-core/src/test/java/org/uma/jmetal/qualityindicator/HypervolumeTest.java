package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
class HypervolumeTest {

  private static final double EPSILON = 0.0000000000001;

  @Test
  void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullPointerException.class, () -> new PISAHypervolume(referenceFront));
  }

  @Test
  void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    double[][] referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new PISAHypervolume(referenceFront).compute(front));
  }
}
