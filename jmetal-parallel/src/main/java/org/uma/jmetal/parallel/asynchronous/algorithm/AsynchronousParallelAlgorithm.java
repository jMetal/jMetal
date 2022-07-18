package org.uma.jmetal.parallel.asynchronous.algorithm;

import java.util.List;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;

/**
 * Abstract class representing asynchronous parallel algorithms. The general idea is that whenever a new {@link ParallelTask}
 * object is created, it is sent to an external entity (e.g., a thread) to be computed in an asynchronous way.
 * It is assumed that there is an initial task list (which could be empty). The main loop of the algorithms consists
 * in waiting for any computed task which and, when one is received, it is processed and then a new task can be created
 * and submitted to be computed. The speed-ups that can be obtained will depend on the number of external entities,
 * the granularity of the task computation, and time required to process a received computed task.
 *
 * @param <T> Task to be computed
 */
public interface AsynchronousParallelAlgorithm<T extends ParallelTask<?>, R> {
  void submitInitialTasks(List<T> tasks);
  List<T> createInitialTasks() ;
  T waitForComputedTask();
  void processComputedTask(T task);
  void submitTask(T task);
  T createNewTask();
  boolean thereAreInitialTasksPending(List<T> tasks);
  T getInitialTask(List<T> tasks);
  boolean stoppingConditionIsNotMet();
  void initProgress() ;
  void updateProgress() ;
  R getResult() ;

  default void run() {
    var initialTasks = createInitialTasks();
    submitInitialTasks(initialTasks);

    initProgress() ;
    while (stoppingConditionIsNotMet()) {
      var computedTask = waitForComputedTask();
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
