package org.uma.jmetal.util.artificialdecisionmaker.impl;

import java.util.List;
import org.uma.jmetal.algorithm.InteractiveAlgorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ArtificialDMBuilder<S extends Solution<?>> implements AlgorithmBuilder<ArtificialDM<S>> {

  /**
   * Artificial Decsion Maaker
   */
  private final Problem<S> problem;
  private int maxEvaluations;
  private InteractiveAlgorithm<S,List<S>> algorithm;
  private double considerationProbability;
  private double tolerance;
  private List<Double> rankingCoeficient;
  private int numberReferencePoints;
  private List<Double> asp;

  /**
   * ArtificialDMBuilder constructor
   */
  public ArtificialDMBuilder(Problem<S> problem, InteractiveAlgorithm<S,List<S>> algorithm) {
    this.problem = problem;
    this.maxEvaluations = 25000;
    this.algorithm = algorithm;
    this.numberReferencePoints =1;
  }

  public ArtificialDMBuilder<S> setMaxEvaluations(int maxEvaluations) {
    if (maxEvaluations < 0) {
      throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
    }
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public ArtificialDMBuilder<S> setAsp(List<Double> asp) {
    this.asp = asp;
    return this;
  }

  public ArtificialDMBuilder<S> setAlgorithm(InteractiveAlgorithm<S,List<S>> algorithm) {
    if (algorithm==null) {
      throw new JMetalException("algorithm is null");
    }
    this.algorithm = algorithm;
    return this;
  }

  public ArtificialDMBuilder<S> setConsiderationProbability(double considerationProbability) {
    if (considerationProbability < 0.0) {
      throw new JMetalException("considerationProbability is negative: " + considerationProbability);
    }
    this.considerationProbability = considerationProbability;
    return this;
  }

  public ArtificialDMBuilder<S> setTolerance(double tolerance) {
    if (tolerance < 0.0) {
      throw new JMetalException("tolerance is negative: " + tolerance);
    }
    this.tolerance = tolerance;
    return this;
  }

  public ArtificialDMBuilder<S> setRankingCoeficient(List<Double> rankingCoeficient) {
    this.rankingCoeficient = rankingCoeficient;
    return this;
  }

  public ArtificialDMBuilder<S> setNumberReferencePoints(int numberReferencePoints) {
    this.numberReferencePoints = numberReferencePoints;
    return this;
  }



  public ArtificialDM<S> build() {
    ArtificialDM<S> algorithmRun = null ;
    algorithmRun = new ArtificialDM<S>(problem,algorithm,considerationProbability,tolerance, maxEvaluations,
          rankingCoeficient,asp);

    return algorithmRun ;
  }


  /* Getters */
  public Problem<S> getProblem() {
    return problem;
  }

  public int getMaxIterations() {
    return maxEvaluations;
  }

}
