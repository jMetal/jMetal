package org.uma.jmetal3.core;

/**
 * Created by antonio on 03/09/14.
 */
public interface Solution<V> {
  public void setObjective(int index, double value) ;
  public double getObjective(int index) ;

  public V getVariable(int index) ;
  public void setVariable(int index, V value) ;
 }
