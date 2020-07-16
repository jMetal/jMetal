package org.uma.jmetal.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class SolutionListUtilsTest {

  private static final double EPSILON = 0.0000000001;

  @Rule public ExpectedException exception = ExpectedException.none();

  /** *** Unit tests to method findBestSolution *** */
  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(NullParameterException.class);

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class);

    SolutionListUtils.findBestSolution(null, comparator);
  }

  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(EmptyCollectionException.class);

    @SuppressWarnings("unchecked")
    Comparator<DoubleSolution> comparator = mock(Comparator.class);
    List<DoubleSolution> list = new ArrayList<>();

    SolutionListUtils.findBestSolution(list, comparator);
  }

  @Test
  public void shouldFindBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
    exception.expect(NullParameterException.class);

    List<DoubleSolution> list = new ArrayList<>();
    list.add(mock(DoubleSolution.class));

    SolutionListUtils.findBestSolution(list, null);
  }

  @Test
  public void shouldFindBestSolutionReturnTheSolutionInTheListWhenItContainsOneSolution() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    IntegerSolution solution = mock(IntegerSolution.class);
    list.add(solution);

    assertSame(solution, SolutionListUtils.findBestSolution(list, comparator));
  }

  @Test
  public void shouldFindBestSolutionReturnTheSecondSolutionInTheListIfIsTheBestOufOfTwoSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    IntegerSolution solution1 = mock(IntegerSolution.class);
    list.add(solution1);
    IntegerSolution solution2 = mock(IntegerSolution.class);
    list.add(solution2);

    when(comparator.compare(any(), any()))
        .thenReturn(1);

    assertSame(solution2, SolutionListUtils.findBestSolution(list, comparator));
  }

  @Test
  public void
      shouldFindBestSolutionReturnTheLastOneIfThisIsTheBestSolutionInALastInAListWithFiveSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(mock(IntegerSolution.class));
    }

    when(comparator.compare(any(), any()))
        .thenReturn(1, 0, 0, 1);
    assertSame(list.get(4), SolutionListUtils.findBestSolution(list, comparator));
    verify(comparator, times(4))
        .compare(any(), any());
  }

  /** *** Unit tests to method findIndexOfBestSolution *** */
  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(NullParameterException.class);

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class);

    SolutionListUtils.findIndexOfBestSolution(null, comparator);
  }

  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(EmptyCollectionException.class);

    @SuppressWarnings("unchecked")
    Comparator<DoubleSolution> comparator = mock(Comparator.class);
    List<DoubleSolution> list = new ArrayList<>();

    SolutionListUtils.findIndexOfBestSolution(list, comparator);
  }

  @Test
  public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
    exception.expect(NullParameterException.class);

    List<DoubleSolution> list = new ArrayList<>();
    list.add(mock(DoubleSolution.class));

    SolutionListUtils.findIndexOfBestSolution(list, null);
  }

  @Test
  public void shouldFindIndexOfBestSolutionReturnZeroIfTheListWhenItContainsOneSolution() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    IntegerSolution solution = mock(IntegerSolution.class);
    list.add(solution);

    assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator));
  }

  @Test
  public void
      shouldFindIndexOfBestSolutionReturnZeroIfTheFirstSolutionItTheBestOutOfTwoSolutionsInTheList() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    IntegerSolution solution1 = mock(IntegerSolution.class);
    list.add(solution1);
    IntegerSolution solution2 = mock(IntegerSolution.class);
    list.add(solution2);

    when(comparator.compare(any(), any()))
        .thenReturn(0);
    assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator)
        .compare(any(), any());
  }

  @Test
  public void
      shouldFindIndexOfBestSolutionReturnOneIfTheSecondSolutionItTheBestOutOfTwoSolutionInTheList() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    IntegerSolution solution1 = mock(IntegerSolution.class);
    list.add(solution1);
    IntegerSolution solution2 = mock(IntegerSolution.class);
    list.add(solution2);

    when(comparator.compare(any(), any()))
        .thenReturn(1);
    assertEquals(1, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator)
        .compare(any(), any());
  }

  @Test
  public void
      shouldFindIndexOfBestSolutionReturn4IfTheBestSolutionIsTheLastInAListWithFiveSolutions() {
    @SuppressWarnings("unchecked")
    Comparator<IntegerSolution> comparator = mock(Comparator.class);
    List<IntegerSolution> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(mock(IntegerSolution.class));
    }

    when(comparator.compare(any(), any()))
        .thenReturn(1, 0, 0, 1);
    assertEquals(4, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    verify(comparator, times(4))
        .compare(any(), any());
  }

  /** *** Unit tests to method selectNRandomDifferentSolutions *** */
  @Test
  public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(NullParameterException.class);

    SolutionListUtils.selectNRandomDifferentSolutions(1, null);
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(EmptyCollectionException.class);

    List<DoubleSolution> list = new ArrayList<>();

    SolutionListUtils.selectNRandomDifferentSolutions(1, list);
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnASingleSolution() {
    List<Solution<?>> list = new ArrayList<>();
    list.add(mock(Solution.class));

    assertEquals(1, list.size());
  }

  @Test
  public void
      shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    exception.expect(InvalidConditionException.class);

    List<Solution<?>> list = new ArrayList<>(1);
    list.add(mock(Solution.class));

    SolutionListUtils.selectNRandomDifferentSolutions(2, list);
  }

  @Test
  public void
      shouldelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsTwoAndFourSolutionsAreRequested() {
    exception.expect(InvalidConditionException.class);

    List<Solution<?>> list = new ArrayList<>(2);
    list.add(mock(Solution.class));
    list.add(mock(Solution.class));

    SolutionListUtils.selectNRandomDifferentSolutions(4, list);
  }

  @Test
  public void shouldExecuteReturnTheSolutionInTheListIfTheListContainsASolution() {
    List<IntegerSolution> list = new ArrayList<>(2);
    IntegerSolution solution = mock(IntegerSolution.class);
    list.add(solution);

    List<IntegerSolution> result = SolutionListUtils.selectNRandomDifferentSolutions(1, list);
    assertSame(solution, result.get(0));
  }

  @Test
  public void
      shouldSelectNRandomDifferentSolutionsReturnTheSolutionSInTheListIfTheListContainsTwoSolutions() {
    List<BinarySolution> list = new ArrayList<>(2);
    BinarySolution solution1 = mock(BinarySolution.class);
    BinarySolution solution2 = mock(BinarySolution.class);
    list.add(solution1);
    list.add(solution2);

    List<BinarySolution> result = SolutionListUtils.selectNRandomDifferentSolutions(2, list);

    assertTrue(result.contains(solution1));
    assertTrue(result.contains(solution2));
  }

  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectNumberOfSolutions() {
    int listSize = 20;
    int solutionsToBeReturned = 4;

    List<BinarySolution> list = new ArrayList<>(listSize);
    for (int i = 0; i < listSize; i++) {
      list.add(mock(BinarySolution.class));
    }

    List<BinarySolution> result =
        SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
    assertEquals(solutionsToBeReturned, result.size());
  }

  @Test
  public void
      shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvidedInSelectNRandomDifferentSolutions() {
    // Configuration
    List<BinarySolution> solutions = new LinkedList<>();
    solutions.add(mock(BinarySolution.class));
    solutions.add(mock(BinarySolution.class));
    solutions.add(mock(BinarySolution.class));
    solutions.add(mock(BinarySolution.class));
    solutions.add(mock(BinarySolution.class));
    solutions.add(mock(BinarySolution.class));

    // Check configuration leads to use default generator by default
    final int[] defaultUses = {0};
    JMetalRandom defaultGenerator = JMetalRandom.getInstance();
    AuditableRandomGenerator auditor =
        new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
    defaultGenerator.setRandomGenerator(auditor);
    auditor.addListener((a) -> defaultUses[0]++);

    SolutionListUtils.selectNRandomDifferentSolutions(3, solutions);
    assertTrue("No use of the default generator", defaultUses[0] > 0);

    // Test same configuration uses custom generator instead
    defaultUses[0] = 0;
    final int[] customUses = {0};
    SolutionListUtils.selectNRandomDifferentSolutions(
        3,
        solutions,
        (a, b) -> {
          customUses[0]++;
          return new Random().nextInt(b + 1 - a) + a;
        });
    assertTrue("Default random generator used", defaultUses[0] == 0);
    assertTrue("No use of the custom generator", customUses[0] > 0);
  }

  /** If the list contains 4 solutions, the result list must return all of them */
  @Test
  public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectListOfSolutions() {
    int listSize = 4;
    int solutionsToBeReturned = 4;

    List<IntegerSolution> list = new ArrayList<>(listSize);
    IntegerSolution[] solution = new IntegerSolution[solutionsToBeReturned];
    for (int i = 0; i < listSize; i++) {
      solution[i] = (mock(IntegerSolution.class));
      list.add(solution[i]);
    }

    List<IntegerSolution> result =
        SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
    assertTrue(result.contains(solution[0]));
    assertTrue(result.contains(solution[1]));
    assertTrue(result.contains(solution[2]));
    assertTrue(result.contains(solution[3]));
  }

  @Test
  public void shouldSolutionListsAreEqualsReturnIfTwoIdenticalSolutionListsAreCompared() {
    List<BinarySolution> list1 = new ArrayList<>(3);
    List<BinarySolution> list2 = new ArrayList<>(3);

    for (int i = 0; i < 3; i++) {
      BinarySolution solution = mock(BinarySolution.class);
      list1.add(solution);
      list2.add(solution);
    }

    assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2));
  }

  @Test
  public void
      shouldSolutionListsAreEqualsReturnIfTwoSolutionListsWithIdenticalSolutionsAreCompared() {
    List<BinarySolution> list1 = new ArrayList<>(3);
    List<BinarySolution> list2 = new ArrayList<>(3);

    List<BinarySolution> solutions =
        Arrays.asList(
            mock(BinarySolution.class), mock(BinarySolution.class), mock(BinarySolution.class));

    for (BinarySolution solution : solutions) {
      list1.add(solution);
    }

    list2.add(solutions.get(2));
    list2.add(solutions.get(1));
    list2.add(solutions.get(0));

    assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2));
  }

  @Test
  public void shouldFillPopulationWithNewSolutionsDoNothingIfTheMaxSizeIsLowerThanTheListSize() {
    List<DoubleSolution> solutions =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    Problem<DoubleSolution> problem = new MockedDoubleProblem();

    int maxListSize = 2;
    SolutionListUtils.fillPopulationWithNewSolutions(solutions, problem, maxListSize);

    assertEquals(3, solutions.size());
  }

  @Test
  public void shouldFillPopulationWithNewSolutionsIncreaseTheListLengthToTheIndicatedValue() {
    List<DoubleSolution> solutions = new ArrayList<DoubleSolution>(3);
    solutions.add(mock(DoubleSolution.class));
    solutions.add(mock(DoubleSolution.class));
    solutions.add(mock(DoubleSolution.class));

    Problem<DoubleSolution> problem = new MockedDoubleProblem();

    int maxListSize = 10;
    SolutionListUtils.fillPopulationWithNewSolutions(solutions, problem, maxListSize);

    assertEquals(maxListSize, solutions.size());
  }

  @Test
  public void shouldNormalizeReturnsCorrectNormalizedNumber() {

    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    DoubleSolution s2 = problem.createSolution();

    s1.setObjective(0, 20);
    s1.setObjective(1, 10);
    s2.setObjective(0, 10);
    s2.setObjective(1, 20);

    List<DoubleSolution> solutions = Arrays.asList(s1, s2);

    double[] minValue = new double[] {10, 10};
    double[] maxValue = new double[] {20, 20};

    List<DoubleSolution> normalizedSolutions =
        (List<DoubleSolution>) SolutionListUtils.normalizeSolutionList(solutions, minValue, maxValue);

    assertNotSame(normalizedSolutions, solutions);
    assertEquals(1.0, normalizedSolutions.get(0).getObjective(0), EPSILON);
    assertEquals(0.0, normalizedSolutions.get(0).getObjective(1), EPSILON);
    assertEquals(0.0, normalizedSolutions.get(1).getObjective(0), EPSILON);
    assertEquals(1.0, normalizedSolutions.get(1).getObjective(1), EPSILON);
  }

  @Test
  public void shouldGetFeasibilityRatioReturnOneIfAllTheSolutionsInAListAreFeasible() {
    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    DoubleSolution s2 = problem.createSolution();
    DoubleSolution s3 = problem.createSolution();

    List<DoubleSolution> solutionList = Arrays.asList(s1, s2, s3);
    assertEquals(1.0, ConstraintHandling.feasibilityRatio(solutionList), EPSILON);
  }

  @Test
  public void shouldGetFeasibilityRatioReturnZeroIfAllTheSolutionsInAListAreNotFeasible() {
    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    DoubleSolution s2 = problem.createSolution();
    DoubleSolution s3 = problem.createSolution();
    s1.setConstraint(0, -4);
    s2.setConstraint(0, -4);
    s3.setConstraint(0, -4);


    List<DoubleSolution> solutionList = Arrays.asList(s1, s2, s3);
    assertEquals(0.0, ConstraintHandling.feasibilityRatio(solutionList), EPSILON);
  }

  @Test
  public void shouldGetFeasibilityRatioReturnTheRightRatioOfFeasibleSolutions() {
    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    DoubleSolution s2 = problem.createSolution();
    DoubleSolution s3 = problem.createSolution();
    DoubleSolution s4 = problem.createSolution();
    s1.setConstraint(0, -4);
    s3.setConstraint(0, -4);

    List<DoubleSolution> solutionList = Arrays.asList(s1, s2, s3, s4);
    assertEquals(0.5, ConstraintHandling.feasibilityRatio(solutionList), EPSILON);
  }

  @Test
  public void shouldNormalizeSolutionListWorkProperly() {
    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    s1.setObjective(0, 0.0);
    s1.setObjective(1, 2.0);
    DoubleSolution s2 = problem.createSolution();
    s2.setObjective(0, 1.25);
    s2.setObjective(1, 2.75);
    DoubleSolution s3 = problem.createSolution();
    s3.setObjective(0, 2.5);
    s3.setObjective(1, 2.5);
    DoubleSolution s4 = problem.createSolution();
    s4.setObjective(0, 3.75);
    s4.setObjective(1, 2.25);
    DoubleSolution s5 = problem.createSolution();
    s5.setObjective(0, 4.0);
    s5.setObjective(1, 3.0);

    List<DoubleSolution> solutionList = Arrays.asList(s1, s2, s3, s4, s5);
    List<DoubleSolution> normalizedSolutionList = SolutionListUtils.normalizeSolutionList(solutionList) ;
    assertEquals(solutionList.size(), normalizedSolutionList.size());
    assertEquals(0.0, normalizedSolutionList.get(0).getObjective(0), EPSILON);
    assertEquals(0.0, normalizedSolutionList.get(0).getObjective(1), EPSILON);
    assertEquals(1.0, normalizedSolutionList.get(4).getObjective(0), EPSILON);
    assertEquals(1.0, normalizedSolutionList.get(4).getObjective(1), EPSILON);
  }

  @Test
  public void shouldDistanceBasedSubsetSelectionWorkProperly() {
    MockedDoubleProblem problem = new MockedDoubleProblem();
    DoubleSolution s1 = problem.createSolution();
    s1.setObjective(0, 0.0);
    s1.setObjective(1, 3.0);
    DoubleSolution s2 = problem.createSolution();
    s2.setObjective(0, 0.25);
    s2.setObjective(1, 2.75);
    DoubleSolution s3 = problem.createSolution();
    s3.setObjective(0, 0.5);
    s3.setObjective(1, 2.5);
    DoubleSolution s4 = problem.createSolution();
    s4.setObjective(0, 0.75);
    s4.setObjective(1, 2.25);
    DoubleSolution s5 = problem.createSolution();
    s5.setObjective(0, 1.0);
    s5.setObjective(1, 2.0);

    List<DoubleSolution> solutionList = Arrays.asList(s1, s2, s3, s4, s5);

    List<DoubleSolution> resultList = SolutionListUtils.distanceBasedSubsetSelection(solutionList, 3) ;
    assertEquals(3, resultList.size());
  }

  /** TODO */
  @Test
  public void shouldRestartRemoveTheRequestedPercentageOfSolutions() {}

  @SuppressWarnings("serial")
  private class MockedDoubleProblem extends AbstractDoubleProblem {

    public MockedDoubleProblem() {
      setNumberOfVariables(2);
      setNumberOfObjectives(2);
      setNumberOfConstraints(1);

      setVariableBounds(Arrays.asList(0.0, 0.0), Arrays.asList(1.0, 1.0));
    }

    @Override
    public void evaluate(DoubleSolution solution) {}
  }
}
