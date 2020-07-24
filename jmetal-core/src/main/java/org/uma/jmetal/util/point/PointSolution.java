package org.uma.jmetal.util.point;

import org.uma.jmetal.solution.Solution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Solution used to wrap a {@link Point} object. Only objectives are used.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class PointSolution implements Solution<Double> {
  private int numberOfObjectives ;
  private List<Double> objectives;
  protected Map<Object, Object> attributes ;

  /**
   * Constructor
   *
   * @param numberOfObjectives
   */
  public PointSolution(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
    objectives = new ArrayList<Double>(DoubleStream.of(new double[numberOfObjectives]).mapToObj(d->d).collect(Collectors.toList())) ;
    attributes = new HashMap<>() ;
  }

  /**
   * Constructor
   *
   * @param point
   */
  public PointSolution(Point point) {
    this.numberOfObjectives = point.getDimension() ;
    objectives = new ArrayList<>() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives.add(point.getValue(i)) ;
    }
  }

  /**
   * Constructor
   *
   * @param solution
   */
  public PointSolution(Solution<?> solution) {
    this.numberOfObjectives = solution.objectives().size() ;
    objectives = new ArrayList<>() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives.add(solution.objectives().get(i)) ;
    }
  }

  /**
   * Copy constructor
   *
   * @param point
   */
  public PointSolution(PointSolution point) {
    this(point.objectives().size()) ;

    for (int i = 0; i < numberOfObjectives; i++) {
      this.objectives.set(i, point.objectives().get(i)) ;
    }
  }

  @Deprecated
  @Override public void setObjective(int index, double value) {
    objectives.set(index, value) ;
  }

  @Deprecated
  @Override public double getObjective(int index) {
    return objectives.get(index);
  }

  @Deprecated
  @Override
  public double[] getObjectives() {
    return objectives.stream().mapToDouble(d->d).toArray() ;
  }
  
  @Override
  public List<Double> objectives() {
    return objectives;
  }

  @Override
  public List<Double> getVariables() {
    return Collections.emptyList() ;
  }

  @Override public Double getVariable(int index) {
    return null;
  }

  @Override public void setVariable(int index, Double value) {
	  //This method is an intentionally-blank override.
  }

  @Override
  public double[] getConstraints() {
    return new double[0];
  }

  @Override
  public double getConstraint(int index) {
    return 0;
  }

  @Override
  public void setConstraint(int index, double value) {

  }

  @Override public int getNumberOfVariables() {
    return 0;
  }

  @Deprecated
  @Override public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  @Override
  public int getNumberOfConstraints() {
    return 0;
  }

  @Override public PointSolution copy() {
    return new PointSolution(this);
  }

  @Override public void setAttribute(Object id, Object value) {
    attributes.put(id, value) ;
  }

  @Override public Object getAttribute(Object id) {
    return attributes.get(id);
  }

  @Override
  public boolean hasAttribute(Object id) {
    return false;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    PointSolution that = (PointSolution) o;

    if (numberOfObjectives != that.numberOfObjectives)
      return false;
    if (!Objects.equals(objectives, that.objectives))
      return false;

    return true;
  }

  @Override public int hashCode() {
    return Objects.hashCode(objectives);
  }
  
  @Override
	public String toString() {
		return objectives.toString();
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}
}
