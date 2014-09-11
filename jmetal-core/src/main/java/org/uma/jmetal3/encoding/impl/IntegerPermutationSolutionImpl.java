package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.PermutationSolution;
import org.uma.jmetal3.problem.BinaryProblem;
import org.uma.jmetal3.problem.PermutationProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerPermutationSolutionImpl
        extends GenericSolutionImpl<List<Integer>, PermutationProblem>
        implements PermutationSolution<List<Integer>> {

  /** Constructor */
  public IntegerPermutationSolutionImpl(PermutationProblem problem) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      ArrayList<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength(i));

      for (int j = 0; j < problem.getPermutationLength(i); i++) {
        randomSequence.add(i);
      }

      java.util.Collections.shuffle(randomSequence);

      //for (int j = 0; j < randomSequence.size(); j++) {
      //  vector[j] = randomSequence.get(j);
      //}
      variables.set(i, randomSequence) ;
    }
  }


  @Override
  public Solution<?> copy() {
    // TODO
    return null;
  }
}
