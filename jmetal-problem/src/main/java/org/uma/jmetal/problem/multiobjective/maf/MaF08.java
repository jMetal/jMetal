package org.uma.jmetal.problem.multiobjective.maf;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF08
 */
@SuppressWarnings("serial")
public class MaF08 extends AbstractDoubleProblem {

  public double const8[][];

  /**
   * Default constructor
   */
  public MaF08() {
    this(2, 10);
  }

  /**
   * Creates a MaF03 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF08(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF08");

    double r = 1;
    const8 = polygonpoints(numberOfObjectives, r);

    @NotNull List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (int i = 0; i < numberOfVariables; i++) {
          lower.add(0.0);
          upper.add(1.0);
      }

      setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {

    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double[] f;

      double @NotNull [] result = new double[10];
      int count1 = 0;
      for (Double aDouble : solution.variables()) {
          double v = aDouble;
          if (result.length == count1) result = Arrays.copyOf(result, count1 * 2);
          result[count1++] = v;
      }
      result = Arrays.copyOfRange(result, 0, count1);
      x = result;
    // evaluate f
      double @NotNull [] arr = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfObjectives; i1++) {
          double sqrt = Math.sqrt(Math.pow(const8[i1][0] - x[0], 2) + Math.pow(const8[i1][1] - x[1], 2));
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = sqrt;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      f = arr;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }

  public static double[][] polygonpoints(int m, double r) {
    double[] startp = new double[2];
    startp[0] = 0;
    startp[1] = 1;
    double[] @NotNull [] p1 = new double[m][2];
    double[][] p;
    p1[0] = startp;
    // vertexes with the number of edges(m),start vertex(startp),radius(r)
    for (int i = 1; i < m; i++) {
      p1[i] = nextPoint(2 * Math.PI / m * i, startp, r);
    }
      List<double[]> list = new ArrayList<>();
      for (int i = 0; i < m; i++) {
          double[] doubles = p1[m - i - 1];
          list.add(doubles);
      }
      p = list.toArray(new double[0][]);
    return p;
  }

  public static double[] nextPoint(double arc, double @NotNull [] startp,
      double r) {// arc is radians��evaluation the next vertex with arc and r
    double[] p = new double[2];
    p[0] = startp[0] - r * Math.sin(arc);
    p[1] = r * Math.cos(arc);
    return p;
  }
}
