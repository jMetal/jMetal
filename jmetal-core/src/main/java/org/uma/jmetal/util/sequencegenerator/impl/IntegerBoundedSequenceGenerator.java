package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * This class generates a bounded sequence of consecutive integer numbers. When the last number is generated, the
 * sequence starts again.
 *
 * @author Antonio J. Nebro
 */
public class IntegerBoundedSequenceGenerator implements SequenceGenerator<Integer> {
  private int index;
  private int size ;

  public IntegerBoundedSequenceGenerator(int size) {
    Check.that(size > 0, "Size " + size + " is not a positive number greater than zero");
    this.size = size ;
    index = 0;
  }

  @Override
  public Integer getValue() {
    return index ;
  }

  @Override
  public void generateNext() {
    index++;
    if (index == size) {
      index = 0;
    }
  }

  @Override
  public int getSequenceLength() {
    return size ;
  }
}
