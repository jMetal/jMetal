package org.uma.jmetal3.core;

/**
 * Created by Antonio on 03/09/14.
 */
public interface Solution<T> {
	
	/* Methods */
  public void setObjective(int index, double value) ;
  public double getObjective(int index) ;

  public T getVariableValue(int index) ;
  public void setVariableVariable(int index, T value) ;
 }
