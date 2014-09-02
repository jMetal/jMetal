package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal2.encoding.variable.ArrayReal;
import org.uma.jmetal2.encoding.variable.Real;

import javax.management.JMException;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class SimpleArrayReal implements ArrayReal {
  private Real [] array ;

  /** Constructor */
  public SimpleArrayReal() {
    array = null;
  }

  /* Constructor */
  public SimpleArrayReal(int size) {
    array = new Real[size];
    for (int i = 0 ; i < array.length; i++) {
      array[i] = new SimpleReal(0.0) ;
    }
  }

  public SimpleArrayReal(int size, Problem problem) {
    array = new Real[size];

    for (int i = 0; i < size; i++) {
      double value =
              PseudoRandom.randDouble() * (problem.getUpperLimit(i) - problem.getLowerLimit(i)) + problem.getLowerLimit(i);
      array[i] = new SimpleReal(value) ;
    }
  }

  @Override
  public Real getReal(int index) {
    if ((index >= 0) && (index < array.length)) {
      return array[index];
    } else {
      throw new JMetalException("Index out of bounds") ;
    }
  }

  @Override
  public void setReal(int index, Real realVariable) {
    if ((index >= 0) && (index < array.length)) {
      array[index] = realVariable ;
    } else {
      throw new JMetalException("Index out of bounds") ;
    }
  }
}
