package org.uma.jmetal.auto.algorithm.spea2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.algorithm.moea.AutoMOEA;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class SPEA2WithParameters {

  public static void main(String[] args) {
    String[] parameters =
        ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT4 "
            + "--referenceFrontFileName ZDT4.pf "
            + "--maximumNumberOfEvaluations 25000 "
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
            + "--knnDistanceKValue 1 ")
            .split("\\s+");

    AutoSPEA2 SPEA2 = new AutoSPEA2();
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
