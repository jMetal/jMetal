package org.uma.jmetal.auto.util.ranking.impl;

import org.uma.jmetal.auto.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.auto.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class a null ranking scheme, i.e., the result is a set with all the solutions (no ranking is applied)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NullRanking<S extends Solution<?>> implements Ranking<S> {
  private String attributeId = getClass().getName();

  private List<List<S>> rankedSubPopulations;
  private Comparator<S> solutionComparator;

  public NullRanking() {
    this.solutionComparator = new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.ASCENDING) ;
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionList) {
    rankedSubPopulations = new ArrayList<>(1);
    solutionList.forEach(solution -> {solution.setAttribute(attributeId,0) ;});
    rankedSubPopulations.add(solutionList) ;

    return this;
  }

  @Override
  public List<S> getSubFront(int rank) {
    if (rank >= rankedSubPopulations.size()) {
      throw new JMetalException(
          "Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() - 1));
    }
    return rankedSubPopulations.get(rank);
  }

  @Override
  public int getNumberOfSubFronts() {
    return rankedSubPopulations.size();
  }

  @Override
  public Comparator<S> getSolutionComparator() {
    return solutionComparator;
  }

  @Override
  public String getAttributeId() {
    return attributeId;
  }
}
