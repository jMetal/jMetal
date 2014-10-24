package org.uma.test;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.problem.multiobjective.Fonseca;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Antonio on 15/09/14.
 */
public class RankingAttrTest {
  private List<Solution<?>> population ;
  private Problem problem ;
  private Ranking ranking ;

  @Before
  public void startup() {
    problem = new Fonseca() ;
    ranking = new DominanceRanking() ;
  }

  @Test
  public void rankingOfAnEmptyPopulation() {
    population = Collections.emptyList() ;
    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(population) ;
    assertEquals(0, ranking.getNumberOfSubfronts()) ;
  }

  @Test
  public void rankingOfAnPopulationOfSizeOne() {
    population = new ArrayList<>();

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    population.add(solution) ;

    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubfronts()) ;
    assertNotNull(ranking.getSubfront(0));
    try {
      assertNull(ranking.getSubfront(1)) ;
    } catch (JMetalException e) {
    }
  }

  @Test
  public void rankingOfAnPopulationWithTwoNonDominatedSolutions() {
    population = new ArrayList<>();

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(population) ;

    assertEquals(1, ranking.getNumberOfSubfronts()) ;
    assertNotNull(ranking.getSubfront(0));
    assertEquals(2, ranking.getSubfront(0).size()) ;

    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(0, (int) ranking.getAttribute(population.get(1))) ;

    List<Solution<?>> subfront = ranking.getSubfront(0) ;
    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
  }

  @Test
  public void rankingOfAnPopulationWithTwoDominatedSolutions() {
    population = new ArrayList<>();

    DoubleSolution solution = (DoubleSolution)problem.createSolution() ;
    solution.setObjective(0, 2.0);
    solution.setObjective(1, 3.0);
    DoubleSolution solution2 = (DoubleSolution)problem.createSolution() ;
    solution2.setObjective(0, 3.0);
    solution2.setObjective(1, 6.0);

    population.add(solution) ;
    population.add(solution2) ;

    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(population) ;

    assertEquals(2, ranking.getNumberOfSubfronts()) ;
    assertNotNull(ranking.getSubfront(0));
    assertEquals(1, ranking.getSubfront(0).size()) ;
    assertEquals(1, ranking.getSubfront(1).size()) ;
    assertEquals(0, (int) ranking.getAttribute(population.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(population.get(1))) ;

   // assertEquals(0, (int) population.get(0).getAttribute(Ranking.ATTRIBUTE.RANK)) ;
   // assertEquals(1, (int) population.get(1).getAttribute(Ranking.ATTRIBUTE.RANK)) ;

    List<Solution<?>> subfront = ranking.getSubfront(0) ;
    List<Solution<?>> subfront1 = ranking.getSubfront(1) ;

    assertEquals(0, (int) ranking.getAttribute(subfront.get(0))) ;
    assertEquals(1, (int) ranking.getAttribute(subfront1.get(0))) ;

    //assertEquals(0, (int) subfront.get(0).getAttribute(Ranking.ATTRIBUTE.RANK)) ;
    //assertEquals(1, (int) subfront1.get(0).getAttribute(Ranking.ATTRIBUTE.RANK)) ;
  }

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

}
