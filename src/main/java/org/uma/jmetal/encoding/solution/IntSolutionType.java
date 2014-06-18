package org.uma.jmetal.encoding.solution;

import org.uma.jmetal.core.Solution;

public interface IntSolutionType {
  public int getIntValue(Solution solution, int index) ;
  public void setIntValue(Solution solution, int index, int value) ;
  public int getNumberOfIntVariables(Solution solution_);
  public double getIntUpperBound(Solution solution, int index) ;
  public double getIntLowerBound(Solution solution, int index) ;
}
