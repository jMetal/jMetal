package org.uma.jmetal.auto.irace;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoAGEMOEA;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class AutoAGEMOEASavingFront {
  public static void main(String[] args) throws IOException {
    AutoAGEMOEA ageMoeaWithParameters = new AutoAGEMOEA();
    ageMoeaWithParameters.parse(args);

    EvolutionaryAlgorithm<DoubleSolution> ageMoea = ageMoeaWithParameters.create();
    ageMoea.run();

    List<DoubleSolution> population = ageMoea.result();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
