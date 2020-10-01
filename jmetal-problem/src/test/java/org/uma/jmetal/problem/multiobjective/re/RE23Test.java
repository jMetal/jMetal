package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE23Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE23();

    assertEquals(4, problem.getNumberOfVariables());
    assertEquals(2, problem.getNumberOfObjectives());
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE23", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE23();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(4, solution.getNumberOfVariables());
    assertEquals(2, solution.getNumberOfObjectives());
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
