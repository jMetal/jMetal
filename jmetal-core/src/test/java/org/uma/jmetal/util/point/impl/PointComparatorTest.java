package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.util.comparator.PointComparator;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointComparatorTest {
  private Point point1 ;
  private Point point2 ;

  private PointComparator comparator ;

  @Test(expected = JMetalException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator() ;

    point2 = new ArrayPoint(4) ;
    comparator.compare(null, point2);
  }

  @Test (expected = JMetalException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator() ;

    point1 = new ArrayPoint(4) ;
    comparator.compare(point1, null);
  }

  @Test (expected = JMetalException.class)
  public void shouldComparingDifferentLengthPointsRaiseAnException() throws Exception {
    point1 = new ArrayPoint(2) ;
    point2 = new ArrayPoint(3) ;

    comparator = new PointComparator() ;
    comparator.compare(point1, point2);
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheFirstPointIsBetterThanTheSecondOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setValue(0, 1.0);
    point1.setValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setValue(0, 1.0);
    point2.setValue(1, 2.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(-1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareReturnOneIfTheSecondPointIsBetterThanTheFirstOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setValue(0, 1.0);
    point1.setValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setValue(0, 1.0);
    point2.setValue(1, 5.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setValue(0, 1.0);
    point1.setValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setValue(0, 1.0);
    point2.setValue(1, 3.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(0, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMinimizing() {
    point1 = new ArrayPoint(2) ;
    point1.setValue(0, 1.0);
    point1.setValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setValue(0, 1.0);
    point2.setValue(1, 3.0);

    comparator = new PointComparator() ;
    comparator.setMinimizing();

    assertEquals(0, comparator.compare(point1, point2)) ;
  }
}
