package org.uma.jmetal.operator.mutation.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements the linked polynomial mutation operator presented in: https://doi.org/10.1109/SSCI.2016.7850214
 *
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class LinkedPolynomialMutation implements MutationOperator<DoubleSolution> {
  private static final double DEFAULT_PROBABILITY = 0.01;
  private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0;
  private double distributionIndex;
  private double mutationProbability;
  private RepairDoubleSolution solutionRepair;

  private RandomGenerator<Double> randomGenerator;

  /** Constructor */
  public LinkedPolynomialMutation() {
    this(DEFAULT_PROBABILITY, DEFAULT_DISTRIBUTION_INDEX);
  }

  /** Constructor */
  public LinkedPolynomialMutation(
      DoubleProblem problem, double distributionIndex, RandomGenerator<Double> randomGenerator) {
    this(1.0 / problem.getNumberOfVariables(), distributionIndex);
    this.randomGenerator = randomGenerator;
  }

  /** Constructor */
  public LinkedPolynomialMutation(double mutationProbability, double distributionIndex) {
    this(mutationProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue());
  }

  /** Constructor */
  public LinkedPolynomialMutation(
      double mutationProbability,
      double distributionIndex,
      RandomGenerator<Double> randomGenerator) {
    this(
        mutationProbability,
        distributionIndex,
        new RepairDoubleSolutionWithBoundValue(),
        randomGenerator);
  }

  /** Constructor */
  public LinkedPolynomialMutation(
      double mutationProbability, double distributionIndex, RepairDoubleSolution solutionRepair) {
    this(
        mutationProbability,
        distributionIndex,
        solutionRepair,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public LinkedPolynomialMutation(
      double mutationProbability,
      double distributionIndex,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.that(distributionIndex >= 0, "Distribution index is negative: " + distributionIndex);
    Check.probabilityIsValid(mutationProbability);
    this.mutationProbability = mutationProbability;
    this.distributionIndex = distributionIndex;
    this.solutionRepair = solutionRepair;
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
  public @NotNull DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    Check.notNull(solution);

    doMutation(solution);

    return solution;
  }

  public double[] execute(double @NotNull [] x, double[] lowerBound, double[] upperBound) {
    double delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;

    double rnd = randomGenerator.getRandomValue();
    for (var i = 0; i < x.length; i++) {
      if (randomGenerator.getRandomValue() <= mutationProbability) {
        y = x[i];
        yl = lowerBound[i];
        yu = upperBound[i];
        if (yl == yu) {
          y = yl;
        } else {
          delta1 = (y - yl) / (yu - yl);
          delta2 = (yu - y) / (yu - yl);
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
          y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
        }
        x[i] = y ;
      }
    }

    return x ;
  }

  /** Perform the mutation operation */
  private void doMutation(DoubleSolution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;

    for (var i = 0; i < solution.variables().size(); i++) {
      if (randomGenerator.getRandomValue() <= mutationProbability) {
        y = solution.variables().get(i);
        var bounds = solution.getBounds(i);
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
          y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
        }
        solution.variables().set(i, y);
      }
    }
  }
}
