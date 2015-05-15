package org.uma.jmetal.qualityindicator.calculator;

import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityindicator.InvertedGenerationalDistance;

/**
 * This class was designed to help the calculation of Inverted Generational Distance (IGD).
 * The idea is to add all available Pareto fronts into an <b>internal population</b>,
 * in order to perform an exact normalization. This internal population is used for
 * assessing the max and min values for the normalization. It is useful when you don't know
 * what are these values and must use the max known and min known values.
 * <br/>
 * <br/>
 * If the intention is to compare the fronts A, B and C, then the addParetoFront() method
 * must be invoked three times giving A, B and then C as input.
 */
public class InvertedGenerationalDistanceCalculator extends Calculator {

  private final InvertedGenerationalDistance igd;
  private final int numberOfObjectives;

  /**
   * Constructor for the IGD calculator.
   *
   * @param numberOfObjectives the number of objectives for the problem.
   */
  public InvertedGenerationalDistanceCalculator(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    this.igd = new InvertedGenerationalDistance();
  }

  /**
   * Calculates the IGD for the given front. This method uses the internal
   * population as basis for assessing the min and max values in the normalization.
   *
   * @param frontPath path of the Pareto front to be evaluated.
   * @return the IGD value for the given Pareto front.
   */
  @Override
  public double execute(String frontPath) {
    return execute(metricsUtil.readNonDominatedSolutionSet(frontPath));
  }

  /**
   * Calculates the IGD for the given front. This method uses the internal
   * population as basis for assessing the min and max values in the normalization.
   * 
   * @param front Pareto front to be evaluated.
   * @return the IGD value for the given Pareto front.
   */
  @Override
  public double execute(SolutionSet front) {
    if (internalPopulation.size() != 0) {
      double[] maximumValues = metricsUtil.getMaximumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
      double[] minimumValues = metricsUtil.getMinimumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
      return igd.invertedGenerationalDistance(front.writeObjectivesToMatrix(), metricsUtil.getNonDominatedSolutions(internalPopulation).writeObjectivesToMatrix(), maximumValues, minimumValues);
    }
    return 0D;
  }
  
}
