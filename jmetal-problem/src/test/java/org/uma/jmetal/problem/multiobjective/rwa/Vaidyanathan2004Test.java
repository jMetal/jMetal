package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Vaidyanathan2004Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Vaidyanathan2004() ;

    assertEquals(4, problem.numberOfVariables()) ;
    assertEquals(4, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(0.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(3).getLowerBound()) ;

    assertEquals(1.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(1.0, problem.variableBounds().get(3).getUpperBound()) ;

    assertEquals("Vaidyanathan2004", problem.name()) ;
  }

}