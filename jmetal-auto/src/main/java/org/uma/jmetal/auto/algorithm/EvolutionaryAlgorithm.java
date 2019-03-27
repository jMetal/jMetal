package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.util.observable.Observable;
import org.uma.jmetal.auto.util.observable.ObservableEntity;
import org.uma.jmetal.auto.util.observable.impl.DefaultObservable;
import org.uma.jmetal.solution.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvolutionaryAlgorithm<S extends Solution<?>>
    implements Algorithm<List<S>>, ObservableEntity {
  protected List<S> population;

  private Evaluation<S> evaluation;
  private InitialSolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  protected Map<String, Object> attributes;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;
  protected Observable<Map<String, Object>> observable;

  private final String name;

  public EvolutionaryAlgorithm(
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

    this.observable = new DefaultObservable<>("Evolutionary Algorithm");
    this.attributes = new HashMap<>();
  }

  public void run() {
    population = createInitialPopulation.create();
    population = evaluation.evaluate(population);
    initProgress();
    while (!termination.isMet(attributes)) {
      List<S> matingSolutions = selection.select(population);
      List<S> offspringPopulation = variation.variate(population, matingSolutions);
      offspringPopulation = evaluation.evaluate(offspringPopulation);

      population = replacement.replace(population, offspringPopulation);
      updateProgress();
    }
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
    this.population = newPopulation ;
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

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
