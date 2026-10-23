package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM10Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM10();

    assertEquals(2, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(2, problem.numberOfConstraints());
    assertEquals("RCM10", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM10();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    for (double objective : solution.objectives()) {
      assertFalse(Double.isNaN(objective));
      assertFalse(Double.isInfinite(objective));
    }
    assertEquals(2, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateASolutionAsTheOfficialCode() {
    // Objectives and constraints of the official CEC 2021 code (CEC2021_func.m), in jMetal's
    // convention, for x = (1.0, 1.5)
    DoubleProblem problem = new RCM10();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 1.0);
    solution.variables().set(1, 1.5);

    problem.evaluate(solution);

    assertEquals(120.06673144547577, solution.objectives()[0], 1e-07);
    assertEquals(8.89383195892413e-07, solution.objectives()[1], 1e-09);
    assertEquals(19930.666666666668, solution.constraints()[0], 2e-05);
    assertEquals(20000.0, solution.constraints()[1], 2e-05);
  }
}
