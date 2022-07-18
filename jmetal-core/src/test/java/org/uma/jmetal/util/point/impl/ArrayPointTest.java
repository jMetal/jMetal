package org.uma.jmetal.util.point.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
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
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    var pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    var expectedArray = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    assertArrayEquals(expectedArray, pointDimensions, EPSILON); ;
  }

  @Test
  public void shouldConstructAPointFromOtherPointReturnAnIdenticalPoint() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, 2344234.23424);

    Point newPoint = new ArrayPoint(point) ;

    var expectedArray = new double[]{1.0, -2.0, 45.5, -323.234, 2344234.23424};
    var newPointDimensions = (double[])ReflectionTestUtils.getField(newPoint, "point");

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
    var solution = problem.createSolution() ;
    solution.objectives()[0] = 0.2 ;
    solution.objectives()[1] = 234.23 ;
    solution.objectives()[2] = -234.2356 ;

    Point point = new ArrayPoint(solution.objectives()) ;

    var expectedArray = new double[]{0.2, 234.23, -234.2356};
    var pointDimensions = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(expectedArray, pointDimensions, EPSILON);

//    Mockito.verify(solution).objectives().length ;
//    Mockito.verify(solution, Mockito.times(3)).getObjective(Mockito.anyInt());
  }

  @Test
  public void shouldConstructFromArrayReturnTheCorrectPoint() {
    var array = new double[]{0.2, 234.23, -234.2356};
    Point point = new ArrayPoint(array) ;

    var storedValues = (double[])ReflectionTestUtils.getField(point, "point");

    assertArrayEquals(array, storedValues, EPSILON);
  }

  @Test (expected = NullParameterException.class)
  public void shouldConstructFromNullArrayRaiseAnException() {
    double[] array = null ;

    new ArrayPoint(array) ;
  }

  @Test
  public void shouldGetNumberOfDimensionsReturnTheCorrectValue() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertEquals(dimension, point.getDimension());
  }

  @Test
  public void shouldGetValuesReturnTheCorrectValues() {
    var dimension = 5 ;
    var array = new double[]{1.0, -2.0, 45.5, -323.234, 2344234.23424};

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertArrayEquals(array, point.getValues(), EPSILON);
  }

  @Test
  public void shouldGetDimensionValueReturnTheCorrectValue() {
    var dimension = 5 ;
    var array = new double[]{1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE};

    Point point = new ArrayPoint(dimension) ;
    ReflectionTestUtils.setField(point, "point", array);

    assertEquals(1.0, point.getValue(0), EPSILON) ;
    assertEquals(-2.0, point.getValue(1), EPSILON) ;
    assertEquals(45.5, point.getValue(2), EPSILON) ;
    assertEquals(-323.234, point.getValue(3), EPSILON) ;
    assertEquals(Double.MAX_VALUE, point.getValue(4), EPSILON) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldGetDimensionValueWithInvalidIndexesRaiseAnException() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.getValue(-1) ;
    point.getValue(5) ;
  }

  @Test
  public void shouldSetDimensionValueAssignTheCorrectValue() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    var array = new double[]{1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE};

    assertArrayEquals(array, (double[])ReflectionTestUtils.getField(point, "point"), EPSILON);
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldSetDimensionValueWithInvalidIndexesRaiseAnException() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setValue(-1, 2.2) ;
    point.setValue(5, 2.0) ;
  }

  @Test
  public void shouldEqualsReturnTrueIfThePointsAreIdentical() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;

    assertTrue(point.equals(newPoint));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheTwoPointsAreTheSame() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertTrue(point.equals(point));
  }


  @Test
  public void shouldEqualsReturnFalseIfThePointsAreNotIdentical() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;
    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    Point newPoint = new ArrayPoint(point) ;
    newPoint.setValue(0, -1.0);

    assertFalse(point.equals(newPoint));
  }

 @Test
 public void shouldSetAssignTheRightValues() {
   Point point = new ArrayPoint(new double[]{2, 3, 3}) ;

   point.set(new double[]{5, 6, 7}) ;
   assertEquals(5, point.getValue(0), EPSILON);
   assertEquals(6, point.getValue(1), EPSILON);
   assertEquals(7, point.getValue(2), EPSILON);
 }

  @Test
  public void shouldEqualsReturnFalseIfThePointIsNull() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(null));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheClassIsNotAPoint() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    assertFalse(point.equals(new String("")));
  }

  @Test
  public void shouldHashCodeReturnTheCorrectValue() {
    var dimension = 5 ;
    Point point = new ArrayPoint(dimension) ;

    point.setValue(0, 1.0);
    point.setValue(1, -2.0);
    point.setValue(2, 45.5);
    point.setValue(3, -323.234);
    point.setValue(4, Double.MAX_VALUE);

    var array = new double[]{1.0, -2.0, 45.5, -323.234, Double.MAX_VALUE};

    assertEquals(Arrays.hashCode(array), point.hashCode());
  }
}
