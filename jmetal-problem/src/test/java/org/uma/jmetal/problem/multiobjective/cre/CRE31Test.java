package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE31Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE31();

    assertEquals(7, problem.getNumberOfVariables());
    assertEquals(3, problem.objectives().length);
    assertEquals(10, problem.getNumberOfConstraints());
    assertEquals("CRE31", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE31();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.getNumberOfVariables());
    assertEquals(3, solution.objectives().length);
    assertEquals(10, solution.getNumberOfConstraints());
  }
}
