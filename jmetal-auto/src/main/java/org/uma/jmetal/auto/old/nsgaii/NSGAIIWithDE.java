package org.uma.jmetal.auto.old.nsgaii;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.DifferentialEvolutionMatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F3;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmDefaultOutputData;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.ExternalArchiveObserver;

public class NSGAIIWithDE {
  public static void main(String[] args) {
    DoubleProblem problem = new LZ09F3();
    String referenceParetoFront = "/pareto_fronts/LZ09_F3.pf";

    int populationSize = 100;
    int offspringPopulationSize = 100;
    int archiveMaximumSize = 100;
    int maxNumberOfEvaluations = 175000;

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    InitialSolutionsCreation<DoubleSolution> createInitialPopulation =
        new RandomSolutionsCreation<>(problem, populationSize);

    Termination termination = new TerminationByEvaluations(maxNumberOfEvaluations);

    Variation<DoubleSolution> variation =
        new DifferentialCrossoverVariation(
            offspringPopulationSize,
            new DifferentialEvolutionCrossover(1.0, 0.5, "rand/1/bin"),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 200.0));

    Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>());
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();

    MatingPoolSelection<DoubleSolution> selection =
        new DifferentialEvolutionMatingPoolSelection(variation.getMatingPoolSize());

    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new EvolutionaryAlgorithm<>(
            "NSGA-II",
            evaluation,
            createInitialPopulation,
            termination,
            selection,
            variation,
            replacement, null);

    EvaluationObserver evaluationObserver = new EvaluationObserver(5000);
    // RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
    //    new RunTimeChartObserver<>("NSGA-II", 80, referenceParetoFront);
    ExternalArchiveObserver<DoubleSolution> boundedArchiveObserver =
        new ExternalArchiveObserver<>(new CrowdingDistanceArchive<>(archiveMaximumSize));

    algorithm.getObservable().register(evaluationObserver);
    // algorithm.getObservable().register(runTimeChartObserver);
    // algorithm.getObservable().register(new RunTimeChartObserver<>("EVALS", 80,
    // referenceParetoFront));
    //evaluation.getObservable().register(boundedArchiveObserver);

    algorithm.run();

    algorithm.updatePopulation(boundedArchiveObserver.getArchive().getSolutionList());
    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(
        algorithm.getResult(), algorithm.getTotalComputingTime());

    new SolutionListOutput(algorithm.getResult())
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    System.exit(0);
  }
}
