package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Ahmad2017Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Ahmad2017() ;

    assertEquals(3, problem.numberOfVariables()) ;
    assertEquals(7, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(10.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(10.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(150.0, problem.variableBounds().get(2).getLowerBound()) ;

    assertEquals(50.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(50.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(170.0, problem.variableBounds().get(2).getUpperBound()) ;

    assertEquals("Ahmad2017", problem.name()) ;
  }

}