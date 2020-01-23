package org.uma.jmetal.problem.sequenceproblem;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.stringsolution.SequenceSolution;

public interface SequenceProblem <S extends SequenceSolution<?>> extends Problem<S>  {
    int getLength() ;
}
