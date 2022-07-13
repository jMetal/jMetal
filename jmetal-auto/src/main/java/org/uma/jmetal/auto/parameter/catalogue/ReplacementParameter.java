package org.uma.jmetal.auto.parameter.catalogue;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

public class ReplacementParameter extends CategoricalParameter {
  public ReplacementParameter(String args[], List<String> selectionStrategies) {
    super("replacement", args, selectionStrategies);
  }

  public Replacement<?> getParameter(Comparator<DoubleSolution> comparator) {
    String removalPolicy = (String) findGlobalParameter("removalPolicy").getValue();
    Replacement<?> result;
    switch (getValue()) {
      case "rankingAndDensityEstimatorReplacement":
        String rankingName = (String) findSpecificParameter("rankingForReplacement").getValue();
        String densityEstimatorName =
            (String) findSpecificParameter("densityEstimatorForReplacement").getValue();

        Ranking<Solution<?>> ranking;
        if (rankingName.equals("dominanceRanking")) {
          ranking = new FastNonDominatedSortRanking<>();
        } else {
          ranking = new StrengthRanking<>();
        }

        DensityEstimator<Solution<?>> densityEstimator;
        if (densityEstimatorName.equals("crowdingDistance")) {
          densityEstimator = new CrowdingDistanceDensityEstimator<>();
        } else {
          densityEstimator = new KnnDensityEstimator<>(1);
        }

        if (removalPolicy.equals("oneShot")) {
          result =
              new RankingAndDensityEstimatorReplacement<>(
                  ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);
        } else {
          result =
              new RankingAndDensityEstimatorReplacement<>(
                  ranking, densityEstimator, Replacement.RemovalPolicy.SEQUENTIAL);
        }

        break;
      default:
        throw new JMetalException("Replacement component unknown: " + getValue());
    }

    return result;
  }
}
