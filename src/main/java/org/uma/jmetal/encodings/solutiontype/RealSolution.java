package org.uma.jmetal.encodings.solutiontype;

import org.uma.jmetal.core.Solution;

public interface RealSolution {
  public double getRealValue(Solution solution, int index) ;
  public void setRealValue(Solution solution, int index, double value) ;
  public int getNumberOfVariables(Solution solution_);
  public double getRealUpperBound(Solution solution, int index) ;
  public double getRealLowerBound(Solution solution, int index) ;
}
