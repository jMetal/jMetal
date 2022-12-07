package org.uma.jmetal.problem.singleobjective;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RosenbrockTest {
  @Test
  void constructorMustCreateAValidInstance() {
    int variables = 3 ;
    DoubleProblem problem = new Rosenbrock(3) ;

    assertThat(problem.numberOfVariables()).isEqualTo(variables) ;
    assertThat(problem.numberOfObjectives()).isEqualTo(1) ;
    assertThat(problem.numberOfConstraints()).isZero() ;
    assertThat(problem.name()).isEqualTo("Rosenbrock") ;

    assertThat(problem.variableBounds().get(0).getLowerBound()).isEqualTo(-5.12) ;
    assertThat(problem.variableBounds().get(0).getUpperBound()).isEqualTo(5.12) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getLowerBound()).isEqualTo(-5.12) ;
    assertThat(problem.variableBounds().get(problem.numberOfVariables()-1).getUpperBound()).isEqualTo(5.12) ;
  }

  @Test
  void createSolutionGeneratesAValidSolution() {
    DoubleProblem problem = new Rosenbrock(20) ;
    DoubleSolution solution = problem.createSolution() ;

    assertThat(solution).isNotNull() ;
    assertThat(solution.variables()).hasSize(20) ;
    assertThat(solution.objectives()).hasSize(1) ;
    assertThat(solution.constraints()).isEmpty() ;
  }
}