package org.uma.jmetal.problem.multiobjective.dtlz;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DTLZ1Test {
  @Test
  void constructorMustCreateAValidInstanceUsingTheDefaultConstructor() {
    int defaultNumberOfVariables = 7;
    int defaultNumberObjectives = 3;
    DoubleProblem problem = new DTLZ1();

    assertThat(problem.numberOfVariables()).isEqualTo(defaultNumberOfVariables);
    assertThat(problem.numberOfObjectives()).isEqualTo(defaultNumberObjectives);
    assertThat(problem.numberOfConstraints()).isZero();
    assertThat(problem.name()).isEqualTo("DTLZ1");

    assertThat(problem.variableBounds().get(0).getLowerBound()).isZero();
    assertThat(problem.variableBounds().get(0).getUpperBound()).isEqualTo(1);
    assertThat(problem.variableBounds().get(problem.numberOfVariables() - 1).getLowerBound())
        .isZero();
    assertThat(problem.variableBounds().get(problem.numberOfVariables() - 1).getUpperBound())
        .isEqualTo(1);
  }

  @Test
  void
      constructorMustCreateAValidInstanceWhenIndicatingTheNumberOVariablesAndTheNumberOfObjectives() {
    int numberOfVariables = 10;
    int numberOfObjectives = 3;
    DoubleProblem problem = new DTLZ1(numberOfVariables, numberOfObjectives);

    assertThat(problem.numberOfVariables()).isEqualTo(numberOfVariables);
    assertThat(problem.numberOfObjectives()).isEqualTo(numberOfObjectives);
    assertThat(problem.numberOfConstraints()).isZero();
    assertThat(problem.name()).isEqualTo("DTLZ1");

    assertThat(problem.variableBounds().get(0).getLowerBound()).isZero();
    assertThat(problem.variableBounds().get(0).getUpperBound()).isEqualTo(1);
    assertThat(problem.variableBounds().get(problem.numberOfVariables() - 1).getLowerBound())
        .isZero();
    assertThat(problem.variableBounds().get(problem.numberOfVariables() - 1).getUpperBound())
        .isEqualTo(1);
  }

  @Test
  void createSolutionGeneratesAValidSolution() {
    DoubleProblem problem = new DTLZ1();
    DoubleSolution solution = problem.createSolution();

    int defaultNumberOfVariables = 7;
    int defaultNumberObjectives = 3;

    assertThat(solution).isNotNull();
    assertThat(solution.variables()).hasSize(defaultNumberOfVariables);
    assertThat(solution.objectives()).hasSize(defaultNumberObjectives);
    assertThat(solution.constraints()).isEmpty();

    /*
    for (int i = 0; i < defaultNumberOfVariables; i++) {
      solution.variables().set(i, 1.0);
    }
    problem.evaluate(solution);
    for (int i = 0; i < defaultNumberObjectives; i++) {
      System.out.println("Objective " + i + ": " + solution.objectives()[i]);
    }
     */
  }
}
