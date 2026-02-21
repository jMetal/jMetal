package org.uma.jmetal.util.solutionattribute.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** @author Antonio J. Nebro  */
public class GenericSolutionAttributeTest {

  @Test
  public void
      shouldDefaultConstructorCreateASolutionAttributedWithAnIdentifierEqualToTheClassObject() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute;
    genericSolutionAttribute = new GenericSolutionAttribute<>();

    assertEquals(genericSolutionAttribute.getClass(), genericSolutionAttribute.getAttributeIdentifier());
  }

  @Test
  public void shouldConstructorCreateASolutionAttributedWithThePassedIdentifier() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute;
    Object attributeIdentifier = Double.valueOf(4);
    genericSolutionAttribute = new GenericSolutionAttribute<>(attributeIdentifier);

    assertEquals(attributeIdentifier, genericSolutionAttribute.getAttributeIdentifier());
  }

  @Test
  public void shouldGetAttributeIdentifierReturnTheRightIdentifier() {
    GenericSolutionAttribute<?, ?> genericSolutionAttribute;
    genericSolutionAttribute = new GenericSolutionAttribute<>();

    assertEquals(
        genericSolutionAttribute.getClass(), genericSolutionAttribute.getAttributeIdentifier());
  }

  @Test
  public void shouldGetAttributeReturnNullIfTheSolutionHasNoAttribute() {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    GenericSolutionAttribute<DoubleSolution, ?> genericSolutionAttribute;
    genericSolutionAttribute = new GenericSolutionAttribute<>();

    assertNull(genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldGetAttributeReturnTheAttributeValue() {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    GenericSolutionAttribute<DoubleSolution, Double> genericSolutionAttribute;
    genericSolutionAttribute = new GenericSolutionAttribute<>();

    Double value = 5.0 ;
    genericSolutionAttribute.setAttribute(solution, value);

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldSetAttributeAssignTheAttributeValueToTheSolution() {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    GenericSolutionAttribute<DoubleSolution, Double> genericSolutionAttribute;
    genericSolutionAttribute = new GenericSolutionAttribute<>();

    Double value = 5.0;

    genericSolutionAttribute.setAttribute(solution, value);

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldGetAttributesReturnAnNoAttributesWhenInitiateAnPointSolution() {

    DoubleSolution solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    assertTrue(solution.attributes().isEmpty());
  }

  @Test
  public void shouldReturnTheCorrectAttributesWhenGetAllAttributes() {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    solution.attributes().put("fake-atribute-1", 1);
    solution.attributes().put("fake-atribute-2", 2);

    assertFalse(solution.attributes().isEmpty());
    assertEquals((int) solution.attributes().get("fake-atribute-1"), 1);
    assertEquals((int) solution.attributes().get("fake-atribute-2"), 2);
  }
}
