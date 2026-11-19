package org.uma.jmetal.algorithm.dynamic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * A clock that updates the problem of a {@link DynamicAlgorithm} periodically, with a counter that
 * starts at 1 and grows by one every period (the "time counter generator" of InDM2, which replaces
 * the streaming data sources of jMetalSP). Its thread is a daemon, so it does not keep the JVM alive.
 */
public class PeriodicProblemUpdater implements AutoCloseable {
  private final DynamicAlgorithm<?> algorithm;
  private final long periodInMilliseconds;
  private final AtomicInteger counter = new AtomicInteger(1);
  private ScheduledExecutorService executor;

  /**
   * Creates the clock; it starts with {@link #start()}.
   *
   * @param algorithm the algorithm whose problem is updated
   * @param periodInMilliseconds the period, positive
   */
  public PeriodicProblemUpdater(DynamicAlgorithm<?> algorithm, long periodInMilliseconds) {
    Check.notNull(algorithm);
    Check.that(
        periodInMilliseconds > 0, () -> "The period must be positive: " + periodInMilliseconds);
    this.algorithm = algorithm;
    this.periodInMilliseconds = periodInMilliseconds;
  }

  /** Starts the clock. */
  public synchronized PeriodicProblemUpdater start() {
    Check.that(executor == null, "The clock has already been started");
    executor =
        Executors.newSingleThreadScheduledExecutor(
            runnable -> {
              Thread thread = new Thread(runnable, "PeriodicProblemUpdater");
              thread.setDaemon(true);
              return thread;
            });
    executor.scheduleAtFixedRate(
        () -> algorithm.updateProblem(counter.getAndIncrement()),
        periodInMilliseconds,
        periodInMilliseconds,
        TimeUnit.MILLISECONDS);
    return this;
  }

  /** Stops the clock. */
  @Override
  public synchronized void close() {
    if (executor != null) {
      executor.shutdownNow();
    }
  }
}
