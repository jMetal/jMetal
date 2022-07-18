package org.uma.jmetal.util.solutionattribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class GenericSolutionAttributeTest {

  @Test
  public void
      shouldDefaultConstructorCreateASolutionAttributedWithAnIdentifierEqualToTheClassObject() {
      GenericSolutionAttribute<?, ?> genericSolutionAttribute = new GenericSolutionAttribute<>();

    var solutionAttributeId =
        ReflectionTestUtils.getField(genericSolutionAttribute, "identifier");

    assertEquals(genericSolutionAttribute.getClass(), solutionAttributeId);
  }

  @Test
  public void shouldConstructorCreateASolutionAttributedWithThePassedIdentifier() {
      Object attributeIdentifier = Double.valueOf(4);
      GenericSolutionAttribute<?, ?> genericSolutionAttribute = new GenericSolutionAttribute<>(attributeIdentifier);

    var solutionAttributeId =
        ReflectionTestUtils.getField(genericSolutionAttribute, "identifier");

    assertEquals(attributeIdentifier, solutionAttributeId);
  }

  @Test
  public void shouldGetAttributeIdentifierReturnTheRightIdentifier() {
      GenericSolutionAttribute<?, ?> genericSolutionAttribute = new GenericSolutionAttribute<>();

    assertEquals(
        genericSolutionAttribute.getClass(), genericSolutionAttribute.getAttributeIdentifier());
  }

  @Test
  public void shouldGetAttributeReturnNullIfTheSolutionHasNoAttribute() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

      GenericSolutionAttribute<DoubleSolution, ?> genericSolutionAttribute = new GenericSolutionAttribute<>();

    assertNull(genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldGetAttributeReturnTheAttributeValue() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    var genericSolutionAttribute = new GenericSolutionAttribute<DoubleSolution, Double>();

    Double value = 5.0 ;
    genericSolutionAttribute.setAttribute(solution, value);

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldSetAttributeAssignTheAttributeValueToTheSolution() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    var genericSolutionAttribute = new GenericSolutionAttribute<DoubleSolution, Double>();

    Double value = 5.0;

    genericSolutionAttribute.setAttribute(solution, value);

    assertEquals(value, genericSolutionAttribute.getAttribute(solution));
  }

  @Test
  public void shouldGetAttributesReturnAnNoAttributesWhenInitiateAnPointSolution() {

    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    assertTrue(solution.attributes().isEmpty());
  }

  @Test
  public void shouldReturnTheCorrectAttributesWhenGetAllAttributes() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;

    solution.attributes().put("fake-atribute-1", 1);
    solution.attributes().put("fake-atribute-2", 2);

    assertFalse(solution.attributes().isEmpty());
    assertEquals((int) solution.attributes().get("fake-atribute-1"), 1);
    assertEquals((int) solution.attributes().get("fake-atribute-2"), 2);
  }
}
