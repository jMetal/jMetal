package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

public class RankingParameter <S extends Solution<?>> extends CategoricalParameter {
  public RankingParameter(String name, String args[], List<String> validRankings) {
    super(name, args, validRankings);
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
