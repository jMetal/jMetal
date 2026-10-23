package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM26Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM26();

    assertEquals(3, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(2, problem.numberOfConstraints());
    assertEquals("RCM26", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM26();
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
    // convention, for x = (0.5, 1.2, 0.7): the inequality first, then the equality
    DoubleProblem problem = new RCM26();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 0.5);
    solution.variables().set(1, 1.2);
    solution.variables().set(2, 0.7);

    problem.evaluate(solution);

    assertEquals(1.2, solution.objectives()[0], 1e-09);
    assertEquals(-0.95, solution.objectives()[1], 1e-09);
    assertEquals(-1.7, solution.constraints()[0], 2e-09);
    assertEquals(-0.10228842382440427, solution.constraints()[1], 1e-09);
  }
}
