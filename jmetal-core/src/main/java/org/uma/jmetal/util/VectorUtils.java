package org.uma.jmetal.util;

public class VectorUtils {
  /**
   * Method that apply a dominance test between two vectors. It is assumed that the vectors have
   * been properly tested before calling this method to ensure that they have the same length
   *
   * @param vector1
   * @param vector2
   * @return 0 if the vectors are non-dominated, -1 if vector1 dominates vector2, and 1 if vector2
   *     dominates vector 1
   */
  public static int dominanceTest(double[] vector1, double[] vector2) {
    int bestIsOne = 0;
    int bestIsTwo = 0;
    int result;
    for (int i = 0; i < vector1.length; i++) {
      double value1 = vector1[i];
      double value2 = vector2[i];
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    result = Integer.compare(bestIsTwo, bestIsOne);
    return result;
  }
}
