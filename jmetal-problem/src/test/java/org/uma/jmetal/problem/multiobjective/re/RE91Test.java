package org.uma.jmetal.problem.multiobjective.re;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RE91Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE91();

    assertEquals(7, problem.getNumberOfVariables());
    assertEquals(9, problem.getNumberOfObjectives());
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE91", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE91();
    var solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.variables().size());
    assertEquals(9, solution.objectives().length);
    assertEquals(0, solution.constraints().length);
  }
}
