package org.uma.jmetal.algorithm.multiobjective.pesa2;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PESA2IT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    Kursawe problem = new Kursawe();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PESA2Builder<DoubleSolution>(problem, crossover, mutation).build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is Kursawe, and usually PESA2, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 99);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    ConstrEx problem = new ConstrEx();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PESA2Builder<DoubleSolution>(problem, crossover, mutation).build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    String referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv";

    Front referenceFront = new ArrayFront(referenceFrontFileName);
    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);

    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(population));
    List<PointSolution> normalizedPopulation =
        FrontUtils.convertFrontToSolutionList(normalizedFront);

    double hv =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);

    assertTrue(population.size() >= 98);
    assertTrue(hv > 0.7);
  }
}
