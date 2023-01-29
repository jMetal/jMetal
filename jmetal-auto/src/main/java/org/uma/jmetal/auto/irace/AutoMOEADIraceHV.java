package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEAD;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoMOEADIraceHV {
  public static void main(String[] args) throws IOException {
    AutoMOEAD autoMOEAD = new AutoMOEAD();
    autoMOEAD.parseAndCheckParameters(args);

    EvolutionaryAlgorithm<DoubleSolution> mopso = autoMOEAD.create();
    mopso.run();

    String referenceFrontFile = "resources/referenceFrontsCSV/"
        + autoMOEAD.referenceFrontFilenameParameter.getValue();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[][] front = getMatrixWithObjectiveValues(mopso.getResult()) ;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new PISAHypervolume(normalizedReferenceFront) ;
    System.out.println(qualityIndicator.compute(normalizedFront) * -1.0) ;
  }
}
