package org.uma.jmetal.algorithm.multiobjective.paes;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;

/**
 * Created by ajnebro on 17/11/14.
 */
public class PAESBuilder {
  public Problem problem;

  public int archiveSize;
  public int maxEvaluations;
  public int biSections;

  public MutationOperator mutationOperator;

  public PAESBuilder(Problem problem) {
    this.problem = problem ;
  }

  public PAESBuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize ;

    return this ;
  }

  public PAESBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public PAESBuilder setBiSections(int biSections) {
    this.biSections = biSections ;

    return this ;
  }

  public PAESBuilder setMutation(MutationOperator mutation) {
    mutationOperator = mutation ;

    return this ;
  }

  public PAES build() {
    return new PAES(this) ;
  }
}
