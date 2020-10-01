package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE25Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE25();

    assertEquals(4, problem.getNumberOfVariables());
    assertEquals(2, problem.getNumberOfObjectives());
    assertEquals(1, problem.getNumberOfConstraints());
    assertEquals("CRE25", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE25();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(4, solution.getNumberOfVariables());
    assertEquals(2, solution.getNumberOfObjectives());
    assertEquals(1, solution.getNumberOfConstraints());
  }
}
