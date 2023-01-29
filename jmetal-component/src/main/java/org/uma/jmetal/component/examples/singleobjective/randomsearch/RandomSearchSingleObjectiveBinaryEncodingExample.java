package org.uma.jmetal.component.examples.singleobjective.randomsearch;

import java.util.List;
import org.uma.jmetal.component.algorithm.RandomSearchAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run the a random search.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class RandomSearchSingleObjectiveBinaryEncodingExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException {
    BinaryProblem problem = new OneMax(512) ;
    Termination termination = new TerminationByEvaluations(50000);
    Evaluation<BinarySolution> evaluation = new SequentialEvaluation<>(problem) ;
    SolutionsCreation<BinarySolution> solutionsCreation = new RandomSolutionsCreation<>(problem, 1) ;

    var algorithm = new RandomSearchAlgorithm<>(
          "Random Search", solutionsCreation, evaluation,
                    termination);

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000) ;
    algorithm.getObservable().register(evaluationObserver);

    algorithm.run();

    List<BinarySolution> population = algorithm.result();
    JMetalLogger.logger.info("Total execution time : " + algorithm.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.evaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Best found solution: " + population.get(0).objectives()[0]) ;
  }
}
