package org.uma.jmetal.experimental.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class AutoMOPSOIraceFront {

  public static void main(String[] args) throws IOException {
    AutoMOPSO mopsoWithParameters = new AutoMOPSO();
    mopsoWithParameters.parseAndCheckParameters(args);

    ParticleSwarmOptimizationAlgorithm mopso = mopsoWithParameters.create();
    mopso.run();

    double[][] front = getMatrixWithObjectiveValues(mopso.getResult());
    for (int i = 0; i < front.length; i++) {
      for (int j = 0; j < front[0].length; j++) {
        System.out.print(front[i][j] + "\t");
      }
      System.out.println();
    }

    // Alternative 1
      for (double[] objectives : front) {
          for (int j = 0; j < front[0].length; j++) {
              System.out.print(objectives[j] + "\t");
          }
          System.out.println();
      }

    // Alternative 2
    List<DoubleSolution> resultFront = mopso.getResult();
    for (DoubleSolution solution : resultFront) {
      for (Double objetive : solution.objectives()) {
        System.out.print(objetive + "\t");
      }
      System.out.println();
    }

  }
}
