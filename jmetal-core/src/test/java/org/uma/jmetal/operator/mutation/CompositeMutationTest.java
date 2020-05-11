package org.uma.jmetal.operator.mutation;

import org.junit.Test;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.CompositeMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompositeMutationTest {

  @Test(expected = NullParameterException.class)
  public void shouldConstructorRaiseAnExceptionIfTheParameterListIsNull() {
    new CompositeMutation(null);
  }

  @Test(expected = EmptyCollectionException.class)
  public void shouldConstructorRaiseAnExceptionIfTheParameterListIsEmtpy() {
    new CompositeMutation(new ArrayList<>());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingASingleMutationOperator() {
    PolynomialMutation mutation = new PolynomialMutation(0.9, 20.0);
    List<MutationOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(mutation);

    CompositeMutation operator = new CompositeMutation(operatorList);
    assertNotNull(operator);
    assertEquals(1, operator.getOperators().size());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingTwoMutationOperators() {
    PolynomialMutation polynomialMutation = new PolynomialMutation(0.9, 20.0);
    BitFlipMutation bitFlipMutation = new BitFlipMutation(0.9);

    List<MutationOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(polynomialMutation);
    operatorList.add(bitFlipMutation);

    CompositeMutation operator = new CompositeMutation(operatorList);
    assertNotNull(operator);
    assertEquals(2, operator.getOperators().size());
  }

  @Test
  public void shouldExecuteWorkProperlyWithASingleMutationOperator() {
    CompositeMutation operator =
            new CompositeMutation(Arrays.asList(new PolynomialMutation(1.0, 20.0)));
    DummyDoubleProblem problem = new DummyDoubleProblem();
    CompositeSolution solution = new CompositeSolution(Arrays.asList(problem.createSolution()));

    CompositeSolution mutatedSolution = operator.execute(solution) ;

    assertNotNull(mutatedSolution);
    assertEquals(1, mutatedSolution.getNumberOfVariables());
    //assertTrue(mutatedSolution.getVariable(0) instanceof PolynomialMutation);
  }

  @Test
  public void shouldExecuteWorkProperlyWithTwoMutationOperators() {
    CompositeMutation operator =
            new CompositeMutation(
                    Arrays.asList(new PolynomialMutation(1.0, 20.0), new BitFlipMutation(0.01)));

    DummyDoubleProblem doubleProblem = new DummyDoubleProblem(2, 2, 0);
    CompositeSolution solution =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            new DefaultBinarySolution(Arrays.asList(20, 20), 2)));

    CompositeSolution mutatedSolution = operator.execute(solution);

    assertNotNull(mutatedSolution);
    assertEquals(2, mutatedSolution.getNumberOfVariables());
  }

  @Test (expected = ClassCastException.class)
  public void shouldExecuteRaiseAnExceptionIfTheTypesOfTheSolutionsDoNotMatchTheMutationOperators() {
    CompositeMutation operator =
            new CompositeMutation(
                    Arrays.asList(new PolynomialMutation(1.0, 20.0), new BitFlipMutation(0.01)));

    DummyDoubleProblem doubleProblem = new DummyDoubleProblem(2, 2, 0);
    CompositeSolution solution =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution())) ;

    operator.execute(solution);
  }
}