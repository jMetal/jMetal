package org.uma.jmetal.algorithm.multiobjective.indm2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * InDM2 (Interactive Dynamic Multi-Objective Decision Making) of A. J. Nebro, A. B. Ruiz, C.
 * Barba-González, J. García-Nieto, M. Luque and J. F. Aldana-Montes, "InDM2: Interactive Dynamic
 * Multi-Objective Decision Making Using Evolutionary Algorithms", Swarm and Evolutionary
 * Computation 40:184-195, 2018 (https://doi.org/10.1016/j.swevo.2018.02.004).
 *
 * <p>It runs a reference point-based algorithm (WASF-GA, R-NSGA-II or SMPSO/RP; see the {@code
 * Stepwise} classes of {@code org.uma.jmetal.algorithm.dynamic.stepwise}) on a dynamic problem, and
 * lets the decision maker change the reference points during the run with {@link
 * #updateReferencePoints(List)}, from any thread. As in Algorithm 1 of the paper, the search is
 * restarted with one strategy when the reference points change and with another one when the
 * problem changes (only once if both change at the same time), and the approximation of the region
 * of interest is sent to the observers at the end of each round of iterations, if it differs
 * significantly from the last one sent (see {@link DynamicAlgorithm}). The observers also receive the
 * key {@code REFERENCE_POINTS}, and an event {@link #REFERENCE_POINTS_CHANGE}, without solutions,
 * when the reference points change.
 *
 * <p>The time of the problem changes with {@link #updateProblem(int)}, called by an external clock
 * or data source. The run ends when its stopping condition is met (never, by default) or when
 * {@link #requestStop()} is called; the version of jMetalSP had to be killed.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class InDM2<S extends Solution<?>> extends DynamicAlgorithm<S> {
  public static final String REFERENCE_POINTS_CHANGE = "REFERENCE_POINTS_CHANGE";

  private final InteractiveStepwiseAlgorithm<S> interactiveAlgorithm;
  private final RestartStrategy<S> restartStrategyForReferencePointChange;
  private final AtomicReference<List<List<Double>>> pendingReferencePoints =
      new AtomicReference<>();

  /**
   * Creates the algorithm; {@link InDM2Builder} gives the defaults of the paper.
   *
   * @param interactiveAlgorithm the reference point-based algorithm
   * @param problem the dynamic problem
   * @param restartStrategyForProblemChange the restart strategy applied when the problem changes
   * @param restartStrategyForReferencePointChange the one applied when the reference points change
   * @param iterationsPerRound the iterations of a round
   */
  public InDM2(
      InteractiveStepwiseAlgorithm<S> interactiveAlgorithm,
      DynamicProblem<S, Integer> problem,
      RestartStrategy<S> restartStrategyForProblemChange,
      RestartStrategy<S> restartStrategyForReferencePointChange,
      int iterationsPerRound) {
    super(
        "InDM2-" + interactiveAlgorithm.name(),
        interactiveAlgorithm,
        problem,
        restartStrategyForProblemChange,
        iterationsPerRound);
    Check.notNull(restartStrategyForReferencePointChange);
    this.interactiveAlgorithm = interactiveAlgorithm;
    this.restartStrategyForReferencePointChange = restartStrategyForReferencePointChange;
  }

  /**
   * Gives new reference points, each one with a value per objective, which the algorithm takes
   * after the current iteration. It can be called from any thread; if it is called several times in
   * the same iteration, the last points are taken.
   *
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if a point does
   *     not have a value per objective or the algorithm does not accept that number of points
   */
  public void updateReferencePoints(List<List<Double>> referencePoints) {
    Check.notNull(referencePoints);
    int numberOfObjectives = problem().numberOfObjectives();
    referencePoints.forEach(
        point ->
            Check.that(
                point.size() == numberOfObjectives,
                () -> "A reference point must have " + numberOfObjectives + " values: " + point));
    Check.that(
        interactiveAlgorithm.acceptsNumberOfReferencePoints(referencePoints.size()),
        () ->
            interactiveAlgorithm.name()
                + " does not accept "
                + referencePoints.size()
                + " reference points");
    pendingReferencePoints.set(referencePoints.stream().map(List::copyOf).toList());
  }

  /** Takes the pending reference points, if any, and restarts the search for them. */
  @Override
  protected boolean reactToExternalEvents() {
    List<List<Double>> referencePoints = pendingReferencePoints.getAndSet(null);
    if (referencePoints == null) {
      return false;
    }
    interactiveAlgorithm.updateReferencePoints(referencePoints);
    restart(restartStrategyForReferencePointChange);
    notifyObservers(REFERENCE_POINTS_CHANGE, List.of());
    return true;
  }

  @Override
  protected void addObservedData(Map<String, Object> data) {
    data.put("REFERENCE_POINTS", interactiveAlgorithm.referencePoints());
  }

  /** The reference points in use. */
  public List<List<Double>> referencePoints() {
    return interactiveAlgorithm.referencePoints();
  }

  @Override
  public String description() {
    return "Interactive Dynamic Multi-Objective Decision Making algorithm";
  }
}
