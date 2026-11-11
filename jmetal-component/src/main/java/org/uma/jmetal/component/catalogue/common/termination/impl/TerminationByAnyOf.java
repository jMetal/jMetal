package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Termination condition that is met when any of a list of conditions is met, for example a stop
 * requested by the user or a maximum computing time, whichever comes first.
 *
 * <p>Every condition is checked each time, even when an earlier one is already met, because some
 * conditions keep state that has to be updated in every iteration. The progress is the largest of
 * the progresses that the conditions know.
 */
public class TerminationByAnyOf implements Termination {
  private final List<Termination> terminations;

  /**
   * Creates the condition.
   *
   * @param terminations the conditions; there must be at least one, and none can be null
   */
  public TerminationByAnyOf(Termination... terminations) {
    Check.notNull(terminations);
    Check.that(terminations.length > 0, "There must be at least one termination condition");
    Arrays.stream(terminations).forEach(Check::notNull);

    this.terminations = List.of(terminations);
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    return terminations.stream()
        .map(termination -> termination.isMet(algorithmStatusData))
        .toList()
        .contains(true);
  }

  /** The largest progress of the conditions, or an empty value if none of them knows it. */
  @Override
  public OptionalDouble progress(Map<String, Object> algorithmStatusData) {
    return terminations.stream()
        .map(termination -> termination.progress(algorithmStatusData))
        .filter(OptionalDouble::isPresent)
        .mapToDouble(OptionalDouble::getAsDouble)
        .max();
  }

  /** The conditions, in the order in which they were given. */
  public List<Termination> terminations() {
    return terminations;
  }
}
