package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.DynamicNSGAII;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.fda.FDA2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;

/**
 * Runs the dynamic NSGA-II on FDA2, whose time changes every 50 generations, and shows the front
 * found just before each change. The run ends after 30 changes of the problem.
 */
public class DynamicNSGAIIRunner {

  public static void main(String[] args) {
    int generationsPerChange = 50;
    var problem = new FDA2(31, 2, generationsPerChange, 10);

    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    var algorithm =
        new DynamicNSGAII<DoubleSolution>(
            problem,
            100,
            100,
            new SBXCrossover(0.9, 20.0),
            mutation,
            new BinaryTournamentSelection<>(),
            new SequentialSolutionListEvaluator<>(),
            new DefaultRestartStrategy<>(
                new RemoveNRandomSolutions<>(50), new CreateNRandomSolutions<>()),
            generationsPerChange);
    algorithm.notifyProblemChanges(true);

    var chart = new RunTimeForDynamicProblemsChartObserver<DoubleSolution>("Dynamic NSGA-II", 80);
    algorithm.observable().register(
        (observable, data) -> {
          if (DynamicAlgorithm.PROBLEM_CHANGE.equals(data.get("EVENT"))) {
            chart.update(observable, data);
          }
        });

    algorithm.run();

    JMetalLogger.logger.info(
        "Problem changes: "
            + algorithm.status().problemChanges()
            + ". Evaluations: "
            + algorithm.status().evaluations()
            + ". Computing time: "
            + algorithm.status().computingTime()
            + " ms");
  }
}
