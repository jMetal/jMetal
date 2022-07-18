package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.util;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.util.JMetalLogger;

import java.util.Arrays;
import java.util.stream.IntStream;

public class CMAESUtils {

  private CMAESUtils () {
  }

  // Symmetric Householder reduction to tridiagonal form, taken from JAMA package.

  public static void tred2(int n, @NotNull double v[][], double d[], double e[]) {

    //  This is derived from the Algol procedures tred2 by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    System.arraycopy(v[n - 1], 0, d, 0, n);

    // Householder reduction to tridiagonal form.

    for (var i = n - 1; i > 0; i--) {

      // Scale to avoid under/overflow.

      var h = 0.0;
      var sum = 0.0;
      for (var k = 0; k < i; k++) {
        var abs = Math.abs(d[k]);
        sum += abs;
      }
      var scale = sum;
      if (scale == 0.0) {
        e[i] = d[i - 1];
        for (var j = 0; j < i; j++) {
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

    var h = 0.0;

    // Generate Householder vector.
    for (var k = 0; k < index; k++) {
      d[k] /= scale;
      h += d[k] * d[k];
    }
    var f = d[index - 1];
    var g = Math.sqrt(h);
    if (f > 0) {
      g = -g;
    }
    e[index] = scale * g;
    h = h - f * g;
    d[index - 1] = f - g;
    for (var j = 0; j < index; j++) {
      e[j] = 0.0;
    }

    // Apply similarity transformation to remaining columns.
    for (var j = 0; j < index; j++) {
      f = d[j];
      v[j][index] = f;
      g = e[j] + v[j][j] * f;
      for (var k = j + 1; k <= index - 1; k++) {
        g += v[k][j] * d[k];
        e[k] += v[k][j] * f;
      }
      e[j] = g;
    }
    f = 0.0;
    for (var j = 0; j < index; j++) {
      e[j] /= h;
      f += e[j] * d[j];
    }
    var hh = f / (h + h);
    for (var j = 0; j < index; j++) {
      e[j] -= hh * d[j];
    }
    for (var j = 0; j < index; j++) {
      f = d[j];
      g = e[j];
      for (var k = j; k <= index - 1; k++) {
        v[k][j] -= (f * e[k] + g * d[k]);
      }
      d[j] = v[index - 1][j];
      v[index][j] = 0.0;
    }

    return h;

  }

  private static void accumulateTransformations(int n, double[][]v, double[]d) {

    for (var i = 0; i < n - 1; i++) {
      v[n - 1][i] = v[i][i];
      v[i][i] = 1.0;
      var h = d[i + 1];
      if (h != 0.0) {
        for (var k = 0; k <= i; k++) {
          d[k] = v[k][i + 1] / h;
        }
        for (var j = 0; j <= i; j++) {
          var g = 0.0;
          for (var k = 0; k <= i; k++) {
            g += v[k][i + 1] * v[k][j];
          }
          for (var k = 0; k <= i; k++) {
            v[k][j] -= g * d[k];
          }
        }
      }
      for (var k = 0; k <= i; k++) {
        v[k][i + 1] = 0.0;
      }
    }
    for (var j = 0; j < n; j++) {
      d[j] = v[n - 1][j];
      v[n - 1][j] = 0.0;
    }
    v[n - 1][n - 1] = 1.0;

  }

  // Symmetric tridiagonal QL algorithm, taken from JAMA package.

  public static void tql2(int n, double d[], @NotNull double e[], double v[][]) {

    //  This is derived from the Algol procedures tql2, by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    System.arraycopy(e, 1, e, 0, n - 1);
    e[n - 1] = 0.0;

    var f = 0.0;
    var tst1 = 0.0;
    var eps = Math.pow(2.0, -52.0);
    for (var l = 0; l < n; l++) {

      // Find small subdiagonal element

      tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
      var m = l;
      while (m < n) {
        if (Math.abs(e[m]) <= eps * tst1) {
          break;
        }
        m++;
      }

      // If m == l, d[l] is an eigenvalue,
      // otherwise, iterate.

      if (m > l) {
        var iter = 0;
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

  private static double specificShift(int idx, int n, double @NotNull [] d, double[] e) {

    var g = d[idx];
    var p = (d[idx + 1] - g) / (2.0 * e[idx]);
    var r = hypot(p, 1.0);
    if (p < 0) {
      r = -r;
    }
    d[idx] = e[idx] / (p + r);
    d[idx + 1] = e[idx] * (p + r);

    var h = g - d[idx];
    for (var i = idx + 2; i < n; i++) {
      d[i] -= h;
    }
    return h;

  }

  private static void implicitQLTransformation(int l, int m, int n, double v[][],
                                               double @NotNull [] d, double[] e) {

    var dl1 = d[l + 1];
    var p = d[m];
    var c = 1.0;
    var c2 = c;
    var c3 = c;
    var el1 = e[l + 1];
    var s = 0.0;
    var s2 = 0.0;
    for (var i = m - 1; i >= l; i--) {
      c3 = c2;
      c2 = c;
      s2 = s;
      var g = c * e[i];
      var h = c * p;
      var r = hypot(p, e[i]);
      e[i + 1] = s * r;
      s = e[i] / r;
      c = p / r;
      p = c * d[i] - s * g;
      d[i + 1] = h + s * (c * g + s * d[i]);

      // Accumulate transformation.

      for (var k = 0; k < n; k++) {
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

    for (var i = 0; i < n - 1; i++) {
      var k = i;
      var p = d[i];
      for (var j = i + 1; j < n; j++) {
        if (d[j] < p) { // NH find smallest k>i
          k = j;
          p = d[j];
        }
      }
      if (k != i) {
        d[k] = d[i]; // swap k and i
        d[i] = p;
        for (var j = 0; j < n; j++) {
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
              "CMAESUtils.checkEigenSystem: WARNING - imprecise org.uma.jmetal.experiment output detected " + s);
          ++res;
        }
        if (Math.abs(dd - (i == j ? 1 : 0)) > 1e-10) {
          s = i + " " + j + " " + dd;
          JMetalLogger.logger.severe("CMAESUtils.checkEigenSystem():" +
                  " WARNING - imprecise org.uma.jmetal.experiment output detected (Q not orthog.) " + s);
          ++res;
        }
      }
    }
    return res;
  }

  public static double norm(double[] vector) {
    var result = 0.0;
    for (var v : vector) {
      var v1 = v * v;
      result += v1;
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
