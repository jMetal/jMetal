package org.uma.jmetal.util.distance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInSolutionSpace;

public class DistanceBetweenSolutionAndKNearestNeighborsTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  public void shouldGetDistanceReturnZeroIfTheSolutionListContainsOnlyTheSolution() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    var solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution) ;

    var k = 1  ;
    var distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    var receivedValue = distance.compute(solution, solutionList) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsOnlyASolution() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    var solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    var solution2 = problem.createSolution() ;
    solution2.variables().set(0, 2.0) ;
    solution2.variables().set(1, 2.0) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    var k = 1 ;
    var distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;
    var receivedValue = distance.compute(solution, solutionList) ;
    assertEquals(Math.sqrt(2), receivedValue, EPSILON) ;
  }

  @Test
  public void shouldGetDistanceWorkProperlyIfTheListContainsThreeSolutionsAndKIsEqualToTwo() {

    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    var solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    var solution2 = problem.createSolution() ;
    solution2.variables().set(0, 2.0) ;
    solution2.variables().set(1, 2.0) ;

    var solution3 = problem.createSolution() ;
    solution3.variables().set(0, 3.0) ;
    solution3.variables().set(1, 3.0) ;

    var solution4 = problem.createSolution() ;
    solution4.variables().set(0, 4.0) ;
    solution4.variables().set(1, 4.0) ;

    var solutionList = List.of(solution, solution2, solution3, solution4) ;

    var k = 2 ;
    var distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    var receivedValue = distance.compute(solution, solutionList) ;
    assertEquals((Math.sqrt(4+4)), receivedValue, EPSILON) ;
  }
}