package org.uma.jmetal.problem.multiobjective;

import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 17/09/14.
 */
public class NMMinTest {
  Problem<IntegerSolution> problem ;

  @Test
  public void evaluateSimpleSolutions() {
    problem = new NMMin(1, 100, -100, -1000, 1000) ;
    IntegerSolution solution = problem.createSolution() ;
    solution.setVariableValue(0, 100);
    problem.evaluate(solution);

    assertEquals(0, (int)solution.getObjective(0)) ;
    assertEquals(200, (int)solution.getObjective(1)) ;

    solution.setVariableValue(0, -100);
    problem.evaluate(solution);

    assertEquals(200, (int)solution.getObjective(0)) ;
    assertEquals(0, (int)solution.getObjective(1)) ;

    solution.setVariableValue(0, 0);
    problem.evaluate(solution);

    assertEquals(100, (int)solution.getObjective(0)) ;
    assertEquals(100, (int)solution.getObjective(1)) ;
  }

}
