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
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

class SMPSOBuilderIT {

  @Test
  void SMPSOWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT4() {
    var problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var swarmSize = 100;
    Termination termination = new TerminationByEvaluations(25000);

    var smpso = new SMPSOBuilder(
        (DoubleProblem) problem,
        swarmSize)
        .setTermination(termination)
        .build();

    smpso.run();

    var referenceFront = new double[][]{{0.0, 1.0}, {1.0, 0.0}};
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(smpso.getResult()),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(smpso.getResult()).hasSizeGreaterThan(95);
    assertThat(hv).isGreaterThan(0.65);
  }

  @Test
  void SMPSOWithExternalUnboundedArchiveReturnsAFrontWithHVHigherThanZeroPointThirtyFiveOnProblemDTLZ2()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    var referenceFrontFileName = "DTLZ2.3D.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var swarmSize = 100;

    Termination termination = new TerminationByEvaluations(50000);

    Archive<DoubleSolution> archive = new BestSolutionsArchive<>(
        new NonDominatedSolutionListArchive<>(), swarmSize);
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluationWithArchive<>(problem, archive);

    var smpso = new SMPSOBuilder(
        (DoubleProblem) problem,
        swarmSize)
        .setTermination(termination)
        .setEvaluation(evaluation)
        .build();

    smpso.run();

    var obtainedSolutions = archive.getSolutionList();

    var referenceFrontFile = "../resources/referenceFrontsCSV/" + referenceFrontFileName;

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(obtainedSolutions),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(obtainedSolutions).hasSizeGreaterThan(95);
    assertThat(hv).isGreaterThan(0.35);
  }
}