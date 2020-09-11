package org.uma.jmetal.experimental.parallel.asynchronous.algorithm.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.experimental.parallel.asynchronous.masterworker.Master;
import org.uma.jmetal.experimental.parallel.asynchronous.masterworker.Worker;
import org.uma.jmetal.experimental.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

import java.util.*;
import java.util.stream.IntStream;

public class AsynchronousMultithreadedMasterWorkerNSGAII<S extends Solution<?>>
    extends Master<ParallelTask<S>, List<S>> {
  private Problem<S> problem;
  private CrossoverOperator<S> crossover;
  private MutationOperator<S> mutation;
  private SelectionOperator<List<S>, S> selection;
  private Replacement<S> replacement;
  private Termination termination;

  private List<S> population = new ArrayList<>();
  private int populationSize;
  private int evaluations = 0;
  private long initTime;

  private Map<String, Object> attributes;
  private Observable<Map<String, Object>> observable;

  protected List<ParallelTask<S>> initialTaskList;

  private int numberOfCores;

  public AsynchronousMultithreadedMasterWorkerNSGAII(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      Termination termination) {
    super(numberOfCores);
    this.problem = problem;
    this.crossover = crossover;
    this.mutation = mutation;
    this.populationSize = populationSize;
    this.termination = termination;

    selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

    replacement =
        new RankingAndDensityEstimatorReplacement<S>(
            new MergeNonDominatedSortRanking<S>(),
            new CrowdingDistanceDensityEstimator<S>(),
            Replacement.RemovalPolicy.oneShot);

    attributes = new HashMap<>();
    observable = new DefaultObservable<>("Asynchronous NSGAII observable");

    this.initialTaskList = new LinkedList<>();

    this.numberOfCores = numberOfCores;

    IntStream.range(0, numberOfCores).forEach(i -> new Worker<>(
            (task) -> {
              problem.evaluate(task.getContents());
              return ParallelTask.create(1, task.getContents());
            },
            pendingTaskQueue,
            completedTaskQueue).start());
  }

  @Override
  protected void initProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  protected void updateProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  private void createInitialSolutions() {
    List<S> initialPopulation = new ArrayList<>();
    IntStream.range(0, populationSize)
        .forEach(i -> initialPopulation.add(problem.createSolution()));
    initialPopulation.forEach(
        solution -> {
          int taskId = JMetalRandom.getInstance().nextInt(0, 1000);
          initialTaskList.add(ParallelTask.create(taskId, solution));
        });
  }

  @Override
  protected void submitInitialTaskList() {
    if (initialTaskList.size() >= numberOfCores) {
      initialTaskList.forEach(this::submitTask);
    } else {
      int idleWorkers = numberOfCores - initialTaskList.size();
      initialTaskList.forEach(this::submitTask);
      while (idleWorkers > 0) {
        submitTask(createNewTask());
        idleWorkers--;
      }
    }
  }

  @Override
  protected ParallelTask<S> waitForComputedTask() {
    ParallelTask<S> evaluatedTask = null;
    try {
      evaluatedTask = completedTaskQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return evaluatedTask;
  }

  @Override
  protected void processComputedTask(ParallelTask<S> task) {
    evaluations++;
    if (population.size() < populationSize) {
      population.add(task.getContents());
    } else {
      List<S> offspringPopulation = new ArrayList<>(1);
      offspringPopulation.add(task.getContents());

      population = replacement.replace(population, offspringPopulation);
      Check.that(population.size() == populationSize, "The population size is incorrect");
    }
  }

  @Override
  protected void submitTask(ParallelTask<S> task) {
    pendingTaskQueue.add(task);
  }

  @Override
  protected ParallelTask<S> createNewTask() {
    if (population.size() > 2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(selection.execute(population));
      parents.add(selection.execute(population));

      List<S> offspring = crossover.execute(parents);

      mutation.execute(offspring.get(0));

      return ParallelTask.create(1, offspring.get(0));
    } else {
      return ParallelTask.create(0, problem.createSolution());
    }
  }

  @Override
  protected boolean thereAreInitialTasksPending() {
    return initialTaskList.size() > 0;
  }

  @Override
  protected ParallelTask<S> getInitialTask() {
    ParallelTask<S> initialTask = initialTaskList.get(0);
    initialTaskList.remove(0);
    return initialTask;
  }

  @Override
  protected boolean stoppingConditionIsNotMet() {
    return !termination.isMet(attributes);
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();

    createInitialSolutions();
    super.run();
  }

  @Override
  public List<S> getResult() {
    return population;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
