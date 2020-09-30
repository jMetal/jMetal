package org.uma.jmetal.problem.multiobjective.re;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RE31Test {

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RE31();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(3, problem.getNumberOfObjectives());
    assertEquals(0, problem.getNumberOfConstraints());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new RE31();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.getNumberOfVariables());
    assertEquals(3, solution.getNumberOfObjectives());
    assertEquals(0, solution.getNumberOfConstraints());
  }
}
