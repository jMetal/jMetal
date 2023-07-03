package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Chen2015Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Chen2015() ;

    assertEquals(6, problem.numberOfVariables()) ;
    assertEquals(5, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(17.5, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(17.5, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(2.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(2.0, problem.variableBounds().get(3).getLowerBound()) ;
    assertEquals(5.0, problem.variableBounds().get(4).getLowerBound()) ;
    assertEquals(5.0, problem.variableBounds().get(5).getLowerBound()) ;

    assertEquals(22.5, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(22.5, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(3.0, problem.variableBounds().get(3).getUpperBound()) ;
    assertEquals(7.0, problem.variableBounds().get(4).getUpperBound()) ;
    assertEquals(6.0, problem.variableBounds().get(5).getUpperBound()) ;

    assertEquals("Chen2015", problem.name()) ;
  }

}