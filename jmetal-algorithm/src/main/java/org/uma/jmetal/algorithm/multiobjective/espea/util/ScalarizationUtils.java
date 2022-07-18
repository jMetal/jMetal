package org.uma.jmetal.algorithm.multiobjective.espea.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.extremevalues.impl.FrontExtremeValues;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;

/**
 * A class that contains methods for computing the scalarization values of
 * solutions. A scalarization value is an aggregation of the objective values
 * that maps to the real numbers, e.g. the weighted sum.
 *
 * <p>
 * Scalarization values are stored as {@link ScalarizationValue} in the
 * solutions.
 *
 * @author Marlon Braun
 */
public class ScalarizationUtils {

  /**
   * Sets the scalarization value of a solution. Used for for simplified
   * static access.
   *
   * @param solution           The solution whose scalarization value is set.
   * @param scalarizationValue The scalarization value of the solution.
   */
  private static <S extends Solution<?>> void setScalarizationValue(S solution, double scalarizationValue) {
    solution.attributes().put(new ScalarizationValue<S>().getAttributeIdentifier(), scalarizationValue);
  }

  /**
   * Gets the scalarization value of a solution. Used for for simplified
   * static access.
   *
   * @param solution The solution whose scalarization value is set.
   * @return The scalarization value of the solution.
   */
  private static <S extends Solution<?>> double getScalarizationValue(S solution) {
    return (double) solution.attributes().get(new ScalarizationValue<>().getAttributeIdentifier());
  }

  /**
   * Method for turning a list of doubles to an array.
   *
   * @param list List of doubles
   * @return Array of doubles.
   */
  private static double[] toArray(List<Double> list) {
      double[] values = new double[10];
      int count = 0;
      for (Double aDouble : list) {
          double v = aDouble;
          if (values.length == count) values = Arrays.copyOf(values, count * 2);
          values[count++] = v;
      }
      values = Arrays.copyOfRange(values, 0, count);
      return values;
  }

  /**
   * Computes the ideal point based on a list of solutions
   *
   * @param solutionsList A list of solutions
   * @return The ideal point
   */
  private static <S extends Solution<?>> double[] getIdealValues(List<S> solutionsList) {
    ArrayFront front = new ArrayFront(solutionsList);
    FrontExtremeValues extremeValues = new FrontExtremeValues();
    List<Double> list = extremeValues.findLowestValues(front);
    return toArray(list);
  }

  /**
   * Computes the nadir point based on a list of solutions
   *
   * @param solutionsList A list of solutions
   * @return The nadir point.
   */
  private static <S extends Solution<?>> double[] getNadirValues(List<S> solutionsList) {
    ArrayFront front = new ArrayFront(solutionsList);
    FrontExtremeValues extremeValues = new FrontExtremeValues();
    List<Double> list = extremeValues.findHighestValues(front);
    return toArray(list);
  }

  /**
   * Computes the extreme points on achievement scalarization values
   *
   * @param solutionsList A list of solutions.
   * @return Extreme points of the list of solutions.
   */
  private static <S extends Solution<?>> double[][] getExtremePoints(List<S> solutionsList) {
    // One extreme point for each objective
    double[][] extremePoints = new double[solutionsList.get(0).objectives().length][];

    for (int i = 0; i < extremePoints.length; i++) {
      S extreme = Collections.min(solutionsList, new AchievementScalarizationComparator<S>(i));
      extremePoints[i] = new double[extreme.objectives().length];
      for (int j = 0; j < extremePoints.length; j++) {
        extremePoints[i][j] = extreme.objectives()[j];
      }
    }

    return extremePoints;
  }

  /**
   * Scalarization values is computed by summing objective values.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void sumOfObjectives(List<S> solutionsList) {
    for (S solution : solutionsList) {
      double sum = solution.objectives()[0];
        double result = 0.0;
        double[] array = solution.objectives();
        int bound = solution.objectives().length;
        for (int i = 1; i < bound; i++) {
            double v = array[i];
            result += v;
        }
        sum += result;
      setScalarizationValue(solution, sum);
    }
  }

  /**
   * Objective values are multiplied by weights and summed. Weights should
   * always be positive.
   *
   * @param solutionsList A list of solutions.
   * @param weights       Positive constants by which objectives are summed.
   */
  public static <S extends Solution<?>> void weightedSum(List<S> solutionsList, double[] weights) {
    for (S solution : solutionsList) {
      double sum = weights[0] * solution.objectives()[0];
        double result = 0.0;
        int bound = solution.objectives().length;
        for (int i = 1; i < bound; i++) {
            double v = weights[i] * solution.objectives()[i];
            result += v;
        }
        sum += result;
      setScalarizationValue(solution, sum);
    }

  }

  /**
   * Objective values are multiplied.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void productOfObjectives(List<S> solutionsList) {
    for (S solution : solutionsList) {
      double product = solution.objectives()[0];
        double acc = 1;
        double[] array = solution.objectives();
        int bound = solution.objectives().length;
        for (int i = 1; i < bound; i++) {
            double v = array[i];
            acc = acc * v;
        }
        product *= acc;
      setScalarizationValue(solution, product);
    }
  }

  /**
   * Objectives are exponentiated by a positive weight and afterwards
   * multiplied.
   *
   * @param solutionsList A list of solutions.
   * @param weights       Weights by objectives are exponentiated
   */
  public static <S extends Solution<?>> void weightedProduct(List<S> solutionsList, double[] weights) {
    for (S solution : solutionsList) {
      double product = Math.pow(solution.objectives()[0], weights[0]);
        double acc = 1;
        int bound = solution.objectives().length;
        for (int i = 1; i < bound; i++) {
            double pow = Math.pow(solution.objectives()[i], weights[i]);
            acc = acc * pow;
        }
        product *= acc;
      setScalarizationValue(solution, product);
    }
  }

  /**
   * Scalarization values based on the Chebyshev function. The ideal point is
   * computed from the list of solutions.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void chebyshev(List<S> solutionsList) {
    chebyshev(solutionsList, getIdealValues(solutionsList));
  }

  /**
   * Chebyhsev function with weighted objectives.
   *
   * @param solutionsList A list of solutions.
   * @param weights       Constants by which ideal values and objective values are
   *                      multiplied.
   */
  public static <S extends Solution<?>> void weightedChebyshev(List<S> solutionsList, double[] weights) {
    weightedChebyshev(solutionsList, getIdealValues(solutionsList), weights);
  }

  /**
   * Scalarization values based on the Chebyshev function.
   *
   * @param solutionsList A list of solutions.
   * @param idealValues   The ideal point
   */
  public static <S extends Solution<?>> void chebyshev(List<S> solutionsList, double[] idealValues) {
    for (S solution : solutionsList) {
      double max = solution.objectives()[0] - idealValues[0];
      for (int i = 1; i < solution.objectives().length; i++) {
        max = Math.max(max, solution.objectives()[i] - idealValues[i]);
      }
      setScalarizationValue(solution, max);
    }
  }

  /**
   * Scalarization values based on the weighted Chebyshev function.
   *
   * @param solutionsList A list of solutions.
   * @param idealValues   The ideal point.
   * @param weights       Constants by which ideal values and objective values are
   *                      multiplied.
   */
  public static <S extends Solution<?>> void weightedChebyshev(List<S> solutionsList, double[] idealValues, double[] weights) {
    for (S solution : solutionsList) {
      double max = weights[0] * (solution.objectives()[0] - idealValues[0]);
      for (int i = 1; i < solution.objectives().length; i++) {
        max = Math.max(max, weights[i] * (solution.objectives()[i] - idealValues[i]));
      }
      setScalarizationValue(solution, max);
    }
  }

  /**
   * Scalarization values based on the Nash bargaining solution. The
   * disagreement point is computed based on the list of solutions.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void nash(List<S> solutionsList) {
    nash(solutionsList, getNadirValues(solutionsList));
  }

  /**
   * Scalarization values based on the Nash bargaining solution.
   *
   * @param solutionsList A list of solutions.
   * @param nadirValues   The disagreement point.
   */
  public static <S extends Solution<?>> void nash(List<S> solutionsList, double[] nadirValues) {
    for (S solution : solutionsList) {
      double nash = nadirValues[0] - solution.objectives()[0];
        double acc = 1;
        for (int i = 1; i < nadirValues.length; i++) {
            double v = (nadirValues[i] - solution.objectives()[i]);
            acc = acc * v;
        }
        nash *= acc;
      // The Nash bargaining solution is originally maximized. To conform
      // with minimization the bargaining value is negated.
      setScalarizationValue(solution, -nash);
    }
  }

  /**
   * Scalarization values based on angle utility (see Angle-based Preference
   * Models in Multi-objective Optimization by Braun et al.). Extreme points
   * are computed from the list of solutions.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void angleUtility(List<S> solutionsList) {
    angleUtility(solutionsList, getExtremePoints(solutionsList));
  }

  /**
   * Scalarization values based on angle utility (see Angle-based Preference
   * Models in Multi-objective Optimization by Braun et al.).
   *
   * @param solutionsList A list of solutions.
   * @param extremePoints used for angle computation.
   */
  public static <S extends Solution<?>> void angleUtility(List<S> solutionsList, double[][] extremePoints) {
    for (S solution : solutionsList) {
      double fraction = 0.0;
      for (int i = 0; i < extremePoints.length; i++) {
        double numerator = 0.0;
        double denominator = 0.0;
        for (int j = 0; j < extremePoints.length; j++) {
          if (i == j) {
            denominator = Math.abs(extremePoints[i][j] - solution.objectives()[j]);
          } else {
            numerator += Math.pow(extremePoints[i][j] - solution.objectives()[j], 2.0);
          }
        }
        // Avoid numeric instability and division by 0.
        if (denominator > 10e-6) {
          fraction = Math.max(fraction, Math.sqrt(numerator) / denominator);
        } else {
          fraction = Double.MAX_VALUE;
        }
      }
      setScalarizationValue(solution, Math.atan(fraction));
    }
  }

  /**
   * Scalarization values based on tradeoff utility, also known as proper
   * utility (see "Theory and Algorithm for Finding Knees" by Shukla et al.)
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void tradeoffUtility(List<S> solutionsList) {
    // Reset scalarization values from previous iterations
    for (S solution : solutionsList) {
      setScalarizationValue(solution, 0.0);
    }

    for (int cur = 0; cur < solutionsList.size() - 1; cur++) {
      S current = solutionsList.get(cur);
      for (int oth = cur + 1; oth < solutionsList.size(); oth++) {
        S other = solutionsList.get(oth);
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < current.objectives().length; i++) {
          if (current.objectives()[i] > other.objectives()[i]) {
            numerator = Math.max(numerator, current.objectives()[i] - other.objectives()[i]);
          } else if (current.objectives()[i] < other.objectives()[i]) {
            denominator = Math.max(denominator, other.objectives()[i] - current.objectives()[i]);
          }
        }
        // Neither solution Pareto dominates the other
        if (numerator != 0.0 && denominator != 0.0) {
          setScalarizationValue(current, Math.max(getScalarizationValue(current), numerator / denominator));
          // Tradeoff utility is symmetric
          setScalarizationValue(other, Math.max(getScalarizationValue(other), denominator / numerator));
        }
      }
    }
  }

  /**
   * Uniform preferences. Each solution is assigned a scalarization value of
   * 1.0.
   *
   * @param solutionsList A list of solutions.
   */
  public static <S extends Solution<?>> void uniform(List<S> solutionsList) {
    for (S solution : solutionsList) {
      setScalarizationValue(solution, 1.0);
    }
  }
}
