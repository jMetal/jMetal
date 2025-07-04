package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements the Unimodal Normal Distribution Crossover (UNDX) operator for real-valued
 * solutions. UNDX is a multi-parent crossover operator that generates offspring based on the normal
 * distribution defined by three parent solutions. It is particularly effective for continuous
 * optimization problems as it preserves the statistics of the population.
 *
 * <p>Reference: Onikura, T., & Kobayashi, S. (1999). Extended UNIMODAL DISTRIBUTION CROSSOVER for
 * REAL-CODED GENETIC ALGORITHMS. In Proceedings of the 1999 Congress on Evolutionary
 * Computation-CEC99 (Cat. No. 99TH8406) (Vol. 2, pp. 1581-1588). IEEE.
 *
 * @author Antonio J. Nebro
 */
public class UnimodalNormalDistributionCrossover implements CrossoverOperator<DoubleSolution> {
  private final double crossoverProbability;
  private final double zeta;
  private final double eta;
  private final RepairDoubleSolution solutionRepair;
  private final RandomGenerator<Double> randomGenerator;

  /**
   * Constructor with default parameter values
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   */
  public UnimodalNormalDistributionCrossover(double crossoverProbability) {
    this(crossoverProbability, 0.5, 0.35, new RepairDoubleSolutionWithBoundValue());
  }

  /**
   * Constructor with all parameters
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param zeta Controls the spread along the line connecting parents (typically in [0.1, 1.0], where
   *     smaller values produce offspring closer to the parents)
   * @param eta Controls the spread in the orthogonal direction (typically in [0.1, 0.5], where
   *     smaller values produce more concentrated distributions)
   * @param solutionRepair Strategy for repairing solutions
   */
  public UnimodalNormalDistributionCrossover(
      double crossoverProbability,
      double zeta,
      double eta,
      RepairDoubleSolution solutionRepair) {
    this(
        crossoverProbability,
        zeta,
        eta,
        solutionRepair,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor with all parameters including random generator
   *
   * @param crossoverProbability Crossover probability
   * @param zeta Controls the spread along the line connecting parents
   * @param eta Controls the spread in the orthogonal direction
   * @param solutionRepair Strategy for repairing solutions
   * @param randomGenerator Random number generator
   * @throws IllegalArgumentException if crossoverProbability is not in [0,1], or if zeta or eta are negative
   */
  public UnimodalNormalDistributionCrossover(
      double crossoverProbability,
      double zeta,
      double eta,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.that(zeta >= 0, "Zeta must be non-negative: " + zeta);
    Check.that(eta >= 0, "Eta must be non-negative: " + eta);
    Check.notNull(solutionRepair);
    Check.notNull(randomGenerator);

    this.crossoverProbability = crossoverProbability;
    this.zeta = zeta;
    this.eta = eta;
    this.solutionRepair = solutionRepair;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() >= 3, "UNDX requires at least 3 parents");

    List<DoubleSolution> offspring = new ArrayList<>(2);
    DoubleSolution parent1 = solutions.get(0);
    DoubleSolution parent2 = solutions.get(1);
    DoubleSolution parent3 = solutions.get(2);

    if (randomGenerator.getRandomValue() < crossoverProbability) {
      int numberOfVariables = parent1.variables().size();
      
      // Create offspring solutions
      DoubleSolution child1 = (DoubleSolution) parent1.copy();
      DoubleSolution child2 = (DoubleSolution) parent2.copy();
      
      // Calculate the center of mass between parent1 and parent2
      double[] center = new double[numberOfVariables];
      for (int i = 0; i < numberOfVariables; i++) {
        center[i] = (parent1.variables().get(i) + parent2.variables().get(i)) / 2.0;
      }
      
      // Calculate the difference vector between parent1 and parent2
      double[] diff = new double[numberOfVariables];
      double distanceSquared = 0.0;
      for (int i = 0; i < numberOfVariables; i++) {
        diff[i] = parent2.variables().get(i) - parent1.variables().get(i);
        distanceSquared += diff[i] * diff[i];
      }
      double distance = Math.sqrt(distanceSquared);
      
      // If parents are too close, return exact copies to avoid division by zero
      if (distance < 1e-10) {
        offspring.add((DoubleSolution) parent1.copy());
        offspring.add((DoubleSolution) parent2.copy());
        return offspring;
      }
      
      // Generate offspring
      for (int i = 0; i < numberOfVariables; i++) {
        // Generate values along the line connecting the parents
        double alpha = randomGenerator.getRandomValue() * zeta * distance;
        
        // Generate values in the orthogonal direction
        double beta = 0.0;
        for (int j = 0; j < 2; j++) {
          beta += (randomGenerator.getRandomValue() - 0.5) * eta * distance;
        }
        
        // Calculate the orthogonal component from parent3
        double orthogonal = (parent3.variables().get(i) - center[i]) / distance;
        
        // Create the new values
        double value1 = center[i] + alpha * diff[i] / distance + beta * orthogonal;
        double value2 = center[i] - alpha * diff[i] / distance - beta * orthogonal;
        
        // Apply the new values with repair if needed
        double lowerBound = child1.getBounds(i).getLowerBound();
        double upperBound = child1.getBounds(i).getUpperBound();
        child1.variables().set(i, solutionRepair.repairSolutionVariableValue(
            value1, lowerBound, upperBound));
            
        lowerBound = child2.getBounds(i).getLowerBound();
        upperBound = child2.getBounds(i).getUpperBound();
        child2.variables().set(i, solutionRepair.repairSolutionVariableValue(
            value2, lowerBound, upperBound));
      }
      
      offspring.add(child1);
      offspring.add(child2);
    } else {
      // If crossover is not applied, return the parents
      offspring.add((DoubleSolution) parent1.copy());
      offspring.add((DoubleSolution) parent2.copy());
    }

    return offspring;
  }

  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  @Override
  public int numberOfRequiredParents() {
    return 3; // UNDX requires exactly 3 parents
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2; // UNDX generates 2 offspring
  }

  /** @return The zeta parameter */
  public double zeta() {
    return zeta;
  }

  /** @return The eta parameter */
  public double eta() {
    return eta;
  }
}
