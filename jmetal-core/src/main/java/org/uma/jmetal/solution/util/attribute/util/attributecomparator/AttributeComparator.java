package org.uma.jmetal.solution.util.attribute.util.attributecomparator;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

/**
 * Compares two population according to an attribute value, which must be a double number. The higher
 * the value the better.
 *
 * @author Antonio J. Nebro
 */
public abstract class AttributeComparator<S extends Solution<?>> implements Comparator<S> {
  public enum Ordering {
    DESCENDING,
    ASCENDING
  };

  protected final String attributeName;
  protected Ordering ordering;

  public AttributeComparator(String name, Ordering ordering) {
    this.attributeName = name;
    this.ordering = ordering;
  }

  public AttributeComparator(String name) {
    this(name, Ordering.DESCENDING);
  }
}
