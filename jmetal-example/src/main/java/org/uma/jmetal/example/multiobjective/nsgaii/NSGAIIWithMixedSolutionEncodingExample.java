package org.uma.jmetal.example.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.CompositeCrossover;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.CompositeMutation;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.MixedIntegerDoubleProblem;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Class to configure and run the NSGA-II algorithm. A chart showing the front at the end of each
 * iteration is displayed.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIWithMixedSolutionEncodingExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<CompositeSolution> problem =
        new MixedIntegerDoubleProblem(10, 10, 100, -100, -1000, +1000);

    CompositeCrossover crossover =
        new CompositeCrossover(
            Arrays.asList(new IntegerSBXCrossover(1.0, 20.0), new SBXCrossover(1.0, 20.0)));

    CompositeMutation mutation =
        new CompositeMutation(
            Arrays.asList(
                new IntegerPolynomialMutation(0.1, 2.0), new PolynomialMutation(0.1, 20.0)));

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(25000);

    NSGAII<CompositeSolution> algorithm;
    algorithm =
        new NSGAII<>(
            problem, populationSize, offspringPopulationSize, crossover, mutation, termination);

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>("NSGA-II", 80, 100, null);

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    new SolutionListOutput(algorithm.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    System.exit(0);
  }
}
