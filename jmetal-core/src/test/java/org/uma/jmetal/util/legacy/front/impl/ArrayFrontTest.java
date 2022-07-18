package org.uma.jmetal.util.legacy.front.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.comparator.LexicographicalPointComparator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayFrontTest {
  private static final double EPSILON = 0.0000000000001;
  private static String frontDirectory ;
  private static String resourcesDirectory ;

  @BeforeAll
  public static void startup() throws IOException {
    var jMetalProperties = new Properties() ;
    jMetalProperties.load(new FileInputStream("../jmetal.properties"));

    resourcesDirectory = "../" + jMetalProperties.getProperty("resourcesDirectory") ;
    frontDirectory = resourcesDirectory + "/" +jMetalProperties.getProperty("referenceFrontsDirectory") ;
  }

  @Test
  public void shouldDefaultConstructorCreateAnEmptyArrayFront() {
    Front front = new ArrayFront();

    assertNull(ReflectionTestUtils.getField(front, "points"));
    assertEquals(0, ReflectionTestUtils.getField(front, "numberOfPoints"));
    assertEquals(0, ReflectionTestUtils.getField(front, "pointDimensions"));
  }

  @Test
  public void shouldCreateAnArrayFrontFromANullListRaiseAnAnException() {
    List<DoubleSolution> list = null;
    assertThrows(JMetalException.class, () -> new ArrayFront(list));
  }

  @Test
  public void shouldCreateAnArrayFrontFromAnEmptyListRaiseAnException() {
    List<DoubleSolution> list = new ArrayList<>(0);
    assertThrows(JMetalException.class, () -> new ArrayFront(list));
  }

  @Test
  public void shouldConstructorCreateAnArranFrontFromAFileContainingA2DFront()
      throws FileNotFoundException {
    Front storeFront = new ArrayFront(frontDirectory + "/ZDT1.csv");

    assertEquals(1001, storeFront.getNumberOfPoints());
    assertEquals(0.0, storeFront.getPoint(0).getValues()[0], 0.0001);
    assertEquals(1.0, storeFront.getPoint(0).getValues()[1], 0.0001);
    assertEquals(1.0, storeFront.getPoint(1000).getValues()[0], 0.0001);
    assertEquals(0.0, storeFront.getPoint(1000).getValues()[1], 0.0001);
  }

  @Test
  @Disabled
  public void shouldConstructorCreateAnArranFrontFromAFileContainingA3DFront()
      throws FileNotFoundException {
    Front storeFront = new ArrayFront(frontDirectory + "/DTLZ1.3D.csv");

    assertEquals(9901, storeFront.getNumberOfPoints());

    assertEquals(0.0, storeFront.getPoint(0).getValues()[0], 0.0001);
    assertEquals(0.0, storeFront.getPoint(0).getValues()[1], 0.0001);
    assertEquals(0.5, storeFront.getPoint(0).getValues()[2], 0.0001);
    assertEquals(0.49005, storeFront.getPoint(9999).getValues()[0], 0.0001);
    assertEquals(0.00495, storeFront.getPoint(9999).getValues()[1], 0.0001);
    assertEquals(0.005, storeFront.getPoint(9999).getValues()[2], 0.0001);
  }

  @Test
  public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingOneDoubleSolutionObject() {
    var numberOfObjectives = 3;

    var bounds = Arrays.asList(Bounds.create(-1.0, 1.0));
    List<DoubleSolution> list =
        Arrays.asList(new DefaultDoubleSolution(bounds, numberOfObjectives, 0));
    Front front = new ArrayFront(list);

    assertNotNull(ReflectionTestUtils.getField(front, "points"));
    assertEquals(1, ReflectionTestUtils.getField(front, "numberOfPoints"));
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions"));
  }

  @Test
  public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingTwoDoubleSolutionObject() {
    var numberOfObjectives = 3;

    var bounds = Arrays.asList(Bounds.create(-1.0, 1.0));
    List<DoubleSolution> list =
        Arrays.asList(
            new DefaultDoubleSolution(bounds, numberOfObjectives, 0),
            new DefaultDoubleSolution(bounds, numberOfObjectives, 0));
    Front front = new ArrayFront(list);

    assertNotNull(ReflectionTestUtils.getField(front, "points"));
    assertEquals(2, ReflectionTestUtils.getField(front, "numberOfPoints"));
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions"));
  }

  @Test
  public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingOneSingleSolutionObject() {
    var numberOfObjectives = 3;

    var bounds = Arrays.asList(Bounds.create(0, 1)) ;

    List<IntegerSolution> list =
        Arrays.asList(
            new DefaultIntegerSolution(bounds, numberOfObjectives, 0)) ;
    Front front = new ArrayFront(list);

    assertNotNull(ReflectionTestUtils.getField(front, "points"));
    assertEquals(1, ReflectionTestUtils.getField(front, "numberOfPoints"));
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions"));
  }

  @Test
  public void shouldCreateAnArrayFrontFromANullFrontRaiseAnException() {
    Front front = null;
    assertThrows(JMetalException.class, () -> new ArrayFront(front));
  }

  @Test
  public void shouldCreateAnArrayFrontFromAnEmptyFrontRaiseAnException() {
    Front front = new ArrayFront(0, 0);
    assertThrows(JMetalException.class, () -> new ArrayFront(front));
  }

  @Test
  public void shouldCreateAnArrayFrontFromASolutionListResultInTwoEqualsFronts() {
    var numberOfObjectives = 3;

    var bounds = Arrays.asList(Bounds.create(0, 1)) ;

    IntegerSolution solution1 =
        new DefaultIntegerSolution(bounds, numberOfObjectives, 0);
    solution1.objectives()[0] = 2;
    solution1.objectives()[1] = 235;
    solution1.objectives()[2] =-123;
    IntegerSolution solution2 =
        new DefaultIntegerSolution(bounds, numberOfObjectives, 0);
    solution2.objectives()[0] = -13234;
    solution2.objectives()[1] = 523;
    solution2.objectives()[2] = -123423455;

    var list = Arrays.asList(solution1, solution2);

    Front front = new ArrayFront(list);

    assertNotNull(ReflectionTestUtils.getField(front, "points"));
    assertEquals(2, ReflectionTestUtils.getField(front, "numberOfPoints"));
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions"));

    assertEquals(list.get(0).objectives()[0], front.getPoint(0).getValue(0), EPSILON);
    assertEquals(list.get(0).objectives()[1], front.getPoint(0).getValue(1), EPSILON);
    assertEquals(list.get(0).objectives()[2], front.getPoint(0).getValue(2), EPSILON);
    assertEquals(list.get(1).objectives()[0], front.getPoint(1).getValue(0), EPSILON);
    assertEquals(list.get(1).objectives()[1], front.getPoint(1).getValue(1), EPSILON);
    assertEquals(list.get(1).objectives()[2], front.getPoint(1).getValue(2), EPSILON);
  }

  @Test
  public void shouldCreateAnArrayFrontFromAnotherFrontResultInTwoEqualsFrontssss() {
    var numberOfPoints = 2;
    var pointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, pointDimensions);

    Point point1 = new ArrayPoint(pointDimensions);
    point1.setValue(0, 0.1323);
    point1.setValue(1, -30.1323);
    Point point2 = new ArrayPoint(pointDimensions);
    point2.setValue(0, +2342342.24232);
    point2.setValue(1, -23423423425.234);

    front.setPoint(0, point1);
    front.setPoint(1, point2);

    Front newFront = new ArrayFront(front);

    assertNotNull(ReflectionTestUtils.getField(newFront, "points"));
    assertEquals(numberOfPoints, ReflectionTestUtils.getField(newFront, "numberOfPoints"));
    assertEquals(pointDimensions, ReflectionTestUtils.getField(newFront, "pointDimensions"));

    assertEquals(front.getPoint(0).getValue(0), newFront.getPoint(0).getValue(0), EPSILON);
    assertEquals(front.getPoint(0).getValue(1), newFront.getPoint(0).getValue(1), EPSILON);
    assertEquals(front.getPoint(1).getValue(0), newFront.getPoint(1).getValue(0), EPSILON);
    assertEquals(front.getPoint(1).getValue(1), newFront.getPoint(1).getValue(1), EPSILON);
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenThePointIsNull() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    Executable executable = () -> front.setPoint(0, null);

    var cause = assertThrows(JMetalException.class, executable);
    assertThat(cause.getMessage(), containsString("The point is null"));
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenTheIndexIsNegative() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    Executable executable = () -> front.setPoint(-1, new ArrayPoint(1));

    var cause = assertThrows(JMetalException.class, executable);
    assertThat(cause.getMessage(), containsString("The index value is negative"));
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenTheIndexIsGreaterThanTheFrontSize() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    Executable executable = () -> front.setPoint(3, new ArrayPoint(1));

    var cause = assertThrows(JMetalException.class, executable);
    assertThat(cause.getMessage(),
            containsString("The index value (3) is greater than the number of " + "points (1)"));
  }

  @Test
  public void shouldSetPointAssignTheCorrectObject() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);
    Point point = new ArrayPoint(1);
    front.setPoint(0, point);

    var newPoint = front.getPoint(0);
    assertSame(point, newPoint);
  }

  @Test
  public void shouldGetPointRaiseAnExceptionWhenTheIndexIsNegative() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    Executable executable = () -> front.getPoint(-1);

    var cause = assertThrows(JMetalException.class, executable);
    assertThat(cause.getMessage(), containsString("The index value is negative"));
  }

  @Test
  public void shouldGetPointRaiseAnExceptionWhenTheIndexIsGreaterThanTheFrontSize() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    Executable executable = () -> front.getPoint(3);

    var cause = assertThrows(JMetalException.class, executable);
    assertThat(cause.getMessage(),
            containsString("The index value (3) is greater than the number of " + "points (1)"));
  }

  @Test
  public void shouldGetPointReturnTheCorrectObject() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);
    Point point = new ArrayPoint(1);
    front.setPoint(0, point);

    assertSame(point, front.getPoint(0));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheArgumentIsTheSameObject() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    assertTrue(front.equals(front));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheArgumentIsNull() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    assertFalse(front.equals(null));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void shouldEqualsReturnFalseIfTheArgumentIsFromAWrongClass() {
    var numberOfPoints = 1;
    var numberOfPointDimensions = 2;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions);

    assertFalse(front.equals(new ArrayList<Integer>()));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheArgumentIsEqual() {
    var numberOfPoints = 1;
    var pointDimensions = 2;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions);
    Front front2 = new ArrayFront(numberOfPoints, pointDimensions);

    Point point1 = new ArrayPoint(pointDimensions);
    point1.setValue(0, 0.1323);
    point1.setValue(1, -30.1323);
    Point point2 = new ArrayPoint(pointDimensions);
    point2.setValue(0, 0.1323);
    point2.setValue(1, -30.1323);

    front1.setPoint(0, point1);
    front2.setPoint(0, point2);

    assertTrue(front1.equals(front2));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheComparedFrontHasADifferentNumberOfPoints() {
    var pointDimensions = 2;
    Front front1 = new ArrayFront(1, pointDimensions);
    Front front2 = new ArrayFront(2, pointDimensions);

    assertFalse(front1.equals(front2));
  }

  @Test
  public void shouldEqualsReturnFalseIfPointDimensionsOfTheFrontsIsDifferent() {
    Front front1 = new ArrayFront(1, 1);
    Front front2 = new ArrayFront(1, 2);

    assertFalse(front1.equals(front2));
  }

  @Test
  public void shouldEqualsReturnFalseIfTheFrontsAreDifferent() {
    var numberOfPoints = 1;
    var pointDimensions = 2;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions);
    Front front2 = new ArrayFront(numberOfPoints, pointDimensions);

    Point point1 = new ArrayPoint(pointDimensions);
    point1.setValue(0, 0.1323);
    point1.setValue(1, -3.1323);
    Point point2 = new ArrayPoint(pointDimensions);
    point2.setValue(0, 0.1323);
    point2.setValue(1, -30.1323);

    front1.setPoint(0, point1);
    front2.setPoint(0, point2);

    assertFalse(front1.equals(front2));
  }

  @Test
  public void shouldSortReturnAnOrderedFront() {
    var numberOfPoints = 3;
    var pointDimensions = 2;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions);

    Point point1 = new ArrayPoint(pointDimensions);
    point1.setValue(0, 10.0);
    point1.setValue(1, 12.0);
    Point point2 = new ArrayPoint(pointDimensions);
    point2.setValue(0, 8.0);
    point2.setValue(1, 80.0);
    Point point3 = new ArrayPoint(pointDimensions);
    point3.setValue(0, 5.0);
    point3.setValue(1, 50.0);

    front1.setPoint(0, point1);
    front1.setPoint(1, point2);
    front1.setPoint(2, point3);

    front1.sort(new LexicographicalPointComparator());

    assertEquals(5.0, front1.getPoint(0).getValue(0), EPSILON);
    assertEquals(8.0, front1.getPoint(1).getValue(0), EPSILON);
    assertEquals(10.0, front1.getPoint(2).getValue(0), EPSILON);
    assertEquals(50.0, front1.getPoint(0).getValue(1), EPSILON);
    assertEquals(80.0, front1.getPoint(1).getValue(1), EPSILON);
    assertEquals(12.0, front1.getPoint(2).getValue(1), EPSILON);
  }

  // TODO more test for ordering are missing

  @Test
  public void shouldCreateInputStreamThrownAnExceptionIfFileDoesNotExist()
      throws FileNotFoundException {
    var fileName = "abcdefadg";

    assertThrows(FileNotFoundException.class, () -> new ArrayFront(fileName));
  }

  @Test
  public void shouldReadFrontAnEmptyFileCreateAnEmptyFront() throws FileNotFoundException {
    var fileName = resourcesDirectory + "/unitTestsData/arrayFront/emptyFile.dat";
    Front front = new ArrayFront(fileName);

    assertEquals(0, front.getNumberOfPoints());
  }

  /** Test using a file containing: 1.0 2.0 -3.0 */
  @Test
  public void shouldReadFrontAFileWithOnePointCreateTheCorrectFront() throws FileNotFoundException {
    var fileName = resourcesDirectory + "/unitTestsData/arrayFront/fileWithOnePoint.dat";

    Front front = new ArrayFront(fileName);

    assertEquals(1, front.getNumberOfPoints());
    assertEquals(3, ReflectionTestUtils.getField(front, "pointDimensions"));
    assertEquals(1.0, front.getPoint(0).getValue(0), EPSILON);
    assertEquals(2.0, front.getPoint(0).getValue(1), EPSILON);
    assertEquals(-3.0, front.getPoint(0).getValue(2), EPSILON);
  }

  /** Test using a file containing: 3.0 2.3 asdfg */
  @Test
  @Disabled
  public void shouldReadFrontWithALineContainingWrongDataRaiseAnException()
      throws FileNotFoundException, JMetalException {
    var fileName = "../resources/unitTestsData/arrayFront/fileWithWrongData.dat";

    assertThrows(JMetalException.class, () -> new ArrayFront(fileName));
  }

  /** Test using a file containing: -30 234.234 90.25 15 -5.23 */
  @Test
  public void shouldReadFrontWithALineWithALineMissingDataRaiseAnException()
      throws FileNotFoundException, JMetalException {
    var fileName = resourcesDirectory + "/unitTestsData/arrayFront/fileWithMissingData.dat";

    assertThrows(InvalidConditionException.class, () -> new ArrayFront(fileName));
  }

  /** Test using a file containing: 1 2 3 4 5 6 7 8 9 10 11 12 -1 -2 -3 -4 */
  @Test
  public void shouldReadFrontFourPointsCreateTheCorrectFront()
      throws FileNotFoundException, JMetalException {
    var fileName = resourcesDirectory + "/unitTestsData/arrayFront/fileWithFourPoints.dat";

    Front front = new ArrayFront(fileName);

    assertEquals(4, front.getNumberOfPoints());
    assertEquals(4, ReflectionTestUtils.getField(front, "pointDimensions"));
    assertEquals(1, front.getPoint(0).getValue(0), EPSILON);
    assertEquals(6, front.getPoint(1).getValue(1), EPSILON);
    assertEquals(11, front.getPoint(2).getValue(2), EPSILON);
    assertEquals(-4, front.getPoint(3).getValue(3), EPSILON);
  }
}
