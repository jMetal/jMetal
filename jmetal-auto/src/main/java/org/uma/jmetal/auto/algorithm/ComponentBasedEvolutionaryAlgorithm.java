package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an evolutionary algorithm. It implements the {@link Algorithm} interface by
 * applying a component based approach, in which each step of an evolutionary algorithm (i.e,
 * selection, variation, replacement, etc.) is considered as a component that can be set in the
 * class constructor.
 *
 * @param <S> Solution
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ComponentBasedEvolutionaryAlgorithm<S extends Solution<?>>
    extends AbstractEvolutionaryAlgorithm<S, List<S>> {
  protected Evaluation<S> evaluation;
  protected InitialSolutionsCreation<S> createInitialPopulation;
  protected Termination termination;
  protected MatingPoolSelection<S> selection;
  protected Variation<S> variation;
  protected Replacement<S> replacement;

  protected Map<String, Object> attributes;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;

  protected Observable<Map<String, Object>> observable;

  protected String name;
  protected Archive<S> archive ;
  /**
   * Constructor
   *
   * @param name
   * @param evaluation
   * @param initialPopulationCreation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public ComponentBasedEvolutionaryAlgorithm(
      String name,
      Evaluation<S> evaluation,
      InitialSolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this.name = name;

    this.evaluation = evaluation;
    this.createInitialPopulation = initialPopulationCreation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    this.archive = null ;
  }

  /**
   * Empty constructor that creates an empty instance. It is intended to allow the definition of
   * different subclass constructors. It is up to the developer the correct creation of the
   * algorithm components.
   */
  public ComponentBasedEvolutionaryAlgorithm() {}

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    super.run();
    totalComputingTime = System.currentTimeMillis() - initTime;
  }

  @Override
  protected void initProgress() {
    evaluations = population.size();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  protected void updateProgress() {
    evaluations += variation.getOffspringPopulationSize();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return this.termination.isMet(attributes);
  }

  @Override
  protected List<S> createInitialPopulation() {
    return createInitialPopulation.create();
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    var solutionList = evaluation.evaluate(population) ;
    if (null != archive) {
      solutionList.forEach(archive::add);
    }

    return solutionList ;
  }

  @Override
  protected List<S> selection(List<S> population) {
    return selection.select(population);
  }

  @Override
  protected List<S> reproduction(List<S> matingPool) {
    return variation.variate(population, matingPool);
  }

  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    return replacement.replace(population, offspringPopulation);
  }

  @Override
  public List<S> getResult() {
    return null == archive ? population: archive.getSolutionList() ;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return name;
  }

  public int getEvaluations() {
    return evaluations;
  }

  public Archive<S> getArchive() {
    return archive ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withArchive(Archive<S> archive) {
    this.archive = archive ;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withCreateInitialPopulation(InitialSolutionsCreation<S> createInitialPopulation) {
    this.createInitialPopulation = createInitialPopulation;

    return this ;
  }


  public ComponentBasedEvolutionaryAlgorithm<S> withTermination(Termination termination) {
    this.termination = termination;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withSelection(MatingPoolSelection<S> selection) {
    this.selection = selection;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withVariation(Variation<S> variation) {
    this.variation = variation;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withReplacement(Replacement<S> replacement) {
    this.replacement = replacement;

    return this ;
  }

  public ComponentBasedEvolutionaryAlgorithm<S> withName(String newName) {
    this.name = newName ;

    return this ;
  }

  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }
}
