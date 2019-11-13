package org.uma.jmetal.util.front.imp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.comparator.LexicographicalPointComparator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ArrayFrontTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test public void shouldDefaultConstructorCreateAnEmptyArrayFront() {
    Front front = new ArrayFront() ;

    assertNull(ReflectionTestUtils.getField(front, "points")) ;
    assertEquals(0, ReflectionTestUtils.getField(front, "numberOfPoints")) ;
    assertEquals(0, ReflectionTestUtils.getField(front, "pointDimensions")) ;
  }


  @Test (expected = JMetalException.class)
  public void shouldCreateAnArrayFrontFromANullListRaiseAnAnException() {
    List<DoubleSolution> list = null ;
    new ArrayFront(list) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldCreateAnArrayFrontFromAnEmptyListRaiseAnException() {
    List<DoubleSolution> list = new ArrayList<>(0) ;
    new ArrayFront(list) ;
  }

  @Test
  public void shouldConstructorCreateAnArranFrontFromAFileContainingA2DFront() throws FileNotFoundException {
    Front storeFront = new ArrayFront("/pareto_fronts/ZDT1.pf") ;

    assertEquals(1001, storeFront.getNumberOfPoints());
    assertEquals(0.0, storeFront.getPoint(0).getValues()[0], 0.0001) ;
    assertEquals(1.0, storeFront.getPoint(0).getValues()[1], 0.0001) ;
    assertEquals(1.0, storeFront.getPoint(1000).getValues()[0], 0.0001) ;
    assertEquals(0.0, storeFront.getPoint(1000).getValues()[1], 0.0001) ;
  }

  @Test
  public void shouldConstructorCreateAnArranFrontFromAFileContainingA3DFront() throws FileNotFoundException {
    Front storeFront = new ArrayFront("/pareto_fronts/DTLZ1.3D.pf") ;

    assertEquals(9901, storeFront.getNumberOfPoints());

    assertEquals(0.0, storeFront.getPoint(0).getValues()[0], 0.0001) ;
    assertEquals(0.0, storeFront.getPoint(0).getValues()[1], 0.0001) ;
    assertEquals(0.5, storeFront.getPoint(0).getValues()[2], 0.0001) ;
    assertEquals(0.49005, storeFront.getPoint(9900).getValues()[0], 0.0001) ;
    assertEquals(0.00495, storeFront.getPoint(9900).getValues()[1], 0.0001) ;
    assertEquals(0.005, storeFront.getPoint(9900).getValues()[2], 0.0001) ;
  }

  @Test public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingOneDoubleSolutionObject() {
    int numberOfObjectives = 3 ;
    DoubleProblem problem = new MockDoubleProblem(numberOfObjectives) ;

    List<DoubleSolution> list = Arrays.asList(problem.createSolution()) ;
    Front front = new ArrayFront(list) ;

    assertNotNull(ReflectionTestUtils.getField(front, "points")) ;
    assertEquals(1, ReflectionTestUtils.getField(front, "numberOfPoints")) ;
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions")) ;
  }

  @Test public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingTwoDoubleSolutionObject() {
    int numberOfObjectives = 3 ;
    DoubleProblem problem = new MockDoubleProblem(numberOfObjectives) ;

    List<DoubleSolution> list = Arrays.asList(problem.createSolution(), problem.createSolution()) ;
    Front front = new ArrayFront(list) ;

    assertNotNull(ReflectionTestUtils.getField(front, "points")) ;
    assertEquals(2, ReflectionTestUtils.getField(front, "numberOfPoints")) ;
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions")) ;
  }

  @Test public void shouldCreateAnArrayFrontFromAListOfSolutionsHavingOneSingleSolutionObject() {
    int numberOfObjectives = 3 ;
    IntegerProblem problem = new MockIntegerProblem(numberOfObjectives) ;

    List<IntegerSolution> list = Arrays.asList(problem.createSolution()) ;
    Front front = new ArrayFront(list) ;

    assertNotNull(ReflectionTestUtils.getField(front, "points")) ;
    assertEquals(1, ReflectionTestUtils.getField(front, "numberOfPoints")) ;
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions")) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldCreateAnArrayFrontFromANullFrontRaiseAnException() {
    Front front = null ;
    new ArrayFront(front) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldCreateAnArrayFrontFromAnEmptyFrontRaiseAnException() {
    Front front = new ArrayFront(0, 0) ;
    new ArrayFront(front) ;
  }

  @Test public void shouldCreateAnArrayFrontFromASolutionListResultInTwoEqualsFronts() {
    int numberOfObjectives = 3 ;
    IntegerProblem problem = new MockIntegerProblem(numberOfObjectives) ;

    IntegerSolution solution1 = problem.createSolution() ;
    solution1.setObjective(0, 2);
    solution1.setObjective(0, 235);
    solution1.setObjective(0, -123);
    IntegerSolution solution2 = problem.createSolution() ;
    solution2.setObjective(0, -13234);
    solution2.setObjective(0, 523);
    solution2.setObjective(0, -123423455);

    List<IntegerSolution> list = Arrays.asList(solution1, solution2) ;

    Front front = new ArrayFront(list) ;

    assertNotNull(ReflectionTestUtils.getField(front, "points")) ;
    assertEquals(2, ReflectionTestUtils.getField(front, "numberOfPoints")) ;
    assertEquals(numberOfObjectives, ReflectionTestUtils.getField(front, "pointDimensions")) ;

    assertEquals(list.get(0).getObjective(0), front.getPoint(0).getValue(0), EPSILON);
    assertEquals(list.get(0).getObjective(1), front.getPoint(0).getValue(1), EPSILON);
    assertEquals(list.get(0).getObjective(2), front.getPoint(0).getValue(2), EPSILON);
    assertEquals(list.get(1).getObjective(0), front.getPoint(1).getValue(0), EPSILON);
    assertEquals(list.get(1).getObjective(1), front.getPoint(1).getValue(1), EPSILON);
    assertEquals(list.get(1).getObjective(2), front.getPoint(1).getValue(2), EPSILON);
  }

  @Test public void shouldCreateAnArrayFrontFromAnotherFrontResultInTwoEqualsFrontssss() {
    int numberOfPoints = 2 ;
    int pointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, pointDimensions) ;

    Point point1 = new ArrayPoint(pointDimensions) ;
    point1.setValue(0, 0.1323);
    point1.setValue(1, -30.1323);
    Point point2 = new ArrayPoint(pointDimensions) ;
    point2.setValue(0, +2342342.24232);
    point2.setValue(1, -23423423425.234);

    front.setPoint(0, point1);
    front.setPoint(1, point2);

    Front newFront = new ArrayFront(front) ;

    assertNotNull(ReflectionTestUtils.getField(newFront, "points")) ;
    assertEquals(numberOfPoints, ReflectionTestUtils.getField(newFront, "numberOfPoints")) ;
    assertEquals(pointDimensions, ReflectionTestUtils.getField(newFront, "pointDimensions")) ;

    assertEquals(front.getPoint(0).getValue(0), newFront.getPoint(0).getValue(0), EPSILON);
    assertEquals(front.getPoint(0).getValue(1), newFront.getPoint(0).getValue(1), EPSILON);
    assertEquals(front.getPoint(1).getValue(0), newFront.getPoint(1).getValue(0), EPSILON);
    assertEquals(front.getPoint(1).getValue(1), newFront.getPoint(1).getValue(1), EPSILON);
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenThePointIsNull() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The point is null"));

    front.setPoint(0, null);
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenTheIndexIsNegative() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The index value is negative"));

    front.setPoint(-1, new ArrayPoint(1));
  }

  @Test
  public void shouldSetPointRaiseAnExceptionWhenTheIndexIsGreaterThanTheFrontSize() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The index value (3) is greater than the number of "
        + "points (1)"));

    front.setPoint(3, new ArrayPoint(1));
  }

  @Test
  public void shouldSetPointAssignTheCorrectObject() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;
    Point point = new ArrayPoint(1) ;
    front.setPoint(0, point);

    Point newPoint = front.getPoint(0) ;
    assertSame(point, newPoint);
  }

  @Test
  public void shouldGetPointRaiseAnExceptionWhenTheIndexIsNegative() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The index value is negative"));

    front.getPoint(-1);
  }

  @Test
  public void shouldGetPointRaiseAnExceptionWhenTheIndexIsGreaterThanTheFrontSize() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The index value (3) is greater than the number of "
        + "points (1)"));

    front.getPoint(3);
  }

  @Test
  public void shouldGetPointReturnTheCorrectObject() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;
    Point point = new ArrayPoint(1) ;
    front.setPoint(0, point);

    assertSame(point, front.getPoint(0));
  }

  @Test
  public void shouldEqualsReturnTrueIfTheArgumentIsTheSameObject() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    assertTrue(front.equals(front)) ;
  }

  @Test
  public void shouldEqualsReturnFalseIfTheArgumentIsNull() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    assertFalse(front.equals(null)) ;
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void shouldEqualsReturnFalseIfTheArgumentIsFromAWrongClass() {
    int numberOfPoints = 1 ;
    int numberOfPointDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfPointDimensions) ;

    assertFalse(front.equals(new ArrayList<Integer>())) ;
  }

  @Test
  public void shouldEqualsReturnTrueIfTheArgumentIsEqual() {
    int numberOfPoints = 1 ;
    int pointDimensions = 2 ;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions) ;
    Front front2 = new ArrayFront(numberOfPoints, pointDimensions) ;

    Point point1 = new ArrayPoint(pointDimensions) ;
    point1.setValue(0, 0.1323);
    point1.setValue(1, -30.1323);
    Point point2 = new ArrayPoint(pointDimensions) ;
    point2.setValue(0, 0.1323);
    point2.setValue(1, -30.1323);

    front1.setPoint(0, point1);
    front2.setPoint(0, point2);

    assertTrue(front1.equals(front2)) ;
  }

  @Test
  public void shouldEqualsReturnFalseIfTheComparedFrontHasADifferentNumberOfPoints() {
    int pointDimensions = 2 ;
    Front front1 = new ArrayFront(1, pointDimensions) ;
    Front front2 = new ArrayFront(2, pointDimensions) ;

    assertFalse(front1.equals(front2)) ;
  }

  @Test
  public void shouldEqualsReturnFalseIfPointDimensionsOfTheFrontsIsDifferent() {
    Front front1 = new ArrayFront(1, 1) ;
    Front front2 = new ArrayFront(1, 2) ;

    assertFalse(front1.equals(front2)) ;
  }

  @Test
  public void shouldEqualsReturnFalseIfTheFrontsAreDifferent() {
    int numberOfPoints = 1 ;
    int pointDimensions = 2 ;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions) ;
    Front front2 = new ArrayFront(numberOfPoints, pointDimensions) ;

    Point point1 = new ArrayPoint(pointDimensions) ;
    point1.setValue(0, 0.1323);
    point1.setValue(1, -3.1323);
    Point point2 = new ArrayPoint(pointDimensions) ;
    point2.setValue(0, 0.1323);
    point2.setValue(1, -30.1323);

    front1.setPoint(0, point1);
    front2.setPoint(0, point2);

    assertFalse(front1.equals(front2)) ;
  }

  @Test
  public void shouldSortReturnAnOrderedFront() {
    int numberOfPoints = 3 ;
    int pointDimensions = 2 ;
    Front front1 = new ArrayFront(numberOfPoints, pointDimensions) ;

    Point point1 = new ArrayPoint(pointDimensions) ;
    point1.setValue(0, 10.0);
    point1.setValue(1, 12.0);
    Point point2 = new ArrayPoint(pointDimensions) ;
    point2.setValue(0, 8.0);
    point2.setValue(1, 80.0);
    Point point3 = new ArrayPoint(pointDimensions) ;
    point3.setValue(0, 5.0);
    point3.setValue(1, 50.0);

    front1.setPoint(0, point1);
    front1.setPoint(1, point2);
    front1.setPoint(2, point3);

    front1.sort(new LexicographicalPointComparator());

    assertEquals(5.0, front1.getPoint(0).getValue(0), EPSILON) ;
    assertEquals(8.0, front1.getPoint(1).getValue(0), EPSILON) ;
    assertEquals(10.0, front1.getPoint(2).getValue(0), EPSILON) ;
    assertEquals(50.0, front1.getPoint(0).getValue(1), EPSILON) ;
    assertEquals(80.0, front1.getPoint(1).getValue(1), EPSILON) ;
    assertEquals(12.0, front1.getPoint(2).getValue(1), EPSILON) ;
  }

  //TODO more test for ordering are missing

  @Test (expected = FileNotFoundException.class)
  public void shouldCreateInputStreamThrownAnExceptionIfFileDoesNotExist()
      throws FileNotFoundException {
    String fileName = "abcdefadg" ;

    new ArrayFront(fileName) ;
  }

  @Test
  public void shouldReadFrontAnEmptyFileCreateAnEmptyFront()
      throws FileNotFoundException {
    String fileName = "/arrayFront/emptyFile.dat" ;
    Front front = new ArrayFront(fileName) ;

    assertEquals(0, front.getNumberOfPoints());
  }

  /**
   * Test using a file containing:
   * 1.0 2.0 -3.0
   */
  @Test
  public void shouldReadFrontAFileWithOnePointCreateTheCorrectFront()
      throws FileNotFoundException {
    String fileName = "/arrayFront/fileWithOnePoint.dat" ;

    Front front = new ArrayFront(fileName) ;

    assertEquals(1, front.getNumberOfPoints());
    assertEquals(3, ReflectionTestUtils.getField(front, "pointDimensions"));
    assertEquals(1.0, front.getPoint(0).getValue(0), EPSILON);
    assertEquals(2.0, front.getPoint(0).getValue(1), EPSILON);
    assertEquals(-3.0, front.getPoint(0).getValue(2), EPSILON);
  }

  /**
   * Test using a file containing:
    3.0 2.3
    asdfg
   */
  @Test (expected = JMetalException.class)
  public void shouldReadFrontWithALineContainingWrongDataRaiseAnException()
      throws FileNotFoundException, JMetalException {
    String fileName = "/arrayFront/fileWithWrongData.dat" ;

    new ArrayFront(fileName) ;
  }

  /**
   * Test using a file containing:
   -30  234.234 90.25
    15 -5.23
   */
  @Test
  public void shouldReadFrontWithALineWithALineMissingDataRaiseAnException()
      throws FileNotFoundException, JMetalException {
    String fileName = "/arrayFront/fileWithMissingData.dat" ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Invalid number of points read. "
        + "Expected: 3, received: 2"));

    new ArrayFront(fileName) ;
  }

  /**
   * Test using a file containing:
   1 2 3 4
   5 6 7 8
   9 10 11 12
   -1 -2 -3 -4
   */
  @Test
  public void shouldReadFrontFourPointsCreateTheCorrectFront()
      throws FileNotFoundException, JMetalException {
    String fileName = "/arrayFront/fileWithFourPoints.dat" ;

    Front front = new ArrayFront(fileName) ;

    assertEquals(4, front.getNumberOfPoints());
    assertEquals(4, ReflectionTestUtils.getField(front, "pointDimensions"));
    assertEquals(1, front.getPoint(0).getValue(0), EPSILON);
    assertEquals(6, front.getPoint(1).getValue(1), EPSILON);
    assertEquals(11, front.getPoint(2).getValue(2), EPSILON);
    assertEquals(-4, front.getPoint(3).getValue(3), EPSILON);
  }

  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {
    /**
     * Constructor
     */
    public MockDoubleProblem(Integer numberOfObjectives) {
      setNumberOfVariables(1);
      setNumberOfObjectives(numberOfObjectives);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    @Override public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }

  @SuppressWarnings("serial")
  private class MockIntegerProblem extends AbstractIntegerProblem {
    /**
     * Constructor
     */
    public MockIntegerProblem(Integer numberOfObjectives) {
      setNumberOfVariables(1);
      setNumberOfObjectives(numberOfObjectives);

      List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4);
        upperLimit.add(4);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    @Override public void evaluate(IntegerSolution solution) {
      solution.setObjective(0, 0);
      solution.setObjective(1, 1);
    }
  }
}
