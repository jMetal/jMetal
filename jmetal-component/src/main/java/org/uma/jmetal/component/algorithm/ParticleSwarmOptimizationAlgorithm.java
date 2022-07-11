package org.uma.jmetal.component.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

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
  private LocalBestUpdate localBestUpdate;
  private InertiaWeightComputingStrategy inertiaWeightComputingStrategy;
  private GlobalBestSelection globalBestSelection;
  private Map<String, Object> attributes;

  private long initTime;
  private long totalComputingTime;
  private int evaluations;
  private Observable<Map<String, Object>> observable;

  private final String name;
  /**
   * Constructor
   *
   * @param name
   * @param createInitialSwarm
   * @param evaluation
   * @param termination
   * @param velocityInitialization
   * @param localBestInitialization
   * @param globalBestInitialization
   * @param inertiaWeightComputingStrategy
   * @param velocityUpdate
   * @param positionUpdate
   * @param perturbation
   * @param globalBestUpdate
   * @param localBestUpdate
   * @param globalBestSelection
   * @param globalBestArchive
   */
  public ParticleSwarmOptimizationAlgorithm(
      String name,
      SolutionsCreation<DoubleSolution> createInitialSwarm,
      Evaluation<DoubleSolution> evaluation,
      Termination termination,
      VelocityInitialization velocityInitialization,
      LocalBestInitialization localBestInitialization,
      GlobalBestInitialization globalBestInitialization,
      InertiaWeightComputingStrategy inertiaWeightComputingStrategy,
      VelocityUpdate velocityUpdate,
      PositionUpdate positionUpdate,
      Perturbation perturbation,
      GlobalBestUpdate globalBestUpdate,
      LocalBestUpdate localBestUpdate,
      GlobalBestSelection globalBestSelection,
      BoundedArchive<DoubleSolution> globalBestArchive) {
    this.name = name;
    this.evaluation = evaluation;
    this.createInitialSwarm = createInitialSwarm;
    this.termination = termination;
    this.globalBest = globalBestArchive;

    this.velocityInitialization = velocityInitialization;
    this.localBestInitialization = localBestInitialization;
    this.globalBestInitialization = globalBestInitialization;
    this.inertiaWeightComputingStrategy = inertiaWeightComputingStrategy;
    this.velocityUpdate = velocityUpdate;
    this.positionUpdate = positionUpdate;
    this.perturbation = perturbation;
    this.globalBestUpdate = globalBestUpdate;
    this.localBestUpdate = localBestUpdate;
    this.globalBestSelection = globalBestSelection;

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
      velocityUpdate.update(swarm, speed, localBest, globalBest, globalBestSelection,
          inertiaWeightComputingStrategy);
      positionUpdate.update(swarm, speed);
      swarm = perturbation.perturb(swarm);
      swarm = evaluation.evaluate(swarm);
      globalBest = globalBestUpdate.update(swarm, globalBest);
      localBest = localBestUpdate.update(swarm, localBest);
      updateProgress();
    }

    totalComputingTime = System.currentTimeMillis() - initTime;
  }

  protected void initProgress() {
    evaluations = swarm.size();
    globalBest.computeDensityEstimator();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", globalBest.getSolutionList());
    attributes.put("COMPUTING_TIME", getCurrentComputingTime());
  }

  protected void updateProgress() {
    evaluations += swarm.size();
    globalBest.computeDensityEstimator();

    attributes.put("EVALUATIONS", evaluations);
    attributes.put("POPULATION", globalBest.getSolutionList());
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
    return globalBest.getSolutionList();
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

  public void setTermination(Termination termination) {
    this.termination = termination;
  }
}
