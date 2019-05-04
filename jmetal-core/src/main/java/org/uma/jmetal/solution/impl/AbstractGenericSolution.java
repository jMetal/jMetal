package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.*;
import java.util.function.Function;

/**
 * Abstract class representing a generic solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractGenericSolution<T, P extends Problem<?>> implements Solution<T> {
  private double[] objectives;
  private List<T> variables;
  protected P problem ;
  protected Map<Object, Object> attributes ;
  /**
   * @deprecated Call {@link JMetalRandom#getInstance()} if you need one.
   */
  @Deprecated
  protected final JMetalRandom randomGenerator ;

  /**
   * Create a new {@link AbstractGenericSolution} with the given data.
   * 
   * @param problem
   *          {@link Problem} to rely on
   * @param variableProvider
   *          variable values
   * @param objectiveProvider
   *          objective values
   * @param attributes
   *          attributes
   */
  public AbstractGenericSolution(P problem, Function<Integer, T> variableProvider, Function<Integer, Double> objectiveProvider, Map<Object, Object> attributes
      ) {
    this.problem = problem ;
    attributes = new HashMap<>() ;
    randomGenerator = JMetalRandom.getInstance() ;

    objectives = new double[problem.getNumberOfObjectives()] ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables.add(i, variableProvider.apply(i)) ;
    }
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives[i] = objectiveProvider.apply(i) ;
    }
    this.attributes = attributes;
  }

  /**
   * Create a new {@link AbstractGenericSolution} with zero objectives and no
   * attribute.
   * 
   * @param problem
   *          {@link Problem} to rely on
   * @param variableProvider
   *          variable values
   */
  public AbstractGenericSolution(P problem, Function<Integer, T> variableProvider) {
    this(problem, variableProvider, i -> 0.0, Collections.emptyMap());
  }

  /**
   * Create a new {@link AbstractGenericSolution} with <code>null</code>
   * variables, zero objectives and no attribute.
   * 
   * @param problem
   *          {@link Problem} to rely on
   */
  public AbstractGenericSolution(P problem) {
    this(problem, i -> null) ;
  }

  /**
   * Copy constructor
   */
  public AbstractGenericSolution(P problem, Solution<T> solution) {
    this(problem, solution::getVariableValue, solution::getObjective, new HashMap<>(solution.getAttributes()));
  }
  
  @Override
  public double[] getObjectives() {
    return objectives ;
  }

  @Override
  public List<T> getVariables() {
    return variables ;
  }

  @Override
  public void setAttribute(Object id, Object value) {
    attributes.put(id, value) ;
  }

  @Override
  public Object getAttribute(Object id) {
    return attributes.get(id) ;
  }

  @Override
  public void setObjective(int index, double value) {
    objectives[index] = value ;
  }

  @Override
  public double getObjective(int index) {
    return objectives[index];
  }

  @Override
  public T getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, T value) {
    variables.set(index, value);
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.length;
  }

  /**
   * @deprecated This method is automatically called by
   *             {@link #AbstractGenericSolution(Problem)} and
   *             {@link #AbstractGenericSolution(Problem, Function)}. If you don't
   *             need it, it means you aim for non-zero objectives, in which case
   *             you should rely on
   *             {@link #AbstractGenericSolution(Problem, Function, Function)} or
   *             {@link #AbstractGenericSolution(Problem, Solution)} instead.
   */
  @Deprecated
  protected void initializeObjectiveValues() {
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives[i] = 0.0 ;
    }
  }

  @Override
  public String toString() {
    String result = "Variables: " ;
    for (T var : variables) {
      result += "" + var + " " ;
    }
    result += "Objectives: " ;
    for (Double obj : objectives) {
      result += "" + obj + " " ;
    }
    result += "\t" ;
    result += "AlgorithmAttributes: " + attributes + "\n" ;

    return result ;
  }

  private boolean equalsIgnoringAttributes(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    AbstractGenericSolution<?, ?> that = (AbstractGenericSolution<?, ?>) o;

    if (!Arrays.equals(objectives, that.objectives))
      return false;

    if (!variables.equals(that.variables))
      return false;

    return true;
  }

  @Override public boolean equals(Object o) {

    if (!this.equalsIgnoringAttributes(o)) {
      return false;
    }

    AbstractGenericSolution<?, ?> that = (AbstractGenericSolution<?, ?>) o;
    // avoid recursive infinite comparisons when solution as attribute

    // examples when problems would arise with a simple comparison attributes.equals(that.attributes):
    // if A contains itself as Attribute
    // If A contains B as attribute, B contains A as attribute
    //
    // the following implementation takes care of this by considering solutions as attributes as a special case

    if (attributes.size() != that.attributes.size()) {
      return false;
    }

    for (Object key : attributes.keySet()) {
      Object value      = attributes.get(key);
      Object valueThat  = that.attributes.get(key);

      if (value != valueThat) { // it only makes sense comparing when having different references

        if (value == null) {
          return false;
        } else if (valueThat == null) {
          return false;
        } else { // both not null

          boolean areAttributeValuesEqual;
          if (value instanceof AbstractGenericSolution) {
            areAttributeValuesEqual = ((AbstractGenericSolution<?, ?>) value).equalsIgnoringAttributes(valueThat);
          } else {
            areAttributeValuesEqual = !value.equals(valueThat);
          }
          if (!areAttributeValuesEqual) {
            return false;
          } // if equal the next attributeValue will be checked
        }
      }
    }

    return true;
  }

  @Override public int hashCode() {
    int result = Arrays.hashCode(objectives);
    result = 31 * result + variables.hashCode();
    result = 31 * result + attributes.hashCode();
    return result;
  }
}
