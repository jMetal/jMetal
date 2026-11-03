package org.uma.jmetal.parallel.asynchronous.multithreaded;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.util.JMetalLogger;

/**
 * A thread that takes tasks from a queue of pending tasks, computes them and puts the results in a
 * queue of completed tasks.
 *
 * <p>It is a daemon thread, so that the workers of an algorithm that has finished do not keep the
 * JVM alive, and it ends when it is interrupted. If the computation of a task fails the worker ends
 * and keeps the failure, which the {@link Master} reports instead of waiting forever for a result
 * that will not come.
 *
 * @param <T> the type of the tasks
 */
public class Worker<T extends ParallelTask<?>> extends Thread {
  private BlockingQueue<T> completedTaskQueue;
  private BlockingQueue<T> pendingTaskQueue;
  protected Function<T, T> computeFunction;
  private volatile Throwable failure;

  public Worker(
      Function<T, T> computeFunction,
      BlockingQueue<T> pendingTaskQueue,
      BlockingQueue<T> completedTaskQueue) {
    this.computeFunction = computeFunction;
    this.completedTaskQueue = completedTaskQueue;
    this.pendingTaskQueue = pendingTaskQueue;
    setDaemon(true);
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      T taskToCompute;
      try {
        taskToCompute = pendingTaskQueue.take();
      } catch (InterruptedException e) {
        interrupt();
        return;
      }

      try {
        completedTaskQueue.add(computeFunction.apply(taskToCompute));
      } catch (RuntimeException | Error e) {
        failure = e;
        JMetalLogger.logger.severe("A worker failed computing a task: " + e);
        return;
      }
    }
  }

  /** The exception that ended the worker, or null if it has not failed. */
  public Throwable failure() {
    return failure;
  }

  public BlockingQueue<T> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  public BlockingQueue<T> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
