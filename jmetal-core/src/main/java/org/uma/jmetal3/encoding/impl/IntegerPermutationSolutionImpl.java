package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.PermutationSolution;
import org.uma.jmetal3.problem.BinaryProblem;
import org.uma.jmetal3.problem.PermutationProblem;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerPermutationSolutionImpl extends GenericSolutionImpl<Integer, PermutationProblem> implements PermutationSolution<Integer> {

  @Override
  public Solution<?> copy() {
    return null;
  }
}
