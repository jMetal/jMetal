package org.uma.jmetal.problem.doubleproblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

class AbstractIntegerProblemTest {
  private AbstractIntegerProblem problem ;

  @BeforeEach
  public void setup() {
    problem = new AbstractIntegerProblem() {
      @Override
      public IntegerSolution evaluate(IntegerSolution solution) {
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
    problem.setVariableBounds(List.of(1,2,3), List.of(2,3,4));

    assertThat(problem.getVariableBounds()).hasSize(3) ;
    assertThat(problem.getNumberOfVariables()).isEqualTo(3) ;
  }

  @Test
  void createSolutionProducesAValidInstance() {
    problem.setVariableBounds(List.of(1,2,3), List.of(2,3,4));

    IntegerSolution solution = problem.createSolution() ;
    assertThat(solution.variables()).hasSize(3) ;
    assertThat(solution.objectives()).isEmpty(); ;
    assertThat(solution.constraints()).isEmpty(); ;
  }
}