package org.uma.jmetal.problem.multiobjective;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.integerdoublesolution.impl.DefaultIntegerDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Bi-objective problem for testing class {@link DefaultIntegerDoubleSolution )}, e.g., a solution composed of an
 * integer solution and a double solution. It is assumed that the lower and upper bounds of the variables of both solutions
 * are the same.
 *
 * Objective 1: minimizing the distance to value N (using the integer variables)
 * Objective 2: minimizing the distance to value M (using the double variables
 */
@SuppressWarnings("serial")
public class NMMin2 extends AbstractGenericProblem<Solution<?>> {
  private int valueN ;
  private int valueM ;
  private List<Pair<Integer, Integer>> integerBounds ;
  private List<Pair<Double, Double>> doubleBounds ;

  public NMMin2() {
    this(10, 10, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin2(int numberOfIntegerVariables, int numberOfDoubleVariables, int n, int m, int lowerBound, int upperBound)  {
    valueN = n ;
    valueM = m ;
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setName("NMMin2");

    integerBounds = new ArrayList<>(numberOfIntegerVariables) ;
    doubleBounds = new ArrayList<>(numberOfDoubleVariables) ;

    for (int i = 0; i < numberOfIntegerVariables; i++) {
      integerBounds.add(new ImmutablePair<>(lowerBound, upperBound)) ;
    }

    for (int i = 0; i < numberOfDoubleVariables; i++) {
      doubleBounds.add(new ImmutablePair<>((double)lowerBound, (double)upperBound)) ;
    }
  }

  /** Evaluate() method */
  @Override
  public void evaluate(IntegerSolution solution) {
    int approximationToN;
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      int value = solution.getVariable(i) ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }
}
