package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class EvolutionaryAlgorithm<S extends Solution<?>>
    implements Algorithm<List<S>>, ObservableEntity {
  private List<S> population;
  private Archive<S> externalArchive;

  private Evaluation<S> evaluation;
  private InitialSolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  private Map<String, Object> attributes;

  private long initTime;
  private long totalComputingTime;
  private int evaluations;
  private Observable<Map<String, Object>> observable;

  private final String name;

  /**
   * Constructor
   *
   * @param name Algorithm name
   * @param evaluation
   * @param initialPopulationCreation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   * @param externalArchive
   */
  public EvolutionaryAlgorithm(
      String name,
      Evaluation<S> evaluation,
      InitialSolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement,
      Archive<S> externalArchive) {
    this.name = name;
    this.evaluation = evaluation;
    this.createInitialPopulation = initialPopulationCreation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;
    this.externalArchive = externalArchive;

    this.observable = new DefaultObservable<>("Evolutionary Algorithm");
    this.attributes = new HashMap<>();
  }

  /**
   * Constructor
   *
   * @param name Algorithm name
   * @param evaluation
   * @param initialPopulationCreation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public EvolutionaryAlgorithm(
      String name,
      Evaluation<S> evaluation,
      InitialSolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this(
        name,
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement,
        null);
  }

  public void run() {
    initTime = System.currentTimeMillis() ;

    population = createInitialPopulation.create();
    population = evaluation.evaluate(population);
    initProgress();
    while (!termination.isMet(attributes)) {
      List<S> matingPopulation = selection.select(population);
      List<S> offspringPopulation = variation.variate(population, matingPopulation);
      offspringPopulation = evaluation.evaluate(offspringPopulation);
      updateArchive(offspringPopulation);

      population = replacement.replace(population, offspringPopulation);
      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime ;
  }


  private void updateArchive(List<S> population) {
    if (externalArchive != null) {
      for (S solution : population) {
        externalArchive.add(solution);
      }
    }
  }

  protected void initProgress() {
    evaluations = population.size();

    updateArchive(population);

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

    totalComputingTime = getCurrentComputingTime() ;
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
    if (externalArchive != null) {
      return externalArchive.getSolutionList();
    } else {
      return population;
    }
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

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
