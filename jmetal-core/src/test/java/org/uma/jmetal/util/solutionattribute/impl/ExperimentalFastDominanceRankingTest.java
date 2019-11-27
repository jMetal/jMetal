package org.uma.jmetal.util.solutionattribute.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExperimentalFastDominanceRankingTest {
  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubFronts() {
    List<Solution<?>> population = Collections.emptyList();
    Ranking<Solution<?>> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(0, ranking.getNumberOfSubFronts());
  }

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnOneSubFronts(){
    List<PointSolution> population = Arrays.asList(
            new PointSolution(2),
            new PointSolution(2),
            new PointSolution(2));

    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(1, ranking.getNumberOfSubFronts());
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubFront() {
    List<PointSolution> population = new ArrayList<>() ;

    population.add(makeUnconstrained(2, 3)) ;
    population.add(makeUnconstrained(1, 6)) ;

    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubFronts()) ;
    assertEquals(2, ranking.getSubFront(0).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(0, (int) ranking.getAttribute(population.get(1))) ;

    List<PointSolution> subFront = ranking.getSubFront(0) ;
    assertEquals(0, (int) ranking.getAttribute(subFront.get(0))) ;
  }


  @Test
  public void shouldRankingOfAPopulationWithTwoDominatedSolutionsReturnTwoSubFronts() {
    List<PointSolution> population = new ArrayList<>() ;

    population.add(makeUnconstrained(2, 3)) ;
    population.add(makeUnconstrained(3, 6)) ;

    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population) ;

    assertEquals(2, ranking.getNumberOfSubFronts()) ;

    assertNotNull(ranking.getSubFront(0));
    assertEquals(1, ranking.getSubFront(0).size()) ;
    assertEquals(1, ranking.getSubFront(1).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(population.get(1))) ;

    List<PointSolution> subFront0 = ranking.getSubFront(0) ;
    List<PointSolution> subFront1 = ranking.getSubFront(1) ;

    assertEquals(0, (int) ranking.getAttribute(subFront0.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(subFront1.get(0))) ;
  }

  @Test
  public void shouldRankingOfAPopulationWithThreeDominatedSolutionsReturnThreeSubFronts() {
    List<PointSolution> population = new ArrayList<>();
    population.add(makeUnconstrained(2.0, 3.0));
    population.add(makeUnconstrained(3.0, 6.0));
    population.add(makeUnconstrained(4.0, 8.0));

    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(3, ranking.getNumberOfSubFronts());

    assertNotNull(ranking.getSubFront(0));
    assertEquals(1, ranking.getSubFront(0).size());
    assertEquals(1, ranking.getSubFront(1).size());
    assertEquals(1, ranking.getSubFront(2).size());

    assertEquals(0, (int) ranking.getAttribute(population.get(0)));
    assertEquals(1, (int) ranking.getAttribute(population.get(1)));

    List<PointSolution> subFront0 = ranking.getSubFront(0);
    List<PointSolution> subFront1 = ranking.getSubFront(1);
    List<PointSolution> subFront2 = ranking.getSubFront(2);

    assertEquals(0, (int) ranking.getAttribute(subFront0.get(0)));
    assertEquals(1, (int) ranking.getAttribute(subFront1.get(0)));
    assertEquals(2, (int) ranking.getAttribute(subFront2.get(0)));
  }

  @Test
  public void shouldRankingOfAPopulationWithFiveSolutionsWorkProperly() {
    List<PointSolution>population = new ArrayList<>();

    population.add(makeUnconstrained(1.0, 0.0));
    population.add(makeUnconstrained(0.6, 0.6));
    population.add(makeUnconstrained(0.5, 0.5));
    population.add(makeUnconstrained(1.1, 0.0));
    population.add(makeUnconstrained(0.0, 1.0));

    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(2, ranking.getNumberOfSubFronts());

    assertNotNull(ranking.getSubFront(0));
    assertEquals(3, ranking.getSubFront(0).size());
    assertEquals(2, ranking.getSubFront(1).size());

    List<PointSolution> subFront0 = ranking.getSubFront(0);
    List<PointSolution> subFront1 = ranking.getSubFront(1);

    assertEquals(0, (int) ranking.getAttribute(subFront0.get(0)));
    assertEquals(1, (int) ranking.getAttribute(subFront1.get(0)));
  }

  @Test
  public void testWithConstraints() {
    List<PointSolution> solutions = Arrays.asList(
            makeConstrained(8, 0, 0), makeConstrained(0, 4, -1), makeConstrained(8, 5, 0),
            makeConstrained(0, 0, -1), makeConstrained(7, 0, -1), makeConstrained(2, 7, 0),
            makeConstrained(4, 8, 0), makeConstrained(5, 8, 0), makeConstrained(8, 1, -1),
            makeConstrained(6, 3, -1));
    Ranking<PointSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(solutions);
    assertEquals(0, (int) ranking.getAttribute(solutions.get(0)));
    assertEquals(4, (int) ranking.getAttribute(solutions.get(1)));
    assertEquals(1, (int) ranking.getAttribute(solutions.get(2)));
    assertEquals(3, (int) ranking.getAttribute(solutions.get(3)));
    assertEquals(4, (int) ranking.getAttribute(solutions.get(4)));
    assertEquals(0, (int) ranking.getAttribute(solutions.get(5)));
    assertEquals(1, (int) ranking.getAttribute(solutions.get(6)));
    assertEquals(2, (int) ranking.getAttribute(solutions.get(7)));
    assertEquals(5, (int) ranking.getAttribute(solutions.get(8)));
    assertEquals(4, (int) ranking.getAttribute(solutions.get(9)));
  }

  private PointSolution makeUnconstrained(double x, double y) {
    PointSolution sol = new PointSolution(2);
    sol.setObjective(0, x);
    sol.setObjective(1, y);
    return sol;
  }

  private PointSolution makeConstrained(double x, double y, double c) {
    PointSolution sol = new PointSolution(2, 1);
    sol.setObjective(0, x);
    sol.setObjective(1, y);
    sol.setConstraint(0, c);
    return sol;
  }
}
