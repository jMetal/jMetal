package org.uma.jmetal.experimental.qualityIndicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.qualityIndicator.impl.SetCoverage;
import org.uma.jmetal.util.checking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SetCoverageTest {
  private static final double EPSILON = 0.0000000000001 ;

  private SetCoverage setCoverage ;

  @BeforeEach
  public void setup() {
    setCoverage = new SetCoverage() ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFirstFrontIsNull() {
    Assertions.assertThrows(NullParameterException.class, () -> setCoverage.compute(null, new double[][]{}));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    Assertions.assertThrows(NullParameterException.class, () -> setCoverage.compute(new double[][]{}, null));
  }

  @Test
  public void shouldExecuteReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
   double[][] frontA = {{10.0, 12.0, -1.0}} ;
   double[][] frontB = {{10.0, 12.0, -1.0}} ;

    Assertions.assertEquals(0.0, setCoverage.compute(frontA, frontB), EPSILON);
  }

  @Test
  public void shouldExecuteReturnZeroIfBothFrontsAreEmpty() {
    double[][] frontA = {{}} ;
    double[][] frontB = {{}} ;

    Assertions.assertEquals(0.0, setCoverage.compute(frontA, frontB), EPSILON);
  }

  @Test
  public void shouldExecuteReturnOneIfTheSecondFrontIsEmpty() {
    double[][] frontA = {{10.0, 12.0}} ;
    double[][] frontB = {{}} ;

    Assertions.assertEquals(1.0, setCoverage.compute(frontA, frontB), EPSILON);
  }

  /**
   * Given a frontA with point [2,3] and a frontB with point [1,2], the value of the
   * setCoverage(frontA, frontB) == 0 and setCoverage(frontB, frontA) == 1
   */
  @Test
  public void shouldExecuteReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    double[][] frontA = {{2.0, 3.0}} ;
    double[][] frontB = {{1.0, 2.0}} ;

    Assertions.assertEquals(0.0, setCoverage.compute(frontA, frontB), EPSILON);
    Assertions.assertEquals(1.0, setCoverage.compute(frontB, frontA), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.0,3.0], [3.5, 1.0], the value of setCoverage(frontA, frontB) == 1/3 and
   * setCoverage(frontB, frontA) == 1/3
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    double[][] frontA = {{0.0, 6.0}, {2.0, 3.0}, {4.0, 2.0}} ;
    double[][] frontB = {{1.0, 7.0}, {2.0, 3.0}, {3.5, 1.0}} ;

    Assertions.assertEquals(1.0/3.0, setCoverage.compute(frontA, frontB), EPSILON);
    Assertions.assertEquals(1.0/3.0, setCoverage.compute(frontB, frontA), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.5,3.0], [5.0, 2.5], the value of setCoverage(frontA, frontB) == 1 and
   * setCoverage(frontB, frontA) == 0
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    double[][] frontA = {{0.0, 6.0}, {2.0, 3.0}, {4.0, 2.0}} ;
    double[][] frontB = {{1.0, 7.0}, {2.5, 3.0}, {5.0, 2.5}} ;

    Assertions.assertEquals(1.0, setCoverage.compute(frontA, frontB), EPSILON);
    Assertions.assertEquals(0.0, setCoverage.compute(frontB, frontA), EPSILON);
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
    point1.setValue(0, 0.0);
    point1.setValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, 2.0);
    point2.setValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setValue(0, 4.0);
    point3.setValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setValue(0, 1.0);
    point4.setValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setValue(0, 2.5);
    point5.setValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setValue(0, 5.0);
    point6.setValue(1, 2.5);

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
    Assertions.assertEquals("SC", setCoverage.getName());
  }
}
