//  pSMPSO.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.metaheuristic.multiobjective.smpso;

import org.uma.jmetal.core.*;
import org.uma.jmetal.qualityIndicator.fastHypervolume.FastHypervolumeArchive;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.CrowdingArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XReal;

import java.io.IOException;
import java.util.Comparator;

/**
 * This class implements the SMPSO algorithm described in:
 * A.J. Nebro, J.J. Durillo, J. Garcia-Nieto, C.A. Coello Coello, F. Luna and E. Alba
 * "SMPSO: A New PSO-based Metaheuristic for Multi-objective Optimization".
 * IEEE Symposium on Computational Intelligence in Multicriteria Decision-Making
 * (MCDM 2009), pp: 66-73. March 2009
 */
public class SMPSO extends Algorithm {
  private static final long serialVersionUID = 6433458914602768519L;

  SolutionSetEvaluator evaluator;

  double r1Max;
  double r1Min;
  double r2Max;
  double r2Min;
  double c1Max;
  double c1Min;
  double c2Max;
  double c2Min;
  double weightMax;
  double weightMin;
  double changeVelocity1;
  double changeVelocity2;

  private int swarmSize;
  private int archiveSize;
  private int maxIterations;
  private int iterations;
  private SolutionSet swarm;
  private Solution[] best;

  private Archive leaders;
  private double[][] speed;
  private Comparator<Solution> dominance;
  private Comparator<Solution> crowdingDistanceComparator;

  private Distance distance;
  private Operator mutation;

  private double deltaMax[];
  private double deltaMin[];


  public SMPSO(Builder builder) {
    super() ;

    problem_ = builder.problem;
    swarmSize = builder.swarmSize;
    archiveSize = builder.archiveSize;
    leaders = builder.leaders;
    mutation = builder.mutationOperator;
    maxIterations = builder.maxIterations;
    evaluator = builder.evaluator;

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
  }

  @Deprecated
  public SMPSO() {
    super();

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

  /* Getters/Setters */
  public void setEvaluator(SolutionSetEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  public int getSwarmSize() {
    return swarmSize;
  }

  public int getArchiveSize() {
    return archiveSize;
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

  /**
   * Runs of the SMPSO algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a experimentoutput of the algorithm execution
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException, IOException {
    initialization();
    createInitialSwarm() ;
    evaluateSwarm();
    initializeLeaders() ;
    initializeParticlesMemory() ;
    updateLeadersDensityEstimator() ;

    while (!stoppingCondition()) {
      computeSpeed(iterations, maxIterations);
      computeNewPositions();
      perturbation();
      evaluateSwarm();
      updateLeaders() ;
      updateParticleMemory() ;
      updateLeadersDensityEstimator() ;
      iterations++ ;
    }

    tearDown() ;
    return paretoFrontApproximation() ;
  }

  public void initialization() {
    // The initial swarm evaluation is iteration 0
    iterations = 1;

    swarm = new SolutionSet(swarmSize);
    best = new Solution[swarmSize];

    dominance = new DominanceComparator();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    distance = new Distance();

    speed = new double[swarmSize][problem_.getNumberOfVariables()];

    deltaMax = new double[problem_.getNumberOfVariables()];
    deltaMin = new double[problem_.getNumberOfVariables()];
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    for (int i = 0; i < swarmSize; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  protected void createInitialSwarm() throws ClassNotFoundException, JMetalException {
    swarm = new SolutionSet(swarmSize);

    Solution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = new Solution(problem_);
      swarm.add(newSolution);
    }
  }

  protected void evaluateSwarm() throws JMetalException {
    swarm = evaluator.evaluate(swarm, problem_);
  }

  protected void initializeLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      leaders.add(particle);
    }
  }

  protected void initializeParticlesMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      best[i] = particle;
    }
  }

  protected void updateLeadersDensityEstimator() {
    if (leaders instanceof CrowdingArchive) {
      distance.crowdingDistanceAssignment(leaders);
    } else if (leaders instanceof FastHypervolumeArchive) {
      ((FastHypervolumeArchive) leaders).computeHVContribution();
    } else {
      throw new JMetalException("Invalid archive type") ;
    }
  }

  protected boolean stoppingCondition() {
    return iterations == maxIterations;
  }

  protected void computeSpeed(int iter, int miter) throws JMetalException, IOException {
    double r1, r2, c1, c2;
    double wmax, wmin ;
    XReal bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      XReal bestParticle = new XReal(best[i]);

      bestGlobal = selectGlobalBest() ;

      r1 = PseudoRandom.randDouble(r1Min, r1Max);
      r2 = PseudoRandom.randDouble(r2Min, r2Max);
      c1 = PseudoRandom.randDouble(c1Min, c1Max);
      c2 = PseudoRandom.randDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) *
          (inertiaWeight(iter, miter, wmax, wmin) *
            speed[i][var] +
            c1 * r1 * (bestParticle.getValue(var) -
              particle.getValue(var)) +
            c2 * r2 * (bestGlobal.getValue(var) -
              particle.getValue(var))), deltaMax, deltaMin, var);
      }
    }
  }

  protected void computeNewPositions() {
    for (int i = 0; i < swarmSize; i++) {
      XReal particle = new XReal(swarm.get(i));
      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        particle.setValue(var, particle.getValue(var) + speed[i][var]);

        if (particle.getValue(var) < problem_.getLowerLimit(var)) {
          particle.setValue(var, problem_.getLowerLimit(var));
          speed[i][var] = speed[i][var] * changeVelocity1;
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed[i][var] = speed[i][var] * changeVelocity2;
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

  protected XReal selectGlobalBest() {
    Solution one, two;
    XReal bestGlobal ;
    int pos1 = PseudoRandom.randInt(0, leaders.size() - 1);
    int pos2 = PseudoRandom.randInt(0, leaders.size() - 1);
    one = leaders.get(pos1);
    two = leaders.get(pos2);

    if (crowdingDistanceComparator.compare(one, two) < 1) {
      bestGlobal = new XReal(one);
    } else {
      bestGlobal = new XReal(two);
    }

    return bestGlobal ;
  }

  protected void updateLeaders() {
    for (int i = 0; i < swarm.size(); i++) {
      Solution particle = new Solution(swarm.get(i));
      leaders.add(particle);
    }
  }

  protected void updateParticleMemory() {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominance.compare(swarm.get(i), best[i]);
      if (flag != 1) {
        Solution particle = new Solution(swarm.get(i));
        best[i] = particle;
      }
    }
  }

  protected SolutionSet paretoFrontApproximation() {
    return this.leaders;
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    //Alternative: return - (((wma-wmin)*(double)iter)/(double)miter);
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
    int variableIndex) throws IOException {

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
    evaluator.shutdown();
  }

  /** Builder class */
  public static class Builder {
    protected SolutionSetEvaluator evaluator;
    protected Problem problem;
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


    public Builder(Problem problem, Archive leaders, SolutionSetEvaluator evaluator) {
      this.evaluator = evaluator ;
      this.problem = problem ;
      this.leaders = leaders ;

      swarmSize = 100 ;
      maxIterations = 25000 ;
      archiveSize = 100 ;

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

    public Builder swarmSize(int swarmSize) {
      this.swarmSize = swarmSize ;

      return this ;
    }

    public Builder maxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      this.archiveSize = archiveSize ;

      return this ;
    }

    public Builder c1Max(double c1Max) {
      this.c1Max = c1Max ;

      return this ;
    }

    public Builder c1Min(double c1Min) {
      this.c1Min = c1Min ;

      return this ;
    }

    public Builder c2Max(double c2Max) {
      this.c2Max = c2Max ;

      return this ;
    }

    public Builder c2Min(double c2Min) {
      this.c2Min = c2Min ;

      return this ;
    }

    public Builder r1Max(double r1Max) {
      this.r1Max = r1Max ;

      return this ;
    }

    public Builder r1Min(double r1Min) {
      this.r1Min = r1Min ;

      return this ;
    }

    public Builder r2Max(double r2Max) {
      this.r2Max = r2Max ;

      return this ;
    }

    public Builder r2Min(double r2Min) {
      this.r2Min = r2Min ;

      return this ;
    }

    public Builder weightMax(double weightMax) {
      this.weightMax = weightMax ;

      return this ;
    }

    public Builder weightMin(double weightMin) {
      this.weightMin = weightMin ;

      return this ;
    }

    public Builder changeVelocity1(double changeVelocity1) {
      this.changeVelocity1 = changeVelocity1 ;

      return this ;
    }

    public Builder changeVelocity2(double changeVelocity2) {
      this.changeVelocity2 = changeVelocity2 ;

      return this ;
    }

    public SMPSO build() {
      return new SMPSO(this) ;
    }
  }
}
