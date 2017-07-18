package org.uma.jmetal.qualityindicator.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SetCoverageTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private SetCoverage setCoverage ;

  @Before public void setup() {
    setCoverage = new SetCoverage() ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFirstFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The first front is null"));

    List<Solution<DoubleSolution>> frontA = null ;
    List<Solution<DoubleSolution>> frontB = new ArrayList<>() ;

    setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(frontA, frontB));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The second front is null"));

    List<Solution<DoubleSolution>> frontA = new ArrayList<>() ;
    List<Solution<DoubleSolution>> frontB = null ;

    setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(frontA, frontB));
  }

  @Test
  public void shouldExecuteReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 3 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);
    point1.setDimensionValue(2, -1.0);

    frontA.setPoint(0, point1);
    frontB.setPoint(0, point1);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(0.0, result.getLeft(), EPSILON);
  }

  @Test
  public void shouldExecuteReturnZeroIfBothFrontsAreEmpty() {
    int numberOfPoints = 0 ;
    int numberOfDimensions = 3 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(0.0, result.getLeft(), EPSILON);
    assertEquals(0.0, result.getRight(), EPSILON);
  }

  @Test
  public void shouldExecuteReturnOneIfTheSecondFrontIsEmpty() {
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(1, numberOfDimensions);
    Front frontB = new ArrayFront(0, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);

    frontA.setPoint(0, point1);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(1.0, result.getLeft(), EPSILON);
  }

  /**
   * Given a frontA with point [2,3] and a frontB with point [1,2], the value of the
   * setCoverage(frontA, frontB) == 0 and setCoverage(frontB, frontA) == 1
   */
  @Test
  public void shouldExecuteReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 2.0);
    point1.setDimensionValue(1, 3.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontB.setPoint(0, point2);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(0.0, result.getLeft(), EPSILON);
    assertEquals(1.0, result.getRight(), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.0,3.0], [3.5, 1.0], the value of setCoverage(frontA, frontB) == 1/3 and
   * setCoverage(frontB, frontA) == 1/3
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.0);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 3.5);
    point6.setDimensionValue(1, 1.0);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(1.0/3.0, result.getLeft(), EPSILON);
    assertEquals(1.0/3.0, result.getRight(), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.5,3.0], [5.0, 2.5], the value of setCoverage(frontA, frontB) == 1 and
   * setCoverage(frontB, frontA) == 0
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.5);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 5.0);
    point6.setDimensionValue(1, 2.5);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    Pair<Double, Double> result = setCoverage.evaluate(new ImmutablePair<List<? extends Solution<?>>, List<? extends Solution<?>>>(
        FrontUtils.convertFrontToSolutionList(frontA),
        FrontUtils.convertFrontToSolutionList(frontB))) ;

    assertEquals(1.0, result.getLeft(), EPSILON);
    assertEquals(0.0, result.getRight(), EPSILON);
  }

  /**
   * The same case as shouldExecuteReturnTheCorrectValueCaseB() but using solution lists
   */
  /*
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseC() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.5);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 5.0);
    point6.setDimensionValue(1, 2.5);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    List<PointSolution> listA = FrontUtils.convertFrontToSolutionList(frontA) ;
    List<PointSolution> listB = FrontUtils.convertFrontToSolutionList(frontB) ;

    assertEquals(1.0, setCoverage.execute(listA, listB), EPSILON);
    assertEquals(0.0, setCoverage.execute(listB, listA), EPSILON);
  }
*/
  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    assertEquals("SC", setCoverage.getName());
  }
}
