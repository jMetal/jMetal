package org.uma.jmetal.problem.sequenceproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.sequenceproblem.SequenceProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;
import org.uma.jmetal.solution.stringsolution.SequenceSolution;
import org.uma.jmetal.solution.stringsolution.impl.CharSequenceSolution;

@SuppressWarnings("serial")
public abstract class CharSequenceProblem
    extends AbstractGenericProblem<CharSequenceSolution> implements
        SequenceProblem<CharSequenceSolution> {

  @Override
  public CharSequenceSolution createSolution() {
    return new CharSequenceSolution(getLength(), getNumberOfObjectives()) ;
  }
}
