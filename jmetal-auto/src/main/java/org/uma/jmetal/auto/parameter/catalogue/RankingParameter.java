package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.ranking.impl.StrengthRanking;
import org.uma.jmetal.solution.Solution;

import java.util.List;
import java.util.function.Function;

public class RankingParameter <S extends Solution<?>> extends CategoricalParameter<String> {
  public RankingParameter(String name, String args[], List<String> validRankings) {
    super(name, args, validRankings);
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--" + getName(), getArgs(), Function.identity()));

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

  public Ranking<S> getParameter() {
    Ranking<S> result ;
    switch (getValue()) {
      case "dominanceRanking":
        result = new FastNonDominatedSortRanking<>() ;
        break;
      case "strengthRanking":
        result = new StrengthRanking<>() ;
        break;
      default:
        throw new RuntimeException("Ranking does not exist: " + getName());
    }
    return result;
  }
}
