package org.uma.jmetal.example.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.DynamicAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.DynamicNSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.multiobjective.fda.FDA2;
import org.uma.jmetal.qualityindicator.impl.CoverageFront;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;

import java.util.List;

public class DynamicNSGAIIRunner {
  /**
   * main() method to run the algorithm as a process
   *
   * @param args
   */
  public static void main(String[] args) {
    DynamicProblem<DoubleSolution, Integer> problem = new FDA2();

    // STEP 2. Create the algorithm
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20.0);
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection =
        new BinaryTournamentSelection<>();

    InvertedGenerationalDistance<PointSolution> igd = new InvertedGenerationalDistance<>();
    CoverageFront<PointSolution> coverageFront = new CoverageFront<>(0.055, igd);
    DynamicAlgorithm<List<DoubleSolution>> algorithm =
        new DynamicNSGAII<>(
            problem,
            25000,
            100,
            100,
            100,
            crossover,
            mutation,
            selection,
            new SequentialSolutionListEvaluator<>(),
            new DefaultRestartStrategy<>(
                new RemoveNRandomSolutions<>(10), new CreateNRandomSolutions<>()),
            new DefaultObservable<>("Dynamic NSGA-II"),
            coverageFront);

    // EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeForDynamicProblemsChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeForDynamicProblemsChartObserver<>("Dynamic NSGA-II", 80);

    // algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();
  }
}
