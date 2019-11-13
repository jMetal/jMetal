package org.uma.jmetal.util.solutionattribute.impl;

import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DominanceRankingTest {
  private DoubleProblem problem ;

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnZeroSubfronts() {
    List<Solution<?>> population = Collections.emptyList() ;
    Ranking<Solution<?>> ranking = new DominanceRanking<Solution<?>>() ;
    ranking.computeRanking(population) ;

    assertEquals(0, ranking.getNumberOfSubfronts()) ;
  }

  @Test
  public void shouldTheRankingOfAnEmptyPopulationReturnOneSubfronts(){
    problem = new DummyProblem(2) ;
    List<DoubleSolution> population = Arrays.<DoubleSolution>asList(
            new DefaultDoubleSolution(problem),
            new DefaultDoubleSolution(problem),
            new DefaultDoubleSolution(problem));

    Ranking<DoubleSolution> ranking = new DominanceRanking<DoubleSolution>() ;
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubfronts());
  }

  @Test
  public void shouldRankingOfAPopulationWithTwoNonDominatedSolutionsReturnOneSubfront() {
    int numberOfObjectives = 2 ;
    problem = new DummyProblem(numberOfObjectives) ;

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = problem.createSolution() ;
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking<DoubleSolution> ranking = new DominanceRanking<>() ;
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
    problem = new DummyProblem(numberOfObjectives) ;

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking<DoubleSolution> ranking = new DominanceRanking<>() ;
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
    problem = new DummyProblem(numberOfObjectives) ;

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);
    DoubleSolution solution3 = (DoubleSolution)problem.createSolution() ;
    solution3.setObjective(0, 4.0);
    solution3.setObjective(1, 8.0);

    population.add(solution) ;
    population.add(solution2) ;
    population.add(solution3) ;

    Ranking<DoubleSolution> ranking = new DominanceRanking<>() ;
    ranking.computeRanking(population) ;

    assertEquals(3, ranking.getNumberOfSubfronts()) ;

    assertNotNull(ranking.getSubfront(0));
    assertEquals(1, ranking.getSubfront(0).size()) ;
    assertEquals(1, ranking.getSubfront(1).size()) ;
    assertEquals(1, ranking.getSubfront(2).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(population.get(1))) ;

    List<DoubleSolution> subfront = ranking.getSubfront(0) ;
    List<DoubleSolution> subfront1 = ranking.getSubfront(1) ;
    List<DoubleSolution> subfront2 = ranking.getSubfront(2) ;

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0))) ;
    assertEquals(2, (int) ranking.getAttribute(subfront2.get(0))) ;
  }

  @Test
  public void shouldRankingOfAPopulationWithFiveSolutionsWorkProperly() {
    int numberOfObjectives = 2 ;
    problem = new DummyProblem(numberOfObjectives) ;

    List<DoubleSolution>population = new ArrayList<>() ;

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setObjective(0, 1.0);
    solution.setObjective(1, 0.0);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setObjective(0, 0.6);
    solution2.setObjective(1, 0.6);
    DoubleSolution solution3 = (DoubleSolution)problem.createSolution() ;
    solution3.setObjective(0, 0.5);
    solution3.setObjective(1, 0.5);
    DoubleSolution solution4 = (DoubleSolution)problem.createSolution() ;
    solution4.setObjective(0, 1.1);
    solution4.setObjective(1, 0.0);
    DoubleSolution solution5 = (DoubleSolution)problem.createSolution() ;
    solution5.setObjective(0, 0.0);
    solution5.setObjective(1, 1.0);

    population.add(solution) ;
    population.add(solution2) ;
    population.add(solution3) ;
    population.add(solution4) ;
    population.add(solution5) ;

    Ranking<DoubleSolution> ranking = new DominanceRanking<>() ;
    ranking.computeRanking(population) ;

    assertEquals(2, ranking.getNumberOfSubfronts()) ;

    assertNotNull(ranking.getSubfront(0));
    assertEquals(3, ranking.getSubfront(0).size()) ;
    assertEquals(2, ranking.getSubfront(1).size()) ;

    //assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    //assertEquals(1, (int) ranking.getAttribute(population.get(1))) ;

    List<DoubleSolution> subfront = ranking.getSubfront(0) ;
    List<DoubleSolution> subfront1 = ranking.getSubfront(1) ;

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0))) ;
  }
/*
  @Test
  public void anotherTest() {
    population = new ArrayList<>();

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setVariableValue(0, -93536.88629126895);
    solution.setObjective(0, 8.749149097065777E9);
    solution.setObjective(1, 8.749523248610943E9);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setVariableValue(0, -55341.05654491017);
    solution2.setObjective(0, 3.0626325395069447E9);
    solution2.setObjective(1, 3.0628539077331243E9);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(population) ;

    assertEquals(1, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(0, (int) ranking.getAttribute(population.get(1))) ;
    //assertEquals(1, (int) population.get(0).getAttribute(Ranking.ATTRIBUTE.RANK)) ;
    //assertEquals(0, (int) population.get(1).getAttribute(Ranking.ATTRIBUTE.RANK)) ;
  }
*/

  @SuppressWarnings("serial")
  private class DummyProblem extends AbstractDoubleProblem {

    public DummyProblem(int numberOfObjectives) {
      setNumberOfObjectives(numberOfObjectives);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
    }
  }
}
