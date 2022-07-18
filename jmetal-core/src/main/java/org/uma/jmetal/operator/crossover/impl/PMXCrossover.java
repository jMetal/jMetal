package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class allows to apply a PMX crossover operator using two parent solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PMXCrossover implements
    CrossoverOperator<PermutationSolution<Integer>> {
  private double crossoverProbability = 1.0;
  private BoundedRandomGenerator<Integer> cuttingPointRandomGenerator ;
  private RandomGenerator<Double> crossoverRandomGenerator ;

  /**
   * Constructor
   */
  public PMXCrossover(double crossoverProbability) {
	  this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
  }

  /**
   * Constructor
   */
  public PMXCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
	  this(crossoverProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /**
   * Constructor
   */
  public PMXCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
    if ((crossoverProbability < 0) || (crossoverProbability > 1)) {
      throw new JMetalException("Crossover probability value invalid: " + crossoverProbability) ;
    }
    this.crossoverProbability = crossoverProbability;
    this.crossoverRandomGenerator = crossoverRandomGenerator ;
    this.cuttingPointRandomGenerator = cuttingPointRandomGenerator ;
  }

  /* Getters */
  @Override
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  /* Setters */
  public void setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /**
   * Executes the operation
   *
   * @param parents An object containing an array of two solutions
   */
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> parents) {
    if (null == parents) {
      throw new JMetalException("Null parameter") ;
    } else if (parents.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + parents.size()) ;
    }

    return doCrossover(crossoverProbability, parents) ;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover probability
   * @param parents     Parents
   * @return An array containing the two offspring
   */
  public @NotNull List<PermutationSolution<Integer>> doCrossover(double probability, List<PermutationSolution<Integer>> parents) {
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);

    offspring.add((PermutationSolution<Integer>) parents.get(0).copy()) ;
    offspring.add((PermutationSolution<Integer>) parents.get(1).copy()) ;

    var permutationLength = parents.get(0).variables().size() ;

    if (crossoverRandomGenerator.getRandomValue() < probability) {

      // STEP 1: Get two cutting points
      int cuttingPoint1 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
      int cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
      while (cuttingPoint2 == cuttingPoint1)
        cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);

      if (cuttingPoint1 > cuttingPoint2) {
        var swap = cuttingPoint1;
        cuttingPoint1 = cuttingPoint2;
        cuttingPoint2 = swap;
      }

      // STEP 2: Get the subchains to interchange
      @NotNull int replacement2[] = new int[permutationLength];
      var arr = new int[10];
      var count = 0;
        for (var i1 = 0; i1 < permutationLength; i1++) {
          var i2 = replacement2[i1] = -1;
            if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
            arr[count++] = i2;
        }
        arr = Arrays.copyOfRange(arr, 0, count);
      var replacement1 = arr;

      // STEP 3: Interchange
      for (var i = cuttingPoint1; i <= cuttingPoint2; i++) {
        offspring.get(0).variables().set(i, parents.get(1).variables().get(i));
        offspring.get(1).variables().set(i, parents.get(0).variables().get(i));

        replacement1[parents.get(1).variables().get(i)] = parents.get(0).variables().get(i) ;
        replacement2[parents.get(0).variables().get(i)] = parents.get(1).variables().get(i) ;
      }

      // STEP 4: Repair offspring
      for (var i = 0; i < permutationLength; i++) {
        if ((i >= cuttingPoint1) && (i <= cuttingPoint2))
          continue;

        int n1 = parents.get(0).variables().get(i);
        var m1 = replacement1[n1];

        int n2 = parents.get(1).variables().get(i);
        var m2 = replacement2[n2];

        while (m1 != -1) {
          n1 = m1;
          m1 = replacement1[m1];
        }

        while (m2 != -1) {
          n2 = m2;
          m2 = replacement2[m2];
        }

        offspring.get(0).variables().set(i, n1);
        offspring.get(1).variables().set(i, n2);
      }
    }

    return offspring;
  }

  @Override
  public int getNumberOfRequiredParents() {
    return 2 ;
  }

  @Override
  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}
