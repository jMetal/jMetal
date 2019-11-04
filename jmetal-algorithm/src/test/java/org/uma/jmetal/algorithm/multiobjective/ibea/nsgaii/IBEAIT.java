package org.uma.jmetal.algorithm.multiobjective.ibea.nsgaii;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class IBEAIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
    Kursawe problem = new Kursawe() ;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int populationSize = 100 ;
    algorithm = new IBEABuilder<>(problem, crossover, mutation)
            .setArchiveSize(populationSize)
            .setPopulationSize(populationSize)
            .setMaxEvaluations(25000)
            .build() ;

    new AlgorithmRunner.Executor(algorithm).execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;

    /*
    Rationale: the default problem is Kursawe, and usually IBEA, configured with standard settings, should return
    more than 80 solutions
    */
    assertTrue(population.size() >= 80) ;
  }
}
