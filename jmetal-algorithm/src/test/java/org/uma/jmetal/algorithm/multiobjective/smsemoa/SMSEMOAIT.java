package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

public class SMSEMOAIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    var problem = new ZDT4();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      Hypervolume<DoubleSolution> hypervolumeImplementation = new PISAHypervolume<>();
    hypervolumeImplementation.setOffset(100.0);

    algorithm =
        new SMSEMOABuilder<>(problem, crossover, mutation)
            .setSelectionOperator(new RandomSelection<DoubleSolution>())
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setHypervolumeImplementation(hypervolumeImplementation)
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and usually SMSEMOA, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      Hypervolume<DoubleSolution> hypervolumeImplementation = new PISAHypervolume<>();
    hypervolumeImplementation.setOffset(100.0);

    algorithm =
        new SMSEMOABuilder<>(problem, crossover, mutation)
            .setSelectionOperator(new RandomSelection<DoubleSolution>())
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setHypervolumeImplementation(hypervolumeImplementation)
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("../resources/referenceFrontsCSV/ZDT1.csv");

    // Rationale: the default problem is ZDT1, and SMSEMOA, configured with standard settings,
    // should
    // return find a front with a hypervolume value higher than 0.65

    double hv = (Double) hypervolume.evaluate(population);

    assertTrue(hv > 0.65);
  }
}
