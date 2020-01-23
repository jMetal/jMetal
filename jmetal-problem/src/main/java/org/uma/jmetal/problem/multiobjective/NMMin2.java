package org.uma.jmetal.problem.multiobjective;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integerdoublesolution.IntegerDoubleSolution;
import org.uma.jmetal.solution.integerdoublesolution.impl.DefaultIntegerDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/07/14. Bi-objective problem for testing class {@link
 * DefaultIntegerDoubleSolution )}, e.g., a solution composed of an integer solution and a double
 * solution. It is assumed that the lower and upper bounds of the variables of both solutions are
 * the same.
 *
 * Objective 1: minimizing the sum of the distances of every variable to value N
 * Objective 2: minimizing the sum of the distances of every variable to value M
 */
@SuppressWarnings("serial")
@Deprecated
public class NMMin2 extends AbstractGenericProblem<IntegerDoubleSolution> {
  private int valueN;
  private int valueM;
  private List<Pair<Integer, Integer>> integerBounds;
  private List<Pair<Double, Double>> doubleBounds;

  public NMMin2() {
    this(10, 10, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin2(
      int numberOfIntegerVariables,
      int numberOfDoubleVariables,
      int n,
      int m,
      int lowerBound,
      int upperBound) {
    valueN = n;
    valueM = m;
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setName("NMMin2");

    integerBounds = new ArrayList<>(numberOfIntegerVariables);
    doubleBounds = new ArrayList<>(numberOfDoubleVariables);

    for (int i = 0; i < numberOfIntegerVariables; i++) {
      integerBounds.add(new ImmutablePair<>(lowerBound, upperBound));
    }

    for (int i = 0; i < numberOfDoubleVariables; i++) {
      doubleBounds.add(new ImmutablePair<>((double) lowerBound, (double) upperBound));
    }
  }

  /** Evaluate() method */
  @Override
  public void evaluate(IntegerDoubleSolution solution) {
    int approximationToN;
    int approximationToM;

    approximationToN = 0;
    approximationToM = 0;

    List<Integer> integerVariables = ((IntegerSolution) solution.getVariable(0)).getVariables();
    for (int i = 0; i < integerVariables.size(); i++) {
      approximationToN += Math.abs(valueN - integerVariables.get(i));
      approximationToM += Math.abs(valueM - integerVariables.get(i));
    }

    List<Double> doubleVariables = ((DoubleSolution) solution.getVariable(1)).getVariables();
    for (int i = 0; i < integerVariables.size(); i++) {
      approximationToN += Math.abs(valueN - doubleVariables.get(i));
      approximationToM += Math.abs(valueM - doubleVariables.get(i));
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }

  @Override
  public IntegerDoubleSolution createSolution() {
    return new DefaultIntegerDoubleSolution(integerBounds, doubleBounds, 2);
  }
}
