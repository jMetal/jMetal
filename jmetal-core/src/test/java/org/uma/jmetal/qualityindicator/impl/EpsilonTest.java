package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.PointSolution;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class EpsilonTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference pareto front is null"));

    Front referenceFront = null ;
    new Epsilon<PointSolution>(referenceFront) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The pareto front approximation list is null"));

    Front referenceFront = new ArrayFront() ;

    Epsilon<PointSolution> epsilon = new Epsilon<PointSolution>(referenceFront) ;
    List<PointSolution> list = null ;
    epsilon.evaluate(list) ;
  }

  @Test
  public void shouldExecuteReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 3 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 10.0);
    point1.setValue(1, 12.0);
    point1.setValue(2, -1.0);

    frontApproximation.setPoint(0, point1);
    referenceFront.setPoint(0, point1);

    QualityIndicator<List<PointSolution>, Double> epsilon =
        new Epsilon<PointSolution>(referenceFront) ;

    List<PointSolution> front = FrontUtils.convertFrontToSolutionList(frontApproximation) ;

    assertEquals(0.0, epsilon.evaluate(front), EPSILON);
  }

  /**
   * Given a front with point [2,3] and a Pareto front with point [1,2], the value of the
   * epsilon indicator is 1
   */
  @Test
  public void shouldExecuteReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 2.0);
    point1.setValue(1, 3.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 1.0);
    point2.setValue(1, 2.0);

    frontApproximation.setPoint(0, point1);
    referenceFront.setPoint(0, point2);

    QualityIndicator<List<PointSolution>, Double> epsilon =
        new Epsilon<PointSolution>(referenceFront) ;

    List<PointSolution> front = FrontUtils.convertFrontToSolutionList(frontApproximation) ;

    assertEquals(1.0, epsilon.evaluate(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [2.0,3.0],[3.0,2.0] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 1
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 1.5);
    point1.setValue(1, 4.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 2.0);
    point2.setValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setValue(0, 3.0);
    point3.setValue(1, 2.0);

    frontApproximation.setPoint(0, point1);
    frontApproximation.setPoint(1, point2);
    frontApproximation.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setValue(0, 1.0);
    point4.setValue(1, 3.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setValue(0, 1.5);
    point5.setValue(1, 2.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setValue(0, 2.0);
    point6.setValue(1, 1.5);

    referenceFront.setPoint(0, point4);
    referenceFront.setPoint(1, point5);
    referenceFront.setPoint(2, point6);

    QualityIndicator<List<PointSolution>, Double> epsilon =
        new Epsilon<PointSolution>(referenceFront) ;

    List<PointSolution> front = FrontUtils.convertFrontToSolutionList(frontApproximation) ;
    assertEquals(1.0, epsilon.evaluate(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [1.5,2.0],[2.0,1.5] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 0.5
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 1.5);
    point1.setValue(1, 4.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 1.5);
    point2.setValue(1, 2.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setValue(0, 2.0);
    point3.setValue(1, 1.5);

    frontApproximation.setPoint(0, point1);
    frontApproximation.setPoint(1, point2);
    frontApproximation.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setValue(0, 1.0);
    point4.setValue(1, 3.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setValue(0, 1.5);
    point5.setValue(1, 2.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setValue(0, 2.0);
    point6.setValue(1, 1.5);

    referenceFront.setPoint(0, point4);
    referenceFront.setPoint(1, point5);
    referenceFront.setPoint(2, point6);

    QualityIndicator<List<PointSolution>, Double> epsilon =
        new Epsilon<PointSolution>(referenceFront) ;
    List<PointSolution> front = FrontUtils.convertFrontToSolutionList(frontApproximation) ;
    assertEquals(0.5, epsilon.evaluate(front), EPSILON);
  }

  /**
   * The same case as shouldExecuteReturnTheCorrectValueCaseB() but using list of solutions
   */
  /*
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseC() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 1.5);
    point1.setValue(1, 4.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 1.5);
    point2.setValue(1, 2.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setValue(0, 2.0);
    point3.setValue(1, 1.5);

    frontApproximation.setPoint(0, point1);
    frontApproximation.setPoint(1, point2);
    frontApproximation.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setValue(0, 1.0);
    point4.setValue(1, 3.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setValue(0, 1.5);
    point5.setValue(1, 2.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setValue(0, 2.0);
    point6.setValue(1, 1.5);

    referenceFront.setPoint(0, point4);
    referenceFront.setPoint(1, point5);
    referenceFront.setPoint(2, point6);

    List<PointSolution> listA = FrontUtils.convertFrontToSolutionList(frontApproximation) ;
    List<PointSolution> listB = FrontUtils.convertFrontToSolutionList(referenceFront) ;

    assertEquals(0.5, epsilon.execute(listA, listB), EPSILON);
  }
*/
  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    QualityIndicator<?, Double> epsilon = new Epsilon<PointSolution>(new ArrayFront()) ;

    assertEquals("EP", epsilon.getName());
  }
}
