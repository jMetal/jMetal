package org.uma.jmetal.auto.algorithm.spea2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.StrengthRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.Preference;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class implementing an algorithm based on SPEA2 by using the {@link EvolutionaryAlgorithm} class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class SPEA2 {
  public static void main(String[] args) {
    DoubleProblem problem = new ZDT1();

    int populationSize = 100;
    int offspringPopulationSize = 100;
    int maxNumberOfEvaluations = 25000;

    RepairDoubleSolution crossoverSolutionRepair = new RepairDoubleSolutionWithRandomValue();
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex, crossoverSolutionRepair);

    RepairDoubleSolution mutationSolutionRepair = new RepairDoubleSolutionWithRandomValue();
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(
            mutationProbability, mutationDistributionIndex, mutationSolutionRepair);

    Variation<DoubleSolution> variation =
            new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    InitialSolutionsCreation<DoubleSolution> createInitialPopulation =
        new RandomSolutionsCreation<>(problem, populationSize);

    Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Ranking<DoubleSolution> ranking = new StrengthRanking<>() ;
    DensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);

    Preference<DoubleSolution> preferenceForReplacement = new Preference<>(ranking, densityEstimator) ;
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement, Replacement.RemovalPolicy.sequential);

    int tournamentSize = 2 ;
    Preference<DoubleSolution> preferenceForSelection = new Preference<>(ranking, densityEstimator, preferenceForReplacement) ;
    MatingPoolSelection<DoubleSolution> selection =
        new NaryTournamentMatingPoolSelection<>(
            tournamentSize, variation.getMatingPoolSize(), preferenceForSelection);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new EvolutionaryAlgorithm<>(
            "SPEA2",
            evaluation,
            createInitialPopulation,
            termination,
            selection,
            variation,
            replacement);

    //EvaluationObserver evaluationObserver = new EvaluationObserver(1000);
    //RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
        //new RunTimeChartObserver<>("NSGA-II", 80, referenceParetoFront);
    //ExternalArchiveObserver<DoubleSolution> boundedArchiveObserver =
    //    new ExternalArchiveObserver<>(new CrowdingDistanceArchive<>(archiveMaximumSize));

    //algorithm.getObservable().register(evaluationObserver);
    //algorithm.getObservable().register(runTimeChartObserver);
    //evaluation.getObservable().register(boundedArchiveObserver);

    algorithm.run();
    System.out.println("Total computing time: " + algorithm.getTotalComputingTime()) ;
    /*
    algorithm.updatePopulation(boundedArchiveObserver.getArchive().getSolutionList());
    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
        algorithm.getResult(), algorithm.getTotalComputingTime());
    */
    new SolutionListOutput(algorithm.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    System.exit(0);
  }
}
