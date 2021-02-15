package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE41Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE41();

    assertEquals(7, problem.getNumberOfVariables());
    assertEquals(4, problem.objectives().length);
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE41", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE41();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.getNumberOfVariables());
    assertEquals(4, solution.objectives().length);
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
