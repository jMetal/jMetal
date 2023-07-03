package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Padhi2016Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Padhi2016() ;

    assertEquals(5, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(1.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(10.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(850.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(20.0, problem.variableBounds().get(3).getLowerBound()) ;
    assertEquals(4.0, problem.variableBounds().get(4).getLowerBound()) ;

    assertEquals(1.4, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(26.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(1650.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(40.0, problem.variableBounds().get(3).getUpperBound()) ;
    assertEquals(8.0, problem.variableBounds().get(4).getUpperBound()) ;

    assertEquals("Padhi2016", problem.name()) ;
  }
}