package org.uma.jmetal.lab.experiment.lab;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.jmetal5version.NSGAIIBuilder;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class ExperimentTemplate {

  @FunctionalInterface
  public interface AlgorithmWrapper<S extends Solution<?>> {
    Algorithm<List<S>> create(Problem<S> problem);
  }

  public class NSGAIIgg implements AlgorithmWrapper<DoubleSolution> {
    public Algorithm<List<DoubleSolution>> create(Problem<DoubleSolution> problem) {
      Algorithm<List<DoubleSolution>> algorithm =
          new NSGAIIBuilder<DoubleSolution>(
                  problem,
                  new SBXCrossover(1.0, 20.0),
                  new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0),
                  100)
              .build();

      return algorithm;
    }
  }

  AlgorithmWrapper<DoubleSolution> a =
      (problem ->
          new NSGAIIBuilder<DoubleSolution>(
                  problem,
                  new SBXCrossover(1.0, 20.0),
                  new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0),
                  100)
              .build());

  public void run() {
    ExperimentSettings<DoubleSolution, List<DoubleSolution>> experimentSettings;

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT1()));
    problemList.add(new ExperimentProblem<>(new ZDT2()));
    problemList.add(new ExperimentProblem<>(new ZDT3()));
    problemList.add(new ExperimentProblem<>(new ZDT4()));
    problemList.add(new ExperimentProblem<>(new ZDT6()));

    List<AlgorithmWrapper> algorithmWrappers = new ArrayList<>();

    // algorithmWrappers.add()

    List<AlgorithmBuilder<?>> algorithmBuilders = new ArrayList<>();

    String experimentName = "test";
    String experimentBaseDirectory = "";
    // experimentSettings = new ExperimentSettings<>(experimentName, experimentBaseDirectory, ) ;
  }
}
