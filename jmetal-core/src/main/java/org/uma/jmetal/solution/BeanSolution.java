package org.uma.jmetal.solution;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;

/**
 * Generic bean implementation of {@link Solution}. Genericness means that it
 * can store any type of variable. Bean means that each piece of data
 * (variables, constraints, objectives, and attributes) is stored in an internal
 * field and each accessor (get/set method) provides a direct access to the
 * corresponding field.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <T>
 *          the type of variables
 */
@SuppressWarnings("serial")
public class BeanSolution<T> implements Solution<T> {
  private final List<T> variables;
  private final List<Double> constraints;
  private final List<Double> objectives;
  private final Map<Object, Object> attributes;

  /**
   * Creates a {@link BeanSolution} with the given values. More advanced
   * instantiation methods are provided as factory methods
   * <code>createXxx(...)</code>.
   * 
   * @param variables
   *          the variables of the {@link BeanSolution}
   * @param objectives
   *          the objectives of the {@link BeanSolution}
   * @param constraints
   *          the constraints of the {@link BeanSolution}
   * @param attributes
   *          the attributes of the {@link BeanSolution}
   */
  public BeanSolution(List<T> variables, List<Double> objectives, List<Double> constraints,
      Map<Object, Object> attributes) {
    this.variables = Objects.requireNonNull(variables, "Null variables provided");
    this.objectives = Objects.requireNonNull(objectives, "Null objectives provided");
    this.constraints = Objects.requireNonNull(constraints, "Null constraints provided");
    this.attributes = Objects.requireNonNull(attributes, "Null attributes provided");
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public List<T> getVariables() {
    return variables;
  }

  @Override
  public T getVariable(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariable(int index, T value) {
    variables.set(index, value);
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.size();
  }

  @Override
  public List<Double> getObjectivesList() {
    return objectives;
  }

  @Override
  public DoubleStream getObjectivesStream() {
    return objectives.stream().mapToDouble(d -> d);
  }

  @Override
  public double[] getObjectives() {
    return getObjectivesStream().toArray();
  }

  @Override
  public double getObjective(int index) {
    return objectives.get(index);
  }

  @Override
  public void setObjective(int index, double value) {
    objectives.set(index, value);
  }

  @Override
  public int getNumberOfConstraints() {
    return constraints.size();
  }

  @Override
  public List<Double> getConstraintsList() {
    return constraints;
  }

  @Override
  public DoubleStream getConstraintsStream() {
    return constraints.stream().mapToDouble(d -> d);
  }

  @Override
  public double[] getConstraints() {
    return getConstraintsStream().toArray();
  }

  @Override
  public double getConstraint(int index) {
    return constraints.get(index);
  }

  @Override
  public void setConstraint(int index, double value) {
    constraints.set(index, value);
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Object getAttribute(Object id) {
    // TODO NoSuchElementException if absent
    return attributes.get(id);
  }

  @Override
  public void setAttribute(Object id, Object value) {
    attributes.put(id, value);
  }

  @Override
  public boolean hasAttribute(Object id) {
    return attributes.containsKey(id);
  }

  @Override
  public Solution<T> copy() {
    return createFromSolution(this);
  }

  /**
   * Two {@link BeanSolution} instances are equal when they represent the same
   * point in the solution space. In other words, two solutions sharing the same
   * variables have the same identity.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof BeanSolution) {
      BeanSolution<?> that = (BeanSolution<?>) o;
      return Objects.equals(this.variables, that.variables);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return variables.hashCode();
  }

  /**
   * Since a {@link BeanSolution} is identified by its variables, it
   * {@link String} representation corresponds to the {@link String}
   * representation of its variables. Since this representation can be used in
   * logs and other one-line {@link String} representations, it must remain short
   * and in one line, so we only display the variables without decorations. More
   * details can be generated with {@link #toExtendedString()}.
   */
  @Override
  public String toString() {
    return variables.toString();
  }

  /**
   * Generates a full description of this {@link BeanSolution} (variables,
   * objectives, constraints, and attributes). Since it is an extended version of
   * {@link #toString()}, we still keep it compact and in one-line. For a more
   * readable version you can use {@link #getDescription()}.
   * 
   * @return an extended {@link #toString()} representation
   */
  public String toExtendedString() {
    return Map.of(//
        "V", variables, //
        "O", objectives, //
        "C", constraints, //
        "A", attributes//
    ).toString();
  }

  /**
   * Create a detailed description of this {@link BeanSolution}. It implements the
   * same representation than {@link AbstractSolution#toString()}. It does not
   * have the same constraint than {@link #toString()} of being on a single line.
   * 
   * @return a full description of this {@link BeanSolution}
   */
  public String getDescription() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("Variables: ");
    variables.forEach(var -> buffer.append(var).append(" "));
    buffer.append("Objectives: ");
    objectives.forEach(obj -> buffer.append(obj).append(" "));
    buffer.append("Constraints: ");
    constraints.forEach(con -> buffer.append(con).append(" "));
    buffer.append("\t");
    buffer.append("AlgorithmAttributes: ").append(attributes).append("\n");

    return buffer.toString();
  }

  /**
   * Creates a {@link BeanSolution} with the same variables, objectives,
   * constraints, and attributes than the provided {@link Solution}. The
   * {@link BeanSolution} is not backed by the provided {@link Solution}, though.
   * It uses its own {@link List} instances, so modifying the provided
   * {@link Solution} will not modify the {@link BeanSolution}.
   * 
   * @param <T>
   *          the type of variables
   * @param source
   *          the {@link Solution} to copy
   * @return a new {@link BeanSolution}
   */
  public static <T> BeanSolution<T> createFromSolution(Solution<T> source) {
    return new BeanSolution<>(//
        new ArrayList<>(source.getVariables()), //
        new ArrayList<>(source.getObjectivesList()), //
        new ArrayList<>(source.getConstraintsList()), //
        new HashMap<>(source.getAttributes()));
  }

  /**
   * Creates a {@link BeanSolution} with the given number of variables and
   * objectives. The number of constraints is fixed to zero. Variables are
   * initialized to <code>null</code> and objectives to zero.
   * 
   * @param <T>
   *          the type of variables
   * @param variablesSize
   *          the number of variables
   * @param objectivesSize
   *          the number of objectives
   * @return a new {@link BeanSolution}
   */
  // TODO Remove if useless, otherwise test
  public static <T> BeanSolution<T> createWithoutConstraints(int variablesSize, int objectivesSize) {
    return createWithConstraints(variablesSize, objectivesSize, 0);
  }

  /**
   * Creates a {@link BeanSolution} with the given number of variables,
   * objectives, and constraints. Variables are initialized to <code>null</code>
   * and objectives and constraints to zero.
   * 
   * @param <T>
   *          the type of variables
   * @param variablesSize
   *          the number of variables
   * @param objectivesSize
   *          the number of objectives
   * @param constraintsSize
   *          the number of constraints
   * @return a new {@link BeanSolution}
   */
  // TODO Remove if useless, otherwise test
  public static <T> BeanSolution<T> createWithConstraints(int variablesSize, int objectivesSize, int constraintsSize) {
    return create(//
        variablesSize, i -> (T) null, //
        objectivesSize, i -> 0D, //
        constraintsSize, i -> 0D);
  }

  /**
   * Creates a {@link BeanSolution} with the given number of variables,
   * objectives, and constraints. Each component is initialized with the
   * associated factory.
   * 
   * 
   * @param <T>
   *          the type of variables
   * @param variablesSize
   *          the number of variables
   * @param variableFactory
   *          the variables factory
   * @param objectivesSize
   *          the number of objectives
   * @param objectiveFactory
   *          the objectives factory
   * @param constraintsSize
   *          the number of constraints
   * @param constraintFactory
   *          the constraints factory
   * @return a new {@link BeanSolution}
   */
  // TODO Remove if useless, otherwise test
  public static <T> BeanSolution<T> create(//
      int variablesSize, IntFunction<? extends T> variableFactory, //
      int objectivesSize, IntFunction<Double> objectiveFactory, //
      int constraintsSize, IntFunction<Double> constraintFactory) {

    return new BeanSolution<>(//
        createList(variablesSize, variableFactory), //
        createList(objectivesSize, objectiveFactory), //
        createList(constraintsSize, constraintFactory), //
        new HashMap<>());
  }

  private static <T> List<T> createList(int size, IntFunction<? extends T> itemFactory) {
    return range(0, size).mapToObj(itemFactory).collect(toCollection(ArrayList::new));
  }
}
