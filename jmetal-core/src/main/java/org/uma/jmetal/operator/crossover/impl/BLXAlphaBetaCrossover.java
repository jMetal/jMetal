package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements the BLX-αβ crossover operator for real-valued solutions.
 * The BLX-αβ crossover creates offspring that are randomly selected from intervals
 * that extend beyond the parent values, controlled by parameters α and β.
 *
 * <p>Reference: Eshelman, L. J., & Schaffer, J. D. (1993). Real-coded genetic
 * algorithms and interval-schemata. Foundations of genetic algorithms, 2, 187-202.
 *
 * <p>Parameter recommendations:
 * <ul>
 *   <li>α (alpha): Controls the exploration range below the smaller parent value.
 *       Typical values are in the range [0.0, 1.0]. A value of 0.0 means no exploration
 *       below the smaller parent, while 1.0 allows exploration equal to the distance
 *       between parents below the smaller parent.</li>
 *   
 *   <li>β (beta): Controls the exploration range above the larger parent value.
 *       Typical values are in the range [0.0, 1.0]. A value of 0.0 means no exploration
 *       above the larger parent, while 1.0 allows exploration equal to the distance
 *       between parents above the larger parent.</li>
 * </ul>
 *
 * <p>Note: Both α and β must be non-negative values. Setting both to 0.0 results in
 * a crossover that only generates values strictly between the parent values.
 *
 * @author Antonio J. Nebro
 * @generated Cascade v1.0.0
 */
public class BLXAlphaBetaCrossover implements CrossoverOperator<DoubleSolution> {
  private final double crossoverProbability;
  private final double alpha;
  private final double beta;
  private final RandomGenerator<Double> randomGenerator;

  /**
   * Constructor with default random generator
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param alpha Controls exploration below parents (must be >= 0, typical [0,1])
   * @param beta Controls exploration above parents (must be >= 0, typical [0,1])
   */
  public BLXAlphaBetaCrossover(double crossoverProbability, double alpha, double beta) {
    this(
        crossoverProbability,
        alpha,
        beta,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor with custom random generator
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param alpha Controls exploration below parents (must be >= 0, typical [0,1])
   * @param beta Controls exploration above parents (must be >= 0, typical [0,1])
   * @param randomGenerator Custom random number generator
   */
  public BLXAlphaBetaCrossover(
      double crossoverProbability,
      double alpha,
      double beta,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.that(alpha >= 0, "Alpha must be non-negative: " + alpha);
    Check.that(beta >= 0, "Beta must be non-negative: " + beta);
    Check.notNull(randomGenerator);

    this.crossoverProbability = crossoverProbability;
    this.alpha = alpha;
    this.beta = beta;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "BLX-αβ Crossover requires exactly two parents");

    List<DoubleSolution> offspring = new ArrayList<>(2);
    DoubleSolution parent1 = solutions.get(0);
    DoubleSolution parent2 = solutions.get(1);

    // Always create offspring solutions
    DoubleSolution child1 = (DoubleSolution) parent1.copy();
    DoubleSolution child2 = (DoubleSolution) parent2.copy();

    // Apply crossover with probability
    if (randomGenerator.getRandomValue() < crossoverProbability) {
      int numberOfVariables = parent1.variables().size();

      for (int i = 0; i < numberOfVariables; i++) {
        double p1 = parent1.variables().get(i);
        double p2 = parent2.variables().get(i);

        // Ensure p1 <= p2
        if (p1 > p2) {
          double temp = p1;
          p1 = p2;
          p2 = temp;
        }

        // Calculate the range
        double d = p2 - p1;
        double cMin = p1 - alpha * d;
        double cMax = p2 + beta * d;

        // Generate new values for both children
        double value1 = cMin + randomGenerator.getRandomValue() * (cMax - cMin);
        double value2 = cMin + randomGenerator.getRandomValue() * (cMax - cMin);

        // Apply bounds checking and repair if needed
        double lowerBound = child1.getBounds(i).getLowerBound();
        double upperBound = child1.getBounds(i).getUpperBound();
        
        value1 = Math.max(lowerBound, Math.min(upperBound, value1));
        value2 = Math.max(lowerBound, Math.min(upperBound, value2));

        child1.variables().set(i, value1);
        child2.variables().set(i, value2);
      }
    }

    offspring.add(child1);
    offspring.add(child2);
    return offspring;
  }

  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }

  /**
   * @return The alpha parameter value (exploration below parents)
   */
  public double getAlpha() {
    return alpha;
  }

  /**
   * @return The beta parameter value (exploration above parents)
   */
  public double getBeta() {
    return beta;
  }
}
