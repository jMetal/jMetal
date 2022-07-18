package org.uma.jmetal.solution.sequencesolution.impl;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.sequencesolution.SequenceSolution;

/**
 * Defines an implementation of solution representing sequences of chars.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CharSequenceSolution extends AbstractSolution<Character> implements SequenceSolution<Character> {
  /** Constructor */
  public CharSequenceSolution(int stringLength, int numberOfObjectives) {
    super(stringLength, numberOfObjectives);

    for (var i = 0; i < stringLength; i++) {
      variables().set(i, ' ');
    }
  }

  /** Copy Constructor */
  public CharSequenceSolution(@NotNull CharSequenceSolution solution) {
    super(solution.getLength(), solution.objectives().length);

    for (var i = 0; i < objectives().length; i++) {
      objectives()[i] = solution.objectives()[i];
    }

    for (var i = 0; i < variables().size(); i++) {
      variables().set(i, solution.variables().get(i));
    }

    for (var i = 0; i < constraints().length; i++) {
      constraints()[i] =  solution.constraints()[i];
    }

    attributes = new HashMap<Object, Object>(solution.attributes);
  }

  @Override
  public @NotNull CharSequenceSolution copy() {
    return new CharSequenceSolution(this);
  }

  @Override
  public int getLength() {
    return variables().size();
  }
}
