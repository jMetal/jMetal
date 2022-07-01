package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public abstract class AbstractLSMOP extends AbstractDoubleProblem {

  protected int nk; // Number of subcomponents in each variable group
  protected List<Integer> subLen; // Number of variables in each subcomponent
  protected List<Integer> len;    // Cumulative sum of lengths of variable groups

  protected AbstractLSMOP(int nk, int numberOfVariables, int numberOfObjectives) {
    super();
    setNumberOfObjectives(numberOfObjectives);

    List<Double> lowerLimit = new ArrayList<Double>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<Double>(numberOfVariables);

    for (int i = 0; i < getNumberOfObjectives() - 1; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    for (int i = getNumberOfObjectives() - 1; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(10.0);
    }

    setVariableBounds(lowerLimit, upperLimit);

    this.nk = nk;
    double c = 3.8 * 0.1 * (1 - 0.1);
    double sum = c;

    List<Double> c_list = new ArrayList<>(getNumberOfObjectives());
    c_list.add(c);
    for (int i = 0; i < getNumberOfObjectives() - 1; i++) {
      c = 3.8 * c * (1.0 - c);
      c_list.add(c);
      sum += c;
    }

    this.subLen = new ArrayList<>(getNumberOfObjectives());
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      int aux = (int) Math.floor(
          c_list.get(i) / sum * (numberOfVariables - getNumberOfObjectives() + 1) / this.nk);
      subLen.add(aux);
    }

    len = new ArrayList<>(subLen.size() + 1);
    len.add(0);
    int cum = 0;
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      cum += subLen.get(i) * this.nk;
      this.len.add(cum);
    }

  }

  protected abstract Function getOddFunction();

  protected abstract Function getEvenFunction();

  protected abstract List<Double> evaluate(List<Double> variables);

  @java.lang.Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> variables = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      variables.add(solution.variables().get(i));
    }
    List<Double> y = evaluate(variables);

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.objectives()[i] = y.get(i);
    }
    return solution;
  }

}
