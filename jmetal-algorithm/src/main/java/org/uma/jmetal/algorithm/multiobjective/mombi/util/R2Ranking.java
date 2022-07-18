package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

@SuppressWarnings("serial")
public class R2Ranking<S extends Solution<?>> extends GenericSolutionAttribute<S, R2SolutionData> {

  private AbstractUtilityFunctionsSet<S> utilityFunctions;
  private List<List<S>> rankedSubpopulations;
  private int numberOfRanks = 0;
  private @NotNull R2RankingAttribute<S> attribute = new R2RankingAttribute<>();


  public R2Ranking(AbstractUtilityFunctionsSet<S> utilityFunctions) {
    this.utilityFunctions = utilityFunctions;
  }

  public R2Ranking<S> computeRanking(List<S> population) {

    for (var solution : population) {
      solution.attributes().put(getAttributeIdentifier(), new R2SolutionData());
    }

    for (var i = 0; i < this.utilityFunctions.getSize(); i++) {
      for (var solution : population) {
        var solutionData = this.getAttribute(solution);
        solutionData.alpha = this.utilityFunctions.evaluate(solution, i);

        if (solutionData.alpha < solutionData.utility)
          solutionData.utility = solutionData.alpha;
      }

      Collections.sort(population, (o1, o2) -> {
        @NotNull R2RankingAttribute<S> attribute = new R2RankingAttribute<>();
        var data1 = (R2SolutionData) attribute.getAttribute(o1);
        var data2 = (R2SolutionData) attribute.getAttribute(o2);

        if (data1.alpha < data2.alpha)
          return -1;
        else if (data1.alpha > data2.alpha)
          return 1;
        else
          return 0;
      });

      var rank = 1;
      for (var p : population) {
        var r2Data = this.getAttribute(p);
        if (rank < r2Data.rank) {
          r2Data.rank = rank;
          numberOfRanks = Math.max(numberOfRanks, rank);
        }
        rank = rank + 1;
      }
    }

    Map<Integer, List<S>> fronts = new TreeMap<>(); // sorted on key
    for (var solution : population) {
      var r2Data = this.getAttribute(solution);
      if (fronts.get(r2Data.rank) == null)
        fronts.put(r2Data.rank, new LinkedList<S>());

      fronts.get(r2Data.rank).add(solution);
    }

    this.rankedSubpopulations = new ArrayList<>(fronts.size());
    var iterator = fronts.keySet().iterator();
    while (iterator.hasNext())
      this.rankedSubpopulations.add(fronts.get(iterator.next()));

    return this;
  }

  public List<S> getSubfront(int rank) {
    return this.rankedSubpopulations.get(rank);
  }

  public int getNumberOfSubfronts() {
    return this.rankedSubpopulations.size();
  }

  @Override
  public void setAttribute(@NotNull S solution, R2SolutionData value) {
    this.attribute.setAttribute(solution, value);
  }

  @Override
  public R2SolutionData getAttribute(S solution) {
    return this.attribute.getAttribute(solution);
  }

  @Override
  public Object getAttributeIdentifier() {
    return this.attribute.getAttributeIdentifier();
  }

  public AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
    return this.utilityFunctions;
  }
}
