package org.uma.jmetal.util.solutionattribute.impl;

import org.junit.Test;
import org.uma.jmetal.solution.DoubleSolution;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenericSolutionAttributeTest {
  @Test
  public void shouldDefaultConstructorCreateASolutionAttributedWithAnIdentifierEqualToTheClassName() {

    GenericSolutionAttribute<DoubleSolution, Integer> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    assertEquals(genericSolutionAttribute.getClass(), genericSolutionAttribute.getAttributeID());
  }
  
}