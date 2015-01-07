package org.uma.jmetal.util.point.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import static org.junit.Assert.assertEquals;

public class PointComparatorTest {
  private static final double EPSILON = 0.0000000000001 ;

  private Point point1 ;
  private Point point2 ;

  private PointComparator comparator ;

  @Before public void setup() {
  }

  @After public void clean() {
  }

  @Test(expected = JMetalException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator(true) ;
    comparator.compare(null, point2);
  }

  @Test (expected = JMetalException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator(true) ;
    comparator.compare(point1, null);
  }

  @Test (expected = JMetalException.class)
  public void shouldComparingDifferentLengthPointsRaiseAnException() throws Exception {
    point1 = new ArrayPoint(2) ;
    point2 = new ArrayPoint(3) ;

    comparator = new PointComparator(true) ;
    comparator.compare(point1, point2);
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheFirstPointIsBetterThanTheSecondOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 2.0);

    boolean maximization = true ;
    comparator = new PointComparator(maximization) ;

    assertEquals(-1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareReturnOneIfTheSecondPointIsBetterThanTheFirstOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 5.0);

    boolean maximization = true ;
    comparator = new PointComparator(maximization) ;

    assertEquals(1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 3.0);

    boolean maximization = true ;
    comparator = new PointComparator(maximization) ;

    assertEquals(0, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMinimizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 3.0);

    boolean maximization = false ;
    comparator = new PointComparator(maximization) ;

    assertEquals(0, comparator.compare(point1, point2)) ;
  }
}
