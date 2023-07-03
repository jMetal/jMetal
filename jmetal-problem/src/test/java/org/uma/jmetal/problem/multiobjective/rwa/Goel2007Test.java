package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Goel2007Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Goel2007() ;

    assertEquals(4, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(0.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(3).getLowerBound()) ;

    assertEquals(1.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(3).getUpperBound()) ;

    assertEquals("Goel2007", problem.name()) ;
  }

}