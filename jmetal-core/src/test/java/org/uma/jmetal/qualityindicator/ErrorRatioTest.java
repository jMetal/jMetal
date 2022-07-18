package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.ErrorRatio;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ErrorRatioTest {
  private static final double EPSILON = 0.0000000000001;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new ErrorRatio(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    var referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new ErrorRatio(referenceFront).compute(front));
  }

  @Test
  public void shouldExecuteReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    var point = new double[]{10.0, 12.0, -1.0};

    var front = new double[][]{point};
    var referenceFront = new double[][]{point};

    Assertions.assertEquals(0.0, new ErrorRatio(referenceFront).compute(front), EPSILON);
  }

  @Test
  public void shouldExecuteReturnOneIfTheFrontsContainADifferentPoint() {
    var front = new double[][]{{10.0, 12.0, -1.0}};
    var referenceFront = new double[][]{{3.0, 5.0, -2.0}};

    Assertions.assertEquals(1.0, new ErrorRatio(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [1.5,2.0],[2.0,1.5] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 2/3
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    var front = new double[][]{{1.5, 4.0}, {1.5, 2.0}, {2.0, 1.5}};
    var referenceFront = new double[][]{{1.0, 3.0}, {1.5, 2.0}, {2.0, 1.5}};

    Assertions.assertEquals(1.0 / front.length, new ErrorRatio(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a list with solutions [1.5,3.0], [4.0,2.0] and another lists with solutions
   * [-1.0,-1.0], [0.0,0.0], the value of the epsilon indicator is 1
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    var front = new double[][]{{1.5, 3.0}, {4.0, 2.0}};
    var referenceFront = new double[][]{{-1.0, -1.0}, {0.0, 0.0}};

    Assertions.assertEquals(1.0, new ErrorRatio(referenceFront).compute(front), EPSILON);

  }

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    Assertions.assertEquals("ER", new ErrorRatio().getName());
  }
}
