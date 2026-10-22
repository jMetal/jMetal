package org.uma.jmetal.problem.multiobjective.cre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class CRE31Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE31();

    assertEquals(7, problem.numberOfVariables());
    assertEquals(3, problem.numberOfObjectives());
    assertEquals(10, problem.numberOfConstraints());
    assertEquals("CRE31", problem.name());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE31();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.variables().size());
    assertEquals(3, solution.objectives().length);
    assertEquals(10, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateASolutionAsTheReferenceCode() {
    // Objectives and constraints of the reference code of the suite (Tanabe and Ishibuchi's
    // reproblem.py, whose constraints are satisfied when >= 0, as in jMetal) for
    // x = (1.0, 0.9, 1.2, 1.0, 0.8, 1.0, 1.1)
    DoubleProblem problem = new CRE31();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 1.0);
    solution.variables().set(1, 0.9);
    solution.variables().set(2, 1.2);
    solution.variables().set(3, 1.0);
    solution.variables().set(4, 0.8);
    solution.variables().set(5, 1.0);
    solution.variables().set(6, 1.1);

    problem.evaluate(solution);

    assertEquals(29.696009999999998, solution.objectives()[0], 1e-9);
    assertEquals(4.0148, solution.objectives()[1], 1e-9);
    assertEquals(12.249022499999999, solution.objectives()[2], 1e-9);
    assertEquals(0.18568136000000013, solution.constraints()[0], 1e-09);
    assertEquals(0.12770959999999998, solution.constraints()[1], 1e-09);
    assertEquals(0.1291072, solution.constraints()[2], 1e-09);
    assertEquals(0.01774440000000005, solution.constraints()[3], 1e-09);
    assertEquals(3.8941550000000014, solution.constraints()[4], 4e-09);
    assertEquals(4.896930000000001, solution.constraints()[5], 5e-09);
    assertEquals(-0.9995000000000047, solution.constraints()[6], 1e-09);
    assertEquals(-0.014800000000000146, solution.constraints()[7], 1e-09);
    assertEquals(0.5320750000000007, solution.constraints()[8], 1e-09);
    assertEquals(0.5698799999999995, solution.constraints()[9], 1e-09);
  }
}
