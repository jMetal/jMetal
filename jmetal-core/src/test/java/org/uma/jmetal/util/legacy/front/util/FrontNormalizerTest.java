package org.uma.jmetal.util.legacy.front.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * @author Antonio J. Nebro
 */
public class FrontNormalizerTest {

  private static final double EPSILON = 0.0000000000001 ;

  

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheReferenceFrontIsNull() {
    JMetalException e = assertThrows(JMetalException.class, () -> new FrontNormalizer((Front) null));
    assertTrue(e.getMessage().contains("The reference front is null"));
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheReferenceSolutionListIsNull() {
    JMetalException e = assertThrows(JMetalException.class, () -> new FrontNormalizer((List<DoubleSolution>) null));
    assertTrue(e.getMessage().contains("The reference front is null"));
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheVectorOfMinimumValuesIsNull() {
    JMetalException e = assertThrows(JMetalException.class, () -> new FrontNormalizer(null, new double[1]));
    assertTrue(e.getMessage().contains("The array of minimum values is null"));
  }

  @Test
  public void shouldFrontNormalizerConstructorRaiseAnExceptionIsTheVectorOfMaximumValuesIsNull() {
    JMetalException e = assertThrows(JMetalException.class, () -> new FrontNormalizer(new double[1], null));
    assertTrue(e.getMessage().contains("The array of maximum values is null"));
  }

  @Test
  public void shouldFrontNormalizerContructorRaiseAnExceptionTheDimensionOfTheMaximumAndMinimumArrayIsNotEqual() {
    JMetalException e = assertThrows(JMetalException.class, () -> new FrontNormalizer(new double[1], new double[2]));
    assertTrue(e.getMessage().contains("The length of the maximum array (2) is different from the length of the minimum array (1)"));
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionTheFrontIsNull() {
    FrontNormalizer frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize((Front) null));
    assertTrue(e.getMessage().contains("The front is null"));
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionTheSolutionListIsNull() {
    FrontNormalizer frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize((List<DoubleSolution>) null));
    assertTrue(e.getMessage().contains("The front is null"));
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheFrontIsEmpty() {
    Front front = new ArrayFront(0, 0);
    FrontNormalizer frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize(front));
    assertTrue(e.getMessage().contains("The front is empty"));
  }

  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheSolutionListIsEmpty() {
    List<DoubleSolution> solutionList = Collections.emptyList() ;
    FrontNormalizer frontNormalizer = new FrontNormalizer(new double[1], new double[1]) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize(solutionList));
    assertTrue(e.getMessage().contains("The list of solutions is empty"));
  }


  @Test
  public void shouldNormalizeRaiseAnExceptionTheDimensionOfTheMaximumArrayPointsIsNotCorrect() {
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(1, numberOfPointDimensions);
    FrontNormalizer frontNormalizer = new FrontNormalizer(new double[4], new double[4]) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize(front));
    assertTrue(e.getMessage().contains("The length of the point dimensions (2) is different from the length of the maximum array (4)"));
  }

  /**
   * Point: [2,4]
   * Maximum values: [4, 4]
   * Minimum values: [0, 0]
   * Result: [0.5, 1.0]
   */
  @Test
  public void shouldNormalizeReturnTheCorrectFrontIfThisContainsOnePoint() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.value(0, 2);
    point.value(1, 4);

    front.setPoint(0, point);

    double[] minimum = {0, 0} ;
    double[] maximum = {4, 4} ;

    FrontNormalizer frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    Front normalizedFront = frontNormalizer.normalize(front) ;

    assertEquals(0.5, normalizedFront.getPoint(0).value(0), EPSILON) ;
    assertEquals(1.0, normalizedFront.getPoint(0).value(1), EPSILON) ;
  }

  /**
   * Point: [2,4]
   * Maximum values: [2, 4]
   * Minimum values: [2, 4]
   * Result: [0.5, 1.0]
   */
  @Test
  public void shouldNormalizeRaiseAnExceptionIfTheMaxAndMinValuesAreTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);
    Point point = new ArrayPoint(numberOfDimensions) ;
    point.value(0, 2);
    point.value(1, 4);
    front.setPoint(0, point);
    double[] minimum = {2, 4} ;
    double[] maximum = {2, 4} ;
    FrontNormalizer frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    JMetalException e = assertThrows(JMetalException.class, () -> frontNormalizer.normalize(front));
    assertTrue(e.getMessage().contains("Maximum and minimum values of index 0 are the same: 2"));
  }

  /**
   * Points: [2,4], [-2, 3]
   * Maximum values: [6, 8]
   * Minimum values: [-10, 1]
   * Result: [0.5, 1.0], []
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfThisContainsTwoPoints() {
    int numberOfPoints = 2 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.value(0, 2);
    point1.value(1, 4);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.value(0, -2);
    point2.value(1, 3);

    front.setPoint(0, point1);
    front.setPoint(1, point2);

    double[] minimum = {-10, 1} ;
    double[] maximum = {6, 8} ;

    FrontNormalizer frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    Front normalizedFront = frontNormalizer.normalize(front) ;

    assertEquals(0.75, normalizedFront.getPoint(0).value(0), EPSILON) ;
    assertEquals(3.0/7.0, normalizedFront.getPoint(0).value(1), EPSILON) ;
    assertEquals(0.5, normalizedFront.getPoint(1).value(0), EPSILON) ;
    assertEquals(2.0 / 7.0, normalizedFront.getPoint(1).value(1), EPSILON) ;
  }

  /**
   * Points: [2,4], [-2, 3]
   * Maximum values: [6, 8]
   * Minimum values: [-10, 1]
   * Result: [0.5, 1.0], []
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfTheSolutionListContainsTwoPoints() {
    int numberOfDimensions = 2 ;

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.value(0, 2);
    point1.value(1, 4);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.value(0, -2);
    point2.value(1, 3);

    List<PointSolution> solutionList = new ArrayList<PointSolution>() ;
    solutionList.add(new PointSolution(point1.values()));
    solutionList.add(new PointSolution(point2.values()));

    double[] minimum = {-10, 1} ;
    double[] maximum = {6, 8} ;

    FrontNormalizer frontNormalizer = new FrontNormalizer(minimum, maximum) ;
    List<? extends Solution<?>> normalizedList = frontNormalizer.normalize(solutionList) ;

    assertEquals(0.75, normalizedList.get(0).objectives()[0], EPSILON) ;
    assertEquals(3.0/7.0, normalizedList.get(0).objectives()[1], EPSILON) ;
    assertEquals(0.5, normalizedList.get(1).objectives()[0], EPSILON) ;
    assertEquals(2.0/7.0, normalizedList.get(1).objectives()[1], EPSILON) ;
  }
}
