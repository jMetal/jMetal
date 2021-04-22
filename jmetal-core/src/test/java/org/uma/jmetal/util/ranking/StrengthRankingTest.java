package org.uma.jmetal.util.ranking;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StrengthRankingTest {

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
    DoubleProblem problem = new DummyDoubleProblem(2,2,0) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;
    DoubleSolution solution4 = problem.createSolution() ;

    solution1.objectives()[0] = 1.0;
    solution1.objectives()[1] = 5.0;

    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 4.0;

    solution3.objectives()[0] = 3.0;
    solution3.objectives()[1] = 3.0;

    solution4.objectives()[0] = 5.0;
    solution4.objectives()[1] = 1.0;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3, solution4) ;

    Ranking<Solution<?>> ranking = new StrengthRanking<>() ;
    ranking.compute(solutionList);

    assertEquals(1, ranking.getNumberOfSubFronts());
    assertEquals(0, ranking.getRank(solution1));
    assertEquals(0, ranking.getRank(solution2));
    assertEquals(0, ranking.getRank(solution3));
    assertEquals(0, ranking.getRank(solution4));
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
    DoubleProblem problem = new DummyDoubleProblem(2,2,0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();
    DoubleSolution solution5 = problem.createSolution();

    solution1.objectives()[0] = 1.0;
    solution1.objectives()[1] = 5.0;

    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 4.0;

    solution3.objectives()[0] = 3.0;
    solution3.objectives()[1] = 3.0;

    solution4.objectives()[0] = 5.0;
    solution4.objectives()[1] = 1.0;

    solution5.objectives()[0] = 3;
    solution5.objectives()[1] = 2;

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution4, solution3, solution5);

    Ranking<DoubleSolution> ranking = new StrengthRanking<>() ;
    ranking.compute(solutionList);

    assertEquals(2, ranking.getNumberOfSubFronts());
    assertEquals(0, ranking.getRank(solution1));
    assertEquals(0, ranking.getRank(solution2));
    assertEquals(1, ranking.getRank(solution3));
    assertEquals(0, ranking.getRank(solution4));
    assertEquals(0, ranking.getRank(solution5));
  }
}