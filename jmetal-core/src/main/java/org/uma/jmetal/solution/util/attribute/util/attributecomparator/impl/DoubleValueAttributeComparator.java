package org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.util.checking.Check;

/**
 * Compares two population according to an attribute value, which must be a double number. The higher
 * the value the better.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class DoubleValueAttributeComparator<S extends Solution<?>> extends AttributeComparator<S> {

  public DoubleValueAttributeComparator(String name, Ordering ordering) {
    super(name, ordering) ;
  }

  public DoubleValueAttributeComparator(String name) {
    super(name) ;
  }
  /**
   * Compare two population.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 is has greater, equal, or less attribute value than
   *     solution2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    Check.isNotNull(solution1);
    Check.isNotNull(solution2);

    int result ;

    if (ordering.equals(Ordering.DESCENDING)) {
      double value1 = Double.MIN_VALUE;
      if (solution1.getAttribute(attributeName) != null) {
        value1 = (double) solution1.getAttribute(attributeName);
      }

      double value2 = Double.MIN_VALUE;
      if (solution2.getAttribute(attributeName) != null) {
        value2 = (double) solution2.getAttribute(attributeName);
      }

      result = Double.compare(value2, value1);
    } else {
      double value1 = Double.MAX_VALUE;
      if (solution1.getAttribute(attributeName) != null) {
        value1 = (double) solution1.getAttribute(attributeName);
      }

      double value2 = Double.MAX_VALUE;
      if (solution2.getAttribute(attributeName) != null) {
        value2 = (double) solution2.getAttribute(attributeName);
      }
      result = Double.compare(value1, value2);
    }

    return result ;
  }
}
