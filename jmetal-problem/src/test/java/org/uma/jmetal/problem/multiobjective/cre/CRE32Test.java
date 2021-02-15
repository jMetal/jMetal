package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE32Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE32();

    assertEquals(6, problem.getNumberOfVariables());
    assertEquals(3, problem.objectives().length);
    assertEquals(9, problem.getNumberOfConstraints());
    assertEquals("CRE32", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE32();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(6, solution.getNumberOfVariables());
    assertEquals(3, solution.objectives().length);
    assertEquals(9, solution.getNumberOfConstraints());
  }
}
