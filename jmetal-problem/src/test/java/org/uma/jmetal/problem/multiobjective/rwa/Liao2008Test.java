package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Liao2008Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Liao2008() ;

    assertEquals(5, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(1.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(1.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(1.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(1.0, problem.variableBounds().get(3).getLowerBound()) ;
    assertEquals(1.0, problem.variableBounds().get(4).getLowerBound()) ;

    assertEquals(3.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(3).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(4).getUpperBound()) ;

    assertEquals("Liao2008", problem.name()) ;
  }

}