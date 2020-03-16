package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

/**
 * Variant of SMPSO using an external archive. The archive is updated with the evaluated solutions
 * and a subset of the solution list it contains is returned as algorithm result.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMPSOWithArchive extends SMPSO {
  private Archive<DoubleSolution> archive;

  public SMPSOWithArchive(
      DoubleProblem problem,
      int swarmSize,
      BoundedArchive<DoubleSolution> leaders,
      MutationOperator<DoubleSolution> mutationOperator,
      Evaluation<DoubleSolution> evaluation,
      Termination termination,
      Archive<DoubleSolution> archive) {
    this(
        problem,
        swarmSize,
        leaders,
        mutationOperator,
        0.0,
        1.0,
        0.0,
        1.0,
        1.5,
        2.5,
        1.5,
        2.5,
        0.1,
        0.1,
        -1,
        -1,
        evaluation,
        termination,
        archive);
  }

  /** Constructor */
  public SMPSOWithArchive(
      DoubleProblem problem,
      int swarmSize,
      BoundedArchive<DoubleSolution> leaders,
      MutationOperator<DoubleSolution> mutationOperator,
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
      Evaluation<DoubleSolution> evaluation,
      Termination termination,
      Archive<DoubleSolution> archive) {
    super(
        problem,
        swarmSize,
        leaders,
        mutationOperator,
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
        evaluation,
        termination);
    this.archive = archive;
  }

  @Override
  protected void initializeLeader(List<DoubleSolution> swarm) {
    super.initializeLeader(swarm);
    for (DoubleSolution particle : swarm) {
      archive.add(particle);
    }
  }

  @Override
  protected void updateLeaders(List<DoubleSolution> swarm) {
    super.updateLeaders(swarm);
    for (DoubleSolution particle : swarm) {
      archive.add((DoubleSolution) particle.copy());
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }

  public Archive<DoubleSolution> getArchive() {
    return archive;
  }

  public List<DoubleSolution> getPopulation() {
    return getSwarm();
  }
}
