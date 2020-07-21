package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.algorithm.multiobjective.smpso.jmetal5version.SMPSOBuilder;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2007;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2011;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.problem.singleobjective.Rosenbrock;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class to configure and run a steady-state genetic algorithm. The target problem is TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SteadyStateGeneticAlgorithmBinaryEncodingRunner {
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.SteadyStateGeneticAlgorithmBinaryEncodingRunner
   */
  public static void main(String[] args) throws Exception {
    BinaryProblem problem;
    Algorithm<BinarySolution> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    SelectionOperator<List<BinarySolution>, BinarySolution> selection;

    problem = new OneMax(1024) ;

    crossover = new SinglePointCrossover(0.9) ;

    double mutationProbability = 1.0 / problem.getBitsFromVariable(0) ;
    mutation = new BitFlipMutation(mutationProbability) ;

    selection = new BinaryTournamentSelection<BinarySolution>();

    algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
        .setPopulationSize(50)
        .setMaxEvaluations(25000)
        .setSelectionOperator(selection)
        .setVariant(GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE)
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    long computingTime = algorithmRunner.getComputingTime() ;

    BinarySolution solution = algorithm.getResult() ;
    List<BinarySolution> population = new ArrayList<>(1) ;
    population.add(solution) ;

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    JMetalLogger.logger.info("Fitness: " + solution.getObjective(0)) ;
    JMetalLogger.logger.info("Solution: " + solution.getVariable(0)) ;
  }

    /**
     * Class to configure and run a coral reefs optimization algorithm. The target
     * problem is OneMax.
     *
     * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
     */
    public static class CoralReefsOptimizationRunner {
        /**
         * Usage: java
         * org.uma.jmetal.runner.singleobjective.CoralReefsOptimizationRunner
         */
        public static void main(String[] args) throws Exception {
            Algorithm<List<BinarySolution>> algorithm;
            BinaryProblem problem = new OneMax(512);

            CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(
                    0.9);
            MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(
                    1.0 / problem.getBitsFromVariable(0));
            SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator = new BinaryTournamentSelection<BinarySolution>();

            algorithm = new CoralReefsOptimizationBuilder<BinarySolution>(problem,
                    selectionOperator, crossoverOperator, mutationOperator)
                    .setM(10).setN(10).setRho(0.6).setFbs(0.9).setFbr(0.1)
                    .setFa(0.1).setPd(0.1).setAttemptsToSettle(3)
                    .setComparator(new ObjectiveComparator<BinarySolution>(0))
                    .build();

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(
                    algorithm).execute();

            List<BinarySolution> population = algorithm.getResult();

            long computingTime = algorithmRunner.getComputingTime();

            new SolutionListOutput(population)
                    .setVarFileOutputContext(
                            new DefaultFileOutputContext("VAR.tsv"))
                    .setFunFileOutputContext(
                            new DefaultFileOutputContext("FUN.tsv")).print();

            JMetalLogger.logger.info("Total execution time: " + computingTime
                    + "ms");
            JMetalLogger.logger
                    .info("Objectives values have been written to file FUN.tsv");
            JMetalLogger.logger
                    .info("Variables values have been written to file VAR.tsv");

        }
    }

    /**
     */
    public static class CovarianceMatrixAdaptationEvolutionStrategyRunner {
      /**
       */
      public static void main(String[] args) throws Exception {

        Algorithm<DoubleSolution> algorithm;
        DoubleProblem problem = new Sphere() ;

        algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem)
                .build() ;


        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        DoubleSolution solution = algorithm.getResult() ;
        List<DoubleSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

      }
    }

    /**
     * Class to configure and run a differential evolution algorithm. The algorithm can be configured to
     * use threads. The number of cores is specified as an optional parameter. The target problem is
     * Sphere.
     *
     * @author Antonio J. Nebro <antonio@lcc.uma.es>
     */
    public static class DifferentialEvolutionRunner {
      private static final int DEFAULT_NUMBER_OF_CORES = 1;

      /** Usage: java org.uma.jmetal.runner.singleobjective.DifferentialEvolutionRunner [cores] */
      public static void main(String[] args) throws Exception {

        DoubleProblem problem;
        Algorithm<DoubleSolution> algorithm;
        DifferentialEvolutionSelection selection;
        DifferentialEvolutionCrossover crossover;
        SolutionListEvaluator<DoubleSolution> evaluator;

        problem = new Sphere(20);

        int numberOfCores;
        if (args.length == 1) {
          numberOfCores = Integer.valueOf(args[0]);
        } else {
          numberOfCores = DEFAULT_NUMBER_OF_CORES;
        }

        if (numberOfCores == 1) {
          evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
        } else {
          evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores);
        }

        crossover =
            new DifferentialEvolutionCrossover(
                0.5, 0.5, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
        selection = new DifferentialEvolutionSelection();

        algorithm =
            new DifferentialEvolutionBuilder(problem)
                .setCrossover(crossover)
                .setSelection(selection)
                .setSolutionListEvaluator(evaluator)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        DoubleSolution solution = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        List<DoubleSolution> population = new ArrayList<>(1);
        population.add(solution);
        new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));

        evaluator.shutdown();
      }
    }

    /**
     * Class to configure and run an elitist (mu + lambda) evolution strategy. The target problem is
     * OneMax.
     *
     * @author Antonio J. Nebro <antonio@lcc.uma.es>
     */
    public static class ElitistEvolutionStrategyRunner {
      /**
       * Usage: java org.uma.jmetal.runner.singleobjective.ElitistEvolutionStrategyRunner
       */
      public static void main(String[] args) throws Exception {
        Algorithm<BinarySolution> algorithm;
        BinaryProblem problem = new OneMax(512) ;

        MutationOperator<BinarySolution> mutationOperator =
            new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;

        algorithm = new EvolutionStrategyBuilder<BinarySolution>(problem, mutationOperator,
            EvolutionStrategyBuilder.EvolutionStrategyVariant.ELITIST)
            .setMaxEvaluations(25000)
            .setMu(1)
            .setLambda(10)
            .build() ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

        BinarySolution solution = algorithm.getResult() ;
        List<BinarySolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
      }
    }

  /**
   * Class to configure and run a single objective local search. The target problem is OneMax.
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class LocalSearchRunner {
    /**
     * Usage: java org.uma.jmetal.runner.singleobjective.LocalSearchRunner
     */
    public static void main(String[] args) throws Exception {
      BinaryProblem problem = new OneMax(1024) ;

      MutationOperator<BinarySolution> mutationOperator =
          new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;

      int improvementRounds = 10000 ;

      Comparator<BinarySolution> comparator = new DominanceComparator<>() ;

      DefaultLocalSearch<BinarySolution> localSearch = new DefaultLocalSearch<BinarySolution>(
              improvementRounds,
              problem,
              mutationOperator,
              comparator) ;

      localSearch.run();

      BinarySolution newSolution = localSearch.getResult() ;

      JMetalLogger.logger.info("Fitness: " + newSolution.getObjective(0)) ;
      JMetalLogger.logger.info("Solution: " + newSolution.getVariable(0)) ;
    }
  }

  /**
   * Class to configure and run a non elitist (mu,lamba) evolution strategy. The target problem is
   * OneMax.
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class NonElitistEvolutionStrategyRunner {
    /**
     * Usage: java org.uma.jmetal.runner.singleobjective.NonElitistEvolutionStrategyRunner
     */
    public static void main(String[] args) throws Exception {

      Algorithm<BinarySolution> algorithm;
      BinaryProblem problem = new OneMax(512) ;

      MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;

      algorithm = new EvolutionStrategyBuilder<BinarySolution>(problem, mutationOperator,
          EvolutionStrategyBuilder.EvolutionStrategyVariant.NON_ELITIST)
          .setMaxEvaluations(25000)
          .setMu(1)
          .setLambda(10)
          .build() ;

      AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
              .execute() ;

      BinarySolution solution = algorithm.getResult() ;
      List<BinarySolution> population = new ArrayList<>(1) ;
      population.add(solution) ;

      long computingTime = algorithmRunner.getComputingTime() ;

      new SolutionListOutput(population)
              .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
              .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
              .print();

      JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
      JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    }
  }

  /**
   * Class to configure and run a parallel (multithreaded) generational genetic algorithm. The number
   * of cores is specified as an optional parameter. A default value is used is the parameter is not
   * provided. The target problem is OneMax
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class ParallelGenerationalGeneticAlgorithmRunner {
    private static final int DEFAULT_NUMBER_OF_CORES = 0;
    /**
     * Usage: java org.uma.jmetal.runner.singleobjective.ParallelGenerationalGeneticAlgorithmRunner
     * [cores]
     */
    public static void main(String[] args) throws Exception {
      Algorithm<BinarySolution> algorithm;
      BinaryProblem problem = new OneMax(512);

      int numberOfCores;
      if (args.length == 1) {
        numberOfCores = Integer.valueOf(args[0]);
      } else {
        numberOfCores = DEFAULT_NUMBER_OF_CORES;
      }

      CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(0.9);
      MutationOperator<BinarySolution> mutationOperator =
          new BitFlipMutation(1.0 / problem.getBitsFromVariable(0));
      SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator =
          new BinaryTournamentSelection<BinarySolution>();

      GeneticAlgorithmBuilder<BinarySolution> builder =
          new GeneticAlgorithmBuilder<BinarySolution>(problem, crossoverOperator, mutationOperator)
              .setPopulationSize(100)
              .setMaxEvaluations(25000)
              .setSelectionOperator(selectionOperator)
              .setSolutionListEvaluator(
                  new MultithreadedSolutionListEvaluator<BinarySolution>(numberOfCores));

      algorithm = builder.build();

      AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

      builder.getEvaluator().shutdown();

      BinarySolution solution = algorithm.getResult();
      List<BinarySolution> population = new ArrayList<>(1);
      population.add(solution);

      long computingTime = algorithmRunner.getComputingTime();

      new SolutionListOutput(population)
          .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
          .print();

      JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
      JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    }
  }

  /**
   * Class for configuring and running the SMPSO algorithm to solve a single-objective problem
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class SMPSORunner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments. The first (optional) argument specifies
     *             the problem to solve.
     * @throws org.uma.jmetal.util.JMetalException
     * @throws java.io.IOException
     * @throws SecurityException
     * Invoking command:
    java org.uma.jmetal.runner.multiobjective.smpso.SMPSORunner problemName [referenceFront]
     */
    public static void main(String[] args) throws Exception {
      DoubleProblem problem;
      Algorithm<List<DoubleSolution>> algorithm;
      MutationOperator<DoubleSolution> mutation;

      problem = new Rosenbrock(20) ;

      BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

      double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
      double mutationDistributionIndex = 20.0 ;
      mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

      algorithm = new SMPSOBuilder(problem, archive)
          .setMutation(mutation)
          .setMaxIterations(25)
          .setSwarmSize(100)
          .setRandomGenerator(new MersenneTwisterGenerator())
          .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
          .build();

      AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
          .execute();

      List<DoubleSolution> population = algorithm.getResult();
      long computingTime = algorithmRunner.getComputingTime();

      new SolutionListOutput(population)
              .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
              .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
              .print();

      JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
      JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

      JMetalLogger.logger.info("Fitness: " + population.get(0).getObjective(0)) ;
    }
  }

  /**
   * Class to configure and run a StandardPSO2007. The algorithm can be configured to use threads. The
   * number of cores is specified as an optional parameter. The target problem is Sphere.
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class StandardPSO2007Runner {
    private static final int DEFAULT_NUMBER_OF_CORES = 1;

    /** Usage: java org.uma.jmetal.runner.singleobjective.StandardPSO2007Runner [cores] */
    public static void main(String[] args) throws Exception {

      DoubleProblem problem;
      Algorithm<DoubleSolution> algorithm;
      SolutionListEvaluator<DoubleSolution> evaluator;

      String problemName = "org.uma.jmetal.problem.singleobjective.Sphere";

      problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

      int numberOfCores;
      if (args.length == 1) {
        numberOfCores = Integer.valueOf(args[0]);
      } else {
        numberOfCores = DEFAULT_NUMBER_OF_CORES;
      }

      if (numberOfCores == 1) {
        evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
      } else {
        evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores);
      }

      algorithm =
          new StandardPSO2007(
              problem,
              10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())),
              25000,
              3,
              evaluator);

      AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

      DoubleSolution solution = algorithm.getResult();
      long computingTime = algorithmRunner.getComputingTime();

      List<DoubleSolution> population = new ArrayList<>(1);
      population.add(solution);
      new SolutionListOutput(population)
          .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
          .print();

      JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
      JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

      JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));
      JMetalLogger.logger.info("Solution: " + solution.getVariable(0));
      evaluator.shutdown();
    }
  }

  /**
   * Class to configure and run a StandardPSO2007. The algorithm can be configured to use threads. The
   * number of cores is specified as an optional parameter. The target problem is Sphere.
   *
   * @author Antonio J. Nebro <antonio@lcc.uma.es>
   */
  public static class StandardPSO2011Runner {
    private static final int DEFAULT_NUMBER_OF_CORES = 1;

    /** Usage: java org.uma.jmetal.runner.singleobjective.StandardPSO2007Runner [cores] */
    public static void main(String[] args) throws Exception {

      DoubleProblem problem;
      Algorithm<DoubleSolution> algorithm;
      SolutionListEvaluator<DoubleSolution> evaluator;

      String problemName = "org.uma.jmetal.problem.singleobjective.Sphere";

      problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

      int numberOfCores;
      if (args.length == 1) {
        numberOfCores = Integer.valueOf(args[0]);
      } else {
        numberOfCores = DEFAULT_NUMBER_OF_CORES;
      }

      if (numberOfCores == 1) {
        evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
      } else {
        evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores);
      }

      algorithm =
          new StandardPSO2011(
              problem,
              10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())),
              25000,
              3,
              evaluator);

      AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

      DoubleSolution solution = algorithm.getResult();
      long computingTime = algorithmRunner.getComputingTime();

      List<DoubleSolution> population = new ArrayList<>(1);
      population.add(solution);
      new SolutionListOutput(population)
          .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
          .print();

      JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
      JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
      JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

      JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));
      JMetalLogger.logger.info("Solution: " + solution.getVariable(0));
      evaluator.shutdown();
    }
  }
}
