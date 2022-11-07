package org.uma.jmetal.algorithm.multiobjective.mgpso;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.RingNeighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a version of the VEPSO algorithm described in:
 * MULTIOBJECTIVE OPTIMIZATION USING PARALLEL VECTOR EVALUATED PARTICLE SWARM OPTIMIZATION
 * K.E. Parsopoulos, D.K. Tasoulis, M.N. Vrahatis
 * Proceedings of international conference on artificial intelligence and applications (IASTED), Innsbruck, Austria;
 * 2004.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MGPSO implements Algorithm<List<DoubleSolution>> {
  private DoubleProblem problem;
  private BoundedArchive<DoubleSolution> archive ;

  private int swarmSize;
  private int maxEvaluations;
  private List<MGPSOSubSwarm> subSwarms;

  private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  public MGPSO(DoubleProblem problem,
               int swarmSize,
               int maxEvaluations,
               BoundedArchive<DoubleSolution> archive) {
    this.problem = problem ;
    this.swarmSize = swarmSize ;
    this.maxEvaluations = maxEvaluations ;
    this.archive = archive ;

    Neighborhood<DoubleSolution> neighborhood = new RingNeighborhood<>() ;

    subSwarms = new ArrayList<>() ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      subSwarms.add(new MGPSOSubSwarm(problem, i, swarmSize/problem.getNumberOfObjectives(),
              maxEvaluations/problem.getNumberOfObjectives(),
          neighborhood,  archive)) ;
    }
  }

  public void run() {
    createInitialSwarms() ;
    evaluateSwarms();
    initializeVelocity();
    initializeParticleBest() ;
    initializeGlobalBest() ;
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity();
      updatePosition();
      perturbation();
      evaluateSwarms() ;
      updateGlobalBest() ;
      updateParticlesBest() ;
      updateProgress();
    }
  }

  protected void initProgress() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.initProgress();
    }
  }

  protected void updateProgress() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.updateProgress();
    }
  }

  protected boolean isStoppingConditionReached() {
    return subSwarms.get(0).isStoppingConditionReached() ;
  }

  protected void createInitialSwarms() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.createInitialSwarm();
    }
  }

  protected void evaluateSwarms() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.evaluateSwarm();
    }
  }

  protected void initializeGlobalBest() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.initializeGlobalBest();
    }
  }

  protected void initializeParticleBest() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.initializeParticleBest();
    }
  }

  protected void initializeVelocity() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.initializeVelocity();
    }
  }

  protected void updatePosition() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.updatePosition();
    }
  }

  protected void perturbation() {

  }

  public List<DoubleSolution> getResult() {
    return archive.getSolutionList() ;
  }

  protected void updateVelocity() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.updateVelocity();
    }
  }

  protected void updateGlobalBest() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.updateGlobalBest();
    }
  }

  protected void updateParticlesBest() {
    for (MGPSOSubSwarm pso : subSwarms) {
      pso.updateParticleBest();
    }
  }

  @Override
  public String getName() {
    return "MGPSO";
  }

  @Override
  public String getDescription() {
    return "MGPSO";
  }
}
