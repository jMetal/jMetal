package org.uma.jmetal.parallel.asynchronous.algorithm;

import java.util.List;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;

/**
 * Interface of the asynchronous parallel algorithms. Whenever a new {@link ParallelTask} is
 * created, it is sent to an external entity (e.g., a thread) that computes it asynchronously. The
 * algorithm starts with a list of initial tasks (which could be empty), and its main loop waits for
 * any computed task, processes it and submits another one: an initial task while there are some
 * left, and a new one afterwards. So there are always as many tasks being computed as tasks were
 * submitted at the beginning. The speed-ups that can be obtained depend on the number of external
 * entities, the granularity of the computation of the tasks, and the time required to process a
 * computed task.
 *
 * <p>{@link #run()} implements that loop; the implementations provide the steps.
 *
 * @param <T> the type of the tasks to be computed
 * @param <R> the type of the result of the algorithm
 */
public interface AsynchronousParallelAlgorithm<T extends ParallelTask<?>, R> {
  /**
   * Submits the first tasks to be computed. The initial tasks that are submitted must be removed
   * from the list (e.g., with {@link #getInitialTask(List)}), because the main loop submits the
   * ones that remain in it, and a task that is submitted twice is computed twice.
   *
   * @param tasks the initial tasks, as returned by {@link #createInitialTasks()}
   */
  void submitInitialTasks(List<T> tasks);

  /** Creates the initial tasks, such as the solutions of the initial population. */
  List<T> createInitialTasks() ;

  /** Waits until a task has been computed, and returns it. */
  T waitForComputedTask();

  /** Processes a computed task, for example by inserting its solution in the population. */
  void processComputedTask(T task);

  /** Sends a task to be computed. */
  void submitTask(T task);

  /** Creates a new task from the state of the algorithm, for example a new offspring solution. */
  T createNewTask();

  /** Returns true if there are initial tasks that have not been submitted yet. */
  boolean thereAreInitialTasksPending(List<T> tasks);

  /** Removes the next initial task from the list and returns it. */
  T getInitialTask(List<T> tasks);

  /** Returns true while the algorithm has to go on. */
  boolean stoppingConditionIsNotMet();

  /** Initializes the progress of the algorithm; it is called after the initial tasks are submitted. */
  void initProgress() ;

  /** Updates the progress of the algorithm; it is called once per computed task. */
  void updateProgress() ;

  /** Returns the result of the algorithm. */
  R result() ;

  /**
   * Runs the algorithm: it submits the initial tasks and, until the stopping condition is met,
   * waits for a computed task, processes it and submits an initial task, if any is left, or a new
   * one.
   */
  default void run() {
    List<T> initialTasks = createInitialTasks();
    submitInitialTasks(initialTasks);

    initProgress() ;
    while (stoppingConditionIsNotMet()) {
      T computedTask = waitForComputedTask();
      processComputedTask(computedTask);

      if (thereAreInitialTasksPending(initialTasks)) {
        submitTask(getInitialTask(initialTasks));
      } else {
        submitTask(createNewTask());
      }
      updateProgress();
    }
  }
}
