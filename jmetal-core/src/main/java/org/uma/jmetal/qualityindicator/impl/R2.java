package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the R2 quality indicator.
 *
 * <p>The R2 indicator measures the quality of a Pareto front approximation using a set of uniformly
 * distributed weight vectors and a utility function (typically Tchebycheff).
 *
 * <p>References:
 * <ul>
 *   <li>Hansen, M.P., Jaszkiewicz, A.: Evaluating the quality of approximations to the
 *       non-dominated set. IMM Technical Report IMM-REP-1998-7 (1998)</li>
 *   <li>Brockhoff, D., Wagner, T., Trautmann, H.: On the properties of the R2 indicator.
 *       GECCO 2012, pp. 465-472</li>
 * </ul>
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class R2 extends QualityIndicator {

  private double[][] weightVectors;
  private double[] idealPoint;
  private UtilityFunction utilityFunction;

  /** Utility functions supported by the R2 indicator */
  public enum UtilityFunction {
    TCHEBYCHEFF,
    WEIGHTED_SUM
  }

  /** Default constructor */
  public R2() {
    this.utilityFunction = UtilityFunction.TCHEBYCHEFF;
  }

  /**
   * Constructor with reference front. Generates weight vectors automatically.
   *
   * @param referenceFront The reference Pareto front
   */
  public R2(double[][] referenceFront) {
    super(referenceFront);
    Check.that(referenceFront.length > 0, "Reference front cannot be empty");

    int numberOfObjectives = referenceFront[0].length;
    int numberOfVectors = calculateDefaultNumberOfVectors(numberOfObjectives);
    this.weightVectors = generateUniformWeightVectors(numberOfObjectives, numberOfVectors);
    this.idealPoint = computeIdealPoint(referenceFront);
    this.utilityFunction = UtilityFunction.TCHEBYCHEFF;
  }

  /**
   * Constructor with reference front and custom number of weight vectors.
   *
   * @param referenceFront The reference Pareto front
   * @param numberOfVectors Number of weight vectors to generate
   */
  public R2(double[][] referenceFront, int numberOfVectors) {
    super(referenceFront);
    Check.that(referenceFront.length > 0, "Reference front cannot be empty");
    Check.that(numberOfVectors > 0, "Number of vectors must be positive");

    int numberOfObjectives = referenceFront[0].length;
    this.weightVectors = generateUniformWeightVectors(numberOfObjectives, numberOfVectors);
    this.idealPoint = computeIdealPoint(referenceFront);
    this.utilityFunction = UtilityFunction.TCHEBYCHEFF;
  }

  /**
   * Constructor with reference front, custom weight vectors, and utility function.
   *
   * @param referenceFront The reference Pareto front
   * @param weightVectors Custom weight vectors
   * @param utilityFunction The utility function to use
   */
  public R2(double[][] referenceFront, double[][] weightVectors, UtilityFunction utilityFunction) {
    super(referenceFront);
    Check.notNull(weightVectors);
    Check.that(weightVectors.length > 0, "Weight vectors cannot be empty");

    this.weightVectors = weightVectors;
    this.idealPoint = computeIdealPoint(referenceFront);
    this.utilityFunction = utilityFunction;
  }

  @Override
  public QualityIndicator newInstance() {
    return new R2();
  }

  /**
   * Computes the R2 indicator value for a given front.
   *
   * @param front The front to evaluate
   * @return The R2 indicator value
   */
  @Override
  public double compute(double[][] front) {
    Check.notNull(front);
    Check.that(front.length > 0, "Front cannot be empty");

    if (weightVectors == null) {
      int numberOfObjectives = front[0].length;
      int numberOfVectors = calculateDefaultNumberOfVectors(numberOfObjectives);
      weightVectors = generateUniformWeightVectors(numberOfObjectives, numberOfVectors);
    }

    if (idealPoint == null && referenceFront != null) {
      idealPoint = computeIdealPoint(referenceFront);
    } else if (idealPoint == null) {
      idealPoint = computeIdealPoint(front);
    }

    return computeR2(front);
  }

  /**
   * Computes the R2 indicator value.
   *
   * @param front The front to evaluate
   * @return The R2 indicator value
   */
  private double computeR2(double[][] front) {
    double sum = 0.0;

    for (double[] weightVector : weightVectors) {
      double minUtility = Double.POSITIVE_INFINITY;

      for (double[] point : front) {
        double utility = computeUtility(point, weightVector);
        minUtility = Math.min(minUtility, utility);
      }

      sum += minUtility;
    }

    return sum / weightVectors.length;
  }

  /**
   * Computes the utility value of a point for a given weight vector.
   *
   * @param point The objective values of a solution
   * @param weightVector The weight vector
   * @return The utility value
   */
  private double computeUtility(double[] point, double[] weightVector) {
    return switch (utilityFunction) {
      case TCHEBYCHEFF -> computeTchebycheffUtility(point, weightVector);
      case WEIGHTED_SUM -> computeWeightedSumUtility(point, weightVector);
    };
  }

  /**
   * Computes the Tchebycheff utility function value.
   *
   * @param point The objective values
   * @param weightVector The weight vector
   * @return The Tchebycheff utility value (max of weighted distances to ideal point)
   */
  private double computeTchebycheffUtility(double[] point, double[] weightVector) {
    double maxValue = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < point.length; i++) {
      double weight = Math.max(weightVector[i], 1e-10); // Avoid division by zero
      double value = weight * Math.abs(point[i] - idealPoint[i]);
      maxValue = Math.max(maxValue, value);
    }

    return maxValue;
  }

  /**
   * Computes the weighted sum utility function value.
   *
   * @param point The objective values
   * @param weightVector The weight vector
   * @return The weighted sum utility value
   */
  private double computeWeightedSumUtility(double[] point, double[] weightVector) {
    double sum = 0.0;

    for (int i = 0; i < point.length; i++) {
      sum += weightVector[i] * point[i];
    }

    return sum;
  }

  /**
   * Computes the ideal point from a front (minimum value for each objective).
   *
   * @param front The front
   * @return The ideal point
   */
  private double[] computeIdealPoint(double[][] front) {
    int numberOfObjectives = front[0].length;
    double[] ideal = new double[numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      ideal[i] = Double.POSITIVE_INFINITY;
      for (double[] point : front) {
        ideal[i] = Math.min(ideal[i], point[i]);
      }
    }

    return ideal;
  }

  /**
   * Generates uniformly distributed weight vectors using the simplex-lattice design.
   *
   * @param numberOfObjectives Number of objectives
   * @param numberOfVectors Approximate number of vectors to generate
   * @return Array of weight vectors
   */
  private double[][] generateUniformWeightVectors(int numberOfObjectives, int numberOfVectors) {
    // Calculate H (number of divisions) based on desired number of vectors
    // For simplex-lattice: C(H + m - 1, m - 1) ≈ numberOfVectors
    int H = calculateH(numberOfObjectives, numberOfVectors);

    // Generate vectors using simplex-lattice design
    java.util.List<double[]> vectors = new java.util.ArrayList<>();
    generateWeightVectorsRecursive(vectors, new double[numberOfObjectives], 0, H, numberOfObjectives);

    return vectors.toArray(new double[0][]);
  }

  /**
   * Recursively generates weight vectors for the simplex-lattice design.
   */
  private void generateWeightVectorsRecursive(
      java.util.List<double[]> vectors, double[] current, int index, int H, int m) {
    if (index == m - 1) {
      double sum = 0.0;
      for (int i = 0; i < index; i++) {
        sum += current[i];
      }
      current[index] = 1.0 - sum;
      if (current[index] >= -1e-10) { // Allow small numerical errors
        current[index] = Math.max(0.0, current[index]);
        vectors.add(current.clone());
      }
      return;
    }

    double sum = 0.0;
    for (int i = 0; i < index; i++) {
      sum += current[i];
    }

    for (int i = 0; i <= H; i++) {
      current[index] = (double) i / H;
      if (sum + current[index] <= 1.0 + 1e-10) {
        generateWeightVectorsRecursive(vectors, current, index + 1, H, m);
      }
    }
  }

  /**
   * Calculates H (number of divisions) to achieve approximately the desired number of vectors.
   */
  private int calculateH(int m, int targetVectors) {
    int H = 1;
    while (binomial(H + m - 1, m - 1) < targetVectors) {
      H++;
    }
    return H;
  }

  /**
   * Calculates binomial coefficient C(n, k).
   */
  private long binomial(int n, int k) {
    if (k > n - k) {
      k = n - k;
    }
    long result = 1;
    for (int i = 0; i < k; i++) {
      result = result * (n - i) / (i + 1);
    }
    return result;
  }

  /**
   * Calculates a default number of weight vectors based on the number of objectives.
   */
  private int calculateDefaultNumberOfVectors(int numberOfObjectives) {
    return switch (numberOfObjectives) {
      case 2 -> 100;
      case 3 -> 105; // H=13 gives 105 vectors
      case 4 -> 126; // H=8 gives 165 vectors
      case 5 -> 126; // H=6 gives 126 vectors
      default -> Math.max(50, numberOfObjectives * 20);
    };
  }

  /** Sets custom weight vectors */
  public void setWeightVectors(double[][] weightVectors) {
    this.weightVectors = weightVectors;
  }

  /** Gets the weight vectors */
  public double[][] getWeightVectors() {
    return weightVectors;
  }

  /** Sets the ideal point */
  public void setIdealPoint(double[] idealPoint) {
    this.idealPoint = idealPoint;
  }

  /** Gets the ideal point */
  public double[] getIdealPoint() {
    return idealPoint;
  }

  /** Sets the utility function */
  public void setUtilityFunction(UtilityFunction utilityFunction) {
    this.utilityFunction = utilityFunction;
  }

  /** Gets the utility function */
  public UtilityFunction getUtilityFunction() {
    return utilityFunction;
  }

  @Override
  public String name() {
    return "R2";
  }

  @Override
  public String description() {
    return "R2 Quality Indicator";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }
}
