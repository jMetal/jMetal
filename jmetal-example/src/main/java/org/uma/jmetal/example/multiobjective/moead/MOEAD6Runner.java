package org.uma.jmetal.example.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD6;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the MOEA/D algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEAD6Runner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.moead.MOEADRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws FileNotFoundException {
    DoubleProblem problem;
    MOEAD6<DoubleSolution> algorithm;
    MutationOperator<DoubleSolution> mutation;
    DifferentialEvolutionCrossover crossover;

    String problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
    String referenceParetoFront = "referenceFronts/LZ09_F2.pf";

    /*
         Problem<S> problem,
     int populationSize,
     int neighborSize,
     double neighborhoodSelectionProbability,
     int maximumNumberOfReplacedSolutions,
     AggregativeFunction aggregativeFunction,
     SequenceGenerator<Integer> sequenceGenerator,
     MutationOperator<S> mutationOperator,
     Termination termination
    */

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double cr = 1.0;
    double f = 0.5;
    crossover =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm =
        new MOEAD6<DoubleSolution>(
            problem,
            100,
            20,
            0.9,
            2,
            new Tschebyscheff(),
            new IntegerPermutationGenerator(100),
            mutation,
            new TerminationByEvaluations(150000));

    /*
    new MOEADBuilder(problem, MOEADBuilder.Variant.MOEAD)
        .setCrossover(crossover)
        .setMutation(mutation)
        .setMaxEvaluations(150000)
        .setPopulationSize(300)
        .setResultPopulationSize(100)
        .setNeighborhoodSelectionProbability(0.9)
        .setMaximumNumberOfReplacedSolutions(2)
        .setNeighborSize(20)
        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
        .build();
    */

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }

    System.exit(0);
  }
}
