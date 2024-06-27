package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class allows to apply a SBX crossover operator using two parent solutions (Double encoding).
 * A {@link RepairDoubleSolution} object is used to decide the strategy to apply when a value is out
 * of range.
 *
 * <p>The implementation is based on the NSGA-II code available in <a
 * href="http://www.iitk.ac.in/kangal/codes.shtml">http://www.iitk.ac.in/kangal/codes.shtml</a>
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class SBXCrossover implements CrossoverOperator<DoubleSolution> {

  /**
   * EPS defines the minimum difference allowed between real values
   */
  private static final double EPS = 1.0e-14;
  private double distributionIndex;
  private double crossoverProbability;
  private RepairDoubleSolution solutionRepair;
  private RandomGenerator<Double> randomGenerator;

  /**
   * Constructor
   */
  public SBXCrossover(double crossoverProbability, double distributionIndex) {
    this(crossoverProbability, distributionIndex, new RepairDoubleSolutionWithBoundValue());
  }

  /**
   * Constructor
   */
  public SBXCrossover(
      double crossoverProbability,
      double distributionIndex,
      RandomGenerator<Double> randomGenerator) {
    this(
        crossoverProbability,
        distributionIndex,
        new RepairDoubleSolutionWithBoundValue(),
        randomGenerator);
  }

  /**
   * Constructor
   */
  public SBXCrossover(
      double crossoverProbability, double distributionIndex, RepairDoubleSolution solutionRepair) {
    this(
        crossoverProbability,
        distributionIndex,
        solutionRepair,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor
   */
  public SBXCrossover(
      double crossoverProbability,
      double distributionIndex,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.that(distributionIndex >= 0, "Distribution index is negative: " + distributionIndex);

    this.crossoverProbability = crossoverProbability;
    this.distributionIndex = distributionIndex;
    this.solutionRepair = solutionRepair;

    this.randomGenerator = randomGenerator;
  }

  /* Getters */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  public double distributionIndex() {
    return distributionIndex;
  }

  /* Setters */
  public void crossoverProbability(double probability) {
    this.crossoverProbability = probability;
  }

  public void distributionIndex(double distributionIndex) {
    this.distributionIndex = distributionIndex;
  }

  /**
   * Execute() method
   */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
  }

  /**
   * doCrossover method
   */
  public List<DoubleSolution> doCrossover(
      double probability, DoubleSolution parent1, DoubleSolution parent2) {
    List<DoubleSolution> offspring = new ArrayList<DoubleSolution>(2);

    offspring.add((DoubleSolution) parent1.copy());
    offspring.add((DoubleSolution) parent2.copy());

    int i;
    double rand;
    double y1, y2, lowerBound, upperBound;
    double c1, c2;
    double alpha, beta, betaq;
    double valueX1, valueX2;

    if (randomGenerator.getRandomValue() <= probability) {
      for (i = 0; i < parent1.variables().size(); i++) {
        valueX1 = parent1.variables().get(i);
        valueX2 = parent2.variables().get(i);
        if (randomGenerator.getRandomValue() <= 0.5) {
          if (Math.abs(valueX1 - valueX2) > EPS) {
            if (valueX1 < valueX2) {
              y1 = valueX1;
              y2 = valueX2;
            } else {
              y1 = valueX2;
              y2 = valueX1;
            }

            Bounds<Double> bounds = parent1.getBounds(i);
            lowerBound = bounds.getLowerBound();
            upperBound = bounds.getUpperBound();

            rand = randomGenerator.getRandomValue();
            beta = 1.0 + (2.0 * (y1 - lowerBound) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow(rand * alpha, (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math.pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }
            c1 = 0.5 * (y1 + y2 - betaq * (y2 - y1));

            beta = 1.0 + (2.0 * (upperBound - y2) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math.pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }
            c2 = 0.5 * (y1 + y2 + betaq * (y2 - y1));

            c1 = solutionRepair.repairSolutionVariableValue(c1, lowerBound, upperBound);
            c2 = solutionRepair.repairSolutionVariableValue(c2, lowerBound, upperBound);

            if (randomGenerator.getRandomValue() <= 0.5) {
              offspring.get(0).variables().set(i, c2);
              offspring.get(1).variables().set(i, c1);
            } else {
              offspring.get(0).variables().set(i, c1);
              offspring.get(1).variables().set(i, c2);
            }
          } else {
            offspring.get(0).variables().set(i, valueX1);
            offspring.get(1).variables().set(i, valueX2);
          }
        } else {
          offspring.get(0).variables().set(i, valueX2);
          offspring.get(1).variables().set(i, valueX1);
        }
      }
    }

    return offspring;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
}
