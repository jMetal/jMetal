package org.uma.jmetal.component.catalogue.ea.replacement;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SMSEMOAReplacement with 2-objective problems.
 */
class SMSEMOAReplacement2DTest {

  /**
   * Basic test with simple 2D Pareto front.
   */
  @Test
  void testReplacement2D() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    
    // Create population of 4 solutions + 1 offspring = 5 total, should reduce to 4
    List<DoubleSolution> population = new ArrayList<>();
    population.add(createSolution(problem, new double[]{1.0, 5.0}));
    population.add(createSolution(problem, new double[]{2.0, 4.0}));
    population.add(createSolution(problem, new double[]{3.0, 3.0}));
    population.add(createSolution(problem, new double[]{4.0, 2.0}));
    
    List<DoubleSolution> offspring = new ArrayList<>();
    offspring.add(createSolution(problem, new double[]{5.0, 1.0}));

    Replacement<DoubleSolution> replacement = 
        new SMSEMOAReplacement<>(new FastNonDominatedSortRanking<>());

    List<DoubleSolution> result = replacement.replace(population, offspring);

    // Should return 4 solutions (remove 1 from 5)
    assertEquals(4, result.size());
    
      // Removed print statements for unit test cleanliness
    
    // Removed print statements for unit test cleanliness
  }

  /**
   * Test that verifies it works correctly as a replacement operator.
   */
  @Test
  void testMultipleReplacements() {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      population.add(createSolution(problem, new double[]{i + 1.0, 10.0 - i}));
    }
    
    List<DoubleSolution> offspring = new ArrayList<>();
    offspring.add(createSolution(problem, new double[]{5.5, 5.5}));

    Replacement<DoubleSolution> replacement = 
        new SMSEMOAReplacement<>(new FastNonDominatedSortRanking<>());

    List<DoubleSolution> result = replacement.replace(population, offspring);

    // Should maintain the population size
    assertEquals(population.size(), result.size());
    
      // Removed print statements for unit test cleanliness
  }

  /**
   * Test with 3-objective problem (should use original algorithm).
   */
  @Test
  void testReplacement3D() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);
    
    List<DoubleSolution> population = new ArrayList<>();
    population.add(createSolution(problem, new double[]{1.0, 5.0, 5.0}));
    population.add(createSolution(problem, new double[]{2.0, 4.0, 4.0}));
    population.add(createSolution(problem, new double[]{3.0, 3.0, 3.0}));
    population.add(createSolution(problem, new double[]{4.0, 2.0, 2.0}));
    
    List<DoubleSolution> offspring = new ArrayList<>();
    offspring.add(createSolution(problem, new double[]{5.0, 1.0, 1.0}));

    Replacement<DoubleSolution> replacement = 
        new SMSEMOAReplacement<>(new FastNonDominatedSortRanking<>());

    List<DoubleSolution> result = replacement.replace(population, offspring);

    assertEquals(4, result.size());
    
    // Removed print statements for unit test cleanliness
  }

  private DoubleSolution createSolution(DoubleProblem problem, double[] objectives) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < objectives.length; i++) {
      solution.objectives()[i] = objectives[i];
    }
    return solution;
  }
}
