package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RCM48IslandedMicrogridDroopControlActiveLossVoltageDeviationProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM48IslandedMicrogridDroopControlActiveLossVoltageDeviationProblem();

    assertEquals(18, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(12, problem.numberOfConstraints());
    assertEquals("IslandedMicrogridDroopControlActiveLossVoltageDeviationProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM48IslandedMicrogridDroopControlActiveLossVoltageDeviationProblem();
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
