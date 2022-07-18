package org.uma.jmetal.experimental.auto.irace;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class AutoMOPSOIraceFront {

  public static void main(String[] args) throws IOException {
    var mopsoWithParameters = new AutoMOPSO();
    mopsoWithParameters.parseAndCheckParameters(args);

    var mopso = mopsoWithParameters.create();
    mopso.run();

    var front = getMatrixWithObjectiveValues(mopso.getResult());
    for (var i = 0; i < front.length; i++) {
      for (var j = 0; j < front[0].length; j++) {
        System.out.print(front[i][j] + "\t");
      }
      System.out.println();
    }

    // Alternative 1
      for (var objectives : front) {
          for (var j = 0; j < front[0].length; j++) {
              System.out.print(objectives[j] + "\t");
          }
          System.out.println();
      }

    // Alternative 2
    var resultFront = mopso.getResult();
    for (var solution : resultFront) {
      for (Double objetive : solution.objectives()) {
        System.out.print(objetive + "\t");
      }
      System.out.println();
    }

  }
}
