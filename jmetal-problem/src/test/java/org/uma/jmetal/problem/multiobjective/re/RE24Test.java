package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE24Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE24();

    assertEquals(2, problem.getNumberOfVariables());
    assertEquals(2, problem.getNumberOfObjectives());
    assertEquals(0, problem.getNumberOfConstraints());
    assertEquals("RE24", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE24();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(2, solution.variables().size());
    assertEquals(2, solution.objectives().length);
    assertEquals(0, solution.constraints().length);
  }
}
