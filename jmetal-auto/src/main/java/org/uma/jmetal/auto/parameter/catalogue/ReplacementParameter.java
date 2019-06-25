package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.DifferentialEvolutionMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ReplacementParameter extends CategoricalParameter<String> {
  public ReplacementParameter(String args[], List<String> selectionStrategies) {
    super("replacement", args, selectionStrategies) ;
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--replacement", getArgs(), Function.identity()));

    getSpecificParameters()
        .forEach(
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
              }
            });

    return this;
  }

  public Replacement<?> getParameter() {
    Replacement<?> result ;
    switch(getValue()) {
      case "rankingAndDensityEstimatorReplacement":
        Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>());
        DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();

        result = new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator);

        break ;
      default:
        throw new RuntimeException("Replacement component unknown: " + getValue()) ;
    }

    return result ;
  }
}
