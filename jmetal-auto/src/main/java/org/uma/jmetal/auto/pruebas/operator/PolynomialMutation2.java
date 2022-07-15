package org.uma.jmetal.auto.pruebas.operator;

import org.uma.jmetal.auto.pruebas.solution.DoubleSolution2;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a polynomial mutation operator
 *
 * <p>The implementation is based on the NSGA-II code available in
 * http://www.iitk.ac.in/kangal/codes.shtml
 *
 * <p>If the lower and upper bounds of a variable are the same, no mutation is carried out and the
 * bound value is returned.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PolynomialMutation2 implements Mutation2<DoubleSolution2> {
  private static final double DEFAULT_PROBABILITY = 0.01;
  private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0;
  private double distributionIndex;
  private double mutationProbability;
  private RandomGenerator<Double> randomGenerator;

  /** Constructor */
  public PolynomialMutation2(
      double mutationProbability,
      double distributionIndex,
      RandomGenerator<Double> randomGenerator) {
    Check.that(distributionIndex >= 0, "Distribution index is negative: " + distributionIndex);
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
    this.distributionIndex = distributionIndex;
    this.randomGenerator = randomGenerator;
  }

  /* Getters */
  @Override
  public double getMutationProbability() {
    return mutationProbability;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  /* Setters */
  public void setMutationProbability(double probability) {
    this.mutationProbability = probability;
  }

  public void setDistributionIndex(double distributionIndex) {
    this.distributionIndex = distributionIndex;
  }

  /** Execute() method */
  @Override
  public DoubleSolution2 execute(DoubleSolution2 solution) throws JMetalException {
    Check.notNull(solution);

    doMutation2(solution);

    return solution;
  }

  /** Perform the mutation operation */
  private void doMutation2(DoubleSolution2 solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;

    for (int i = 0; i < solution.variables().size(); i++) {
      if (randomGenerator.getRandomValue() <= mutationProbability) {
        y = solution.variables().get(i);
        Bounds<Double> bounds = solution.getBounds().get(i);
        yl = bounds.getLowerBound();
        yu = bounds.getUpperBound();
        if (yl == yu) {
          y = yl;
        } else {
          delta1 = (y - yl) / (yu - yl);
          delta2 = (yu - y) / (yu - yl);
          rnd = randomGenerator.getRandomValue();
          mutPow = 1.0 / (distributionIndex + 1.0);
          if (rnd <= 0.5) {
            xy = 1.0 - delta1;
            val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
            deltaq = Math.pow(val, mutPow) - 1.0;
          } else {
            xy = 1.0 - delta2;
            val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
            deltaq = 1.0 - Math.pow(val, mutPow);
          }
          y = y + deltaq * (yu - yl);
        }
        solution.variables().set(i, y);
      }
    }
  }
}
