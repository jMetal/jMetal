package org.uma.jmetal.parallel.asynchronous.multithreaded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;
import org.uma.jmetal.util.errorchecking.JMetalException;

@DisplayName("Unit tests for Master and Worker")
class MasterWorkerTest {

  /** A master whose tasks are numbers, and whose workers add one to them. */
  private static class NumberMaster extends Master<ParallelTask<Integer>, List<Integer>> {
    private final AtomicLong identifiers = new AtomicLong(1000);

    NumberMaster(int numberOfCores) {
      super(numberOfCores);
    }

    void startWorkers(int count, java.util.function.Function<ParallelTask<Integer>, ParallelTask<Integer>> function) {
      for (int i = 0; i < count; i++) {
        Worker<ParallelTask<Integer>> worker = new Worker<>(function, pendingTaskQueue, completedTaskQueue);
        register(worker);
        worker.start();
      }
    }

    @Override public List<ParallelTask<Integer>> createInitialTasks() { return new ArrayList<>(); }
    @Override public void processComputedTask(ParallelTask<Integer> task) {}
    @Override public ParallelTask<Integer> createNewTask() { return ParallelTask.create(identifiers.getAndIncrement(), -1); }
    @Override public boolean stoppingConditionIsNotMet() { return false; }
    @Override public void initProgress() {}
    @Override public void updateProgress() {}
    @Override public List<Integer> result() { return List.of(); }
  }

  private static List<ParallelTask<Integer>> initialTasks(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> ParallelTask.create(i, i))
        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
  }

  @Nested
  @DisplayName("When the initial tasks are submitted")
  class WhenTheInitialTasksAreSubmitted {
    @Test
    @DisplayName("given more initial tasks than workers, then one task per worker is submitted and the rest stay in the list")
    void givenMoreInitialTasksThanWorkers_thenOneTaskPerWorkerIsSubmitted() {
      NumberMaster master = new NumberMaster(3);
      List<ParallelTask<Integer>> tasks = initialTasks(5);

      master.submitInitialTasks(tasks);

      assertThat(master.getPendingTaskQueue()).hasSize(3);
      assertThat(tasks).extracting(ParallelTask::getContents).containsExactly(3, 4);
      assertThat(master.getPendingTaskQueue()).extracting(ParallelTask::getContents).containsExactly(0, 1, 2);
    }

    @Test
    @DisplayName("given fewer initial tasks than workers, then a new task is created for each worker that has none")
    void givenFewerInitialTasksThanWorkers_thenNewTasksAreCreated() {
      NumberMaster master = new NumberMaster(4);
      List<ParallelTask<Integer>> tasks = initialTasks(1);

      master.submitInitialTasks(tasks);

      assertThat(master.getPendingTaskQueue()).hasSize(4);
      assertThat(tasks).isEmpty();
      assertThat(master.getPendingTaskQueue()).extracting(ParallelTask::getContents).containsExactly(0, -1, -1, -1);
    }
  }

  @Nested
  @DisplayName("When the workers compute tasks")
  class WhenTheWorkersComputeTasks {
    @Test
    @DisplayName("given a pending task, when a worker is started, then the master receives the computed task")
    void givenAPendingTask_whenAWorkerIsStarted_thenTheMasterReceivesTheComputedTask() {
      NumberMaster master = new NumberMaster(1);
      master.startWorkers(1, task -> ParallelTask.create(task.getIdentifier(), task.getContents() + 1));
      master.submitTask(ParallelTask.create(7, 41));

      ParallelTask<Integer> computed =
          assertTimeoutPreemptively(Duration.ofSeconds(10), master::waitForComputedTask);

      assertThat(computed.getContents()).isEqualTo(42);
      assertThat(computed.getIdentifier()).isEqualTo(7);
    }

    @Test
    @DisplayName("given a worker whose computation fails, when the master waits, then it raises an exception and does not wait forever")
    void givenAFailingWorker_whenTheMasterWaits_thenItRaisesAnException() {
      NumberMaster master = new NumberMaster(1);
      master.startWorkers(1, task -> { throw new IllegalStateException("the evaluation failed"); });
      master.submitTask(ParallelTask.create(1, 1));

      JMetalException exception =
          assertTimeoutPreemptively(
              Duration.ofSeconds(10),
              () -> assertThrows(JMetalException.class, master::waitForComputedTask));

      assertThat(exception).hasRootCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("given a worker, when it is interrupted, then it ends")
    void givenAWorker_whenItIsInterrupted_thenItEnds() throws InterruptedException {
      BlockingQueue<ParallelTask<Integer>> pending = new LinkedBlockingQueue<>();
      BlockingQueue<ParallelTask<Integer>> completed = new LinkedBlockingQueue<>();
      Worker<ParallelTask<Integer>> worker = new Worker<>(task -> task, pending, completed);
      worker.start();

      worker.interrupt();
      worker.join(TimeUnit.SECONDS.toMillis(10));

      assertThat(worker.isAlive()).isFalse();
      assertThat(worker.failure()).isEmpty();
    }

    @Test
    @DisplayName("given a worker, then it is a daemon thread")
    void givenAWorker_thenItIsADaemonThread() {
      Worker<ParallelTask<Integer>> worker =
          new Worker<>(task -> task, new LinkedBlockingQueue<>(), new LinkedBlockingQueue<>());

      assertThat(worker.isDaemon()).isTrue();
    }
  }
}
