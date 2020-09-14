package org.uma.jmetal.experimental.parallel.asynchronous.algorithm;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.variation.Variation;
import org.uma.jmetal.experimental.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AsynchronousParallelEvolutionaryAlgorithm<S extends Solution<?>>
        implements AsynchronousParallelAlgorithm<ParallelTask<S>, List<S>> {
  protected Evaluation<S> evaluation;
  protected SolutionsCreation<S> initialSolutionsCreation;
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

  public AsynchronousParallelEvolutionaryAlgorithm(String name,
                                                   Evaluation<S> evaluation,
                                                   SolutionsCreation<S> initialPopulationCreation,
                                                   Termination termination,
                                                   MatingPoolSelection<S> selection,
                                                   Variation<S> variation,
                                                   Replacement<S> replacement) {
    this.name = name;

    this.evaluation = evaluation;
    this.initialSolutionsCreation = initialPopulationCreation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;

    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    this.archive = null ;
  }


  @Override
  public void submitInitialTasks(List<ParallelTask<S>> taskList) {

  }

  @Override
  public List<ParallelTask<S>> createInitialTasks() {
    return null;
  }

  @Override
  public ParallelTask<S> waitForComputedTask() {
    return null;
  }

  @Override
  public void processComputedTask(ParallelTask<S> task) {

  }

  @Override
  public void submitTask(ParallelTask<S> task) {

  }

  @Override
  public ParallelTask<S> createNewTask() {
    return null;
  }

  @Override
  public boolean thereAreInitialTasksPending(List<ParallelTask<S>> tasks) {
    return false;
  }

  @Override
  public ParallelTask<S> getInitialTask(List<ParallelTask<S>> tasks) {
    return null;
  }

  @Override
  public boolean stoppingConditionIsNotMet() {
    return false;
  }

  @Override
  public void initProgress() {

  }

  @Override
  public void updateProgress() {
  }

  @Override
  public List<S> getResult() {
    return null;
  }
}
