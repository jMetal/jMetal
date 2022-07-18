package org.uma.jmetal.util.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;

/**
 * Solution used to wrap a {@link Point} object. Only objectives are used.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class PointSolution implements Solution<Double> {
  private int numberOfObjectives;
  private double[] objectives;
  protected @NotNull Map<Object, Object> attributes = new HashMap<>();

  @Override
  public @NotNull List<Double> variables() {
    return new ArrayList<>();
  }

  @Override
  public double[] objectives() {
    return objectives;
  }

  @Override
  public double[] constraints() {
    return new double[0];
  }

  @Override
  public Map<Object, Object> attributes() {
    return attributes;
  }

  /**
   * Constructor
   *
   * @param numberOfObjectives
   */
  public PointSolution(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    objectives = new double[numberOfObjectives];
    attributes = new HashMap<>();
  }

  /**
   * Constructor
   *
   * @param point
   */
  public PointSolution(Point point) {
    this.numberOfObjectives = point.getDimension();
    objectives = new double[numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives[i] = point.getValue(i);
    }
  }

  /**
   * Constructor
   *
   * @param solution
   */
  public PointSolution(@NotNull Solution<?> solution) {
    this.numberOfObjectives = solution.objectives().length;
    objectives = new double[numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives[i] = solution.objectives()[i];
    }
  }

  /**
   * Copy constructor
   *
   * @param point
   */
  public PointSolution(@NotNull PointSolution point) {
    this(point.objectives().length);

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives[i] = point.objectives()[i];
    }
  }

  @Override
  public @NotNull PointSolution copy() {
    return new PointSolution(this);
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PointSolution that = (PointSolution) o;

    if (numberOfObjectives != that.numberOfObjectives) return false;
    if (!Arrays.equals(objectives, that.objectives)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(objectives);
  }

  @Override
  public String toString() {
    return Arrays.toString(objectives);
  }
}
