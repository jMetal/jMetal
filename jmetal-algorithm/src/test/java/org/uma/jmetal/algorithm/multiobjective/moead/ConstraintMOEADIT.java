package org.uma.jmetal.algorithm.multiobjective.moead;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.problem.multiobjective.Tanaka;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

public class ConstraintMOEADIT {

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() {
      DoubleProblem problem = new Srinivas();

    var cr = 1.0;
    var f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

      Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.ConstraintMOEAD)
              .setCrossover(crossover)
              .setMutation(mutation)
              .setMaxEvaluations(50000)
              .setPopulationSize(300)
              .setResultPopulationSize(100)
              .setNeighborhoodSelectionProbability(0.9)
              .setMaximumNumberOfReplacedSolutions(2)
              .setNeighborSize(20)
              .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
              .setDataDirectory("../../resources/weightVectorFiles/moead")
              .build();

    algorithm.run() ;
    var population = algorithm.getResult();

    assertTrue(population.size() == 100);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
      DoubleProblem problem = new Tanaka();

    var cr = 1.0;
    var f = 0.5;
    CrossoverOperator<DoubleSolution> crossover = new DifferentialEvolutionCrossover(cr, f,
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

      Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.ConstraintMOEAD)
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
                      "../resources/weightVectorFiles/moead")
              .build();

    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("../resources/referenceFrontsCSV/Tanaka.csv");

    // Rationale: the default problem is Tanaka", and the constraint MOEA/D algoritm,
    // configured with standard settings, should return find a front with a hypervolume value higher
    // than 0.22
    double hv = hypervolume.evaluate(population);

    assertTrue(hv > 0.22);
  }
}
