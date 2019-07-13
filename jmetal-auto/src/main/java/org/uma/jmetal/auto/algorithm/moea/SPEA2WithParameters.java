package org.uma.jmetal.auto.algorithm.moea;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class SPEA2WithParameters {

  public static void main(String[] args) {
    String[] parameters =
            /*
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                + "--referenceFrontFileName ZDT1.pf "
                + "--maximumNumberOfEvaluations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 2 "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbability 0.01 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
                */
        ("--problemName org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1 "
            + "--referenceFrontFileName DTLZ1.3D.pf "
            + "--maximumNumberOfEvaluations 50000 "
            + "--algorithmResult population "
            + "--populationSize 100 "
            + "--offspringPopulationSize 100 "
            + "--createInitialSolutions random "
            + "--variation crossoverAndMutationVariation "
            + "--selection tournament "
            + "--selectionTournamentSize 2 "
            + "--rankingForSelection strengthRanking "
            + "--densityEstimatorForSelection knn "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbability 0.01 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--replacement rankingAndDensityEstimatorReplacement "
            + "--removalPolicy sequential "
            + "--rankingForReplacement strengthRanking "
            + "--densityEstimatorForReplacement knn ")
            .split("\\s+");

    AutoMOEA SPEA2 = new AutoMOEA();
    SPEA2.parseAndCheckParameters(parameters);

    SPEA2.print(SPEA2.fixedParameterList);
    SPEA2.print(SPEA2.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> spea2 = SPEA2.create();
    spea2.run();

    new SolutionListOutput(spea2.getResult())
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();
  }
}
