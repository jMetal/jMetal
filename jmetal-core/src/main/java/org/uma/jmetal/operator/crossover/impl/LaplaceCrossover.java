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
 * This class implements a Laplace Crossover (LX) operator for real-valued solutions. The Laplace
 * Crossover uses the Laplace distribution to generate offspring solutions. The scale parameter (b)
 * controls the spread of the distribution around the parent solutions. The scale parameter must be
 * a positive value (scale > 0). Typical values range between 0.1 and 0.5, where smaller values
 * generate offspring closer to the parents and larger values produce more diverse offspring.
 *
 * <p>Reference: Deep, K., Thakur, M. (2007). A new crossover operator for real coded genetic
 * algorithms. Applied Mathematics and Computation, 188(1), 895-911.
 *
 * @author Your Name
 * @param <S> Solution type
 */
public class LaplaceCrossover implements CrossoverOperator<DoubleSolution> {
  private static final double DEFAULT_EPSILON = 1.0e-14;
  private static final double DEFAULT_SCALE = 0.5; // Default scale parameter for Laplace distribution

  private final double scale;
  private final double crossoverProbability;
  private final RepairDoubleSolution solutionRepair;
  private final RandomGenerator<Double> randomGenerator;

  /** Constructor with default scale parameter */
  public LaplaceCrossover(double crossoverProbability) {
    this(crossoverProbability, DEFAULT_SCALE, new RepairDoubleSolutionWithBoundValue());
  }

  /** Constructor with configurable scale parameter */
  public LaplaceCrossover(
      double crossoverProbability, double scale, RepairDoubleSolution solutionRepair) {
    this(
        crossoverProbability,
        scale,
        solutionRepair,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor with configurable random generator */
  public LaplaceCrossover(
      double crossoverProbability,
      double scale,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.that(scale > 0, "Scale parameter must be positive");
    Check.notNull(solutionRepair);
    Check.notNull(randomGenerator);

    this.crossoverProbability = crossoverProbability;
    this.scale = scale;
    this.solutionRepair = solutionRepair;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
  }

  private List<DoubleSolution> doCrossover(
      double probability, DoubleSolution parent1, DoubleSolution parent2) {
    List<DoubleSolution> offspring = new ArrayList<>(2);
    offspring.add((DoubleSolution) parent1.copy());
    offspring.add((DoubleSolution) parent2.copy());

    if (randomGenerator.getRandomValue() <= probability) {
      for (int i = 0; i < parent1.variables().size(); i++) {
        double x1 = parent1.variables().get(i);
        double x2 = parent2.variables().get(i);
        Bounds<Double> bounds = parent1.getBounds(i);
        double lowerBound = bounds.getLowerBound();
        double upperBound = bounds.getUpperBound();

        if (Math.abs(x1 - x2) > DEFAULT_EPSILON) {
          double beta = 0.0;
          double u = randomGenerator.getRandomValue();
          double r = randomGenerator.getRandomValue();

          // Generate beta from Laplace distribution
          if (r <= 0.5) {
            beta = x1 - scale * Math.log(1.0 - u);
          } else {
            beta = x1 + scale * Math.log(u);
          }

          // Calculate offspring values
          double y1 = beta;
          double y2 = x1 + x2 - beta;

          // Ensure values are within bounds
          y1 = solutionRepair.repairSolutionVariableValue(y1, lowerBound, upperBound);
          y2 = solutionRepair.repairSolutionVariableValue(y2, lowerBound, upperBound);

          // Set offspring values
          offspring.get(0).variables().set(i, y1);
          offspring.get(1).variables().set(i, y2);
        }
      }
    }

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

  public double getScale() {
    return scale;
  }
}
