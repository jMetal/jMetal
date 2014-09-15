package org.uma.test3;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.encoding.attributes.Attributes;
import org.uma.jmetal3.encoding.impl.DoubleSolutionImpl;
import org.uma.jmetal3.metaheuristic.multiobjective.nsgaii.Ranking;
import org.uma.jmetal3.problem.multiobjective.Fonseca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio on 15/09/14.
 */
public class RankingTest {
  List<DoubleSolution> population ;
  Problem problem ;

  @Before
  public void startup() {
    problem = new Fonseca() ;
  }

  @Test
  public void rankingOfAnEmptyPopulation() {
    population = Collections.emptyList() ;
    Ranking ranking = new Ranking(population) ;
    assertEquals(0, ranking.getNumberOfSubfronts()) ;
  }

  @Test
  public void rankingOfAnPopulationOfSizeOne() {
    population = new ArrayList<>();

    DoubleSolution solution = (DoubleSolution)problem.createSolution(null) ;
    population.add(solution) ;

    Ranking ranking = new Ranking(population) ;
    assertEquals(1, ranking.getNumberOfSubfronts()) ;
  }

}
