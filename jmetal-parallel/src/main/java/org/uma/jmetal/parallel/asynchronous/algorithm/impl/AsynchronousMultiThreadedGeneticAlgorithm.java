package org.uma.jmetal.parallel.asynchronous.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.parallel.asynchronous.multithreaded.Master;
import org.uma.jmetal.parallel.asynchronous.multithreaded.Worker;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

public class AsynchronousMultiThreadedGeneticAlgorithm<S extends Solution<?>>
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

  private int numberOfCores;
  private final AtomicLong nextTaskIdentifier = new AtomicLong();

  public AsynchronousMultiThreadedGeneticAlgorithm(
      int numberOfCores,
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      SelectionOperator<List<S>, S> selection,
      Replacement<S> replacement,
      Termination termination) {
    super(numberOfCores);
    this.problem = problem;
    this.crossover = crossover;
    this.mutation = mutation;
    this.populationSize = populationSize;
    this.termination = termination;
    this.selection = selection ;
    this.replacement = replacement ;

    attributes = new HashMap<>();
    observable = new DefaultObservable<>("Observable");

    this.numberOfCores = numberOfCores;

    createWorkers(numberOfCores, problem);
  }

  private void createWorkers(int numberOfCores, Problem<S> problem) {
    IntStream.range(0, numberOfCores)
        .forEach(
            i -> {
              Worker<ParallelTask<S>> worker =
                  new Worker<>(
                      (task) -> {
                        problem.evaluate(task.getContents());
                        return ParallelTask.create(task.getIdentifier(), task.getContents());
                      },
                      pendingTaskQueue,
                      completedTaskQueue);
              register(worker);
              worker.start();
            });
  }

  /** The identifiers of the tasks are consecutive numbers, so none is repeated. */
  private long createTaskIdentifier() {
    return nextTaskIdentifier.getAndIncrement();
  }

  @Override
  public void initProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  public void updateProgress() {
    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", population);
    attributes.put("COMPUTING_TIME", System.currentTimeMillis() - initTime);
    attributes.put("BEST_SOLUTION", population.get(0));

    observable.setChanged();
    observable.notifyObservers(attributes);
  }

  @Override
  public List<ParallelTask<S>> createInitialTasks() {
    List<S> initialPopulation = new ArrayList<>();
    List<ParallelTask<S>> initialTaskList = new ArrayList<>() ;
    IntStream.range(0, populationSize)
        .forEach(i -> initialPopulation.add(problem.createSolution()));
    initialPopulation.forEach(
        solution -> {
          initialTaskList.add(ParallelTask.create(createTaskIdentifier(), solution));
        });

    return initialTaskList ;
  }

  @Override
  public void processComputedTask(ParallelTask<S> task) {
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
  public void submitTask(ParallelTask<S> task) {
    pendingTaskQueue.add(task);
  }

  @Override
  public ParallelTask<S> createNewTask() {
    int numberOfParents = crossover.numberOfRequiredParents();
    if (population.size() > numberOfParents) {
      List<S> parents = new ArrayList<>(numberOfParents);
      for (int i = 0; i < numberOfParents; i++) {
        parents.add(selection.execute(population));
      }

      List<S> offspring = crossover.execute(parents);

      mutation.execute(offspring.get(0));

      return ParallelTask.create(createTaskIdentifier(), offspring.get(0));
    } else {
      return ParallelTask.create(createTaskIdentifier(), problem.createSolution());
    }
  }

  @Override
  public boolean stoppingConditionIsNotMet() {
    return !termination.isMet(attributes);
  }

  @Override
  public void run() {
    initTime = System.currentTimeMillis();
    super.run();
  }

  @Override
  public List<S> result() {
    return population;
  }

  public Observable<Map<String, Object>> observable() {
    return observable;
  }
}
