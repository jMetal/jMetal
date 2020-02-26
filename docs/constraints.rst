.. _constraints:

Constraint handling
===================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2020-2-25

The constraint handling mechanism adopted in jMetal is based on the one used in NSGA-II (described in `[DPA02] <https://doi.org/10.1109/4235.996017>`_), and it involves solutions, problems, and comparators.

If we take a look to the `Solution` interface:

.. code-block:: java

    package org.uma.jmetal.solution;
    
    public interface Solution<T> extends Serializable {
      void setObjective(int index, double value) ;
      double getObjective(int index) ;
      double[] getObjectives() ;

      T getVariable(int index) ;
      List<T> getVariables() ;
      void setVariable(int index, T variable) ;

      double[] getConstraints() ;
      double getConstraint(int index) ;
      void setConstraint(int index, double value) ;

      int getNumberOfVariables() ;
      int getNumberOfObjectives() ;
      int getNumberOfConstraints() ;

      ...
    }

any solution can have a set of constraints, which are returned as an array of double values by the `getConstraints()` method. The constraints are usually assigned when a solution is evaluated by a constrained problem; if the problem has no constraints, that method will return an emtpy array.

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

      for (int i = 0; i < getNumberOfConstraints(); i++) {
        solution.setConstraint(i, constraint[i]);
      }
    }
  }

We can observe that the problem formulation includes two constraints, which are defined in the `evaluateConstraints()` method that is called after a solution is evaluated. Note that this is not mandatory, and the computation of the contraints could be included inside the `evaluate()` method.

The requirement to work with constraints is that every constraint must be expressed as an unequality of type `expression >=0.0`. This way, when `expression < 0.0` then it is considered as a constraint violation, and the overall constraint violation degree of a solution can be computed by this method:

.. code-block:: java
  
  public static <S extends Solution<?>> int numberOfViolatedConstraints(S solution) {
    int result = 0 ;
    for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
      if (solution.getConstraint(i) < 0) {
        result ++ ;
      }
    }

    return result ;
  }

The `numberOfViolatedConstraints()` belongs to the static `ConstraintHandling <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ConstraintHandling.java>`_ class, which includes also the following methods:

* `isFeasible(Solution solution)`: returns true if the solution has no constraints or the number of violated constraits is zero.

* `numberOfViolatedConstraints(Solution solution)`: returns the number of constraints such as `constraintValue < 0.0`.

* `feasibilityRatio(List<Solution> solutions)`:  computes the ratio of feasible solutions in a solution list.

When a dominance test is applied to solutions having constraints, the scheme proposed in the NSGA-II paper is adopted, which is implemented in the `OverallConstraintViolationComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/impl/OverallConstraintViolationComparator.java>`_ class: 

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