package org.uma.jmetal.solution;

import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.uma.jmetal.Mocking.*;
import static org.uma.jmetal.Naming.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BeanSolutionTest {

  static Stream<Arguments> testGetNumberOfVariablesReturnsVariablesSize() {
    return Stream.of(//
        arguments(0, Arrays.asList()), //
        arguments(1, Arrays.asList(new Object())), //
        arguments(2, Arrays.asList("foo", "bar")), //
        arguments(3, Arrays.asList(1, 2, 3)));
  }

  @ParameterizedTest(name = "{0} in {1}")
  @MethodSource
  <T> void testGetNumberOfVariablesReturnsVariablesSize(int size, List<T> variables) {
    // GIVEN
    BeanSolution<T> solution = new BeanSolution<>(variables, mockObjectives(), mockConstraints(), mockAttributes());

    // WHEN
    int result = solution.getNumberOfVariables();

    // THEN
    assertEquals(size, result);
  }

  static Stream<Arguments> testGetVariablesReturnsProvidedVariables() {
    return Stream.of(//
        arguments(Arrays.asList()), //
        arguments(Arrays.asList(new Object())), //
        arguments(Arrays.asList("foo", "bar")), //
        arguments(Arrays.asList(1, 2, 3)));
  }

  @ParameterizedTest
  @MethodSource
  <T> void testGetVariablesReturnsProvidedVariables(List<T> variables) {
    // GIVEN
    BeanSolution<T> solution = new BeanSolution<>(variables, mockObjectives(), mockConstraints(), mockAttributes());

    // WHEN
    List<T> result = solution.getVariables();

    // THEN
    assertEquals(variables, result);
  }

  static Stream<Arguments> testGetVariableReturnsProvidedValue() {
    Object object = new Object();
    return Stream.of(//
        arguments(Arrays.asList(object), 0, object), //
        arguments(Arrays.asList("foo", "bar"), 1, "bar"), //
        arguments(Arrays.asList(1, 2, 3), 1, 2));
  }

  @ParameterizedTest(name = "{0} at {1} has {2}")
  @MethodSource
  <T> void testGetVariableReturnsProvidedValue(List<T> variables, int index, T value) {
    // GIVEN
    BeanSolution<T> solution = new BeanSolution<>(variables, mockObjectives(), mockConstraints(), mockAttributes());

    // WHEN
    T result = solution.getVariable(index);

    // THEN
    assertEquals(value, result);
  }

  static Stream<Arguments> testSetVariableUpdatesTargetValue() {
    return Stream.of(//
        arguments(Arrays.asList(new Object()), 0, new Object()), //
        arguments(Arrays.asList("foo", "bar"), 1, "test"), //
        arguments(Arrays.asList(1, 2, 3), 1, 42));
  }

  @ParameterizedTest(name = "{0} changes item {1} with {2}")
  @MethodSource
  <T> void testSetVariableUpdatesTargetValue(List<T> variables, int index, T value) {
    // GIVEN
    BeanSolution<T> solution = new BeanSolution<>(variables, mockObjectives(), mockConstraints(), mockAttributes());
    assertNotEquals(value, solution.getVariable(index));

    // WHEN
    solution.setVariable(index, value);

    // THEN
    assertEquals(value, solution.getVariable(index));
  }

  static Stream<Arguments> testGetNumberOfObjectivesReturnsObjectivesSize() {
    return Stream.of(//
        arguments(0, Arrays.asList()), //
        arguments(1, Arrays.asList(5D)), //
        arguments(2, Arrays.asList(8D, 9D)), //
        arguments(3, Arrays.asList(3D, 1D, 7D)));
  }

  @ParameterizedTest(name = "{0} in {1}")
  @MethodSource
  void testGetNumberOfObjectivesReturnsObjectivesSize(int size, List<Double> objectives) {
    // GIVEN
    BeanSolution<Double> solution = new BeanSolution<>(mockVariables(), objectives, mockConstraints(),
        mockAttributes());

    // WHEN
    int result = solution.getNumberOfObjectives();

    // THEN
    assertEquals(size, result);
  }

  static Stream<Arguments> testGetObjectivesListReturnsProvidedObjectives() {
    return Stream.of(//
        arguments(Arrays.asList()), //
        arguments(Arrays.asList(1D)), //
        arguments(Arrays.asList(1D, 2D)), //
        arguments(Arrays.asList(1D, 2D, 3D)));
  }

  @ParameterizedTest
  @MethodSource
  void testGetObjectivesListReturnsProvidedObjectives(List<Double> objectives) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), objectives, mockConstraints(), mockAttributes());

    // WHEN
    List<Double> result = solution.getObjectivesList();

    // THEN
    assertEquals(objectives, result);
  }

  static Stream<Arguments> testGetObjectivesReturnsObjectivesAsArray() {
    return Stream.of(//
        arguments(Arrays.asList(), new double[] {}), //
        arguments(Arrays.asList(1D), new double[] { 1D }), //
        arguments(Arrays.asList(1D, 2D), new double[] { 1D, 2D }), //
        arguments(Arrays.asList(1D, 2D, 3D), new double[] { 1D, 2D, 3D }));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource
  void testGetObjectivesReturnsObjectivesAsArray(List<Double> objectives, double[] array) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), objectives, mockConstraints(), mockAttributes());

    // WHEN
    double[] result = solution.getObjectives();

    // THEN
    assertArrayEquals(array, result);
  }

  static Stream<Arguments> testGetObjectiveReturnsProvidedValue() {
    return Stream.of(//
        arguments(Arrays.asList(123D), 0, 123D), //
        arguments(Arrays.asList(123D, 456D), 1, 456D), //
        arguments(Arrays.asList(1D, 2D, 3D), 1, 2D));
  }

  @ParameterizedTest(name = "{0} at {1} has {2}")
  @MethodSource
  void testGetObjectiveReturnsProvidedValue(List<Double> objectives, int index, Double value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), objectives, mockConstraints(), mockAttributes());

    // WHEN
    double result = solution.getObjective(index);

    // THEN
    assertEquals(value, result);
  }

  static Stream<Arguments> testSetObjectiveUpdatesTargetValue() {
    return Stream.of(//
        arguments(Arrays.asList(123D), 0, 456D), //
        arguments(Arrays.asList(123D, 456D), 1, 789D), //
        arguments(Arrays.asList(1D, 2D, 3D), 1, 42D));
  }

  @ParameterizedTest(name = "{0} changes item {1} with {2}")
  @MethodSource
  void testSetObjectiveUpdatesTargetValue(List<Double> objectives, int index, Double value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), objectives, mockConstraints(), mockAttributes());
    assertNotEquals(value, solution.getObjective(index));

    // WHEN
    solution.setObjective(index, value);

    // THEN
    assertEquals(value, solution.getObjective(index));
  }

  static Stream<Arguments> testGetNumberOfConstraintsReturnsConstraintsSize() {
    return Stream.of(//
        arguments(0, Arrays.asList()), //
        arguments(1, Arrays.asList(5D)), //
        arguments(2, Arrays.asList(8D, 9D)), //
        arguments(3, Arrays.asList(3D, 1D, 7D)));
  }

  @ParameterizedTest(name = "{0} in {1}")
  @MethodSource
  void testGetNumberOfConstraintsReturnsConstraintsSize(int size, List<Double> constraints) {
    // GIVEN
    BeanSolution<Double> solution = new BeanSolution<>(mockVariables(), mockObjectives(), constraints,
        mockAttributes());

    // WHEN
    int result = solution.getNumberOfConstraints();

    // THEN
    assertEquals(size, result);
  }

  static Stream<Arguments> testGetConstraintsListReturnsProvidedConstraints() {
    return Stream.of(//
        arguments(Arrays.asList()), //
        arguments(Arrays.asList(1D)), //
        arguments(Arrays.asList(1D, 2D)), //
        arguments(Arrays.asList(1D, 2D, 3D)));
  }

  @ParameterizedTest
  @MethodSource
  void testGetConstraintsListReturnsProvidedConstraints(List<Double> constraints) {
    // GIVEN
    BeanSolution<Object> solution = new BeanSolution<>(mockVariables(), mockObjectives(), constraints,
        mockAttributes());

    // WHEN
    List<Double> result = solution.getConstraintsList();

    // THEN
    assertEquals(constraints, result);
  }

  static Stream<Arguments> testGetConstraintsReturnsConstraintsAsArray() {
    return Stream.of(//
        arguments(Arrays.asList(), new double[] {}), //
        arguments(Arrays.asList(1D), new double[] { 1D }), //
        arguments(Arrays.asList(1D, 2D), new double[] { 1D, 2D }), //
        arguments(Arrays.asList(1D, 2D, 3D), new double[] { 1D, 2D, 3D }));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource
  void testGetConstraintsReturnsConstraintsAsArray(List<Double> constraints, double[] array) {
    // GIVEN
    BeanSolution<Object> solution = new BeanSolution<>(mockVariables(), mockObjectives(), constraints,
        mockAttributes());

    // WHEN
    double[] result = solution.getConstraints();

    // THEN
    assertArrayEquals(array, result);
  }

  static Stream<Arguments> testGetConstraintReturnsProvidedValue() {
    return Stream.of(//
        arguments(Arrays.asList(123D), 0, 123D), //
        arguments(Arrays.asList(123D, 456D), 1, 456D), //
        arguments(Arrays.asList(1D, 2D, 3D), 1, 2D));
  }

  @ParameterizedTest(name = "{0} at {1} has {2}")
  @MethodSource
  void testGetConstraintReturnsProvidedValue(List<Double> constraints, int index, Double value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), constraints, mockAttributes());

    // WHEN
    double result = solution.getConstraint(index);

    // THEN
    assertEquals(value, result);
  }

  static Stream<Arguments> testSetConstraintUpdatesTargetValue() {
    return Stream.of(//
        arguments(Arrays.asList(123D), 0, 456D), //
        arguments(Arrays.asList(123D, 456D), 1, 789D), //
        arguments(Arrays.asList(1D, 2D, 3D), 1, 42D));
  }

  @ParameterizedTest(name = "{0} changes item {1} with {2}")
  @MethodSource
  void testSetConstraintUpdatesTargetValue(List<Double> constraints, int index, Double value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), constraints, mockAttributes());
    assertNotEquals(value, solution.getConstraint(index));

    // WHEN
    solution.setConstraint(index, value);

    // THEN
    assertEquals(value, solution.getConstraint(index));
  }

  static Stream<Arguments> testGetAttributesReturnsProvidedAttributes() {
    return Stream.of(//
        arguments(Map.of()), //
        arguments(Map.of("foo", "bar")), //
        arguments(Map.of(new Object(), new String())), //
        arguments(Map.of("a", 123, "b", 456)));
  }

  @ParameterizedTest
  @MethodSource
  void testGetAttributesReturnsProvidedAttributes(Map<Object, Object> attributes) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), mockConstraints(), attributes);

    // WHEN
    Map<Object, Object> result = solution.getAttributes();

    // THEN
    assertEquals(attributes, result);
  }

  static Stream<Arguments> testGetAttributeReturnsProvidedValue() {
    Object key = new Object();
    String value = new String();
    return Stream.of(//
        arguments(Map.of("foo", "bar"), "test", null), //
        arguments(Map.of("foo", "bar"), "foo", "bar"), //
        arguments(Map.of(key, value), key, value), //
        arguments(Map.of("a", 123, "b", 456), "b", 456));
  }

  @ParameterizedTest(name = "{0} searched for {1} returns {2}")
  @MethodSource
  void testGetAttributeReturnsProvidedValue(Map<Object, Object> attributes, Object id, Object value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), mockConstraints(), attributes);

    // WHEN
    Object result = solution.getAttribute(id);

    // THEN
    assertEquals(value, result);
  }

  static Stream<Arguments> testSetAttributeUpdatesTargetValue() {
    Object key = new Object();
    return Stream.of(//
        arguments(Map.of(), "foo", "bar"), //
        arguments(Map.of("foo", "bar"), "foo", "test"), //
        arguments(Map.of(key, new String()), key, new Object()), //
        arguments(Map.of("a", 123, "b", 456), "b", 789));
  }

  @ParameterizedTest(name = "{0} changes {1} with {2}")
  @MethodSource
  void testSetAttributeUpdatesTargetValue(Map<Object, Object> attributes, Object id, Object value) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), mockConstraints(),
        new HashMap<>(attributes));
    assertNotEquals(value, solution.getAttribute(id));

    // WHEN
    solution.setAttribute(id, value);

    // THEN
    assertEquals(value, solution.getAttribute(id));
  }

  static Stream<Arguments> testHasAttributeReturnsAttributePresence() {
    return Stream.of(//
        arguments(Map.of("foo", "bar"), "foo", true), //
        arguments(Map.of("foo", "bar"), "bar", false));
  }

  @ParameterizedTest(name = "{0} has {1} = {2}")
  @MethodSource
  void testHasAttributeReturnsAttributePresence(Map<Object, Object> attributes, Object id, boolean presence) {
    // GIVEN
    BeanSolution<?> solution = new BeanSolution<>(mockVariables(), mockObjectives(), mockConstraints(), attributes);

    // WHEN
    boolean result = solution.hasAttribute(id);

    // THEN
    assertEquals(presence, result);
  }

  static Stream<Arguments> testEqualsOnlySolutionsWithSameVariables() {
    List<?> var = asList("foo", "bar");
    List<?> var2 = asList("test", "bar");
    List<Double> obj = asList(1D, 2D, 3D);
    List<Double> obj2 = asList(1D, 8D, 3D);
    List<Double> con = asList(4D, 5D, 6D);
    List<Double> con2 = asList(4D, 0D, 6D);
    Map<Object, Object> att = Map.of("a", 1, "b", 2);
    Map<Object, Object> att2 = Map.of("a", 9, "b", 2);
    BeanSolution<?> solution = new BeanSolution<>(var, obj, con, att);
    return Stream.of(//
        // Equal solutions
        arguments(true, solution, solution), //
        arguments(true, solution, name("equivalent solution", new BeanSolution<>(var, obj, con, att))), //
        arguments(true, solution, name("different objectives", new BeanSolution<>(var, obj2, con, att))), //
        arguments(true, solution, name("different constraints", new BeanSolution<>(var, obj, con2, att))), //
        arguments(true, solution, name("different attributes", new BeanSolution<>(var, obj, con, att2))), //
        arguments(true, solution, name("only same variables", new BeanSolution<>(var, obj2, con2, att2))), //

        // Not equal solutions
        arguments(false, solution, name("different variables", new BeanSolution<>(var2, obj, con, att))), //
        arguments(false, solution, name("completely different solution", new BeanSolution<>(var2, obj2, con2, att2))), //

        // Other objects
        arguments(false, solution, BeanSolution.class), //
        arguments(false, solution, new Object()), //
        arguments(false, solution, null));
  }

  @ParameterizedTest(name = "{0} when comparing {1} with {2}")
  @MethodSource
  void testEqualsOnlySolutionsWithSameVariables(boolean equals, BeanSolution<?> solution, Object other) {
    // GIVEN

    // WHEN
    boolean result = solution.equals(other);

    // THEN
    assertEquals(equals, result);
  }

  @SuppressWarnings("serial")
  static Stream<Arguments> testSameHashcodeWhenEqualsReturnsTrue() {
    List<Object> var = asList("foo", "bar");
    List<Double> obj = asList(1D, 2D, 3D);
    List<Double> obj2 = asList(1D, 8D, 3D);
    List<Double> con = asList(4D, 5D, 6D);
    List<Double> con2 = asList(4D, 0D, 6D);
    Map<Object, Object> att = Map.of("a", 1, "b", 2);
    Map<Object, Object> att2 = Map.of("a", 9, "b", 2);
    BeanSolution<?> solution = new BeanSolution<>(var, obj, con, att);
    // Cannot use name(...) here, so directly override toString()
    return Stream.of(//
        arguments(solution, solution), //
        arguments(solution, new BeanSolution<Object>(var, obj, con, att) {
          public String toString() {
            return "equivalent solution";
          };
        }), //
        arguments(solution, new BeanSolution<Object>(var, obj2, con, att) {
          public String toString() {
            return "different objectives";
          }
        }), //
        arguments(solution, new BeanSolution<Object>(var, obj, con2, att) {
          public String toString() {
            return "different constraints";
          }
        }), //
        arguments(solution, new BeanSolution<Object>(var, obj, con, att2) {
          public String toString() {
            return "different attributes";
          }
        }), //
        arguments(solution, new BeanSolution<Object>(var, obj2, con2, att2) {
          public String toString() {
            return "only same variables";
          }
        }));
  }

  @ParameterizedTest(name = "{0} vs. {1}")
  @MethodSource
  void testSameHashcodeWhenEqualsReturnsTrue(BeanSolution<?> solution1, BeanSolution<?> solution2) {
    assertEquals(solution1.hashCode(), solution2.hashCode());
  }

  static Stream<Arguments> testCopyHasEqualComponentsToSource() {
    List<?> variables = asList("foo", "bar");
    List<Double> objectives = asList(1D, 2D, 3D);
    List<Double> constraints = asList(4D, 5D, 6D);
    Map<Object, Object> attributes = Map.of("foo", "bar");
    BeanSolution<?> solution = new BeanSolution<>(variables, objectives, constraints, attributes);
    return Stream.of(//
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getVariables())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getObjectivesList())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getConstraintsList())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getAttributes())));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource
  void testCopyHasEqualComponentsToSource(BeanSolution<?> source, SolutionReader componentReader) {
    // GIVEN
    Object expected = componentReader.read(source);
    Solution<?> copy = source.copy();

    // WHEN
    Object actual = componentReader.read(copy);

    // THEN
    assertEquals(expected, actual);
  }

  static Stream<Arguments> beanSolutionChanges() {
    List<String> variables = asList("foo", "bar");
    List<Double> objectives = asList(1D, 2D, 3D);
    List<Double> constraints = asList(4D, 5D, 6D);
    Map<Object, Object> attributes = new HashMap<>(Map.of("foo", "bar"));
    BeanSolution<String> solution = new BeanSolution<>(variables, objectives, constraints, attributes);
    return Stream.of(//
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getVariable(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setVariable(0, "test"))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getObjective(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setObjective(0, 8D))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getConstraint(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setConstraint(0, 9D))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getAttribute("foo")),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setAttribute("foo", "test"))));
  }

  @ParameterizedTest(name = "{1} modified with {2}")
  @MethodSource("beanSolutionChanges")
  <T> void testCopyIsNotModifiedWithSource(BeanSolution<T> source, SolutionReader componentReader,
      SolutionChanger<T> componentChanger) {
    // GIVEN
    Solution<T> copy = source.copy();
    Object sourceBefore = componentReader.read(source);
    Object copyBefore = componentReader.read(copy);

    // WHEN
    componentChanger.change(source);

    // THEN
    Object sourceAfter = componentReader.read(source);
    Object copyAfter = componentReader.read(copy);
    if (Objects.equals(sourceBefore, sourceAfter)) {
      throw new IllegalArgumentException("The change is ineffective on the tested component");
    }
    assertEquals(copyBefore, copyAfter);
  }

  @ParameterizedTest(name = "{1} modified with {2}")
  @MethodSource("beanSolutionChanges")
  <T> void testSourceIsNotModifiedWithCopy(BeanSolution<T> source, SolutionReader componentReader,
      SolutionChanger<T> componentChanger) {
    // GIVEN
    Solution<T> copy = source.copy();
    Object sourceBefore = componentReader.read(source);
    Object copyBefore = componentReader.read(copy);

    // WHEN
    componentChanger.change(copy);

    // THEN
    Object sourceAfter = componentReader.read(source);
    Object copyAfter = componentReader.read(copy);
    if (Objects.equals(copyBefore, copyAfter)) {
      throw new IllegalArgumentException("The change is ineffective on the tested component");
    }
    assertEquals(sourceBefore, sourceAfter);
  }

  static Stream<Arguments> testCreateFromSolutionGeneratesSolutionWithEqualComponentsToSource() {
    List<String> variables = asList("foo", "bar");
    List<Double> objectives = asList(1D, 2D, 3D);
    List<Double> constraints = asList(4D, 5D, 6D);
    Map<Object, Object> attributes = Map.of("foo", "bar");
    Solution<String> solution = mockSolution(variables, objectives, constraints, attributes);
    return Stream.of(//
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getVariables())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getObjectivesList())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getConstraintsList())), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getAttributes())));
  }

  @ParameterizedTest(name = "{1}")
  @MethodSource
  void testCreateFromSolutionGeneratesSolutionWithEqualComponentsToSource(Solution<?> source,
      SolutionReader componentReader) {
    // GIVEN
    Object expected = componentReader.read(source);
    Solution<?> copy = BeanSolution.createFromSolution(source);

    // WHEN
    Object actual = componentReader.read(copy);

    // THEN
    assertEquals(expected, actual);
  }

  static Stream<Arguments> solutionChanges() {
    List<String> variables = asList("foo", "bar");
    List<Double> objectives = asList(1D, 2D, 3D);
    List<Double> constraints = asList(4D, 5D, 6D);
    HashMap<Object, Object> attributes = new HashMap<>(Map.of("foo", "bar"));
    Solution<String> solution = mockSolution(variables, objectives, constraints, attributes);
    return Stream.of(//
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getVariable(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setVariable(0, "test"))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getObjective(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setObjective(0, 8D))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getConstraint(0)),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setConstraint(0, 9D))), //
        arguments(solution, nameAsCalledMethod((SolutionReader) sol -> sol.getAttribute("foo")),
            nameAsCalledMethod((SolutionChanger<String>) sol -> sol.setAttribute("foo", "test"))));
  }

  @ParameterizedTest(name = "{1} modified with {2}")
  @MethodSource("solutionChanges")
  <T> void testCreateFromSolutionGeneratesSolutionNotModifiedWithSource(Solution<T> source,
      SolutionReader componentReader, SolutionChanger<T> componentChanger) {
    // GIVEN
    BeanSolution<T> copy = BeanSolution.createFromSolution(source);
    Object sourceBefore = componentReader.read(source);
    Object copyBefore = componentReader.read(copy);

    // WHEN
    componentChanger.change(source);

    // THEN
    Object sourceAfter = componentReader.read(source);
    Object copyAfter = componentReader.read(copy);
    if (Objects.equals(sourceBefore, sourceAfter)) {
      throw new IllegalArgumentException("The change is ineffective on the tested component");
    }
    assertEquals(copyBefore, copyAfter);
  }

  @ParameterizedTest(name = "{1} modified with {2}")
  @MethodSource("solutionChanges")
  <T> void testCreateFromSolutionGeneratesSolutionNotModifyingSource(Solution<T> source, SolutionReader componentReader,
      SolutionChanger<T> componentChanger) {
    // GIVEN
    BeanSolution<T> copy = BeanSolution.createFromSolution(source);
    Object sourceBefore = componentReader.read(source);
    Object copyBefore = componentReader.read(copy);

    // WHEN
    componentChanger.change(copy);

    // THEN
    Object sourceAfter = componentReader.read(source);
    Object copyAfter = componentReader.read(copy);
    if (Objects.equals(copyBefore, copyAfter)) {
      throw new IllegalArgumentException("The change is ineffective on the tested component");
    }
    assertEquals(sourceBefore, sourceAfter);
  }

  static Stream<Arguments> testStringContainsRelevantComponent() {
    List<String> variables = name("variables", mockVariables());
    List<Double> objectives = name("objectives", mockObjectives());
    List<Double> constraints = name("constraints", mockConstraints());
    Map<Object, Object> attributes = name("attributes", mockAttributes());
    BeanSolution<String> solution = new BeanSolution<>(variables, objectives, constraints, attributes);
    return Stream.of(//
        arguments(solution, nameAsCalledMethod((StringGenerator) BeanSolution::toString), variables), //

        arguments(solution, nameAsCalledMethod((StringGenerator) BeanSolution::toExtendedString), variables), //
        arguments(solution, nameAsCalledMethod((StringGenerator) BeanSolution::toExtendedString), objectives), //
        arguments(solution, nameAsCalledMethod((StringGenerator) BeanSolution::toExtendedString), constraints), //
        arguments(solution, nameAsCalledMethod((StringGenerator) BeanSolution::toExtendedString), attributes));
  }

  @ParameterizedTest(name = "{1} contains {2}")
  @MethodSource
  void testStringContainsRelevantComponent(BeanSolution<String> solution, StringGenerator string, Object component) {
    // GIVEN

    // WHEN
    String result = string.generate(solution);

    // THEN
    assertTrue(result.contains(component.toString()),
        String.format("'%s' do not appear properly in '%s'", component, result));
  }

  @Test
  void testGetDescriptionIsNotNull() {
    // GIVEN
    BeanSolution<String> solution = new BeanSolution<>(//
        mockVariables(), mockObjectives(), //
        mockConstraints(), mockAttributes());

    // WHEN
    String result = solution.getDescription();

    // THEN
    assertNotNull(result);
  }

  @FunctionalInterface
  public interface SolutionReader {
    Object read(Solution<?> solution);
  }

  @FunctionalInterface
  public interface SolutionChanger<T> {
    void change(Solution<T> solution);
  }

  @FunctionalInterface
  public interface StringGenerator {
    String generate(BeanSolution<?> solution);
  }
}
