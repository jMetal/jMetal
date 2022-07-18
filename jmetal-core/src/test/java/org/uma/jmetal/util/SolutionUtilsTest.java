package org.uma.jmetal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class SolutionUtilsTest {
  private static final double EPSILON = 0.0000000001;

  /** Case A: the two solutions are the same */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithOneObjectiveCaseA() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  0.1;
    solution2.objectives()[0] =  0.1;

    assertEquals(0.0, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON);
  }

  /** Case B: the two solutions are not the same */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithOneObjectiveCaseB() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  0.1;
    solution2.objectives()[0] =  0.3;

    assertEquals(0.2, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON);
  }

  /** Case A: the two solutions are the same */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithTwoObjectivesCaseA() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  0.1;
    solution1.objectives()[1] =  0.2;
    solution2.objectives()[0] =  0.1;
    solution2.objectives()[1] =  0.2;

    assertEquals(0.0, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON);
  }

  /** Case B: the two solutions are not the same */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithTwoObjectivesCaseB() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  0.1;
    solution1.objectives()[1] =  0.1;
    solution2.objectives()[0] =  0.4;
    solution2.objectives()[1] =  0.4;

    assertEquals(0.42426406871192857, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON);
  }

  /** Case A. Solution = [1], solutionList = [1]] */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseA() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  1.0;
    solution2.objectives()[0] =  1.0;
    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(solution2);

    assertEquals(
        0.0, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON);
  }

  /** Case B. Solution = [1], solutionList = [[2]] */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseB() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();

    solution1.objectives()[0] =  1.0;
    solution2.objectives()[0] =  2.0;
    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(solution2);

    assertEquals(1, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON);
  }

  /** Case C. Solution = [1], solutionList = [[1], [2]] */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseC() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();
    var solution3 = problem.createSolution();

    solution1.objectives()[0] =  1.0;
    solution2.objectives()[0] =  1.0;
    solution3.objectives()[0] =  2.0;
    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(solution2);
    solutionList.add(solution3);

    assertEquals(
        0.5, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON);
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenTheSolutionIsNull() {
    SolutionUtils.normalize(null, new double[] {0.1}, new double[] {0.2});
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenTheMinValueIsNull() {

    DoubleProblem problem = new FakeDoubleProblem(0, 2, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, null, new double[] {0.2});
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenTheMaxValueIsNull() {

    DoubleProblem problem = new FakeDoubleProblem(0, 2, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, new double[] {0.2}, null);
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenMinAndMaxValuesHaveDifferentLength() {

    DoubleProblem problem = new FakeDoubleProblem(0, 2, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, new double[] {0.2, 0.2}, new double[] {0.3});
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenMinIsEmpty() {

    DoubleProblem problem = new FakeDoubleProblem(0, 0, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, new double[] {}, new double[] {0.2});
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenMaxIsEmpty() {

    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, new double[] {0.2}, new double[] {});
  }

  @Test(expected = JMetalException.class)
  public void shouldNormalizeThrowsAnExceptionWhenMinAndMaxValuesHaveDifferentNumberOfObjective() {

    DoubleProblem problem = new FakeDoubleProblem(3, 1, 0);
    var solution = problem.createSolution();

    SolutionUtils.normalize(solution, new double[] {0.2, 0.2}, new double[] {0.3, 0.3});
  }

  @Test
  public void shouldNormalizeReturnsCorrectNormalizedNumber() {

    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);

    solution.objectives()[0] = 10;
    solution.objectives()[1] = 20;

    var minValue = new double[] {10, 10};
    var maxValue = new double[] {20, 20};

    var normalized =
        (DoubleSolution) SolutionUtils.normalize(solution, minValue, maxValue);

    assertNotSame(normalized, solution);
    assertEquals(0.0, normalized.objectives()[0], EPSILON);
    assertEquals(1.0, normalized.objectives()[1], EPSILON);
  }

  /*
    TODO

    @Test
    public void shouldAverageDistanceToSolutionListWorkProperlyCaseD() {
      DoubleProblem problem = new MockedDoubleProblem(2) ;
      DoubleSolution solution1 = problem.createSolution() ;
      DoubleSolution solution2 = problem.createSolution() ;
      DoubleSolution solution3 = problem.createSolution() ;

      solution1.objectives()[0] =  0.0);
      solution1.setObjective(1, 1.0);
      solution2.objectives()[0] =  0.1);
      solution2.setObjective(1, 0.9);
      solution3.objectives()[0] =  0.3);
      solution3.setObjective(1, 0.7);

      List<DoubleSolution> solutionList = new ArrayList<>() ;
      solutionList.add(solution1) ;
      solutionList.add(solution2) ;
      solutionList.add(solution3) ;

      System.out.println("D1: " + SolutionUtils.averageDistanceToSolutionList(solution1, solutionList)) ;
      System.out.println("D2: " + SolutionUtils.averageDistanceToSolutionList(solution2, solutionList)) ;
      System.out.println("D3: " + SolutionUtils.averageDistanceToSolutionList(solution3, solutionList)) ;

      assertEquals(0.5, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON) ;
    }
  */
}
