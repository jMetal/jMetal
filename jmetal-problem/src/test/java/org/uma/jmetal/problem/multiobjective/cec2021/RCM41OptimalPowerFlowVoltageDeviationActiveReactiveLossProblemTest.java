package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM41OptimalPowerFlowVoltageDeviationActiveReactiveLossProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM41OptimalPowerFlowVoltageDeviationActiveReactiveLossProblem();

    assertEquals(34, problem.numberOfVariables());
    assertEquals(3, problem.numberOfObjectives());
    assertEquals(26, problem.numberOfConstraints());
    assertEquals("OptimalPowerFlowVoltageDeviationActiveReactiveLossProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM41OptimalPowerFlowVoltageDeviationActiveReactiveLossProblem();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    for (double objective : solution.objectives()) {
      assertFalse(Double.isNaN(objective));
      assertFalse(Double.isInfinite(objective));
    }
    for (double constraint : solution.constraints()) {
      assertFalse(Double.isNaN(constraint));
      assertFalse(Double.isInfinite(constraint));
    }
    assertEquals(26, solution.constraints().length);
  }
}
