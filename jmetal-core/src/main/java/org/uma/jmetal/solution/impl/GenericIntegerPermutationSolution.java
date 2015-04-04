package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class GenericIntegerPermutationSolution
    extends AbstractGenericSolution<List<Integer>, PermutationProblem>
    implements PermutationSolution<List<Integer>> {

  /** Constructor */
  public GenericIntegerPermutationSolution(PermutationProblem<PermutationSolution<Integer>> problem) {
    super(problem) ;

    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    List<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength());

    for (int i = 0; i < problem.getPermutationLength(); i++) {
      randomSequence.add(i);
    }

    java.util.Collections.shuffle(randomSequence);
    for (int i = 0; i < problem.getPermutationLength(); i++) {
      setVariableValue(i, randomSequence) ;
    }
  }

  /** Copy Constructor */
  public GenericIntegerPermutationSolution(GenericIntegerPermutationSolution solution) {
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

  @Override
  public Solution copy() {
    return new GenericIntegerPermutationSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    String result = "" ;
    for (Integer element : getVariableValue(index)) {
      result += element + " " ;
    }

    return getVariableValue(index).toString() ;
  }
}
