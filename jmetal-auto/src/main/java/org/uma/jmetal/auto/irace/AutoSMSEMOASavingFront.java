package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.List;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoSMSEMOA;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class AutoSMSEMOASavingFront {
  public static void main(String[] args) throws IOException {
    var smsemoaWithParameters = new AutoSMSEMOA();
    smsemoaWithParameters.parse(args);

    EvolutionaryAlgorithm<DoubleSolution> smsemoa = smsemoaWithParameters.create();
    smsemoa.run();

    List<DoubleSolution> population = smsemoa.result();

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
  }
}
