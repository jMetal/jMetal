package org.uma.jmetal.util.legacy.front.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontNormalizerTest {

  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheReferenceFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference front is null"));

    new FrontNormalizer((Front) null) ;
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheReferenceSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The reference front is null"));

    new FrontNormalizer((List<DoubleSolution>) null) ;
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheVectorOfMinimumValuesIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The array of minimum values is null"));

    new FrontNormalizer(null, new double[1]) ;
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheVectorOfMaximumValuesIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The array of maximum values is null"));

    new FrontNormalizer(new double[1], null) ;
  }

  @Test
  public void shouldFrontNormalizerContructorRaiseAnExceptionTheDimensionOfTheMaximumAndMinimumArrayIsNotEqual() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The length of the maximum array (2) "
        + "is different from the length of the minimum array (1)"));

    new FrontNormalizer(new double[1], new double[2]) ;
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionTheFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is null"));

    var frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    frontNormalizer.normalize((Front) null) ;
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is null"));

    var frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    frontNormalizer.normalize((List<DoubleSolution>) null) ;
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheFrontIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is empty"));

    Front front = new ArrayFront(0, 0);

    var frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    frontNormalizer.normalize(front) ;
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The list of solutions is empty"));

    List<DoubleSolution> solutionList = Collections.emptyList() ;

    var frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    frontNormalizer.normalize(solutionList) ;
  }


  @Test
  public void shouldNormalizeRaiseAnExceptionTheDimensionOfTheMaximumArrayPointsIsNotCorrect() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The length of the point dimensions (2) " +
        "is different from the length of the maximum array (4)"));

    var numberOfPointDimensions = 2 ;

    Front front = new ArrayFront(1, numberOfPointDimensions);

    var frontNormalizer = new FrontNormalizer(new double[4], new double[4]) ;
    frontNormalizer.normalize(front) ;
  }

  /**
   * Point: [2,4]
   * Maximum values: [4, 4]
   * Minimum values: [0, 0]
   * Result: [0.5, 1.0]
   */
  @Test
  public void shouldNormalizeReturnTheCorrectFrontIfThisContainsOnePoint() {
    var numberOfPoints = 1 ;
    var numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.setValue(0, 2);
    point.setValue(1, 4);

    front.setPoint(0, point);

    double[] minimum = {0, 0} ;
    double[] maximum = {4, 4} ;

    var frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    var normalizedFront = frontNormalizer.normalize(front) ;

    assertEquals(0.5, normalizedFront.getPoint(0).getValue(0), EPSILON) ;
    assertEquals(1.0, normalizedFront.getPoint(0).getValue(1), EPSILON) ;
  }

  /**
   * Point: [2,4]
   * Maximum values: [2, 4]
   * Minimum values: [2, 4]
   * Result: [0.5, 1.0]
   */
  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheMaxAndMinValuesAreTheSame() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Maximum and minimum values of index 0 are the same: 2"));

    var numberOfPoints = 1 ;
    var numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.setValue(0, 2);
    point.setValue(1, 4);

    front.setPoint(0, point);

    double[] minimum = {2, 4} ;
    double[] maximum = {2, 4} ;

    var frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    frontNormalizer.normalize(front) ;
  }

  /**
   * Points: [2,4], [-2, 3]
   * Maximum values: [6, 8]
   * Minimum values: [-10, 1]
   * Result: [0.5, 1.0], []
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfThisContainsTwoPoints() {
    var numberOfPoints = 2 ;
    var numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 2);
    point1.setValue(1, 4);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, -2);
    point2.setValue(1, 3);

    front.setPoint(0, point1);
    front.setPoint(1, point2);

    double[] minimum = {-10, 1} ;
    double[] maximum = {6, 8} ;

    var frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    var normalizedFront = frontNormalizer.normalize(front) ;

    assertEquals(0.75, normalizedFront.getPoint(0).getValue(0), EPSILON) ;
    assertEquals(3.0/7.0, normalizedFront.getPoint(0).getValue(1), EPSILON) ;
    assertEquals(0.5, normalizedFront.getPoint(1).getValue(0), EPSILON) ;
    assertEquals(2.0 / 7.0, normalizedFront.getPoint(1).getValue(1), EPSILON) ;
  }

  /**
   * Points: [2,4], [-2, 3]
   * Maximum values: [6, 8]
   * Minimum values: [-10, 1]
   * Result: [0.5, 1.0], []
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfTheSolutionListContainsTwoPoints() {
    var numberOfDimensions = 2 ;

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setValue(0, 2);
    point1.setValue(1, 4);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setValue(0, -2);
    point2.setValue(1, 3);

    List<PointSolution> solutionList = new ArrayList<PointSolution>() ;
    solutionList.add(new PointSolution(point1));
    solutionList.add(new PointSolution(point2));

    double[] minimum = {-10, 1} ;
    double[] maximum = {6, 8} ;

    var frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    var normalizedList = frontNormalizer.normalize(solutionList) ;

    assertEquals(0.75, normalizedList.get(0).objectives()[0], EPSILON) ;
    assertEquals(3.0/7.0, normalizedList.get(0).objectives()[1], EPSILON) ;
    assertEquals(0.5, normalizedList.get(1).objectives()[0], EPSILON) ;
    assertEquals(2.0/7.0, normalizedList.get(1).objectives()[1], EPSILON) ;
  }
}
