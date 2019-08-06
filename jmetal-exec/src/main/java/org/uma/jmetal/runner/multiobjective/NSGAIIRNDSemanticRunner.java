package org.uma.jmetal.runner.multiobjective;

import java.util.function.Function;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.RadioNetworkDesign;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.semantic.OWLUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRNDSemanticRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException Invoking command:
   *                               java org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<BinarySolution> problem;
    Algorithm<List<BinarySolution>> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    String referenceParetoFront = "";


    problem = new RadioNetworkDesign(100);
    crossover = new SinglePointCrossover(1.0);
    mutation =  new BitFlipMutation(
            1.0 / ((BinaryProblem) problem).getNumberOfBits(0));

    /*Function<BinarySolution, Double> constraint1;
    Function<BinarySolution, Double> constraint2;
    constraint1 = solution -> !solution.getVariableValue(0).get(0) ? 0.0 : -1.0 * Double.MAX_VALUE;

    constraint2 = solution -> solution.getObjective(1)<=(100-80) ? 0.0 : -1.0 * solution.getObjective(1);
    ((RadioNetworkDesign) problem).addConstraint(constraint1);
    ((RadioNetworkDesign) problem).addConstraint(constraint2);*/
    /*OWLUtils owlUtils = new OWLUtils("jmetal-core/src/main/resources/ontology/traffic-tsp.owl");
    owlUtils.addImport(
            "/Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/traffic.owl",
            "http://www.khaos.uma.es/perception/traffic/khaosteam");
    owlUtils.addImport(
            "/Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/bigowl.owl",
            "http://www.khaos.uma.es/perception/bigowl");
    owlUtils.loadOntology();
    List<Function<BinarySolution, Double>> constraintList =
            owlUtils.getRNDConstraintFromOntology();
    if (constraintList != null && !constraintList.isEmpty()) {
      for (Function<BinarySolution, Double> constraint : constraintList) {
        ((RadioNetworkDesign) problem).addConstraint(constraint);
      }
    }*/
    int populationSize = 100 ;
     algorithm = new NSGAIIBuilder<BinarySolution>(
           problem,
            crossover,
            mutation,
             populationSize)
            .setMaxEvaluations(25000)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<BinarySolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
