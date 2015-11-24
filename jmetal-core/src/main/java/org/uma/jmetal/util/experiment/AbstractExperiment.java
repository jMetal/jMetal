package org.uma.jmetal.util.experiment;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.util.List;

/**
 * Created by ajnebro on 16/11/15.
 */
public abstract class AbstractExperiment<S extends Solution<?>, Result> {
  public abstract List<Problem<S>> createProblemList() ;
  public abstract List<TaggedAlgorithm<Result>> configureAlgorithmList(List<Problem<S>> problemList) ;
  public abstract ExperimentConfiguration<S, Result> experimentConfiguration(List<TaggedAlgorithm<Result>> algorithmList) ;
  public abstract ExperimentExecution<S, Result> experimentExecution(ExperimentConfiguration<S, Result> experimentConfiguration) ;

  public void run() {
    List<Problem<S>> problemList = createProblemList() ;
    List<TaggedAlgorithm<Result>> algorithmList = configureAlgorithmList(problemList) ;

    ExperimentConfiguration<S, Result> experimentConfiguration ;
    experimentConfiguration = experimentConfiguration(algorithmList) ;

    ExperimentExecution<S, Result> experimentExecution ;
    experimentExecution = experimentExecution(experimentConfiguration) ;
  }
}
