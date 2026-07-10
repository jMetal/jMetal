package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM32SOPWMFor7LevelInvertersProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM32SOPWMFor7LevelInvertersProblem();

    assertEquals(25, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(24, problem.numberOfConstraints());
    assertEquals("SOPWMFor7LevelInvertersProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM32SOPWMFor7LevelInvertersProblem();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    for (double objective : solution.objectives()) {
      assertFalse(Double.isNaN(objective));
      assertFalse(Double.isInfinite(objective));
    }
    assertEquals(24, solution.constraints().length);
  }
}
