package org.uma.jmetal.util.point.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.util.comparator.PointDimensionComparator;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointDimensionComparatorTest {
  private Point point1 ;
  private Point point2 ;

  private PointDimensionComparator comparator ;

  @Before public void setup() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, -2.0);

    point2 = new ArrayPoint(3) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, -3.0);
    point2.setDimensionValue(2, 5.0);
  }

  @After public void clean() {
    point1 = null ;
    point2 = null ;
  }

  @Test public void shouldCompareReturnZeroIfTheComparedValuesAreEqual() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    assertEquals(0, comparator.compare(point1, point2)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheFirstValueIsLower() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    point1.setDimensionValue(0, 0.0);

    assertEquals(-1, comparator.compare(point1, point2)) ;
  }

  @Test public void shouldCompareReturnPlusOneIfTheFirstValueIsGreater() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    point1.setDimensionValue(0, 3.0);

    assertEquals(1, comparator.compare(point1, point2)) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldIndexLessThanZeroRaiseAnException() throws Exception {
    comparator = new PointDimensionComparator(-1) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    comparator.compare(null, point2);
  }

  @Test (expected = JMetalException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointDimensionComparator(0) ;
    comparator.compare(point1, null);
  }

  @Test (expected = JMetalException.class)
  public void shouldIndexValueGreaterThanFirstPointDimensionsRaiseAnException() throws Exception {
    point1 = new ArrayPoint(3) ;
    point2 = new ArrayPoint(6) ;
    comparator = new PointDimensionComparator(3) ;
    comparator.compare(point1, point2);
  }

  @Test (expected = JMetalException.class)
  public void shouldIndexValueGreaterThanSecondPointDimensionsRaiseAnException() throws Exception {
    point1 = new ArrayPoint(6) ;
    point2 = new ArrayPoint(3) ;
    comparator = new PointDimensionComparator(3) ;
    comparator.compare(point1, point2);
  }
}
