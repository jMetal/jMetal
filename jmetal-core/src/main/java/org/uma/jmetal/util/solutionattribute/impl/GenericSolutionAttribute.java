package org.uma.jmetal.util.solutionattribute.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.SolutionAttribute;

/**
 * Generic class for implementing {@link SolutionAttribute} classes. By default, the identifier
 * of a {@link SolutionAttribute} is the class object, but it can be set to a different value
 * when constructing an instance.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GenericSolutionAttribute <S extends Solution<?>, V> implements SolutionAttribute<S, V>{
  private Object identifier;

  /**
   * Constructor
   */
  public GenericSolutionAttribute() {
    identifier = this.getClass() ;
  }

  /**
   * Constructor
   * @param id Attribute identifier
   */
  public GenericSolutionAttribute(Object id) {
    this.identifier = id ;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V getAttribute(@NotNull S solution) {
    return (V)solution.attributes().get(getAttributeIdentifier());
  }

  @Override
  public void setAttribute(@NotNull S solution, V value) {
     solution.attributes().put(getAttributeIdentifier(), value);
  }

  @Override
  public Object getAttributeIdentifier() {
    return identifier;
  }
}
