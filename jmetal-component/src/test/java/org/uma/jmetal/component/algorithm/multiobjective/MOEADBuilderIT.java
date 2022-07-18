package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
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
import org.uma.jmetal.util.aggregativefunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

class MOEADBuilderIT {
  @Test
  void MOEADWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSeventySevenOnProblemDTLZ1()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
    var referenceFrontFileName = "DTLZ1.3D.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 91;

    Termination termination = new TerminationByEvaluations(40000);

    var weightVectorDirectory = "../resources/weightVectorFiles/moead";

    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(populationSize) ;
    var moead = new MOEADBuilder<>(
        problem,
        populationSize,
        crossover,
        mutation,
        weightVectorDirectory,
        sequenceGenerator)
        .setTermination(termination)
        .setMaximumNumberOfReplacedSolutionsy(2)
        .setNeighborhoodSelectionProbability(0.9)
        .setNeighborhoodSize(20)
        .setAggregativeFunction(new PenaltyBoundaryIntersection())
        .build();

    moead.run();

    var population = moead.getResult();

    var referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(populationSize).isGreaterThan(90) ;
    assertThat(hv).isGreaterThan(0.77) ;
  }

  @Test
  void MOEADDEWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroSixtyFiveOnProblemLZ09F2()
      throws IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
    var referenceFrontFileName = "DTLZ1.3D.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);
    var cr = 1.0 ;
    var f = 0.5 ;

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 300;

    Termination termination = new TerminationByEvaluations(175000);

    var weightVectorDirectory = "../resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(populationSize) ;

    var moead = new MOEADDEBuilder(
        problem,
        populationSize,
        cr,
        f,
        mutation,
        weightVectorDirectory,
        sequenceGenerator)
        .setTermination(termination)
        .setMaximumNumberOfReplacedSolutionsy(2)
        .setNeighborhoodSelectionProbability(0.9)
        .setNeighborhoodSize(20)
        .setAggregativeFunction(new Tschebyscheff())
        .build() ;

    moead.run();

    var population = moead.getResult();

    var referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    var normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertThat(populationSize).isGreaterThan(90) ;
    assertThat(hv).isGreaterThan(0.65) ;
  }
}