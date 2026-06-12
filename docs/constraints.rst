.. _constraints:

Constraint handling
===================

:Author: Antonio J. Nebro
:Version: 1.2
:Date: 2026-6-12

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
    public Srinivas() {
      int numberOfVariables = 2 ;
      numberOfObjectives(2);
      numberOfConstraints(2);
      name("Srinivas");

      List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
      List<Double> upperLimit = new ArrayList<>(numberOfVariables);

      for (int i = 0; i < numberOfVariables; i++) {
        lowerLimit.add(-20.0);
        upperLimit.add(20.0);
      }

      variableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      double[] f = new double[solution.variables().size()];

      double x1 = solution.variables().get(0);
      double x2 = solution.variables().get(1);
      f[0] = 2.0 + (x1 - 2.0) * (x1 - 2.0) + (x2 - 1.0) * (x2 - 1.0);
      f[1] = 9.0 * x1 - (x2 - 1.0) * (x2 - 1.0);

      solution.objectives()[0] = f[0];
      solution.objectives()[1] = f[1];

      evaluateConstraints(solution);
      return solution;
    }

    /** EvaluateConstraints() method */
    public void evaluateConstraints(DoubleSolution solution) {
      double[] constraint = new double[this.numberOfConstraints()];

      double x1 = solution.variables().get(0);
      double x2 = solution.variables().get(1);

      constraint[0] = 1.0 - (x1 * x1 + x2 * x2) / 225.0;
      constraint[1] = (3.0 * x2 - x1) / 10.0 - 1.0;

      IntStream.range(0, numberOfConstraints())
          .forEach(i -> solution.constraints()[i] = constraint[i]);
    }
  }

We can observe that the problem formulation includes two constraints, which are defined in the ``evaluateConstraints()`` method that is called after a solution is evaluated. According to the NSGA-II constraint handling scheme, the requirement to work with constraints is that every constraint must be expressed as an inequality of type `expression >=0.0`. This way, when `expression < 0.0` then it is considered as a constraint violation, and the overall constraint violation degree will the sum of the values of the constraints array. If that value is negative, then the solution is infeasible.

.. note:: The sign convention is a key point: a constraint value lower than 0.0 means that the constraint is violated, so the overall constraint violation degree of a solution is a negative number, and a value of 0.0 means that the solution is feasible. All the constraint-aware machinery of jMetal (comparators, rankings, archives, and constraint handling components) relies on this convention.

To facilitate the use of constraints, jMetal provides the utility `ConstraintHandling <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ConstraintHandling.java>`_ class, which provides the following static methods:

* ``isFeasible(Solution solution)``: returns true if the solution has no constraints or the number of violated constraints is zero.

* ``numberOfViolatedConstraints(Solution solution)``: returns the number of violated constraints.

* ``overallConstraintViolationDegree(Solution solution)``: returns the overall constraint violation degree of a solution.

* ``feasibilityRatio(List<Solution> solutions)``:  computes the ratio of feasible solutions in a solution list.

* ``numberOfViolatedConstraints(Solution solution, int number)``: sets the number of violated constraints of a solution.

* ``overallConstraintViolationDegree(Solution solution, double value)``: sets the overall constraint violation degree of a solution.

The last two methods are included to allow the adoption of adhoc mechanisms to compute both the number of violated constraints and the overall constraint violation degree. To cope with this possibility, the `ConstraintHandling <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ConstraintHandling.java>`_ class includes the following enum type:

.. code-block:: java

  public enum PRECOMPUTED {
    OVERALL_CONSTRAINT_VIOLATION,
    NUMBER_OF_VIOLATED_CONSTRAINTS
  }

It is assumed that a solution may have an attribute whose key value is ``OVERALL_CONSTRAINT_VIOLATION`` or ``NUMBER_OF_VIOLATED_CONSTRAINTS``. If so, the associated values are returned when querying the constraint properties of the solution, including the 0.0 degree of feasible solutions; otherwise, the constraints are computed using the default scheme. This is illustrated in the ``numberOfViolatedConstraints(Solution solution)`` and ``overallConstraintViolationDegree(Solution solution)`` methods:

.. code-block:: java

  public static <S extends Solution<?>> int numberOfViolatedConstraints(S solution) {
    return (int) solution.attributes().getOrDefault(
        PRECOMPUTED.NUMBER_OF_VIOLATED_CONSTRAINTS,
        (int) IntStream.range(0, solution.constraints().length)
            .filter(i -> solution.constraints()[i] < 0)
            .count());
  }

  public static <S extends Solution<?>> double overallConstraintViolationDegree(S solution) {
    Object precomputedValue = solution.attributes().get(PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION);
    if (precomputedValue instanceof Double overallConstraintViolation) {
      return overallConstraintViolation;
    }
    return IntStream.range(0, solution.constraints().length)
        .filter(i -> solution.constraints()[i] < 0.0)
        .mapToDouble(i -> solution.constraints()[i])
        .sum();
  }

.. note:: Problems precomputing these attributes must follow the jMetal sign convention: the stored overall constraint violation degree must be a negative number for infeasible solutions and 0.0 for feasible ones. This mechanism is intended for problems whose raw constraint values have non-standard semantics; an example is the `CF benchmark suite <https://github.com/jMetal/jMetal/tree/master/jmetal-problem/src/main/java/org/uma/jmetal/problem/multiobjective/cf>`_ (Xiang et al., IEEE TEVC 2021), where problems CF9-CF16 define pairs of band constraints that are violated only when both members of a pair are violated, so the violation degree is precomputed in the ``evaluate()`` method.

Constraint-aware comparators
----------------------------

Given two solutions, the `OverallConstraintViolationDegreeComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/constraintcomparator/impl/OverallConstraintViolationDegreeComparator.java>`_ and `NumberOfViolatedConstraintsComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/constraintcomparator/impl/NumberOfViolatedConstraintsComparator.java>`_ classes (both implementing the `ConstraintComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/constraintcomparator/ConstraintComparator.java>`_ interface) can be used to compare them according to the constraints. The implementation of the `OverallConstraintViolationDegreeComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/constraintcomparator/impl/OverallConstraintViolationDegreeComparator.java>`_ class is included next:

.. code-block:: java

  public class OverallConstraintViolationDegreeComparator<S extends Solution<?>>
      implements ConstraintComparator<S> {
    /**
     * Compares two solutions. If the solutions have no constraints the method returns 0
     *
     * @param solution1 Object representing the first <code>Solution</code>.
     * @param solution2 Object representing the second <code>Solution</code>.
     * @return -1 if the overall constraint violation degree of solution1 is higher than the one
     * of solution2, 1 in the opposite case, and 0 if they have the same value.
     */
    @Override
    public int compare(S solution1, S solution2) {
      var violationDegreeSolution1 = ConstraintHandling.overallConstraintViolationDegree(solution1);
      var violationDegreeSolution2 = ConstraintHandling.overallConstraintViolationDegree(solution2);

      if ((violationDegreeSolution1 < 0.0) && (violationDegreeSolution2 < 0.0)) {
        return Double.compare(violationDegreeSolution2, violationDegreeSolution1);
      } else if ((violationDegreeSolution1 == 0.0) && (violationDegreeSolution2 < 0.0)) {
        return -1;
      } else if ((violationDegreeSolution1 < 0.0) && (violationDegreeSolution2 == 0.0)) {
        return 1;
      } else {
        return 0;
      }
    }
  }

Note that the violation degree of a solution is a negative number.

The constrained dominance principle of NSGA-II (feasible solutions dominate infeasible ones; among infeasible solutions, the one with the lowest violation dominates) is implemented by the `DominanceWithConstraintsComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/dominanceComparator/impl/DominanceWithConstraintsComparator.java>`_ class, which combines a constraint comparator (by default, ``OverallConstraintViolationDegreeComparator``) with Pareto dominance. This comparator is the default one used by the non-dominated sorting rankings and by the `NonDominatedSolutionListArchive <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/archive/impl/NonDominatedSolutionListArchive.java>`_ class, so both are constraint-aware out of the box.

Constraint handling in component-based MOEA/D
---------------------------------------------

In decomposition-based algorithms such as MOEA/D, constraint handling is applied in the rule that decides whether a new solution replaces the current solution of a subproblem. In the component-based implementations of MOEA/D and MOEA/D-DE (sub-project ``jmetal-component``), that rule is encapsulated in the `SubproblemUpdateCriterion <https://github.com/jMetal/jMetal/blob/master/jmetal-component/src/main/java/org/uma/jmetal/component/catalogue/ea/replacement/subproblemupdate/SubproblemUpdateCriterion.java>`_ interface, used by the ``MOEADReplacement`` component:

.. code-block:: java

  public interface SubproblemUpdateCriterion<S extends Solution<?>> {
    boolean replaces(
        S newSolution,
        double newSolutionAggregationValue,
        S currentSolution,
        double currentSolutionAggregationValue);

    default void update(List<S> population, S newSolution) {
      // Stateless criteria do not need to update anything
    }
  }

The available implementations are:

* ``AggregationCriterion``: default criterion of unconstrained MOEA/D; the new solution replaces the current one if it has a better aggregation function value.
* ``FeasibilityRulesCriterion``: feasibility rules of the constrained dominance principle (K. Deb, "An efficient constraint handling method for genetic algorithms", 2000).
* ``ViolationThresholdCriterion``: adaptive violation threshold scheme (M. A. Jan and R. A. Khanum, "A study of two penalty-parameterless constraint handling techniques in the framework of MOEA/D", Applied Soft Computing, 2013).
* ``ImprovedEpsilonCriterion``: improved epsilon constraint-handling method (Z. Fan et al., "An improved epsilon constraint-handling method in MOEA/D for CMOPs with large infeasible regions", Soft Computing, 2019).

Solving a constrained problem only requires configuring the MOEA/D builders with the proper criterion:

.. code-block:: java

  EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADBuilder<>(
      problem,
      populationSize,
      crossover,
      mutation,
      weightVectorDirectory,
      sequenceGenerator,
      normalizeObjectives)
      .setTermination(termination)
      .setSubproblemUpdateCriterion(new FeasibilityRulesCriterion<>())
      .build();

Examples of use are included in the ``org.uma.jmetal.component.examples.multiobjective.moead`` package: ``MOEADWithFeasibilityRulesExample`` (Srinivas), ``MOEADWithViolationThresholdExample`` (Osyczka2), and ``MOEADDEWithImprovedEpsilonExample`` (Tanaka).

Solving constrained problems with an external archive
------------------------------------------------------

A recommended approach for solving constrained problems with three or more objectives is to combine a constraint handling criterion with an unbounded external archive that stores every evaluated solution, returning as result a diverse subset of the archive obtained by distance-based subset selection. As the default comparator of ``NonDominatedSolutionListArchive`` is ``DominanceWithConstraintsComparator``, the archive automatically keeps only feasible non-dominated solutions once feasible solutions are found, while the population is free to traverse infeasible regions (which methods such as the improved epsilon explicitly promote):

.. code-block:: java

  Archive<DoubleSolution> externalArchive =
      new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), populationSize);

  EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADDEBuilder(
      problem, populationSize, cr, f, mutation, weightVectorDirectory, sequenceGenerator, false)
      .setTermination(termination)
      .setSubproblemUpdateCriterion(new ImprovedEpsilonCriterion<>(tc))
      .setEvaluation(new SequentialEvaluationWithArchive<>(problem, externalArchive))
      .build();

  moead.run();
  List<DoubleSolution> result = externalArchive.solutions();

The ``MOEADSolvingCF4Example`` and ``MOEADDESolvingCF10Example`` classes illustrate this approach on the three-objective constrained problems CF4 and CF10 of the CF benchmark suite, whose reference fronts are available in the ``resources/referenceFrontsRSG`` directory.
