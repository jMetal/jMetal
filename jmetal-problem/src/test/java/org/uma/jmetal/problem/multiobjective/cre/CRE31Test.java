package org.uma.jmetal.problem.multiobjective.cre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class CRE31Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE31();

    assertEquals(7, problem.getNumberOfVariables());
    assertEquals(3, problem.getNumberOfObjectives());
    assertEquals(10, problem.getNumberOfConstraints());
    assertEquals("CRE31", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE31();
    var solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.variables().size());
    assertEquals(3, solution.objectives().length);
    assertEquals(10, solution.constraints().length);
  }
}
