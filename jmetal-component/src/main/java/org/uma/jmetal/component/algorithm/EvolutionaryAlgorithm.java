package org.uma.jmetal.component.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.observer.Observer;

/**
 * Template for evolutionary algorithms. Its mains features are:
 * - The step of the algorithm are objects (components)
 * - The algorithms are {@link ObservableEntity}, which can be observed by {@link Observer} objects.
 * - The {@link #observable} element is a map of  pairs (String, Object), which is initialized and
 *   updated by the {@link #initProgress()} and {@link #updateProgress()} methods.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S> Generic type representing the encoding of the solutions
 */
public class EvolutionaryAlgorithm<S extends Solution<?>>
    implements Algorithm<List<S>>, ObservableEntity<Map<String, Object>> {

  private List<S> population;
  private final Evaluation<S> evaluation;
  private final SolutionsCreation<S> createInitialPopulation;
  private final Termination termination;
  private final Selection<S> selection;
  private final Variation<S> variation;
  private final Replacement<S> replacement;
  private final Map<String, Object> attributes;
  private long initTime;
  private long totalComputingTime;
  private int evaluations;
  private final Observable<Map<String, Object>> observable;

  private final String name;

  /**
   * Constructor
   *
   * @param name                      Algorithm name
   * @param initialPopulationCreation
   * @param evaluation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public EvolutionaryAlgorithm(
      String name,
      SolutionsCreation<S> initialPopulationCreation,
      Evaluation<S> evaluation,
      Termination termination,
      Selection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this.name = name;
    this.createInitialPopulation = initialPopulationCreation;
    this.evaluation = evaluation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>("Evolutionary Algorithm");
    this.attributes = new HashMap<>();
  }

  public void run() {
    initTime = System.currentTimeMillis();

    population = createInitialPopulation.create();
    population = evaluation.evaluate(population);
    initProgress();
    while (!termination.isMet(attributes)) {
      List<S> matingPopulation = selection.select(population);
      List<S> offspringPopulation = variation.variate(population, matingPopulation);
      offspringPopulation = evaluation.evaluate(offspringPopulation);

      population = replacement.replace(population, offspringPopulation);
      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime;
  }

  protected void initProgress() {
    evaluations = population.size();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
  }

  protected void updateProgress() {
    evaluations += variation.getOffspringPopulationSize();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);

    totalComputingTime = getCurrentComputingTime();
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }

  public int getNumberOfEvaluations() {
    return evaluations;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public List<S> getResult() {
    return population;
  }

  public void updatePopulation(List<S> newPopulation) {
    this.population = newPopulation;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "Evolutionary algorithm";
  }

  public Evaluation<S> getEvaluation() {
    return evaluation;
  }

  public Map<String, Object> getAttributes() {
    return attributes ;
  }

  public List<S> getPopulation() {
    return population ;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
