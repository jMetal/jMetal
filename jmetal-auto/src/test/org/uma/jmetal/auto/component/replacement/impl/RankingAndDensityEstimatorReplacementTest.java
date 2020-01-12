package org.uma.jmetal.auto.component.replacement.impl;

import org.junit.Test;
import org.uma.jmetal.component.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.StrengthRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RankingAndDensityEstimatorReplacementTest {
  private double EPSILON = 0.00000000000001;

  @Test
  public void shouldReplacementReturnTheListIfTheOffspringListIsEmpty() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5
    */
    DoubleProblem problem = new MockDoubleProblem(2);
    Ranking<DoubleSolution> ranking = new StrengthRanking<>();
    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);
    RankingAndDensityEstimatorReplacement rankingAndDensityEstimatorReplacement =
        new RankingAndDensityEstimatorReplacement(ranking, densityEstimator);

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

    List<DoubleSolution> resultList = rankingAndDensityEstimatorReplacement.replace(solutionList, new ArrayList<>()) ;

    assertEquals(0, (int) solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution4.getAttribute(ranking.getAttributeId()));

    assertEquals(4, resultList.size());
  }

  @Test
  public void shouldReplacementReturnTheRightValuesCase1() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5

         List: 1,2,3   OffspringList: 4
         Expected result: 4, 1, 3
    */
    DoubleProblem problem = new MockDoubleProblem(2);
    Ranking<DoubleSolution> ranking = new StrengthRanking<>();
    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);
    RankingAndDensityEstimatorReplacement rankingAndDensityEstimatorReplacement =
        new RankingAndDensityEstimatorReplacement(ranking, densityEstimator);

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

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution3);
    List<DoubleSolution> offspringList = Arrays.asList(solution4) ;

    List<DoubleSolution> resultList = rankingAndDensityEstimatorReplacement.replace(solutionList, offspringList) ;

    assertEquals(0, (int) solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution4.getAttribute(ranking.getAttributeId()));

    assertEquals(3, resultList.size());
    assertTrue(resultList.contains(solution4));
    assertTrue(resultList.contains(solution1));
    assertTrue(resultList.contains(solution3));
  }

  @Test
  public void shouldReplacementReturnTheRightValuesCase2() {
    /*
         5 1
         4   2
         3     3
         2    5
         1         4
         0 1 2 3 4 5

         List: 1,2,4   OffspringList: 3,5
         Expected result: 1, 5, 4
    */
    DoubleProblem problem = new MockDoubleProblem(2);
    Ranking<DoubleSolution> ranking = new StrengthRanking<>();
    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);
    RankingAndDensityEstimatorReplacement rankingAndDensityEstimatorReplacement =
        new RankingAndDensityEstimatorReplacement(ranking, densityEstimator);

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

    solution5.setObjective(0, 2.5);
    solution5.setObjective(1, 2.5);

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution4);
    List<DoubleSolution> offspringList = Arrays.asList(solution3, solution5) ;

    List<DoubleSolution> resultList = rankingAndDensityEstimatorReplacement.replace(solutionList, offspringList) ;

    assertEquals(0, (int) solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(1, (int) solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution4.getAttribute(ranking.getAttributeId()));
    assertEquals(0, (int) solution5.getAttribute(ranking.getAttributeId()));

    assertEquals(3, resultList.size());
    assertTrue(resultList.contains(solution1));
    assertTrue(resultList.contains(solution5));
    assertTrue(resultList.contains(solution4));
  }

  /** Mock class representing a double problem */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

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
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
