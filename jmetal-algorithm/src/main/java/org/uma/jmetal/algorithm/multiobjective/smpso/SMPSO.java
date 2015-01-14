package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by antonio on 24/09/14.
 */
public class SMPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {
  private DoubleProblem problem;

  private double c1Max;
  private double c1Min;
  private double c2Max;
  private double c2Min;
  private double r1Max;
  private double r1Min;
  private double r2Max;
  private double r2Min;
  private double weightMax;
  private double weightMin;
  private double changeVelocity1;
  private double changeVelocity2;

  private int swarmSize;
  private int maxIterations;
  private int iterations;
  private DoubleSolution[] best;

  private JMetalRandom randomGenerator;

  private Archive<DoubleSolution> leaders;
  private double[][] speed;
  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> crowdingDistanceComparator;

  private MutationOperator mutation;

  private double deltaMax[];
  private double deltaMin[];

  private SolutionListEvaluator evaluator;

  /**
   * Constructor
   */
  public SMPSO(DoubleProblem problem, int swarmSize, Archive<DoubleSolution> leaders,
      MutationOperator mutationOperator, int maxIterations, double r1Min, double r1Max,
      double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
      double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
      SolutionListEvaluator evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.leaders = leaders;
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;

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
    this.evaluator = evaluator;

    dominanceComparator = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    best = new DoubleSolution[swarmSize];
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }
  }

  @Override public void run() {
    List<DoubleSolution> swarm;
    swarm = createInitialSwarm();
    swarm = evaluateSwarm(swarm);
    initializeLeaders(swarm);
    initializeParticlesMemory(swarm);
    initializeLeaders(swarm);
    updateLeadersDensityEstimator();
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity(swarm);
      updatePosition(swarm);
      perturbation(swarm);
      swarm = evaluateSwarm(swarm);
      updateLeaders(swarm);
      updateParticlesMemory(swarm);
      updateLeadersDensityEstimator();
      updateProgress();
    }
  }

  protected void updateLeadersDensityEstimator() {
    if (leaders instanceof CrowdingDistanceArchive) {
      ((CrowdingDistanceArchive) leaders).computeDistance();
    } else {
      throw new JMetalException("Invalid setArchive type");
    }
  }

  @Override protected void initProgress() {
    iterations = 1;
  }

  @Override protected void updateProgress() {
    iterations += 1;
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override protected void initializeLeaders(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      leaders.add(particle);
    }
  }

  @Override protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = swarm.get(i).copy();
      best[i] = (DoubleSolution) particle;
    }
  }

  @Override protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    double wmax, wmin;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) best[i].copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) * (
            inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var] +
                c1 * r1 * (bestParticle.getVariableValue(var) - particle.getVariableValue(var)) +
                c2 * r2 * (bestGlobal.getVariableValue(var) - particle.getVariableValue(var))),
            deltaMax, deltaMin, var);
      }
    }
  }

  @Override protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.getNumberOfVariables(); j++) {
        double v = particle.getVariableValue(j);
        particle.setVariableValue(j, particle.getVariableValue(j) + speed[i][j]);

        if (particle.getVariableValue(j) < problem.getLowerBound(j)) {
          particle.setVariableValue(j, problem.getLowerBound(j));
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.getVariableValue(j) > problem.getUpperBound(j)) {
          particle.setVariableValue(j, problem.getUpperBound(j));
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  @Override protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override protected void updateLeaders(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      leaders.add(particle);
    }
  }

  @Override protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), best[i]);
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        best[i] = particle;
      }
    }
  }

  @Override public List<DoubleSolution> getResult() {
    return this.leaders.getSolutionList();
  }

  protected DoubleSolution selectGlobalBest() {
    Solution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    one = leaders.getSolutionList().get(pos1);
    two = leaders.getSolutionList().get(pos2);

    if (crowdingDistanceComparator.compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  private double velocityConstriction(double v, double[] deltaMax, double[] deltaMin,
      int variableIndex) {

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

  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma;
  }
}
