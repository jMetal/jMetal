package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

public class RankingParameter <S extends Solution<?>> extends CategoricalParameter {
  public RankingParameter(String name,List<String> validRankings) {
    super(name, validRankings);
  }

  public Ranking<S> getParameter() {
    Ranking<S> result ;
    switch (value()) {
      case "dominanceRanking":
        result = new FastNonDominatedSortRanking<>() ;
        break;
      case "strengthRanking":
        result = new StrengthRanking<>() ;
        break;
      default:
        throw new JMetalException("Ranking does not exist: " + name());
    }
    return result;
  }
}
