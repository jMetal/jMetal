package org.uma.jmetal.problem.singleobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

class GriewankTest {
  @Test
  void constructorMustCreateAValidInstance() {
    int variables = 3 ;
    DoubleProblem problem = new Griewank(3) ;

    assertThat(problem.getNumberOfVariables()).isEqualTo(variables) ;
    assertThat(problem.getNumberOfObjectives()).isEqualTo(1) ;
    assertThat(problem.getNumberOfConstraints()).isZero() ;
    assertThat(problem.getName()).isEqualTo("Griewank") ;

    assertThat(problem.getVariableBounds().get(0).getLowerBound()).isEqualTo(-600) ;
    assertThat(problem.getVariableBounds().get(0).getUpperBound()).isEqualTo(600) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getLowerBound()).isEqualTo(-600) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getUpperBound()).isEqualTo(600) ;
  }
}