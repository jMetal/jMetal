package org.uma.jmetal.algorithm.examples.multiobjective.mocell;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the MOCell algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOCellChangingMutationAndCrossoverProbabilitiesRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
    java org.uma.jmetal.runner.multiobjective.MOCellRunner problemName [referenceFront]
   */
  public static void main(String @NotNull [] args) throws JMetalException, FileNotFoundException {
    var referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.csv" ;
    }

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    @SuppressWarnings("serial")
    class MOCellWithChangesInVariationOperator  extends MOCell<DoubleSolution> {

      /**
       * Constructor
       *
       * @param problem
       * @param maxEvaluations
       * @param populationSize
       * @param archive
       * @param neighborhood
       * @param crossoverOperator
       * @param mutationOperator
       * @param selectionOperator
       * @param evaluator
       */
      public MOCellWithChangesInVariationOperator(
              Problem<DoubleSolution> problem, int maxEvaluations, int populationSize,
              BoundedArchive<DoubleSolution> archive, Neighborhood<DoubleSolution> neighborhood,
              CrossoverOperator<DoubleSolution> crossoverOperator, MutationOperator<DoubleSolution> mutationOperator,
              SelectionOperator<List<DoubleSolution>, DoubleSolution> selectionOperator,
              SolutionListEvaluator<DoubleSolution> evaluator) {
        super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator,
                selectionOperator, evaluator);
      }

      @Override
      public void updateProgress() {
        super.updateProgress();

        if (evaluations > 10000) {
          crossoverOperator = new SBXCrossover(0.7, 20.0) ;
          mutationOperator = new PolynomialMutation(0.001, 30.0) ;
        }
      }
    }

    Algorithm<List<DoubleSolution>> algorithm = new MOCellWithChangesInVariationOperator(
            problem,
            25000,
            100,
            new CrowdingDistanceArchive<>(100),
            new C9<DoubleSolution>((int) Math.sqrt(100), (int) Math.sqrt(100)),
            crossover,
            mutation,
            selection,
            new SequentialSolutionListEvaluator<DoubleSolution>());

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    var population = algorithm.getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
