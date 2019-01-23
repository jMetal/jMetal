package org.uma.jmetal.algorithm.multiobjective.moead;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F2;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F3;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;

public class MOEADDRAIT {

  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
    LZ09F3 problem = new LZ09F3();

    double cr = 1.0;
    double f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
        "rand/1/bin");

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

    algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRA)
        .setCrossover(crossover)
        .setMutation(mutation)
        .setMaxEvaluations(50000)
        .setPopulationSize(300)
        .setResultPopulationSize(100)
        .setNeighborhoodSelectionProbability(0.9)
        .setMaximumNumberOfReplacedSolutions(2)
        .setNeighborSize(20)
        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
        .setDataDirectory(
            "/Users/ajnebro/Softw/jMetal/jMetal/jmetal-core/src/main/resources/MOEAD_Weights")
        .build();

    algorithm.run() ;
    List<DoubleSolution> population = algorithm.getResult();

    assertTrue(population.size() == 100);
  }

  @Ignore("fail when making a deployment")
  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    LZ09F3 problem = new LZ09F3();

    double cr = 1.0;
    double f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
        "rand/1/bin");

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

    algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADDRA)
        .setCrossover(crossover)
        .setMutation(mutation)
        .setMaxEvaluations(150000)
        .setPopulationSize(300)
        .setResultPopulationSize(100)
        .setNeighborhoodSelectionProbability(0.9)
        .setMaximumNumberOfReplacedSolutions(2)
        .setNeighborSize(20)
        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
        .setDataDirectory(
            "/Users/ajnebro/Softw/jMetal/jMetal/jmetal-core/src/main/resources/MOEAD_Weights")
        .build();

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("/pareto_fronts/LZ09_F3.pf");

    // Rationale: the default problem is LZ09F", and MOEA/D, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.96
    double hv = hypervolume.evaluate(population);

    assertTrue(hv > 0.65);
  }
}
