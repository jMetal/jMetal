package org.uma.jmetal.solution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Abstract class representing a generic solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractSolution<T> implements Solution<T> {
  private final double @NotNull [] objectives;
  private final List<T> variables;
  private final double[] constraints;
  protected Map<Object, Object> attributes;

  @Override
  public List<T> variables() {
    return variables ;
  }

  @Override
  public double[] objectives() {
    return objectives ;
  }

  @Override
  public double[] constraints() {
    return constraints ;
  }

  @Override
  public Map<Object, Object> attributes() {
    return attributes ;
  }

  /** Constructor */
  protected AbstractSolution(int numberOfVariables, int numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 0);
  }

  /** Constructor */
  protected AbstractSolution(
      int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    attributes = new HashMap<>();

    variables = new ArrayList<>(numberOfVariables);
    for (int i2 = 0; i2 < numberOfVariables; i2++) {
      variables.add(i2, null);
    }

    objectives = new double[numberOfObjectives];
    for (int i1 = 0; i1 < numberOfObjectives; i1++) {
      objectives[i1] = 0.0;
    }

    constraints = new double[numberOfConstraints];
    for (int i = 0; i < numberOfConstraints; i++) {
      constraints[i] = 0.0;
    }

    attributes = new HashMap<>();
  }

  @Override
  public String toString() {
    @NotNull StringBuilder result = new StringBuilder("Variables: ");
    for (T variable : variables) {
      result.append(variable).append(" ");
    }
    result.append("Objectives: ");
    for (Double obj : objectives) {
      result.append(obj).append(" ");
    }
    result.append("Constraints: ");
    for (Double obj : constraints) {
      result.append(obj).append(" ");
    }
    result.append("\t");
    result.append("AlgorithmAttributes: ").append(attributes).append("\n");

    return result.toString();
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (object == null)
      return false;

    if (this.getClass() != object.getClass())
      return false;

    @NotNull Solution<T> solution = (Solution<T>) object;

    return this.variables().equals(solution.variables());
  }

  @Override
  public int hashCode() {
    return variables.hashCode();
  }
}
