package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

class RCM02VibratingPlatformProblemTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM02VibratingPlatformProblem();

    assertEquals(5, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(5, problem.numberOfConstraints());
    assertEquals("VibratingPlatformProblem", problem.name());

    assertEquals(0.05, problem.variableBounds().get(0).getLowerBound(), EPSILON);
    assertEquals(0.5, problem.variableBounds().get(0).getUpperBound(), EPSILON);
    assertEquals(3.0, problem.variableBounds().get(4).getLowerBound(), EPSILON);
    assertEquals(6.0, problem.variableBounds().get(4).getUpperBound(), EPSILON);
  }

  @Test
  public void shouldEvaluateAFeasiblePointBeFlaggedAsFeasible() {
    DoubleProblem problem = new RCM02VibratingPlatformProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 0.2);
    solution.variables().set(1, 0.2);
    solution.variables().set(2, 0.2);
    solution.variables().set(3, 0.4);
    solution.variables().set(4, 4.0);

    problem.evaluate(solution);

    assertTrue(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldEvaluateAnInfeasiblePointBeFlaggedAsInfeasible() {
    DoubleProblem problem = new RCM02VibratingPlatformProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 0.5);
    solution.variables().set(1, 0.2);
    solution.variables().set(2, 0.2);
    solution.variables().set(3, 0.4);
    solution.variables().set(4, 4.0);

    problem.evaluate(solution);

    assertTrue(solution.constraints()[1] < 0);
    assertFalse(ConstraintHandling.isFeasible(solution));
  }
}
