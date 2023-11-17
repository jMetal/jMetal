package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

public class AngleCalculator {
  /**
   * Angle for each dimension of PS
   *
   * @param j Coordinate of PS
   * @param m Dimension of PS
   * @param n Number of decision variables of the MOP
   * @return Angle value for the specified dimension
   */
  public static double calculateTheta(int j, int m, int n) {
    double theta;
    assertValidRange(j, n, m);
    theta = 2.0 * Math.PI * (j - 1.0) / (n - m);
    return theta;
  }

  /**
   * Asserts that the value of j is within the valid range.
   *
   * @param j Coordinate of PS
   * @param n Number of decision variables of the MOP
   * @param m Dimension of PS
   */
  private static void assertValidRange(int j, int n, int m) {
    if (!(1 <= j && j <= n - m)) {
      throw new IllegalArgumentException("Invalid value for j. It should be in the range [1, n - m]");
    }
  }
}
