package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE22Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE22();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(2, problem.objectives().length);
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE22", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE22();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.getNumberOfVariables());
    assertEquals(2, solution.objectives().length);
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
