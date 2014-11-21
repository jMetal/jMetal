//  CovarianceMatrixAdaptationEvolutionStrategy.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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

package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class implementing the CMA-ES algorithm
 */
public class CovarianceMatrixAdaptationEvolutionStrategy extends AbstractEvolutionStrategy<DoubleSolution, DoubleSolution> {

  private Comparator<Solution> comparator ;
  private int lambda ;
  private int evaluations ;
  private int maxEvaluations ;

  private ContinuousProblem problem ;

  private double sigma ;

  private double[] xMean ;

  /*
   * Strategy parameter setting: Selection
   */
  private int mu ;
  private double[] weights ;
  private double muEff;

  /*
   * Strategy parameter setting: Adaptation
   */
  private double cc ;
  private double cs ;
  private double c1 ;
  private double cmu ;
  private double damps ;

  /*
   * Dynamic (internal) strategy parameters and constants
   */
  private double[] pc ;
  private double[] ps ;
  private double[][] B ;
  private double[] diagD ;
  private double[][] C ;
  private double[][] invsqrtC ;
  private int eigeneval ;
  private double chiN ;

  //private double[][] arx;
  private DoubleSolution bestSolutionEver = null;

  private Random rand;

  /** Constructor */
  private CovarianceMatrixAdaptationEvolutionStrategy (Builder builder) {
    this.problem = builder.problem ;
    this.lambda = builder.lambda ;
    this.maxEvaluations = builder.maxEvaluations ;

    long seed = System.currentTimeMillis();
    rand = new Random(seed);
    comparator = new ObjectiveComparator(0);

    initializeInternalParameters();

  }

  /* Getters */
  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  /** Buider class */
  public static class Builder {
    private ContinuousProblem problem ;
    private int lambda ;
    private int maxEvaluations ;

    public Builder(ContinuousProblem problem)  {
      this.problem = problem ;
      lambda = 10;
      maxEvaluations = 1000000;
    }

    public Builder setLambda (int lambda) {
      this.lambda = lambda ;

      return this ;
    }

    public Builder setMaxEvaluations (int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public CovarianceMatrixAdaptationEvolutionStrategy build() {
      return new CovarianceMatrixAdaptationEvolutionStrategy(this) ;
    }
  }

  @Override
  protected void initProgress() {
    evaluations = 0;
  }

  @Override protected void updateProgress() {
    evaluations += lambda;
    updateInternalParameters();
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override
  protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(lambda) ;
    for (int i = 0; i < lambda; i++) {
      DoubleSolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
    for (DoubleSolution solution : population) {
      problem.evaluate(solution);
    }
    return population ;
  }

  @Override
  protected List<DoubleSolution> selection(List<DoubleSolution> population) {
    return population;
  }

  @Override
  protected List<DoubleSolution> reproduction(List<DoubleSolution> population) {

    List<DoubleSolution> offspringPopulation = new ArrayList<>(lambda) ;

    for (int iNk = 0; iNk < lambda; iNk++) {
      offspringPopulation.add(sampleSolution());
    }

    return offspringPopulation;
  }

  @Override
  protected List<DoubleSolution> replacement(List<DoubleSolution> population,
          List<DoubleSolution> offspringPopulation) {
    return offspringPopulation;
  }

  @Override public DoubleSolution getResult() {
    return bestSolutionEver;
  }

  private void initializeInternalParameters() {

    // number of objective variables/problem dimension
    int N = problem.getNumberOfVariables();

    // objective variables initial point
    // TODO: Initialize the mean in a better way
    xMean = new double[N];
    for (int i = 0; i < N; i++) {
      xMean[i] = rand.nextDouble();
    }

    // coordinate wise standard deviation (step size)
    sigma = 0.3;

    /* Strategy parameter setting: Selection */

    // population size, offspring number
    //lambda = 4+Math.floor(3*Math.log(N));

    // number of parents/points for recombination
    // TODO: Maybe use this parameter in recombination
    mu = (int) Math.floor(lambda / 2);

    // muXone array for weighted recombination
    weights = new double[mu];
    double sum = 0;
    for (int i = 0; i < mu; i++) {
      weights[i] = (Math.log(mu + 1 / 2) - Math.log(i + 1));
      sum += weights[i];
    }
    // normalize recombination weights array
    for (int i = 0; i < mu; i++) {
      weights[i] = weights[i] / sum;
    }

    // variance-effectiveness of sum w_i x_i
    double sum1 = 0;
    double sum2 = 0;
    for (int i = 0; i < mu; i++) {
      sum1 += weights[i];
      sum2 += weights[i] * weights[i];
    }
    muEff = sum1 * sum1 / sum2;

    /* Strategy parameter setting: Adaptation */

    // time constant for cumulation for C
    cc = (4 + muEff / N) / (N + 4 + 2 * muEff / N);

    // t-const for cumulation for sigma control
    cs = (muEff + 2) / (N + muEff + 5);

    // learning rate for rank-one update of C
    c1 = 2 / ((N + 1.3) * (N + 1.3) + muEff);

    // learning rate for rank-mu update
    cmu = Math.min(1 - c1, 2 * (muEff - 2 + 1 / muEff) / ((N + 2) * (N + 2) + muEff));

    // damping for sigma, usually close to 1
    damps = 1 + 2 * Math.max(0, Math.sqrt((muEff - 1) / (N + 1)) - 1) + cs;

    /* Initialize dynamic (internal) strategy parameters and constants */

    // diagonal D defines the scaling
    diagD = new double[N];

    // evolution paths for C and sigma
    pc = new double[N];
    ps = new double[N];

    // B defines the coordinate system
    B = new double[N][N];
    // covariance matrix C
    C = new double[N][N];

    // C^-1/2
    invsqrtC = new double[N][N];

    for (int i = 0; i < N; i++) {
      pc[i] = 0;
      ps[i] = 0;
      diagD[i] = 1;
      for (int j = 0; j < N; j++) {
        B[i][j] = 0;
        invsqrtC[i][j] = 0;
      }
      for (int j = 0; j < i; j++) {
        C[i][j] = 0;
      }
      B[i][i] = 1;
      C[i][i] = diagD[i] * diagD[i];
      invsqrtC[i][i] = 1;
    }

    // track update of B and D
    eigeneval = 0;

    chiN = Math.sqrt(N) * (1 - 1 / (4 * N) + 1 / (21 * N * N));

  }

  private void updateInternalParameters() {

    int N = problem.getNumberOfVariables();

    double[] oldXMean = new double[N];

    /* Sort by fitness and compute weighted mean into xMean */

    //minimization
    getPopulation().sort(comparator);
    storeBest();

    // calculate xMean and BDz~N(0,C)
    for (int i = 0; i < N; i++) {
      oldXMean[i] = xMean[i];
      xMean[i] = 0.;
      for (int iNk = 0; iNk < mu; iNk++) {
        //xMean[i] += weights[iNk] * arx[arindex[iNk]][i];
        double variableValue = (double) getPopulation().get(iNk).getVariableValue(i);
        xMean[i] += weights[iNk] * variableValue;
      }
    }


    /* Cumulation: Update evolution paths */

    double[] artmp = new double[N];
    for (int i = 0; i < N; i++) {
      artmp[i] = 0;
      for (int j = 0; j < N; j++) {
        artmp[i] += invsqrtC[i][j] * (xMean[j] - oldXMean[j]) / sigma;
      }
    }
    // cumulation for sigma (ps)
    for (int i = 0; i < N; i++) {
      ps[i] = (1. - cs) * ps[i]
          + Math.sqrt(cs * (2. - cs) * muEff)
          * artmp[i];
    }

    // calculate norm(ps)^2
    double psxps = 0.0;
    for (int i = 0; i < N; i++) {
      psxps += ps[i] * ps[i];
    }

    // cumulation for covariance matrix (pc)
    int hsig = 0;
    if ((Math.sqrt(psxps) / Math.sqrt(1. - Math.pow(1. - cs, 2. * evaluations / lambda)) / chiN)
        < (1.4 + 2. / (N + 1.))) {
      hsig = 1;
    }
    for (int i = 0; i < N; i++) {
      pc[i] = (1. - cc) * pc[i]
          + hsig * Math.sqrt(cc * (2. - cc) * muEff) * (xMean[i] - oldXMean[i]) / sigma;
    }


    /* Adapt covariance matrix C */

    for (int i = 0; i < N; i++) {
      for (int j = 0; j <= i; j++) {
        C[i][j] = (1 - c1 - cmu)
            * C[i][j]
            + c1
            * (pc[i] * pc[j] + (1 - hsig) * cc
            * (2. - cc) * C[i][j]);
        for (int k = 0; k < mu; k++) {
          /*
           * additional rank mu
           * update
           */
          double valueI = (double) getPopulation().get(k).getVariableValue(i);
          double valueJ = (double) getPopulation().get(k).getVariableValue(j);
          C[i][j] += cmu
              * weights[k]
              * (valueI - oldXMean[i])
              * (valueJ - oldXMean[j]) /sigma
              / sigma;
        }
      }
    }

    /* Adapt step size sigma */

    sigma *= Math.exp((cs / damps) * (Math.sqrt(psxps) / chiN - 1));


    /* Decomposition of C into B*diag(D.^2)*B' (diagonalization) */

    if (evaluations - eigeneval > lambda / (c1 + cmu) / N / 10) {

      eigeneval = evaluations;

      // enforce symmetry
      for (int i = 0; i < N; i++) {
        for (int j = 0; j <= i; j++) {
          B[i][j] = B[j][i] = C[i][j];
        }
      }

      // eigen decomposition, B==normalized eigenvectors
      double[] offdiag = new double[N];
      CMAESUtils.tred2(N, B, diagD, offdiag);
      CMAESUtils.tql2(N, diagD, offdiag, B);

      // TODO: Maybe refactor as stoppping condition
      if (CMAESUtils.checkEigenSystem(N, C, diagD, B) > 0) {
        evaluations = maxEvaluations;
      }

      // TODO: Maybe refactor as stoppping condition
      for (int i = 0; i < N; i++) {
        if (diagD[i] < 0) { // numerical problem?
          JMetalLogger.logger.severe(
                  "CovarianceMatrixAdaptationEvolutionStrategy.updateDistribution:" +
                  " WARNING - an eigenvalue has become negative.");
          evaluations = maxEvaluations;
        }
        diagD[i] = Math.sqrt(diagD[i]);
      }

      double[][] artmp2 = new double[N][N];
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          artmp2[i][j] = B[i][j] * (1 / diagD[j]);
        }
      }
      for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
          invsqrtC[i][j] = 0.0;
          for (int k = 0; k < N; k++) {
            invsqrtC[i][j] += artmp2[i][k] * B[j][k];
          }
        }
      }

    }

  }

  private DoubleSolution sampleSolution() {

    DoubleSolution solution = problem.createSolution();

    int N = problem.getNumberOfVariables();
    double[] artmp = new double[N];
    double sum;

    for (int i = 0; i < N; i++) {
      //TODO: Check the correctness of this random (http://en.wikipedia.org/wiki/CMA-ES)
      artmp[i] = diagD[i] * rand.nextGaussian();
    }
    for (int i = 0; i < N; i++) {
      sum = 0.0;
      for (int j = 0; j < N; j++) {
        sum += B[i][j] * artmp[j];
      }

      double value = xMean[i] + sigma * sum;
      if (value > problem.getUpperBound(i)) {
        value = problem.getUpperBound(i);
      } else if (value < problem.getLowerBound(i)) {
        value = problem.getLowerBound(i);
      }

      solution.setVariableValue(i, value);
    }

    return solution;
  }

  private void storeBest() {
    if ((bestSolutionEver == null)
            || (bestSolutionEver.getObjective(0) > getPopulation().get(0).getObjective(0))) {
      bestSolutionEver = getPopulation().get(0);
    }
  }

}
