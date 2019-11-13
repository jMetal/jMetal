package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.util;

import org.uma.jmetal.util.JMetalLogger;

public class CMAESUtils {

  private CMAESUtils () {
  }

  // Symmetric Householder reduction to tridiagonal form, taken from JAMA package.

  public static void tred2(int n, double v[][], double d[], double e[]) {

    //  This is derived from the Algol procedures tred2 by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    System.arraycopy(v[n - 1], 0, d, 0, n);

    // Householder reduction to tridiagonal form.

    for (int i = n - 1; i > 0; i--) {

      // Scale to avoid under/overflow.

      double scale = 0.0;
      double h = 0.0;
      for (int k = 0; k < i; k++) {
        scale = scale + Math.abs(d[k]);
      }
      if (scale == 0.0) {
        e[i] = d[i - 1];
        for (int j = 0; j < i; j++) {
          d[j] = v[i - 1][j];
          v[i][j] = 0.0;
          v[j][i] = 0.0;
        }
      } else {

        h = householderIteration(i, scale, v, d, e);

      }
      d[i] = h;
    }

    // Accumulate transformations.
    accumulateTransformations(n, v, d);

    e[0] = 0.0;
  }

  private static double householderIteration(int index, double scale,
        double[][]v, double d[], double e[]) {

    double h = 0.0;

    // Generate Householder vector.
    for (int k = 0; k < index; k++) {
      d[k] /= scale;
      h += d[k] * d[k];
    }
    double f = d[index - 1];
    double g = Math.sqrt(h);
    if (f > 0) {
      g = -g;
    }
    e[index] = scale * g;
    h = h - f * g;
    d[index - 1] = f - g;
    for (int j = 0; j < index; j++) {
      e[j] = 0.0;
    }

    // Apply similarity transformation to remaining columns.
    for (int j = 0; j < index; j++) {
      f = d[j];
      v[j][index] = f;
      g = e[j] + v[j][j] * f;
      for (int k = j + 1; k <= index - 1; k++) {
        g += v[k][j] * d[k];
        e[k] += v[k][j] * f;
      }
      e[j] = g;
    }
    f = 0.0;
    for (int j = 0; j < index; j++) {
      e[j] /= h;
      f += e[j] * d[j];
    }
    double hh = f / (h + h);
    for (int j = 0; j < index; j++) {
      e[j] -= hh * d[j];
    }
    for (int j = 0; j < index; j++) {
      f = d[j];
      g = e[j];
      for (int k = j; k <= index - 1; k++) {
        v[k][j] -= (f * e[k] + g * d[k]);
      }
      d[j] = v[index - 1][j];
      v[index][j] = 0.0;
    }

    return h;

  }

  private static void accumulateTransformations(int n, double[][]v, double[]d) {

    for (int i = 0; i < n - 1; i++) {
      v[n - 1][i] = v[i][i];
      v[i][i] = 1.0;
      double h = d[i + 1];
      if (h != 0.0) {
        for (int k = 0; k <= i; k++) {
          d[k] = v[k][i + 1] / h;
        }
        for (int j = 0; j <= i; j++) {
          double g = 0.0;
          for (int k = 0; k <= i; k++) {
            g += v[k][i + 1] * v[k][j];
          }
          for (int k = 0; k <= i; k++) {
            v[k][j] -= g * d[k];
          }
        }
      }
      for (int k = 0; k <= i; k++) {
        v[k][i + 1] = 0.0;
      }
    }
    for (int j = 0; j < n; j++) {
      d[j] = v[n - 1][j];
      v[n - 1][j] = 0.0;
    }
    v[n - 1][n - 1] = 1.0;

  }

  // Symmetric tridiagonal QL algorithm, taken from JAMA package.

  public static void tql2(int n, double d[], double e[], double v[][]) {

    //  This is derived from the Algol procedures tql2, by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    System.arraycopy(e, 1, e, 0, n - 1);
    e[n - 1] = 0.0;

    double f = 0.0;
    double tst1 = 0.0;
    double eps = Math.pow(2.0, -52.0);
    for (int l = 0; l < n; l++) {

      // Find small subdiagonal element

      tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
      int m = l;
      while (m < n) {
        if (Math.abs(e[m]) <= eps * tst1) {
          break;
        }
        m++;
      }

      // If m == l, d[l] is an eigenvalue,
      // otherwise, iterate.

      if (m > l) {
        int iter = 0;
        do {
          iter = iter + 1;  // (Could check iteration count here.)

          // Compute implicit shift
          f += specificShift(l, n, d, e);

          // Implicit QL transformation.
          implicitQLTransformation(l, m, n, v, d, e);

          // Check for convergence.

        } while (Math.abs(e[l]) > eps * tst1);
      }
      d[l] = d[l] + f;
      e[l] = 0.0;
    }

    // Sort eigenvalues and corresponding vectors.
    sortEigenValues(n, d, v);

  } // tql2

  private static double specificShift(int idx, int n, double[] d, double[] e) {

    double g = d[idx];
    double p = (d[idx + 1] - g) / (2.0 * e[idx]);
    double r = hypot(p, 1.0);
    if (p < 0) {
      r = -r;
    }
    d[idx] = e[idx] / (p + r);
    d[idx + 1] = e[idx] * (p + r);

    double h = g - d[idx];
    for (int i = idx + 2; i < n; i++) {
      d[i] -= h;
    }
    return h;

  }

  private static void implicitQLTransformation(int l, int m, int n, double v[][],
        double[] d, double[] e) {

    double dl1 = d[l + 1];
    double p = d[m];
    double c = 1.0;
    double c2 = c;
    double c3 = c;
    double el1 = e[l + 1];
    double s = 0.0;
    double s2 = 0.0;
    for (int i = m - 1; i >= l; i--) {
      c3 = c2;
      c2 = c;
      s2 = s;
      double g = c * e[i];
      double h = c * p;
      double r = hypot(p, e[i]);
      e[i + 1] = s * r;
      s = e[i] / r;
      c = p / r;
      p = c * d[i] - s * g;
      d[i + 1] = h + s * (c * g + s * d[i]);

      // Accumulate transformation.

      for (int k = 0; k < n; k++) {
        h = v[k][i + 1];
        v[k][i + 1] = s * v[k][i] + c * h;
        v[k][i] = c * v[k][i] - s * h;
      }
    }
    p = -s * s2 * c3 * el1 * e[l] / dl1;
    e[l] = s * p;
    d[l] = c * p;

  }

  private static void sortEigenValues(int n, double[] d, double[][] v) {

    for (int i = 0; i < n - 1; i++) {
      int k = i;
      double p = d[i];
      for (int j = i + 1; j < n; j++) {
        if (d[j] < p) { // NH find smallest k>i
          k = j;
          p = d[j];
        }
      }
      if (k != i) {
        d[k] = d[i]; // swap k and i
        d[i] = p;
        for (int j = 0; j < n; j++) {
          p = v[j][i];
          v[j][i] = v[j][k];
          v[j][k] = p;
        }
      }
    }

  }

  public static int checkEigenSystem(int n, double c[][], double diag[], double q[][]) {
    /*
     exhaustive org.uma.test of the output of the eigendecomposition
     needs O(n^3) operations

     produces error
     returns number of detected inaccuracies
    */

    /* compute q diag q^T and q q^T to check */
    int i, j, k, res = 0;
    double cc, dd;
    String s;

    for (i = 0; i < n; ++i) {
      for (j = 0; j < n; ++j) {
        for (cc = 0., dd = 0., k = 0; k < n; ++k) {
          cc += diag[k] * q[i][k] * q[j][k];
          dd += q[i][k] * q[j][k];
        }
        /* check here, is the normalization the right one? */
        if (Math.abs(cc - c[biggerValue(i,j)][smallerValue(i,j)])
              / Math.sqrt(c[i][i] * c[j][j]) > 1e-10
            && Math.abs(cc - c[biggerValue(i,j)][smallerValue(i,j)]) > 1e-9) {
          s = " " + i + " " + j + " " + cc
                + " " + c[biggerValue(i,j)][smallerValue(i,j)]
                + " " + (cc - c[biggerValue(i,j)][smallerValue(i,j)]);
          JMetalLogger.logger.severe(
              "CMAESUtils.checkEigenSystem: WARNING - imprecise experiment output detected " + s);
          ++res;
        }
        if (Math.abs(dd - (i == j ? 1 : 0)) > 1e-10) {
          s = i + " " + j + " " + dd;
          JMetalLogger.logger.severe("CMAESUtils.checkEigenSystem():" +
                  " WARNING - imprecise experiment output detected (Q not orthog.) " + s);
          ++res;
        }
      }
    }
    return res;
  }

  public static double norm(double[] vector) {
    double result = 0.0;
    for (int i = 0; i < vector.length; i++) {
      result += vector[i] * vector[i];
    }
    return result;
  }

  /**
   * sqrt(a^2 + b^2) without under/overflow. *
   */
  private static double hypot(double a, double b) {
    double r = 0;
    if (Math.abs(a) > Math.abs(b)) {
      r = b / a;
      r = Math.abs(a) * Math.sqrt(1 + r * r);
    } else if (b != 0) {
      r = a / b;
      r = Math.abs(b) * Math.sqrt(1 + r * r);
    }
    return r;
  }

  /**
   * Returns the bigger value of the two params
   */
  private static int biggerValue(int i, int j) {
    return i > j ? i : j;
  }

  /**
   * Returns the smaller value of the two params
   */
  private static int smallerValue(int i, int j) {
    return i > j ? j : i;
  }

}
