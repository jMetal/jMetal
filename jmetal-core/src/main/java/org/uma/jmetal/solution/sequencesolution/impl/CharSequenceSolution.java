package org.uma.jmetal.solution.sequencesolution.impl;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.sequencesolution.SequenceSolution;

import java.util.HashMap;
import java.util.Map;

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

    for (int i = 0; i < stringLength; i++) {
      setVariable(i, ' ');
    }
  }

  /** Copy Constructor */
  public CharSequenceSolution(CharSequenceSolution solution) {
    super(solution.getLength(), solution.getNumberOfObjectives());

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i));
    }

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariable(i, solution.getVariable(i));
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      setConstraint(i, solution.getConstraint(i));
    }

    attributes = new HashMap<Object, Object>(solution.attributes);
  }

  @Override
  public CharSequenceSolution copy() {
    return new CharSequenceSolution(this);
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return attributes;
  }

  @Override
  public int getLength() {
    return getNumberOfVariables();
  }
}
