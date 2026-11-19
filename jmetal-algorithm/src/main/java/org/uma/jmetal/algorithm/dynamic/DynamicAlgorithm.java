package org.uma.jmetal.algorithm.dynamic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * Runs a {@link StepwiseAlgorithm} on a {@link DynamicProblem}, one iteration at a time, and reacts
 * to the changes of the problem between iterations:
 *
 * <pre>
 * initialize the population
 * while the stopping condition is not met and no stop has been requested:
 *   run one iteration
 *   if a round of iterations has finished: publish the front to the observers if it is
 *       significantly different from the last one published
 *   update the time of the problem (with the iteration counter, or with the pending external updates)
 *   react to other external events (e.g. new reference points; see InDM2)
 *   if the problem has changed: restart the search (part of the population is replaced and the
 *       whole population is evaluated again)
 * </pre>
 *
 * <p>The front of a round is published before the changes are applied, so it is always the front of
 * the problem with which it was computed.
 *
 * <p>The changes of the problem come either from the algorithm itself, which passes its iteration
 * counter to {@link DynamicProblem#update(Object)} after each iteration (the protocol of the FDA and
 * CEC 2018 benchmarks; see {@link #timeDrivenByIterations(boolean)}), or from other threads (a
 * clock, a data stream), which call {@link #updateProblem(int)}. External updates are queued and
 * applied between iterations, so the problem never changes in the middle of an evaluation.
 *
 * <p>The search is restarted only when the problem changes. A round is a fixed number of iterations,
 * which a restart does not reset; at the end of each round, the front is published if there is no
 * previous one or if its distance (IGD by default) to the last one published is greater than a
 * threshold (0 by default, so every round is published). The run ends when the {@link
 * DynamicStoppingCondition} is met (never, by default) or when {@link #requestStop()} is called
 * from any thread; {@link #result()} then gives the current front.
 *
 * <p>The observers receive a map with the keys {@code EVENT} ({@link #ROUND_END}, or {@link
 * #PROBLEM_CHANGE} if {@link #notifyProblemChanges(boolean)} is set, with the front before the
 * restart), {@code POPULATION} (copies of the solutions of the front), {@code EVALUATIONS} (all the
 * evaluations, as an Integer), {@code ITERATIONS}, {@code ROUNDS}, {@code PROBLEM_CHANGES} and
 * {@code COMPUTING_TIME}.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class DynamicAlgorithm<S extends Solution<?>>
    implements Algorithm<List<S>>, ObservableEntity<Map<String, Object>> {
  public static final String ROUND_END = "ROUND_END";
  public static final String PROBLEM_CHANGE = "PROBLEM_CHANGE";

  private final String name;
  private final StepwiseAlgorithm<S> algorithm;
  private final DynamicProblem<S, Integer> problem;
  private final RestartStrategy<S> restartStrategyForProblemChange;
  private final int iterationsPerRound;
  private final Observable<Map<String, Object>> observable;

  private DynamicStoppingCondition stoppingCondition = DynamicStoppingCondition.never();
  private boolean timeDrivenByIterations = false;
  private boolean notifyProblemChanges = false;
  private QualityIndicator frontChangeIndicator = new InvertedGenerationalDistance();
  private double frontChangeThreshold = 0.0;

  private final AtomicBoolean stopRequested = new AtomicBoolean(false);
  private final Queue<Integer> pendingProblemUpdates = new ConcurrentLinkedQueue<>();

  private long iterations;
  private long evaluations;
  private int rounds;
  private int problemChanges;
  private int iterationsInRound;
  private long startTime;
  private double[][] lastPublishedFront;

  /**
   * Creates the dynamic algorithm.
   *
   * @param name the name of the algorithm
   * @param algorithm the algorithm that is run one iteration at a time
   * @param problem the dynamic problem
   * @param restartStrategyForProblemChange the restart strategy applied when the problem changes
   * @param iterationsPerRound the iterations of a round, positive
   */
  public DynamicAlgorithm(
      String name,
      StepwiseAlgorithm<S> algorithm,
      DynamicProblem<S, Integer> problem,
      RestartStrategy<S> restartStrategyForProblemChange,
      int iterationsPerRound) {
    Check.notNull(algorithm);
    Check.notNull(problem);
    Check.notNull(restartStrategyForProblemChange);
    Check.that(
        iterationsPerRound > 0,
        () -> "The iterations of a round must be positive: " + iterationsPerRound);
    this.name = name;
    this.algorithm = algorithm;
    this.problem = problem;
    this.restartStrategyForProblemChange = restartStrategyForProblemChange;
    this.iterationsPerRound = iterationsPerRound;
    this.observable = new DefaultObservable<>(name);
  }

  /** Sets the condition that ends the run; by default, it never ends by itself. */
  public DynamicAlgorithm<S> stoppingCondition(DynamicStoppingCondition stoppingCondition) {
    Check.notNull(stoppingCondition);
    this.stoppingCondition = stoppingCondition;
    return this;
  }

  /**
   * If true, the algorithm passes its iteration counter to the problem after each iteration, so
   * that the time of the problem advances with the generations; otherwise (the default), it only
   * changes with {@link #updateProblem(int)}.
   */
  public DynamicAlgorithm<S> timeDrivenByIterations(boolean timeDrivenByIterations) {
    this.timeDrivenByIterations = timeDrivenByIterations;
    return this;
  }

  /** If true, the observers also receive the front just before each restart for a change. */
  public DynamicAlgorithm<S> notifyProblemChanges(boolean notifyProblemChanges) {
    this.notifyProblemChanges = notifyProblemChanges;
    return this;
  }

  /**
   * Sets when the front of a round is published: when the indicator, with the last front published
   * as reference, is greater than the threshold. A threshold of 0 publishes every round.
   */
  public DynamicAlgorithm<S> frontChangeThreshold(QualityIndicator indicator, double threshold) {
    Check.notNull(indicator);
    Check.valueIsNotNegative(threshold);
    this.frontChangeIndicator = indicator;
    this.frontChangeThreshold = threshold;
    return this;
  }

  /**
   * Queues a new value of the counter of the problem, which is passed to {@link
   * DynamicProblem#update(Object)} after the current iteration. It can be called from any thread.
   */
  public void updateProblem(int counter) {
    pendingProblemUpdates.add(counter);
  }

  /**
   * Asks the algorithm to stop at the end of the current iteration. It can be called from any
   * thread.
   */
  public void requestStop() {
    stopRequested.set(true);
  }

  @Override
  public void run() {
    startTime = System.currentTimeMillis();
    evaluations += algorithm.initialize();

    while (!stopRequested.get() && !stoppingCondition.isMet(status())) {
      evaluations += algorithm.step();
      iterations++;
      iterationsInRound++;
      if (iterationsInRound >= iterationsPerRound) {
        endTheRound();
      }

      updateTheTimeOfTheProblem();
      boolean restarted = reactToExternalEvents();
      if (problem.hasChanged()) {
        problem.clearChanged();
        problemChanges++;
        if (!restarted) {
          if (notifyProblemChanges) {
            notifyObservers(PROBLEM_CHANGE, algorithm.result());
          }
          restart(restartStrategyForProblemChange);
        }
      }
    }
  }

  private void updateTheTimeOfTheProblem() {
    if (timeDrivenByIterations) {
      problem.update((int) Math.min(iterations, Integer.MAX_VALUE));
    }
    Integer counter = pendingProblemUpdates.poll();
    while (counter != null) {
      problem.update(counter);
      counter = pendingProblemUpdates.poll();
    }
  }

  /**
   * Reacts to the external events other than the changes of the problem, between iterations; it
   * returns true if it restarted the search, so that a change of the problem at the same time does
   * not restart it again. By default there are none.
   */
  protected boolean reactToExternalEvents() {
    return false;
  }

  /** Restarts the search with the strategy and counts the evaluations made. */
  protected void restart(RestartStrategy<S> restartStrategy) {
    evaluations += algorithm.restart(restartStrategy, problem);
  }

  private void endTheRound() {
    rounds++;
    iterationsInRound = 0;
    List<S> front = algorithm.result();
    double[][] objectives = copyOfTheObjectives(front);
    if (isSignificantlyDifferentFromTheLastPublishedFront(objectives)) {
      lastPublishedFront = objectives;
      notifyObservers(ROUND_END, front);
    }
  }

  private boolean isSignificantlyDifferentFromTheLastPublishedFront(double[][] front) {
    if (lastPublishedFront == null || frontChangeThreshold == 0.0) {
      return true;
    }
    frontChangeIndicator.referenceFront(lastPublishedFront);
    return frontChangeIndicator.compute(front) > frontChangeThreshold;
  }

  private static <S extends Solution<?>> double[][] copyOfTheObjectives(List<S> solutions) {
    double[][] objectives = SolutionListUtils.getMatrixWithObjectiveValues(solutions);
    for (int i = 0; i < objectives.length; i++) {
      objectives[i] = objectives[i].clone();
    }
    return objectives;
  }

  /** Notifies the observers with the event, copies of the solutions and the status of the run. */
  @SuppressWarnings("unchecked")
  protected void notifyObservers(String event, List<S> solutions) {
    Map<String, Object> data = new HashMap<>();
    data.put("EVENT", event);
    data.put("POPULATION", solutions.stream().map(solution -> (S) solution.copy()).toList());
    data.put("EVALUATIONS", (int) Math.min(evaluations, Integer.MAX_VALUE));
    data.put("ITERATIONS", iterations);
    data.put("ROUNDS", rounds);
    data.put("PROBLEM_CHANGES", problemChanges);
    data.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);
    addObservedData(data);
    observable.setChanged();
    observable.notifyObservers(data);
  }

  /** Adds more data to the notifications to the observers; by default, none. */
  protected void addObservedData(Map<String, Object> data) {}

  /** The current state of the run. */
  public DynamicStatus status() {
    return new DynamicStatus(
        iterations, evaluations, rounds, problemChanges, System.currentTimeMillis() - startTime);
  }

  /** The algorithm that is run one iteration at a time. */
  protected StepwiseAlgorithm<S> algorithm() {
    return algorithm;
  }

  public DynamicProblem<S, Integer> problem() {
    return problem;
  }

  public int iterationsPerRound() {
    return iterationsPerRound;
  }

  @Override
  public List<S> result() {
    return algorithm.result();
  }

  @Override
  public Observable<Map<String, Object>> observable() {
    return observable;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return "Dynamic version of " + algorithm.name();
  }
}
