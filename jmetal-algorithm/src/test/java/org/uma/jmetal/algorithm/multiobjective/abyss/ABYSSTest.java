package org.uma.jmetal.algorithm.multiobjective.abyss;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.localsearch.impl.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/** Created by ajnebro on 11/6/15. */
public class ABYSSTest {
  DoubleProblem problem;
  LocalSearchOperator<DoubleSolution> localSearch;
  MutationOperator<DoubleSolution> mutation;
  Archive<DoubleSolution> archive;

  @Before
  public void setup() {
    problem = new FakeDoubleProblem();
    archive = new CrowdingDistanceArchive<>(10);
    mutation = new PolynomialMutation(1.0, 20.0);
    localSearch = new BasicLocalSearch<>(2, mutation, new DominanceWithConstraintsComparator<>(), problem);
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnTrueIfTheConditionFulfills() {
    var maxEvaluations = 100;
    var abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0);

    ReflectionTestUtils.setField(abyss, "evaluations", 101);

    assertTrue(abyss.isStoppingConditionReached());
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnFalseIfTheConditionDoesNotFulfill() {
    var maxEvaluations = 100;
    var abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0);

    ReflectionTestUtils.setField(abyss, "evaluations", 1);

    assertFalse(abyss.isStoppingConditionReached());
  }

  @Test
  public void shouldInitializationPhaseLeadToAPopulationFilledWithEvaluatedSolutions() {
    var populationSize = 20;
    var numberOfSubRanges = 4;
    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(problem, 0, populationSize, 0, 0, 0, null, localSearch, null, numberOfSubRanges);

    abyss.initializationPhase();
    assertEquals(populationSize, abyss.getPopulation().size());
  }

  @Test
  public void shouldReferenceSetUpdateCreateTheTwoRefSetsAfterBeingInvokedTheFirstTime() {
    var populationSize = 20;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 6;
    var referenceSet2Size = 4;

    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    assertEquals(referenceSet1Size, abyss.referenceSet1.size());
    assertEquals(referenceSet2Size, abyss.referenceSet2.size());
    assertEquals(
        populationSize - referenceSet1Size - referenceSet2Size, abyss.getPopulation().size());
  }

  @Test
  public void
      shouldReferenceSetUpdateCreateAReducedSizeReferenceSet2IfThePopulationIsNotBigEnough() {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 8;
    var referenceSet2Size = 4;

    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    assertEquals(referenceSet1Size, abyss.referenceSet1.size());
    assertEquals(populationSize - referenceSet1Size, abyss.referenceSet2.size());
    assertEquals(0, abyss.getPopulation().size());
  }

  /* TODO
    @Test
    public void shouldSubsetGenerationGenerateAllPairWiseCombinationsOfTheRefSets() {
      int populationSize = 10;
      int numberOfSubRanges = 4;
      int referenceSet1Size = 4;
      int referenceSet2Size = 4;
      DoubleProblem problem = new MockProblem();
      abyss = new ABYSS(problem, 0, populationSize, referenceSet1Size, referenceSet2Size, 0, null,
          localSearch, null, numberOfSubRanges);
      abyss.initializationPhase();
      abyss.referenceSetUpdate();
      List<List<DoubleSolution>> list = abyss.subsetGeneration();
      int expectedCombinations = 0;
      for (int i = 0; i < abyss.referenceSet1.size(); i++) {
        for (int j = i + 1; j < abyss.referenceSet1.size(); j++) {
          expectedCombinations++;
        }
      }
      for (int i = 0; i < abyss.referenceSet2.size(); i++) {
        for (int j = i + 1; j < abyss.referenceSet2.size(); j++) {
          expectedCombinations++;
        }
      }
      assertEquals(expectedCombinations, list.size()) ;
      for (List<DoubleSolution> pair : list) {
        assertEquals(2, pair.size()) ;
      }
    }
  */
  @Test
  public void shouldSubsetGenerationProduceAnEmptyListIfAllTheSolutionsAreMarked() {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 4;
    var referenceSet2Size = 4;

    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    for (var solution : abyss.referenceSet1) {
      solution.attributes().put(ABYSS.SOLUTION_IS_MARKED, true);
    }

    for (var solution : abyss.referenceSet2) {
      solution.attributes().put(ABYSS.SOLUTION_IS_MARKED, true);
    }
    var list = abyss.subsetGeneration();

    assertEquals(0, list.size());
  }

  @Test
  public void shouldSolutionCombinationProduceTheRightNumberOfSolutions() {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 4;
    var referenceSet2Size = 4;

    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            new SBXCrossover(1.0, 20.0),
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();
    var list = abyss.subsetGeneration();
    var combinedSolutions = abyss.solutionCombination(list);

    var expectedValue = combinedSolutions.size() / 2;

    assertEquals(expectedValue, list.size());
  }

  @Test
  public void shouldRestartCreateANewPopulationWithTheRefSet1Solutions() {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 4;
    var referenceSet2Size = 4;

    DoubleProblem problem = new FakeDoubleProblem();

    var abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            10,
            new CrowdingDistanceArchive<DoubleSolution>(10),
            localSearch,
            new SBXCrossover(1.0, 20.0),
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();
    var list = abyss.subsetGeneration();
    var combinedSolutions = abyss.solutionCombination(list);
    for (var solution : combinedSolutions) {
      var improvedSolution = abyss.improvement(solution);
      abyss.referenceSetUpdate(improvedSolution);
    }

    abyss.restart();
    assertEquals(populationSize, abyss.getPopulation().size());
  }
}
