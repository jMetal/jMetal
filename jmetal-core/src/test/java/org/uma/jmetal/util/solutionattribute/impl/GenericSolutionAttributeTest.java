package org.uma.jmetal.util.solutionattribute.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.Solution;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenericSolutionAttributeTest {
  @Test
  public void shouldDefaultConstructorCreateASolutionAttributedWithAnIdentifierEqualToTheClassObject() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    Object solutionAttributeId = ReflectionTestUtils.getField(genericSolutionAttribute, "identifier") ;

    assertEquals(genericSolutionAttribute.getClass(), solutionAttributeId);
  }

  @Test
  public void shouldConstructorCreateASolutionAttributedWithThePassedIdentifier() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute ;
    Object attributeIdentifier = new Double(4) ;
    genericSolutionAttribute = new GenericSolutionAttribute<>(attributeIdentifier) ;

    Object solutionAttributeId = ReflectionTestUtils.getField(genericSolutionAttribute, "identifier") ;

    assertEquals(attributeIdentifier, solutionAttributeId);
  }

  @Test
  public void shouldGetAttributeIdentifierReturnTheRightIdentifier() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    assertEquals(genericSolutionAttribute.getClass(), genericSolutionAttribute.getAttributeIdentifier());
  }

  @Test
  public void shouldGetAttributeReturnNullIfTheSolutionHasNoAttribute() {
    GenericSolutionAttribute<MockedDoubleSolution, ?> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    MockedDoubleSolution solution = new MockedDoubleSolution() ;

    assertNull(genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldGetAttributeReturnTheAttributeValue() {
    GenericSolutionAttribute<MockedDoubleSolution, Integer> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    Object value = new Double(5) ;

    MockedDoubleSolution solution = mock(MockedDoubleSolution.class) ;
    when(solution.getAttribute(genericSolutionAttribute.getAttributeIdentifier())).thenReturn(value) ;

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldSetAttributeAssignTheAttributeValueToTheSolution() {
    GenericSolutionAttribute<Solution<?>, Object> genericSolutionAttribute ;
    genericSolutionAttribute = new GenericSolutionAttribute<>() ;

    Object value = new Double(5) ;

    MockedDoubleSolution solution = new MockedDoubleSolution() ;
    genericSolutionAttribute.setAttribute(solution, value);

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @SuppressWarnings("serial")
  private class MockedDoubleSolution implements Solution<Double> {
    protected Map<Object, Object> attributes ;

    public MockedDoubleSolution() {
      attributes = new HashMap<>() ;
    }

    @Override
    public void setObjective(int index, double value) {

    }

    @Override
    public double getObjective(int index) {
      return 0;
    }

    @Override
    public Double getVariableValue(int index) {
      return null;
    }

    @Override
    public void setVariableValue(int index, Double value) {

    }

    @Override
    public String getVariableValueString(int index) {
      return null;
    }

    @Override
    public int getNumberOfVariables() {
      return 0;
    }

    @Override
    public int getNumberOfObjectives() {
      return 0;
    }

    @Override
    public Solution<Double> copy() {
      return null;
    }

    @Override
    public void setAttribute(Object id, Object value) {
      attributes.put(id, value) ;
    }

    @Override
    public Object getAttribute(Object id) {
      return attributes.get(id) ;
    }
  }
}
