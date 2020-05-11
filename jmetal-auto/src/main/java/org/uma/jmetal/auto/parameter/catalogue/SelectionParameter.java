package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class SelectionParameter extends CategoricalParameter<String> {
  public SelectionParameter(String args[], List<String> selectionStrategies) {
    super("selection", args, selectionStrategies) ;
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--selection", getArgs(), Function.identity()));

    getSpecificParameters()
        .forEach(
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
              }
            });

    return this;
  }

  public MatingPoolSelection<?> getParameter(int matingPoolSize, Comparator<?> comparator) {
    MatingPoolSelection<Solution<?>> result ;
    switch(getValue()) {
      case "tournament":
        int tournamentSize =
                (Integer) findSpecificParameter("selectionTournamentSize").getValue();
        /*
        String rankingName = (String) findSpecificParameter("rankingForSelection").getValue() ;
        String densityEstimatorName = (String) findSpecificParameter("densityEstimatorForSelection").getValue() ;
        Ranking<?> ranking ;
        if (rankingName.equals("dominanceRanking")) {
        } else {
          ranking = new StrengthRanking<>() ;
        }

        DensityEstimator<?> densityEstimator ;
        if (densityEstimatorName.equals("crowdingDistance")){
        } else {
          densityEstimator = new KnnDensityEstimator<>(1) ;
        }
        */
        Ranking<Solution<?>> ranking = new FastNonDominatedSortRanking<>();
        DensityEstimator<Solution<?>> densityEstimator = new CrowdingDistanceDensityEstimator<>();

        MultiComparator<Solution<?>> rankingAndCrowdingComparator =
            new MultiComparator<>(
                Arrays.asList(
                    ranking.getSolutionComparator(), densityEstimator.getSolutionComparator()));
        result = new NaryTournamentMatingPoolSelection<>(
                tournamentSize, matingPoolSize, rankingAndCrowdingComparator);

        break ;
      case "random":
        result = new RandomMatingPoolSelection<>(matingPoolSize);
        break ;
      default:
        throw new RuntimeException("Selection component unknown: " + getValue()) ;
    }

    return result ;
  }
}
