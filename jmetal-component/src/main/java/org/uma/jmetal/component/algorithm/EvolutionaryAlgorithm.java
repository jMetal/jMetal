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
 * - The steps of the algorithm are carried out by objects (components)
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
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;
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
    attributes.put("COMPUTING_TIME", currentComputingTime());
  }

  protected void updateProgress() {
    evaluations += variation.getOffspringPopulationSize();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", currentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);

    totalComputingTime = currentComputingTime();
  }

  public long currentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }

  public int numberOfEvaluations() {
    return evaluations;
  }

  public long totalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public List<S> result() {
    return population;
  }

  public void updatePopulation(List<S> newPopulation) {
    this.population = newPopulation;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return "Evolutionary algorithm";
  }

  public Map<String, Object> attributes() {
    return attributes ;
  }

  public List<S> population() {
    return population ;
  }

  @Override
  public Observable<Map<String, Object>> observable() {
    return observable;
  }

  public void termination(Termination termination) {
    this.termination = termination ;
  }

  public Termination termination() {
    return termination ;
  }

  public void evaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation ;
  }

  public Evaluation<S> evaluation() {
    return evaluation;
  }
}
