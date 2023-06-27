package org.uma.jmetal.problem.multiobjective.zcat.util;

public class ZCatGFunctions {

  /**
   * Angle for each dimension of PS
   *
   * @param j: Coordinate of PS
   * @param m: Dimension of PS
   * @param n: Number of decision variables of the MOP
   * @return
   */
  public static double Thetaj(int j, int m, int n) {
    assert (1 <= j && j <= n - m);
    double Tj = 2.0 * Math.PI * (j - 1.0) / (n - m);
    return Tj;
  }

  /**
   * g0: Shape of the PS (exactly as DTLZx, WFGx, i.e., it is a piecewise linear PS)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return
   */
  public static double[] g0(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      g[j - 1] = 0.2210;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g1: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return
   */
  public static double[] g1(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.sin(1.5 * Math.PI * y[i - 1] + Thetaj(j, m, n));
      }
      g[j - 1] = sum / (2.0 * m) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g2: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g2(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.pow(y[i - 1], 2.0) * Math.sin(4.5 * Math.PI * y[i - 1] + Thetaj(j, m, n));
      }
      g[j - 1] = sum / (2.0 * m) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g3: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g3(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.pow(Math.cos(Math.PI * y[i - 1] + Thetaj(j, m, n)), 2.0);
      }
      g[j - 1] = sum / m;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g4: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g4(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double mu = 0.0;
      for (int i = 1; i <= m; ++i) {
        mu += y[i - 1];
      }
      mu = mu / m;
      g[j - 1] = (mu / 2.0) * Math.cos(4.0 * Math.PI * mu + Thetaj(j, m, n)) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g5: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g5(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.pow(Math.sin(2.0 * Math.PI * y[i - 1] - 1 + Thetaj(j, m, n)), 3.0);
      }
      g[j - 1] = sum / (2.0 * m) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g6: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g6(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double s1 = 0.0;
      double s2 = 0.0;
      for (int i = 1; i <= m; ++i) {
        s1 += Math.pow(y[i - 1], 2.0);
        s2 += Math.pow(Math.cos(11.0 * Math.PI * y[i - 1] + Thetaj(j, m, n)), 3.0);
      }
      s1 /= m;
      s2 /= m;
      g[j - 1] =
          (-10.0 * Math.exp((-2.0 / 5.0) * Math.sqrt(s1)) - Math.exp(s2) + 10.0 + Math.exp(1.0))
              / (-10.0 * Math.exp(-2.0 / 5.0) - Math.pow(Math.exp(1.0), -1) + 10.0 + Math.exp(1.0));
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g7: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g7(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double mu = 0.0;
      for (int i = 1; i <= m; ++i) {
        mu += y[i - 1];
      }
      mu /= m;
      g[j - 1] =
          (mu + Math.exp(Math.sin(7.0 * Math.PI * mu - Math.PI / 2.0 + Thetaj(j, m, n))) - Math.exp(
              -1.0))
              / (1.0 + Math.exp(1) - Math.exp(-1));
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g8: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g8(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.abs(Math.sin(2.5 * Math.PI * (y[i - 1] - 0.5) + Thetaj(j, m, n)));
      }
      g[j - 1] = sum / m;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g9: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g9(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      double mu = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.abs(Math.sin(2.5 * Math.PI * y[i - 1] - Math.PI / 2.0 + Thetaj(j, m, n)));
        mu += y[i - 1];
      }
      mu /= m;
      g[j - 1] = mu / 2.0 - sum / (2.0 * m) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }

  /**
   * g10: Shape of the PS (as in the paper)
   *
   * @param y: The first 'm' normalized decision variables
   * @param m: The dimension of PS
   * @param n: The number of decision variables
   * @return g array
   */
  public static double[] g10(double[] y, int m, int n) {
    double[] g = new double[n - m];
    for (int j = 1; j <= n - m; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= m; ++i) {
        sum += Math.sin((4.0 * y[i - 1] - 2.0) * Math.PI + Thetaj(j, m, n));
      }
      g[j - 1] = Math.pow(sum, 3.0) / (2.0 * Math.pow(m, 3.0)) + 0.5;
      assert (0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }
}