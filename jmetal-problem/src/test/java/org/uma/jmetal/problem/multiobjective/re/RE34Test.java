package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE34Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE34();

    assertEquals(5, problem.getNumberOfVariables());
    assertEquals(3, problem.getNumberOfObjectives());
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE34", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE34();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(5, solution.variables().size());
    assertEquals(3, solution.objectives().length);
    assertEquals(0, solution.constraints().length);
  }
}
