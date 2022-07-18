package org.uma.jmetal.operator.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.CompositeMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

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
    var mutation = new PolynomialMutation(0.9, 20.0);
    List<MutationOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(mutation);

    var operator = new CompositeMutation(operatorList);
    assertNotNull(operator);
    assertEquals(1, operator.getOperators().size());
  }

  @Test
  public void shouldConstructorCreateAValidOperatorWhenAddingTwoMutationOperators() {
    var polynomialMutation = new PolynomialMutation(0.9, 20.0);
    var bitFlipMutation = new BitFlipMutation(0.9);

    List<MutationOperator<?>> operatorList = new ArrayList<>();
    operatorList.add(polynomialMutation);
    operatorList.add(bitFlipMutation);

    var operator = new CompositeMutation(operatorList);
    assertNotNull(operator);
    assertEquals(2, operator.getOperators().size());
  }

  @Test
  public void shouldExecuteWorkProperlyWithASingleMutationOperator() {
    var operator =
            new CompositeMutation(Arrays.asList(new PolynomialMutation(1.0, 20.0)));
    var problem = new FakeDoubleProblem();
    var solution = new CompositeSolution(Arrays.asList(problem.createSolution()));

    var mutatedSolution = operator.execute(solution) ;

    assertNotNull(mutatedSolution);
    assertEquals(1, mutatedSolution.variables().size());
    //assertTrue(mutatedSolution.getVariable(0) instanceof PolynomialMutation);
  }

  @Test
  public void shouldExecuteWorkProperlyWithTwoMutationOperators() {
    var operator =
            new CompositeMutation(
                    Arrays.asList(new PolynomialMutation(1.0, 20.0), new BitFlipMutation(0.01)));

    var doubleProblem = new FakeDoubleProblem(2, 2, 0);
    var solution =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            new DefaultBinarySolution(Arrays.asList(20, 20), 2)));

    var mutatedSolution = operator.execute(solution);

    assertNotNull(mutatedSolution);
    assertEquals(2, mutatedSolution.variables().size());
  }

  @Test (expected = ClassCastException.class)
  public void shouldExecuteRaiseAnExceptionIfTheTypesOfTheSolutionsDoNotMatchTheMutationOperators() {
    var operator =
            new CompositeMutation(
                    Arrays.asList(new PolynomialMutation(1.0, 20.0), new BitFlipMutation(0.01)));

    var doubleProblem = new FakeDoubleProblem(2, 2, 0);
    var solution =
            new CompositeSolution(
                    Arrays.asList(
                            doubleProblem.createSolution(),
                            doubleProblem.createSolution())) ;

    operator.execute(solution);
  }
}