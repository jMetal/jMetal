package org.uma.jmetal.problem.multiobjective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Created by Antonio J. Nebro on 17/09/14.
 */
class NMMinTest {
  Problem<IntegerSolution> problem ;

  @Test
  void evaluateSimpleSolutions() {
    problem = new NMMin(1, 100, -100, -1000, 1000) ;
    var solution = problem.createSolution() ;
    solution.variables().set(0, 100);
    problem.evaluate(solution);

    assertEquals(0, (int)solution.objectives()[0]) ;
    assertEquals(200, (int)solution.objectives()[1]) ;

    solution.variables().set(0, -100);
    problem.evaluate(solution);

    assertEquals(200, (int)solution.objectives()[0]) ;
    assertEquals(0, (int)solution.objectives()[1]) ;

    solution.variables().set(0, 0);
    problem.evaluate(solution);

    assertEquals(100, (int)solution.objectives()[0]) ;
    assertEquals(100, (int)solution.objectives()[1]) ;
  }
}
