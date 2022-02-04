package org.uma.jmetal.experimental.auto.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.termination.Termination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ParticleSwarmOptimizationAlgorithm
        implements Algorithm<List<DoubleSolution>>, ObservableEntity {
  private List<DoubleSolution> swarm;
  private double[][] speed;
  private DoubleSolution[] localBest;
  private BoundedArchive<DoubleSolution> globalBest;

  private Evaluation<DoubleSolution> evaluation;
  private SolutionsCreation<DoubleSolution> createInitialSwarm;
  private Termination termination;
  private VelocityInitialization velocityInitialization;
  private LocalBestInitialization localBestInitialization;
  private GlobalBestInitialization globalBestInitialization;
  private VelocityUpdate velocityUpdate;
  private PositionUpdate positionUpdate;
  private Perturbation perturbation;
  private GlobalBestUpdate globalBestUpdate;
  //private UpdateParticleBest updateParticleBest ;

  private Map<String, Object> attributes;

  private long initTime;
  private long totalComputingTime;
  private int evaluations;
  private Observable<Map<String, Object>> observable;

  private final String name;

  /**
   * Constructor
   *
   * @param name            Algorithm name
   * @param evaluation
   * @param termination
   * @param externalArchive
   */
  public ParticleSwarmOptimizationAlgorithm(
          String name,
          SolutionsCreation<DoubleSolution> createInitialSwarm,
          Evaluation<DoubleSolution> evaluation,
          Termination termination,
          VelocityInitialization velocityInitialization,
          LocalBestInitialization localBestInitialization,
          GlobalBestInitialization globalBestInitialization,
          VelocityUpdate velocityUpdate,
          PositionUpdate positionUpdate,
          Perturbation perturbation,
          GlobalBestUpdate globalBestUpdate,
          BoundedArchive<DoubleSolution> externalArchive) {
    this.name = name;
    this.evaluation = evaluation;
    this.createInitialSwarm = createInitialSwarm;
    this.termination = termination;
    this.globalBest = externalArchive;

    this.velocityInitialization = velocityInitialization;
    this.localBestInitialization = localBestInitialization;
    this.globalBestInitialization = globalBestInitialization;
    this.velocityUpdate = velocityUpdate;
    this.positionUpdate = positionUpdate;
    this.perturbation = perturbation;
    this.globalBestUpdate = globalBestUpdate;

    this.observable = new DefaultObservable<>("Evolutionary Algorithm");
    this.attributes = new HashMap<>();
  }

  public void run() {
    initTime = System.currentTimeMillis();

    swarm = createInitialSwarm.create();
    swarm = evaluation.evaluate(swarm);
    speed = velocityInitialization.initialize(swarm);
    localBest = localBestInitialization.initialize(swarm);
    globalBest = globalBestInitialization.initialize(swarm, globalBest);
    initProgress();

    while (!termination.isMet(attributes)) {
      velocityUpdate.update(swarm, speed, localBest, globalBest);
      positionUpdate.update(swarm, speed);
      swarm = perturbation.perturbate(swarm);
      swarm = evaluation.evaluate(swarm);
      globalBest = globalBestUpdate.update(swarm,globalBest) ;
      //updateParticleBest.update(swarm) ;
      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime;
  }


  private void updateArchive(List<DoubleSolution> population) {
    if (globalBest != null) {
      for (DoubleSolution solution : population) {
        globalBest.add(solution);
      }
    }
  }

  protected void initProgress() {
    evaluations = swarm.size();

    updateArchive(swarm);

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", swarm);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
  }

  protected void updateProgress() {
    evaluations += swarm.size();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", swarm);
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());

    observable.setChanged();
    observable.notifyObservers(attributes);

    totalComputingTime = getCurrentComputingTime();
  }

  public long getCurrentComputingTime() {
    return System.currentTimeMillis() - initTime;
  }

  public int getNumberOfEvaluations() {
    return evaluations;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  @Override
  public List<DoubleSolution> getResult() {
    if (globalBest != null) {
      return globalBest.getSolutionList();
    } else {
      return swarm;
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "Particle Swarm Optimization";
  }

  public Evaluation<DoubleSolution> getEvaluation() {
    return evaluation;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
