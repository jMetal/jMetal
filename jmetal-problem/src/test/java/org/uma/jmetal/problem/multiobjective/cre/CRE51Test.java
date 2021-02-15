package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE51Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE51();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(5, problem.objectives().length);
    assertEquals(7, problem.getNumberOfConstraints());
    assertEquals("CRE51", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE51();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.getNumberOfVariables());
    assertEquals(5, solution.objectives().length);
    assertEquals(7, solution.getNumberOfConstraints());
  }
}
