package org.uma.jmetal.problem.multiobjective.zcat.util;

import java.util.Random;

public class ZCatUtils {

  private static final double DBL_EPSILON = Math.ulp(1.0);
  private static final Random rand = new Random();

  /**
   * Fix the value 'a' to be between 0 and 1 (numerical precision issues)
   * @param a: The value to fix
   * @return: The fixed value
   */
  public static double zcatFixTo01(double a) {
    double epsilon = DBL_EPSILON;
    double min = 0.0;
    double max = 1.0;

    double minEpsilon = min - epsilon;
    double maxEpsilon = max + epsilon;

    if (a <= min && a >= minEpsilon) {
      return min;
    } else if (a >= max && a <= maxEpsilon) {
      return max;
    } else {
      return a;
    }
  }

  /**
   * For precision issues.
   * This function verifies whether y <= z
   */
  public static int zcatLq(double y, double z) {
    if (y < z || Math.abs(z - y) < DBL_EPSILON) {
      return 1;
    }
    return 0;
  }

  /**
   * For precision issues.
   * This function verifies whether y == z
   */
  public static int zcatEq(double y, double z) {
    if (Math.abs(z - y) < DBL_EPSILON) {
      return 1;
    }
    return 0;
  }

  /**
   * Verify if value 'y' is into 'lb' and 'ub': lb <= y <= ub
   * @param y:  A given value
   * @param lb: Low bound
   * @param ub: Up bound
   * @return: 1: lb <= y <= ub; 0: otherwise
   */
  public static boolean zcatValueIn(double y, double lb, double ub) {
    return (zcatLq(lb, y) == 1 && zcatLq(y, ub) == 1) ? true : false;
  }

  /**
   * Verify if all the values 'yi' are into 'lb' and 'ub': lb <= yi <= ub (i=0,..,m-1)
   * @param y:  A given array
   * @param m:  The size of y
   * @param lb: Low bound
   * @param ub: Up bound
   * @return: 1: lb <= yi <= ub (for all i=1,..,m-1); 0: otherwise
   */
  public static int zcatForAllValueIn(double[] y, int m, double lb, double ub) {
    for (int i = 0; i < m; i++) {
      if (!zcatValueIn(y[i], lb, ub)) {
        return 0;
      }
    }
    return 1;
  }

  /**
   * Display double array
   * @param v:    The array
   * @param size: The size of the array
   */
  public static void printDoubleArray(double[] v, int size) {
    for (int i = 0; i < size; i++) {
      System.out.print(v[i] + " ");
    }
    System.out.println();
  }

  /**
   * Real number between lb and ub (it includes the bounds)
   * @param lb: The low bound
   * @param ub: The up bound
   * @return: The random real number
   */
  public static double rndReal(double lb, double ub) {
    assert lb < ub;
    double rnd = rand.nextDouble();
    rnd = rnd * (ub - lb) + lb;
    assert lb <= rnd && rnd <= ub;
    return rnd;
  }

  /**
   * Random percentage between 0 and 1
   * @return: The random percentage
   */
  public static double rndPerc() {
    return rndReal(0.0, 1.0);
  }

  /**
   * Integer number between lb and ub (it includes the bounds)
   * @param lb: The low bound
   * @param ub: The up bound
   * @return: The random integer number
   */
  public static int rndInt(int lb, int ub) {
    assert lb <= ub;
    int r = lb + rand.nextInt(ub - lb + 1);
    assert lb <= r && r <= ub;
    return r;
  }
}
