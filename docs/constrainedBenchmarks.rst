.. _constrainedBenchmarks:

Constrained benchmarks and reference fronts
===========================================

This page lists the benchmarks of constrained problems included in ``jmetal-problem`` (see
:ref:`constraints`): how many problems each one has, the paper that defines it and whether jMetal
provides reference fronts for them, which are needed to compute quality indicators such as the
inverted generational distance or the hypervolume of the normalized fronts (see
:ref:`qualityIndicators`).

The reference fronts are CSV files with one solution per row in ``resources/referenceFronts``
(``resources/referenceFrontsRSG`` for the CF problems). The ``NAME.xD.csv`` files are the fronts for
problems with ``x`` objectives. The *Reference fronts* column says how they were obtained:

* **Exact**: computed from the analytical definition of the problem (the Pareto set or the Pareto
  front is known in closed form).
* **Estimated**: the non-dominated solutions of many runs of several algorithms, or the fronts
  published by the authors of the benchmark when they were obtained that way. They approximate the
  true front and a good algorithm can improve them.
* **None**: the repository has no reference fronts for the benchmark.

The origin of the files was checked in the history of the repository (the commit that added them
and its message). When it could not be confirmed, the entry is marked **unverified** and gives the
type that the benchmark is expected to have; these files predate the detailed commit messages, and
the type should be confirmed before relying on it in a publication.

.. list-table::
   :header-rows: 1
   :widths: 16 9 14 61

   * - Benchmark
     - Problems
     - Reference
     - Reference fronts
   * - CF (Xiang et al.)
     - 16
     - [Xiang2021]_
     - CF1-16 with 3 objectives, in ``resources/referenceFrontsRSG``. Estimated (generated with the
       RSG algorithm, according to the commit that added them).
   * - C-DTLZ
     - 6
     - [Jain2014]_
     - C1-DTLZ1, C1-DTLZ3, C2-DTLZ2, ConvexC2-DTLZ2, C3-DTLZ1 and C3-DTLZ4 with 3 objectives.
       Exact: written from the definition of the problems; every point is the objective vector of a
       feasible solution.
   * - LIR-CMOP
     - 14
     - [Fan2019]_
     - LIR-CMOP1-14. Unverified.
   * - DAS-CMOP
     - 9
     - [Fan2020]_
     - DAS-CMOP1-9 with the default difficulty triplet of each problem (2 objectives for 1-6, 3
       objectives for 7-9). Exact: written from the definition of the problems.
   * - MW
     - 14
     - [Ma2019]_
     - MW1-14; MW4, MW8 and MW14 also with 3 objectives. Exact: computed as the smallest feasible
       distance along the shape of each problem (about 1500 points with two objectives and 7000
       with three). The fronts of PlatEMO are not used because some of their points are infeasible
       or dominated.
   * - CRE (constrained RE)
     - 8
     - [Tanabe2020]_
     - CRE21-25, CRE31, CRE32 and CRE51. Estimated: the non-dominated feasible solutions of many
       runs of NSGA-II, SMS-EMOA, AGE-MOEA-II, NSGA-III and MOEA/D, completed near their extremes
       by SLSQP. Every point is the objective vector of a solution that jMetal evaluates as
       feasible, and no solution of the runs dominates it; at most 1000 points with two objectives
       and 2500 with more.
   * - CEC 2021 RWMOP (RCM)
     - 50
     - [Kumar2021]_
     - RCM01-50 except RCM17, whose first objective has no lower bound in the feasible region; the
       files are named after the number of the problem (``RCM01.csv``). Estimated as the CRE fronts,
       with problem-specific searches for the problems whose equality constraints leave feasible
       regions that the algorithms do not reach (RCM22, RCM24, RCM28 and RCM30-49). The front of
       RCM30 is less certain than the rest: half of its runs reach 0.886 of the hypervolume of all
       of them. RCM11 is the same problem as CRE51, and has the same front.
   * - EBES
     - 1
     - Javadoc of the classes
     - None.
   * - Classical problems
     - 8
     - Javadoc of the classes
     - All but Viennet4. Unverified (the files date from the reorganization of the resources in 2020).
   * - Engineering problems
     - 3
     - Javadoc of the classes
     - Fronts added with the problems. Unverified.


Notes:

* The fronts of the feasible region are not available for the benchmarks marked *None*; to compute
  indicators on them, build a reference front from the non-dominated feasible solutions of several
  runs.
* ``Ebes``, the classical problems and the engineering problems do not cite a paper in a single
  place; see the Javadoc of their classes. ``Ebes`` is by G. R. Zavala, A. J. Nebro and J. J.
  Durillo.
* The reference of the RCM suite (CEC 2021) was written from memory, not from the code, and its
  bibliographic details should be checked.

References
----------

.. [Xiang2021] Y. Xiang et al., "Constrained Multiobjective Optimization: Test Problem Construction and
   Performance Evaluations", IEEE Transactions on Evolutionary Computation, 2021
   (https://doi.org/10.1109/TEVC.2020.3011829).

.. [Jain2014] H. Jain and K. Deb, "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-
   Based Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive
   Approach", IEEE Transactions on Evolutionary Computation 18(4):602-622, 2014.

.. [Fan2019] Z. Fan et al., "An improved epsilon constraint-handling method in MOEA/D for CMOPs with large
   infeasible regions", Soft Computing, 2019 (https://doi.org/10.1007/s00500-019-03794-x).

.. [Fan2020] Z. Fan, W. Li, X. Cai, H. Li, C. Wei, Q. Zhang, K. Deb and E. Goodman, "Difficulty Adjustable and
   Scalable Constrained Multiobjective Test Problem Toolkit", Evolutionary Computation
   28(3):339-378, 2020 (https://doi.org/10.1162/evco_a_00259).

.. [Ma2019] Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test Suite Construction
   and Performance Comparisons", IEEE Transactions on Evolutionary Computation 23(6):972-986, 2019
   (https://doi.org/10.1109/TEVC.2019.2896967).

.. [Tanabe2020] R. Tanabe and H. Ishibuchi, "An easy-to-use real-world multi-objective optimization problem
   suite", Applied Soft Computing 89:106078, 2020 (https://doi.org/10.1016/j.asoc.2020.106078).

.. [Kumar2021] A. Kumar et al., "A benchmark-suite of real-world constrained multi-objective optimization
   problems and some baseline results", Swarm and Evolutionary Computation, 2021.

