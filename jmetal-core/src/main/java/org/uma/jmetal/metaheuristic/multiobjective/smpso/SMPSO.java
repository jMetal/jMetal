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
import org.uma.jmetal.qualityIndicator.QualityIndicator;
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
  SolutionSetEvaluator evaluator_ ;

  private static final long serialVersionUID = 6433458914602768519L;
  QualityIndicator indicators_;

  double r1Max_;
  double r1Min_;
  double r2Max_;
  double r2Min_;
  double c1Max_;
  double c1Min_;
  double c2Max_;
  double c2Min_;
  double weightMax_;
  double weightMin_;
  double changeVelocity1_;
  double changeVelocity2_;

  private int swarmSize_;
  private int archiveSize_;
  private int maxIterations_;
  private int iterations_;
  private SolutionSet swarm_;
  private Solution[] best_;

  private Archive leaders_;
  private double[][] speed_;
  private Comparator<Solution> dominance_;
  private Comparator<Solution> crowdingDistanceComparator_;

  private Distance distance_ ;
  private Operator mutation_ ;

  private double deltaMax_[];
  private double deltaMin_[];


  public SMPSO(Builder builder) {
    super() ;

    problem_ = builder.problem_ ;
    swarmSize_ = builder.swarmSize_ ;
    archiveSize_ = builder.archiveSize_ ;
    leaders_ = builder.leaders_ ;
    mutation_ = builder.mutationOperator_ ;
    maxIterations_ = builder.maxIterations_ ;
    evaluator_ = builder.evaluator_ ;

    r1Max_ = builder.r1Max_ ;
    r1Min_ = builder.r1Min_ ;
    r2Max_ = builder.r2Max_ ;
    r2Min_ = builder.r2Min_ ;
    c1Max_ = builder.c1Max_ ;
    c1Min_ = builder.c1Min_ ;
    c2Max_ = builder.c2Max_ ;
    c2Min_ = builder.c2Min_ ;
    weightMax_ = builder.weightMax_ ;
    weightMin_ = builder.weightMin_ ;
    changeVelocity1_ = builder.changeVelocity1_;
    changeVelocity2_ = builder.changeVelocity2_;
  }

  @Deprecated
  public SMPSO() {
    super();

    r1Max_ = 1.0;
    r1Min_ = 0.0;
    r2Max_ = 1.0;
    r2Min_ = 0.0;
    c1Max_ = 2.5;
    c1Min_ = 1.5;
    c2Max_ = 2.5;
    c2Min_ = 1.5;
    weightMax_ = 0.1;
    weightMin_ = 0.1;
    changeVelocity1_ = -1;
    changeVelocity2_ = -1;
  }

  /* Getters/Setters */
  public void setEvaluator(SolutionSetEvaluator evaluator) {
    evaluator_ = evaluator;
  }

  public int getSwarmSize() {
    return swarmSize_ ;
  }

  public int getArchiveSize() {
    return archiveSize_ ;
  }

  public int getMaxIterations() {
    return maxIterations_ ;
  }

  public double getR1Max() {
    return r1Max_ ;
  }

  public double getR1Min() {
    return r1Min_ ;
  }

  public double getR2Max() {
    return r2Max_ ;
  }

  public double getR2Min() {
    return r2Min_ ;
  }

  public double getC1Max() {
    return c1Max_ ;
  }

  public double getC1Min() {
    return c1Min_ ;
  }

  public double getC2Max() {
    return c2Max_ ;
  }

  public double getC2Min() {
    return c2Min_ ;
  }

  public Operator getMutation() {
    return mutation_ ;
  }

  public double getWeightMax() {
    return weightMax_ ;
  }
  public double getWeightMin() {
    return weightMin_ ;
  }

  public double getChangeVelocity1() {
    return changeVelocity1_ ;
  }

  public double getChangeVelocity2() {
    return changeVelocity2_ ;
  }

  /**
   * Runs of the SMPSO algorithm.
   *
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   */
  public SolutionSet execute() throws JMetalException, ClassNotFoundException, IOException {
    initialization();
    createInitialSwarm() ;
    evaluateSwarm();
    initializeLeaders() ;
    initializeParticlesMemory() ;
    updateLeadersDensityEstimator() ;

    while (!stoppingCondition()) {
      computeSpeed(iterations_, maxIterations_);
      computeNewPositions();
      perturbation();
      evaluateSwarm();
      updateLeaders() ;
      updateParticleMemory() ;
      updateLeadersDensityEstimator() ;
      iterations_++ ;
    }

    tearDown() ;
    return paretoFrontApproximation() ;
  }

  public void initialization() {
    // The initial swarm evaluation is iteration 0
    iterations_ = 1;

    swarm_ = new SolutionSet(swarmSize_);
    best_ = new Solution[swarmSize_];

    dominance_ = new DominanceComparator();
    crowdingDistanceComparator_ = new CrowdingDistanceComparator();
    distance_ = new Distance();

    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];

    deltaMax_ = new double[problem_.getNumberOfVariables()];
    deltaMin_ = new double[problem_.getNumberOfVariables()];
    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
      deltaMax_[i] = (problem_.getUpperLimit(i) -
        problem_.getLowerLimit(i)) / 2.0;
      deltaMin_[i] = -deltaMax_[i];
    }

    for (int i = 0; i < swarmSize_; i++) {
      for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
        speed_[i][j] = 0.0;
      }
    }
  }

  protected void createInitialSwarm() throws ClassNotFoundException, JMetalException {
    swarm_ = new SolutionSet(swarmSize_);

    Solution newSolution;
    for (int i = 0; i < swarmSize_; i++) {
      newSolution = new Solution(problem_);
      swarm_.add(newSolution);
    }
  }

  protected void evaluateSwarm() throws JMetalException {
    swarm_ = evaluator_.evaluate(swarm_, problem_);
  }

  protected void initializeLeaders() {
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      leaders_.add(particle);
    }
  }

  protected void initializeParticlesMemory() {
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      best_[i] = particle;
    }
  }

  protected void updateLeadersDensityEstimator() {
    if (leaders_ instanceof CrowdingArchive) {
      distance_.crowdingDistanceAssignment(leaders_);
    } else if (leaders_ instanceof FastHypervolumeArchive) {
      ((FastHypervolumeArchive)leaders_).computeHVContribution();
    } else {
      throw new JMetalException("Invalid archive type") ;
    }
  }

  protected boolean stoppingCondition() {
    return iterations_ == maxIterations_ ;
  }

  protected void computeSpeed(int iter, int miter) throws JMetalException, IOException {
    double r1, r2, c1, c2;
    double wmax, wmin ;
    XReal bestGlobal;

    for (int i = 0; i < swarmSize_; i++) {
      XReal particle = new XReal(swarm_.get(i));
      XReal bestParticle = new XReal(best_[i]);

      bestGlobal = selectGlobalBest() ;

      r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
      r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
      c1 = PseudoRandom.randDouble(c1Min_, c1Max_);
      c2 = PseudoRandom.randDouble(c2Min_, c2Max_);
      wmax = weightMax_;
      wmin = weightMin_;

      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        speed_[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) *
          (inertiaWeight(iter, miter, wmax, wmin) *
            speed_[i][var] +
            c1 * r1 * (bestParticle.getValue(var) -
              particle.getValue(var)) +
            c2 * r2 * (bestGlobal.getValue(var) -
              particle.getValue(var))), deltaMax_, deltaMin_, var);
      }
    }
  }

  protected void computeNewPositions() {
    for (int i = 0; i < swarmSize_; i++) {
      XReal particle = new XReal(swarm_.get(i));
      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
        particle.setValue(var, particle.getValue(var) + speed_[i][var]);

        if (particle.getValue(var) < problem_.getLowerLimit(var)) {
          particle.setValue(var, problem_.getLowerLimit(var));
          speed_[i][var] = speed_[i][var] * changeVelocity1_;
        }
        if (particle.getValue(var) > problem_.getUpperLimit(var)) {
          particle.setValue(var, problem_.getUpperLimit(var));
          speed_[i][var] = speed_[i][var] * changeVelocity2_;
        }
      }
    }
  }

  protected void perturbation() {
    for (int i = 0; i < swarm_.size(); i++) {
      if ((i % 6) == 0) {
        mutation_.execute(swarm_.get(i));
      }
    }
  }

  protected XReal selectGlobalBest() {
    Solution one, two;
    XReal bestGlobal ;
    int pos1 = PseudoRandom.randInt(0, leaders_.size() - 1);
    int pos2 = PseudoRandom.randInt(0, leaders_.size() - 1);
    one = leaders_.get(pos1);
    two = leaders_.get(pos2);

    if (crowdingDistanceComparator_.compare(one, two) < 1) {
      bestGlobal = new XReal(one);
    } else {
      bestGlobal = new XReal(two);
    }

    return bestGlobal ;
  }

  protected void updateLeaders() {
    for (int i = 0; i < swarm_.size(); i++) {
      Solution particle = new Solution(swarm_.get(i));
      leaders_.add(particle);
    }
  }

  protected void updateParticleMemory() {
    for (int i = 0; i < swarm_.size(); i++) {
      int flag = dominance_.compare(swarm_.get(i), best_[i]);
      if (flag != 1) {
        Solution particle = new Solution(swarm_.get(i));
        best_[i] = particle;
      }
    }
  }

  protected SolutionSet paretoFrontApproximation() {
    return this.leaders_;
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
    evaluator_.shutdown();
  }

  /** Builder class */
  public static class Builder {
    protected SolutionSetEvaluator evaluator_ ;
    protected Problem problem_ ;
    protected Archive leaders_ ;

    protected int swarmSize_;
    protected int maxIterations_;
    protected int archiveSize_ ;

    protected Operator mutationOperator_;

    private double c1Max_;
    private double c1Min_;
    private double c2Max_;
    private double c2Min_;
    private double r1Max_;
    private double r1Min_;
    private double r2Max_;
    private double r2Min_;
    private double weightMax_;
    private double weightMin_;
    private double changeVelocity1_;
    private double changeVelocity2_;


    public Builder(Problem problem, Archive leaders, SolutionSetEvaluator evaluator) {
      evaluator_ = evaluator ;
      problem_ = problem ;
      leaders_ = leaders ;

      swarmSize_ = 100 ;
      maxIterations_ = 25000 ;
      archiveSize_ = 100 ;

      r1Max_ = 1.0;
      r1Min_ = 0.0;
      r2Max_ = 1.0;
      r2Min_ = 0.0;
      c1Max_ = 2.5;
      c1Min_ = 1.5;
      c2Max_ = 2.5;
      c2Min_ = 1.5;
      weightMax_ = 0.1;
      weightMin_ = 0.1;
      changeVelocity1_ = -1;
      changeVelocity2_ = -1;
    }

    public Builder swarmSize(int swarmSize) {
      swarmSize_ = swarmSize ;

      return this ;
    }

    public Builder maxIterations(int maxIterations) {
      maxIterations_ = maxIterations ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator_ = mutation ;

      return this ;
    }

    public Builder archiveSize(int archiveSize) {
      archiveSize_ = archiveSize ;

      return this ;
    }

    public Builder c1Max(double c1Max) {
      c1Max_ = c1Max ;

      return this ;
    }

    public Builder c1Min(double c1Min) {
      c1Min_ = c1Min ;

      return this ;
    }

    public Builder c2Max(double c2Max) {
      c2Max_ = c2Max ;

      return this ;
    }

    public Builder c2Min(double c2Min) {
      c2Min_ = c2Min ;

      return this ;
    }

    public Builder r1Max(double r1Max) {
      r1Max_ = r1Max ;

      return this ;
    }

    public Builder r1Min(double r1Min) {
      r1Min_ = r1Min ;

      return this ;
    }

    public Builder r2Max(double r2Max) {
      r2Max_ = r2Max ;

      return this ;
    }

    public Builder r2Min(double r2Min) {
      r2Min_ = r2Min ;

      return this ;
    }

    public Builder weightMax(double weightMax) {
      weightMax_ = weightMax ;

      return this ;
    }

    public Builder weightMin(double weightMin) {
      weightMin_ = weightMin ;

      return this ;
    }

    public Builder changeVelocity1(double changeVelocity1) {
      changeVelocity1_ = changeVelocity1 ;

      return this ;
    }

    public Builder changeVelocity2(double changeVelocity2) {
      changeVelocity2_ = changeVelocity2 ;

      return this ;
    }

    public SMPSO build() {
      return new SMPSO(this) ;
    }
  }
}
