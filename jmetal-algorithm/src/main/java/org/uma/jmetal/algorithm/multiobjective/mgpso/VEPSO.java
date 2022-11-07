package org.uma.jmetal.algorithm.multiobjective.mgpso;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
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
public class VEPSO implements Algorithm<List<DoubleSolution>> {
  private DoubleProblem problem;
  private Archive<DoubleSolution> archive ;

  private int swarmSize;
  private int maxIterations;

  private List<ConstrictionBasedPSO> psoIslandList;
  private List<List<DoubleSolution>> ListOfIslandSwarms;
  private List<Double> lambda ;

  private double weight;

  private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  public VEPSO(DoubleProblem problem,
               int swarmSize,
               int maxIterations,
               Archive<DoubleSolution> archive) {
    this.problem = problem ;
    this.swarmSize = swarmSize ;
    this.maxIterations = maxIterations ;
    this.archive = archive ;

    weight = 1.0 / (2.0 * Math.log(2));

    lambda = new ArrayList<Double>(swarmSize);
    for (int i = 0; i < swarmSize; i++) {
      lambda.set(i, randomGenerator.nextDouble()) ;
    }

    psoIslandList = new ArrayList<ConstrictionBasedPSO>() ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      psoIslandList.add(new ConstrictionBasedPSO(problem, i, swarmSize,
              maxIterations/problem.getNumberOfObjectives(),
              new SequentialSolutionListEvaluator<DoubleSolution>())) ;
    }
  }

  public void run() {
    ListOfIslandSwarms = createInitialSwarm() ;
    ListOfIslandSwarms = evaluateSwarm(ListOfIslandSwarms);
    initializeVelocity(ListOfIslandSwarms);
    initializeParticlesMemory(ListOfIslandSwarms) ;
    initializeLeaders(ListOfIslandSwarms) ;
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity(ListOfIslandSwarms);
      updatePosition(ListOfIslandSwarms);
      perturbation(ListOfIslandSwarms);
      ListOfIslandSwarms = evaluateSwarm(ListOfIslandSwarms) ;
      updateLeaders(ListOfIslandSwarms) ;
      updateParticlesMemory(ListOfIslandSwarms) ;
      updateProgress();
    }
  }

  protected void initProgress() {
    for (ConstrictionBasedPSO pso : psoIslandList) {
      pso.initProgress();
    }
  }

  protected void updateProgress() {
    for (ConstrictionBasedPSO pso : psoIslandList) {
      pso.updateProgress();
    }
  }

  protected boolean isStoppingConditionReached() {
    return psoIslandList.get(0).isStoppingConditionReached() ;
  }

  protected List<List<DoubleSolution>> createInitialSwarm() {
    ListOfIslandSwarms = new ArrayList<>(problem.getNumberOfObjectives()) ;

    for (ConstrictionBasedPSO pso : psoIslandList) {
      ListOfIslandSwarms.add(pso.createInitialSwarm()) ;
    }

    return ListOfIslandSwarms;
  }

  protected List<List<DoubleSolution>> evaluateSwarm(List<List<DoubleSolution>> swarmList) {
    List<List<DoubleSolution>> swarms = new ArrayList<>(problem.getNumberOfObjectives()) ;
    for (int i = 0; i < psoIslandList.size(); i++) {
      swarms.add(i, psoIslandList.get(i).evaluateSwarm(swarmList.get(i))) ;
      updateArchive(swarmList.get(i));
    }

    return swarms;
  }

  protected void initializeLeaders(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).initializeGlobalBest(swarmList.get(i)) ;
    }
  }

  protected void initializeParticlesMemory(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).initializeParticleBest(swarmList.get(i)) ;
      psoIslandList.get(i).setSwarm(swarmList.get(i));
    }
  }

  protected void initializeVelocity(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).initializeVelocity(swarmList.get(i)) ;
    }
  }

  protected void updateVelocity(List<List<DoubleSolution>> swarmList) {
    updateSwarmVelocity(swarmList);
  }

  protected void updatePosition(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).updatePosition(swarmList.get(i)) ;
    }
  }

  protected void perturbation(List<List<DoubleSolution>> swarmList) {

  }

  protected void updateLeaders(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).updateGlobalBest(swarmList.get(i)) ;
    }
  }

  protected void updateParticlesMemory(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIslandList.size(); i++) {
      psoIslandList.get(i).updateParticleBest(swarmList.get(i));
    }
  }

  protected void updateArchive(List<DoubleSolution> swarm) {
    for (DoubleSolution solution : swarm) {
      archive.add((DoubleSolution)solution.copy()) ;
    }
  }

  public List<DoubleSolution> getResult() {
    return archive.getSolutionList() ;
  }

  private void updateSwarmVelocity(List<List<DoubleSolution>> swarmList) {
    for (ConstrictionBasedPSO pso : psoIslandList) {
      double r1, r2 ;
      double c1, c2 ;
      double constrictionFactor ;

      for (int i = 0; i < swarmSize; i++) {
        DoubleSolution particle = pso.getSwarm().get(i);

        r1 = randomGenerator.nextDouble(0, 1.0);
        r2 = randomGenerator.nextDouble(0, 1.0);

        c1 = 2.05;
        c2 = 2.05;

        DoubleSolution globalBest;
        DoubleSolution localBest;

        int globalBestSwarmId ;
        //globalBestSwarmId = JMetalRandom.getInstance().nextInt(0, 1);
        globalBestSwarmId = (i + 1) % psoIslandList.size() ;

        localBest = pso.getLocalBest()[i];
        globalBest = psoIslandList.get(globalBestSwarmId).getResult();
        double lambda = JMetalRandom.getInstance().nextDouble() ;

        for (int var = 0; var < particle.variables().size(); var++) {
          pso.getSwarmSpeedMatrix()[i][var] = (weight * pso.getSwarmSpeedMatrix()[i][var] +
                  c1 * r1 * (localBest.variables().get(var) - particle.variables().get(var)) +
                  c2 * r2 * (globalBest.variables().get(var) - particle.variables().get(var)));
        }
      }
    }
  }

  @Override
  public String getName() {
    return "VEPSO";
  }

  @Override
  public String getDescription() {
    return "VEPSO";
  }
}
