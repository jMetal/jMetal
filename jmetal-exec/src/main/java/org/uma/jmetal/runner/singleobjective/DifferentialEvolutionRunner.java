package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to configure and run a differential evolution algorithm. The algorithm can be configured
 * to use threads. The number of cores is specified as an optional parameter. The target problem is Sphere.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DifferentialEvolutionRunner {
  private static final int DEFAULT_NUMBER_OF_CORES = 1 ;

  /**
   *  Usage: java org.uma.jmetal.runner.singleobjective.DifferentialEvolutionRunner [cores]
   */
  public static void main(String[] args) throws Exception {

    DoubleProblem problem;
    Algorithm<DoubleSolution> algorithm;
    DifferentialEvolutionSelection selection;
    DifferentialEvolutionCrossover crossover;
    SolutionListEvaluator<DoubleSolution> evaluator ;

    problem = new Sphere(20) ;

    int numberOfCores ;
    if (args.length == 1) {
      numberOfCores = Integer.valueOf(args[0]) ;
    } else {
      numberOfCores = DEFAULT_NUMBER_OF_CORES ;
    }

    if (numberOfCores == 1) {
      evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
    } else {
      evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores, problem) ;
    }

    crossover = new DifferentialEvolutionCrossover(0.5, 0.5, "rand/1/bin") ;
    selection = new DifferentialEvolutionSelection();

    algorithm = new DifferentialEvolutionBuilder(problem)
        .setCrossover(crossover)
        .setSelection(selection)
        .setSolutionListEvaluator(evaluator)
        .setMaxEvaluations(25000)
        .setPopulationSize(100)
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    DoubleSolution solution = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    List<DoubleSolution> population = new ArrayList<>(1) ;
    population.add(solution) ;
    new SolutionListOutput(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    JMetalLogger.logger.info("Fitness: " + solution.getObjective(0)) ;

    evaluator.shutdown();
  }
}
