package org.uma.jmetal.experimental.auto.irace;

import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.nsgaii.AutoNSGAII;
import org.uma.jmetal.experimental.auto.parameter.*;
import org.uma.jmetal.experimental.auto.parameter.catalogue.*;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

public class AutoNSGAIIIrace {
  public static void main(String[] args) throws IOException {
    AutoNSGAII nsgaiiWithParameters = new AutoNSGAII();
    nsgaiiWithParameters.parseAndCheckParameters(args);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create();
    nsgaII.run();

    String referenceFrontFile =
        "resources/referenceFrontsCSV/" + nsgaiiWithParameters.referenceFrontFilename.getValue();

    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[][] front = getMatrixWithObjectiveValues(nsgaII.getResult()) ;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var qualityIndicator = new NormalizedHypervolume(normalizedReferenceFront) ;
    System.out.println(qualityIndicator.compute(normalizedFront)) ;
  }
}
