package org.uma.jmetal.component.catalogue.ranking;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ranking.impl.StrengthRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
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
    DoubleProblem problem = new MockDoubleProblem(2) ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;
    DoubleSolution solution4 = problem.createSolution() ;

    solution1.objectives().set(0, 1.0);
    solution1.objectives().set(1, 5.0);

    solution2.objectives().set(0, 2.0);
    solution2.objectives().set(1, 4.0);

    solution3.objectives().set(0, 3.0);
    solution3.objectives().set(1, 3.0);

    solution4.objectives().set(0, 5.0);
    solution4.objectives().set(1, 1.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3, solution4) ;

    Ranking<Solution<?>> ranking = new StrengthRanking<>() ;
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

    solution1.objectives().set(0, 1.0);
    solution1.objectives().set(1, 5.0);

    solution2.objectives().set(0, 2.0);
    solution2.objectives().set(1, 4.0);

    solution3.objectives().set(0, 3.0);
    solution3.objectives().set(1, 3.0);

    solution4.objectives().set(0, 5.0);
    solution4.objectives().set(1, 1.0);

    solution5.objectives().set(0, 3.0);
    solution5.objectives().set(1, 2.0);

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution4, solution3, solution5);

    Ranking<DoubleSolution> ranking = new StrengthRanking<>() ;
    ranking.computeRanking(solutionList);

    assertEquals(2, ranking.getNumberOfSubFronts());
    assertEquals(0, solution1.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution2.getAttribute(ranking.getAttributeId()));
    assertEquals(1, solution3.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution4.getAttribute(ranking.getAttributeId()));
    assertEquals(0, solution5.getAttribute(ranking.getAttributeId()));
  }

  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(5.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.objectives().set(0, 0.0);
      solution.objectives().set(1, 1.0);
    }
  }
}