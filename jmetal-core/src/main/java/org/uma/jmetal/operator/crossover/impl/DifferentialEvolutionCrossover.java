package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Differential evolution crossover operator
 *
 * @author Antonio J. Nebro
 *     <p>Comments: - The operator receives two parameters: the current individual and an array of
 *     three parent individuals - The best and rand variants depends on the third parent, according
 *     whether it represents the current of the "best" individual or a random one. The
 *     implementation of both variants are the same, due to that the parent selection is external to
 *     the crossover operator. - Implemented variants: - rand/1/bin (best/1/bin) - rand/1/exp
 *     (best/1/exp) - current-to-rand/1 (current-to-best/1) - current-to-rand/1/bin
 *     (current-to-best/1/bin) - current-to-rand/1/exp (current-to-best/1/exp)
 */
@SuppressWarnings("serial")
public class DifferentialEvolutionCrossover implements CrossoverOperator<DoubleSolution> {
  public enum DE_VARIANT {
    RAND_1_BIN,
    RAND_1_EXP,
    RAND_2_BIN,
    RAND_2_EXP,
    BEST_1_BIN,
    BEST_1_EXP,
    BEST_2_BIN,
    BEST_2_EXP,
    RAND_TO_BEST_1_BIN,
    RAND_TO_BEST_1_EXP,
    CURRENT_TO_RAND_1_BIN,
    CURRENT_TO_RAND_1_EXP
  }

  public enum DE_CROSSOVER_TYPE {
    BIN,
    EXP
  }

  public enum DE_MUTATION_TYPE {
    RAND,
    BEST,
    RAND_TO_BEST,
    CURRENT_TO_RAND
  }

  private static final DE_VARIANT DEFAULT_DE_VARIANT = DE_VARIANT.RAND_1_BIN;

  private static final double DEFAULT_CR = 0.5;
  private static final double DEFAULT_F = 0.5;

  private double cr;
  private double f;

  private int numberOfDifferenceVectors = 1;
  private DE_CROSSOVER_TYPE crossoverType = DE_CROSSOVER_TYPE.BIN;
  private DE_MUTATION_TYPE mutationType = DE_MUTATION_TYPE.RAND;

  private DE_VARIANT variant;

  private DoubleSolution currentSolution = null;
  private DoubleSolution bestSolution = null;

  private BoundedRandomGenerator<Integer> jRandomGenerator;
  private BoundedRandomGenerator<Double> crRandomGenerator;

  private RepairDoubleSolution solutionRepair;

  /** Constructor */
  public DifferentialEvolutionCrossover() {
    this(DEFAULT_CR, DEFAULT_F, DEFAULT_DE_VARIANT);
  }

  /**
   * Constructor
   *
   * @param cr
   * @param f
   * @param variant
   */
  public DifferentialEvolutionCrossover(double cr, double f, DE_VARIANT variant) {
    this(
        cr,
        f,
        variant,
        (a, b) -> JMetalRandom.getInstance().nextInt(a, b),
        (a, b) -> JMetalRandom.getInstance().nextDouble(a, b));
  }

  /**
   * Constructor
   *
   * @param cr
   * @param f
   * @param variant
   * @param randomGenerator
   */
  public DifferentialEvolutionCrossover(
      double cr, double f, DE_VARIANT variant, RandomGenerator<Double> randomGenerator) {
    this(
        cr,
        f,
        variant,
        BoundedRandomGenerator.fromDoubleToInteger(randomGenerator),
        BoundedRandomGenerator.bound(randomGenerator));
  }

  /**
   * Constructor
   *
   * @param cr
   * @param f
   * @param variant
   * @param jRandomGenerator
   * @param crRandomGenerator
   */
  public DifferentialEvolutionCrossover(
      double cr,
      double f,
      DE_VARIANT variant,
      BoundedRandomGenerator<Integer> jRandomGenerator,
      BoundedRandomGenerator<Double> crRandomGenerator) {
    this.cr = cr;
    this.f = f;
    this.variant = variant;

    analyzeVariant(variant);

    this.jRandomGenerator = jRandomGenerator;
    this.crRandomGenerator = crRandomGenerator;

    solutionRepair = new RepairDoubleSolutionWithBoundValue();
  }

  private void analyzeVariant(DE_VARIANT variant) {
    switch (variant) {
      case RAND_1_BIN:
      case RAND_1_EXP:
      case BEST_1_BIN:
      case BEST_1_EXP:
      case RAND_TO_BEST_1_BIN:
      case RAND_TO_BEST_1_EXP:
      case CURRENT_TO_RAND_1_BIN:
      case CURRENT_TO_RAND_1_EXP:
        numberOfDifferenceVectors = 1;
        break;
      case RAND_2_BIN:
      case RAND_2_EXP:
      case BEST_2_BIN:
      case BEST_2_EXP:
        numberOfDifferenceVectors = 2;
        break;
      default:
        throw new JMetalException("DE variant type invalid: " + variant);
    }

    switch (variant) {
      case RAND_1_BIN:
      case BEST_1_BIN:
      case RAND_TO_BEST_1_BIN:
      case CURRENT_TO_RAND_1_BIN:
      case RAND_2_BIN:
      case BEST_2_BIN:
        crossoverType = DE_CROSSOVER_TYPE.BIN;
        break;
      case RAND_1_EXP:
      case BEST_1_EXP:
      case RAND_TO_BEST_1_EXP:
      case CURRENT_TO_RAND_1_EXP:
      case RAND_2_EXP:
      case BEST_2_EXP:
        crossoverType = DE_CROSSOVER_TYPE.EXP;
        break;
      default:
        throw new JMetalException("DE crossover type invalid: " + variant);
    }

    switch (variant) {
      case RAND_1_BIN:
      case RAND_1_EXP:
      case RAND_2_BIN:
      case RAND_2_EXP:
        mutationType = DE_MUTATION_TYPE.RAND;
        break;
      case BEST_1_BIN:
      case BEST_1_EXP:
      case BEST_2_BIN:
      case BEST_2_EXP:
        mutationType = DE_MUTATION_TYPE.BEST;
        break;
      case CURRENT_TO_RAND_1_BIN:
      case CURRENT_TO_RAND_1_EXP:
        mutationType = DE_MUTATION_TYPE.CURRENT_TO_RAND;
        break;
      case RAND_TO_BEST_1_BIN:
      case RAND_TO_BEST_1_EXP:
        mutationType = DE_MUTATION_TYPE.RAND_TO_BEST;
        break;
      default:
        throw new JMetalException("DE mutation type invalid: " + variant);
    }
  }

  /* Getters */
  public double getCr() {
    return cr;
  }

  public double getF() {
    return f;
  }

  public DE_VARIANT getVariant() {
    return variant;
  }

  public int getNumberOfDifferenceVectors() {
    return numberOfDifferenceVectors;
  }

  public DE_CROSSOVER_TYPE getCrossoverType() {
    return crossoverType;
  }

  public DE_MUTATION_TYPE getMutationType() {
    return mutationType;
  }

  public int getNumberOfRequiredParents() {
    return 1 + numberOfDifferenceVectors * 2;
  }

  public int getNumberOfGeneratedChildren() {
    return 1;
  }

  public double getCrossoverProbability() {
    return 1.0;
  }

  /* Setters */
  public void setCurrentSolution(DoubleSolution current) {
    this.currentSolution = current;
  }

  public void setBestSolution(DoubleSolution bestSolution) {
    this.bestSolution = bestSolution;
  }

  public void setCr(double cr) {
    this.cr = cr;
  }

  public void setF(double f) {
    this.f = f;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> parentSolutions) {
    DoubleSolution child = (DoubleSolution) currentSolution.copy();

    int numberOfVariables = parentSolutions.get(0).getNumberOfVariables();
    int jrand = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);

    Double[][] parent = new Double[getNumberOfRequiredParents()][];

    IntStream.range(0, getNumberOfRequiredParents())
        .forEach(
            i -> {
              parent[i] = new Double[numberOfVariables];
              parentSolutions.get(i).getVariables().toArray(parent[i]);
            });

    if (crossoverType.equals(DE_CROSSOVER_TYPE.BIN)) {
      for (int j = 0; j < numberOfVariables; j++) {
        if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
          double value = mutate(parent, j);

          child.setVariable(j, value);
        }
      }
    } else if (crossoverType.equals(DE_CROSSOVER_TYPE.EXP)) {
      int j = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);
      int l = 0;

      do {
        double value = mutate(parent, j);

        child.setVariable(j, value);

        j = (j + 1) % numberOfVariables;
        l++;
      } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
    }

    /*

       if (DE_VARIANT.RAND_1_BIN.equals(variant)) {
         for (int j = 0; j < numberOfVariables; j++) {
           if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
             double value = parent[2][j] + f * (parent[0][j] - parent[1][j]);

             child.setVariable(j, value);
           }
         }
       } else if (DE_VARIANT.BEST_1_BIN.equals(variant)) {
         for (int j = 0; j < numberOfVariables; j++) {
           if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
             double value = bestSolution.getVariable(j) + f * (parent[0][j] - parent[1][j]);

             child.setVariable(j, value);
           }
         }
       } else if (DE_VARIANT.RAND_2_BIN.equals(variant)) {
         for (int j = 0; j < numberOfVariables; j++) {
           if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
             double value =
                 parent[4][j] + f * (parent[0][j] - parent[1][j]) + f * (parent[2][j] - parent[3][j]);

             child.setVariable(j, value);
           }
         }
       } else if (DE_VARIANT.RAND_1_EXP.equals(variant)) {
         int k = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);
         int l = 0;

         do {
           double value = parent[2][k] + f * (parent[0][k] - parent[1][k]);

           child.setVariable(k, value);

           k = (k + 1) % numberOfVariables;
           l++;
         } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
       } else if (DE_VARIANT.BEST_1_EXP.equals(variant)) {
         int k = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);
         int l = 0;

         do {
           double value = bestSolution.getVariable(k) + f * (parent[0][k] - parent[1][k]);

           child.setVariable(k, value);

           k = (k + 1) % numberOfVariables;
           l++;
         } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
       } else if (DE_VARIANT.RAND_2_EXP.equals(variant)) {
         int k = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);
         int l = 0;

         do {
           double value =
               parent[4][k] + f * (parent[0][k] - parent[1][k] + f * (parent[2][k] - parent[3][k]));

           child.setVariable(k, value);

           k = (k + 1) % numberOfVariables;
           l++;
         } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
       } else if (DE_VARIANT.CURRENT_TO_RAND_1_BIN.equals(variant)
           || "current-to-best/1/bin".equals(variant)) {
         for (int j = 0; j < numberOfVariables; j++) {
           if (crRandomGenerator.getRandomValue(0.0, 1.0) < cr || j == jrand) {
             double value;
             value =
                 currentSolution.getVariable(j)
                     + f * (parent[2][j] - currentSolution.getVariable(j))
                     + f * (parent[0][j] - parent[1][j]);

             child.setVariable(j, value);
           }
         }
       } else if (DE_VARIANT.CURRENT_TO_RAND_1_EXP.equals(variant)
           || "current-to-best/1/exp".equals(variant)) {
         int k = jRandomGenerator.getRandomValue(0, numberOfVariables - 1);
         int l = 0;

         do {
           double value =
               currentSolution.getVariable(k)
                   + f * (parent[2][k] - currentSolution.getVariable(k))
                   + f * (parent[0][k] - parent[1][k]);

           k = (k + 1) % numberOfVariables;
           l++;
         } while ((crRandomGenerator.getRandomValue(0.0, 1.0) < cr) && (l < numberOfVariables));
       } else {
         JMetalLogger.logger.severe(
             "DifferentialEvolutionCrossover.execute: " + " unknown DE variant (" + variant + ")");
         Class<String> cls = String.class;
         String name = cls.getName();
         throw new JMetalException("Exception in " + name + ".execute()");
       }
    */

    repairVariableValues(child);

    List<DoubleSolution> result = new ArrayList<>(1);
    result.add(child);
    return result;
  }

  private void repairVariableValues(DoubleSolution solution) {
    IntStream.range(0, solution.getNumberOfVariables())
        .forEach(
            i ->
                solution.setVariable(
                    i,
                    solutionRepair.repairSolutionVariableValue(
                        solution.getVariable(i),
                        solution.getLowerBound(i),
                        solution.getUpperBound(i))));
  }

  private double mutate(Double[][] parent, int index) {
    double value = 0;
    if (mutationType.equals(DE_MUTATION_TYPE.RAND)) {
      value = randMutation(parent, index, numberOfDifferenceVectors);
    } else if (mutationType.equals(DE_MUTATION_TYPE.BEST)) {
      value = bestMutation(parent, index, numberOfDifferenceVectors);
    } else if (mutationType.equals(DE_MUTATION_TYPE.RAND_TO_BEST)) {
      value = bestRandToBestMutation(parent, index);
    }

    return value;
  }

  private double randMutation(Double[][] parent, int index, int numberOfDifferenceVectors) {
    if (numberOfDifferenceVectors == 1) {
      return parent[2][index] + f * (parent[0][index] - parent[1][index]);
    } else if (numberOfDifferenceVectors == 2) {
      return parent[4][index]
          + f * (parent[0][index] - parent[1][index])
          + f * (parent[2][index] - parent[3][index]);
    } else {
      throw new JMetalException(
          "Number of difference vectors invalid: " + numberOfDifferenceVectors);
    }
  }

  private double bestMutation(Double[][] parent, int index, int numberOfDifferenceVectors) {
    Check.isNotNull(bestSolution) ;
    if (numberOfDifferenceVectors == 1) {
      return bestSolution.getVariable(index) + f * (parent[0][index] - parent[1][index]);
    } else if (numberOfDifferenceVectors == 2) {
      return bestSolution.getVariable(index)
          + f * (parent[0][index] - parent[1][index])
          + f * (parent[2][index] - parent[3][index]);
    } else {
      throw new JMetalException(
          "Number of difference vectors invalid: " + numberOfDifferenceVectors);
    }
  }

  private double bestRandToBestMutation(Double[][] parent, int index) {
    Check.isNotNull(bestSolution) ;
    Check.isNotNull(currentSolution) ;
    return currentSolution.getVariable(index)
        + f * (bestSolution.getVariable(index) - currentSolution.getVariable(index))
        + f * (parent[0][index] - parent[1][index]);
  }
}
