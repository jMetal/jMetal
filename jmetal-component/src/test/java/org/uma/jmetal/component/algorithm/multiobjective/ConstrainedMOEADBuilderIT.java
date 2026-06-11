package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.FeasibilityRulesCriterion;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ImprovedEpsilonCriterion;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ViolationThresholdCriterion;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Osyczka2;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.problem.multiobjective.Tanaka;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

@DisplayName("Integration tests for MOEA/D with constraint handling subproblem update criteria")
class ConstrainedMOEADBuilderIT {

  private static final String WEIGHT_VECTOR_DIRECTORY = "../resources/weightVectorFiles/moead";
  private static final String REFERENCE_FRONTS_DIRECTORY = "../resources/referenceFrontsCSV/";

  @Test
  @DisplayName("MOEA/D with feasibility rules computes a front with an acceptable hypervolume on Srinivas")
  void moeadWithFeasibilityRulesReturnsAGoodFrontOnProblemSrinivas() throws IOException {
    Problem<DoubleSolution> problem = new Srinivas();

    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    int populationSize = 100;
    Termination termination = new TerminationByEvaluations(25000);
    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);

    EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADBuilder<>(
        problem,
        populationSize,
        crossover,
        mutation,
        WEIGHT_VECTOR_DIRECTORY,
        sequenceGenerator,
        false)
        .setTermination(termination)
        .setSubproblemUpdateCriterion(new FeasibilityRulesCriterion<>())
        .build();

    moead.run();

    double hv = normalizedHypervolume(moead.result(), "Srinivas.csv");

    assertThat(hv).isGreaterThan(0.50);
  }

  @Test
  @DisplayName("MOEA/D with violation threshold computes a front with an acceptable hypervolume on Osyczka2")
  void moeadWithViolationThresholdReturnsAGoodFrontOnProblemOsyczka2() throws IOException {
    Problem<DoubleSolution> problem = new Osyczka2();

    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    int populationSize = 100;
    Termination termination = new TerminationByEvaluations(30000);
    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);

    EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADBuilder<>(
        problem,
        populationSize,
        crossover,
        mutation,
        WEIGHT_VECTOR_DIRECTORY,
        sequenceGenerator,
        false)
        .setTermination(termination)
        .setSubproblemUpdateCriterion(new ViolationThresholdCriterion<>())
        .build();

    moead.run();

    double hv = normalizedHypervolume(moead.result(), "Osyczka2.csv");

    assertThat(hv).isGreaterThan(0.25);
  }

  @Test
  @DisplayName("MOEA/D-DE with improved epsilon computes a front with an acceptable hypervolume on Tanaka")
  void moeadDEWithImprovedEpsilonReturnsAGoodFrontOnProblemTanaka() throws IOException {
    Problem<DoubleSolution> problem = new Tanaka();

    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    int populationSize = 100;
    int maximumNumberOfEvaluations = 30000;
    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);

    int tc = (int) (0.8 * maximumNumberOfEvaluations / populationSize);

    EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADDEBuilder(
        problem,
        populationSize,
        1.0,
        0.5,
        mutation,
        WEIGHT_VECTOR_DIRECTORY,
        sequenceGenerator,
        false)
        .setTermination(termination)
        .setSubproblemUpdateCriterion(new ImprovedEpsilonCriterion<>(tc))
        .build();

    moead.run();

    double hv = normalizedHypervolume(moead.result(), "Tanaka.csv");

    assertThat(hv).isGreaterThan(0.28);
  }

  private double normalizedHypervolume(
      List<DoubleSolution> population, String referenceFrontFileName) throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors(REFERENCE_FRONTS_DIRECTORY + referenceFrontFileName, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);
    JMetalLogger.logger.info(referenceFrontFileName + " HV: " + hv);

    return hv;
  }
}
