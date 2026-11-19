package org.uma.jmetal.algorithm.examples.multiobjective.indm2;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.PeriodicProblemUpdater;
import org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseRNSGAII;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2Builder;
import org.uma.jmetal.algorithm.multiobjective.indm2.KeyboardReferencePointReader;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.fda.FDA3;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;

/**
 * Runs InDM2 with R-NSGA-II on FDA3 with 31 variables and one variable in x_I, as in the paper,
 * starting with the reference points (0.2, 0.5) and (0.4, 1.0). The problem is updated every second
 * and its time changes every five seconds. Type new reference points in the console (e.g. "0.2,
 * 0.5, 0.4, 1.0" for two points), or q to stop.
 */
public class InDM2WithRNSGAIIRunner {

  public static void main(String[] args) {
    var problem = new FDA3(31, 2, 1);
    int populationSize = 50;
    List<List<Double>> referencePoints = List.of(List.of(0.2, 0.5), List.of(0.4, 1.0));

    var rnsgaii =
        new StepwiseRNSGAII<DoubleSolution>(
            problem,
            populationSize,
            populationSize,
            populationSize,
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
            new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
            new SequentialSolutionListEvaluator<>(),
            referencePoints,
            0.001);

    InDM2<DoubleSolution> indm2 =
        new InDM2Builder<>(rnsgaii, problem, populationSize)
            .setIterationsPerRound(25000 / populationSize)
            .setFrontChangeThreshold(new InvertedGenerationalDistance(), 0.005)
            .build();

    var chart = new RunTimeForDynamicProblemsChartObserver<DoubleSolution>(indm2.name(), 80);
    chart.setReferencePointList(referencePoints);
    indm2.observable().register(chart);

    try (var clock = new PeriodicProblemUpdater(indm2, 1000).start()) {
      new KeyboardReferencePointReader(indm2).start();
      indm2.run();
    }

    JMetalLogger.logger.info("Stopped. " + indm2.status());
  }
}
