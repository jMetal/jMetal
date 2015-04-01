package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class DefaultIntegerPermutationSolution
    extends AbstractGenericSolution<Integer, PermutationProblem>
    implements IntegerSolution {

  /** Constructor */
  public DefaultIntegerPermutationSolution(PermutationProblem problem) {
    super(problem) ;

    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    List<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength(0));

    for (int j = 0; j < problem.getPermutationLength(0); j++) {
      randomSequence.add(j);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariableValue(i, randomSequence.get(i)) ;
    }
  }

  /** Copy Constructor */
  public DefaultIntegerPermutationSolution(DefaultIntegerPermutationSolution solution) {
    super(solution.problem) ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

    attributes = new HashMap(solution.attributes) ;
  }

  @Override public String getVariableValueString(int index) {
    return "" + getVariableValue(index);
  }

  @Override
  public Solution copy() {
    return new DefaultIntegerPermutationSolution(this);
  }




  @Override public Integer getLowerBound(int index) {
    return null;
  }

  @Override public Integer getUpperBound(int index) {
    return null;
  }
}
