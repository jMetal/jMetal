package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class GenerationalDistanceTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front approximation is null"));

    Front front = new ArrayFront(0, 0) ;

    GenerationalDistance<DoubleSolution> gd = new GenerationalDistance<DoubleSolution>(front) ;
    gd.evaluate(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference pareto front is null"));

    Front front = null ;

    new GenerationalDistance<>(front) ;
  }
/*
  @Test
  public void shouldExecuteRaiseAndExceptionIfTheFrontsContainOnePointWhichIsTheSame() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Maximum and minimum values of index 0 are the same: 10"));

    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 10.0);
    point1.setValue(1, 12.0);

    frontApproximation.setPoint(0, point1);
    paretoFront.setPoint(0, point1);

    GenerationalDistance<List<DoubleSolution>> gd =
        new GenerationalDistance<List<DoubleSolution>>(paretoFront) ;

    assertEquals(0.0, gd.runAlgorithm(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }
  */
/**
  @Test
  public void shouldExecuteReturnTheCorrectValue() {
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(3, numberOfDimensions);
    Front paretoFront = new ArrayFront(4, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 2.5);
    point1.setValue(1, 9.0);

    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 3.0);
    point2.setValue(1, 6.0);

    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setValue(0, 5.0);
    point3.setValue(1, 4.0);

    frontApproximation.setPoint(0, point1);
    frontApproximation.setPoint(1, point2);
    frontApproximation.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setValue(0, 1.5);
    point4.setValue(1, 10.0);

    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setValue(0, 2.0);
    point5.setValue(1, 8.0);

    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setValue(0, 3.0);
    point6.setValue(1, 6.0);

    Point point7 = new ArrayPoint(numberOfDimensions) ;
    point7.setValue(0, 4.0);
    point7.setValue(1, 4.0);

    paretoFront.setPoint(0, point4);
    paretoFront.setPoint(1, point5);
    paretoFront.setPoint(2, point6);
    paretoFront.setPoint(3, point7);

    QualityIndicator gd = new GenerationalDistance(paretoFront) ;

    assertEquals(0.5, (Double)gd.runAlgorithm(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }
*/

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    //assertEquals("GD", generationalDistance.getName());
  }

}
