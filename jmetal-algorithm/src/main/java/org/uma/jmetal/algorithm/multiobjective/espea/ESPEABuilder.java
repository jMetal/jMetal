package org.uma.jmetal.algorithm.multiobjective.espea;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.espea.util.EnergyArchive.ReplacementStrategy;
import org.uma.jmetal.algorithm.multiobjective.espea.util.ScalarizationWrapper;
import org.uma.jmetal.algorithm.multiobjective.espea.util.ScalarizationWrapper.ScalarizationType;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

public class ESPEABuilder<S extends Solution<?>> implements AlgorithmBuilder<ESPEA<S>> {

  private final Problem<S> problem;
  private int maxEvaluations;
  private int populationSize;
  private CrossoverOperator<S> crossoverOperator;
  private CrossoverOperator<S> fullArchiveCrossoverOperator;
  private MutationOperator<S> mutationOperator;
  private SelectionOperator<List<S>, S> selectionOperator;
  private SolutionListEvaluator<S> evaluator;
  private ScalarizationWrapper scalarization;
  private boolean normalizeObjectives;
  private ReplacementStrategy replacementStrategy;

  public ESPEABuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
    this.problem = problem;
    this.maxEvaluations = 25000;
    this.populationSize = 100;
    this.crossoverOperator = crossoverOperator;
    this.fullArchiveCrossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = new RandomSelection<>();
    this.evaluator = new SequentialSolutionListEvaluator<>();
    this.scalarization = new ScalarizationWrapper(ScalarizationType.UNIFORM);
    this.normalizeObjectives = true;
    this.replacementStrategy = ReplacementStrategy.LARGEST_DIFFERENCE;
  }

  @Override
  public ESPEA<S> build() {
    return new ESPEA<>(problem, maxEvaluations, populationSize, crossoverOperator, fullArchiveCrossoverOperator, mutationOperator,
            selectionOperator, scalarization, evaluator, normalizeObjectives, replacementStrategy);
  }

  /**
   * @return the maxEvaluations
   */
  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  /**
   * @return the populationSize
   */
  public int getPopulationSize() {
    return populationSize;
  }

  /**
   * @return the crossoverOperator
   */
  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  /**
   * @return the fullArchiveCrossoverOperator
   */
  public CrossoverOperator<S> getFullArchiveCrossoverOperator() {
    return fullArchiveCrossoverOperator;
  }

  /**
   * @return the mutationOperator
   */
  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  /**
   * @return the selectionOperator
   */
  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }

  /**
   * @return the evaluator
   */
  public SolutionListEvaluator<S> getEvaluator() {
    return evaluator;
  }

  /**
   * @return the scalarization
   */
  public ScalarizationWrapper getScalarization() {
    return scalarization;
  }

  /**
   * @param maxEvaluations the maxEvaluations to set
   */
  public void setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;
  }

  /**
   * @param populationSize the populationSize to set
   */
  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  /**
   * @param crossoverOperator the crossoverOperator to set
   */
  public void setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;
  }

  /**
   * @param fullArchiveCrossoverOperator the fullArchiveCrossoverOperator to set
   */
  public void setFullArchiveCrossoverOperator(CrossoverOperator<S> fullArchiveCrossoverOperator) {
    this.fullArchiveCrossoverOperator = fullArchiveCrossoverOperator;
  }

  /**
   * @param mutationOperator the mutationOperator to set
   */
  public void setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator;
  }

  /**
   * @param selectionOperator the selectionOperator to set
   */
  public void setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    this.selectionOperator = selectionOperator;
  }

  /**
   * @param evaluator the evaluator to set
   */
  public void setEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator;
  }

  /**
   * @param scalarization the scalarization to set
   */
  public void setScalarization(ScalarizationWrapper scalarization) {
    this.scalarization = scalarization;
  }

  /**
   * @return the normalizeObjectives
   */
  public boolean isNormalizeObjectives() {
    return normalizeObjectives;
  }

  /**
   * @param normalizeObjectives the normalizeObjectives to set
   */
  public void setNormalizeObjectives(boolean normalizeObjectives) {
    this.normalizeObjectives = normalizeObjectives;
  }

  /**
   * @return the replacement strategy
   */
  public ReplacementStrategy getOperationType() {
    return replacementStrategy;
  }

  /**
   * @param replacementStrategy the replacement strategy to set
   */
  public void setReplacementStrategy(ReplacementStrategy replacementStrategy) {
    this.replacementStrategy = replacementStrategy;
  }
}
