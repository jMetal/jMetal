package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Single objective problem for testing integer encoding.
 * Objective: minimizing the distance to value N
 */
@SuppressWarnings("serial")
public class NIntegerMin extends AbstractIntegerProblem {
  private int valueN ;

  public NIntegerMin() {
    this(10, 100, -100, +100);
  }

  /** Constructor */
  public NIntegerMin(int numberOfVariables, int n, int lowerBound, int upperBound)  {
    valueN = n ;
    setNumberOfObjectives(1);
    setName("NIntegerMin");

    @NotNull List<Integer> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Integer> upperLimit = new ArrayList<>(numberOfVariables) ;

      for (int i = 0; i < numberOfVariables; i++) {
          lowerLimit.add(lowerBound);
          upperLimit.add(upperBound);
      }

      setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public IntegerSolution evaluate(IntegerSolution solution) {
    int approximationToN;

    approximationToN = 0;

      int sum = 0;
      for (Integer integer : solution.variables()) {
          int value = integer;
          int abs = Math.abs(valueN - value);
          sum += abs;
      }
      approximationToN += sum;

    solution.objectives()[0] = approximationToN;

    return solution ;
  }
}
