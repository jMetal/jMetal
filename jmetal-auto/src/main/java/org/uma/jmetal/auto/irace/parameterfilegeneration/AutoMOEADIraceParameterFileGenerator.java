package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEAD;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;

/**
 * Program to generate the irace configuration file for class {@link AutoNSGAII}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class AutoMOEADIraceParameterFileGenerator {

  public static void main(String[] args) {
    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1 "
            + "--referenceFrontFileName DTLZ1.3D.csv "
            + "--randomGeneratorSeed 124 "
            + "--maximumNumberOfEvaluations 25000 "
            + "--algorithmResult population "
            + "--normalizeObjectives false "
            + "--populationSize 91 "
            + "--offspringPopulationSize 1 "
            + "--createInitialSolutions random "
            + "--neighborhoodSize 20 "
            + "--maximumNumberOfReplacedSolutions 2 "
            + "--aggregationFunction penaltyBoundaryIntersection "
            + "--pbiTheta 5.0 "
            + "--neighborhoodSelectionProbability 0.9 "
            + "--variation crossoverAndMutationVariation "
            + "--selection populationAndNeighborhoodMatingPoolSelection "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator() ;
    parameterFileGenerator.generateConfigurationFile(new AutoMOEAD(), parameters) ;
  }
}
