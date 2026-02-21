package org.uma.jmetal.util.point.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.comparator.PointDimensionComparator;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointDimensionComparatorTest {
  private Point point1 ;
  private Point point2 ;

  private PointDimensionComparator comparator ;

  @BeforeEach public void setup() {
    point1 = new ArrayPoint(2) ;
    point1.value(0, 1.0);
    point1.value(1, -2.0);

    point2 = new ArrayPoint(3) ;
    point2.value(0, 1.0);
    point2.value(1, -3.0);
    point2.value(2, 5.0);
  }

  @AfterEach public void clean() {
    point1 = null ;
    point2 = null ;
  }

  @Test public void shouldCompareReturnZeroIfTheComparedValuesAreEqual() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    assertEquals(0, comparator.compare(point1, point2)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheFirstValueIsLower() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    point1.value(0, 0.0);

    assertEquals(-1, comparator.compare(point1, point2)) ;
  }

  @Test public void shouldCompareReturnPlusOneIfTheFirstValueIsGreater() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    point1.value(0, 3.0);

    assertEquals(1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldIndexLessThanZeroRaiseAnException()  {
    assertThrows(NegativeValueException.class, () -> new PointDimensionComparator(-1));
  }

  @Test
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() throws Exception {
    assertThrows(NullParameterException.class, () -> {
      comparator = new PointDimensionComparator(0) ;
      comparator.compare(null, point2);
    });
  }

  @Test
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() throws Exception {
    assertThrows(NullParameterException.class, () -> {
      comparator = new PointDimensionComparator(0) ;
      comparator.compare(point1, null);
    });
  }

  @Test
  public void shouldIndexValueGreaterThanFirstPointDimensionsRaiseAnException() throws Exception {
    assertThrows(InvalidConditionException.class, () -> {
      point1 = new ArrayPoint(3) ;
      point2 = new ArrayPoint(6) ;
      comparator = new PointDimensionComparator(3) ;
      comparator.compare(point1, point2);
    });
  }

  @Test
  public void shouldIndexValueGreaterThanSecondPointDimensionsRaiseAnException() throws Exception {
    assertThrows(InvalidConditionException.class, () -> {
      point1 = new ArrayPoint(6) ;
      point2 = new ArrayPoint(3) ;
      comparator = new PointDimensionComparator(3) ;
      comparator.compare(point1, point2);
    });
  }
}
