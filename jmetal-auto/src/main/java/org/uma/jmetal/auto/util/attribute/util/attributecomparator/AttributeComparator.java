package org.uma.jmetal.auto.util.attribute.util.attributecomparator;

import org.uma.jmetal.auto.util.checking.Checker;
import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two solutions according to an attribute value, which must be a double number. The higher
 * the value the better.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public abstract class AttributeComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
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
