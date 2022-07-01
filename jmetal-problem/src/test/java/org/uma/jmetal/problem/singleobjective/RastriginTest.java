package org.uma.jmetal.problem.singleobjective;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

class RastriginTest {
  @Test
  void constructorMustCreateAValidInstance() {
    int variables = 3 ;
    DoubleProblem problem = new Rastrigin(3) ;

    assertThat(problem.getNumberOfVariables()).isEqualTo(variables) ;
    assertThat(problem.getNumberOfObjectives()).isEqualTo(1) ;
    assertThat(problem.getNumberOfConstraints()).isZero() ;
    assertThat(problem.getName()).isEqualTo("Rastrigin") ;

    assertThat(problem.getVariableBounds().get(0).getLowerBound()).isEqualTo(-5.12) ;
    assertThat(problem.getVariableBounds().get(0).getUpperBound()).isEqualTo(5.12) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getLowerBound()).isEqualTo(-5.12) ;
    assertThat(problem.getVariableBounds().get(problem.getNumberOfVariables()-1).getUpperBound()).isEqualTo(5.12) ;
  }

}