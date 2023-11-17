package org.uma.jmetal.problem.multiobjective.zcat.util;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatFixTo01;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatForAllValueIn;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatValueIn;

public class ZcatFFunctions {

  /**
   * F1: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F1(double[] F, double[] y, int M) {
    F[0] = 1.0;
    for (int i = 1; i <= M - 1; i++) {
      F[0] *= Math.sin(y[i - 1] * Math.PI / 2.0);
    }
    F[0] = zcatFixTo01(F[0]);
    assert 0 <= F[0] && F[0] <= 1.0;

    for (int j = 2; j <= M - 1; j++) {
      F[j - 1] = 1.0;
      for (int i = 1; i <= M - j; i++) {
        F[j - 1] *= Math.sin(y[i - 1] * Math.PI / 2.0);
      }
      F[j - 1] *= Math.cos(y[M - j + 1 - 1] * Math.PI / 2.0);
      F[j - 1] = zcatFixTo01(F[j - 1]);
      assert 0 <= F[j - 1] && F[j - 1] <= 1.0;
    }

    F[M - 1] = 1.0 - Math.sin(y[0] * Math.PI / 2.0);
    F[M - 1] = zcatFixTo01(F[M - 1]);
    assert 0 <= F[M - 1] && F[M - 1] <= 1.0;
  }


  public static void F2(double[] F, double[] y, int M) {
    double[] temp = new double[M];

    temp[0] = 1.0;
    for (int i = 1; i <= M - 1; ++i) {
      temp[0] *= 1.0 - Math.cos(y[i - 1] * Math.PI / 2);
    }
    assert (0 <= temp[0] && temp[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      temp[j - 1] = 1.0;
      for (int i = 1; i <= M - j; ++i) {
        temp[j - 1] *= 1.0 - Math.cos(y[i - 1] * Math.PI / 2);
      }
      temp[j - 1] *= 1.0 - Math.sin(y[M - j + 1 - 1] * Math.PI / 2);
      assert (0 <= temp[j - 1] && temp[j - 1] <= 1.0);
    }

    temp[M - 1] = 1.0 - Math.sin(y[0] * Math.PI / 2);
    assert (0 <= temp[M - 1] && temp[M - 1] <= 1.0);

    System.arraycopy(temp, 0, F, 0, M);
  }

  /**
   * F3: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F3(double[] F, double[] y, int M) {
    double[] temp = new double[M];

    temp[0] = 0.0;
    for (int i = 1; i <= M - 1; ++i) {
      temp[0] += y[i - 1];
    }
    temp[0] = temp[0] / (M - 1);
    assert (0 <= temp[0] && temp[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      temp[j - 1] = 0.0;
      for (int i = 1; i <= M - j; ++i) {
        temp[j - 1] += y[i - 1];
      }
      temp[j - 1] += (1 - y[M - j + 1 - 1]);
      temp[j - 1] = temp[j - 1] / (M - j + 1);
      assert (0 <= temp[j - 1] && temp[j - 1] <= 1.0);
    }

    temp[M - 1] = 1 - y[0];
    assert (0 <= temp[M - 1] && temp[M - 1] <= 1.0);

    System.arraycopy(temp, 0, F, 0, M);
  }

  /**
   * F4: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F4(double[] F, double[] y, int M) {
    for (int j = 1; j <= M - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    double sum = 0.0;
    for (int i = 1; i <= M - 1; ++i) {
      sum += y[i - 1];
    }

    F[M - 1] = 1.0 - sum / (M - 1);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F5: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F5(double[] F, double[] y, int M) {
    for (int j = 1; j <= M - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    double sum = 0.0;
    for (int i = 1; i <= M - 1; ++i) {
      sum += 1.0 - y[i - 1];
    }

    F[M - 1] = (Math.pow(Math.exp(sum / (M - 1)), 8.0) - 1.0) / (Math.pow(Math.exp(1), 8.0) - 1.0);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F6: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F6(double[] F, double[] y, int M) {
    double mu = 0.0;
    double k = 40.0;
    double r = 0.05;

    for (int j = 1; j <= M - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    for (int i = 1; i <= M - 1; ++i) {
      mu += y[i - 1];
    }
    mu = mu / (M - 1);

    F[M - 1] = (Math.pow(1 + Math.exp(2 * k * mu - k), -1.0) - r * mu
        - Math.pow(1 + Math.exp(k), -1.0) + r)
        / (Math.pow(1 + Math.exp(-k), -1.0) - Math.pow(1 + Math.exp(k), -1.0) + r);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }


  /**
   * F7: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F7(double[] F, double[] y, int M) {
    double sum = 0.0;

    for (int j = 1; j <= M - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    for (int i = 1; i <= M - 1; ++i) {
      sum += Math.pow(0.5 - y[i - 1], 5);
    }
    sum = sum / (2 * (M - 1) * Math.pow(0.5, 5));

    F[M - 1] = sum + 0.5;
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F8: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F8(double[] F, double[] y, int M) {
    F[0] = 1.0;
    for (int i = 1; i <= M - 1; ++i) {
      F[0] *= 1.0 - Math.sin(y[i - 1] * Math.PI / 2);
    }
    F[0] = 1.0 - F[0];
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      F[j - 1] = 1.0;
      for (int i = 1; i <= M - j; ++i) {
        F[j - 1] *= 1.0 - Math.sin(y[i - 1] * Math.PI / 2);
      }
      F[j - 1] *= 1.0 - Math.cos(y[M - j + 1 - 1] * Math.PI / 2);
      F[j - 1] = 1.0 - F[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] = Math.cos(y[0] * Math.PI / 2);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F9: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F9(double[] F, double[] y, int M) {
    F[0] = 0.0;
    for (int i = 1; i <= M - 1; ++i) {
      F[0] += Math.sin(y[i - 1] * Math.PI / 2);
    }
    F[0] = F[0] / (M - 1);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      F[j - 1] = 0.0;
      for (int i = 1; i <= M - j; ++i) {
        F[j - 1] += Math.sin(y[i - 1] * Math.PI / 2);
      }
      F[j - 1] += Math.cos(y[M - j + 1 - 1] * Math.PI / 2);
      F[j - 1] = F[j - 1] / (M - j + 1);
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] = Math.cos(y[0] * Math.PI / 2);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F10: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F10(double[] F, double[] y, int M) {
    double sum = 0.0;
    double r = 0.02;

    for (int j = 1; j <= M - 1; ++j) {
      sum += 1 - y[j - 1];
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] = (Math.pow(r, -1) - Math.pow(sum / (M - 1) + r, -1))
        / (Math.pow(r, -1) - Math.pow(1 + r, -1));
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F11: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F11(double[] F, double[] y, int M) {
    double k = 4.0;

    F[0] = 0.0;
    for (int i = 1; i <= M - 1; ++i) {
      F[0] += y[i - 1];
    }
    F[0] = F[0] / (M - 1);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      F[j - 1] = 0.0;
      for (int i = 1; i <= M - j; ++i) {
        F[j - 1] += y[i - 1];
      }
      F[j - 1] += (1 - y[M - j + 1 - 1]);
      F[j - 1] = F[j - 1] / (M - j + 1);
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] = (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0])
        - 1) / (4 * k);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  /**
   * F12: Shape of the PF (as in the paper)
   *
   * @param F: The shape of the PF
   * @param y: The first 'm' normalized decision variables
   * @param M: The number of objectives
   */
  public static void F12(double[] F, double[] y, int M) {
    double k = 3.0;

    F[0] = 1.0;
    for (int i = 1; i <= M - 1; ++i) {
      F[0] *= (1.0 - y[i - 1]);
    }
    F[0] = 1.0 - F[0];
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= M - 1; ++j) {
      F[j - 1] = 1.0;
      for (int i = 1; i <= M - j; ++i) {
        F[j - 1] *= (1.0 - y[i - 1]);
      }
      F[j - 1] *= y[M - j + 1 - 1];
      F[j - 1] = 1.0 - F[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] = (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0])
        - 1) / (4 * k);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F13(double[] F, double[] y, int M) {
    int i, j;
    double k = 3.0;

    F[0] = 0.0;
    for (i = 1; i <= M - 1; ++i) {
      F[0] += Math.sin(y[i - 1] * Math.PI / 2);
    }
    F[0] = 1.0 - F[0] / (M - 1.0);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (j = 2; j <= M - 1; ++j) {
      F[j - 1] = 0.0;
      for (i = 1; i <= M - j; ++i) {
        F[j - 1] += Math.sin(y[i - 1] * Math.PI / 2);
      }
      F[j - 1] += Math.cos(y[M - j + 1 - 1] * Math.PI / 2);
      F[j - 1] = 1.0 - F[j - 1] / (M - j + 1);
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] =
        1.0 - (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0])) / (4.0 * k);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F14(double[] F, double[] y, int M) {
    int j;

    F[0] = Math.pow(Math.sin(y[0] * Math.PI / 2), 2.0);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (j = 2; j <= M - 2; ++j) {
      F[j - 1] = Math.pow(Math.sin(y[0] * Math.PI / 2), 2.0 + (j - 1.0) / (M - 2.0));
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (M > 2) {
      F[M - 2] = 0.5 * (1 + Math.sin(6 * y[0] * Math.PI / 2 - Math.PI / 2));
      assert (0 <= F[M - 2] && F[M - 2] <= 1.0);
    }

    F[M - 1] = Math.cos(y[0] * Math.PI / 2);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F15(double[] F, double[] y, int M) {
    int j;
    double k = 3.0;

    for (j = 1; j <= M - 1; ++j) {
      F[j - 1] = Math.pow(y[0], 1.0 + (j - 1.0) / (4.0 * M));
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[M - 1] =
        (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0]) - 1) / (4 * k);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F16(double[] F, double[] y, int M) {
    int j;
    double k = 5.0;

    F[0] = Math.sin(y[0] * Math.PI / 2);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (j = 2; j <= M - 2; ++j) {
      F[j - 1] = Math.pow(Math.sin(y[0] * Math.PI / 2), 1.0 + (j - 1.0) / (M - 2.0));
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (M > 2) {
      F[M - 2] = 0.5 * (1 + Math.sin(10 * y[0] * Math.PI / 2 - Math.PI / 2));
      assert (0 <= F[M - 2] && F[M - 2] <= 1.0);
    }

    F[M - 1] =
        (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0]) - 1) / (4 * k);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F17(double[] F, double[] y, int M) {
    int j;
    boolean wedge_flag;
    double sum;

    wedge_flag = true;
    for (j = 0; j <= M - 2; ++j) {
      if (y[j] < 0.0 || y[j] > 0.5) {
        wedge_flag = false;
        break;
      }
    }

    sum = 0.0;
    for (j = 1; j <= M - 1; ++j) {
      if (wedge_flag) {
        F[j - 1] = y[0];
      } else {
        F[j - 1] = y[j - 1];
        sum += 1 - y[j - 1];
      }
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (wedge_flag) {
      F[M - 1] = (Math.pow(Math.exp(1.0 - y[0]), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
    } else {
      F[M - 1] =
          (Math.pow(Math.exp(sum / (M - 1)), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
    }
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F18(double[] F, double[] y, int M) {
    int j;
    boolean f1, f2;
    boolean wedge_flag;
    double sum;

    f1 = zcatForAllValueIn(y, M - 1, 0.0, 0.4);
    f2 = zcatForAllValueIn(y, M - 1, 0.6, 1.0);
    wedge_flag = (f1 || f2) ? true : false;

    sum = 0.0;
    for (j = 1; j <= M - 1; ++j) {
      if (wedge_flag) {
        F[j - 1] = y[0];
      } else {
        F[j - 1] = y[j - 1];
        sum += Math.pow(0.5 - y[j - 1], 5.0);
      }
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (wedge_flag) {
      F[M - 1] = (Math.pow(0.5 - y[0], 5.0) + Math.pow(0.5, 5.0)) / (2.0 * Math.pow(0.5, 5.0));
    } else {
      F[M - 1] = sum / (2.0 * (M - 1.0) * Math.pow(0.5, 5.0)) + 0.5;
    }
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F19(double[] F, double[] y, int M) {
    int j;
    double A = 5.0;
    double mu;

    int flag_deg;

    flag_deg = (zcatValueIn(y[0], 0.0, 0.2) || zcatValueIn(y[0], 0.4, 0.6)) ? 1 : 0;
    mu = 0.0;
    for (j = 1; j <= M - 1; ++j) {
      mu += y[j - 1];
      F[j - 1] = (flag_deg == 1) ? y[0] : y[j - 1];
      F[j - 1] = zcatFixTo01(F[j - 1]);
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }
    mu = (flag_deg == 1) ? y[0] : mu / (M - 1);

    F[M - 1] = (1.0 - mu - Math.cos(2.0 * A * Math.PI * mu + Math.PI / 2.0) / (2.0 * A * Math.PI));
    F[M - 1] = zcatFixTo01(F[M - 1]);
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }

  public static void F20(double[] F, double[] y, int M) {
    int j;
    int deg_flag;
    double sum;

    deg_flag = (zcatValueIn(y[0], 0.1, 0.4) || zcatValueIn(y[0], 0.6, 0.9)) ? 1 : 0;

    sum = 0.0;
    for (j = 1; j <= M - 1; ++j) {
      sum += Math.pow(0.5 - y[j - 1], 5.0);
      F[j - 1] = (deg_flag == 1) ? y[0] : y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (deg_flag == 1) {
      F[M - 1] = (Math.pow(0.5 - y[0], 5.0) + Math.pow(0.5, 5.0)) / (2.0 * Math.pow(0.5, 5.0));
    } else {
      F[M - 1] = sum / (2.0 * (M - 1.0) * Math.pow(0.5, 5.0)) + 0.5;
    }
    assert (0 <= F[M - 1] && F[M - 1] <= 1.0);
  }
}
