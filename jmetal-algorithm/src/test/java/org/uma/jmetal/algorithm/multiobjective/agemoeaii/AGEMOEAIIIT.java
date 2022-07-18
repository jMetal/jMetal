package org.uma.jmetal.algorithm.multiobjective.agemoeaii;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.agemoea.util.SurvivalScoreComparator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

public class AGEMOEAIIIT {

  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new Kursawe();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
        new SurvivalScoreComparator<>());

    var populationSize = 100;
    algorithm =
        new AGEMOEAIIBuilder<>(problem)
            .setCrossoverOperator(crossover)
            .setMutationOperator(mutation)
            .setMaxIterations(250)
            .setPopulationSize(populationSize)
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is Kursawe, and usually AGE-MOEA-II, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 90);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    algorithm =
        new AGEMOEAIIBuilder<>(problem)
            .setCrossoverOperator(crossover)
            .setMutationOperator(mutation)
            .setMaxIterations(250)
            .setPopulationSize(populationSize)
            .build();

    algorithm.run();

    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
        new PISAHypervolume(
            VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and AGE-MOEA-II, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.62

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.62);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    var problem = new ConstrEx();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    algorithm =
        new AGEMOEAIIBuilder<>(problem)
            .setCrossoverOperator(crossover)
            .setMutationOperator(mutation)
            .setMaxIterations(250)
            .setPopulationSize(populationSize)
            .build();

    algorithm.run();

    var population = algorithm.getResult();

    var referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv";

    var referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    // Rationale: the default problem is DTLZ1 (3 objectives), and AGE-MOEA-II, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.96

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 85);
    assertTrue(hv > 0.77);
  }
}