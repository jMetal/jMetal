package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing an evolutionary algorithm. It implements the {@link Algorithm} interface by applying a
 * component based approach, in which each step of an evolutionary algorithm (i.e, selection, variation,
 * replacement, etc.) is considered as a component that can be set in the class constructor.
 *
 * @param <S> Solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ComponentBasedEvolutionaryAlgorithm<S extends Solution<?>>  extends AbstractEvolutionaryAlgorithm<S, List<S>> {
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

  private String name ;

  public ComponentBasedEvolutionaryAlgorithm(String name,
                                             Problem<S> problem,
                                             Evaluation<S> evaluation,
                                             InitialSolutionsCreation<S> initialPopulationCreation,
                                             Termination termination,
                                             MatingPoolSelection<S> selection,
                                             Variation<S> variation,
                                             Replacement<S> replacement) {
    this.name = name ;
    this.problem = problem ;

    this.evaluation = evaluation;
    this.createInitialPopulation = initialPopulationCreation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis() ;
    super.run();
    totalComputingTime = System.currentTimeMillis() - initTime ;
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
    return evaluation.evaluate(population, problem);
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
    return population;
  }

  @Override
  public String getName() {
    return name ;
  }

  @Override
  public String getDescription() {
    return name;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public long getTotalComputingTime() {
    return totalComputingTime ;
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }
}
