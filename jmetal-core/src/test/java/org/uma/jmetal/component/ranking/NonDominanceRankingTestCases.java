package org.uma.jmetal.component.ranking;

import org.junit.Test;
import org.uma.jmetal.component.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class NonDominanceRankingTestCases<R extends Ranking<DoubleSolution>> {
  protected R ranking;

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubfronts() {
    List<Solution<?>> population = Collections.emptyList();
    Ranking<Solution<?>> ranking = new ExperimentalFastNonDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(0, ranking.getNumberOfSubFronts());
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubfront() {
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 6.0);

    population.add(solution);
    population.add(solution2);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);

    List<DoubleSolution> population = new ArrayList<>();

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);

    population.add(solution);
    population.add(solution2);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);
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

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution3, solution4);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0);

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
    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

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

    ranking.computeRanking(solutionList);

    assertEquals(11, solutionList.size());
    assertEquals(3, ranking.getNumberOfSubFronts());
    assertEquals(4, ranking.getSubFront(0).size());
    assertEquals(4, ranking.getSubFront(1).size());
    assertEquals(3, ranking.getSubFront(2).size());
  }


  @Test
  public void shouldRankingOfAPopulationWithTenSolutionsWorkProperly() {
    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

    double[][] objectiveValues =
            new double[][] {
                    {1.4648056109874181, 8.970087855444899E-34, 5.301705982489511E-43},
                    {1.4648056109874181, 8.970087855444899E-34, 5.301705982489511E-43},
                    {1.5908547487753466, 4.21325648871815E-91, 5.492563533270124E-38},
                    {1.460628598699147, 7.251230487490275E-13, 6.836254915688127E-21},
                    {1.53752105026832, 1.30774962272882E-89, 1.964911546564003E-276},
                    {1.7827030380249338, 4.7213519324741183E-91, 1.093734894701149E-8},
                    {1.5077459267903963, 3.717675758529715E-9, 7.056780562019277E-21},
                    {1.7182703887918194, 4.567060424443055E-69, 6.126880230825156E-225},
                    {1.551119525194089, 3.0514004681678587E-46, 1.927008515185969E-40},
                    {1.572731735111519, 1.337698324772074E-89, 4.4182881457366E-206},
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

    ranking.computeRanking(solutionList);

    assertEquals(10, solutionList.size());
    assertEquals(2, ranking.getNumberOfSubFronts());
    assertEquals(5, ranking.getSubFront(0).size());
    assertEquals(5, ranking.getSubFront(1).size());
  }


  @Test
  public void shouldRankingOfAPopulationWithTwentySolutionsWorkProperly() {
    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

    double[][] objectiveValues =
        new double[][] {
          {1.551119525194089, 3.0514004681678587E-46, 1.927008515185969E-40},
          {1.4648056109874181, 8.970087855444899E-34, 5.301705982489511E-43},
          {1.5494254948894577, 1.7966427544774256E-51, 4.075358470895911E-37},
          {1.7827030380249338, 4.7213519324741183E-91, 1.093734894701149E-8},
          {1.572731735111519, 1.337698324772074E-89, 4.4182881457366E-206},
          {1.5077459267903963, 3.717675758529715E-9, 7.056780562019277E-21},
          {1.839802686546447, 1.2860202607443394E-73, 8.610922838975571E-21},
          {1.7246492482280722, 2.8107611659011267E-20, 4.845071259695356E-206},
          {1.78303953210775, 2.291263808931386E-76, 4.39046494875984E-16},
          {1.6377979691545304, 6.598125815633987E-69, 4.413952566653338E-15},
          {1.551119525194089, 3.0514004681678587E-46, 1.927008515185969E-40},
          {1.4648056109874181, 8.970087855444899E-34, 5.301705982489511E-43},
          {1.5908547487753466, 4.21325648871815E-91, 5.492563533270124E-38},
          {1.7919394752021747, 2.0778508454927406E-51, 1.1494687423683686E-8},
          {1.460628598699147, 7.251230487490275E-13, 6.836254915688127E-21},
          {1.53752105026832, 1.30774962272882E-89, 1.964911546564003E-276},
          {1.774518916547955, 1.0572709295236387E-21, 9.767967318141209E-20},
          {1.7182703887918194, 4.567060424443055E-69, 6.126880230825156E-225},
          {1.6477777209632063, 6.638330809951104E-69, 4.644669999824093E-15},
          {1.7801748970303293, 2.2875826596576342E-76, 4.1864149241795527E-16}
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

    ranking.computeRanking(solutionList);

    assertEquals(20, solutionList.size());
    assertEquals(5, ranking.getNumberOfSubFronts());
    assertEquals(5, ranking.getSubFront(0).size());
    assertEquals(7, ranking.getSubFront(1).size());
    assertEquals(5, ranking.getSubFront(2).size());
    assertEquals(2, ranking.getSubFront(3).size());
    assertEquals(1, ranking.getSubFront(4).size());
  }
}
