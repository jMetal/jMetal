package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import org.uma.jmetal.solution.Solution;

import java.util.*;

@SuppressWarnings("serial")
public class R2RankingNormalized<S extends Solution<?>> extends R2Ranking<S> {

  private List<List<S>> rankedSubpopulations;
  private int numberOfRanks 								= 0;
  private final Normalizer normalizer;


  public R2RankingNormalized(AbstractUtilityFunctionsSet<S> utilityFunctions, Normalizer normalizer) {
    super(utilityFunctions);
    this.normalizer       = normalizer;
  }

  private double computeNorm(S solution) {
    List<Double> values = new ArrayList<Double>(solution.getNumberOfObjectives());
    for (int i = 0; i < solution.getNumberOfObjectives(); i++)
      if (normalizer == null)
        values.add(solution.getObjective(i));
      else
        values.add(this.normalizer.normalize(solution.getObjective(i), i));

    double result = 0.0;
    for (Double d : values)
      result += Math.pow(d, 2.0);

    return Math.sqrt(result);
  }

  public R2RankingNormalized<S> computeRanking(List<S> population) {
    for (S solution : population) {
      R2SolutionData data =  new R2SolutionData();
      data.utility = this.computeNorm(solution);
      solution.setAttribute(getAttributeIdentifier(), data);
    }

    for (int i = 0; i < this.getUtilityFunctions().getSize(); i++) {
      for (S solution : population) {
        R2SolutionData solutionData = this.getAttribute(solution);
        solutionData.alpha = this.getUtilityFunctions().evaluate(solution, i);
      }

      Collections.sort(population, new Comparator<S>() {
        @Override
        public int compare(S o1, S o2) {
          R2RankingAttribute<S> attribute = new R2RankingAttribute<>();
          R2SolutionData data1 = (R2SolutionData) attribute.getAttribute(o1);
          R2SolutionData data2 = (R2SolutionData) attribute.getAttribute(o2);

          if (data1.alpha < data2.alpha)
            return -1;
          else if (data1.alpha > data2.alpha)
            return 1;
          else
            return 0;
        }
      });

      int rank = 1;
      for (S p : population) {
        R2SolutionData r2Data = this.getAttribute(p);
        if (rank < r2Data.rank) {
          r2Data.rank   = rank;
          numberOfRanks = Math.max(numberOfRanks, rank);
        }
        rank = rank + 1;
      }
    }

    Map<Integer, List<S>> fronts = new TreeMap<>(); // sorted on key
    for (S solution : population) {
      R2SolutionData r2Data = this.getAttribute(solution);
      if (fronts.get(r2Data.rank) == null)
        fronts.put(r2Data.rank, new LinkedList<S>());

      fronts.get(r2Data.rank).add(solution);
    }

    this.rankedSubpopulations = new ArrayList<>(fronts.size());
    Iterator<Integer> iterator = fronts.keySet().iterator();
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
}
