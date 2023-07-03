package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Gao2020Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Gao2020() ;

    assertEquals(9, problem.numberOfVariables()) ;
    assertEquals(3, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(40.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(0.35, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(333.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(20.0, problem.variableBounds().get(3).getLowerBound()) ;
    assertEquals(3000.0, problem.variableBounds().get(4).getLowerBound()) ;
    assertEquals(0.1, problem.variableBounds().get(5).getLowerBound()) ;
    assertEquals(308.0, problem.variableBounds().get(6).getLowerBound()) ;
    assertEquals(150.0, problem.variableBounds().get(7).getLowerBound()) ;
    assertEquals(0.1, problem.variableBounds().get(8).getLowerBound()) ;

    assertEquals(100.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(0.5, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(363.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(40.0, problem.variableBounds().get(3).getUpperBound()) ;
    assertEquals(4000.0, problem.variableBounds().get(4).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(5).getUpperBound()) ;
    assertEquals(328, problem.variableBounds().get(6).getUpperBound()) ;
    assertEquals(200, problem.variableBounds().get(7).getUpperBound()) ;
    assertEquals(2.0, problem.variableBounds().get(8).getUpperBound()) ;

    assertEquals("Gao2020", problem.name()) ;
  }

}