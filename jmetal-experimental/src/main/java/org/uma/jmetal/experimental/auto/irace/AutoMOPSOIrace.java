package org.uma.jmetal.experimental.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;

public class AutoMOPSOIrace {
    public static void main(String[] args) throws IOException{
        AutoMOPSO mopsoWithParameters = new AutoMOPSO();
        mopsoWithParameters.parseAndCheckParameters(args);

        ParticleSwarmOptimizationAlgorithm  mopso = mopsoWithParameters.create();
        mopso.run();

        String referenceFrontFile = "resources/referenceFrontsCSV/" + mopsoWithParameters.referenceFrontFilenameParameter.getValue();

        double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
        double[][] front = getMatrixWithObjectiveValues(mopso.getResult()) ;

        double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
        double[][] normalizedFront =
                NormalizeUtils.normalize(
                        front,
                        NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                        NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

        var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront) ;
        System.out.println(qualityIndicator.compute(normalizedFront)) ;
    }
}
