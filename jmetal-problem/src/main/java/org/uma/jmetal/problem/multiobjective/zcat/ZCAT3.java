package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import java.util.function.Function;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F1;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT3 extends ZCAT {

  private Function<double[], double[]> fFunction ;
  private Function<double[], double[]> gFunction ;

  public ZCAT3(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    int paretoSetDimension = numberOfObjectives - 1 ; // m

    fFunction = new F1(numberOfObjectives) ;
    gFunction = new G0(numberOfVariables, paretoSetDimension) ;
  }

  public ZCAT3() {
    this(3, 30, true, 1, false, false) ;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] normalizedVariables = zcatGetY(solution.variables()) ;
    double[] alpha = zcatGetAlpha(normalizedVariables, numberOfObjectives, fFunction) ;

    return null;
  }

  public static void main(String[] args) {
    DoubleProblem zcat3 = new ZCAT3() ;
    zcat3.variableBounds().forEach(i -> System.out.print("[" + i.getLowerBound() + ", "
        + i.getUpperBound()+ "]" ));

    DoubleSolution solution = zcat3.createSolution() ;
    Collections.fill(solution.variables(), 0.4);


  }

}

