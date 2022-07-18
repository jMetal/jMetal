package org.uma.jmetal.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;

/**
 * created at 10:34 pm, 2019/1/28 The comparator of DIR score used in D-NSGA-II
 *
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class DirScoreComparator<S extends Solution<?>> implements Comparator<S>, Serializable {

  @Override
  public int compare(@NotNull S o1, @NotNull S o2) {
    var score1 = Double.MAX_VALUE;
    var score2 = Double.MAX_VALUE;

    var scoreObj1 = o1.attributes().get("dir-score");
    var scoreObj2 = o2.attributes().get("dir-score");

    if (scoreObj1 != null) {
      score1 = (double) scoreObj1;
    }

    if (scoreObj2 != null) {
      score2 = (double) scoreObj2;
    }

    return Double.compare(score1, score2);
  }
}
