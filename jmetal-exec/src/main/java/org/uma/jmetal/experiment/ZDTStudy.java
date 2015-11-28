package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.ExperimentConfigurationBuilder;
import org.uma.jmetal.util.experiment.ExperimentExecution;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoFront;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajnebro on 22/3/15.
 */
public class ZDTStudy {
  public static void main(String[] args) throws IOException {
    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
        new ZDT3(), new ZDT4(), new ZDT6()) ;

    List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf") ;

    List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList) ;

    ExperimentConfiguration<DoubleSolution, List<DoubleSolution>> configuration =
        new ExperimentConfigurationBuilder<DoubleSolution, List<DoubleSolution>>("ZDTStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setReferenceFrontDirectory("/Users/ajnebro/Softw/jMetal/jMetal/jmetal-algorithm/src/test/resources/pareto_fronts/")
            .setReferenceFrontFileNames(referenceFrontFileNames)
            .setExperimentBaseDirectory("/Users/ajnebro/Softw/tmp/pruebas3")
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndependentRuns(2)
            .setNumberOfCores(8)
            .build();

    ExperimentExecution experimentExecution = new ExperimentExecution() ;
    experimentExecution
        //.add(new ExecuteAlgorithms<>(configuration))
        .add(new ComputeQualityIndicators(configuration, Arrays.asList(
            new Epsilon<>(), new Spread<>(), new GenerationalDistance<>(), new Hypervolume<>(),
            new InvertedGenerationalDistance<>(), new InvertedGenerationalDistancePlus<>())))
        .run();
  }

  static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList) {
    List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>() ;

      for (int i = 0; i < problemList.size(); i++) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problemList.get(i), new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problemList.get(i)));
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
