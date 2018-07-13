package org.uma.jmetal.util.point.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayPointTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Test
  public void shouldConstructAPointOfAGivenDimension() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    double[] pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    double[] expectedArray = {0.0, 0.0, 0.0, 0.0, 0.0} ;
    assertArrayEquals(expectedArray, pointDimensions, EPSILON); ;
  }

  @Test
  public void shouldConstructAPointFromOtherPointReturnAnIdenticalPoint() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setDimensionValue(0, 1.0);
    point.setDimensionValue(1, -2.0);
    point.setDimensionValue(2, 45.5);
    point.setDimensionValue(3, -323.234);
    point.setDimensionValue(4, 2344234.23424);

    Point newPoint = new ArrayPoint(point) ;

    double[] expectedArray = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;
    double[] newPointDimensions = (double[])ReflectionTestUtils.getField(newPoint, "point");

    assertArrayEquals(expectedArray, newPointDimensions, EPSILON);

    assertEquals(point, newPoint) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructAPointFromANullPointRaiseAnException() {
    Point point = null ;

    new ArrayPoint(point) ;
  }

  @Test
  public void shouldConstructFromASolutionReturnTheCorrectPoint() {
    Solution<?> solution = Mockito.mock(Solution.class) ;

    Mockito.when(solution.getNumberOfObjectives()).thenReturn(3) ;
    Mockito.when(solution.getObjective(0)).thenReturn(0.2) ;
    Mockito.when(solution.getObjective(1)).thenReturn(234.23) ;
    Mockito.when(solution.getObjective(2)).thenReturn(-234.2356) ;

    Point point = new ArrayPoint(solution) ;

    double[] expectedArray = {0.2, 234.23, -234.2356} ;
    double[] pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(expectedArray, pointDimensions, EPSILON);

    Mockito.verify(solution).getNumberOfObjectives() ;
    Mockito.verify(solution, Mockito.times(3)).getObjective(Mockito.anyInt());
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructAPointFromANullSolutionRaiseAnException() {
    Solution<?> solution = null ;

    new ArrayPoint(solution) ;
  }

  @Test
  public void shouldConstructFromArrayReturnTheCorrectPoint() {
    double[] array = {0.2, 234.23, -234.2356} ;
    Point point = new ArrayPoint(array) ;

    double[] storedValues = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(array, storedValues, EPSILON);
  }

  @Test (expected = JMetalException.class)
  public void shouldConstructFromNullArrayRaiseAnException() {
    double[] array = null ;

    new ArrayPoint(array) ;
  }

  @Test
  public void shouldGetNumberOfDimensionsReturnTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertEquals(dimension, point.getNumberOfDimensions());
  }

  @Test
  public void shouldGetValuesReturnTheCorrectValues() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertArrayEquals(array, point.getValues(), EPSILON);
  }

  @Test
  public void shouldGetDimensionValueReturnTheCorrectValue() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertEquals(1.0, point.getDimensionValue(0), EPSILON) ;
    assertEquals(-2.0, point.getDimensionValue(1), EPSILON) ;
    assertEquals(45.5, point.getDimensionValue(2), EPSILON) ;
    assertEquals(-323.234, point.getDimensionValue(3), EPSILON) ;
    assertEquals(Double.MAX_VALUE, point.getDimensionValue(4), EPSILON) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldGetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.getDimensionValue(-1) ;
    point.getDimensionValue(5) ;
  }

  @Test
  public void shouldSetDimensionValueAssignTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setDimensionValue(0, 1.0);
    point.setDimensionValue(1, -2.0);
    point.setDimensionValue(2, 45.5);
    point.setDimensionValue(3, -323.234);
    point.setDimensionValue(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertArrayEquals(array, (double[])ReflectionTestUtils.getField(point, "point"), EPSILON);
  }

  @Test (expected = JMetalException.class)
  public void shouldSetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setDimensionValue(-1, 2.2) ;
    point.setDimensionValue(5, 2.0) ;
  }

  @Test
  public void shouldEqualsReturnTrueIfThePointsAreIdentical() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setDimensionValue(0, 1.0);
    point.setDimensionValue(1, -2.0);
    point.setDimensionValue(2, 45.5);
    point.setDimensionValue(3, -323.234);
    point.setDimensionValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;

    assertTrue(point.equals(newPoint));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheTwoPointsAreTheSame() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertTrue(point.equals(point));
  }


  @Test
  public void shouldEqualsReturnFalseIfThePointsAreNotIdentical() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setDimensionValue(0, 1.0);
    point.setDimensionValue(1, -2.0);
    point.setDimensionValue(2, 45.5);
    point.setDimensionValue(3, -323.234);
    point.setDimensionValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;
    newPoint.setDimensionValue(0, -1.0);

    assertFalse(point.equals(newPoint));
  }

  @Test
  public void shouldEqualsReturnFalseIfThePointIsNull() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(null));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void shouldEqualsReturnFalseIfTheClassIsNotAPoint() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(new String("")));
  }

  @Test
  public void shouldHashCodeReturnTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setDimensionValue(0, 1.0);
    point.setDimensionValue(1, -2.0);
    point.setDimensionValue(2, 45.5);
    point.setDimensionValue(3, -323.234);
    point.setDimensionValue(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertEquals(Arrays.hashCode(array), point.hashCode());
  }
}
