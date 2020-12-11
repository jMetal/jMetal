package org.uma.jmetal.qualityIndicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityIndicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class HypervolumeTest {

  private static final double EPSILON = 0.0000000000001;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new PISAHypervolume(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    double[][] referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new PISAHypervolume(referenceFront).compute(front));
  }

}
