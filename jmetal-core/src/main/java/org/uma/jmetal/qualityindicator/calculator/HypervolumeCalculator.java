package org.uma.jmetal.qualityindicator.calculator;

import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.qualityindicator.Hypervolume;

/**
 * This class was designed to help the calculation of Hypervolume.
 * The idea is to add all available Pareto fronts into an <b>internal
 * population</b>, in order to perform an exact normalization.
 * This internal population is used for assessing the max and min
 * values for the normalization. It is useful when you don't know
 * what are these values and must use the max known and min known
 * values.
 * <br/>
 * <br/>
 * If the intention is to compare the fronts A, B and C, then the
 * addParetoFront() method must be invoked three times giving A, B
 * and then C as input.
 */
public class HypervolumeCalculator extends Calculator{

  private final Hypervolume hypervolume;

  private final int numberOfObjectives;
  
  /**
   * Constructor for the hypervolume calculator.
   * 
   * @param numberOfObjectives the number of objectives for the problem.
   */
  public HypervolumeCalculator(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    this.hypervolume = new Hypervolume();
  }

  /**
   * Calculates the hypervolume for the given front. This method uses the internal
   * population as basis for assessing the min and max values in the normalization.
   * 
   * @param frontPath path of the Pareto front to be evaluated.
   * @return the hypervolume value for the given Pareto front.
   */
  @Override
  public double execute(String frontPath) {
    return execute(metricsUtil.readNonDominatedSolutionSet(frontPath));
  }

  /**
   * Calculates the hypervolume for the given front. This method uses the internal
   * population as basis for assessing the min and max values in the normalization.
   * 
   * @param front Pareto front to be evaluated.
   * @return the hypervolume value for the given Pareto front.
   */
  @Override
  public double execute(SolutionSet front) {
    if (internalPopulation.size() != 0) {
      double[] maximumValues = metricsUtil.getMaximumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
      double[] minimumValues = metricsUtil.getMinimumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
      return hypervolume.hypervolume(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
    }
    return 0D;
  }

}
