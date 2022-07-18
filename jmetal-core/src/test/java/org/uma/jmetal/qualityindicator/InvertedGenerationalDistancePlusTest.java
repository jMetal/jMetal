package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InvertedGenerationalDistancePlusTest {
  private static final double EPSILON = 0.0000000000001;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new InvertedGenerationalDistancePlus(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    var referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new InvertedGenerationalDistancePlus(referenceFront).compute(front));
  }

  @Test
  public void shouldEvaluateReturnZeroIfTheFrontAndTheReferenceFrontContainsTheSamePoints() {
    var point = new double[]{4.0, 10.0};
    var referenceFront = new double[][]{point};
    var front = new double[][]{point};

    Assertions.assertEquals(0.0, new InvertedGenerationalDistancePlus(referenceFront).compute(front), EPSILON);
  }

  @Test
  public void shouldEvaluateReturnTheCorrectValueCaseA() {
    var front = new double[][]{{0.2, 0.5}, {0.3, 0.4}, {0.4, 0.3}};
    var referenceFront = new double[][]{{0.1, 0.7}, {0.2, 0.3}, {0.6, 0.2}};

    var igdPlus =
            new InvertedGenerationalDistancePlus(referenceFront);

    Assertions.assertEquals((2.0 * Math.sqrt(0.01) + Math.sqrt(0.02)) / 3.0, igdPlus.compute(front), EPSILON);
  }

  @Test
  public void shouldEvaluateReturnTheCorrectValueCaseB() {
    var front = new double[][]{{0.3, 0.7}, {0.5, 0.6}, {0.7, 0.4}};
    var referenceFront = new double[][]{{0.1, 0.7}, {0.2, 0.3}, {0.6, 0.2}};

    var igdPlus =
            new InvertedGenerationalDistancePlus(referenceFront);

    Assertions.assertEquals((0.2 + Math.sqrt(0.01 + 0.16) + Math.sqrt(0.01 + 0.04)) / 3.0, igdPlus.compute(front), EPSILON);
  }
}
