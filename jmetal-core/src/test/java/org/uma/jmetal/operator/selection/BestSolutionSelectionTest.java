package org.uma.jmetal.operator.selection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.selection.impl.BestSolutionSelection;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class BestSolutionSelectionTest {
  @Test
  void shouldConstructorCreateANonNullObject() {
    var operator = new BestSolutionSelection<>(new ObjectiveComparator<>(0));

    assertNotNull(operator);
  }

  @Test
  void shouldConstructorRaiseAnExceptionIfTheComparatorIsNull() {
    assertThrows(NullParameterException.class, () -> new BestSolutionSelection<>(null));
  }

  @Test
  void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    var operator = new BestSolutionSelection<>(new ObjectiveComparator<>(0));

    assertThrows(NullParameterException.class, () -> operator.execute(null));
  }

  @Test
  void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    var operator = new BestSolutionSelection<>(new ObjectiveComparator<>(0));

    assertThrows(EmptyCollectionException.class, () -> operator.execute(List.of()));
  }

  @Test
  void shouldExecuteWorkProperlyIfTheListContainsASolution() {
    var operator = new BestSolutionSelection<DoubleSolution>(new ObjectiveComparator<>(0));
    var dummyDoubleProblem = new FakeDoubleProblem(3, 2, 0) ;

    var solution = dummyDoubleProblem.createSolution() ;

    assertSame(solution, operator.execute(List.of(solution)));
  }

  @Test
  void shouldExecuteWorkProperlyIfTheListContainTwoSingleObjectiveSolutionsAndTheFirstOneIsBetter() {
    var operator = new BestSolutionSelection<DoubleSolution>(new ObjectiveComparator<>(0));
    var dummyDoubleProblem = new FakeDoubleProblem(2, 1, 0) ;

    var solution1 = dummyDoubleProblem.createSolution() ;
    var solution2 = dummyDoubleProblem.createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution2.objectives()[0] = 2.0 ;

    assertSame(solution1, operator.execute(List.of(solution1, solution2)));
  }

  @Test
  void shouldExecuteWorkProperlyIfTheListContainTwoSingleObjectiveSolutionsAndTheSecondOneIsBetter() {
    var operator = new BestSolutionSelection<DoubleSolution>(new ObjectiveComparator<>(0));
    var dummyDoubleProblem = new FakeDoubleProblem(2, 1, 0) ;

    var solution1 = dummyDoubleProblem.createSolution() ;
    var solution2 = dummyDoubleProblem.createSolution() ;
    solution1.objectives()[0] = 3.0 ;
    solution2.objectives()[0] = 2.0 ;

    assertSame(solution2, operator.execute(List.of(solution1, solution2)));
  }

  @Test
  void shouldExecuteWorkProperlyIfTheListContainThreeSolutions() {
    var operator = new BestSolutionSelection<DoubleSolution>(new DominanceWithConstraintsComparator<>());
    var dummyDoubleProblem = new FakeDoubleProblem(2, 3, 0) ;

    var solution1 = dummyDoubleProblem.createSolution() ;
    var solution2 = dummyDoubleProblem.createSolution() ;
    var solution3 = dummyDoubleProblem.createSolution() ;

    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 2.0 ;
    solution1.objectives()[2] = 0.0 ;

    solution2.objectives()[0] = 1.0 ;
    solution2.objectives()[1] = 0.0 ;
    solution2.objectives()[2] = 1.0 ;

    solution3.objectives()[0] = 0.0 ;
    solution3.objectives()[1] = 0.0 ;
    solution3.objectives()[2] = 0.0 ;

    assertSame(solution3, operator.execute(List.of(solution1, solution2, solution3)));
  }
}
