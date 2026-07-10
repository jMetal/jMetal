package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

class RCM03TwoBarTrussDesignProblemTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM03TwoBarTrussDesignProblem();

    assertEquals(3, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(3, problem.numberOfConstraints());
    assertEquals("TwoBarTrussDesignProblem", problem.name());

    assertEquals(1e-5, problem.variableBounds().get(0).getLowerBound(), EPSILON);
    assertEquals(100.0, problem.variableBounds().get(0).getUpperBound(), EPSILON);
    assertEquals(1.0, problem.variableBounds().get(2).getLowerBound(), EPSILON);
    assertEquals(3.0, problem.variableBounds().get(2).getUpperBound(), EPSILON);
  }

  @Test
  public void shouldEvaluateAFeasiblePointBeFlaggedAsFeasible() {
    DoubleProblem problem = new RCM03TwoBarTrussDesignProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 0.001);
    solution.variables().set(1, 0.01);
    solution.variables().set(2, 1.0);

    problem.evaluate(solution);

    assertTrue(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldEvaluateAnInfeasiblePointBeFlaggedAsInfeasible() {
    DoubleProblem problem = new RCM03TwoBarTrussDesignProblem();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 100.0);
    solution.variables().set(1, 100.0);
    solution.variables().set(2, 3.0);

    problem.evaluate(solution);

    assertTrue(solution.constraints()[0] < 0);
    assertFalse(ConstraintHandling.isFeasible(solution));
  }
}
