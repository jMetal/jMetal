package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.*;

/**
 * This class implements the SMPSO algorithm described in: SMPSO: A new PSO-based metaheuristic for
 * multi-objective optimization MCDM 2009. DOI: http://dx.doi.org/10.1109/MCDM.2009.4938830
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SMPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> implements ObservableEntity {
  protected DoubleProblem problem;

  protected double c1Max;
  protected double c1Min;
  protected double c2Max;
  protected double c2Min;
  protected double r1Max;
  protected double r1Min;
  protected double r2Max;
  protected double r2Min;
  protected double weightMax;
  protected double weightMin;
  protected double changeVelocity1;
  protected double changeVelocity2;

  protected int swarmSize;
  protected int evaluations;

  protected GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
  protected double[][] speed;

  protected JMetalRandom randomGenerator;

  protected BoundedArchive<DoubleSolution> leaders;
  protected Comparator<DoubleSolution> dominanceComparator;

  protected MutationOperator<DoubleSolution> mutation;

  protected double[] deltaMax;
  protected double[] deltaMin;

  protected Evaluation<DoubleSolution> evaluation;
  protected Termination termination;

  protected long startTime;
  protected long totalComputingTime;

  protected Map<String, Object> algorithmStatusData;
  protected Observable<Map<String, Object>> observable;

  public SMPSO(
          DoubleProblem problem,
          int swarmSize,
          BoundedArchive<DoubleSolution> leaders,
          MutationOperator<DoubleSolution> mutationOperator,
          Evaluation<DoubleSolution> evaluation, Termination termination) {
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
            termination);
  }

  /**
   * Constructor
   */
  public SMPSO(
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
          Termination termination) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.leaders = leaders;
    this.mutation = mutationOperator;

    this.evaluations = 0;

    this.r1Max = r1Max;
    this.r1Min = r1Min;
    this.r2Max = r2Max;
    this.r2Min = r2Min;
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;

    randomGenerator = JMetalRandom.getInstance();

    this.evaluation = evaluation;
    this.termination = termination;

    dominanceComparator = new DominanceComparator<DoubleSolution>();
    localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>();
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    algorithmStatusData = new HashMap<>();
    observable = new DefaultObservable<>("SMPSO observable");
  }

  @Override
  public void run() {
    startTime = System.currentTimeMillis();
    super.run();
    totalComputingTime = System.currentTimeMillis() - startTime;
  }

  protected void updateLeadersDensityEstimator() {
    leaders.computeDensityEstimator();
  }

  @Override
  protected void initProgress() {
    evaluations = swarmSize;
    updateLeadersDensityEstimator();

    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("SWARM", getSwarm());
    algorithmStatusData.put("POPULATION", leaders.getSolutionList());
    algorithmStatusData.put("LEADERS_ARCHIVE", leaders);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected void updateProgress() {
    evaluations += swarmSize;
    updateLeadersDensityEstimator();

    algorithmStatusData.put("EVALUATIONS", evaluations);
    algorithmStatusData.put("SWARM", getSwarm());
    algorithmStatusData.put("POPULATION", leaders.getSolutionList());
    algorithmStatusData.put("LEADERS_ARCHIVE", leaders);
    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

    observable.setChanged();
    observable.notifyObservers(algorithmStatusData);
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return termination.isMet(algorithmStatusData);
  }

  @Override
  protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    return evaluation.evaluate(swarm);
  }

  @Override
  protected void initializeLeader(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      leaders.add(particle);
    }
  }

  @Override
  protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override
  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override
  protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest.getAttribute(swarm.get(i)).copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] =
                velocityConstriction(
                        constrictionCoefficient(c1, c2)
                                * (weightMax * speed[i][var]
                                + c1 * r1 * (bestParticle.getVariable(var) - particle.getVariable(var))
                                + c2 * r2 * (bestGlobal.getVariable(var) - particle.getVariable(var))),
                        deltaMax,
                        deltaMin,
                        var);
      }
    }
  }

  @Override
  protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.getNumberOfVariables(); j++) {
        particle.setVariable(j, particle.getVariable(j) + speed[i][j]);

        if (particle.getVariable(j) < problem.getLowerBound(j)) {
          particle.setVariable(j, problem.getLowerBound(j));
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.getVariable(j) > problem.getUpperBound(j)) {
          particle.setVariable(j, problem.getUpperBound(j));
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  @Override
  protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override
  protected void updateLeaders(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      leaders.add((DoubleSolution) particle.copy());
    }
  }

  @Override
  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return leaders.getSolutionList();
  }

  protected DoubleSolution selectGlobalBest() {
    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    one = leaders.getSolutionList().get(pos1);
    two = leaders.getSolutionList().get(pos2);

    if (leaders.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  private double velocityConstriction(
          double v, double[] deltaMax, double[] deltaMin, int variableIndex) {
    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  protected double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  @Override
  public String getName() {
    return "SMPSO";
  }

  @Override
  public String getDescription() {
    return "Speed contrained Multiobjective PSO";
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }

  public long getTotalComputingTime() {
    return totalComputingTime;
  }

  /* Getters */
  public int getSwarmSize() {
    return swarmSize;
  }

  public int getEvaluations() {
    return evaluations;
  }
}
