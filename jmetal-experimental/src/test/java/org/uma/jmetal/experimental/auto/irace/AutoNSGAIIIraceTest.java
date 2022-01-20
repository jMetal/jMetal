package org.uma.jmetal.experimental.auto.irace;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.qualityIndicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.front.util.FrontNormalizer;
import org.uma.jmetal.util.legacy.front.util.FrontUtils;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.point.PointSolution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

class AutoNSGAIIIraceTest {

@Test
public void shouldTheProgramRunProperly() throws FileNotFoundException {
  String[] arguments = ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
          + "--referenceFrontFileName ZDT1.csv "
          + "--maximumNumberOfEvaluations 75000 "
          + "--populationSize 100 --algorithmResult population --createInitialSolutions scatterSearch --variation crossoverAndMutationVariation --offspringPopulationSize 205 --crossover BLX_ALPHA --crossoverProbability 0.3286 --crossoverRepairStrategy round --blxAlphaCrossoverAlphaValue 0.9043 --mutation polynomial --mutationProbability 0.4431 --mutationRepairStrategy random --polynomialMutationDistributionIndex 86.8901 --selection tournament --selectionTournamentSize 2 ")
          .split("\\s+");

  AutoNSGAIIIrace autoNSGAIIIrace = new AutoNSGAIIIrace();
  autoNSGAIIIrace.parseAndCheckParameters(arguments);

  EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAIIIrace.create();
  nsgaII.run();

  String referenceFrontFile = "../resources/referenceFrontsCSV/ZDT1.csv" ;
  Front referenceFront = new ArrayFront(referenceFrontFile);

  FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
  Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
  Front normalizedFront = frontNormalizer.normalize(new ArrayFront(nsgaII.getResult()));
  List<PointSolution> normalizedPopulation =
          FrontUtils.convertFrontToSolutionList(normalizedFront);

  double referenceFrontHV =
          new PISAHypervolume<PointSolution>(normalizedReferenceFront)
                  .evaluate(FrontUtils.convertFrontToSolutionList(normalizedReferenceFront));
  double obtainedFrontHV =
          new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
  System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);


  assertNotNull(nsgaII.getResult());

}
@Test
public void shouldTheProgramRunProperlyV2() throws IOException {
    String[] arguments = ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--referenceFrontFileName ZDT1.csv "
            + "--maximumNumberOfEvaluations 75000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--offspringPopulationSize 100 "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--selection tournament "
            + "--selectionTournamentSize 2 "
            + "--rankingForSelection dominanceRanking "
            + "--densityEstimatorForSelection crowdingDistance "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbability 0.01 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAIIIrace autoNSGAIIIrace = new AutoNSGAIIIrace();
    autoNSGAIIIrace.parseAndCheckParameters(arguments);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAIIIrace.create();
    nsgaII.run();

    double[][] referenceFront = VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ",");
    double[][] front = getMatrixWithObjectiveValues(nsgaII.getResult()) ;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront) ;
    double value = qualityIndicator.compute(normalizedFront) ;

  System.out.println("NHV: "+ value);

  assertNotNull(nsgaII.getResult());

  }

}