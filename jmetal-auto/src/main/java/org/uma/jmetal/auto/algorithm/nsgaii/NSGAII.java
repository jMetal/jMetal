package org.uma.jmetal.auto.algorithm.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.createinitialsolutions.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.evaluation.Evaluation;
import org.uma.jmetal.auto.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.observer.impl.EvaluationObserver;
import org.uma.jmetal.auto.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.auto.replacement.Replacement;
import org.uma.jmetal.auto.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.selection.MatingPoolSelection;
import org.uma.jmetal.auto.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.termination.Termination;
import org.uma.jmetal.auto.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.auto.variation.Variation;
import org.uma.jmetal.auto.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.DefaultAlgorithmOutputData;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.Arrays;

public class NSGAII {
  public static void main(String[] args) {
    DoubleProblem problem = new ZDT1();
    String referenceParetoFront = "pareto_fronts/ZDT1.pf" ;

    int populationSize = 100;
    int offspringPopulationSize = 100;
    int maxNumberOfEvaluations = 25000;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    CreateInitialSolutions<DoubleSolution> createInitialPopulation =
        new RandomSolutionsCreation<>(problem, populationSize);

    Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Variation<DoubleSolution> variation =
        new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>()) ;
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>() ;

    MultiComparator<DoubleSolution> rankingAndCrowdingComparator = new MultiComparator<>(
        Arrays.asList(
                ranking.getSolutionComparator(),
                densityEstimator.getSolutionComparator()));

    MatingPoolSelection<DoubleSolution> selection =
        new NaryTournamentMatingPoolSelection<>(
            2, variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Replacement<DoubleSolution> replacement = new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator) ;

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new EvolutionaryAlgorithm<>(
                "NSGA-II",
                evaluation,
                createInitialPopulation,
                termination,
                selection,
                variation,
                replacement);

    EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        new RunTimeChartObserver<>("NSGA-II", 80, referenceParetoFront) ;

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    DefaultAlgorithmOutputData.generateMultiObjectiveAlgorithmOutputData(
        algorithm.getResult(), algorithm.getTotalComputingTime());

    System.exit(0);
  }
}
