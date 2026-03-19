package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class EpsilonTest {
  private static final double EPSILON = 0.0000000000001;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new Epsilon(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    double[][] referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new Epsilon(referenceFront).compute(front));
  }

  @Test
  public void shouldComputeReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    double[] vector = { 10, 12, -1 };

    double[][] front = { vector };
    double[][] referenceFront = { vector };

    Assertions.assertEquals(0.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with point [2,3] and a Pareto front with point [1,2], the value
   * of the
   * epsilon indicator is 1
   */
  @Test
  public void shouldComputeReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    double[][] front = { { 2, 3 } };
    double[][] referenceFront = { { 1, 2 } };

    Assertions.assertEquals(1.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [2.0,3.0],[3.0,2.0] and a Pareto front
   * with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 1
   */
  @Test
  public void shouldComputeReturnTheCorrectValueCaseA() {
    double[][] front = { { 1.5, 4.0 }, { 2.0, 3.0 }, { 3.0, 2.0 } };
    double[][] referenceFront = { { 1.0, 3.0 }, { 1.5, 2.0 }, { 2.0, 1.5 } };

    Assertions.assertEquals(1.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [1.5,2.0],[2.0,1.5] and a Pareto front
   * with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 0.5
   */
  @Test
  public void shouldComputeReturnTheCorrectValueCaseB() {
    double[][] front = { { 1.5, 4.0 }, { 1.5, 2.0 }, { 2.0, 1.5 } };
    double[][] referenceFront = { { 1.0, 3.0 }, { 1.5, 2.0 }, { 2.0, 1.5 } };

    Assertions.assertEquals(0.5, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    Assertions.assertEquals("EP", new Epsilon().name());
  }
}
