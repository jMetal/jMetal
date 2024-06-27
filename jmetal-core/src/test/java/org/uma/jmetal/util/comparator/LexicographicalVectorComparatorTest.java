package org.uma.jmetal.util.comparator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class LexicographicalVectorComparatorTest {

  private LexicographicalVectorComparator comparator;

  @BeforeEach
  public void startup() {
    comparator = new LexicographicalVectorComparator();
  }

  @Test
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class, () -> comparator.compare(null, new double[]{1, 2}));
  }

  @Test
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class, () -> comparator.compare(new double[]{1, 2}, null));
  }

  @Test
  public void shouldCompareIdenticalPointsReturnZero() {
    assertEquals(0, comparator.compare(new double[]{1, 3}, new double[]{1, 3}));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheFirstValueReturnPlus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0};
    double[] point2 = new double[]{-1.0, 0.0, 5.0, 7.0};

    assertEquals(1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheFirstValueReturnMinus1() {
    double[] point1 = new double[]{-1.0, 0.0, 5.0, 7.0};
    double[] point2 = new double[]{1.0, 0.0, 5.0, 7.0};

    assertEquals(-1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheLastValueReturnMinus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 0.0};
    double[] point2 = new double[]{1.0, 0.0, 5.0, 7.0};

    assertEquals(-1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheLastValueReturnPlus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0};
    double[] point2 = new double[]{1.0, 0.0, 5.0, 0.0};

    assertEquals(1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareEmptyPointsReturnZero() {
    assertEquals(0, comparator.compare(new double[]{}, new double[]{}));
  }

  @Test
  public void shouldCompareDifferentLengthPointsWithTheSameValuesInTheCommonPositionsReturnZero() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0};
    double[] point2 = new double[]{1.0, 0.0, 5.0};

    assertEquals(0, comparator.compare(point1, point2));
  }
}
