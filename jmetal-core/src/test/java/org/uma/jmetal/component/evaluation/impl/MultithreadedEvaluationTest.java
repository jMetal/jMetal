package org.uma.jmetal.component.evaluation.impl;

import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class MultithreadedEvaluationTest extends EvaluationTestCases<DoubleSolution>{

  public MultithreadedEvaluationTest() {
    this.problem = new DummyDoubleProblem() ;
    this.evaluation = new MultithreadedEvaluation<>(8, problem) ;
  }
}
