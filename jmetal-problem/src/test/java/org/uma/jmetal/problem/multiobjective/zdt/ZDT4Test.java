package org.uma.jmetal.problem.multiobjective.zdt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class ZDT4Test {
  @Test
  void constructorMustCreateAValidInstanceUsingTheDefaultConstructor() {
    var defaultNumberOfVariables = 10 ;
    DoubleProblem problem = new ZDT4() ;

    assertThat(problem.getNumberOfVariables()).isEqualTo(defaultNumberOfVariables) ;
    assertThat(problem.getNumberOfObjectives()).isEqualTo(2) ;
    assertThat(problem.getNumberOfConstraints()).isZero() ;
    assertThat(problem.getName()).isEqualTo("ZDT4") ;

    assertThat(problem.getVariableBounds().get(0).getLowerBound()).isZero() ;
    assertThat(problem.getVariableBounds().get(0).getUpperBound()).isEqualTo(1) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getLowerBound()).isEqualTo(-5.0) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getUpperBound()).isEqualTo(5.0) ;
  }

  @Test
  void constructorMustCreateAValidInstanceWhenIndicatingTheNumberOVariables() {
    var numberOfVariables = 20 ;
    DoubleProblem problem = new ZDT4(numberOfVariables) ;

    assertThat(problem.getNumberOfVariables()).isEqualTo(numberOfVariables) ;
    assertThat(problem.getNumberOfObjectives()).isEqualTo(2) ;
    assertThat(problem.getNumberOfConstraints()).isZero() ;
    assertThat(problem.getName()).isEqualTo("ZDT4") ;

    assertThat(problem.getVariableBounds().get(0).getLowerBound()).isZero() ;
    assertThat(problem.getVariableBounds().get(0).getUpperBound()).isEqualTo(1) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getLowerBound()).isEqualTo(-5.0) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getUpperBound()).isEqualTo(5.0) ;
  }

  @Test
  void createSolutionGeneratesAValidSolution() {
    var numberOfVariables = 15 ;

    DoubleProblem problem = new ZDT4(numberOfVariables) ;
    var solution = problem.createSolution() ;

    assertThat(solution).isNotNull() ;
    assertThat(solution.variables()).hasSize(numberOfVariables) ;
    assertThat(solution.objectives()).hasSize(2) ;
    assertThat(solution.constraints()).isEmpty() ;
  }
}