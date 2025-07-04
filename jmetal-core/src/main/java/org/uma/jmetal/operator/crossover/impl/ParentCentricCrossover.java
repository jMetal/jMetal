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
 * This class implements the Parent-Centric Recombination (PCX) operator for real-valued solutions.
 * PCX is a multi-parent crossover operator that generates offspring based on a normal distribution
 * centered around a parent solution, with spread controlled by the distribution of other parents.
 *
 * <p>Reference: Deb, K., Anand, A., & Joshi, D. (2002). A computationally efficient evolutionary
 * algorithm for real-parameter optimization. Evolutionary computation, 10(4), 371-395.
 *
 * @author Antonio J. Nebro
 */
public class ParentCentricCrossover implements CrossoverOperator<DoubleSolution> {
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
  public ParentCentricCrossover(double crossoverProbability) {
    this(crossoverProbability, 0.5, 0.1, new RepairDoubleSolutionWithBoundValue());
  }

  /**
   * Constructor with all parameters
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param zeta Controls the spread along the line connecting parents (typically in [0.1, 0.5])
   * @param eta Controls the spread in orthogonal directions (typically in [0.1, 0.5])
   * @param solutionRepair Strategy for repairing solutions
   */
  public ParentCentricCrossover(
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
   * Constructor with all parameters including random generator (for testing)
   *
   * @param crossoverProbability Crossover probability (must be in [0,1])
   * @param zeta Controls the spread along the line connecting parents
   * @param eta Controls the spread in orthogonal directions
   * @param solutionRepair Strategy for repairing solutions
   * @param randomGenerator Random number generator
   */
  public ParentCentricCrossover(
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
    Check.that(solutions.size() >= 3, "PCX requires at least 3 parents");

    List<DoubleSolution> offspring = new ArrayList<>(2);
    DoubleSolution parent1 = solutions.get(0);
    DoubleSolution parent2 = solutions.get(1);
    DoubleSolution parent3 = solutions.get(2);

    if (randomGenerator.getRandomValue() < crossoverProbability) {
      int numberOfVariables = parent1.variables().size();
      
      // Create offspring solutions
      DoubleSolution child1 = (DoubleSolution) parent1.copy();
      DoubleSolution child2 = (DoubleSolution) parent2.copy();
      
      // Calculate the center of mass between parent2 and parent3
      double[] center = new double[numberOfVariables];
      for (int i = 0; i < numberOfVariables; i++) {
        center[i] = (parent2.variables().get(i) + parent3.variables().get(i)) / 2.0;
      }
      
      // Calculate the difference vector between parent2 and parent3
      double[] diff = new double[numberOfVariables];
      double distanceSquared = 0.0;
      for (int i = 0; i < numberOfVariables; i++) {
        diff[i] = parent3.variables().get(i) - parent2.variables().get(i);
        distanceSquared += diff[i] * diff[i];
      }
      double distance = Math.sqrt(distanceSquared);
      
      // If parents are too close, return exact copies to avoid division by zero
      if (distance < 1e-10) {
        offspring.add((DoubleSolution) parent1.copy());
        offspring.add((DoubleSolution) parent2.copy());
        return offspring;
      }
      
      // Generate orthogonal basis vectors
      double[] orthogonal = new double[numberOfVariables];
      for (int i = 0; i < numberOfVariables; i++) {
        orthogonal[i] = parent1.variables().get(i) - center[i];
        
        // Project out the component along the diff vector
        double dotProduct = 0.0;
        for (int j = 0; j < numberOfVariables; j++) {
          dotProduct += orthogonal[j] * diff[j];
        }
        
        for (int j = 0; j < numberOfVariables; j++) {
          orthogonal[j] -= (dotProduct / distanceSquared) * diff[j];
        }
      }
      
      // Normalize the orthogonal vector
      double orthogonalNorm = 0.0;
      for (int i = 0; i < numberOfVariables; i++) {
        orthogonalNorm += orthogonal[i] * orthogonal[i];
      }
      orthogonalNorm = Math.sqrt(orthogonalNorm);
      
      // Generate offspring
      for (int i = 0; i < numberOfVariables; i++) {
        // Generate values along the line connecting parent2 and parent3
        double alpha = randomGenerator.getRandomValue() * zeta * distance;
        
        // Generate values in the orthogonal direction
        double beta = 0.0;
        if (orthogonalNorm > 1e-10) {
          beta = (randomGenerator.getRandomValue() - 0.5) * eta * distance / orthogonalNorm;
        }
        
        // Create the new values centered around parent1
        double value1 = parent1.variables().get(i) + alpha * diff[i] / distance + beta * orthogonal[i];
        double value2 = parent1.variables().get(i) - alpha * diff[i] / distance - beta * orthogonal[i];
        
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
    return 3;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
  
  /**
   * @return The zeta parameter (spread along the line connecting parents)
   */
  public double zeta() {
    return zeta;
  }
  
  /**
   * @return The eta parameter (spread in orthogonal directions)
   */
  public double eta() {
    return eta;
  }
}
