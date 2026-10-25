package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Fitness of CMOEA-MS: the constraint violation ({@code CalCV} in PlatEMO), the shift-based
 * density estimation ({@code CalSDE.m}) and the SPEA2-like fitness ({@code CalFitness.m}).
 *
 * <p>The methods work on matrices whose rows are solutions, and replicate PlatEMO's arithmetic,
 * including its divisions by zero: a NaN produced by them propagates as in MATLAB, and sorting
 * places it after every number, as MATLAB's {@code sort} does.
 */
final class CMOEAMSFitness {
  private CMOEAMSFitness() {}

  /**
   * Computes the constraint violation of each solution as {@code CalCV} of PlatEMO's CMOEA-MS:
   * the violation of each constraint, {@code max(0, -c)} for a jMetal constraint value {@code c}
   * (which is satisfied when it is not negative), is divided by its maximum over the solutions,
   * and the results are averaged over the constraints. The constraints whose maximum violation is
   * zero (PlatEMO detects them by a NaN in the first row) contribute zero. Without
   * constraints, the violation is zero, as in PlatEMO, whose problems without constraints have a
   * column of zeros.
   *
   * @param constraints the constraint values of the solutions (one row per solution)
   * @return the constraint violation of each solution, in [0, 1]
   */
  static double[] constraintViolation(double[][] constraints) {
    Check.notNull(constraints, "constraints");
    int numberOfSolutions = constraints.length;
    int numberOfConstraints = numberOfSolutions == 0 ? 0 : constraints[0].length;
    double[] result = new double[numberOfSolutions];
    if (numberOfConstraints == 0) {
      return result;
    }

    double[][] violation = new double[numberOfSolutions][numberOfConstraints];
    for (int i = 0; i < numberOfSolutions; i++) {
      for (int j = 0; j < numberOfConstraints; j++) {
        double value = -constraints[i][j];
        // max(g, 0) of MATLAB ignores a NaN, so a NaN value counts as no violation
        violation[i][j] = value > 0.0 ? value : 0.0;
      }
    }

    for (int j = 0; j < numberOfConstraints; j++) {
      double maximum = 0.0;
      for (int i = 0; i < numberOfSolutions; i++) {
        maximum = Math.max(maximum, violation[i][j]);
      }
      for (int i = 0; i < numberOfSolutions; i++) {
        violation[i][j] = violation[i][j] / maximum;
      }
      if (Double.isNaN(violation[0][j])) {
        for (int i = 0; i < numberOfSolutions; i++) {
          violation[i][j] = 0.0;
        }
      }
    }

    for (int i = 0; i < numberOfSolutions; i++) {
      double sum = 0.0;
      for (int j = 0; j < numberOfConstraints; j++) {
        sum += violation[i][j];
      }
      result[i] = sum / numberOfConstraints;
    }

    return result;
  }

  /**
   * Computes the shift-based density estimation of each solution as {@code CalSDE.m}: the
   * objectives are normalized to [0, 1] with their minimum and maximum over the solutions, and the
   * value of solution {@code i} is {@code 1 / (Dk + 2)}, where {@code Dk} is the
   * (floor(sqrt(N)) + 1)-th smallest Euclidean distance between solution {@code i} and the
   * solutions shifted to it (each objective of another solution that is better than that of
   * {@code i} takes the value of {@code i}), counting the distance of {@code i} to itself. A lower
   * value means a less crowded solution. An objective with the same value in all the solutions
   * gives NaN, as in PlatEMO.
   *
   * @param objectives the objective values of the solutions (one row per solution)
   * @return the density of each solution
   */
  static double[] shiftBasedDensity(double[][] objectives) {
    Check.notNull(objectives, "objectives");
    int numberOfSolutions = objectives.length;
    Check.that(numberOfSolutions >= 2, "The SDE needs at least two solutions");
    int numberOfObjectives = objectives[0].length;

    double[][] normalized = new double[numberOfSolutions][numberOfObjectives];
    for (int j = 0; j < numberOfObjectives; j++) {
      double minimum = Double.NaN;
      double maximum = Double.NaN;
      for (double[] solution : objectives) {
        // min and max of MATLAB ignore NaN values
        minimum = Double.isNaN(minimum) || solution[j] < minimum ? solution[j] : minimum;
        maximum = Double.isNaN(maximum) || solution[j] > maximum ? solution[j] : maximum;
      }
      for (int i = 0; i < numberOfSolutions; i++) {
        normalized[i][j] = (objectives[i][j] - minimum) / (maximum - minimum);
      }
    }

    int neighbour = (int) Math.floor(Math.sqrt(numberOfSolutions));
    double[] result = new double[numberOfSolutions];
    double[] distance = new double[numberOfSolutions];
    for (int i = 0; i < numberOfSolutions; i++) {
      for (int k = 0; k < numberOfSolutions; k++) {
        double sum = 0.0;
        for (int j = 0; j < numberOfObjectives; j++) {
          double shifted =
              normalized[k][j] < normalized[i][j] ? normalized[i][j] : normalized[k][j];
          double difference = normalized[i][j] - shifted;
          sum += difference * difference;
        }
        distance[k] = Math.sqrt(sum);
      }
      Arrays.sort(distance);
      result[i] = 1.0 / (distance[neighbour] + 2.0);
    }

    return result;
  }

  /**
   * Computes the fitness of stage A of CMOEA-MS: the fitness of the two objectives [SDE, CV]
   * with Pareto dominance ({@code CalFitness([CalSDE(objs)', CV])} in PlatEMO).
   *
   * @param objectives the objective values of the solutions (one row per solution)
   * @param constraintViolation the constraint violation of the solutions
   * @return the fitness of each solution
   */
  static double[] stageAFitness(double[][] objectives, double[] constraintViolation) {
    Check.notNull(objectives, "objectives");
    Check.notNull(constraintViolation, "constraintViolation");
    Check.that(
        objectives.length == constraintViolation.length,
        "The objectives and the constraint violation have different sizes");
    double[] density = shiftBasedDensity(objectives);
    double[][] points =
        IntStream.range(0, objectives.length)
            .mapToObj(i -> new double[] {density[i], constraintViolation[i]})
            .toArray(double[][]::new);

    return fitness(points, new double[objectives.length]);
  }

  /**
   * Computes the fitness of stage B of CMOEA-MS: the fitness of the objectives with the
   * constrained dominance ({@code CalFitness(objs, CV)} in PlatEMO).
   *
   * @param objectives the objective values of the solutions (one row per solution)
   * @param constraintViolation the constraint violation of the solutions
   * @return the fitness of each solution
   */
  static double[] stageBFitness(double[][] objectives, double[] constraintViolation) {
    return fitness(objectives, constraintViolation);
  }

  /**
   * Computes the fitness of {@code CalFitness.m}, {@code R + D}. A solution dominates another if
   * its constraint violation is lower or, when they are equal, if it Pareto-dominates it. {@code
   * R} is the sum of the number of solutions dominated by each solution dominating the solution
   * (zero for the non-dominated ones), and {@code D = 1 / (d + 2)}, where {@code d} is the cosine
   * distance to the floor(sqrt(N))-th nearest solution, is in (0, 0.5]; so the non-dominated
   * solutions are those whose fitness is lower than 1.
   *
   * @param points the vectors compared by dominance and by the cosine distance (one row per
   *     solution)
   * @param constraintViolation the constraint violation of the solutions
   * @return the fitness of each solution
   */
  static double[] fitness(double[][] points, double[] constraintViolation) {
    Check.notNull(points, "points");
    Check.notNull(constraintViolation, "constraintViolation");
    int numberOfSolutions = points.length;
    Check.that(numberOfSolutions > 0, "There are no solutions");
    Check.that(
        numberOfSolutions == constraintViolation.length,
        "The points and the constraint violation have different sizes");

    boolean[][] dominates = new boolean[numberOfSolutions][numberOfSolutions];
    for (int i = 0; i < numberOfSolutions - 1; i++) {
      for (int j = i + 1; j < numberOfSolutions; j++) {
        if (constraintViolation[i] < constraintViolation[j]) {
          dominates[i][j] = true;
        } else if (constraintViolation[i] > constraintViolation[j]) {
          dominates[j][i] = true;
        } else {
          int relation = paretoRelation(points[i], points[j]);
          if (relation == 1) {
            dominates[i][j] = true;
          } else if (relation == -1) {
            dominates[j][i] = true;
          }
        }
      }
    }

    int[] strength = new int[numberOfSolutions];
    for (int i = 0; i < numberOfSolutions; i++) {
      for (int j = 0; j < numberOfSolutions; j++) {
        if (dominates[i][j]) {
          strength[i]++;
        }
      }
    }

    double[][] distance = cosineDistances(points);
    int neighbour = (int) Math.floor(Math.sqrt(numberOfSolutions)) - 1;
    double[] result = new double[numberOfSolutions];
    for (int i = 0; i < numberOfSolutions; i++) {
      double raw = 0.0;
      for (int j = 0; j < numberOfSolutions; j++) {
        if (dominates[j][i]) {
          raw += strength[j];
        }
      }
      distance[i][i] = Double.POSITIVE_INFINITY;
      Arrays.sort(distance[i]);
      result[i] = raw + 1.0 / (distance[i][neighbour] + 2.0);
    }

    return result;
  }

  /**
   * Computes the cosine distance between each pair of vectors as MATLAB's {@code
   * pdist2(X, X, 'cosine')}: one minus the cosine of the angle between them, with the cosine
   * limited to 1 against round-off errors. The distance involving a zero vector is NaN.
   *
   * @param points the vectors (one per row)
   * @return the matrix of distances, whose diagonal is zero except for zero vectors
   */
  static double[][] cosineDistances(double[][] points) {
    Check.notNull(points, "points");
    int numberOfPoints = points.length;
    double[][] unit = new double[numberOfPoints][];
    for (int i = 0; i < numberOfPoints; i++) {
      unit[i] = unitVector(points[i]);
    }

    double[][] result = new double[numberOfPoints][numberOfPoints];
    for (int i = 0; i < numberOfPoints; i++) {
      for (int j = 0; j < numberOfPoints; j++) {
        double cosine = 0.0;
        for (int k = 0; k < unit[i].length; k++) {
          cosine += unit[i][k] * unit[j][k];
        }
        if (cosine > 1.0) {
          cosine = 1.0;
        }
        result[i][j] = 1.0 - cosine;
      }
    }

    return result;
  }

  /**
   * Compares two values as MATLAB's ascending {@code sort}: NaN goes after every number and two
   * NaN values are equal, as are -0.0 and 0.0.
   */
  static int compareAscending(double a, double b) {
    if (Double.isNaN(a)) {
      return Double.isNaN(b) ? 0 : 1;
    }
    if (Double.isNaN(b)) {
      return -1;
    }
    return a < b ? -1 : (a > b ? 1 : 0);
  }

  /**
   * Returns the indices that sort the values in ascending order as {@code [~, I] = sort(values)}
   * of MATLAB: the sort is stable and NaN values go last.
   */
  static int[] sortedIndices(double[] values) {
    Comparator<Integer> ascending = (i, j) -> compareAscending(values[i], values[j]);
    return IntStream.range(0, values.length)
        .boxed()
        .sorted(ascending)
        .mapToInt(Integer::intValue)
        .toArray();
  }

  /**
   * Returns 1 if {@code a} Pareto-dominates {@code b}, -1 if {@code b} dominates {@code a}, and 0
   * otherwise, as {@code any(a < b) - any(a > b)} of PlatEMO (a comparison with NaN is false).
   */
  private static int paretoRelation(double[] a, double[] b) {
    boolean better = false;
    boolean worse = false;
    for (int k = 0; k < a.length; k++) {
      better |= a[k] < b[k];
      worse |= a[k] > b[k];
    }
    return (better ? 1 : 0) - (worse ? 1 : 0);
  }

  /**
   * Divides a vector by its norm, which is computed as in MATLAB's {@code pdist2}, scaling the
   * vector by its largest absolute value first (ignoring NaN, as MATLAB's {@code max}). A zero
   * vector, or one with a NaN, gives a vector of NaN.
   */
  private static double[] unitVector(double[] point) {
    double largest = 0.0;
    for (double value : point) {
      if (Math.abs(value) > largest) {
        largest = Math.abs(value);
      }
    }
    double norm = 0.0;
    if (largest > 0.0) {
      double sum = 0.0;
      for (double value : point) {
        double scaled = value / largest;
        sum += scaled * scaled;
      }
      norm = Math.sqrt(sum) * largest;
    }

    double[] result = new double[point.length];
    for (int k = 0; k < point.length; k++) {
      result[k] = point[k] / norm;
    }
    return result;
  }
}
