package org.uma.jmetal.util.distance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.impl.DistanceBetweenSolutionAndKNearestNeighbors;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInSolutionSpace;

class DistanceBetweenSolutionAndKNearestNeighborsTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  void shouldGetDistanceReturnZeroIfTheSolutionListContainsOnlyTheSolution() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution) ;

    int k = 1  ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    double receivedValue = distance.compute(solution, solutionList) ;
    Assertions.assertEquals(0.0, receivedValue, EPSILON);
  }

  @Test
  void shouldGetDistanceWorkProperlyIfTheListContainsOnlyASolution() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    DoubleSolution solution2 = problem.createSolution() ;
    solution2.variables().set(0, 2.0) ;
    solution2.variables().set(1, 2.0) ;

    List<DoubleSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution2) ;

    int k = 1 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;
    double receivedValue = distance.compute(solution, solutionList) ;
    Assertions.assertEquals(Math.sqrt(2), receivedValue, EPSILON);
  }

  @Test
  void shouldGetDistanceWorkProperlyIfTheListContainsThreeSolutionsAndKIsEqualToTwo() {

    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.variables().set(0, 1.0) ;
    solution.variables().set(1, 1.0) ;

    DoubleSolution solution2 = problem.createSolution() ;
    solution2.variables().set(0, 2.0) ;
    solution2.variables().set(1, 2.0) ;

    DoubleSolution solution3 = problem.createSolution() ;
    solution3.variables().set(0, 3.0) ;
    solution3.variables().set(1, 3.0) ;

    DoubleSolution solution4 = problem.createSolution() ;
    solution4.variables().set(0, 4.0) ;
    solution4.variables().set(1, 4.0) ;

    List<DoubleSolution> solutionList = List.of(solution, solution2, solution3, solution4) ;

    int k = 2 ;
    DistanceBetweenSolutionAndKNearestNeighbors<DoubleSolution> distance =
            new DistanceBetweenSolutionAndKNearestNeighbors<>(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<>()) ;

    double receivedValue = distance.compute(solution, solutionList) ;
    Assertions.assertEquals((Math.sqrt(4+4)), receivedValue, EPSILON);
  }
}