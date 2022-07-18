package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMSEMOABuilder<S extends Solution<?>> implements AlgorithmBuilder<SMSEMOA<S>> {
  private static final double DEFAULT_OFFSET = 100.0 ;

  protected Problem<S> problem;

  protected int populationSize;
  protected int maxEvaluations;

  protected CrossoverOperator<S> crossoverOperator;
  protected MutationOperator<S> mutationOperator;
  protected SelectionOperator<List<S>, S> selectionOperator;

  protected double offset ;

  protected Hypervolume<S> hypervolumeImplementation;
  protected Comparator<S> dominanceComparator ;

  public SMSEMOABuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem ;
    this.offset = DEFAULT_OFFSET ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;
    this.hypervolumeImplementation = new PISAHypervolume<>() ;
    hypervolumeImplementation.setOffset(offset);

    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    this.selectionOperator = new RandomSelection<S>() ;
    this.dominanceComparator = new DominanceWithConstraintsComparator<>()  ;
  }

  public @NotNull SMSEMOABuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public SMSEMOABuilder<S> setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public @NotNull SMSEMOABuilder<S> setCrossoverOperator(CrossoverOperator<S> crossover) {
    crossoverOperator = crossover ;

    return this ;
  }

  public SMSEMOABuilder<S> setMutationOperator(MutationOperator<S> mutation) {
    mutationOperator = mutation ;

    return this ;
  }

  public @NotNull SMSEMOABuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selection) {
    selectionOperator = selection ;

    return this ;
  }

  public SMSEMOABuilder<S> setHypervolumeImplementation(Hypervolume<S> hypervolumeImplementation) {
    this.hypervolumeImplementation = hypervolumeImplementation;

    return this ;
  }


  public @NotNull SMSEMOABuilder<S> setOffset(double offset) {
    this.offset = offset ;

    return this ;
  }

  public SMSEMOABuilder<S> setDominanceComparator(@Nullable Comparator<S> dominanceComparator) {
    if (dominanceComparator == null) {
      throw new JMetalException("dominanceComparator is null");
    }
    this.dominanceComparator = dominanceComparator ;

    return this;
  }

  @Override public SMSEMOA<S> build() {
    return new SMSEMOA<S>(problem, maxEvaluations, populationSize, offset,
        crossoverOperator, mutationOperator, selectionOperator, dominanceComparator,
        hypervolumeImplementation);
  }

  /*
   * Getters
   */

  public Problem<S> getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }

  public double getOffset() {
    return offset;
  }
}