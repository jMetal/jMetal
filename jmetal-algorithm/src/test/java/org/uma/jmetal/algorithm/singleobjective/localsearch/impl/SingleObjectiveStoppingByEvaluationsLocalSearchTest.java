package org.uma.jmetal.algorithm.singleobjective.localsearch.impl;

import org.junit.Test;
import org.mockito.internal.matchers.Any;
import org.uma.jmetal.algorithm.impl.AbstractLocalSearch;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleObjectiveStoppingByEvaluationsLocalSearchTest {

 @Test
 public void shouldImproveCarryOutZeroIterationIfTheMaximumNumberOfEvaluationsIsZero() {
   BinaryProblem problem = mock(BinaryProblem.class) ;
   AbstractLocalSearch<BinarySolution> localSearch = new SingleObjectiveStoppingByEvaluationsLocalSearch<>(problem, 0) ;

   localSearch.run();

   assertEquals(0, localSearch.getNumberOfEvaluations());
 }
}