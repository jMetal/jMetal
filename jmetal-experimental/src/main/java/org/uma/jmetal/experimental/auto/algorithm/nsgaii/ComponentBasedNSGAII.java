package org.uma.jmetal.experimental.auto.algorithm.nsgaii;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.experimental.componentbasedalgorithm.util.Preference;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;


/**
 * Class to configure and run the NSGA-II using the {@link EvolutionaryAlgorithm} class.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class ComponentBasedNSGAII {
  public static void main(String[] args) {
    DoubleProblem problem = new ZDT1();

    var populationSize = 100;
    var offspringPopulationSize = 100;
    var maxNumberOfEvaluations = 50000;

    @NotNull RepairDoubleSolution crossoverSolutionRepair = new RepairDoubleSolutionWithRandomValue();
    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    @NotNull CrossoverOperator<DoubleSolution> crossover =
        new SBXCrossover(crossoverProbability, crossoverDistributionIndex, crossoverSolutionRepair);

    RepairDoubleSolution mutationSolutionRepair = new RepairDoubleSolutionWithRandomValue();
    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation =
        new PolynomialMutation(
            mutationProbability, mutationDistributionIndex, mutationSolutionRepair);

    @NotNull CrossoverAndMutationVariation<DoubleSolution> variation =
            new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    @NotNull Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    @NotNull SolutionsCreation<DoubleSolution> createInitialPopulation =
        new RandomSolutionsCreation<>(problem, populationSize);

    @NotNull Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Ranking<DoubleSolution> ranking = new MergeNonDominatedSortRanking<>();
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();

    var preferenceForReplacement = new Preference<DoubleSolution>(ranking, densityEstimator) ;
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement, Replacement.RemovalPolicy.oneShot);

    var tournamentSize = 2 ;
    var preferenceForSelection = new Preference<DoubleSolution>(ranking, densityEstimator, preferenceForReplacement) ;
    @NotNull MatingPoolSelection<DoubleSolution> selection =
        new NaryTournamentMatingPoolSelection<>(
            tournamentSize, variation.getMatingPoolSize(), preferenceForSelection);

    var algorithm =
        new EvolutionaryAlgorithm<DoubleSolution>(
            "NSGA-II",
            evaluation,
            createInitialPopulation,
            termination,
            selection,
            variation,
            replacement
        );

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
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    System.exit(0);
  }
}
