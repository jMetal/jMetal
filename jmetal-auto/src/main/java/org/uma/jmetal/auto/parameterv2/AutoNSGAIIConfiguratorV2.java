package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.auto.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.DifferentialEvolutionMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.auto.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.auto.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.auto.irace.parameter.algorithmresult.AlgorithmResultType;
import org.uma.jmetal.auto.irace.parameter.createinitialsolutionsstrategy.CreateInitialSolutionsStrategyType;
import org.uma.jmetal.auto.irace.parameter.crossover.CrossoverType;
import org.uma.jmetal.auto.irace.parameter.mutation.MutationType;
import org.uma.jmetal.auto.irace.parameter.repairstrategy.RepairStrategyType;
import org.uma.jmetal.auto.irace.parameter.selection.SelectionType;
import org.uma.jmetal.auto.irace.parameter.variation.VariationType;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.observer.impl.ExternalArchiveObserver;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.SimpleRandomMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import picocli.CommandLine.Option;

import java.util.Arrays;
import java.util.List;

public class AutoNSGAIIConfiguratorV2 {

  String[] parameters = {
    " --algorithmResult externalArchive " +
            "--populationSizeWithArchive 20 " +
            "--offspringPopulationSize 5 "
        + "--variation crossoverAndMutationVariation " +
            "--createInitialSolutions latinHypercubeSampling "
        + "--crossover SBX " +
            "--crossoverProbability 0.9791 "
        + "--crossoverRepairStrategy round " +
            "--sbxCrossoverDistributionIndex 5.0587 "
        + "--mutation uniform " +
            "--mutationProbability 0.0463 "
        + "--mutationRepairStrategy random " +
            "--uniformMutationPerturbation 0.2307 "
        + "--selection tournament " +
            "--selectionTournamentSize 4  "
  };

  // int populat

  // MutationOperator<DoubleSolution> mutation =

  public EvolutionaryAlgorithm<DoubleSolution> configureAndGetAlgorithm() {

    return null;
  }
}
