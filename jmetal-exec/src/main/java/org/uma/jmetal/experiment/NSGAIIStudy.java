package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.ExperimentConfigurationBuilder;
import org.uma.jmetal.util.experiment.ExperimentalStudy;
import org.uma.jmetal.util.experiment.impl.AlgorithmExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajnebro on 22/3/15.
 */
public class NSGAIIStudy  {
  public NSGAIIStudy() {
    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
        new ZDT3(), new ZDT4(), new ZDT6()) ;

    List<Algorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList) ;

    ExperimentConfiguration<DoubleSolution> configuration = new ExperimentConfigurationBuilder<DoubleSolution>("Experiment")
        .setAlgorithmList(algorithmList)
        .setProblemList(problemList)
        .setExperimentBaseDirectory("/Users/antelverde/Softw/jMetal/jMetalGitHub/pruebas")
        .setOutputParetoFrontFileName("FUN")
        .setOutputParetoSetFileName("VAR")
        .setIndependentRuns(4)
        .build();

    AlgorithmExecution algorithmExecution = new AlgorithmExecution(configuration) ;

    ExperimentalStudy study = new ExperimentalStudy.Builder(configuration)
        .addExperiment(algorithmExecution)
        .build() ;

    study.run() ;
  }



  List<Algorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList) {
    List<Algorithm<List<DoubleSolution>>> algorithms = new ArrayList<>() ;
    for (int i = 0 ; i < problemList.size(); i++) {
      algorithms.add(
          new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(1.0, 20.0),
              new PolynomialMutation(1.0/problemList.get(i).getNumberOfVariables(), 20.0),
              NSGAIIBuilder.NSGAIIVariant.NSGAII)
              .build()) ;
    }

    return algorithms ;
  }
}
