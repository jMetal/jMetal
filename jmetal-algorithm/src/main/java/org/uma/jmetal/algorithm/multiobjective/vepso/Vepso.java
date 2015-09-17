package org.uma.jmetal.algorithm.multiobjective.vepso;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2007;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.selection.BestSolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.AdaptiveRandomNeighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;


/**
 * This class implements the VEPSO algorithm described in:
 * MULTIOBJECTIVE OPTIMIZATION USING PARALLEL VECTOR EVALUATED PARTICLE SWARM OPTIMIZATION
 * K.E. Parsopoulos, D.K. Tasoulis, M.N. Vrahatis
 * Proceedings of international conference on artificial intelligence
 and applications (IASTED), Innsbruck, Austria; 2004.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class Vepso implements Algorithm<List<DoubleSolution>> {
  private DoubleProblem problem;
  private Archive<DoubleSolution> archive ;

  private Operator<List<DoubleSolution>, DoubleSolution> findBestSolution;
  private Comparator<DoubleSolution> fitnessComparator ;
  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private int numberOfParticlesToInform;

  private List<StandardPSO2007> psoIsland ;
  private List<List<DoubleSolution>> psoIslandSwarm ;

  private double weight;
  private double c;

  private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  public Vepso(DoubleProblem problem,
               int swarmSize,
               int maxIterations,
               int numberOfParticlesToInform,
               Archive<DoubleSolution> archive) {
    this.problem = problem ;
    this.swarmSize = swarmSize ;
    this.maxIterations = maxIterations ;
    this.numberOfParticlesToInform = numberOfParticlesToInform ;
    this.archive = archive ;

    weight = 1.0 / (2.0 * Math.log(2));
    c = 1.0 / 2.0 + Math.log(2);

    psoIsland = new ArrayList<StandardPSO2007>() ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      psoIsland.add(new StandardPSO2007(problem, i, swarmSize,
              maxIterations/problem.getNumberOfObjectives(),
              numberOfParticlesToInform,
              new SequentialSolutionListEvaluator<DoubleSolution>())) ;
    }
  }

  public void run() {
    psoIslandSwarm = createInitialSwarm() ;
    psoIslandSwarm = evaluateSwarm(psoIslandSwarm);
    initializeVelocity(psoIslandSwarm);
    initializeParticlesMemory(psoIslandSwarm) ;
    initializeLeaders(psoIslandSwarm) ;
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity(psoIslandSwarm);
      updatePosition(psoIslandSwarm);
      perturbation(psoIslandSwarm);
      psoIslandSwarm = evaluateSwarm(psoIslandSwarm) ;
      updateLeaders(psoIslandSwarm) ;
      updateParticlesMemory(psoIslandSwarm) ;
      updateProgress();
    }
  }

  protected void initProgress() {
    for (StandardPSO2007 pso : psoIsland) {
      pso.initProgress();
    }
  }

  protected void updateProgress() {
    for (StandardPSO2007 pso : psoIsland) {
      pso.updateProgress();
    }
  }

  protected boolean isStoppingConditionReached() {
    return psoIsland.get(0).isStoppingConditionReached() ;
  }

  protected List<List<DoubleSolution>> createInitialSwarm() {
    psoIslandSwarm = new ArrayList<>(problem.getNumberOfObjectives()) ;

    for (StandardPSO2007 pso : psoIsland) {
      psoIslandSwarm.add(pso.createInitialSwarm()) ;
    }

    return psoIslandSwarm ;
  }

  protected List<List<DoubleSolution>> evaluateSwarm(List<List<DoubleSolution>> swarmList) {
    List<List<DoubleSolution>> swarms = new ArrayList<>(problem.getNumberOfObjectives()) ;
    for (int i = 0; i < psoIsland.size(); i++) {
      swarms.add(i, psoIsland.get(i).evaluateSwarm(swarmList.get(i))) ;
      updateArchive(swarmList.get(i));
    }

    return swarms;
  }

  protected void initializeLeaders(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).initializeLeaders(swarmList.get(i)) ;
    }
  }

  protected void initializeParticlesMemory(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).initializeParticlesMemory(swarmList.get(i)) ;
      psoIsland.get(i).setSwarm(swarmList.get(i));
    }
  }

  protected void initializeVelocity(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).initializeVelocity(swarmList.get(i)) ;
    }
  }

  protected void updateVelocity(List<List<DoubleSolution>> swarmList) {
    updateSwarmVelocity(swarmList);
    //for (int i = 0; i < psoIsland.size(); i++) {
      //psoIsland.get(i).updateVelocity(swarmList.get(i)) ;
    //}
  }

  protected void updatePosition(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).updatePosition(swarmList.get(i)) ;
    }
  }

  protected void perturbation(List<List<DoubleSolution>> swarmList) {

  }

  protected void updateLeaders(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).updateLeaders(swarmList.get(i)) ;
    }
  }

  protected void updateParticlesMemory(List<List<DoubleSolution>> swarmList) {
    for (int i = 0; i < psoIsland.size(); i++) {
      psoIsland.get(i).updateParticlesMemory(swarmList.get(i));
    }
  }

  protected void updateArchive(List<DoubleSolution> swarm) {
    for (DoubleSolution solution : swarm) {
      archive.add((DoubleSolution)solution.copy()) ;
    }
  }

  public List<DoubleSolution> getResult() {
    //List<DoubleSolution> result = new ArrayList<>() ;
    //for (int i = 0; i < psoIsland.size(); i++) {
    //  result.add(psoIsland.get(i).getResult()) ;
    //}
    return archive.getSolutionList() ;
  }



  private void updateSwarmVelocity(List<List<DoubleSolution>> swarmList) {
    for (StandardPSO2007 pso : psoIsland) {
      double r1, r2;
      for (int i = 0; i < swarmSize; i++) {
        DoubleSolution particle = pso.getSwarm().get(i);

        r1 = JMetalRandom.getInstance().nextDouble(0, c);
        r2 = JMetalRandom.getInstance().nextDouble(0, c);

        DoubleSolution globalBest ;
        DoubleSolution localBest ;

        int swarmId = JMetalRandom.getInstance().nextInt(0, 1) ;
        int particleID = JMetalRandom.getInstance().nextInt(0, swarmSize-1) ;

        localBest = pso.getLocalBest()[i] ;

        globalBest = selectGlobalBest() ;
        //globalBest = pso.getNeighborhoodBest()[i] ;
        //globalBest = swarmList.get(swarmId).get(particleID) ;

        if (localBest != globalBest) {
          for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            pso.getSwarmSpeedMatrix()[i][var] = weight * pso.getSwarmSpeedMatrix()[i][var] +
                    r1 * (localBest.getVariableValue(var) - particle.getVariableValue(var)) +
                    r2 * (globalBest.getVariableValue(var) - particle.getVariableValue
                            (var));
          }
        } else {
          for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            pso.getSwarmSpeedMatrix()[i][var] = weight * pso.getSwarmSpeedMatrix()[i][var] +
                    r1 * (localBest.getVariableValue(var) -
                            particle.getVariableValue(var));
          }
        }
      }
    }
  }

  protected DoubleSolution selectGlobalBest() {
    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    one = archive.getSolutionList().get(pos1);
    two = archive.getSolutionList().get(pos2);

    Comparator<DoubleSolution> crowdingDistanceComparator = new CrowdingDistanceComparator<DoubleSolution>() ;

    if (crowdingDistanceComparator.compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }
}
