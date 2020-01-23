package org.uma.jmetal.problem.sequenceproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.sequenceproblem.SequenceProblem;
import org.uma.jmetal.solution.sequencesolution.impl.CharSequenceSolution;

@SuppressWarnings("serial")
public abstract class CharSequenceProblem
    extends AbstractGenericProblem<CharSequenceSolution> implements
        SequenceProblem<CharSequenceSolution> {

  @Override
  public CharSequenceSolution createSolution() {
    return new CharSequenceSolution(getLength(), getNumberOfObjectives()) ;
  }
}
