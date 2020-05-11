package org.uma.jmetal.auto.parameter.catalogue;


import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.ranking.impl.StrengthRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ReplacementParameter extends CategoricalParameter<String> {
  public ReplacementParameter(String args[], List<String> selectionStrategies) {
    super("replacement", args, selectionStrategies);
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--replacement", getArgs(), Function.identity()));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
              }
            });

    return this;
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
                  ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);
        } else {
          result =
              new RankingAndDensityEstimatorReplacement<>(
                  ranking, densityEstimator, Replacement.RemovalPolicy.sequential);
        }

        break;
      default:
        throw new RuntimeException("Replacement component unknown: " + getValue());
    }

    return result;
  }
}
