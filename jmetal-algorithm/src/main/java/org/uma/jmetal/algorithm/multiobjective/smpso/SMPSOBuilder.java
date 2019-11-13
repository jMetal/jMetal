package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOBuilder implements AlgorithmBuilder<SMPSO> {
  public enum SMPSOVariant {SMPSO, Measures}

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

  protected int archiveSize;

  protected MutationOperator<DoubleSolution> mutationOperator;

  protected BoundedArchive<DoubleSolution> leaders;

  protected SolutionListEvaluator<DoubleSolution> evaluator;

  protected SMPSOVariant variant ;

  public SMPSOBuilder(DoubleProblem problem, BoundedArchive<DoubleSolution> leaders) {
    this.problem = problem;
    this.leaders = leaders;

    swarmSize = 100;
    maxIterations = 250;

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

    mutationOperator = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;

    this.variant = SMPSOVariant.SMPSO ;

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

  public MutationOperator<DoubleSolution> getMutation() {
    return mutationOperator;
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

  /* Setters */
  public SMPSOBuilder setSwarmSize(int swarmSize) {
    this.swarmSize = swarmSize;

    return this;
  }

  public SMPSOBuilder setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;

    return this;
  }

  public SMPSOBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
    mutationOperator = mutation;

    return this;
  }

  public SMPSOBuilder setC1Max(double c1Max) {
    this.c1Max = c1Max;

    return this;
  }

  public SMPSOBuilder setC1Min(double c1Min) {
    this.c1Min = c1Min;

    return this;
  }

  public SMPSOBuilder setC2Max(double c2Max) {
    this.c2Max = c2Max;

    return this;
  }

  public SMPSOBuilder setC2Min(double c2Min) {
    this.c2Min = c2Min;

    return this;
  }

  public SMPSOBuilder setR1Max(double r1Max) {
    this.r1Max = r1Max;

    return this;
  }

  public SMPSOBuilder setR1Min(double r1Min) {
    this.r1Min = r1Min;

    return this;
  }

  public SMPSOBuilder setR2Max(double r2Max) {
    this.r2Max = r2Max;

    return this;
  }

  public SMPSOBuilder setR2Min(double r2Min) {
    this.r2Min = r2Min;

    return this;
  }

  public SMPSOBuilder setWeightMax(double weightMax) {
    this.weightMax = weightMax;

    return this;
  }

  public SMPSOBuilder setWeightMin(double weightMin) {
    this.weightMin = weightMin;

    return this;
  }

  public SMPSOBuilder setChangeVelocity1(double changeVelocity1) {
    this.changeVelocity1 = changeVelocity1;

    return this;
  }

  public SMPSOBuilder setChangeVelocity2(double changeVelocity2) {
    this.changeVelocity2 = changeVelocity2;

    return this;
  }

  public SMPSOBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

    return this;
  }

  public SMPSOBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public SMPSOBuilder setVariant(SMPSOVariant variant) {
    this.variant = variant;

    return this;
  }

  public SMPSO build() {
    if (variant.equals(SMPSOVariant.SMPSO)) {
      return new SMPSO(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max,
          r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1,
          changeVelocity2, evaluator);
    } else {
      return new SMPSOMeasures(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max,
          r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1,
          changeVelocity2, evaluator);
    }
  }

  /*
   * Getters
   */
  public DoubleProblem getProblem() {
    return problem;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  public BoundedArchive<DoubleSolution> getLeaders() {
    return leaders;
  }

  public SolutionListEvaluator<DoubleSolution> getEvaluator() {
    return evaluator;
  }
}



