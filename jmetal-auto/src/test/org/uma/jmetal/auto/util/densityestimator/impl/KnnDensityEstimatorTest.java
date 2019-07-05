package org.uma.jmetal.auto.util.densityestimator.impl;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KnnDensityEstimatorTest {

  @Test
  public void test() {
    KnnDensityEstimator<Solution<?>> densityEstimator = new KnnDensityEstimator<>() ;
    DoubleSolution solution1 = new Do

    Solution<?> solution1 = mock(Solution.class) ;
    when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    when(solution1.getObjectives()).thenReturn(new double[]{1.0, 4.0}) ;

    Solution<?> solution2 = mock(Solution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjectives()).thenReturn(new double[]{2.0, 3.0}) ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    densityEstimator.computeDensityEstimator(solutionList);

    int a = 4 ;
  }


}