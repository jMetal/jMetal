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

The constrained dominance principle of NSGA-II (feasible solutions dominate infeasible ones; among infeasible solutions, the one with the lowest violation dominates) is implemented by the `DominanceWithConstraintsComparator <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/comparator/dominanceComparator/impl/DominanceWithConstraintsComparator.java>`_ class, which combines a constraint comparator (by default, ``OverallConstraintViolationDegreeComparator``) with Pareto dominance. Without constraints, ``DominanceWithConstraintsComparator`` is plain Pareto dominance.

Whether a component uses this comparator by default depends on the component:

* **Constraint-aware by default**: the `NonDominatedSolutionListArchive <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/archive/impl/NonDominatedSolutionListArchive.java>`_ class; the bounded archives (``CrowdingDistanceArchive``, ``AngleArchive``, ``CosineSimilarityArchive`` and any subclass of ``AbstractBoundedArchive`` built without an explicit comparator); ``StrengthRanking``; ``ExperimentalFastNonDominanceRanking``, which sorts the solutions by their overall constraint violation before the non-dominated sorting; the replacement of the component-based PAES (``PAESBuilder``); and the environmental selection of the component-based AGE-MOEA and AGE-MOEA-II (``AGEMOEABuilder``), which ranks the population with the constrained dominance and passes that ranking to its parent selection (another ranking can be given to ``AGEMOEAEnvironmentalSelection``). In them, an infeasible solution never replaces a feasible one.
* **Not constraint-aware by default**: `FastNonDominatedSortRanking <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/ranking/impl/FastNonDominatedSortRanking.java>`_, whose default constructor uses plain Pareto dominance (``DefaultDominanceComparator``), and ``MergeNonDominatedSortRanking``, which only compares objectives and accepts no comparator. Consequently, the builders that use these rankings by default, such as ``NSGAIIBuilder`` and ``SMSEMOABuilder``, **ignore the constraints** unless a constraint-aware ranking is set. The reason is efficiency: computing the constraint violation in every comparison would slow down the non-dominated sorting of problems without constraints, which are the most frequent ones.

To solve a problem with constraints with these algorithms, set a constraint-handling technique (see the next section) or a ranking with ``DominanceWithConstraintsComparator``, as in `NSGAIISolvingConstrainedProblemExample <https://github.com/jMetal/jMetal/blob/master/jmetal-component/src/main/java/org/uma/jmetal/component/examples/multiobjective/nsgaii/NSGAIISolvingConstrainedProblemExample.java>`_:

.. code-block:: java

    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
        new DominanceWithConstraintsComparator<>(
            new OverallConstraintViolationDegreeComparator<>()));

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem, populationSize, offspringPopulationSize, crossover, mutation)
        .setTermination(termination)
        .setRanking(ranking)
        .build();

The same applies to ``SMSEMOABuilder``: its replacement (``SMSEMOAReplacement``) honors the comparator of the ranking, and ranks the whole population when the solutions have constraints, so it handles them only when it is given a constraint-aware ranking.

Constraint-handling techniques
------------------------------

The package ``org.uma.jmetal.util.constrainthandling`` (sub-project ``jmetal-core``) contains constraint-handling techniques (CHTs) that can be passed to an algorithm, so that the same algorithm can handle the constraints in different ways, and that can be a configurable parameter in automatic design tools such as `Evolver <https://github.com/jMetal/Evolver>`_. A technique is a `ConstraintHandlingTechnique <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/constrainthandling/ConstraintHandlingTechnique.java>`_:

.. code-block:: java

  public interface ConstraintHandlingTechnique<S extends Solution<?>> {
    Ranking<S> ranking();         // a new ranking that sorts a population with the technique
    Comparator<S> comparator();   // for pairwise decisions
    default void update(List<S> population, double progress) {}  // once per generation
  }

There are two kinds of techniques:

* **Pairwise techniques** (``PairwiseConstraintHandlingTechnique``) look at the constraints of two solutions and, when the constraints do not decide, delegate in a comparison of their objectives that the algorithm provides: Pareto dominance in the algorithms based on dominance, or the value of the aggregation function of a subproblem in MOEA/D. They can be used by any algorithm.
* **Population techniques** need the whole population to rank it, so they can only be used by algorithms that rank their population. Their comparator, for the pairwise decisions those algorithms may also take, is the constrained-domination principle.

The techniques are the following:

.. list-table::
   :header-rows: 1
   :widths: 18 14 38 30

   * - Class
     - Kind
     - Parameters
     - Reference
   * - ``ConstrainedDominance``
     - pairwise
     - violation measure: the overall constraint violation (default) or the number of violated constraints
     - Deb et al., IEEE TEVC 6(2), 2002
   * - ``EpsilonConstrained``
     - pairwise, changes during the run
     - quantile of the violations of the first population that gives the initial epsilon; exponent of its decrease; fraction of the run from which epsilon is zero
     - Takahama and Sakai, IEEE CEC 2006
   * - ``PushPullSearch``
     - pairwise, changes during the run
     - change threshold of the ideal and nadir points and window (a fraction of the run) that end the push stage; fraction of the run Tc from which epsilon is zero; exponent; feasibility ratio alpha; reduction tau
     - Fan et al., Swarm and Evolutionary Computation 44, 2019
   * - ``StochasticRanking``
     - population
     - probability pf of comparing two solutions by their Pareto rank when one of them is infeasible
     - Runarsson and Yao, IEEE TEVC 4(3), 2000 (multi-objective form)
   * - ``SelfAdaptivePenalty``
     - population
     - none
     - Woldesenbet, Yen and Tessema, IEEE TEVC 13(3), 2009

The parameters are scale-free (quantiles, fractions of the run and ratios rather than absolute values of epsilon), so a setting can be used on problems whose constraints have different scales. The techniques that change during the run are scheduled with the progress of the run, in [0, 1], which the termination condition computes: they need a ``TerminationByEvaluations`` or a ``TerminationByComputingTime`` (any ``Termination`` that implements ``progress``), and throw an exception otherwise. A technique has state, so an instance must not be shared by several runs.

The builders of the component-based algorithms (sub-project ``jmetal-component``) accept a technique with ``setConstraintHandlingTechnique``; the classic algorithms of ``jmetal-algorithm`` do not use them:

.. list-table::
   :header-rows: 1
   :widths: 40 20 40

   * - Builders
     - Techniques
     - Where the technique is used
   * - ``NSGAIIBuilder``, ``NSGAIIDEBuilder``, ``RDSMOEABuilder``, ``NSGAIIIBuilder``, ``SMSEMOABuilder``, ``SMSEMOADEBuilder``, ``AGEMOEABuilder`` (AGE-MOEA and AGE-MOEA-II)
     - any
     - the ranking of the selection and the replacement is ``technique.ranking()``
   * - ``PAESBuilder``
     - pairwise
     - whether the mutant replaces the current solution
   * - ``SMPSOBuilder``
     - pairwise
     - whether a particle replaces its local best (which, by default, ignores the constraints)
   * - ``MOEADBuilder``, ``MOEADDEBuilder``
     - pairwise
     - as the subproblem update criterion (``ConstraintHandlingCriterion``): the technique decides by the constraints and then by the aggregation function values; with ``ConstrainedDominance`` it decides as ``FeasibilityRulesCriterion``

For example, NSGA-II with its own technique, the constrained-domination principle, on the Srinivas problem (see `NSGAIIWithConstrainedDominanceExample <https://github.com/jMetal/jMetal/blob/master/jmetal-component/src/main/java/org/uma/jmetal/component/examples/multiobjective/nsgaii/NSGAIIWithConstrainedDominanceExample.java>`_):

.. code-block:: java

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        new Srinivas(), populationSize, offspringPopulationSize, crossover, mutation)
        .setTermination(new TerminationByEvaluations(25000))
        .setConstraintHandlingTechnique(new ConstrainedDominance<>())
        .build();

Changing the technique only requires changing that line. NSGA-II with push and pull search and an external archive (see `NSGAIIWithConstraintHandlingTechniqueExample <https://github.com/jMetal/jMetal/blob/master/jmetal-component/src/main/java/org/uma/jmetal/component/examples/multiobjective/nsgaii/NSGAIIWithConstraintHandlingTechniqueExample.java>`_):

.. code-block:: java

    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem, populationSize, offspringPopulationSize, crossover, mutation)
        .setTermination(new TerminationByEvaluations(25000))
        .setConstraintHandlingTechnique(new PushPullSearch<>(0.001, 0.02, 0.8, 2.0, 0.95, 0.1))
        .setEvaluation(new SequentialEvaluationWithArchive<>(problem, archive))
        .build();

On LIR-CMOP5, with 25,000 evaluations and five runs, the median IGD+ of the feasible solutions of the archive is 0.217 with this technique and 1.229 with the constrained-domination principle; on LIR-CMOP1 it is 0.147 with push and pull search, 0.171 with ``new EpsilonConstrained<>(0.2, 5.0, 0.5)`` and 0.260 with the constrained-domination principle.

Some remarks:

* **The technique only guides the search.** The archives that keep the result of an algorithm (an external archive, the archive of PAES, the leaders of SMPSO) compare solutions with the constrained-domination principle whatever the technique, so a technique that tolerates violations does not decide what is returned. In SMPSO the technique only acts on the local bests, so its effect is smaller than in the algorithms that rank their population.
* **The final population.** ``EpsilonConstrained`` and ``PushPullSearch`` end as the constrained-domination principle, so the final population is ranked with it. ``StochasticRanking`` (with pf > 0) and ``SelfAdaptivePenalty`` can end with infeasible solutions in the population: use an external archive to get the feasible solutions found. The self-adaptive penalty, in particular, keeps the population just outside very narrow feasible regions (on LIR-CMOP1 to LIR-CMOP4, violations of 1e-4 to 1e-7 and hardly any feasible solution at the end), because a solution with a tiny violation and better objectives outranks a feasible one when few are feasible; an external archive keeps the feasible solutions it finds along the run.
* **The initial epsilon** is a quantile of the violations of the first population, so it is zero, and ``EpsilonConstrained`` is the constrained-domination principle, when that quantile of the population is feasible (as on LIR-CMOP5). ``PushPullSearch`` does not have this limitation, because it starts by ignoring the constraints.
* **The number of violated constraints** as violation measure makes two infeasible solutions that violate as many constraints be compared by their objectives, which gives little guidance towards the feasible region to an algorithm without a population such as PAES.
* **RVEA** is not included: its rule (C-RVEA) is already the constrained-domination principle within each niche, with the angle-penalized distance as the comparison of the objectives, so a pairwise technique could be used there in the future.

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

ToP: a two-phase framework
--------------------------

ToP (class ``ToP``, package ``org.uma.jmetal.algorithm.multiobjective.top``, sub-project ``jmetal-algorithm``) is an algorithm designed for constrained problems, proposed by Z.-Z. Liu and Y. Wang in "Handling Constrained Multiobjective Optimization Problems With Constraints in Both the Decision and Objective Spaces", IEEE Transactions on Evolutionary Computation, 23(5):870-884, 2019 (`DOI: 10.1109/TEVC.2019.2894743 <https://doi.org/10.1109/TEVC.2019.2894743>`_). It runs in two phases:

* **Phase 1** solves the problem as a constrained single-objective one, whose objective is the mean of the objectives, with differential evolution (DE/current-to-rand/1 and DE/rand-to-best/1/bin, followed by the polynomial mutation) and a one-to-one replacement by the feasibility rule. It lasts until more than a third of the population is feasible and the best third of the feasible solutions are close to each other in the normalized objective space (delta < 0.2), or until 90% of the evaluations have been done.
* **Phase 2** is NSGA-II with the constrained-domination principle, whose offspring are created with DE/rand/1/bin and the polynomial mutation.

The implementation follows the ToP of PlatEMO, including the details of its non-dominated sorting, crowding distance and tie-breaking; where PlatEMO and the paper differ (for example, the paper draws F and CR from pools, does not mutate the trial vectors of the first phase and uses the SBX crossover in the second one), it follows PlatEMO. The differences are listed in the documentation of the class. The algorithm is configured with ``ToPBuilder``, whose default values are those of PlatEMO (population size 100, CR = 1.0, F = 0.5, polynomial mutation with probability 1/n and distribution index 20):

.. code-block:: java

    ToP algorithm = new ToPBuilder(problem)
        .setPopulationSize(100)
        .setMaxEvaluations(100000)
        .build();

    algorithm.run();
    List<DoubleSolution> population = algorithm.result();

The result is the final population, which can contain infeasible solutions, and ``phaseTwoStart()`` returns the number of evaluations done when the second phase started. In the integration tests (``ToPIT``), the median IGD of the feasible solutions is 0.013 on MW3 (25,000 evaluations), 0.23 on LIR-CMOP2 and 0.027 on C1-DTLZ1 (100,000 evaluations); on MW3 the population collapsed into a single feasible point in 4 of 15 runs, and on MW1 none of five runs with 25,000 evaluations found a feasible solution. An example is included in `ToPRunner <https://github.com/jMetal/jMetal/blob/main/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/examples/multiobjective/top/ToPRunner.java>`_, which solves LIR-CMOP2.
