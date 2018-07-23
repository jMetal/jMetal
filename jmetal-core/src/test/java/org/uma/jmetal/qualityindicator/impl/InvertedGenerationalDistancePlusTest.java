package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.PointSolution;

import java.io.FileNotFoundException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InvertedGenerationalDistancePlusTest {

  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference pareto front is null"));

    Front front = null ;

    new InvertedGenerationalDistancePlus<DoubleSolution>(front) ;
  }

  @Test (expected = FileNotFoundException.class)
  public void shouldConstructorRaiseAnExceptionIfFileNameIsNull() throws FileNotFoundException {
    new InvertedGenerationalDistancePlus<>("nonExistingFile") ;
  }

  @Test
  public void shouldEvaluateRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front approximation is null"));

    Front front = new ArrayFront(0, 0) ;

    InvertedGenerationalDistancePlus<DoubleSolution> igdPlus =
        new InvertedGenerationalDistancePlus<DoubleSolution>(front) ;
    igdPlus.evaluate(null) ;
  }

  @Test
  public void shouldEvaluateReturnZeroIfTheFrontAndTheReferenceFrontContainsTheSamePoints() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 4.0);
    point1.setValue(1, 10.0);

    frontApproximation.setPoint(0, point1);
    paretoFront.setPoint(0, point1);

    InvertedGenerationalDistancePlus<PointSolution> igdPlus =
        new InvertedGenerationalDistancePlus<PointSolution>(paretoFront) ;

    assertEquals(0.0, igdPlus.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)),
        EPSILON);
  }

  @Test
  public void shouldEvaluateReturnTheCorrectValueCaseA() {
    int numberOfPointsOfTheParetoFront = 3 ;
    int numberOfPointsOfTheFront = 3 ;
    int numberOfDimensions = 2 ;

    Front frontApproximation = new ArrayFront(numberOfPointsOfTheFront, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPointsOfTheParetoFront, numberOfDimensions);

    Point [] points ;
    points = new ArrayPoint[numberOfPointsOfTheParetoFront] ;
    for (int i = 0; i < numberOfPointsOfTheParetoFront; i++) {
      points[i] = new ArrayPoint(numberOfDimensions) ;
    }

    points[0].setValue(0, 0.1);
    points[0].setValue(1, 0.7);
    points[1].setValue(0, 0.2);
    points[1].setValue(1, 0.3);
    points[2].setValue(0, 0.6);
    points[2].setValue(1, 0.2);

    paretoFront.setPoint(0, points[0]);
    paretoFront.setPoint(1, points[1]);
    paretoFront.setPoint(2, points[2]);

    points = new ArrayPoint[numberOfPointsOfTheFront] ;
    for (int i = 0; i < numberOfPointsOfTheFront; i++) {
      points[i] = new ArrayPoint(numberOfDimensions) ;
    }

    points[0].setValue(0, 0.2);
    points[0].setValue(1, 0.5);
    points[1].setValue(0, 0.3);
    points[1].setValue(1, 0.4);
    points[2].setValue(0, 0.4);
    points[2].setValue(1, 0.3);

    frontApproximation.setPoint(0, points[0]);
    frontApproximation.setPoint(1, points[1]);
    frontApproximation.setPoint(2, points[2]);

    InvertedGenerationalDistancePlus<PointSolution> igdPlus =
        new InvertedGenerationalDistancePlus<PointSolution>(paretoFront) ;

    assertEquals((2.0 * Math.sqrt(0.01) + Math.sqrt(0.02))/3.0,
        igdPlus.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)),
        EPSILON);
  }

  @Test
  public void shouldEvaluateReturnTheCorrectValueCaseB() {
    int numberOfPointsOfTheParetoFront = 3 ;
    int numberOfPointsOfTheFront = 3 ;
    int numberOfDimensions = 2 ;

    Front frontApproximation = new ArrayFront(numberOfPointsOfTheFront, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPointsOfTheParetoFront, numberOfDimensions);

    Point [] points ;
    points = new ArrayPoint[numberOfPointsOfTheParetoFront] ;
    for (int i = 0; i < numberOfPointsOfTheParetoFront; i++) {
      points[i] = new ArrayPoint(numberOfDimensions) ;
    }

    points[0].setValue(0, 0.1);
    points[0].setValue(1, 0.7);
    points[1].setValue(0, 0.2);
    points[1].setValue(1, 0.3);
    points[2].setValue(0, 0.6);
    points[2].setValue(1, 0.2);

    paretoFront.setPoint(0, points[0]);
    paretoFront.setPoint(1, points[1]);
    paretoFront.setPoint(2, points[2]);

    points = new ArrayPoint[numberOfPointsOfTheFront] ;
    for (int i = 0; i < numberOfPointsOfTheFront; i++) {
      points[i] = new ArrayPoint(numberOfDimensions) ;
    }

    points[0].setValue(0, 0.3);
    points[0].setValue(1, 0.7);
    points[1].setValue(0, 0.5);
    points[1].setValue(1, 0.6);
    points[2].setValue(0, 0.7);
    points[2].setValue(1, 0.4);

    frontApproximation.setPoint(0, points[0]);
    frontApproximation.setPoint(1, points[1]);
    frontApproximation.setPoint(2, points[2]);

    InvertedGenerationalDistancePlus<PointSolution> igdPlus =
        new InvertedGenerationalDistancePlus<PointSolution>(paretoFront) ;

    assertEquals((0.2 + Math.sqrt(0.01+0.16) + Math.sqrt(0.01+0.04))/3.0,
        igdPlus.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)),
        EPSILON);
  }
}
