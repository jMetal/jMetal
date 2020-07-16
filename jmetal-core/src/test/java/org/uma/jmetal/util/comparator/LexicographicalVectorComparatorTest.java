package org.uma.jmetal.util.comparator;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class LexicographicalVectorComparatorTest {
  private LexicographicalVectorComparator comparator ;

  @Before
  public void startup() {
    comparator = new LexicographicalVectorComparator() ;
  }

  @Test(expected = NullParameterException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    comparator.compare(null, new double[]{1, 2});
  }

  @Test (expected = NullParameterException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    comparator.compare(new double[]{1, 2}, null);
  }

  @Test
  public void shouldCompareIdenticalPointsReturnZero() {
    assertEquals(0, comparator.compare(new double[]{1, 3}, new double[]{1, 3}));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheFirstValueReturnPlus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0} ;
    double[] point2 = new double[]{-1.0, 0.0, 5.0, 7.0} ;

    assertEquals(1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheFirstValueReturnMinus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0} ;
    double[] point2 = new double[]{-1.0, 0.0, 5.0, 7.0} ;

    assertEquals(-1, comparator.compare(point2, point1));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheLastValueReturnMinus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 0.0} ;
    double[] point2 = new double[]{1.0, 0.0, 5.0, 7.0} ;

    assertEquals(-1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareIdenticalPointsButTheLastValueReturnPlus1() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0} ;
    double[] point2 = new double[]{1.0, 0.0, 5.0, 0.0} ;

    assertEquals(1, comparator.compare(point1, point2));
  }

  @Test
  public void shouldCompareEmptyPointsReturnZero() {
    assertEquals(0, comparator.compare(new double[]{}, new double[]{}));
  }

  @Test
  public void shouldCompareDifferentLengthPointsReturnTheCorrectValue() {
    double[] point1 = new double[]{1.0, 0.0, 5.0, 7.0} ;
    double[] point2 = new double[]{1.0, 0.0, 5.0} ;

    assertEquals(0, comparator.compare(point1, point2));
  }
}
