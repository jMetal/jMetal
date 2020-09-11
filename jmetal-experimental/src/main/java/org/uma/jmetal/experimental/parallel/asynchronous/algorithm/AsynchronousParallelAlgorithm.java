package org.uma.jmetal.experimental.parallel.asynchronous.algorithm;

import org.uma.jmetal.experimental.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.util.observable.Observable;

import java.util.Map;

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
public abstract class AsynchronousParallelAlgorithm<T extends ParallelTask<?>, R> {
  abstract protected void submitInitialTaskList();
  abstract protected T waitForComputedTask();
  abstract protected void processComputedTask(T task);
  abstract protected void submitTask(T task);
  abstract protected T createNewTask();
  abstract protected boolean thereAreInitialTasksPending();
  abstract protected T getInitialTask();
  abstract protected boolean stoppingConditionIsNotMet();
  abstract protected void initProgress() ;
  abstract protected void updateProgress() ;
  abstract public R getResult() ;

  abstract public Observable<Map<String, Object>> getObservable() ;

  public void run() {
    initProgress() ;
    submitInitialTaskList();

    while (stoppingConditionIsNotMet()) {
      T computedTask = waitForComputedTask();
      processComputedTask(computedTask);

      if (thereAreInitialTasksPending()) {
        submitTask(getInitialTask());
      } else {
        submitTask(createNewTask());
      }

      updateProgress();
    }
  }
}
