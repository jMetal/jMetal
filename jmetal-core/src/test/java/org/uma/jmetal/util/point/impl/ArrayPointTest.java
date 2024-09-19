package org.uma.jmetal.util.point.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.point.Point;

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

    double[] pointDimensions = point.values() ;

    double[] expectedArray = {0.0, 0.0, 0.0, 0.0, 0.0} ;
    assertArrayEquals(expectedArray, pointDimensions, EPSILON);
  }

  @Test
  public void shouldConstructAPointFromOtherPointReturnAnIdenticalPoint() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.value(0, 1.0);
    point.value(1, -2.0);
    point.value(2, 45.5);
    point.value(3, -323.234);
    point.value(4, 2344234.23424);

    Point newPoint = new ArrayPoint(point) ;

    double[] expectedArray = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;
    double[] newPointDimensions = point.values() ;

    assertArrayEquals(expectedArray, newPointDimensions, EPSILON);

    assertEquals(point, newPoint) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldConstructAPointFromANullPointRaiseAnException() {
    Point point = null ;

    new ArrayPoint(point) ;
  }

  @Test
  public void shouldConstructFromASolutionReturnTheCorrectPoint() {
    DoubleProblem problem = new FakeDoubleProblem(3, 3, 0) ;
    DoubleSolution solution = problem.createSolution() ;
    solution.objectives()[0] = 0.2 ;
    solution.objectives()[1] = 234.23 ;
    solution.objectives()[2] = -234.2356 ;

    Point point = new ArrayPoint(solution.objectives()) ;

    double[] expectedArray = {0.2, 234.23, -234.2356} ;
    double[] pointDimensions = point.values() ;

    assertArrayEquals(expectedArray, pointDimensions, EPSILON);
  }

  @Test
  public void shouldConstructFromArrayReturnTheCorrectPoint() {
    double[] array = {0.2, 234.23, -234.2356} ;
    Point point = new ArrayPoint(array) ;

    double[] storedValues = point.values() ;

    assertArrayEquals(array, storedValues, EPSILON);
  }

  @Test (expected = NullParameterException.class)
  public void shouldConstructFromNullArrayRaiseAnException() {
    double[] array = null ;

    new ArrayPoint(array) ;
  }

  @Test
  public void shouldGetNumberOfDimensionsReturnTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertEquals(dimension, point.dimension());
  }

  @Test
  public void shouldGetValuesReturnTheCorrectValues() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, 2344234.23424} ;

    Point point = new ArrayPoint(dimension) ;
    point.set(array);

    assertArrayEquals(array, point.values(), EPSILON);
  }

  @Test
  public void shouldGetDimensionValueReturnTheCorrectValue() {
    int dimension = 5 ;
    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    Point point = new ArrayPoint(dimension) ;
    point.set(array);

    assertEquals(1.0, point.value(0), EPSILON) ;
    assertEquals(-2.0, point.value(1), EPSILON) ;
    assertEquals(45.5, point.value(2), EPSILON) ;
    assertEquals(-323.234, point.value(3), EPSILON) ;
    assertEquals(Double.MAX_VALUE, point.value(4), EPSILON) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldGetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.value(-1) ;
    point.value(5) ;
  }

  @Test
  public void shouldSetDimensionValueAssignTheCorrectValue() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.value(0, 1.0);
    point.value(1, -2.0);
    point.value(2, 45.5);
    point.value(3, -323.234);
    point.value(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertArrayEquals(array, point.values(), EPSILON);
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldSetDimensionValueWithInvalidIndexesRaiseAnException() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.value(-1, 2.2) ;
    point.value(5, 2.0) ;
  }

  @Test
  public void shouldEqualsReturnTrueIfThePointsAreIdentical() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.value(0, 1.0);
    point.value(1, -2.0);
    point.value(2, 45.5);
    point.value(3, -323.234);
    point.value(4, Double.MAX_VALUE);

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
    point.value(0, 1.0);
    point.value(1, -2.0);
    point.value(2, 45.5);
    point.value(3, -323.234);
    point.value(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;
    newPoint.value(0, -1.0);

    assertFalse(point.equals(newPoint));
  }

 @Test
 public void shouldSetAssignTheRightValues() {
   Point point = new ArrayPoint(new double[]{2, 3, 3}) ;

   point.set(new double[]{5, 6, 7}) ;
   assertEquals(5, point.value(0), EPSILON);
   assertEquals(6, point.value(1), EPSILON);
   assertEquals(7, point.value(2), EPSILON);
 }

  @Test
  public void shouldEqualsReturnFalseIfThePointIsNull() {
    int dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(null));
  }

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

    point.value(0, 1.0);
    point.value(1, -2.0);
    point.value(2, 45.5);
    point.value(3, -323.234);
    point.value(4, Double.MAX_VALUE);

    double[] array = {1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE} ;

    assertEquals(Arrays.hashCode(array), point.hashCode());
  }
}
