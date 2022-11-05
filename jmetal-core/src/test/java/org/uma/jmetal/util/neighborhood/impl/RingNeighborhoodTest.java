package org.uma.jmetal.util.neighborhood.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class RingNeighborhoodTest {
  @Test
  void getNeighboursRaisesAnExceptionIfTheSolutionListIsNull() {
    var neighborhood = new RingNeighborhood<>() ;

    assertThrows(NullParameterException.class, () -> neighborhood.getNeighbors(null, 1)) ;
  }

  @Test
  void getNeighboursRaisesAnExceptionIfTheSolutionListHasOneElement() {
    var neighborhood = new RingNeighborhood<DoubleSolution>() ;
    List<DoubleSolution> solutions = new ArrayList();

    assertThrows(EmptyCollectionException.class, () -> neighborhood.getNeighbors(solutions, 1)) ;
  }

  @Test
  void getNeighboursRaisesAnExceptionIfTheSolutionListIsEmpty() {
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    DoubleSolution solution = problem.createSolution() ;
    var solutions = List.of(solution) ;
    var neighborhood = new RingNeighborhood<DoubleSolution>() ;

    assertThrows(InvalidConditionException.class, () -> neighborhood.getNeighbors(solutions, 1)) ;
  }

  @Test
  void getNeighboursRaisesAnExceptionIfIndexIsEqualToTheSolutionListSize() {
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    var solutions = List.of(problem.createSolution(), problem.createSolution(), problem.createSolution()) ;
    var neighborhood = new RingNeighborhood<DoubleSolution>() ;

    assertThrows(InvalidConditionException.class, () -> neighborhood.getNeighbors(solutions, 3)) ;
  }

  @Test
  void getNeighboursRaisesAnExceptionIfIndexIsHigherThanTheSolutionListSize() {
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    var solutions = List.of(problem.createSolution(), problem.createSolution(), problem.createSolution()) ;
    var neighborhood = new RingNeighborhood<DoubleSolution>() ;

    assertThrows(InvalidConditionException.class, () -> neighborhood.getNeighbors(solutions, 5)) ;
  }

  @Test
  void getNeighboursWithIndexZeroOnAListWithTwoSolutionsReturnsTwoCopiesOfTheSecondSolution(){
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    var solutions = List.of(solution1, solution2) ;

    var neighborhood = new RingNeighborhood<DoubleSolution>() ;
    var neighbors = neighborhood.getNeighbors(solutions, 0) ;

    assertEquals(2, neighbors.size());
    assertSame(solution2, neighbors.get(0));
    assertSame(solution2, neighbors.get(1));
  }

  @Test
  void getNeighboursWithIndexOneOnAListWithTwoSolutionsReturnsTwoCopiesOfTheFirstSolution(){
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    var solutions = List.of(solution1, solution2) ;

    var neighborhood = new RingNeighborhood<DoubleSolution>() ;
    var neighbors = neighborhood.getNeighbors(solutions, 1) ;

    assertEquals(2, neighbors.size());
    assertSame(solution1, neighbors.get(0));
    assertSame(solution1, neighbors.get(1));
  }

  @Test
  void getNeighboursWorksProperly(){
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;
    var solutions = List.of(solution1, solution2, solution3) ;

    var neighborhood = new RingNeighborhood<DoubleSolution>() ;
    var neighbors = neighborhood.getNeighbors(solutions, 1) ;

    assertEquals(2, neighbors.size());
    assertSame(solution1, neighbors.get(0));
    assertSame(solution3, neighbors.get(1));
  }
}