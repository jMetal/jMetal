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

    //Check.isNotNull(solution1.getAttribute(attributeName));
    //Check.isNotNull(solution2.getAttribute(attributeName));

    int result ;

    double value1 = 0 ;
    double value2 = 0 ;

    if (solution1.getAttribute(attributeName) != null) {
      value1 = (double) solution1.getAttribute(attributeName);
    }
    if (solution2.getAttribute(attributeName) != null) {
      value2 = (double) solution2.getAttribute(attributeName);
    }

    if (ordering.equals(Ordering.DESCENDING)) {
      result = Double.compare(value2, value1);
    } else {
      result = Double.compare(value1, value2);
    }

    return result;
  }
}
