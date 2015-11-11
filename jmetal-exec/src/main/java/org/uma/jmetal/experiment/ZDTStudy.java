package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.ExperimentConfigurationBuilder;
import org.uma.jmetal.util.experiment.ExperimentalStudy;
import org.uma.jmetal.util.experiment.impl.AlgorithmExecution;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajnebro on 22/3/15.
 */
public class ZDTStudy {
  public static void main(String[] args) {
    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
        new ZDT3(), new ZDT4(), new ZDT6()) ;

    List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList) ;

    ExperimentConfiguration<DoubleSolution, List<DoubleSolution>> configuration =
        new ExperimentConfigurationBuilder<DoubleSolution, List<DoubleSolution>>("ZDTStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory("/Users/ajnebro/Softw/tmp/pruebas2")
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndependentRuns(4)
            .build();

    AlgorithmExecution<DoubleSolution, List<DoubleSolution>> algorithmExecution =
        new AlgorithmExecution<DoubleSolution, List<DoubleSolution>>(configuration) ;

    ExperimentalStudy study = new ExperimentalStudy.Builder(configuration)
        .addExperiment(algorithmExecution)
        .build() ;

    study.run() ;
  }

  static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList) {
    List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>() ;

    for (int i = 0 ; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problemList.get(i), new SBXCrossover(1.0, 20.0),
          new PolynomialMutation(1.0/problemList.get(i).getNumberOfVariables(), 20.0))
          .build() ;
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problemList.get(i))) ;
    }

    for (int i = 0 ; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(problemList.get(i), new SBXCrossover(1.0, 10.0),
          new PolynomialMutation(1.0/problemList.get(i).getNumberOfVariables(), 20.0))
          .build() ;
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problemList.get(i))) ;
    }

    for (int i = 0 ; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder((DoubleProblem)problemList.get(i),
          new CrowdingDistanceArchive<DoubleSolution>(100))
          .build() ;
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problemList.get(i))) ;
    }

    return algorithms ;
  }
}
