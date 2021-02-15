package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE61Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE61();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(6, problem.objectives().length);
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE61", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE61();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.getNumberOfVariables());
    assertEquals(6, solution.objectives().length);
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
