package org.uma.jmetal.qualityindicator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class HypervolumeTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(NullParameterException.class);

    Front front = new ArrayFront(0, 0) ;

    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<DoubleSolution>(front) ;
    hypervolume.evaluate(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(NullParameterException.class);

    Front front = null ;

    new PISAHypervolume<DoubleSolution>(front) ;
  }
}
