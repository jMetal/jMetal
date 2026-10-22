package org.uma.jmetal.problem.multiobjective.cre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class CRE25Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE25();

    assertEquals(4, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(1, problem.numberOfConstraints());
    assertEquals("CRE25", problem.name());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE25();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(4, solution.variables().size());
    assertEquals(2, solution.objectives().length);
    assertEquals(1, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateASolutionAsTheReferenceCode() {
    // Objectives and constraints of the reference code of the suite (Tanabe and Ishibuchi's
    // reproblem.py, whose constraints are satisfied when >= 0, as in jMetal) for
    // x = (20.3, 31.7, 45.2, 50.9)
    DoubleProblem problem = new CRE25();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 20.3);
    solution.variables().set(1, 31.7);
    solution.variables().set(2, 45.2);
    solution.variables().set(3, 50.9);

    problem.evaluate(solution);

    assertEquals(3.3450625, solution.objectives()[0], 1e-9);
    assertEquals(51.0, solution.objectives()[1], 1e-9);
    assertEquals(0.017376641177319296, solution.constraints()[0], 1e-09);
  }
}
