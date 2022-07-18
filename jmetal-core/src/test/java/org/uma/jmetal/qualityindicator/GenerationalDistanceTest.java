package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class GenerationalDistanceTest {

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new GenerationalDistance(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    var referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new GenerationalDistance(referenceFront).compute(front));
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
   * @Test public void shouldExecuteReturnTheCorrectValue() {
   * int numberOfDimensions = 2 ;
   * Front frontApproximation = new ArrayFront(3, numberOfDimensions);
   * Front paretoFront = new ArrayFront(4, numberOfDimensions);
   * <p>
   * Point point1 = new ArrayPoint(numberOfDimensions) ;
   * point1.setValue(0, 2.5);
   * point1.setValue(1, 9.0);
   * <p>
   * Point point2 = new ArrayPoint(numberOfDimensions) ;
   * point2.setValue(0, 3.0);
   * point2.setValue(1, 6.0);
   * <p>
   * Point point3 = new ArrayPoint(numberOfDimensions) ;
   * point3.setValue(0, 5.0);
   * point3.setValue(1, 4.0);
   * <p>
   * frontApproximation.setPoint(0, point1);
   * frontApproximation.setPoint(1, point2);
   * frontApproximation.setPoint(2, point3);
   * <p>
   * Point point4 = new ArrayPoint(numberOfDimensions) ;
   * point4.setValue(0, 1.5);
   * point4.setValue(1, 10.0);
   * <p>
   * Point point5 = new ArrayPoint(numberOfDimensions) ;
   * point5.setValue(0, 2.0);
   * point5.setValue(1, 8.0);
   * <p>
   * Point point6 = new ArrayPoint(numberOfDimensions) ;
   * point6.setValue(0, 3.0);
   * point6.setValue(1, 6.0);
   * <p>
   * Point point7 = new ArrayPoint(numberOfDimensions) ;
   * point7.setValue(0, 4.0);
   * point7.setValue(1, 4.0);
   * <p>
   * paretoFront.setPoint(0, point4);
   * paretoFront.setPoint(1, point5);
   * paretoFront.setPoint(2, point6);
   * paretoFront.setPoint(3, point7);
   * <p>
   * QualityIndicator gd = new GenerationalDistance(paretoFront) ;
   * <p>
   * assertEquals(0.5, (Double)gd.runAlgorithm(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
   * }
   */

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    Assertions.assertEquals("GD", new GenerationalDistance().getName());
  }

}
