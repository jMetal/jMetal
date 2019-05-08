package org.uma.jmetal.algorithm.singleobjective.localsearch;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.mockito.internal.matchers.Any;
import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.doubleproblem.impl.ComposableDoubleProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class SingleObjectiveStoppingByEvaluationsLocalSearchTest {

  @Test
  public void shouldImproveCarryOutZeroIterationIfTheMaximumNumberOfEvaluationsIsZero() {
    BinarySolution solutionToImprove = mock(BinarySolution.class);
    DefaultLocalSearch<BinarySolution> localSearch =
        new SingleObjectiveStoppingByEvaluationsLocalSearch<>(null, solutionToImprove, null, 0);

    localSearch.run();

    assertEquals(0, localSearch.getEvaluations());
    assertSame(solutionToImprove, localSearch.getResult());
  }

  @Test
  public void shouldImproveCarryOutOneIterationIfTheMaximumNumberOfEvaluationsIsOne() {
    ComposableDoubleProblem problem =
        new ComposableDoubleProblem().addVariable(0, 1).addFunction((x) -> 2.0);

    List<Pair<Double, Double>> bounds = Arrays.asList(new ImmutablePair<>(0.0, 1.0));
    DoubleSolution solutionToImprove = new DefaultDoubleSolution(bounds, 2);

    DefaultLocalSearch<DoubleSolution> localSearch =
        new SingleObjectiveStoppingByEvaluationsLocalSearch<>(
            problem, solutionToImprove, new NullMutation<>(), 1);

    localSearch.run();

    assertEquals(1, localSearch.getEvaluations());
  }

  @Test
  public void shouldImproveReturnTheSameSolutionIfItIsNotImprovedInANumberOfIterations() {
    ComposableDoubleProblem problem =
        new ComposableDoubleProblem().addVariable(0, 1).addFunction((x) -> 2.0);

    List<Pair<Double, Double>> bounds = Arrays.asList(new ImmutablePair<>(0.0, 1.0));
    DoubleSolution solutionToImprove = new DefaultDoubleSolution(bounds, 2);

    MutationOperator<DoubleSolution> mutation = mock(MutationOperator.class) ;

    when(mutation.execute(solutionToImprove)).thenReturn(solutionToImprove) ;

    DefaultLocalSearch<DoubleSolution> localSearch =
        new SingleObjectiveStoppingByEvaluationsLocalSearch<>(
            problem, solutionToImprove, new NullMutation<>(), 3);

    localSearch.run();

    assertEquals(3, localSearch.getEvaluations());
    assertEquals(solutionToImprove, localSearch.getResult());
    verify(mutation, times(3)).execute(any()) ;
  }
}
