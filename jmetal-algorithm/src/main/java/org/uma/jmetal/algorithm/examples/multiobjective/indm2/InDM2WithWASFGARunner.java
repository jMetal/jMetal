package org.uma.jmetal.algorithm.examples.multiobjective.indm2;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.PeriodicProblemUpdater;
import org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseWASFGA;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2Builder;
import org.uma.jmetal.algorithm.multiobjective.indm2.KeyboardReferencePointReader;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.fda.FDA2;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;

/**
 * Runs InDM2 with WASF-GA on FDA2 with the settings of Table 1 of the paper: a population of 50,
 * rounds of 25,000 evaluations (500 generations), a clock that updates the problem every second
 * (its time changes every five seconds, tau_T = 5) and the fronts shown when their IGD with respect
 * to the last one shown is greater than 0.005. The reference point starts at (0, 0); type a new one
 * in the console (e.g. "0.6, 0.4"), or q to stop.
 */
public class InDM2WithWASFGARunner {

  public static void main(String[] args) {
    var problem = new FDA2();
    int populationSize = 50;
    List<Double> referencePoint = List.of(0.0, 0.0);

    var wasfga =
        new StepwiseWASFGA<DoubleSolution>(
            problem,
            populationSize,
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
            new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
            new SequentialSolutionListEvaluator<>(),
            0.01,
            referencePoint);

    InDM2<DoubleSolution> indm2 =
        new InDM2Builder<>(wasfga, problem, populationSize)
            .setIterationsPerRound(25000 / populationSize)
            .setFrontChangeThreshold(new InvertedGenerationalDistance(), 0.005)
            .build();

    var chart = new RunTimeForDynamicProblemsChartObserver<DoubleSolution>(indm2.name(), 80);
    chart.setReferencePointList(List.of(referencePoint));
    indm2.observable().register(chart);

    try (var clock = new PeriodicProblemUpdater(indm2, 1000).start()) {
      new KeyboardReferencePointReader(indm2).start();
      indm2.run();
    }

    JMetalLogger.logger.info("Stopped. " + indm2.status());
  }
}
