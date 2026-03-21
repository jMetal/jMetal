package org.uma.jmetal.algorithm.multiobjective.ibea;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class IBEATest {

  @Test
  void runDoesNotUseTournamentSelectionWhenArchiveContainsOneSolution() {
    FakeDoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    @SuppressWarnings("unchecked")
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
        mock(SelectionOperator.class);

    IBEA<DoubleSolution> algorithm =
        new IBEA<>(
            problem,
            1,
            1,
            2,
            selectionOperator,
            new NullCrossover<>(),
            new NullMutation<>());

    assertDoesNotThrow(algorithm::run);
    verify(selectionOperator, never()).execute(anyList());
    assertEquals(1, algorithm.result().size());
  }

  @Test
  void modifiedIBEARunDoesNotFailWhenNonDominatedArchiveShrinksToOneSolution() {
    FakeDoubleProblem problem = new CollapsingArchiveProblem();
    @SuppressWarnings("unchecked")
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator =
        mock(SelectionOperator.class);

    mIBEA<DoubleSolution> algorithm =
        new mIBEA<>(
            problem,
            2,
            2,
            4,
            selectionOperator,
            new NullCrossover<>(),
            new NullMutation<>());

    assertDoesNotThrow(algorithm::run);
    verify(selectionOperator, never()).execute(anyList());
    assertEquals(1, algorithm.result().size());
  }

  private static class CollapsingArchiveProblem extends FakeDoubleProblem {
    CollapsingArchiveProblem() {
      super(1, 2, 0);
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[0] = 0.0;
      solution.objectives()[1] = 0.0;

      return solution;
    }
  }
}
