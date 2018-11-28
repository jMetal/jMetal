package org.uma.jmetal.util;

import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SolutionUtilsTest {
  private static final double EPSILON = 0.0000000001 ;

  /**
   * Case A: the two solutions are the same
   */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithOneObjectiveCaseA() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 0.1);
    solution2.setObjective(0, 0.1);

    assertEquals(0.0, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON) ;
  }

  /**
   * Case B: the two solutions are not the same
   */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithOneObjectiveCaseB() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 0.1);
    solution2.setObjective(0, 0.3);

    assertEquals(0.2, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON) ;
  }

  /**
   * Case A: the two solutions are the same
   */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithTwoObjectivesCaseA() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 0.1);
    solution1.setObjective(0, 0.2);
    solution2.setObjective(0, 0.1);
    solution2.setObjective(0, 0.2);

    assertEquals(0.0, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON) ;
  }

  /**
   * Case B: the two solutions are not the same
   */
  @Test
  public void shouldDistanceBetweenObjectivesWorkProperlyWithTwoSolutionsWithTwoObjectivesCaseB() {
    DoubleProblem problem = new MockedDoubleProblem(2) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 0.1);
    solution1.setObjective(0, 0.1);
    solution2.setObjective(0, 0.4);
    solution2.setObjective(0, 0.4);

    assertEquals(0.3, SolutionUtils.distanceBetweenObjectives(solution1, solution2), EPSILON) ;
  }

  /**
   * Case A. Solution = [1], solutionList = [1]]
   */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseA() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 1.0);
    solution2.setObjective(0, 1.0);
    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    assertEquals(0.0, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON) ;
  }

  /**
   * Case B. Solution = [1], solutionList = [[2]]
   */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseB() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    solution1.setObjective(0, 1.0);
    solution2.setObjective(0, 2.0);
    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    assertEquals(1, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON) ;
  }

  /**
   * Case C. Solution = [1], solutionList = [[1], [2]]
   */
  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseC() {
    DoubleProblem problem = new MockedDoubleProblem(1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;

    solution1.setObjective(0, 1.0);
    solution2.setObjective(0, 1.0);
    solution3.setObjective(0, 2.0);
    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;
    solutionList.add(solution3) ;

    assertEquals(0.5, SolutionUtils.averageDistanceToSolutionList(solution1, solutionList), EPSILON) ;
  }

  /*
  TODO

  @Test
  public void shouldAverageDistanceToSolutionListWorkProperlyCaseD() {
    DoubleProblem problem = new MockedDoubleProblem(2) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;

    solution1.setObjective(0, 0.0);
    solution1.setObjective(1, 1.0);
    solution2.setObjective(0, 0.1);
    solution2.setObjective(1, 0.9);
    solution3.setObjective(0, 0.3);
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
  private class MockedDoubleProblem extends AbstractDoubleProblem {
    public MockedDoubleProblem(int numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(10.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {

    }

    @Override
    public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this)  ;
    }
  }
}