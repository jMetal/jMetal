package org.uma.jmetal.solution.pointsolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.Solution;

/**
 * Solution used to wrap a point, i.e., an array of double values which are considered as objective
 * values.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class PointSolution implements Solution<Double> {
  final private int numberOfObjectives;
  private double[] objectives;
  protected Map<Object, Object> attributes = new HashMap<>();

  @Override
  public List<Double> variables() {
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
  public PointSolution(double[] point) {
    numberOfObjectives = point.length;
    objectives = new double[numberOfObjectives];

    System.arraycopy(point, 0, this.objectives, 0, numberOfObjectives);
  }

  /**
   * Constructor
   *
   * @param solution
   */
  public PointSolution(Solution<?> solution) {
    this.numberOfObjectives = solution.objectives().length;
    objectives = new double[numberOfObjectives];

    System.arraycopy(solution.objectives(), 0, this.objectives, 0, numberOfObjectives);
  }

  @Override
  public PointSolution copy() {
    return new PointSolution(this);
  }

  @Override
  public boolean equals(Object o) {
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
