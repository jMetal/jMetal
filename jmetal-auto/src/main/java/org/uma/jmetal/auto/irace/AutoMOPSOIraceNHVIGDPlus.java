package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOPSO;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoMOPSOIraceNHVIGDPlus {

  public static void main(String[] args) throws IOException {
    AutoMOPSO mopsoWithParameters = new AutoMOPSO();
    mopsoWithParameters.configure();
    mopsoWithParameters.parse(args);

    ParticleSwarmOptimizationAlgorithm mopso = mopsoWithParameters.create();
    mopso.run();

    String referenceFrontFile = "resources/referenceFrontsCSV/"
        + mopsoWithParameters.referenceFrontFilenameParameter.value();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[][] front = getMatrixWithObjectiveValues(mopso.result());

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var normalizedHypervolume = new NormalizedHypervolume(normalizedReferenceFront);
    var igdPlus = new InvertedGenerationalDistancePlus(normalizedReferenceFront);
    System.out.println(
        normalizedHypervolume.compute(normalizedFront) * igdPlus.compute(normalizedFront));
  }
}
