package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoNSGAIIIraceNHV {
  public static void main(String[] args) throws IOException {
    AutoNSGAII nsgaiiWithParameters = new AutoNSGAII();
    nsgaiiWithParameters.parseAndCheckParameters(args);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create();
    nsgaII.run();

    @NotNull String referenceFrontFile =
        "resources/referenceFrontsCSV/" + nsgaiiWithParameters.referenceFrontFilename.getValue();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[] @NotNull [] front = getMatrixWithObjectiveValues(nsgaII.getResult()) ;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    @NotNull var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront) ;
    System.out.println(qualityIndicator.compute(normalizedFront)) ;
  }
}
