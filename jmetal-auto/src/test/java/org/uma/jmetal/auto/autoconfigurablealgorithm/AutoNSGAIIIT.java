package org.uma.jmetal.auto.autoconfigurablealgorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

class AutoNSGAIIIT {

  @Test
  void AutoNSGAIIHasFiveFirstLevelConfigurableParameters() {
    var autoNSGAII = new AutoNSGAII() ;

    assertThat(autoNSGAII.autoConfigurableParameterList).hasSize(5) ;
  }

  @Test
  void AutoNSGAIIHasFiveFourFixedParameters() {
    var autoNSGAII = new AutoNSGAII() ;

    assertThat(autoNSGAII.fixedParameterList()).hasSize(4) ;
  }

  @Test
  void AutoNSGAIIHas21Parameters() {
    var autoNSGAII = new AutoNSGAII() ;

    assertThat(AutoConfigurableAlgorithm.parameterNames(autoNSGAII.configurableParameterList())).hasSize(21) ;
  }

  @Test
  void AutoNSGAIIWithDefaultSettingsReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT1()
      throws IOException {
    String referenceFrontFileName = "ZDT1.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
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
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    nsgaII.run();

    List<DoubleSolution> population  = nsgaII.result() ;

    String referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 95) ;
    assertTrue(hv > 0.65) ;
  }

  @Test
  void AutoNSGAIIExternalUnboundedArchiveReturnsAFrontWithHVHigherThanZeroPointSixtyFourOnProblemZDT4()
      throws IOException {
    String referenceFrontFileName = "ZDT4.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
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
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    nsgaII.run();

    List<DoubleSolution> population  = nsgaII.result() ;

    String referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 92) ;
    assertTrue(hv > 0.64) ;
  }


  @Test
  void AutoNSGAIIExternalCrowdingArchiveReturnsAFrontWithHVHigherThanZeroPointSixtyFiveOnProblemZDT4()
      throws IOException {
    String referenceFrontFileName = "ZDT4.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT4 "
            + "--referenceFrontFileName "+ referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--randomGeneratorSeed 12 "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--externalArchive crowdingDistanceArchive "
            + "--populationSizeWithArchive 100 "
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
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    nsgaII.run();

    List<DoubleSolution> population  = nsgaII.result() ;

    String referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertThat(population).hasSizeGreaterThan(95) ;
    assertThat(hv).isGreaterThan(0.64) ;
  }

  @Test
  void AutoNSGAIIExternalUnboundedArchiveReturnsAFrontWithHVHigherThanZeroPointFourOnProblemDTLZ2()
      throws IOException {
    String referenceFrontFileName = "DTLZ2.3D.csv" ;

    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2 "
            + "--referenceFrontFileName "+ referenceFrontFileName + " "
            + "--maximumNumberOfEvaluations 30000 "
            + "--algorithmResult externalArchive "
            + "--externalArchive unboundedArchive "
            + "--populationSizeWithArchive 100 "
            + "--randomGeneratorSeed 12 "
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
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    nsgaII.run();

    List<DoubleSolution> population  = nsgaII.result() ;

    String referenceFrontFile = "../resources/referenceFrontsCSV/"+referenceFrontFileName ;

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    double[][] normalizedFront =
        NormalizeUtils.normalize(
            SolutionListUtils.getMatrixWithObjectiveValues(population),
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    double hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 95) ;
    assertTrue(hv > 0.40) ;
  }
}