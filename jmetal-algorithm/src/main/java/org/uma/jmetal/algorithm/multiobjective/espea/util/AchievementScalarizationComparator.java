package org.uma.jmetal.algorithm.multiobjective.espea.util;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

/**
 * Compares solutions based on their achievement scalarization value (ASV). The
 * ASV is always defined for a specific objective k. A solution x dominates
 * solution y w.r.t. to their ASV, if the maximum of all objectives without k is
 * smaller for x compared to y. If both maxima are the same, solutions are
 * compared w.r.t. to objective k. Achievement scalarization values can be used
 * for identifying extreme points.
 *
 * @param <S> The solution type.
 * @author marlon.braun <marlon.braun@partner.kit.edu>
 */
public class AchievementScalarizationComparator<S extends Solution<?>> implements Comparator<S> {

  /**
   * The objective on which the achievement scalarization value is based.
   */
  private final int objective;

  /**
   * The achievement scalarization comparator requires an objective for which
   * it is defined.
   *
   * @param objective The objective for which achievement scalarizationv alues are
   *                  computed.
   */
  public AchievementScalarizationComparator(int objective) {
    this.objective = objective;
  }

  @Override
  public int compare(S s1, S s2) {

    double max1 = -Double.MAX_VALUE;
    double max2 = -Double.MAX_VALUE;

    for (int i = 0; i < s1.getNumberOfObjectives(); i++) {
      if (i != objective) {
        max1 = Math.max(max1, s1.getObjective(i));
        max2 = Math.max(max2, s2.getObjective(i));
      }
    }

    if (max1 < max2)
      return -1;
    if (max1 > max2)
      return 1;

    // max1 = max2
    if (s1.getObjective(objective) < s2.getObjective(objective))
      return -1;
    if (s1.getObjective(objective) > s2.getObjective(objective))
      return 1;

    return 0;
  }
}
