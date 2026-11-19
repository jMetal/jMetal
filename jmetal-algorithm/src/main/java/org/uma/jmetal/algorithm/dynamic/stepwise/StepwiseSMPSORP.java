package org.uma.jmetal.algorithm.dynamic.stepwise;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * SMPSO/RP run one move of the swarm at a time by a {@link
 * org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm}, with reference points that can change during
 * the run. There is an archive of leaders per reference point, so the number of reference points
 * cannot change.
 *
 * <p>The parameters are those of {@link SMPSORP}; {@code maxIterations} only defines the schedule of
 * the inertia weight, which starts again at each restart.
 */
@SuppressWarnings("serial")
public class StepwiseSMPSORP extends SMPSORP
    implements InteractiveStepwiseAlgorithm<DoubleSolution> {
  private List<List<Double>> referencePoints;

  public StepwiseSMPSORP(
      DoubleProblem problem,
      int swarmSize,
      List<ArchiveWithReferencePoint<DoubleSolution>> leaders,
      List<List<Double>> referencePoints,
      MutationOperator<DoubleSolution> mutationOperator,
      int maxIterations,
      double r1Min,
      double r1Max,
      double r2Min,
      double r2Max,
      double c1Min,
      double c1Max,
      double c2Min,
      double c2Max,
      double weightMin,
      double weightMax,
      double changeVelocity1,
      double changeVelocity2,
      DominanceComparator<DoubleSolution> dominanceComparator,
      SolutionListEvaluator<DoubleSolution> evaluator) {
    super(
        problem,
        swarmSize,
        leaders,
        referencePoints,
        mutationOperator,
        maxIterations,
        r1Min,
        r1Max,
        r2Min,
        r2Max,
        c1Min,
        c1Max,
        c2Min,
        c2Max,
        weightMin,
        weightMax,
        changeVelocity1,
        changeVelocity2,
        dominanceComparator,
        evaluator);
    Check.that(
        leaders.size() == referencePoints.size(),
        () ->
            "There must be an archive per reference point ("
                + leaders.size()
                + " archives and "
                + referencePoints.size()
                + " points)");
    this.referencePoints = List.copyOf(referencePoints);
  }

  @Override
  public int initialize() {
    setSwarm(evaluateSwarm(createInitialSwarm()));
    initializeVelocity(getSwarm());
    initializeParticlesMemory(getSwarm());
    initializeLeader(getSwarm());
    initProgress();
    return getSwarm().size();
  }

  @Override
  public int step() {
    updateVelocity(getSwarm());
    updatePosition(getSwarm());
    perturbation(getSwarm());
    setSwarm(evaluateSwarm(getSwarm()));
    updateLeaders(getSwarm());
    updateParticlesMemory(getSwarm());
    updateProgress();
    return getSwarm().size();
  }

  /** Also empties the archives of leaders, whose objectives are no longer valid after a change. */
  @Override
  public int restart(
      RestartStrategy<DoubleSolution> restartStrategy, DynamicProblem<DoubleSolution, ?> problem) {
    restartStrategy.restart(getSwarm(), problem);
    setSwarm(evaluateSwarm(getSwarm()));
    initializeVelocity(getSwarm());
    initializeParticlesMemory(getSwarm());
    leaders.forEach(archive -> archive.solutions().clear());
    initializeLeader(getSwarm());
    initProgress();
    return getSwarm().size();
  }

  @Override
  public void updateReferencePoints(List<List<Double>> referencePoints) {
    Check.that(
        acceptsNumberOfReferencePoints(referencePoints.size()),
        () -> "SMPSO/RP needs " + leaders.size() + " reference points: " + referencePoints);
    changeReferencePoints(referencePoints);
    for (int i = 0; i < referencePoints.size(); i++) {
      for (int j = 0; j < referencePoints.get(i).size(); j++) {
        getReferencePointSolutions().get(i).objectives()[j] = referencePoints.get(i).get(j);
      }
    }
    this.referencePoints = List.copyOf(referencePoints);
  }

  @Override
  public List<List<Double>> referencePoints() {
    return referencePoints;
  }

  @Override
  public boolean acceptsNumberOfReferencePoints(int numberOfReferencePoints) {
    return numberOfReferencePoints == leaders.size();
  }

  @Override
  public List<DoubleSolution> population() {
    return getSwarm();
  }
}
