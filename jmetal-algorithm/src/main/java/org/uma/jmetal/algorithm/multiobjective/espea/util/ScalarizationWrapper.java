package org.uma.jmetal.algorithm.multiobjective.espea.util;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * A class for simplifying the access to {@link ScalarizationUtils}.
 *
 * @author Marlon Braun <marlon.braun@partner.kit.edu>
 */
public class ScalarizationWrapper {

  /**
   * The scalarization function that is used for computing values.
   *
   * @author Marlon Braun <marlon.braun@partner.kit.edu>
   */
  public static enum ScalarizationType {
    /**
     * Scalarization values are based on maximum angles to extreme points
     * (see "Angle based Preferences Models in Multi-objective Optimization"
     * by Braun et al.)
     */
    ANGLE_UTILITY,

    /**
     * Chebyhsev scalarization function.
     */
    CHEBYSHEV,

    /**
     * The Nash bargaining solution
     */
    NASH,

    /**
     * Multiplication of all objectives.
     */
    PRODUCT_OF_OBJECTIVES,

    /**
     * Summing up all objectives.
     */
    SUM_OF_OBJECTIVES,

    /**
     * Tradeoff utility also known as proper utility (see "Theory and
     * Algorithms for Finding Knees" by Shukla et al.).
     */
    TRADEOFF_UTILITY,

    /**
     * All solutions are assigned a scalarization value of 1.
     */
    UNIFORM,

    /**
     * Weighted sum.
     */
    WEIGHTED_SUM,

    /**
     * Objectives are exponentiated by weights before being multiplied.
     */
    WEIGHTED_PRODUCT,

    /**
     * Chebyhsev function with weights.
     */
    WEIGHTED_CHEBYSHEV,
  }

  /**
   * Configuration of the scalarization wrapper.
   *
   * @author Marlon Braun <marlon.braun@partner.kit.edu>
   */
  public static class Config {

    /**
     * Chosen scalarization functions.
     */
    private ScalarizationType scalarizationType;

    /**
     * Weights for the weighted sum, product and Chebyshev.
     */
    private double[] weights;

    /**
     * Ideal values for the Chebyshev and weighted Chebyshev. If no ideal
     * values are specified, they are computed on the fly.
     */
    private double[] idealValues;

    /**
     * Nadir values for the Nash bargaining solution. If no nadir values are
     * specified, they are computed on the fly.
     */
    private double[] nadirValues;

    /**
     * Extreme points for angle utility. If no extreme points are specified,
     * they are computed on the fly.
     */
    private double[][] extremePoints;
  }

  /**
   * Configuration of this scalarization Wrapper
   */
  private final Config config;

  /**
   * Initialize from scalarization type
   *
   * @param scalarizationType Chosen scalarization function
   */
  public ScalarizationWrapper(ScalarizationType scalarizationType) {
    Config config = new Config();
    config.scalarizationType = scalarizationType;
    this.config = config;
  }

  /**
   * Initialize from Config.
   *
   * @param config Configuration of the scalarization Wrapper.
   */
  public ScalarizationWrapper(Config config) {
    this.config = config;
  }

  /**
   * Computes scalarization values and assigns them as
   * {@link ScalarizationValue} attribute to the solutions.
   *
   * @param solutionsList Solutions for which scalarization values computed.
   */
  public <S extends Solution<?>> void execute(List<S> solutionsList) {
    switch (config.scalarizationType) {
      case ANGLE_UTILITY:
        if (config.extremePoints == null)
          ScalarizationUtils.angleUtility(solutionsList);
        else
          ScalarizationUtils.angleUtility(solutionsList, config.extremePoints);
        break;
      case CHEBYSHEV:
        if (config.idealValues == null)
          ScalarizationUtils.chebyshev(solutionsList);
        else
          ScalarizationUtils.chebyshev(solutionsList, config.idealValues);
        break;
      case NASH:
        if (config.nadirValues == null)
          ScalarizationUtils.nash(solutionsList);
        else
          ScalarizationUtils.nash(solutionsList, config.nadirValues);
        break;
      case SUM_OF_OBJECTIVES:
        ScalarizationUtils.sumOfObjectives(solutionsList);
        break;
      case PRODUCT_OF_OBJECTIVES:
        ScalarizationUtils.productOfObjectives(solutionsList);
        break;
      case TRADEOFF_UTILITY:
        ScalarizationUtils.tradeoffUtility(solutionsList);
        break;
      case UNIFORM:
        ScalarizationUtils.uniform(solutionsList);
        break;
      case WEIGHTED_CHEBYSHEV:
        if (config.idealValues == null)
          ScalarizationUtils.weightedChebyshev(solutionsList, config.weights);
        else
          ScalarizationUtils.weightedChebyshev(solutionsList, config.idealValues, config.weights);
        break;
      case WEIGHTED_PRODUCT:
        ScalarizationUtils.weightedProduct(solutionsList, config.weights);
        break;
      case WEIGHTED_SUM:
        ScalarizationUtils.weightedSum(solutionsList, config.weights);
        break;
    }
  }

}
