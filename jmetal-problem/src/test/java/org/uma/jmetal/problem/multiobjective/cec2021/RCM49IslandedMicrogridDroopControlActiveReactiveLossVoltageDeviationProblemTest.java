package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem();

    assertEquals(18, problem.numberOfVariables());
    assertEquals(3, problem.numberOfObjectives());
    assertEquals(12, problem.numberOfConstraints());
    assertEquals("IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM49IslandedMicrogridDroopControlActiveReactiveLossVoltageDeviationProblem();
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
    assertEquals(12, solution.constraints().length);
  }
}
