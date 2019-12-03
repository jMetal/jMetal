package org.uma.jmetal.component.ranking;

import org.junit.Test;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.ranking.impl.MergeSortNonDominatedSortRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MergeNonDominatedSortingRankingTest {

  /*
    @Test
    public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubfronts() {
      List<Solution<?>> population = Collections.emptyList();
      Ranking<Solution<?>> ranking = new MergeSortNonDominatedSortRanking<>(0, 2);
      ranking.computeRanking(population);

      assertEquals(0, ranking.getNumberOfSubFronts());
    }
  */
  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubfront() {
    DoubleProblem problem = new MockDoubleProblem(3, 3);

    double[][] objectiveValues = new double[][] {{2, 3, 3}, {1, 6, 1}};

    List<DoubleSolution> population = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.setObjective(0, objectiveValues[i][0]);
              solution.setObjective(1, objectiveValues[i][1]);
              solution.setObjective(2, objectiveValues[i][2]);
              population.add(solution);
            });

    Ranking<DoubleSolution> ranking = new MergeSortNonDominatedSortRanking<>(population.size(), 3);
    ranking.computeRanking(population);

    assertEquals(1, ranking.getNumberOfSubFronts());
    assertEquals(2, ranking.getSubFront(0).size());

    assertEquals(0, population.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(0, population.get(1).getAttribute(ranking.getAttributeId()));

    List<DoubleSolution> subfront = ranking.getSubFront(0);
    assertEquals(0, subfront.get(0).getAttribute(ranking.getAttributeId()));
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoDominatedSolutionsReturnTwoSubfronts() {
    DoubleProblem problem = new MockDoubleProblem(2, 3);

    double[][] objectiveValues = new double[][] {{2, 3, 2}, {3, 6, 4}};

    List<DoubleSolution> population = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.setObjective(0, objectiveValues[i][0]);
              solution.setObjective(1, objectiveValues[i][1]);
              solution.setObjective(2, objectiveValues[i][2]);
              population.add(solution);
            });

    Ranking<DoubleSolution> ranking = new MergeSortNonDominatedSortRanking<>(population.size(), 3);
    ranking.computeRanking(population);

    assertEquals(2, ranking.getNumberOfSubFronts());

    assertNotNull(ranking.getSubFront(0));
    assertEquals(1, ranking.getSubFront(0).size());
    assertEquals(1, ranking.getSubFront(1).size());

    assertEquals(0, population.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(1, population.get(1).getAttribute(ranking.getAttributeId()));

    List<DoubleSolution> subfront = ranking.getSubFront(0);
    List<DoubleSolution> subfront1 = ranking.getSubFront(1);

    assertEquals(0, subfront.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(1, subfront1.get(0).getAttribute(ranking.getAttributeId()));
  }

  @Test
  public void shouldRankingOfAPopulationWithThreeDominatedSolutionsReturnThreeSubfronts() {
    DoubleProblem problem = new MockDoubleProblem(2, 3);

    double[][] objectiveValues = new double[][] {{2, 3, 4}, {3, 6, 5}, {4, 8, 6}};

    List<DoubleSolution> population = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.setObjective(0, objectiveValues[i][0]);
              solution.setObjective(1, objectiveValues[i][1]);
              solution.setObjective(2, objectiveValues[i][2]);
              population.add(solution);
            });

    Ranking<DoubleSolution> ranking = new MergeSortNonDominatedSortRanking<>(population.size(), 3);
    ranking.computeRanking(population);

    assertEquals(3, ranking.getNumberOfSubFronts());

    assertNotNull(ranking.getSubFront(0));
    assertEquals(1, ranking.getSubFront(0).size());
    assertEquals(1, ranking.getSubFront(1).size());
    assertEquals(1, ranking.getSubFront(2).size());

    assertEquals(0, population.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(1, population.get(1).getAttribute(ranking.getAttributeId()));

    List<DoubleSolution> subfront = ranking.getSubFront(0);
    List<DoubleSolution> subfront1 = ranking.getSubFront(1);
    List<DoubleSolution> subfront2 = ranking.getSubFront(2);

    assertEquals(0, subfront.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(1, subfront1.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(2, subfront2.get(0).getAttribute(ranking.getAttributeId()));
  }

  @Test
  public void shouldRankingWorkOnTheExampleInTheMNDSPaper() {
    DoubleProblem problem = new MockDoubleProblem(2, 3);

    double[][] objectiveValues =
        new double[][] {
          {34, 30, 41},
          {33, 34, 30},
          {32, 32, 31},
          {31, 34, 34},
          {34, 30, 40},
          {36, 33, 32},
          {35, 31, 43},
          {37, 36, 39},
          {35, 34, 38},
          {38, 38, 37},
          {39, 37, 31}
        };

    List<DoubleSolution> solutionList = new ArrayList<>(objectiveValues.length);
    IntStream.range(0, objectiveValues.length)
        .forEach(
            i -> {
              DoubleSolution solution = problem.createSolution();
              solution.setObjective(0, objectiveValues[i][0]);
              solution.setObjective(1, objectiveValues[i][1]);
              solution.setObjective(2, objectiveValues[i][2]);
              solutionList.add(solution);
            });

    Ranking<DoubleSolution> ranking =
        new MergeSortNonDominatedSortRanking<>(solutionList.size(), 3);
    ranking.computeRanking(solutionList);

    assertEquals(11, solutionList.size());
    assertEquals(3, ranking.getNumberOfSubFronts());
    assertEquals(4, ranking.getSubFront(0).size());
    assertEquals(4, ranking.getSubFront(1).size());
    assertEquals(3, ranking.getSubFront(2).size());
  }

  private class MockDoubleProblem extends AbstractDoubleProblem {

    public MockDoubleProblem(Integer numberOfVariables) {
      this(numberOfVariables, 2);
    }

    public MockDoubleProblem(Integer numberOfVariables, int numberOfObjectives) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(numberOfObjectives);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(5.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {}
  }
}
