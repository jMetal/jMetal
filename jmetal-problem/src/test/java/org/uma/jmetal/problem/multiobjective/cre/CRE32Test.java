package org.uma.jmetal.problem.multiobjective.cre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class CRE32Test {
  @Test
  void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE32();

    assertEquals(6, problem.getNumberOfVariables());
    assertEquals(3, problem.getNumberOfObjectives());
    assertEquals(9, problem.getNumberOfConstraints());
    assertEquals("CRE32", problem.getName());
  }

  @Test
  void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE32();
    var solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(6, solution.variables().size());
    assertEquals(3, solution.objectives().length);
    assertEquals(9, solution.constraints().length);
  }
}
