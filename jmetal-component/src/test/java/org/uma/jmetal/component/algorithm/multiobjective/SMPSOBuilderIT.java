package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

class SMPSOBuilderIT {

  @Test
  void SMPSOWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT4() {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    int swarmSize = 100;
    Termination termination = new TerminationByEvaluations(25000);

    ParticleSwarmOptimizationAlgorithm smpso = new SMPSOBuilder(
        (DoubleProblem) problem,
        swarmSize)
        .setTermination(termination)
        .build();

    smpso.run();

    double[][] referenceFront = new double[][]{{0.0, 1.0}, {1.0, 0.0}};
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(smpso.getResult()),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertThat(smpso.getResult()).hasSizeGreaterThan(95);
    assertThat(hv).isGreaterThan(0.65);
  }

  @Test
  void SMPSOWithExternalUnboundedArchiveReturnsAFrontWithHVHigherThanZeroPointThirtyFiveOnProblemDTLZ2()
      throws IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceFrontFileName = "DTLZ2.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int swarmSize = 100;

    Termination termination = new TerminationByEvaluations(50000);

    Archive<DoubleSolution> archive = new BestSolutionsArchive<>(
        new NonDominatedSolutionListArchive<>(), swarmSize);
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluationWithArchive<>(problem, archive);

    ParticleSwarmOptimizationAlgorithm smpso = new SMPSOBuilder(
        (DoubleProblem) problem,
        swarmSize)
        .setTermination(termination)
        .setEvaluation(evaluation)
        .build();

    smpso.run();

    List<DoubleSolution> obtainedSolutions = archive.solutions();

    String referenceFrontFile = "../resources/referenceFrontsCSV/" + referenceFrontFileName;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(obtainedSolutions),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertThat(obtainedSolutions).hasSizeGreaterThan(95);
    assertThat(hv).isGreaterThan(0.35);
  }
}