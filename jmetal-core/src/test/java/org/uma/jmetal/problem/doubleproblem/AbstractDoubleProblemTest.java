package org.uma.jmetal.problem.doubleproblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class AbstractDoubleProblemTest {
  private AbstractDoubleProblem problem ;

  @BeforeEach
  public void setup() {
    problem = new AbstractDoubleProblem() {
      @Override
      public DoubleSolution evaluate(DoubleSolution solution) {
        return null;
      }
    } ;
  }

  @Test
  void constructorCreatesADefaultConfiguredProblemInstance() {
    assertThat(problem.getNumberOfObjectives()).isZero() ;
    assertThat(problem.getNumberOfConstraints()).isZero() ;
    assertThatThrownBy(() -> problem.getNumberOfVariables()).isInstanceOf(NullPointerException.class) ;
  }

  @Test
  void setVariableBoundsWorkProperly() {
    problem.setVariableBounds(List.of(1.0,2.0,3.0), List.of(2.0,3.0,4.0));

    assertThat(problem.getVariableBounds()).hasSize(3) ;
    assertThat(problem.getNumberOfVariables()).isEqualTo(3) ;

  }
}