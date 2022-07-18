package org.uma.jmetal.qualityindicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class EpsilonTest {
  private static final double EPSILON = 0.0000000000001;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheReferenceFrontIsNull() {
    double[][] referenceFront = null;
    Assertions.assertThrows(NullParameterException.class, () -> new Epsilon(referenceFront));
  }

  @Test
  public void shouldComputeRaiseAnExceptionIfTheFrontIsNull() {
    var referenceFront = new double[0][0];
    double[][] front = null;
    Assertions.assertThrows(NullParameterException.class, () -> new Epsilon(referenceFront).compute(front));
  }

  @Test
  public void shouldComputeReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    double[] vector = {10, 12, -1};

    var front = new double[][]{vector};
    var referenceFront = new double[][]{vector};

    Assertions.assertEquals(0.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with point [2,3] and a Pareto front with point [1,2], the value of the
   * epsilon indicator is 1
   */
  @Test
  public void shouldComputeReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    var front = new double[][]{{2, 3}};
    var referenceFront = new double[][]{{1, 2}};

    Assertions.assertEquals(1.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [2.0,3.0],[3.0,2.0] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 1
   */
  @Test
  public void shouldComputeReturnTheCorrectValueCaseA() {
    var front = new double[][]{{1.5, 4.0}, {2.0, 3.0}, {3.0, 2.0}};
    var referenceFront = new double[][]{{1.0, 3.0}, {1.5, 2.0}, {2.0, 1.5}};

    Assertions.assertEquals(1.0, new Epsilon(referenceFront).compute(front), EPSILON);
  }

  /**
   * Given a front with points [1.5,4.0], [1.5,2.0],[2.0,1.5] and a Pareto front with points
   * [1.0,3.0], [1.5,2.0], [2.0, 1.5], the value of the epsilon indicator is 0.5
   */
  @Test
  public void shouldComputeReturnTheCorrectValueCaseB() {
    var front = new double[][]{{1.5, 4.0}, {1.5, 2.0}, {2.0, 1.5}};
    var referenceFront = new double[][]{{1.0, 3.0}, {1.5, 2.0}, {2.0, 1.5}};

    Assertions.assertEquals(0.5, new Epsilon(referenceFront).compute(front), EPSILON);
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
    Assertions.assertEquals("EP", new Epsilon().getName());
  }
}
