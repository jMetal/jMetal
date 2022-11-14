.. _constraints:

Constraint handling
===================

:Author: Antonio J. Nebro
:Version: 1.1
:Date: 2022-3-28

Constraint handling in jMetal involves solutions, problems, and comparators. Currently, two constraint handling mechanisms are provided:

* Number of violated constraints
* Overall constraint violation degree, which is based on the scheme used in NSGA-II (described in `[DPA02] <https://doi.org/10.1109/4235.996017>`_).

If we take a look to the `Solution` interface:

.. code-block:: java

    package org.uma.jmetal.solution;
    
    public interface Solution<T> extends Serializable {
      List<T> variables() ;
      double[] objectives() ;
      double[] constraints() ;
      Map<Object,Object> attributes() ;

      Solution<T> copy() ;
    }

the method `constraints()` returns an array of double values, where each element represents some kind of potential violation degree. These values are usually assigned when a solution is evaluated by a constrained problem; if the problem has no constraints, that method will return an empty array. 

An example of constrained problem is `Srinivas <https://github.com/jMetal/jMetal/blob/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/multiobjective/Srinivas.java>`_, defined in `[DPA02] <https://doi.org/10.1109/4235.996017>`_:


.. code-block:: java

  public class Srinivas extends AbstractDoubleProblem {

    /** Constructor */
    public Srinivas()  {
      setNumberOfVariables(2);
      setNumberOfObjectives(2);
      setNumberOfConstraints(2);
      setName("Srinivas");

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-20.0);
        upperLimit.add(20.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution)  {
      double[] f = new double[solution.getNumberOfVariables()];

      double x1 = solution.getVariable(0);
      double x2 = solution.getVariable(1);
      f[0] = 2.0 + (x1 - 2.0) * (x1 - 2.0) + (x2 - 1.0) * (x2 - 1.0);
      f[1] = 9.0 * x1 - (x2 - 1.0) * (x2 - 1.0);

      solution.setObjective(0, f[0]);
      solution.setObjective(1, f[1]);

      evaluateConstraints(solution);
    }

    /** EvaluateConstraints() method  */
    public void evaluateConstraints(DoubleSolution solution)  {
      double[] constraint = new double[this.getNumberOfConstraints()];

      double x1 = solution.getVariable(0) ;
      double x2 = solution.getVariable(1) ;

      constraint[0] = 1.0 - (x1 * x1 + x2 * x2) / 225.0;
      constraint[1] = (3.0 * x2 - x1) / 10.0 - 1.0;

      IntStream.range(0, getNumberOfConstraints())
          .forEach(i -> solution.constraints()[i] = constraint[i]);
      }
    }
  }

We can observe that the problem formulation includes two constraints, which are defined in the `evaluateConstraints()` method that is called after a solution is evaluated. According to the NSGA-II constraint handling scheme, the requirement to work with constraints is that every constraint must be expressed as an inequality of type `expression >=0.0`. This way, when `expression < 0.0` then it is considered as a constraint violation, and the overall constraint violation degree will the sum of the values of the constraints array. If that value is negative, then the solution is infeasible.

To facilitate the use of constraints, jMetal provides the utility `ConstraintHandling <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ConstraintHandling.java>`_ class, which provides the following static methods:

* `isFeasible(Solution solution)`: returns true if the solution has no constraints or the number of violated constraints is zero.

* `numberOfViolatedConstraints(Solution solution)`: returns the number of violated constraints.

* `overallConstraintViolationDegree(Solution solution)`: returns the overall constraint violation degree of a solution.

* `feasibilityRatio(List<Solution> solutions)`:  computes the ratio of feasible solutions in a solution list.

* `numberOfViolatedConstraints(Solution solution, int number)`: sets the number of violated constraints of a solution.

* `overallConstraintViolationDegree(Solution solution, double value)`: sets the overall constraint violation degree of a solution.

The last two methods are included to allow the adoption of adhoc mechanisms to compute both the number of violated constraints and the overall constraint violation degree. To cope with this possibility, the `ConstraintHandling <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ConstraintHandling.java>`_ class includes the following enum type:

.. code-block:: java

  public enum PRECOMPUTED {
    OVERALL_CONSTRAINT_VIOLATION,
    NUMBER_OF_VIOLATED_CONSTRAINTS
  }

It is assumed that a solution may have an attribute whose key value is `OVERALL_CONSTRAINT_VIOLATION` or `NUMBER_OF_VIOLATED_CONSTRAINTS`. If so, the associated values are returned when querying the constraint properties of the solution; otherwise, the constraints are computed using the default scheme. This is illustrated in the `numberOfViolatedConstraints(Solution solution)` and `overallConstraintViolationDegree(Solution solution)` methods:

.. code-block:: java
  
  public static <S extends Solution<?>> int numberOfViolatedConstraints(S solution) {
    return (int) solution.attributes().getOrDefault(
        PRECOMPUTED.NUMBER_OF_VIOLATED_CONSTRAINTS,
        (int) IntStream.range(0, solution.constraints().length)
            .filter(i -> solution.constraints()[i] < 0)
            .count());
  }

  public static <S extends Solution<?>> double overallConstraintViolationDegree(S solution) {
    double overallConstraintViolation =
        (double) solution.attributes().getOrDefault(
            PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION,
            0.0);
    if (overallConstraintViolation == 0.0) {
      overallConstraintViolation = IntStream.range(0, solution.constraints().length)
          .filter(i -> solution.constraints()[i] < 0.0).mapToDouble(i -> solution.constraints()[i])
          .sum();
    }
    return overallConstraintViolation;
  }

Given two, solutions, the `OverallConstraintViolationComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/impl/OverallConstraintViolationComparator.java>`_ and `NumberOfViolatedConstraintsComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/impl/NumberOfViolatedConstraintsComparator.java>`_ classes can be used to compare then according the constraints. The implementation of the `OverallConstraintViolationComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/impl/OverallConstraintViolationComparator.java>`_ class is included next:

.. code-block:: java

  public class OverallConstraintViolationComparator<S extends Solution<?>>
    implements ConstraintViolationComparator<S> {
    /**
     * Compares two solutions. If the solutions has no constraints the method return 0
     *
     * @param solution1 Object representing the first <code>Solution</code>.
     * @param solution2 Object representing the second <code>Solution</code>.
     * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
     * respectively.
     */
    public int compare(S solution1, S solution2) {
      double violationDegreeSolution1 ;
      double violationDegreeSolution2;

      violationDegreeSolution1 = ConstraintHandling.overallConstraintViolationDegree(solution1);
      violationDegreeSolution2 = ConstraintHandling.overallConstraintViolationDegree(solution2);

      if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 < 0)) {
        if (violationDegreeSolution1 > violationDegreeSolution2) {
          return -1;
        } else if (violationDegreeSolution2 > violationDegreeSolution1) {
          return 1;
        } else {
          return 0;
        }
      } else if ((violationDegreeSolution1 == 0) && (violationDegreeSolution2 < 0)) {
        return -1;
      } else if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 == 0)) {
        return 1;
      } else {
        return 0;
      }
    }
  }

Note that the violation degree of solution is a negative number, so in the comparisons between solutions the higher the value the better.