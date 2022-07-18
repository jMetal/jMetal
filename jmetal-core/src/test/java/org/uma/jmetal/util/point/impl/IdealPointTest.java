package org.uma.jmetal.util.point.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.point.Point;

/**
 * Created by ajnebro on 12/2/16.
 */
public class IdealPointTest {
  private static final double EPSILON = 0.00000000001 ;
  private static final double DEFAULT_INITIAL_VALUE = Double.POSITIVE_INFINITY ;

  private IdealPoint referencePoint ;

  @Test
  public void shouldConstructorCreateAnIdealPointWithAllObjectiveValuesCorrectlyInitialized() {
    var numberOfObjectives = 4 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    assertEquals(numberOfObjectives, referencePoint.getDimension()) ;

    for (var i = 0; i < numberOfObjectives; i++) {
      assertEquals(DEFAULT_INITIAL_VALUE, referencePoint.getValue(i), EPSILON) ;
    }
  }

  @Test
  public void shouldUpdateWithOneSolutionMakeTheIdealPointHaveTheSolutionValues() {
    var numberOfObjectives = 2 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;
    var solution = problem.createSolution() ;
    solution.objectives()[0] = 1.0 ;
    solution.objectives()[1] = 2.0 ;

    referencePoint.update(solution.objectives());

    assertEquals(1.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(2.0, referencePoint.getValue(1), EPSILON) ;
    assertArrayEquals(new double[]{1.0, 2.0}, solution.objectives(), EPSILON);
  }

  @Test
  public void shouldUpdateWithTwoSolutionsLeadToTheCorrectIdealPoint() {
    var numberOfObjectives = 2 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;
    var solution1 = problem.createSolution() ;
    solution1.objectives()[0] = 0.0 ;
    solution1.objectives()[1] = 1.0 ;

    var solution2 = problem.createSolution() ;
    solution2.objectives()[0] = 1.0 ;
    solution2.objectives()[1] = 0.0 ;

    referencePoint.update(solution1.objectives());
    referencePoint.update(solution2.objectives());

    assertEquals(0.0, referencePoint.getValue(0), EPSILON) ;
    assertEquals(0.0, referencePoint.getValue(1), EPSILON) ;
  }

  @Test
  public void shouldUpdateWithThreeSolutionsLeadToTheCorrectIdealPoint() {
    var numberOfObjectives = 3 ;

    referencePoint = new IdealPoint(numberOfObjectives) ;

    DoubleProblem problem = new FakeDoubleProblem(3, 3, 0) ;
    var solution1 = problem.createSolution() ;
    solution1.objectives()[0] = 3.0 ;
    solution1.objectives()[1] = 1.0 ;
    solution1.objectives()[2] = 2.0 ;

    var solution2 = problem.createSolution() ;
    solution2.objectives()[0] = 0.2 ;
    solution2.objectives()[1] = 4.0 ;
    solution2.objectives()[2] = 5.5 ;

    var solution3 = problem.createSolution() ;
    solution3.objectives()[0] = 5.0 ;
    solution3.objectives()[1] = 6.0 ;
    solution3.objectives()[2] = 1.5 ;

    referencePoint.update(solution1.objectives());
    referencePoint.update(solution2.objectives());
    referencePoint.update(solution3.objectives());

    assertEquals(0.2, referencePoint.getValue(0), EPSILON) ;
    assertEquals(1.0, referencePoint.getValue(1), EPSILON) ;
    assertEquals(1.5, referencePoint.getValue(2), EPSILON) ;
  }

  @Test
  public void shouldSetAssignTheRightValues() {
    Point point = new ArrayPoint(new double[]{2, 3, 3}) ;

    point.set(new double[]{5, 6, 7}) ;
    assertEquals(5, point.getValue(0), EPSILON);
    assertEquals(6, point.getValue(1), EPSILON);
    assertEquals(7, point.getValue(2), EPSILON);
  }
}