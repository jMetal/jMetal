package org.uma.jmetal.experimental.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;

public class AutoMOPSOIraceFront {
    public static void main(String[] args) throws IOException{
        AutoMOPSO mopsoWithParameters = new AutoMOPSO();
        mopsoWithParameters.parseAndCheckParameters(args);

        ParticleSwarmOptimizationAlgorithm  mopso = mopsoWithParameters.create();
        mopso.run();
        double[][] front = getMatrixWithObjectiveValues(mopso.getResult());
        for (int i = 0; i < front.length; i++){
            for (int j  = 0; j < front[0].length; j++){
                System.out.print(front[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
