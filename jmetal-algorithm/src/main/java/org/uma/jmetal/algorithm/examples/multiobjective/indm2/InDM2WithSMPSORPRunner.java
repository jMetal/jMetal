package org.uma.jmetal.algorithm.examples.multiobjective.indm2;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.dynamic.PeriodicProblemUpdater;
import org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseSMPSORP;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2;
import org.uma.jmetal.algorithm.multiobjective.indm2.InDM2Builder;
import org.uma.jmetal.algorithm.multiobjective.indm2.KeyboardReferencePointReader;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.df.DF1;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;

/**
 * Runs InDM2 with SMPSO/RP on DF1, with a swarm of 100 particles and two reference points, (0.1,
 * 0.6) and (0.6, 0.1). The problem is updated every second (its time changes every ten seconds,
 * tau_t = 10). Type two new reference points in the console (e.g. "0.2, 0.4, 0.5, 0.2"), or q to
 * stop.
 */
public class InDM2WithSMPSORPRunner {

  public static void main(String[] args) {
    var problem = new DF1();
    int swarmSize = 100;
    List<List<Double>> referencePoints = List.of(List.of(0.1, 0.6), List.of(0.6, 0.1));

    List<ArchiveWithReferencePoint<DoubleSolution>> archives = new ArrayList<>();
    for (List<Double> referencePoint : referencePoints) {
      archives.add(
          new CrowdingDistanceArchiveWithReferencePoint<>(
              swarmSize / referencePoints.size(), referencePoint));
    }

    var smpsorp =
        new StepwiseSMPSORP(
            problem,
            swarmSize,
            archives,
            referencePoints,
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
            250,
            0.0, 1.0,
            0.0, 1.0,
            2.5, 1.5,
            2.5, 1.5,
            0.1, 0.1,
            -1.0, -1.0,
            new DefaultDominanceComparator<>(),
            new SequentialSolutionListEvaluator<>());

    InDM2<DoubleSolution> indm2 =
        new InDM2Builder<>(smpsorp, problem, swarmSize)
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
