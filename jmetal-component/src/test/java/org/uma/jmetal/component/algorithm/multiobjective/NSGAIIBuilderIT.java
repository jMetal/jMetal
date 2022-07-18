package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

class NSGAIIBuilderIT {
  @Test
  void NSGAIIWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT1()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    var offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(25000);

    var nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();

    nsgaii.run();

    var population = nsgaii.getResult();

    var referenceFront = new double[][]{{0.0, 1.0}, {1.0, 0.0}} ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(populationSize).isGreaterThan(95) ;
    assertThat(hv).isGreaterThan(0.65) ;
  }

  @Test
  void NSGAIIWithExternalCrowdingArchiveReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT4()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    var offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(25000);

    Archive<DoubleSolution> archive = new CrowdingDistanceArchive<>(populationSize) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluationWithArchive<>(problem, archive) ;

    var nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setEvaluation(evaluation)
        .build();

    nsgaii.run();

    var population = nsgaii.getResult();

    var referenceFront = new double[][]{{0.0, 1.0}, {1.0, 0.0}} ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    System.out.println(hv);

    assertThat(populationSize).isGreaterThan(95) ;
    assertThat(hv).isGreaterThan(0.65) ;
  }

  @Test
  void NSGAIIWithExternalUnboundedArchiveReturnsAFrontWithHVHigherThanZeroPointFourOnProblemDTLZ2()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    var referenceFrontFileName = "DTLZ2.3D.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    var offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(50000);

    Archive<DoubleSolution> archive = new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), populationSize) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluationWithArchive<>(problem, archive) ;

    var nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setEvaluation(evaluation)
        .build();

    nsgaii.run();

    var population = archive.getSolutionList();

    var referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(populationSize).isGreaterThan(95) ;
    assertThat(hv).isGreaterThan(0.40) ;
  }
}