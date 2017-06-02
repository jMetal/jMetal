package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class HypervolumeTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front approximation is null"));

    Front front = new ArrayFront(0, 0) ;

    Hypervolume<DoubleSolution> hypervolume = new PISAHypervolume<DoubleSolution>(front) ;
    hypervolume.evaluate(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference pareto front is null"));

    Front front = null ;

    new PISAHypervolume<DoubleSolution>(front) ;
  }
}
