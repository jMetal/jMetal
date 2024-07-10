package org.uma.jmetal.algorithm.examples.dynamic;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.DynamicNSGAII;
import org.uma.jmetal.util.DynamicFrontManager;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.multiobjective.fda.FDA2;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.observer.impl.RunTimeForDynamicProblemsChartObserver;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;

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
        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection =
        new BinaryTournamentSelection<>();

    InvertedGenerationalDistance igd = new InvertedGenerationalDistance();
    DynamicFrontManager<DoubleSolution> updatedFront = new DynamicFrontManager<>(0.055, igd);
    var algorithm =
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
            updatedFront);

    RunTimeForDynamicProblemsChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeForDynamicProblemsChartObserver<>("Dynamic NSGA-II", 80);

    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();
  }
}
