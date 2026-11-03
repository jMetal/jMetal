package org.uma.jmetal.parallel.asynchronous.multithreaded;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.uma.jmetal.parallel.asynchronous.algorithm.AsynchronousParallelAlgorithm;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Master of a master-worker scheme with threads: it puts the tasks in a queue of pending tasks,
 * from which the {@link Worker}s take them, and takes the results from a queue of completed tasks,
 * in which the workers put them.
 *
 * <p>The subclasses create the workers with the two queues of the master, register them with
 * {@link #register(Worker)}, so that the master detects a worker that has stopped, and start them.
 * They also provide the steps of the algorithm that depend on the tasks: how they are created and
 * processed, and when the algorithm stops.
 *
 * @param <T> the type of the tasks
 * @param <R> the type of the result of the algorithm
 */
public abstract class Master<T extends ParallelTask<?>,R>
    implements AsynchronousParallelAlgorithm<T,R> {
  protected int numberOfCores;
  private static final long POLLING_INTERVAL_IN_MILLISECONDS = 100;

  protected BlockingQueue<T> completedTaskQueue;
  protected BlockingQueue<T> pendingTaskQueue;
  private final List<Worker<T>> workers = new CopyOnWriteArrayList<>();

  /**
   * Creates a master with empty queues of pending and completed tasks.
   *
   * @param numberOfCores the number of workers, which is also the number of tasks that are being
   *     computed at the same time
   */
  public Master(int numberOfCores) {
    this.numberOfCores = numberOfCores;
    this.completedTaskQueue =  new LinkedBlockingQueue<>();
    this.pendingTaskQueue = new LinkedBlockingQueue<>();
  }

  /**
   * Submits as many initial tasks as there are workers, and a new task for each worker that is left
   * without one. The initial tasks that are submitted are removed from the list: the main loop of
   * the algorithm submits the rest, one for each result that it receives.
   */
  @Override
  public void submitInitialTasks(List<T> initialTasks) {
    int numberOfInitialTasksToSubmit = Math.min(numberOfCores, initialTasks.size());
    for (int i = 0; i < numberOfInitialTasksToSubmit; i++) {
      submitTask(getInitialTask(initialTasks));
    }

    for (int idleWorkers = numberOfCores - numberOfInitialTasksToSubmit; idleWorkers > 0; idleWorkers--) {
      submitTask(createNewTask());
    }
  }

  /**
   * Waits for a computed task. If a worker has failed, it throws an exception, because the result
   * of its task will never arrive.
   */
  @Override
  public T waitForComputedTask() {
    try {
      T computedTask = completedTaskQueue.poll(POLLING_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
      while (computedTask == null) {
        checkThatTheWorkersAreAlive();
        computedTask = completedTaskQueue.poll(POLLING_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
      }

      return computedTask;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new JMetalException("Interrupted while waiting for a computed task", e);
    }
  }

  private void checkThatTheWorkersAreAlive() {
    for (Worker<T> worker : workers) {
      if (!worker.isAlive()) {
        JMetalException exception =
            new JMetalException("A worker has stopped, so a task will never be computed");
        worker.failure().ifPresent(exception::initCause);
        throw exception;
      }
    }
  }

  /** Registers the workers that compute the tasks of this master. */
  protected void register(Worker<T> worker) {
    workers.add(worker);
  }

  @Override
  public abstract void processComputedTask(T task);

  /** Puts the task in the queue of pending tasks, from which a worker will take it. */
  @Override
  public void submitTask(T task) {
    pendingTaskQueue.add(task);
  }

  @Override
  public abstract T createNewTask();

  @Override
  public boolean thereAreInitialTasksPending(List<T> initialTasks) {
    return initialTasks.size() > 0;
  }

  /** Removes the first task of the list and returns it. */
  @Override
  public T getInitialTask(List<T> initialTasks) {
    T initialTask = initialTasks.get(0);
    initialTasks.remove(0);
    return initialTask;
  }

  @Override
  public abstract boolean stoppingConditionIsNotMet();

  /** The queue in which the workers put the computed tasks. */
  public BlockingQueue<T> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  /** The queue from which the workers take the tasks to compute. */
  public BlockingQueue<T> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
