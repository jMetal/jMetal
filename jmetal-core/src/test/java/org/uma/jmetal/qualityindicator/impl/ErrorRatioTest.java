package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ErrorRatioTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The Pareto front approximation is null"));

    Front referenceFront = null ;
    new ErrorRatio<List<DoubleSolution>>(referenceFront) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontApproximationListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    QualityIndicator<List<DoubleSolution>, Double> errorRatio =
        new ErrorRatio<List<DoubleSolution>>(new ArrayFront(0, 0)) ;

    List<DoubleSolution> list = null ;
    errorRatio.evaluate(list) ;
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

    QualityIndicator<List<PointSolution>, Double> errorRatio =
        new ErrorRatio<List<PointSolution>>(referenceFront) ;

    assertEquals(0.0, errorRatio.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }

  @Test
  public void shouldExecuteReturnOneIfTheFrontsContainADifferentPoint() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 3 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front referenceFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 10.0);
    point1.setValue(1, 12.0);
    point1.setValue(2, -1.0);

    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 3.0);
    point2.setValue(1, 5.0);
    point2.setValue(2, -2.0);

    frontApproximation.setPoint(0, point1);
    referenceFront.setPoint(0, point2);

    QualityIndicator<List<PointSolution>, Double> errorRatio =
        new ErrorRatio<List<PointSolution>>(referenceFront) ;

    assertEquals(1.0, errorRatio.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [1.5,2.0],[2.0,1.5] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 2/3
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPoints, numberOfDimensions);

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

    paretoFront.setPoint(0, point4);
    paretoFront.setPoint(1, point5);
    paretoFront.setPoint(2, point6);

    QualityIndicator<List<PointSolution>, Double> errorRatio =
        new ErrorRatio<List<PointSolution>>(paretoFront) ;

    assertEquals(1.0/numberOfPoints, errorRatio.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }

  /**
   * Given a list with solutions [1.5,3.0], [4.0,2.0] and another lists with solutions
   * [-1.0,-1.0], [0.0,0.0], the value of the epsilon indicator is 1
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    DoubleProblem problem = new MockDoubleProblem() ;

    List<DoubleSolution> frontApproximation = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;

    frontApproximation.get(0).setObjective(0, 1.5);
    frontApproximation.get(0).setObjective(1, 3.0);
    frontApproximation.get(1).setObjective(0, 4.0);
    frontApproximation.get(1).setObjective(1, 2.0);

    List<DoubleSolution> paretoFront = Arrays.asList(problem.createSolution(),
        problem.createSolution()) ;

    paretoFront.get(0).setObjective(0, -1.0);
    paretoFront.get(0).setObjective(1, -1.0);
    paretoFront.get(1).setObjective(0, 0.0);
    paretoFront.get(1).setObjective(1, 0.0);

    QualityIndicator<List<DoubleSolution>, Double> errorRatio =
        new ErrorRatio<List<DoubleSolution>>(new ArrayFront(paretoFront)) ;

    assertEquals(1.0, errorRatio.evaluate(frontApproximation), EPSILON);
  }

  @Test
  public void shouldGetNameReturnTheCorrectValue() throws FileNotFoundException {
    QualityIndicator<?, Double> errorRatio = new ErrorRatio<List<DoubleSolution>>(new ArrayFront()) ;
    assertEquals("ER", errorRatio.getName());
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem() {
      setNumberOfVariables(2);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
