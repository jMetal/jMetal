package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.wrapper.XInt;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 */
public class NMMin extends Problem {
  private int N ;
  private int M ;

  public NMMin(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 100, 100, -100);
  }

/** Constructor */
  public NMMin(String solutionType, int numberOfVariables, Integer N, Integer M) throws JMetalException {
    this.N = N ;
    this.M = M ;
    this.numberOfVariables_ = numberOfVariables;
    numberOfObjectives_ = 2;
    numberOfConstraints_ = 0;
    problemName_ = "NMMax";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++) {
      lowerLimit_[var] = Integer.MAX_VALUE;
      upperLimit_[var] = Integer.MIN_VALUE;
    }

    if (solutionType.compareTo("Integer") == 0) {
      solutionType_ = new IntSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  public void evaluate(Solution solution) {
    int approximationToN;
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < XInt.getNumberOfDecisionVariables(solution); i++) {
      int value = XInt.getValue(solution, 0) ;
      approximationToN += Math.abs(N - value) ;
      approximationToM += Math.abs(M - value) ;

    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }
}
