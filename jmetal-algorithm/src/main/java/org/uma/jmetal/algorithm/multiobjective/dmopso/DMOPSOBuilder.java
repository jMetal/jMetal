package org.uma.jmetal.algorithm.multiobjective.dmopso;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * @author Jorge Rodriguez
 */
public class DMOPSOBuilder implements AlgorithmBuilder<DMOPSO> {
  public enum DMOPSOVariant {
    DMOPSO, Measures
  }

  private String name;
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
  private int maxAge;

  private String dataDirectory;

  private DMOPSO.FunctionType functionType;

  private SolutionListEvaluator<DoubleSolution> evaluator;

  private DMOPSOVariant variant;

  public DMOPSOBuilder(DoubleProblem problem) {
    this.name = "dMOPSO";
    this.problem = problem;

    this.swarmSize = 100;
    this.maxIterations = 250;
    this.maxAge = 2;

    this.functionType = DMOPSO.FunctionType.PBI;

    this.r1Max = 1.0;
    this.r1Min = 0.0;
    this.r2Max = 1.0;
    this.r2Min = 0.0;
    this.c1Max = 2.5;
    this.c1Min = 1.5;
    this.c2Max = 2.5;
    this.c2Min = 1.5;
    this.weightMax = 0.1;
    this.weightMin = 0.1;
    this.changeVelocity1 = -1;
    this.changeVelocity2 = -1;

    this.evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();

    this.variant = DMOPSOVariant.DMOPSO;

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

  public int getMaxAge() {
    return maxAge;
  }

  public String getDataDirectory() {
    return dataDirectory;
  }

  public DMOPSO.FunctionType getFunctionType() {
    return functionType;
  }

  public String getName() {
    return name;
  }

  /* Setters */
  public DMOPSOBuilder setSwarmSize(int swarmSize) {
    this.swarmSize = swarmSize;

    return this;
  }

  public DMOPSOBuilder setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;

    return this;
  }

  public DMOPSOBuilder setC1Max(double c1Max) {
    this.c1Max = c1Max;

    return this;
  }

  public DMOPSOBuilder setC1Min(double c1Min) {
    this.c1Min = c1Min;

    return this;
  }

  public DMOPSOBuilder setC2Max(double c2Max) {
    this.c2Max = c2Max;

    return this;
  }

  public DMOPSOBuilder setC2Min(double c2Min) {
    this.c2Min = c2Min;

    return this;
  }

  public DMOPSOBuilder setR1Max(double r1Max) {
    this.r1Max = r1Max;

    return this;
  }

  public DMOPSOBuilder setR1Min(double r1Min) {
    this.r1Min = r1Min;

    return this;
  }

  public DMOPSOBuilder setR2Max(double r2Max) {
    this.r2Max = r2Max;

    return this;
  }

  public DMOPSOBuilder setR2Min(double r2Min) {
    this.r2Min = r2Min;

    return this;
  }

  public DMOPSOBuilder setWeightMax(double weightMax) {
    this.weightMax = weightMax;

    return this;
  }

  public DMOPSOBuilder setWeightMin(double weightMin) {
    this.weightMin = weightMin;

    return this;
  }

  public DMOPSOBuilder setChangeVelocity1(double changeVelocity1) {
    this.changeVelocity1 = changeVelocity1;

    return this;
  }

  public DMOPSOBuilder setChangeVelocity2(double changeVelocity2) {
    this.changeVelocity2 = changeVelocity2;

    return this;
  }

  public DMOPSOBuilder setMaxAge(int maxAge) {
    this.maxAge = maxAge;

    return this;
  }

  public DMOPSOBuilder setDataDirectory(String dataDirectory) {
    this.dataDirectory = dataDirectory;

    return this;
  }

  public DMOPSOBuilder setFunctionType(DMOPSO.FunctionType functionType) {
    this.functionType = functionType;

    return this;
  }

  public DMOPSOBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

    return this;
  }

  public DMOPSOBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public DMOPSOBuilder setName(String name) {
    this.name = name;

    return this;
  }

  public DMOPSOBuilder setVariant(DMOPSOVariant variant) {
    this.variant = variant;

    return this;
  }

  public DMOPSO build() {
    DMOPSO algorithm = null;
    if (variant.equals(DMOPSOVariant.DMOPSO)) {
      algorithm = new DMOPSO(problem, swarmSize, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min,
              c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, functionType, dataDirectory, maxAge,
              name);
    } else if (variant.equals(DMOPSOVariant.Measures)) {
      algorithm = new DMOPSOMeasures(problem, swarmSize, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max,
              c2Min, c2Max, weightMin, weightMax, changeVelocity1, changeVelocity2, functionType, dataDirectory,
              maxAge, name);
    }
    return algorithm;
  }

    /*
     * Getters
     */

  public DoubleProblem getProblem() {
    return problem;
  }

  public SolutionListEvaluator<DoubleSolution> getEvaluator() {
    return evaluator;
  }
}
