package org.uma.jmetal.problem.multiobjective.cec2021;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;

class RCM22HaverlyPoolingProblemTest {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new RCM22HaverlyPoolingProblem();

    assertEquals(9, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(6, problem.numberOfConstraints());
    assertEquals("HaverlyPoolingProblem", problem.name());
  }

  @Test
  public void shouldEvaluateProduceFiniteValues() {
    DoubleProblem problem = new RCM22HaverlyPoolingProblem();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    for (double objective : solution.objectives()) {
      assertFalse(Double.isNaN(objective));
      assertFalse(Double.isInfinite(objective));
    }
    assertEquals(6, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateAHandDerivedFeasiblePointBeFlaggedAsFeasible() {
    DoubleProblem problem = new RCM22HaverlyPoolingProblem();
    DoubleSolution solution = problem.createSolution();
    // x1=30,x2=30,x3=0,x4=40,x5=10,x6=10,x7=20,x8=20,x9=1 satisfies all four equality
    // constraints exactly and both inequality constraints strictly.
    double[] x = {30, 30, 0, 40, 10, 10, 20, 20, 1};
    for (int i = 0; i < x.length; i++) {
      solution.variables().set(i, x[i]);
    }

    problem.evaluate(solution);

    assertTrue(ConstraintHandling.isFeasible(solution));
  }
}
