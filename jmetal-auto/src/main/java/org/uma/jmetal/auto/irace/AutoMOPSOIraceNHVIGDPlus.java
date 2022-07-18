package org.uma.jmetal.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOPSO;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoMOPSOIraceNHVIGDPlus {

  public static void main(String[] args) throws IOException {
    @NotNull AutoMOPSO mopsoWithParameters = new AutoMOPSO();
    mopsoWithParameters.parseAndCheckParameters(args);

    var mopso = mopsoWithParameters.create();
    mopso.run();

    var referenceFrontFile = "resources/referenceFrontsCSV/"
        + mopsoWithParameters.referenceFrontFilenameParameter.getValue();

    var referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    var front = getMatrixWithObjectiveValues(mopso.getResult());

    var normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    var normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var normalizedHypervolume = new NormalizedHypervolume(normalizedReferenceFront);
    @NotNull var igdPlus = new InvertedGenerationalDistancePlus(normalizedReferenceFront);
    System.out.println(
        normalizedHypervolume.compute(normalizedFront) * igdPlus.compute(normalizedFront));
  }
}
