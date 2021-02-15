package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE37Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE37();

    assertEquals(4, problem.getNumberOfVariables());
    assertEquals(3, problem.objectives().length);
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE37", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE37();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(4, solution.getNumberOfVariables());
    assertEquals(3, solution.objectives().length);
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
