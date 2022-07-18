package org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Compares two population according to an attribute value, which must be a double number. The higher
 * the value the better.
 *
 * @author Antonio J. Nebro
 */
public class IntegerValueAttributeComparator<S extends Solution<?>> extends AttributeComparator<S> {

  public IntegerValueAttributeComparator(String name, Ordering ordering) {
    super(name, ordering);
  }

  public IntegerValueAttributeComparator(String name) {
    super(name);
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
    Check.notNull(solution1);
    Check.notNull(solution2);

    //Check.isNotNull(solution1.getAttribute(attributeName));
    //Check.isNotNull(solution2.getAttribute(attributeName));

    int result ;

    var value1 = 0 ;
    var value2 = 0 ;

    if (solution1.attributes().get(attributeName) != null) {
      value1 = (int) solution1.attributes().get(attributeName);
    }
    if (solution2.attributes().get(attributeName) != null) {
      value2 = (int) solution2.attributes().get(attributeName);
    }

    if (ordering.equals(Ordering.DESCENDING)) {
      result = Double.compare(value2, value1);
    } else {
      result = Double.compare(value1, value2);
    }

    return result;
  }
}
