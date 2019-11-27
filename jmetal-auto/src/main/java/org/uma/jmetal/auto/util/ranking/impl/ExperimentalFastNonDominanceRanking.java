package org.uma.jmetal.auto.util.ranking.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.auto.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.auto.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

// This is the common code for both existing implementations.
import org.uma.jmetal.util.solutionattribute.impl.ExperimentalFastDominanceRanking;

/**
 * This is an implementation of the {@link Ranking} interface using non-dominated sorting algorithms
 * from the <a href="https://github.com/mbuzdalov/non-dominated-sorting">non-dominated sorting repository</a>.
 *
 * It uses the solution's objectives directly, and not the dominance comparators,
 * as the structure of the efficient algorithms requires it to be this way.
 *
 * Additionally, if {@link ConstraintHandling#overallConstraintViolationDegree(Solution)} is less than
 * zero, it is used as a preliminary comparison key: all solutions are first sorted in the decreasing order of this
 * property, then non-dominated sorting is executed for each block of solutions with equal value of this property.
 *
 * @param <S> the exact type of a solution.
 * @author Maxim Buzdalov
 */
public class ExperimentalFastNonDominanceRanking<S extends Solution<?>> implements Ranking<S> {
  private String attributeId = getClass().getName() ;
  private Comparator<S> solutionComparator;
  private ExperimentalFastDominanceRanking.NonDominatedFrontAssigner<S> assigner =
          new ExperimentalFastDominanceRanking.NonDominatedFrontAssigner<>();
  private final List<List<S>> subFronts = new ArrayList<>();

  public ExperimentalFastNonDominanceRanking() {
    this.solutionComparator =
        new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.ASCENDING);
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionList) {
    assigner.assignFronts(solutionList, subFronts);
    for (int rank = 0; rank < subFronts.size(); ++rank) {
      Integer theRank = rank;
      for (S solution : subFronts.get(rank)) {
        solution.setAttribute(attributeId, theRank);
      }
    }
    return this;
  }

  @Override
  public List<S> getSubFront(int rank) {
    return subFronts.get(rank);
  }

  @Override
  public int getNumberOfSubFronts() {
    return subFronts.size();
  }

  @Override
  public Comparator<S> getSolutionComparator() {
    return solutionComparator;
  }

  @Override
  public String getAttributeId() {
    return attributeId ;
  }
}
