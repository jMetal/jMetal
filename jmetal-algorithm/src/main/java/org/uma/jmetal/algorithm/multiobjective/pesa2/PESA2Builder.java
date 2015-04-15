package org.uma.jmetal.algorithm.multiobjective.pesa2;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Created by Antonio J. Nebro
 */
public class PESA2Builder<S extends Solution> implements AlgorithmBuilder {
  private final Problem<S> problem;
  private int maxEvaluations ;
  private int archiveSize ;
  private int populationSize ;
  private int biSections ;
  private CrossoverOperator<List<S>, List<S>> crossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SolutionListEvaluator evaluator;

  /**
   * Constructor
   */
  public PESA2Builder(Problem problem, CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem;
    maxEvaluations = 250;
    populationSize = 100;
    archiveSize = 100 ;
    biSections = 5 ;
    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;

    evaluator = new SequentialSolutionListEvaluator();
  }

  public PESA2Builder setMaxEvaluations(int maxEvaluations) {
    if (maxEvaluations < 0) {
      throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
    }
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public PESA2Builder setArchiveSize(int archiveSize) {
    if (archiveSize < 0) {
      throw new JMetalException("archiveSize is negative: " + maxEvaluations);
    }
    this.archiveSize = archiveSize;

    return this;
  }

  public PESA2Builder setBisections(int biSections) {
    if (biSections < 0) {
      throw new JMetalException("biSections is negative: " + maxEvaluations);
    }
    this.biSections = biSections;

    return this;
  }

  public PESA2Builder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new JMetalException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  public PESA2Builder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
    if (evaluator == null) {
      throw new JMetalException("evaluator is null");
    }
    this.evaluator = evaluator;

    return this;
  }

  public Algorithm build() {
    Algorithm algorithm  ;
    algorithm = new PESA2<S>(problem, maxEvaluations, populationSize, archiveSize, biSections,
        crossoverOperator, mutationOperator, evaluator);
    
    return algorithm ;
  }

  /* Getters */
  public Problem getProblem() {
    return problem;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public CrossoverOperator getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator getMutationOperator() {
    return mutationOperator;
  }

  public SolutionListEvaluator getSolutionListEvaluator() {
    return evaluator;
  }

  public int getBiSections() {
    return biSections ;
  }

  public int getArchiveSize() {
    return archiveSize ;
  }
}
