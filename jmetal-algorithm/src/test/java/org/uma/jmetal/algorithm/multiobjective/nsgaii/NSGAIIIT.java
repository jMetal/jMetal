package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class NSGAIIIT {
  Algorithm<List<Solution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    Problem problem = new Kursawe() ;

    algorithm = new NSGAIIBuilder(problem, NSGAIIBuilder.NSGAIIVariant.NSGAII).build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<Solution> population = algorithm.getResult() ;

    /*
    Rationale: the default problem is Kursawe, and usually NSGA-II, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }
}
