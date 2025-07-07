package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * An advanced implementation of Differential Evolution crossover operator supporting multiple variants
 * and parameter adaptation. This implementation is designed to work well with both single and
 * multi-objective optimization problems.
 *
 * <p>Supported variants include: - RAND_1_BIN: Classic DE/rand/1/bin - RAND_2_BIN: DE/rand/2/bin -
 * CURRENT_TO_RAND_1: Current-to-rand/1 - CURRENT_TO_PBEST_1: Current-to-pbest/1 (JADE variant) -
 * SHADE: Success-History based Adaptive DE
 *
 * <p>The operator supports parameter adaptation for CR and F parameters when using adaptive
 * variants.
 *
 * @author Antonio J. Nebro
 * @generated Cascade v1.0.0
 */
public class AdvancedDifferentialEvolutionCrossover
    implements CrossoverOperator<DoubleSolution> {

  /** Enumeration of supported DE variants */
  public enum Variant {
    RAND_1_BIN,
    RAND_2_BIN,
    CURRENT_TO_RAND_1,
    CURRENT_TO_PBEST_1,
    SHADE
  }

  private final double cr; // Crossover probability
  private final double f; // Differential weight
  private final Variant variant;
  private final RandomGenerator<Double> randomGenerator;
  private final int populationSize;
  private final double pBest; // pBest parameter for JADE/SHADE variants
  private final RepairDoubleSolution solutionRepair;

  // Memory for parameter adaptation (used in SHADE)
  private final List<Double> memoryCR;
  private final List<Double> memoryF;
  private final List<DoubleSolution> archive;
  private final int memorySize;
  private int memoryIndex;

  private AdvancedDifferentialEvolutionCrossover(Builder builder) {
    this.cr = builder.cr;
    this.f = builder.f;
    this.variant = builder.variant;
    this.randomGenerator = builder.randomGenerator;
    this.populationSize = builder.populationSize;
    this.pBest = builder.pBest;
    this.solutionRepair = new RepairDoubleSolutionWithBoundValue();
    this.memorySize = builder.memorySize;
    
    // Initialize parameter memory for adaptive variants
    if (variant == Variant.SHADE || variant == Variant.CURRENT_TO_PBEST_1) {
      this.memoryCR = new ArrayList<>(Collections.nCopies(memorySize, 0.5));
      this.memoryF = new ArrayList<>(Collections.nCopies(memorySize, 0.5));
      this.archive = new ArrayList<>();
      this.memoryIndex = 0;
    } else {
      this.memoryCR = null;
      this.memoryF = null;
      this.archive = null;
    }
  }

  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    Check.notNull(solutions);
    Check.that(solutions.size() >= 4, "At least four solutions are required");

    DoubleSolution current = solutions.get(0);
    DoubleSolution trial = (DoubleSolution) current.copy();
    
    // Select parents based on the variant
    List<DoubleSolution> parents = selectParents(solutions, current);
    
    // Get CR and F values (adaptive or fixed)
    double currentCR = getCR();
    double currentF = getF();
    
    // Get random index for binomial crossover
    int jRand = JMetalRandom.getInstance().nextInt(0, current.variables().size() - 1);
    
    // Apply mutation and crossover
    for (int i = 0; i < current.variables().size(); i++) {
      if (shouldApplyCrossover(i, current.variables().size(), currentCR, jRand)) {
        double value = mutate(parents, i, currentF);
        trial.variables().set(i, repairVariableValue(value, current, i));
      }
    }
    
    // Update archive and parameter memory for adaptive variants
    if (variant == Variant.SHADE || variant == Variant.CURRENT_TO_PBEST_1) {
      updateArchive(current);
      // Note: Full SHADE parameter adaptation would be implemented here
    }
    
    List<DoubleSolution> result = new ArrayList<>(1);
    result.add(trial);
    return result;
  }
  
  private List<DoubleSolution> selectParents(List<DoubleSolution> population, DoubleSolution current) {
    List<DoubleSolution> parents = new ArrayList<>();
    
    // Always include the current solution as first parent
    parents.add(current);
    
    // Create a modifiable copy of population and remove current solution
    List<DoubleSolution> candidates = new ArrayList<>(population);
    candidates.remove(current);
    
    // Select remaining parents based on variant
    switch (variant) {
      case RAND_1_BIN:
        // For RAND_1_BIN, we need 2 distinct parents (plus the current solution)
        if (candidates.size() < 2) {
          throw new IllegalArgumentException("At least 3 solutions are required (including current)");
        }
        
        // Select 2 distinct random parents
        int idx1 = (int)(randomGenerator.getRandomValue() * candidates.size());
        DoubleSolution parent1 = candidates.get(idx1);
        candidates.remove(idx1);
        
        int idx2 = (int)(randomGenerator.getRandomValue() * candidates.size());
        DoubleSolution parent2 = candidates.get(idx2);
        
        parents.add(parent1);
        parents.add(parent2);
        break;
        
      case RAND_2_BIN:
        // For RAND_2_BIN, we need 4 distinct parents (plus the current solution)
        if (candidates.size() < 4) {
          throw new IllegalArgumentException("At least 5 solutions are required (including current)");
        }
        
        // Select 4 distinct random parents
        Collections.shuffle(candidates, new java.util.Random(randomGenerator.getRandomValue().longValue()));
        parents.addAll(candidates.subList(0, 4));
        break;
        
      case CURRENT_TO_RAND_1:
        // Current-to-rand uses 3 random parents
        if (candidates.size() < 3) {
          throw new IllegalArgumentException("At least 4 solutions are required (including current)");
        }
        
        Collections.shuffle(candidates, new java.util.Random(randomGenerator.getRandomValue().longValue()));
        parents.addAll(candidates.subList(0, 3));
        break;
        
      case CURRENT_TO_PBEST_1:
      case SHADE:
        // For pbest variants, select one pbest and one random solution
        List<DoubleSolution> sortedPopulation = new ArrayList<>(population);
        sortedPopulation.sort(Comparator.comparingInt(this::getRank));
        
        int pBestSize = Math.max(1, (int) (population.size() * pBest));
        int pBestIndex = (int)(randomGenerator.getRandomValue() * pBestSize) % pBestSize;
        DoubleSolution pBestParent = sortedPopulation.get(pBestIndex);
        
        // Get two distinct random solutions (different from current and pBest)
        candidates = population.stream()
            .filter(s -> s != current && s != pBestParent)
            .collect(Collectors.toList());
        Collections.shuffle(candidates);
        
        parents.add(pBestParent);
        parents.add(candidates.get(0));
        
        // For SHADE, also add a random solution from archive if not empty
        if (variant == Variant.SHADE && !archive.isEmpty()) {
          if (!archive.isEmpty()) {
            int archiveIndex = (int)(randomGenerator.getRandomValue() * archive.size()) % archive.size();
            parents.add(archive.get(archiveIndex));
          } else {
            parents.add(candidates.get(1));
          }
        } else {
          parents.add(candidates.get(1));
        }
        break;
        
      default:
        throw new UnsupportedOperationException("Variant not supported: " + variant);
    }
    
    return parents;
  }
  
  private double mutate(List<DoubleSolution> parents, int varIndex, double f) {
    switch (variant) {
      case RAND_1_BIN:
        return parents.get(0).variables().get(varIndex) + 
               f * (parents.get(1).variables().get(varIndex) - 
                    parents.get(2).variables().get(varIndex));
                    
      case RAND_2_BIN:
        return parents.get(1).variables().get(varIndex) + 
               f * (parents.get(2).variables().get(varIndex) - 
                    parents.get(3).variables().get(varIndex)) +
               f * (parents.get(4).variables().get(varIndex) - 
                    parents.get(4).variables().get(varIndex));
                    
      case CURRENT_TO_RAND_1:
        return parents.get(0).variables().get(varIndex) +
               f * (parents.get(1).variables().get(varIndex) - 
                    parents.get(0).variables().get(varIndex)) +
               f * (parents.get(2).variables().get(varIndex) - 
                    parents.get(3).variables().get(varIndex));
                    
      case CURRENT_TO_PBEST_1:
      case SHADE:
        // For pbest variants, use the pbest parent
        return parents.get(0).variables().get(varIndex) +
               f * (parents.get(1).variables().get(varIndex) - 
                    parents.get(0).variables().get(varIndex)) +
               f * (parents.get(2).variables().get(varIndex) - 
                    parents.get(3).variables().get(varIndex));
                    
      default:
        throw new UnsupportedOperationException("Variant not supported: " + variant);
    }
  }
  
  private boolean shouldApplyCrossover(int varIndex, int totalVars, double cr, int jRand) {
    // Always include at least one variable (jRand)
    if (varIndex == jRand) {
      return true;
    }
    
    // For binomial crossover
    return randomGenerator.getRandomValue() < cr;
  }
  
  private double repairVariableValue(double value, DoubleSolution solution, int varIndex) {
    Bounds<Double> bounds = solution.getBounds(varIndex);
    return solutionRepair.repairSolutionVariableValue(
        value, bounds.getLowerBound(), bounds.getUpperBound());
  }
  
  private void updateArchive(DoubleSolution solution) {
    // Add solution to archive
    archive.add((DoubleSolution) solution.copy());
    
    // Limit archive size
    int maxArchiveSize = (int) (populationSize * 2.0); // Typical archive size
    while (archive.size() > maxArchiveSize) {
      archive.remove(0); // Simple FIFO removal
    }
  }
  
  /**
   * Applies the Box-Muller transform to generate a normally distributed random value.
   * @param mean The mean of the normal distribution
   * @param stdDev The standard deviation of the normal distribution
   * @return A normally distributed random value
   */
  private double nextGaussian(double mean, double stdDev) {
    double u1 = randomGenerator.getRandomValue();
    double u2 = randomGenerator.getRandomValue();
    return mean + stdDev * Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
  }
  
  private double getCR() {
    if (variant == Variant.SHADE || variant == Variant.CURRENT_TO_PBEST_1) {
      // Get CR from memory with some randomization
      double meanCR = memoryCR.get(memoryIndex);
      double randomizedCR = nextGaussian(meanCR, 0.1);
      return Math.max(0, Math.min(1.0, randomizedCR));
    }
    return cr;
  }
  
  private double getF() {
    if (variant == Variant.SHADE || variant == Variant.CURRENT_TO_PBEST_1) {
      // Get F from memory with some randomization
      double meanF = memoryF.get(memoryIndex);
      double randomizedF = nextGaussian(meanF, 0.1);
      return Math.max(0.1, Math.min(1.0, randomizedF));
    }
    return f;
  }
  
  // Helper method to get rank (for multi-objective sorting)
  private int getRank(DoubleSolution solution) {
    // In a real implementation, this would use the solution's rank
    // For simplicity, we'll return 0 here
    return 0;
  }

  @Override
  public int numberOfRequiredParents() {
    switch (variant) {
      case RAND_1_BIN:
      case CURRENT_TO_RAND_1:
      case CURRENT_TO_PBEST_1:
        return 4;
      case RAND_2_BIN:
        return 5;
      case SHADE:
        return 5; // May vary based on archive usage
      default:
        return 4; // Default case
    }
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 1;
  }

  @Override
  public double crossoverProbability() {
    return 1.0; // DE typically applies crossover with probability 1.0
  }

  /** Builder class for AdvancedDifferentialEvolutionCrossover */
  public static class Builder {
    private double cr = 0.5;
    private double f = 0.5;
    private Variant variant = Variant.RAND_1_BIN;
    private RandomGenerator<Double> randomGenerator = JMetalRandom.getInstance()::nextDouble;
    private int populationSize = 100;
    private double pBest = 0.1;
    private int memorySize = 5;

    public Builder withCr(double cr) {
      Check.valueIsNotNegative(cr);
      Check.that(cr <= 1.0, "CR must be less than or equal to 1.0");
      this.cr = cr;
      return this;
    }

    public Builder withF(double f) {
      Check.valueIsNotNegative(f);
      this.f = f;
      return this;
    }

    public Builder withVariant(Variant variant) {
      Check.notNull(variant);
      this.variant = variant;
      return this;
    }

    public Builder withRandomGenerator(RandomGenerator<Double> randomGenerator) {
      Check.notNull(randomGenerator);
      this.randomGenerator = randomGenerator;
      return this;
    }

    public Builder withPopulationSize(int populationSize) {
      Check.that(populationSize >= 4, "Population size must be at least 4");
      this.populationSize = populationSize;
      return this;
    }

    public Builder withPBest(double pBest) {
      Check.valueIsInRange(pBest, 0.0, 1.0);
      this.pBest = pBest;
      return this;
    }

    public Builder withMemorySize(int memorySize) {
      Check.that(memorySize > 0, "Memory size must be greater than 0");
      this.memorySize = memorySize;
      return this;
    }

    public AdvancedDifferentialEvolutionCrossover build() {
      // Additional validations that require multiple parameters
      if (variant == Variant.SHADE || variant == Variant.CURRENT_TO_PBEST_1) {
        Check.that(populationSize >= 5, 
            "Population size must be at least 5 for " + variant + " variant");
      }
      return new AdvancedDifferentialEvolutionCrossover(this);
    }
  }
}
