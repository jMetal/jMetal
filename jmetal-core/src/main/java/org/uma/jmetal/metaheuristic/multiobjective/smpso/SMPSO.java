package org.uma.jmetal.metaheuristic.multiobjective.smpso;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.DoubleSolution;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.ApacheRandomUtilsGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by antonio on 24/09/14.
 */
public class SMPSO implements Algorithm<List<DoubleSolution>> {
  private ContinuousProblem problem ;

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
  private List<DoubleSolution> swarm;
  private DoubleSolution[] best;

  private PseudoRandomGenerator randomGenerator ;

  private Archive<DoubleSolution> leaders;
  private double[][] speed;
  private Comparator<Solution<?>> dominanceComparator;
  private Comparator<Solution<?>> crowdingDistanceComparator;

  private Operator mutation;

  private double deltaMax[];
  private double deltaMin[];

  /** Constructor */
  public SMPSO(Builder builder) {
    problem = builder.problem;
    swarmSize = builder.swarmSize;
    leaders = builder.leaders;
    mutation = builder.mutationOperator;
    maxIterations = builder.maxIterations;

    r1Max = builder.r1Max;
    r1Min = builder.r1Min;
    r2Max = builder.r2Max;
    r2Min = builder.r2Min;
    c1Max = builder.c1Max;
    c1Min = builder.c1Min;
    c2Max = builder.c2Max;
    c2Min = builder.c2Min;
    weightMax = builder.weightMax;
    weightMin = builder.weightMin;
    changeVelocity1 = builder.changeVelocity1;
    changeVelocity2 = builder.changeVelocity2;

    randomGenerator = new ApacheRandomUtilsGenerator() ;
  }

  /* Getters */
  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getR1Max() {
    return r1Max;
  }

  public double getR1Min() {
    return r1Min;
  }

  public double getR2Max() {
    return r2Max;
  }

  public double getR2Min() {
    return r2Min;
  }

  public double getC1Max() {
    return c1Max;
  }

  public double getC1Min() {
    return c1Min;
  }

  public double getC2Max() {
    return c2Max;
  }

  public double getC2Min() {
    return c2Min;
  }

  public Operator getMutation() {
    return mutation;
  }

  public double getWeightMax() {
    return weightMax;
  }

  public double getWeightMin() {
    return weightMin;
  }

  public double getChangeVelocity1() {
    return changeVelocity1;
  }

  public double getChangeVelocity2() {
    return changeVelocity2;
  }

  /** Builder class */
  public static class Builder {
    protected ContinuousProblem problem;
    protected Archive leaders;

    protected int swarmSize;
    protected int maxIterations;
    protected int archiveSize;

    protected Operator mutationOperator;

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


    public Builder(ContinuousProblem problem, Archive leaders) {
      this.problem = problem ;
      this.leaders = leaders ;

      swarmSize = 100 ;
      maxIterations = 25000 ;

      r1Max = 1.0;
      r1Min = 0.0;
      r2Max = 1.0;
      r2Min = 0.0;
      c1Max = 2.5;
      c1Min = 1.5;
      c2Max = 2.5;
      c2Min = 1.5;
      weightMax = 0.1;
      weightMin = 0.1;
      changeVelocity1 = -1;
      changeVelocity2 = -1;
    }

    public Builder setSwarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setMutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder setC1Max(double c1Max) {
      this.c1Max = c1Max ;

      return this ;
    }

    public Builder setC1Min(double c1Min) {
      this.c1Min = c1Min ;

      return this ;
    }

    public Builder setC2Max(double c2Max) {
      this.c2Max = c2Max ;

      return this ;
    }

    public Builder setC2Min(double c2Min) {
      this.c2Min = c2Min ;

      return this ;
    }

    public Builder setR1Max(double r1Max) {
      this.r1Max = r1Max ;

      return this ;
    }

    public Builder setR1Min(double r1Min) {
      this.r1Min = r1Min ;

      return this ;
    }

    public Builder setR2Max(double r2Max) {
      this.r2Max = r2Max ;

      return this ;
    }

    public Builder setR2Min(double r2Min) {
      this.r2Min = r2Min ;

      return this ;
    }

    public Builder setWeightMax(double weightMax) {
      this.weightMax = weightMax ;

      return this ;
    }

    public Builder setWeightMin(double weightMin) {
      this.weightMin = weightMin ;

      return this ;
    }

    public Builder setChangeVelocity1(double changeVelocity1) {
      this.changeVelocity1 = changeVelocity1 ;

      return this ;
    }

    public Builder setChangeVelocity2(double changeVelocity2) {
      this.changeVelocity2 = changeVelocity2 ;

      return this ;
    }

    public SMPSO build() {
      return new SMPSO(this) ;
    }
  }

  /** Execute() method  */
  @Override
  //public List<Solution<?>> run() {
  public void run() {
    initialization() ;
    createInitialSwarm() ;
    evaluateSwarm();
    initializeLeaders() ;
    initializeParticlesMemory() ;
    updateLeadersDensityEstimator() ;

    while (!stoppingCondition()) {
      computeSpeed(iterations, maxIterations);
      computeNewPositions();
      perturbation();
      evaluateSwarm() ;
      updateLeaders() ;
      updateParticleMemory() ;
      updateLeadersDensityEstimator() ;
      iterations++ ;
    }

    tearDown() ;
    //return paretoFrontApproximation() ;
  }

  @Override
  public List<DoubleSolution> getResult() {
    return paretoFrontApproximation() ;
  }

  public void initialization() {
    // The initial swarm evaluation is iteration 0
    iterations = 1;

    swarm = new ArrayList<DoubleSolution>(swarmSize);
    best = new DoubleSolution[swarmSize];

    dominanceComparator = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();

    speed = new double[swarmSize][problem.getNumberOfVariables()];

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  protected void createInitialSwarm() {
    swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = (DoubleSolution)problem.createSolution() ;
      swarm.add(newSolution);
    }
  }

  protected void evaluateSwarm() throws JMetalException {
    for (int i = 0 ; i < swarm.size(); i++) {
      //problem.evaluateConstraints(solutionSet.get(i)) ;
      problem.evaluate(swarm.get(i)) ;
    }
    //swarm = evaluator.evaluate(swarm, problem);
  }

  protected void initializeLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution)swarm.get(i).copy() ;
      leaders.add(particle);
    }
  }

  protected void initializeParticlesMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = swarm.get(i).copy();
      best[i] = (DoubleSolution)particle;
    }
  }

  protected void updateLeadersDensityEstimator() {
   if (leaders instanceof CrowdingDistanceArchive) {
     ((CrowdingDistanceArchive) leaders).computeDistance();
//      distance.crowdingDistanceAssignment(leaders);
//    } else if (leaders instanceof FastHypervolumeArchive) {
//      ((FastHypervolumeArchive) leaders).computeHVContribution();
    } else {
      throw new JMetalException("Invalid setArchive type") ;
    }
  }

  protected boolean stoppingCondition() {
    return iterations == maxIterations;
  }

  protected void computeSpeed(int iter, int miter)  {
    double r1, r2, c1, c2;
    double wmax, wmin ;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = (DoubleSolution)swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution)best[i].copy();

      bestGlobal = selectGlobalBest() ;

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) *
                (inertiaWeight(iter, miter, wmax, wmin) *
                        speed[i][var] +
                        c1 * r1 * (bestParticle.getVariableValue(var) -
                                particle.getVariableValue(var)) +
                        c2 * r2 * (bestGlobal.getVariableValue(var) -
                                particle.getVariableValue(var))), deltaMax, deltaMin, var);
      }
    }
  }

  protected void computeNewPositions() {
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

  protected void perturbation() {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  protected DoubleSolution selectGlobalBest() {
    Solution one, two;
    DoubleSolution bestGlobal ;
    int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    one = leaders.getSolutionList().get(pos1);
    two = leaders.getSolutionList().get(pos2);

    if (crowdingDistanceComparator.compare(one, two) < 1) {
      bestGlobal = (DoubleSolution)one.copy();
    } else {
      bestGlobal = (DoubleSolution)two.copy();
    }

    return bestGlobal ;
  }

  protected void updateLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution)swarm.get(i).copy();
      leaders.add(particle);
    }
  }

  protected void updateParticleMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), best[i]);
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution)swarm.get(i).copy();
        best[i] = particle;
      }
    }
  }

  protected List<DoubleSolution> paretoFrontApproximation() {
    return this.leaders.getSolutionList();
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    /*Alternative: return - (((wma-wmin)*(double)iter)/(double)miter);*/
    return wma;
  }

  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  private double velocityConstriction(
          double v,
          double[] deltaMax,
          double[] deltaMin,
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

  protected void tearDown() {
  }

}
