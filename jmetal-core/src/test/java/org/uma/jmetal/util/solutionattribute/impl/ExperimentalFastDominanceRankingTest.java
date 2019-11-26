package org.uma.jmetal.util.solutionattribute.impl;

import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExperimentalFastDominanceRankingTest {
  private OverallConstraintViolation<PointSolution> constraintViolation = new OverallConstraintViolation<>();
  private DoubleProblem problem ;

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubfronts() {
    List<Solution<?>> population = Collections.emptyList();
    Ranking<Solution<?>> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(0, ranking.getNumberOfSubfronts());
  }

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnOneSubfronts(){
    problem = new DummyProblem(2);
    List<DoubleSolution> population = Arrays.asList(
            new DefaultDoubleSolution(problem),
            new DefaultDoubleSolution(problem),
            new DefaultDoubleSolution(problem));

    Ranking<DoubleSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubfronts());
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubfront() {
    int numberOfObjectives = 2 ;
    problem = new DummyProblem(numberOfObjectives);

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution() ;
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking<DoubleSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubfronts()) ;
    assertEquals(2, ranking.getSubfront(0).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(0, (int) ranking.getAttribute(population.get(1))) ;

    List<DoubleSolution> subfront = ranking.getSubfront(0) ;
    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
  }


  @Test
  public void shouldRankingOfAPopulationWithTwoDominatedSolutionsReturnTwoSubfronts() {
    int numberOfObjectives = 2 ;
    problem = new DummyProblem(numberOfObjectives);

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution();
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution();
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking<DoubleSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population) ;

    assertEquals(2, ranking.getNumberOfSubfronts()) ;

    assertNotNull(ranking.getSubfront(0));
    assertEquals(1, ranking.getSubfront(0).size()) ;
    assertEquals(1, ranking.getSubfront(1).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(population.get(1))) ;

    List<DoubleSolution> subfront = ranking.getSubfront(0) ;
    List<DoubleSolution> subfront1 = ranking.getSubfront(1) ;

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0))) ;
  }

  @Test
  public void shouldRankingOfAPopulationWithThreeDominatedSolutionsReturnThreeSubfronts() {
    int numberOfObjectives = 2 ;
    problem = new DummyProblem(numberOfObjectives);

    List<DoubleSolution>population = new ArrayList<>() ;

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

    Ranking<DoubleSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(3, ranking.getNumberOfSubfronts());

    assertNotNull(ranking.getSubfront(0));
    assertEquals(1, ranking.getSubfront(0).size());
    assertEquals(1, ranking.getSubfront(1).size());
    assertEquals(1, ranking.getSubfront(2).size());

    assertEquals(0, (int) ranking.getAttribute(population.get(0)));
    assertEquals(1, (int) ranking.getAttribute(population.get(1)));

    List<DoubleSolution> subfront = ranking.getSubfront(0);
    List<DoubleSolution> subfront1 = ranking.getSubfront(1);
    List<DoubleSolution> subfront2 = ranking.getSubfront(2);

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0)));
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0)));
    assertEquals(2, (int) ranking.getAttribute(subfront2.get(0)));
  }

  @Test
  public void shouldRankingOfAPopulationWithFiveSolutionsWorkProperly() {
    int numberOfObjectives = 2;
    problem = new DummyProblem(numberOfObjectives);

    List<DoubleSolution>population = new ArrayList<>();

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

    Ranking<DoubleSolution> ranking = new ExperimentalFastDominanceRanking<>();
    ranking.computeRanking(population);

    assertEquals(2, ranking.getNumberOfSubfronts());

    assertNotNull(ranking.getSubfront(0));
    assertEquals(3, ranking.getSubfront(0).size());
    assertEquals(2, ranking.getSubfront(1).size());

    List<DoubleSolution> subfront = ranking.getSubfront(0);
    List<DoubleSolution> subfront1 = ranking.getSubfront(1);

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0)));
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0)));
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

  private PointSolution makeConstrained(double x, double y, double c) {
    PointSolution sol = new PointSolution(2);
    sol.setObjective(0, x);
    sol.setObjective(1, y);
    constraintViolation.setAttribute(sol, c);
    return sol;
  }

  @SuppressWarnings("serial")
  private static class DummyProblem extends AbstractDoubleProblem {

    DummyProblem(int numberOfObjectives) {
      setNumberOfObjectives(numberOfObjectives);
    }

    @Override
    public void evaluate(DoubleSolution solution) {}
  }
}
