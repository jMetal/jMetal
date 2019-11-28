package org.uma.jmetal.auto.util.ranking.impl;

import org.junit.Test;
import org.uma.jmetal.ranking.Ranking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExperimentalFastNonDominanceRankingTest {
  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubfronts() {
    List<Solution<?>> population = Collections.emptyList();
    Ranking<Solution<?>> ranking = new ExperimentalFastNonDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(0, ranking.getNumberOfSubFronts());
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubfront() {
    DoubleProblem problem = new MockDoubleProblem(2);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 6.0);

    population.add(solution);
    population.add(solution2);

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
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
    DoubleProblem problem = new MockDoubleProblem(2);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);

    population.add(solution);
    population.add(solution2);

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
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
    DoubleProblem problem = new MockDoubleProblem(2);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);
    DoubleSolution solution3 = problem.createSolution();
    solution3.setObjective(0, 4.0);
    solution3.setObjective(1, 8.0);

    population.add(solution);
    population.add(solution2);
    population.add(solution3);

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
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
  public void shouldRankingOfAPopulationWithFiveSolutionsWorkProperly() {
    DoubleProblem problem = new MockDoubleProblem(2);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 1.0);
    solution.setObjective(1, 0.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 0.6);
    solution2.setObjective(1, 0.6);
    DoubleSolution solution3 = problem.createSolution();
    solution3.setObjective(0, 0.5);
    solution3.setObjective(1, 0.5);
    DoubleSolution solution4 = problem.createSolution();
    solution4.setObjective(0, 1.1);
    solution4.setObjective(1, 0.0);
    DoubleSolution solution5 = problem.createSolution();
    solution5.setObjective(0, 0.0);
    solution5.setObjective(1, 1.0);

    population.add(solution);
    population.add(solution2);
    population.add(solution3);
    population.add(solution4);
    population.add(solution5);

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(2, ranking.getNumberOfSubFronts());

    assertNotNull(ranking.getSubFront(0));
    assertEquals(3, ranking.getSubFront(0).size());
    assertEquals(2, ranking.getSubFront(1).size());

    List<DoubleSolution> subfront = ranking.getSubFront(0);
    List<DoubleSolution> subfront1 = ranking.getSubFront(1);

    assertEquals(0, subfront.get(0).getAttribute(ranking.getAttributeId()));
    assertEquals(1, subfront1.get(0).getAttribute(ranking.getAttributeId()));
  }

  @Test
  public void shouldRankingAssignZeroToAllTheSolutionsIfTheyAreNonDominated() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5
    */
    DoubleProblem problem = new MockDoubleProblem(2);
    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();

    solution1.setObjective(0, 1.0);
    solution1.setObjective(1, 5.0);

    solution2.setObjective(0, 2.0);
    solution2.setObjective(1, 4.0);

    solution3.setObjective(0, 3.0);
    solution3.setObjective(1, 3.0);

    solution4.setObjective(0, 5.0);
    solution4.setObjective(1, 1.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3, solution4);

    Ranking<Solution<?>> ranking = new ExperimentalFastNonDominanceRanking<>();
    ranking.computeRanking(solutionList);

    assertEquals(1, ranking.getNumberOfSubFronts());
    assertEquals(0, solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution4.getAttribute(ranking.getAttributeId()));
  }

  @Test
  public void shouldRankingWorkProperly() {
    /*
         5 1
         4   2
         3     3
         2     5
         1         4
         0 1 2 3 4 5
         List: 1,2,3,4,5
         Expected result: two ranks (rank 0: 1, 2, 5, 4; rank 1: 3)
    */
    DoubleProblem problem = new MockDoubleProblem(2);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();
    DoubleSolution solution5 = problem.createSolution();

    solution1.setObjective(0, 1.0);
    solution1.setObjective(1, 5.0);

    solution2.setObjective(0, 2.0);
    solution2.setObjective(1, 4.0);

    solution3.setObjective(0, 3.0);
    solution3.setObjective(1, 3.0);

    solution4.setObjective(0, 5.0);
    solution4.setObjective(1, 1.0);

    solution5.setObjective(0, 3);
    solution5.setObjective(1, 2);

    List<DoubleSolution> solutionList =
        Arrays.asList(solution1, solution2, solution4, solution3, solution5);

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
    ranking.computeRanking(solutionList);

    assertEquals(2, ranking.getNumberOfSubFronts());
    assertEquals(0, solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(1, solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution4.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution5.getAttribute(ranking.getAttributeId()));
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

    Ranking<DoubleSolution> ranking = new ExperimentalFastNonDominanceRanking<>();
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
