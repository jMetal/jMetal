package org.uma.jmetal.parallel.asynchronous.multithreaded;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.parallel.asynchronous.algorithm.AsynchronousParallelAlgorithm;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;

public abstract class Master<T extends ParallelTask<?>,R>
    implements AsynchronousParallelAlgorithm<T,R> {
  protected int numberOfCores;
  protected BlockingQueue<T> completedTaskQueue;
  protected BlockingQueue<T> pendingTaskQueue;

  public Master(int numberOfCores) {
    this.numberOfCores = numberOfCores;
    this.completedTaskQueue =  new LinkedBlockingQueue<>();
    this.pendingTaskQueue = new LinkedBlockingQueue<>();
  }

  @Override
  public void submitInitialTasks(@NotNull List<T> initialTasks) {
    if (initialTasks.size() >= numberOfCores) {
        for (@NotNull T initialTask : initialTasks) {
            submitTask(initialTask);
        }
    } else {
      var idleWorkers = numberOfCores - initialTasks.size();
        for (var initialTask : initialTasks) {
            submitTask(initialTask);
        }
        while (idleWorkers > 0) {
        submitTask(createNewTask());
        idleWorkers--;
      }
    }
  }

  @Override
  public @Nullable T waitForComputedTask() {
    T evaluatedTask = null;
    try {
      evaluatedTask = completedTaskQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return evaluatedTask;
  }

  @Override
  public abstract void processComputedTask(T task);

  @Override
  public void submitTask(T task) {
    pendingTaskQueue.add(task);
  }

  @Override
  public abstract T createNewTask();

  @Override
  public boolean thereAreInitialTasksPending(@NotNull List<T> initialTasks) {
    return initialTasks.size() > 0;
  }

  @Override
  public T getInitialTask(List<T> initialTasks) {
    var initialTask = initialTasks.get(0);
    initialTasks.remove(0);
    return initialTask;
  }

  @Override
  public abstract boolean stoppingConditionIsNotMet();

  public BlockingQueue<T> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  public BlockingQueue<T> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
