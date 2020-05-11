package org.uma.jmetal.operator.crossover;

import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.CompositeCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompositeCrossoverTest {
  @Test(expected = NullParameterException.class)
  public void shouldConstructorRaiseAnExceptionIfTheParameterListIsNull() {
    new CompositeCrossover(null);
  }

  @Test(expected = EmptyCollectionException.class)
  public void shouldConstructorRaiseAnExceptionIfTheParameterListIsEmpty() {
    new CompositeCrossover(Collections.emptyList());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingASingleCrossoverOperator() {
    SBXCrossover sbxCrossover = new SBXCrossover(0.9, 20.0);
    List<CrossoverOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(sbxCrossover);

    CompositeCrossover operator = new CompositeCrossover(operatorList);
    assertNotNull(operator);
    assertEquals(1, operator.getOperators().size());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingTwoCrossoverOperators() {
    SBXCrossover sbxCrossover = new SBXCrossover(0.9, 20.0);
    SinglePointCrossover singlePointCrossover = new SinglePointCrossover(0.9);

    List<CrossoverOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(sbxCrossover);
    operatorList.add(singlePointCrossover);

    CompositeCrossover operator = new CompositeCrossover(operatorList);
    assertNotNull(operator);
    assertEquals(2, operator.getOperators().size());
  }

  @Test
  public void shouldExecuteWorkProperlyWithASingleCrossoverOperator() {
    CompositeCrossover operator =
        new CompositeCrossover(Arrays.asList(new SBXCrossover(1.0, 20.0)));
    DummyDoubleProblem problem = new DummyDoubleProblem();
    CompositeSolution solution1 = new CompositeSolution(Arrays.asList(problem.createSolution()));
    CompositeSolution solution2 = new CompositeSolution(Arrays.asList(problem.createSolution()));

    List<CompositeSolution> children = operator.execute(Arrays.asList(solution1, solution2));

    assertNotNull(children);
    assertEquals(2, children.size());
    assertEquals(1, children.get(0).getNumberOfVariables());
    assertEquals(1, children.get(1).getNumberOfVariables());
  }

  @Test
  public void shouldExecuteWorkProperlyWithTwoCrossoverOperators() {
    CompositeCrossover operator =
        new CompositeCrossover(
            Arrays.asList(new SBXCrossover(1.0, 20.0), new SinglePointCrossover(1.0)));

    DummyDoubleProblem doubleProblem = new DummyDoubleProblem(2, 2, 0);
    CompositeSolution solution1 =
        new CompositeSolution(
            Arrays.asList(
                doubleProblem.createSolution(),
                new DefaultBinarySolution(Arrays.asList(20, 20), 2)));
    CompositeSolution solution2 =
        new CompositeSolution(
            Arrays.asList(
                doubleProblem.createSolution(),
                new DefaultBinarySolution(Arrays.asList(20, 20), 2)));

    List<CompositeSolution> children = operator.execute(Arrays.asList(solution1, solution2));

    assertNotNull(children);
    assertEquals(2, children.size());
    assertEquals(2, children.get(0).getNumberOfVariables());
    assertEquals(2, children.get(1).getNumberOfVariables());
  }

  @Test (expected = ClassCastException.class)
  public void shouldExecuteRaiseAnExceptionIfTheTypesOfTheSolutionsDoNotMatchTheCrossoverOperators() {
    CompositeCrossover operator =
            new CompositeCrossover(
                    Arrays.asList(new SBXCrossover(1.0, 20.0), new SinglePointCrossover(1.0)));

    DummyDoubleProblem doubleProblem = new DummyDoubleProblem(2, 2, 0);
    CompositeSolution solution1 =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution()));
    CompositeSolution solution2 =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution()));

    operator.execute(Arrays.asList(solution1, solution2));
  }
}
