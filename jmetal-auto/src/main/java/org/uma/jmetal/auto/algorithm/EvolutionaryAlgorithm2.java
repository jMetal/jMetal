package org.uma.jmetal.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.component.createinitialsolutions.CreateInitialSolutions;
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

public class EvolutionaryAlgorithm2<S extends Solution<?>> implements Algorithm<List<S>>, ObservableEntity {
  private Evaluation<S> evaluation;
  private CreateInitialSolutions<S> createInitialSolutionList;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  protected List<S> population;

  protected long initTime;
  protected long totalComputingTime;
  protected int evaluations;
  protected Observable<Map<String, Object>> observable;
  protected Map<String, Object> attributes;

  private final String name ;

  public EvolutionaryAlgorithm2(
          String name,
      Evaluation<S> evaluation,
      CreateInitialSolutions<S> createInitialSolutionList,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement) {
    this.name = name ;
    this.evaluation = evaluation;
    this.createInitialSolutionList = createInitialSolutionList;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>("Evolutionary Algorithm") ;
  }

  public void run() {
    population = createInitialSolutionSet();
    population = evaluateSolutions(population);
    initProgress();
    while (!stoppingConditionIsMet()) {
      step();
      updateProgress();
    }
  }

  @Override
  protected List<S> createInitialSolutionSet() {
    return createInitialSolutionList.create();
  }

  @Override
  protected List<S> evaluateSolutions(List<S> solutions) {
    return evaluation.evaluate(solutions);
  }

  @Override
  protected void initProgress() {
    evaluations = population.size() ;

    attributes = new HashMap<>() ;
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
  }

  @Override
  protected void updateProgress() {
    evaluations += variation.getOffspringPopulationSize() ;

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  protected boolean stoppingConditionIsMet() {
    return termination.isMet(attributes);
  }

  @Override
  protected void step() {
    List<S> matingSolutions = selection.select(population) ;
    List<S> offspringPopulation = variation.variate(population, matingSolutions) ;
    offspringPopulation = evaluateSolutions(offspringPopulation) ;

    population = replacement.replace(population, offspringPopulation) ;
  }


  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }

  public int getEvaluations() {
    return evaluations;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public List<S> getResult() {
    return population;
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
    return evaluation ;
  }
}
