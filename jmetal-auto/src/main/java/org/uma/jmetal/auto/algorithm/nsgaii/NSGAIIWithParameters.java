package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class NSGAIIWithParameters {

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
            ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                    + "--referenceFrontFileName ZDT1.pf "
                    + "--maximumNumberOfEvaluations 25000 "
                    + "--algorithmResult externalArchive "
                    + "--populationSizeWithArchive 20 "
                    + "--populationSize 100 "
                    + "--offspringPopulationSize 50 "
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
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parseAndCheckParameters(parameters);

    autoNSGAII.print(autoNSGAII.fixedParameterList);
    autoNSGAII.print(autoNSGAII.autoConfigurableParameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();
    nsgaII.run();

    new SolutionListOutput(nsgaII.getResult())
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();
  }
}
