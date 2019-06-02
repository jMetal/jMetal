package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractIntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerDoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 18/09/14.
 * Bi-objective problem for testing integer/double encoding.
 * Objective 1: minimizing the distance to value N
 * Objective 2: minimizing the distance to value M
 */
@SuppressWarnings("serial")
public class NMMin2 extends AbstractIntegerDoubleProblem<IntegerDoubleSolution> {
  private int valueN ;
  private int valueM ;

  public NMMin2() {
    this(10, 10, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin2(int numberOfIntegerVariables, int numberOfDoubleVariables, int n, int m, int lowerBound, int upperBound)  {
    setNumberOfIntegerVariables(numberOfIntegerVariables);
    setNumberOfDoubleVariables(numberOfDoubleVariables);
    valueN = n ;
    valueM = m ;
    setNumberOfVariables(numberOfIntegerVariables+numberOfDoubleVariables);
    setNumberOfObjectives(2);
    setName("NMMin");

    List<Number> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Number> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(lowerBound);
      upperLimit.add(upperBound);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public IntegerDoubleSolution createSolution() {
    return new DefaultIntegerDoubleSolution(this) ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(IntegerDoubleSolution solution) {
    int approximationToN;
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < solution.getNumberOfIntegerVariables(); i++) {
      int value = solution.getVariableValue(i).intValue() ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    for (int i = solution.getNumberOfIntegerVariables(); i < (solution.getNumberOfIntegerVariables()+solution.getNumberOfDoubleVariables()); i++) {
      double value = solution.getVariableValue(i).doubleValue() ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }
}
