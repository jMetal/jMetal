package org.uma.jmetal.algorithm.multiobjective.mocell;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MOCellIT {
  Algorithm<List<DoubleSolution>> algorithm;
  DoubleProblem problem;
  CrossoverOperator<DoubleSolution> crossover;
  MutationOperator<DoubleSolution> mutation;

  @Before
  public void setup() {
    problem = new ZDT4();

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    algorithm =
        new MOCellBuilder<DoubleSolution>(problem, crossover, mutation)
            .setArchive(new CrowdingDistanceArchive<DoubleSolution>(100))
            .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and MOCell, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    algorithm =
        new MOCellBuilder<DoubleSolution>(problem, crossover, mutation)
            .setArchive(new CrowdingDistanceArchive<DoubleSolution>(100))
            .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("../resources/referenceFrontsCSV/ZDT4.csv");

    // Rationale: the default problem is ZDT4, and MOCell, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.65

    double hv = (Double) hypervolume.evaluate(population);

    assertTrue(hv > 0.64);
  }
}
