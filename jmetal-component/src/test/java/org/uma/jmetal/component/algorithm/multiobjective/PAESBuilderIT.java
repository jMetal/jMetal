package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

class PAESBuilderIT {
  @Test
  void PAESWithDefaultSettingsReturnsANonEmptyFrontWithHVHigherThanZeroPointOneFiveOnProblemZDT1() {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int archiveSize = 100;
    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<>(archiveSize);

    Termination termination = new TerminationByEvaluations(25000);

    EvolutionaryAlgorithm<DoubleSolution> paes = new PAESBuilder<>(
        problem,
        archiveSize,
        mutation,
        archive)
        .setTermination(termination)
        .build();

    paes.run();

    List<DoubleSolution> population = paes.result();

    double[][] referenceFront = new double[][]{{0.0, 1.0}, {1.0, 0.0}};
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);
    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertThat(population).isNotEmpty();
    assertThat(hv).isGreaterThan(0.15);
  }
}
