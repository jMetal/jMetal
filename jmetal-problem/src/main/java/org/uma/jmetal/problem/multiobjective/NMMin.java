package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Bi-objective problem for testing class {@link DefaultIntegerSolution )}, e.g., integer encoding.
 * Objective 1: minimizing the distance to value N
 * Objective 2: minimizing the distance to value M
 */
@SuppressWarnings("serial")
public class NMMin extends AbstractIntegerProblem {
  private int valueN ;
  private int valueM ;

  public NMMin() {
    this(20, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin(int numberOfVariables, int n, int m, int lowerBound, int upperBound)  {
    valueN = n ;
    valueM = m ;
    setNumberOfObjectives(2);
    setName("NMMin");

    List<Integer> lowerLimit = new ArrayList<>(numberOfVariables) ;
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
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < solution.variables().size(); i++) {
      int value = solution.variables().get(i) ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    solution.objectives()[0] = approximationToN;
    solution.objectives()[1] = approximationToM;

    return solution ;
  }
}
