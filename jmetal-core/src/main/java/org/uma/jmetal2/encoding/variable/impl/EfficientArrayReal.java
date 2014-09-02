package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal2.encoding.variable.ArrayReal;
import org.uma.jmetal2.encoding.variable.Real;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class EfficientArrayReal implements ArrayReal {
  private double [] array ;

  /** Constructor */
  public EfficientArrayReal() {
    array = null;
  }

  /** Constructor */
  public EfficientArrayReal(int size) {
    array = new double[size];
    for (int i = 0 ; i < array.length; i++) {
      array[i] = 0.0 ;
    }
  }

  public EfficientArrayReal(int size, Problem problem) {
    array = new double[size];

    for (int i = 0; i < size; i++) {
      double value = PseudoRandom.randDouble() * (problem.getUpperLimit(i) -
              problem.getLowerLimit(i)) +
              problem.getLowerLimit(i);
      array[i] = value ;
    }
  }

  @Override
  public Real getReal(int index) {
    return new SimpleReal(array[index]);
  }
}
