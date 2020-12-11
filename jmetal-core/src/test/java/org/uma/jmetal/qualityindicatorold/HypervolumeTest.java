package org.uma.jmetal.qualityindicatorold;

import org.junit.Test;
import org.uma.jmetal.qualityindicatorold.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicatorold.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;

import static org.junit.Assert.assertThrows;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class HypervolumeTest {

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    Front front = new ArrayFront(0, 0);

    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<DoubleSolution>(front);
    assertThrows(NullParameterException.class, () -> hypervolume.evaluate(null));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    Front front = null;

    assertThrows(NullParameterException.class, () -> new PISAHypervolume<DoubleSolution>(front));
  }
}
