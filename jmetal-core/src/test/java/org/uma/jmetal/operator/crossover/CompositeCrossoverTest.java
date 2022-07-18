package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.CompositeCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

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
    var sbxCrossover = new SBXCrossover(0.9, 20.0);
    List<CrossoverOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(sbxCrossover);

    var operator = new CompositeCrossover(operatorList);
    assertNotNull(operator);
    assertEquals(1, operator.getOperators().size());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingTwoCrossoverOperators() {
    var sbxCrossover = new SBXCrossover(0.9, 20.0);
    var singlePointCrossover = new SinglePointCrossover(0.9);

    List<CrossoverOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(sbxCrossover);
    operatorList.add(singlePointCrossover);

    var operator = new CompositeCrossover(operatorList);
    assertNotNull(operator);
    assertEquals(2, operator.getOperators().size());
  }

  @Test
  public void shouldExecuteWorkProperlyWithASingleCrossoverOperator() {
    var operator =
        new CompositeCrossover(List.of(new SBXCrossover(1.0, 20.0)));
    var problem = new FakeDoubleProblem();
    var solution1 = new CompositeSolution(List.of(problem.createSolution()));
    var solution2 = new CompositeSolution(List.of(problem.createSolution()));

    var children = operator.execute(Arrays.asList(solution1, solution2));

    assertNotNull(children);
    assertEquals(2, children.size());
    assertEquals(1, children.get(0).variables().size());
    assertEquals(1, children.get(1).variables().size());
  }

  @Test
  public void shouldExecuteWorkProperlyWithTwoCrossoverOperators() {
    var operator =
        new CompositeCrossover(
            Arrays.asList(new SBXCrossover(1.0, 20.0), new SinglePointCrossover(1.0)));

    var doubleProblem = new FakeDoubleProblem(2, 2, 0);
    var solution1 =
        new CompositeSolution(
            Arrays.asList(
                doubleProblem.createSolution(),
                new DefaultBinarySolution(Arrays.asList(20, 20), 2)));
    var solution2 =
        new CompositeSolution(
            Arrays.asList(
                doubleProblem.createSolution(),
                new DefaultBinarySolution(Arrays.asList(20, 20), 2)));

    var children = operator.execute(Arrays.asList(solution1, solution2));

    assertNotNull(children);
    assertEquals(2, children.size());
    assertEquals(2, children.get(0).variables().size());
    assertEquals(2, children.get(1).variables().size());
  }

  @Test (expected = ClassCastException.class)
  public void shouldExecuteRaiseAnExceptionIfTheTypesOfTheSolutionsDoNotMatchTheCrossoverOperators() {
    var operator =
            new CompositeCrossover(
                    Arrays.asList(new SBXCrossover(1.0, 20.0), new SinglePointCrossover(1.0)));

    var doubleProblem = new FakeDoubleProblem(2, 2, 0);
    var solution1 =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution()));
    var solution2 =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution()));

    operator.execute(Arrays.asList(solution1, solution2));
  }
}
