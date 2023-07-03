package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Xu2020Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Xu2020() ;

    assertEquals(4, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(12.56, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(0.02, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(1.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(0.5, problem.variableBounds().get(3).getLowerBound()) ;

    assertEquals(25.12, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(0.06, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(5.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(2.0, problem.variableBounds().get(3).getUpperBound()) ;

    assertEquals("Xu2020", problem.name()) ;
  }
}