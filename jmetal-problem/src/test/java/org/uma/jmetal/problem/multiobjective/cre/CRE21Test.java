package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.re.RE21;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.*;

class CRE21Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE21();

    assertEquals(3, problem.getNumberOfVariables());
    assertEquals(2, problem.getNumberOfObjectives());
    assertEquals(3, problem.getNumberOfConstraints());
    assertEquals("CRE21", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE21();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.getNumberOfVariables());
    assertEquals(2, solution.getNumberOfObjectives());
    assertEquals(3, solution.getNumberOfConstraints());
  }
}
