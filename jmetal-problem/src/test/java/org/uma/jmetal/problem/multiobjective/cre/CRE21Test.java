package org.uma.jmetal.problem.multiobjective.cre;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class CRE21Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE21();

    assertEquals(3, problem.numberOfVariables());
    assertEquals(2, problem.numberOfObjectives());
    assertEquals(3, problem.numberOfConstraints());
    assertEquals("CRE21", problem.name());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE21();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(3, solution.variables().size());
    assertEquals(2, solution.objectives().length);
    assertEquals(3, solution.constraints().length);
  }

  @Test
  public void shouldEvaluateASolutionAsTheReferenceCode() {
    // Objectives and constraints of the reference code of the suite (Tanabe and Ishibuchi's
    // reproblem.py, whose constraints are satisfied when >= 0, as in jMetal) for
    // x = (10, 20, 2)
    DoubleProblem problem = new CRE21();
    DoubleSolution solution = problem.createSolution();
    solution.variables().set(0, 10.0);
    solution.variables().set(1, 20.0);
    solution.variables().set(2, 2.0);

    problem.evaluate(solution);

    assertEquals(89.44271909999159, solution.objectives()[0], 1e-9);
    assertEquals(4.47213595499958, solution.objectives()[1], 1e-9);
    assertEquals(-89.3427190999916, solution.constraints()[0], 9e-08);
    assertEquals(99995.527864045, solution.constraints()[1], 1e-04);
    assertEquals(99995.527864045, solution.constraints()[2], 1e-04);
  }

  @Test
  public void shouldHaveTheBoundsOfTheReferenceCode() {
    DoubleProblem problem = new CRE21();

    assertEquals(1e-05, problem.variableBounds().get(0).getLowerBound());
    assertEquals(100.0, problem.variableBounds().get(0).getUpperBound());
    assertEquals(1e-05, problem.variableBounds().get(1).getLowerBound());
    assertEquals(100.0, problem.variableBounds().get(1).getUpperBound());
    assertEquals(1.0, problem.variableBounds().get(2).getLowerBound());
    assertEquals(3.0, problem.variableBounds().get(2).getUpperBound());
  }
}
