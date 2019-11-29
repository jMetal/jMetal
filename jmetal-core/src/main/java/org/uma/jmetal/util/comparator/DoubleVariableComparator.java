package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.Check;

import java.util.Comparator;

public class DoubleVariableComparator implements Comparator<DoubleSolution> {
  private int variableIndex ;

  public DoubleVariableComparator() {
    this(0) ;
  }

  public DoubleVariableComparator(int variableIndex) {
    this.variableIndex = variableIndex ;
  }
  /**
   * Compares two double solutions according to a variable value
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
   * respectively.
   */
  @Override
  public int compare(DoubleSolution solution1, DoubleSolution solution2) {
    Check.isNotNull(solution1);
    Check.isNotNull(solution2);

    if (solution1.getVariable(variableIndex) < solution2.getVariable(variableIndex)) {
      return -1;
    }

    if (solution1.getVariable(variableIndex) > solution2.getVariable(variableIndex)) {
      return 1;
    }

    return 0;
  }
}
