package org.uma.jmetal.example.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

import java.io.FileNotFoundException;

/**
 * Class for configuring and running the MOEA/D-DE algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADDEWithRealTimeChartExample extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.moead.MOEADRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws FileNotFoundException {
    DoubleProblem problem;
    MOEADDE algorithm;
    MutationOperator<DoubleSolution> mutation;
    DifferentialEvolutionCrossover crossover;

    String problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
    String referenceParetoFront = "resources/referenceFronts/LZ09_F2.pf";

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int populationSize = 300;

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;

    int maximumNumberOfReplacedSolutions = 2;
    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    algorithm =
            new MOEADDE(
                    problem,
                    populationSize,
                    cr,
                    f,
                    aggregativeFunction,
                    neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions,
                    neighborhoodSize,
                    "resources/weightVectorFiles/moead",
                    new TerminationByEvaluations(1500000));

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>("MOEA/D", 80, 1000, referenceParetoFront);

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    System.exit(0);
  }
}
