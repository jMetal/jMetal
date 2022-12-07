package org.uma.jmetal.problem.multiobjective.zdt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class ZDT4Test {
  @Test
  void constructorMustCreateAValidInstanceUsingTheDefaultConstructor() {
    int defaultNumberOfVariables = 10 ;
    DoubleProblem problem = new ZDT4() ;

    assertThat(problem.numberOfVariables()).isEqualTo(defaultNumberOfVariables) ;
    assertThat(problem.numberOfObjectives()).isEqualTo(2) ;
    assertThat(problem.numberOfConstraints()).isZero() ;
    assertThat(problem.name()).isEqualTo("ZDT4") ;

    assertThat(problem.variableBounds().get(0).getLowerBound()).isZero() ;
    assertThat(problem.variableBounds().get(0).getUpperBound()).isEqualTo(1) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getLowerBound()).isEqualTo(-5.0) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getUpperBound()).isEqualTo(5.0) ;
  }

  @Test
  void constructorMustCreateAValidInstanceWhenIndicatingTheNumberOVariables() {
    int numberOfVariables = 20 ;
    DoubleProblem problem = new ZDT4(numberOfVariables) ;

    assertThat(problem.numberOfVariables()).isEqualTo(numberOfVariables) ;
    assertThat(problem.numberOfObjectives()).isEqualTo(2) ;
    assertThat(problem.numberOfConstraints()).isZero() ;
    assertThat(problem.name()).isEqualTo("ZDT4") ;

    assertThat(problem.variableBounds().get(0).getLowerBound()).isZero() ;
    assertThat(problem.variableBounds().get(0).getUpperBound()).isEqualTo(1) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getLowerBound()).isEqualTo(-5.0) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getUpperBound()).isEqualTo(5.0) ;
  }

  @Test
  void createSolutionGeneratesAValidSolution() {
    int numberOfVariables = 15 ;

    DoubleProblem problem = new ZDT4(numberOfVariables) ;
    DoubleSolution solution = problem.createSolution() ;

    assertThat(solution).isNotNull() ;
    assertThat(solution.variables()).hasSize(numberOfVariables) ;
    assertThat(solution.objectives()).hasSize(2) ;
    assertThat(solution.constraints()).isEmpty() ;
  }
}