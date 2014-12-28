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
  public GenericIntegerPermutationSolution(PermutationProblem problem) {
    this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      List<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength(i));

      for (int j = 0; j < problem.getPermutationLength(i); j++) {
        randomSequence.add(j);
      }

      java.util.Collections.shuffle(randomSequence);
      variables.set(i, randomSequence) ;
    }
  }

  /** Copy Constructor */
  public GenericIntegerPermutationSolution(GenericIntegerPermutationSolution solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    variables = new ArrayList<>() ;
    for (List<Integer> var : solution.variables) {
      List<Integer> list = new ArrayList<>() ;
      for (Integer element : var) {
        var.add(new Integer(element)) ;
      }
      variables.add(list) ;
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
    for (Integer element : variables.get(index)) {
      result += element + " " ;
    }

    return variables.get(index).toString() ;
  }
}
