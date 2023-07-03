package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Ganesan2013Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Ganesan2013() ;

    assertEquals(3, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(0.25, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(10000.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(600.0, problem.variableBounds().get(2).getLowerBound()) ;

    assertEquals(0.55, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(20000.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(1100.0, problem.variableBounds().get(2).getUpperBound()) ;

    assertEquals("Ganesan2013", problem.name()) ;
  }

}