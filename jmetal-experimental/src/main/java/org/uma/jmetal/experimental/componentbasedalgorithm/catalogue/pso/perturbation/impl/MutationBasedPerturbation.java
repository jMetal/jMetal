package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.List;
import java.util.stream.IntStream;

public class MutationBasedPerturbation implements Perturbation {
  private MutationOperator<DoubleSolution> mutationOperator ;
  private double percentageOfApplication ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   * @param mutationOperator
   * @param percentageOfApplication
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator, double percentageOfApplication) {
    this.mutationOperator = mutationOperator ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Constructor
   * @param mutationOperator
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator) {
    this(mutationOperator, 0.18) ;
  }

  @Override
  public List<DoubleSolution> perturbate(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      if (randomGenerator.nextDouble() < percentageOfApplication) {
        mutationOperator.execute(particle) ;
      }
    }
    return swarm;
  }

  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  public double getPercentageOfApplication() {
    return percentageOfApplication;
  }
}
