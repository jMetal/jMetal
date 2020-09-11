package org.uma.jmetal.experimental.parallel.asynchronous.masterworker;

import org.uma.jmetal.experimental.parallel.asynchronous.algorithm.AsynchronousParallelAlgorithm;
import org.uma.jmetal.experimental.parallel.asynchronous.task.ParallelTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Master<T extends ParallelTask<?>,R>
    extends AsynchronousParallelAlgorithm<T,R> {
  protected List<T> initialTaskList;

  protected int numberOfCores;
  protected BlockingQueue<T> completedTaskQueue;
  protected BlockingQueue<T> pendingTaskQueue;

  public Master(int numberOfCores) {
    this.initialTaskList = new LinkedList<>();

    this.numberOfCores = numberOfCores;
    this.completedTaskQueue =  new LinkedBlockingQueue<>();
    this.pendingTaskQueue = new LinkedBlockingQueue<>();
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
  protected T waitForComputedTask() {
    T evaluatedTask = null;
    try {
      evaluatedTask = completedTaskQueue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return evaluatedTask;
  }

  @Override
  protected abstract void processComputedTask(T task);

  @Override
  protected void submitTask(T task) {
    pendingTaskQueue.add(task);
  }

  @Override
  protected abstract T createNewTask();

  @Override
  protected boolean thereAreInitialTasksPending() {
    return initialTaskList.size() > 0;
  }

  @Override
  protected T getInitialTask() {
    T initialTask = initialTaskList.get(0);
    initialTaskList.remove(0);
    return initialTask;
  }

  @Override
  protected abstract boolean stoppingConditionIsNotMet();

  public BlockingQueue<T> getCompletedTaskQueue() {
    return completedTaskQueue;
  }

  public BlockingQueue<T> getPendingTaskQueue() {
    return pendingTaskQueue;
  }
}
