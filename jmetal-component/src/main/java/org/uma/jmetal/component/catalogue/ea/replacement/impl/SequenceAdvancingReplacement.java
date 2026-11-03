package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Decorator of a {@link Replacement} that moves a {@link SequenceGenerator} to its next value once
 * the replacement is done. It is for the algorithms whose selection and variation take, as the
 * solution to which they apply differential evolution, the one whose index is the current value of
 * the generator: without it the index never changes and the same solution is always used.
 *
 * @param <S> the type of the solutions
 */
public class SequenceAdvancingReplacement<S extends Solution<?>> implements Replacement<S> {
  private final Replacement<S> replacement;
  private final SequenceGenerator<Integer> sequenceGenerator;

  public SequenceAdvancingReplacement(
      Replacement<S> replacement, SequenceGenerator<Integer> sequenceGenerator) {
    Check.notNull(replacement);
    Check.notNull(sequenceGenerator);
    this.replacement = replacement;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public List<S> replace(List<S> currentList, List<S> offspringList) {
    List<S> result = replacement.replace(currentList, offspringList);
    sequenceGenerator.generateNext();

    return result;
  }
}
