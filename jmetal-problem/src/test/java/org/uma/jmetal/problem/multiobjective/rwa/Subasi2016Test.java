package org.uma.jmetal.problem.multiobjective.rwa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Subasi2016Test {
  @Test
  void constructorWorksProperly() {
    var problem = new Subasi2016() ;

    assertEquals(5, problem.numberOfVariables()) ;
    assertEquals(2, problem.numberOfObjectives()) ;
    assertEquals(0, problem.numberOfConstraints()) ;

    assertEquals(20.0, problem.variableBounds().get(0).getLowerBound()) ;
    assertEquals(6.0, problem.variableBounds().get(1).getLowerBound()) ;
    assertEquals(20.0, problem.variableBounds().get(2).getLowerBound()) ;
    assertEquals(0.0, problem.variableBounds().get(3).getLowerBound()) ;
    assertEquals(8000.0, problem.variableBounds().get(4).getLowerBound()) ;

    assertEquals(60.0, problem.variableBounds().get(0).getUpperBound()) ;
    assertEquals(15.0, problem.variableBounds().get(1).getUpperBound()) ;
    assertEquals(40.0, problem.variableBounds().get(2).getUpperBound()) ;
    assertEquals(30.0, problem.variableBounds().get(3).getUpperBound()) ;
    assertEquals(25000.0, problem.variableBounds().get(4).getUpperBound()) ;

    assertEquals("Subasi2016", problem.name()) ;
  }

}