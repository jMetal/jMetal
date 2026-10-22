package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM28TwoReactorProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM28TwoReactorProblem();

    assertEquals(7, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(8, problem.numberOfConstraints());
    assertEquals("TwoReactorProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM28TwoReactorProblem();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    for (double objective : solution.objectives()) {
      assertFalse(Double.isNaN(objective));
      assertFalse(Double.isInfinite(objective));
    }
    assertEquals(8, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateASolutionAsTheOfficialCode() {
    // Objectives and constraints of the official CEC 2021 code (CEC2021_func.m), in jMetal's
    // convention, for the feasible x = (11.18649, 0, 10, 0, 1, 0, 11.18649): the four inequalities
    // first, then the four equalities
    DoubleProblem problem = new RCM28TwoReactorProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 11.18649);
    solution.variables().set(1, 0.0);
    solution.variables().set(2, 10.0);
    solution.variables().set(3, 0.0);
    solution.variables().set(4, 1.0);
    solution.variables().set(5, 0.0);
    solution.variables().set(6, 11.18649);

    problem.evaluate(solution);

    assertEquals(133.43245, solution.objectives()[0], 1e-07);
    assertEquals(11.18649, solution.objectives()[1], 1e-08);
    assertEquals(1e-06, solution.constraints()[0], 1e-09);
    assertEquals(0.0, solution.constraints()[1], 1e-09);
    assertEquals(8.81351, solution.constraints()[2], 9e-09);
    assertEquals(0.0, solution.constraints()[3], 1e-09);
    assertEquals(0.0001, solution.constraints()[4], 1e-09);
    assertEquals(9.557905321978524e-05, solution.constraints()[5], 1e-09);
    assertEquals(0.0001, solution.constraints()[6], 1e-09);
    assertEquals(9.557905321978524e-05, solution.constraints()[7], 1e-09);
  }
}
