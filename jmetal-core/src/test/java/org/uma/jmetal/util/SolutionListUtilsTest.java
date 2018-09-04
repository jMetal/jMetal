package org.uma.jmetal.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
  * @version 1.0
 */
public class SolutionListUtilsTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /***** Unit tests to method findBestSolution ****/
  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;

    SolutionListUtils.findBestSolution(null, comparator) ;
  }

  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    @SuppressWarnings("unchecked")
    Comparator<DoubleSolution> comparator = mock(Comparator.class) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    SolutionListUtils.findBestSolution(list, comparator) ;
  }

  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The comparator is null"));

    List<DoubleSolution> list = new ArrayList<>() ;
    list.add(mock(DoubleSolution.class)) ;

    SolutionListUtils.findBestSolution(list, null) ;
  }

  @Test
  public void shouldFindBestSolutionReturnTheSolutionInTheListWhenItContainsOneSolution() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    IntegerSolution solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

    assertSame(solution, SolutionListUtils.findBestSolution(list, comparator)) ;
  }

  @Test
  public void shouldFindBestSolutionReturnTheSecondSolutionInTheListIfIsTheBestOufOfTwoSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    list.add(solution1) ;
    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    list.add(solution2) ;

    when(comparator.compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject())).thenReturn(1) ;

    assertSame(solution2, SolutionListUtils.findBestSolution(list, comparator)) ;
  }

  @Test
  public void shouldFindBestSolutionReturnTheLastOneIfThisIsTheBestSolutionInALastInAListWithFiveSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0 ; i < 5; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    when(comparator.compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject())).thenReturn(1, 0, 0, 1) ;
    assertSame(list.get(4), SolutionListUtils.findBestSolution(list, comparator));
    verify(comparator, times(4)).compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject()) ;
  }

  /***** Unit tests to method findIndexOfBestSolution ****/
  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;

    SolutionListUtils.findIndexOfBestSolution(null, comparator) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    @SuppressWarnings("unchecked")
    Comparator<DoubleSolution> comparator = mock(Comparator.class) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    SolutionListUtils.findIndexOfBestSolution(list, comparator) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The comparator is null"));

    List<DoubleSolution> list = new ArrayList<>() ;
    list.add(mock(DoubleSolution.class)) ;

    SolutionListUtils.findIndexOfBestSolution(list, null) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionReturnZeroIfTheListWhenItContainsOneSolution() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    IntegerSolution solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

    assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator)) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionReturnZeroIfTheFirstSolutionItTheBestOutOfTwoSolutionsInTheList() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    list.add(solution1) ;
    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    list.add(solution2) ;

    when(comparator.compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject())).thenReturn(0) ;
    assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator).compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject()) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionReturnOneIfTheSecondSolutionItTheBestOutOfTwoSolutionInTheList() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    list.add(solution1) ;
    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    list.add(solution2) ;

    when(comparator.compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject())).thenReturn(1) ;
    assertEquals(1, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator).compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject()) ;
  }

  @Test
  public void shouldFindIndexOfBestSolutionReturn4IfTheBestSolutionIsTheLastInAListWithFiveSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0 ; i < 5; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    when(comparator.compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject())).thenReturn(1,0,0,1) ;
    assertEquals(4, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator, times(4)).compare(Matchers.<IntegerSolution> anyObject(), Matchers.<IntegerSolution> anyObject()) ;
  }

  /***** Unit tests to method selectNRandomDifferentSolutions ****/
  @Test
  public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    SolutionListUtils.selectNRandomDifferentSolutions(1, null) ;
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    List<DoubleSolution> list = new ArrayList<>() ;

    SolutionListUtils.selectNRandomDifferentSolutions(1, list) ;
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnASingleSolution() {
    List<Solution<?>> list = new ArrayList<>() ;
    list.add(mock(Solution.class)) ;

    assertEquals(1, list.size()) ;
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list size (1) is less than " +
        "the number of requested solutions (2)"));

    List<Solution<?>> list = new ArrayList<>(1) ;
    list.add(mock(Solution.class)) ;

    SolutionListUtils.selectNRandomDifferentSolutions(2, list) ;
  }

  @Test
  public void shouldelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsTwoAndFourSolutionsAreRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list size (2) is less than " +
        "the number of requested solutions (4)"));

    List<Solution<?>> list = new ArrayList<>(2) ;
    list.add(mock(Solution.class)) ;
    list.add(mock(Solution.class)) ;

    SolutionListUtils.selectNRandomDifferentSolutions(4, list) ;
  }

  @Test
  public void shouldExecuteReturnTheSolutionInTheListIfTheListContainsASolution() {
    List<IntegerSolution> list = new ArrayList<>(2) ;
    IntegerSolution solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

    List<IntegerSolution> result = SolutionListUtils.selectNRandomDifferentSolutions(1, list);
    assertSame(solution, result.get(0)) ;
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnTheSolutionSInTheListIfTheListContainsTwoSolutions() {
    List<BinarySolution> list = new ArrayList<>(2) ;
    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;
    list.add(solution1) ;
    list.add(solution2) ;

    List<BinarySolution> result = SolutionListUtils.selectNRandomDifferentSolutions(2, list);

    assertTrue(result.contains(solution1));
    assertTrue(result.contains(solution2));
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectNumberOfSolutions() {
    int listSize = 20 ;
    int solutionsToBeReturned = 4 ;

    List<BinarySolution> list = new ArrayList<>(listSize) ;
    for (int i = 0; i < listSize; i++) {
      list.add(mock(BinarySolution.class));
    }

    List<BinarySolution> result = SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
    assertEquals(solutionsToBeReturned, result.size());
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvidedInSelectNRandomDifferentSolutions() {
		// Configuration
		List<BinarySolution> solutions = new LinkedList<>();
		solutions.add(mock(BinarySolution.class));
		solutions.add(mock(BinarySolution.class));
		solutions.add(mock(BinarySolution.class));
		solutions.add(mock(BinarySolution.class));
		solutions.add(mock(BinarySolution.class));
		solutions.add(mock(BinarySolution.class));

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		SolutionListUtils.selectNRandomDifferentSolutions(3, solutions);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		SolutionListUtils.selectNRandomDifferentSolutions(3, solutions, (a, b) -> {
			customUses[0]++;
			return new Random().nextInt(b+1-a)+a;
		});
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
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

    List<IntegerSolution> result = SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
    assertTrue(result.contains(solution[0]));
    assertTrue(result.contains(solution[1]));
    assertTrue(result.contains(solution[2]));
    assertTrue(result.contains(solution[3]));
  }

  @Test
  public void shouldSolutionListsAreEqualsReturnIfTwoIdenticalSolutionListsAreCompared() {
    List<BinarySolution> list1 = new ArrayList<>(3) ;
    List<BinarySolution> list2 = new ArrayList<>(3) ;

    for (int i = 0 ; i < 3 ; i++) {
      BinarySolution solution = mock(BinarySolution.class) ;
      list1.add(solution) ;
      list2.add(solution) ;
    }

    assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2)) ;
  }

  @Test
  public void shouldSolutionListsAreEqualsReturnIfTwoSolutionListsWithIdenticalSolutionsAreCompared() {
    List<BinarySolution> list1 = new ArrayList<>(3) ;
    List<BinarySolution> list2 = new ArrayList<>(3) ;

    List<BinarySolution> solutions = Arrays.asList(
        mock(BinarySolution.class),
        mock(BinarySolution.class),
        mock(BinarySolution.class)) ;

    for (BinarySolution solution : solutions) {
      list1.add(solution) ;
    }

    list2.add(solutions.get(2)) ;
    list2.add(solutions.get(1)) ;
    list2.add(solutions.get(0)) ;

    assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2)) ;
  }

  @Test
  public void shouldFillPopulationWithNewSolutionsDoNothingIfTheMaxSizeIsLowerThanTheListSize() {
    List<DoubleSolution> solutions = Arrays.asList(
        mock(DoubleSolution.class),
        mock(DoubleSolution.class),
        mock(DoubleSolution.class)) ;

    Problem<DoubleSolution> problem = new MockedDoubleProblem() ;

    int maxListSize = 2 ;
    SolutionListUtils.fillPopulationWithNewSolutions(solutions, problem, maxListSize);

    assertEquals(3, solutions.size()) ;
  }

  @Test
  public void shouldFillPopulationWithNewSolutionsIncreaseTheListLengthToTheIndicatedValue() {
    List<DoubleSolution> solutions = new ArrayList<DoubleSolution>(3) ;
    solutions.add(mock(DoubleSolution.class)) ;
    solutions.add(mock(DoubleSolution.class)) ;
    solutions.add(mock(DoubleSolution.class)) ;

    Problem<DoubleSolution> problem = new MockedDoubleProblem() ;

    int maxListSize = 10 ;
    SolutionListUtils.fillPopulationWithNewSolutions(solutions, problem, maxListSize);

    assertEquals(maxListSize, solutions.size()) ;
  }

  /**
   * TODO
   */
  @Test
  public void shouldRestartRemoveTheRequestedPercentageOfSolutions() {
  }

  @SuppressWarnings("serial")
  private class MockedDoubleProblem extends AbstractDoubleProblem {

    public MockedDoubleProblem() {
      setNumberOfVariables(2);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);

      setLowerLimit(Arrays.asList(0.0, 0.0));
      setUpperLimit(Arrays.asList(1.0, 1.0));
    }

    @Override
    public void evaluate(DoubleSolution solution) {
    }
  }
}
