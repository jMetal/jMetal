package org.uma.jmetal.operator.selection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.selection.impl.NaryRandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NaryRandomSelectionTest {
  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    NaryRandomSelection<Solution<?>> selection = new NaryRandomSelection<Solution<?>>() ;
    assertThrows(NullParameterException.class, () -> selection.execute(null)) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    NaryRandomSelection<DoubleSolution> selection = new NaryRandomSelection<DoubleSolution>() ;
    List<DoubleSolution> list = new ArrayList<>() ;

    assertThrows(EmptyCollectionException.class, () -> selection.execute(list)) ;
  }

  @Test
  public void shouldDefaultConstructorReturnASingleSolution() {
    NaryRandomSelection<Solution<?>> selection = new NaryRandomSelection<Solution<?>>() ;

    int result = (int)ReflectionTestUtils.getField(selection, "numberOfSolutionsToBeReturned");
    int expectedResult = 1 ;
    assertEquals(expectedResult, result) ;
  }

  @Test
  public void shouldNonDefaultConstructorReturnTheCorrectNumberOfSolutions() {
    int solutionsToBeReturned = 4 ;
    NaryRandomSelection<Solution<?>> selection = new NaryRandomSelection<Solution<?>>(solutionsToBeReturned) ;

    int result = (int)ReflectionTestUtils.getField(selection, "numberOfSolutionsToBeReturned");
    assertEquals(solutionsToBeReturned, result) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    NaryRandomSelection<Solution<?>> selection = new NaryRandomSelection<Solution<?>>(2) ;
    List<Solution<?>> list = new ArrayList<>(1) ;
    list.add(mock(Solution.class)) ;

    Executable executable = () -> selection.execute(list);
    
    InvalidConditionException cause = assertThrows(InvalidConditionException.class, executable) ;
    assertThat(cause.getMessage(), containsString("The solution list size (1) is less than " +
    "the number of requested solutions (2)")) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListSizeIsTwoAndFourSolutionsAreRequested() {
    NaryRandomSelection<Solution<?>> selection = new NaryRandomSelection<Solution<?>>(4) ;
    List<Solution<?>> list = new ArrayList<>(2) ;
    list.add(mock(Solution.class)) ;
    list.add(mock(Solution.class)) ;

    Executable executable = () -> selection.execute(list);
    
    InvalidConditionException cause = assertThrows(InvalidConditionException.class, executable) ;
    assertThat(cause.getMessage(), containsString("The solution list size (2) is less than " +
        "the number of requested solutions (4)")) ;
  }

  @Test
  public void shouldExecuteReturnTheSolutionInTheListIfTheListContainsASolution() {
    NaryRandomSelection<IntegerSolution> selection = new NaryRandomSelection<IntegerSolution>(1) ;
    List<IntegerSolution> list = new ArrayList<>(2) ;
    IntegerSolution solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

    List<IntegerSolution> result = (List<IntegerSolution>) selection.execute(list);
    assertSame(solution, result.get(0)) ;
  }

  @Test
  public void shouldExecuteReturnTheSolutionSInTheListIfTheListContainsTwoSolutions() {
    NaryRandomSelection<BinarySolution> selection = new NaryRandomSelection<BinarySolution>(2) ;
    List<BinarySolution> list = new ArrayList<>(2) ;
    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;
    list.add(solution1) ;
    list.add(solution2) ;

    List<BinarySolution> result = (List<BinarySolution>) selection.execute(list);

    assertThat(result.get(0),
        Matchers.either(Matchers.sameInstance(solution1)).or(Matchers.sameInstance(solution2)));
    assertThat(result.get(1), Matchers.either(Matchers.sameInstance(solution1)).or(Matchers.sameInstance(solution2)));
  }

  @Test
  public void shouldExecuteReturnTheCorrectNumberOfSolutions() {
    int listSize = 20 ;
    int solutionsToBeReturned = 4 ;

    NaryRandomSelection<BinarySolution> selection = new NaryRandomSelection<BinarySolution>(solutionsToBeReturned) ;
      List<BinarySolution> list = new ArrayList<>(listSize);
      for (int i = 0; i < listSize; i++) {
          BinarySolution mock = mock(BinarySolution.class);
          list.add(mock);
      }

      List<BinarySolution> result = (List<BinarySolution>) selection.execute(list);
    assertEquals(solutionsToBeReturned, result.size());
  }

  /**
   * If the list contains 4 solutions, the result list must return all of them
   */
  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectListOfSolutions() {
    int listSize = 4 ;
    int solutionsToBeReturned = 4 ;

    List<IntegerSolution> list = new ArrayList<>(listSize) ;
    IntegerSolution[] solution = new IntegerSolution[solutionsToBeReturned] ;
    for (int i = 0; i < listSize; i++) {
      solution[i] = (mock(IntegerSolution.class)) ;
      list.add(solution[i]);
    }

    NaryRandomSelection<IntegerSolution> selection = new NaryRandomSelection<IntegerSolution>(solutionsToBeReturned) ;

    List<IntegerSolution> result = (List<IntegerSolution>) selection.execute(list);
    assertTrue(result.contains(solution[0]));
    assertTrue(result.contains(solution[1]));
    assertTrue(result.contains(solution[2]));
    assertTrue(result.contains(solution[3]));
  }
}
