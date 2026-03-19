package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoAGEMOEA;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoAGEMOEAIraceNHV {
  public static void main(String[] args) throws IOException {
    AutoAGEMOEA ageMoeaWithParameters = new AutoAGEMOEA();
    ageMoeaWithParameters.parse(args);

    EvolutionaryAlgorithm<DoubleSolution> ageMoea = ageMoeaWithParameters.create();
    ageMoea.run();

    String referenceFrontFile =
        "resources/referenceFrontsCSV/" + ageMoeaWithParameters.referenceFrontFilename.value();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[][] front = getMatrixWithObjectiveValues(ageMoea.result());

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront);
    System.out.println(qualityIndicator.compute(normalizedFront));
  }
}
