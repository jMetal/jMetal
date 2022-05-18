package org.uma.jmetal.util.comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.NumberOfViolatedConstraintsComparator;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
  Test cases:
  - If the first solution in method {@link NumberOfViolatedConstraintsComparator#compare(Solution, Solution)}  is null throws and exception
  - If the second solution in method {@link NumberOfViolatedConstraintsComparator#compare(Solution, Solution)} is null throws and exception
  - The two solutions have no constraints
  - The two solutions are feasible
  - The first solution is feasible and the second one is not
  - The second solution is feasible and the first one is not
  - Both solutions have the same number of violated constraints
  - The first solutions violates more constraints than the second solution
  - The second solutions violates more constraints than the first solution
 */
public class NumberOfViolatedConstraintsComparatorTest {
  public NumberOfViolatedConstraintsComparator<DoubleSolution> comparator ;

  @BeforeEach
  public void setup() {
    comparator = new NumberOfViolatedConstraintsComparator<>() ;
  }

  @Test
  public void compareRaiseAnExceptionIfTheFirstSolutionIsNull() {
    DoubleSolution solution1 = null ;
    DoubleSolution solution2 = Mockito.mock(DoubleSolution.class) ;
    assertThrows(NullParameterException.class, () -> comparator.compare(solution1, solution2)) ;
  }

  @Test
  public void compareRaiseAnExceptionIfTheSecondSolutionIsNull() {
    DoubleSolution solution1 = Mockito.mock(DoubleSolution.class) ;
    DoubleSolution solution2 = null ;
    assertThrows(NullParameterException.class, () -> comparator.compare(solution1, solution2)) ;
  }

  @Test
  public void compareReturnsZeroIfTheSolutionsHaveNoConstraints() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(0); ;
  }

  @Test
  public void compareReturnsZeroIfTheSolutionsHaveConstraintsAndTheyAreFeasible() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = 1.0 ;
    solution2.constraints()[0] = 1.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(0); ;
  }

  @Test
  public void compareReturnsMinusOneIfTheFirstSolutionIsFeasibleAndTheSecondOneIsNot() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = 0.0 ;
    solution2.constraints()[0] = -1.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(-1); ;
  }

  @Test
  public void compareReturnsOneIfTheSecondSolutionIsFeasibleAndTheFirstOneIsNot() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 1) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = -2.0 ;
    solution2.constraints()[0] = 0.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(1); ;
  }

  @Test
  public void compareReturnsZeroIfBothSolutionsHaveTheSameNumberOfViolatedConstraints() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 3) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = -1.0 ;
    solution1.constraints()[1] = 0.0 ;
    solution1.constraints()[2] = -2.0 ;
    solution2.constraints()[0] = -3.0 ;
    solution2.constraints()[1] = -4.0 ;
    solution2.constraints()[2] = 0.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(0); ;
  }

  @Test
  public void compareReturnsOneIfTheFirstSolutionViolatesMoreConstraintsThanTheSecondOne() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 3) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = -1.0 ;
    solution1.constraints()[1] = 0.0 ;
    solution1.constraints()[2] = -2.0 ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = -4.0 ;
    solution2.constraints()[2] = 0.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(1);
  }

  @Test
  public void compareReturnsMinusOneIfTheSecondSolutionViolatesMoreConstraintsThanTheFirstOne() {
    DummyDoubleProblem problem = new DummyDoubleProblem(2, 2, 3) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    solution1.constraints()[0] = -1.0 ;
    solution1.constraints()[1] = 0.0 ;
    solution1.constraints()[2] = 0.0 ;
    solution2.constraints()[0] = -2.0 ;
    solution2.constraints()[1] = -4.0 ;
    solution2.constraints()[2] = 0.0 ;

    assertThat(comparator.compare(solution1, solution2)).isEqualTo(-1); ;
  }
}