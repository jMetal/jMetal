package org.uma.jmetal.experimental.componentbasedalgorithm.example.multiobjective.moead;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.problem.multiobjective.lsmop.LSMOP1;
import org.uma.jmetal.problem.multiobjective.lsmop.LSMOP2;
import org.uma.jmetal.problem.multiobjective.lsmop.LSMOP3;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;
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

    String problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
    String referenceParetoFront = "resources/referenceFronts/LZ09_F2.csv";

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);
    problem = new LSMOP3(5, 100, 2) ;

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
                    new TerminationByEvaluations(150000));

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>("MOEA/D", 80, 1000, null);

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    System.exit(0);
  }
}
