package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE22Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE22();

    assertEquals(4, problem.getNumberOfVariables());
    assertEquals(2, problem.objectives().length);
    assertEquals(4, problem.getNumberOfConstraints());
    assertEquals("CRE22", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE22();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(4, solution.getNumberOfVariables());
    assertEquals(2, solution.objectives().length);
    assertEquals(4, solution.getNumberOfConstraints());
  }
}
