package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

class RCM01PressureVesselProblemTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM01PressureVesselProblem();

    assertEquals(4, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(2, problem.numberOfConstraints());
    assertEquals("PressureVesselProblem", problem.name());

    assertEquals(0.51, problem.variableBounds().get(0).getLowerBound(), EPSILON);
    assertEquals(99.49, problem.variableBounds().get(0).getUpperBound(), EPSILON);
    assertEquals(10.0, problem.variableBounds().get(2).getLowerBound(), EPSILON);
    assertEquals(200.0, problem.variableBounds().get(2).getUpperBound(), EPSILON);
  }

  @Test
  public void shouldEvaluateAFeasiblePointBeFlaggedAsFeasible() {
    DoubleProblem problem = new RCM01PressureVesselProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 90.0);
    solution.variables().set(1, 90.0);
    solution.variables().set(2, 10.0);
    solution.variables().set(3, 10.0);

    problem.evaluate(solution);

    assertTrue(solution.constraints()[0] >= 0);
    assertTrue(solution.constraints()[1] >= 0);
    assertTrue(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldEvaluateAnInfeasiblePointBeFlaggedAsInfeasible() {
    DoubleProblem problem = new RCM01PressureVesselProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 1.0);
    solution.variables().set(1, 1.0);
    solution.variables().set(2, 50.0);
    solution.variables().set(3, 50.0);

    problem.evaluate(solution);

    assertTrue(solution.constraints()[0] < 0);
    assertTrue(solution.constraints()[1] < 0);
    assertFalse(ConstraintHandling.isFeasible(solution));
  }
}
