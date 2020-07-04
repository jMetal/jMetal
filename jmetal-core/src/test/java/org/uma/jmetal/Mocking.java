package org.uma.jmetal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.uma.jmetal.solution.Solution;

/**
 * Set of helper methods to mock useful objects for jMetal tests.
 */
public class Mocking {

  /**
   * Creates an empty mock of the corresponding {@link Class}.
   * 
   * @param <T>
   *          the type to mock
   * @param classToMock
   *          the {@link Class} to mock
   * @return the mock
   */
  public static <T> T mock(Class<T> classToMock) {
    return Mockito.mock(classToMock);
  }

  /**
   * Creates a mocked {@link List} of variables.
   * 
   * @param <T>
   *          the type of variables
   * @return a mocked {@link List} of variables
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> mockVariables() {
    return mock(List.class);
  }

  /**
   * Creates a mocked {@link List} of objectives.
   * 
   * @return a mocked {@link List} of objectives
   */
  @SuppressWarnings("unchecked")
  public static List<Double> mockObjectives() {
    return mock(List.class);
  }

  /**
   * Creates a mocked {@link List} of constraints.
   * 
   * @return a mocked {@link List} of constraints
   */
  @SuppressWarnings("unchecked")
  public static List<Double> mockConstraints() {
    return mock(List.class);
  }

  /**
   * Creates a mocked {@link Map} of attributes.
   * 
   * @return a mocked {@link Map} of attributes
   */
  @SuppressWarnings("unchecked")
  public static Map<Object, Object> mockAttributes() {
    return mock(Map.class);
  }

  /**
   * Creates a mock of a {@link Solution} backed by the provided data containers.
   * 
   * @param <T>
   *          the type of {@link Solution}
   * @param variables
   *          the variables container
   * @param objectives
   *          the objectives container
   * @param constraints
   *          the constraints container
   * @param attributes
   *          the attributes container
   * @return the mocked {@link Solution}
   */
  public static <T> Solution<T> mockSolution(//
      List<T> variables, //
      List<Double> objectives, //
      List<Double> constraints, //
      Map<Object, Object> attributes) {

    @SuppressWarnings("unchecked")
    Solution<T> solution = mock(Solution.class);

    doAnswer(call -> variables).when(solution).getVariables();
    doAnswer(call -> objectives).when(solution).getObjectivesList();
    doAnswer(call -> constraints).when(solution).getConstraintsList();
    doAnswer(call -> attributes).when(solution).getAttributes();

    doAnswer(call -> variables.get(call.getArgument(0))).//
        when(solution).getVariable(anyInt());
    doAnswer(call -> objectives.get(call.getArgument(0))).//
        when(solution).getObjective(anyInt());
    doAnswer(call -> constraints.get(call.getArgument(0))).//
        when(solution).getConstraint(anyInt());
    doAnswer(call -> attributes.get(call.getArgument(0))).//
        when(solution).getAttribute(any());

    doAnswer(call -> variables.set(call.getArgument(0), call.getArgument(1)))//
        .when(solution).setVariable(anyInt(), any());
    doAnswer(call -> objectives.set(call.getArgument(0), call.getArgument(1)))//
        .when(solution).setObjective(anyInt(), anyDouble());
    doAnswer(call -> constraints.set(call.getArgument(0), call.getArgument(1)))//
        .when(solution).setConstraint(anyInt(), anyDouble());
    doAnswer(call -> attributes.put(call.getArgument(0), call.getArgument(1)))//
        .when(solution).setAttribute(any(), any());

    // TODO stub other methods

    return solution;
  }

}
