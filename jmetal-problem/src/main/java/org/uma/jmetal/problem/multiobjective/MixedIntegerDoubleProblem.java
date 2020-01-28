package org.uma.jmetal.problem.multiobjective;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.solution.compositesolution.CompositeSolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bi-objective problem for testing class {@link CompositeSolution )}. This problem requires an encoding where a
 * solution is composed of an integer solution and a double solution. For the sake of simplicity, it is assumed that
 * the lower and upper bounds of the variables of both solutions are the same.
 *
 * Objective 1: minimizing the sum of the distances of every variable to value N
 * Objective 2: minimizing the sum of the distances of every variable to value M
 */
@SuppressWarnings("serial")
public class MixedIntegerDoubleProblem extends AbstractGenericProblem<CompositeSolution> {
  private int valueN;
  private int valueM;
  private List<Pair<Integer, Integer>> integerBounds;
  private List<Pair<Double, Double>> doubleBounds;

  public MixedIntegerDoubleProblem() {
    this(10, 10, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public MixedIntegerDoubleProblem(
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
    setName("MixedIntegerDoubleProblem");

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
  public void evaluate(CompositeSolution solution) {
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
    for (int i = 0; i < doubleVariables.size(); i++) {
      approximationToN += Math.abs(valueN - doubleVariables.get(i));
      approximationToM += Math.abs(valueM - doubleVariables.get(i));
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }

  @Override
  public CompositeSolution createSolution() {
    IntegerSolution integerSolution = new DefaultIntegerSolution(integerBounds, getNumberOfObjectives(), getNumberOfConstraints()) ;
    DoubleSolution doubleSolution = new DefaultDoubleSolution(doubleBounds, getNumberOfObjectives(), getNumberOfConstraints()) ;
    return new CompositeSolution(Arrays.asList(integerSolution, doubleSolution));
  }
}
