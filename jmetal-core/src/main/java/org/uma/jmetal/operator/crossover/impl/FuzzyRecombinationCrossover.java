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
 * This class implements the Fuzzy Recombination (FR) operator for real-valued solutions.
 * FR is a real-parameter crossover operator that uses fuzzy connectives to combine parent solutions,
 * providing a flexible way to control exploration and exploitation.
 *
 * <p>Reference: Herrera, F., Lozano, M., & Verdegay, J. L. (1998). Tackling real-coded genetic
 * algorithms: Operators and tools for behavioural analysis. Artificial intelligence review, 12(4),
 * 265-319.
 *
 * @author Antonio J. Nebro
 * @generated Cascade v1.0.0
 */
public class FuzzyRecombinationCrossover implements CrossoverOperator<DoubleSolution> {
  private static final double EPSILON = 1e-10;
  private final double crossoverProbability;
  private final double alpha;
  private final RepairDoubleSolution solutionRepair;
  private final RandomGenerator<Double> randomGenerator;

  /**
   * Constructor with default parameter values
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   */
  public FuzzyRecombinationCrossover(double crossoverProbability) {
    this(crossoverProbability, 1.0, new RepairDoubleSolutionWithBoundValue());
  }

  /**
   * Constructor with all parameters
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param alpha Controls the spread of the fuzzy membership function (typically in [0.5, 2.0])
   * @param solutionRepair Strategy for repairing solutions
   */
  public FuzzyRecombinationCrossover(
      double crossoverProbability, double alpha, RepairDoubleSolution solutionRepair) {
    this(
        crossoverProbability,
        alpha,
        solutionRepair,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor with all parameters including random generator (for testing)
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param alpha Controls the spread of the fuzzy membership function
   * @param solutionRepair Strategy for repairing solutions
   * @param randomGenerator Random number generator
   */
  public FuzzyRecombinationCrossover(
      double crossoverProbability,
      double alpha,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    Check.that(alpha > 0, "Alpha must be positive: " + alpha);
    Check.notNull(solutionRepair);
    Check.notNull(randomGenerator);

    this.crossoverProbability = crossoverProbability;
    this.alpha = alpha;
    this.solutionRepair = solutionRepair;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() == 2, "FR requires exactly two parents");

    List<DoubleSolution> offspring = new ArrayList<>(2);
    DoubleSolution parent1 = solutions.get(0);
    DoubleSolution parent2 = solutions.get(1);

    if (randomGenerator.getRandomValue() < crossoverProbability) {
      int numberOfVariables = parent1.variables().size();
      
      // Create offspring solutions
      DoubleSolution child1 = (DoubleSolution) parent1.copy();
      DoubleSolution child2 = (DoubleSolution) parent2.copy();
      
      for (int i = 0; i < numberOfVariables; i++) {
        double p1 = parent1.variables().get(i);
        double p2 = parent2.variables().get(i);
        
        // If parents have the same value, skip crossover for this variable
        if (Math.abs(p1 - p2) < EPSILON) {
          continue;
        }
        
        // Calculate the fuzzy membership values
        double min = Math.min(p1, p2);
        double max = Math.max(p1, p2);
        double range = max - min;
        
        // Calculate the fuzzy spread
        double spread = alpha * range;
        
        // Calculate new values using fuzzy connectives
        double beta = randomGenerator.getRandomValue();
        double value1 = min + beta * spread;
        double value2 = max - (1 - beta) * spread;
        
        // Apply the new values with repair if needed
        double lowerBound = child1.getBounds(i).getLowerBound();
        double upperBound = child1.getBounds(i).getUpperBound();
        
        child1.variables().set(i, solutionRepair.repairSolutionVariableValue(value1, lowerBound, upperBound));
        child2.variables().set(i, solutionRepair.repairSolutionVariableValue(value2, lowerBound, upperBound));
      }
      
      offspring.add(child1);
      offspring.add(child2);
    } else {
      // If crossover is not applied, return copies of the parents
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
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
  
  /**
   * @return The alpha parameter (spread of the fuzzy membership function)
   */
  public double alpha() {
    return alpha;
  }
}
