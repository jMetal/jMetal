.. _benchmarks:

Benchmarks and reference fronts
===============================

This page lists the benchmarks included in ``jmetal-problem``: how many problems each one has, the
paper that defines it and whether jMetal provides reference fronts for them, which are needed to
compute quality indicators such as the inverted generational distance or the hypervolume of the
normalized fronts (see :ref:`qualityIndicators`).

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
   :widths: 14 9 33 44

   * - Benchmark
     - Problems
     - Reference
     - Reference fronts
   * - ZDT
     - 6
     - E. Zitzler, K. Deb and L. Thiele, "Comparison of Multiobjective Evolutionary Algorithms:
       Empirical Results", Evolutionary Computation 8(2):173-195, 2000.
     - ZDT1-4 and ZDT6 (ZDT5 is binary and has none). Exact (unverified).
   * - DTLZ (including the *Minus* variants)
     - 7 + 4
     - K. Deb, L. Thiele, M. Laumanns and E. Zitzler, "Scalable Test Problems for Evolutionary
       Multiobjective Optimization", in Evolutionary Multiobjective Optimization, Springer, 2005.
     - DTLZ1-4 and their *Minus* variants with 2, 3, 4, 6 and 8 objectives; DTLZ5-6 with 2 and 3
       and DTLZ7 with 2, 3, 4, 6 and 8. Exact (unverified).
   * - WFG
     - 9
     - S. Huband, P. Hingston, L. Barone and L. While, "A Review of Multiobjective Test Problems and
       a Scalable Test Problem Toolkit", IEEE Transactions on Evolutionary Computation
       10(5):477-506, 2006.
     - WFG1-9 with 2 and 3 objectives. Exact (unverified).
   * - MaF
     - 15
     - R. Cheng et al., "A benchmark test suite for evolutionary many-objective optimization",
       Complex & Intelligent Systems, 2017
       (https://doi.org/10.1007/s40747-017-0039-7).
     - MaF1-15 with 3, 5, 10 and 15 objectives. Exact (unverified).
   * - LSMOP
     - 9
     - R. Cheng, Y. Jin, M. Olhofer and B. Sendhoff, "Test Problems for Large-Scale Multiobjective
       and Many-Objective Optimization", IEEE Transactions on Cybernetics 47(12):4108-4121, 2017.
     - Only LSMOP1 with 2 objectives. Unverified; the other eight have none.
   * - LZ09
     - 9
     - H. Li and Q. Zhang, "Multiobjective Optimization Problems with Complicated Pareto Sets,
       MOEA/D and NSGA-II", IEEE Transactions on Evolutionary Computation 12(2):284-302, 2009.
     - LZ09 F1-F9. Exact (unverified).
   * - UF (CEC 2009)
     - 10
     - Q. Zhang et al., "Multiobjective optimization test instances for the CEC 2009 special
       session and competition", Technical Report CES-887, University of Essex, 2008.
     - UF1-10. Exact (unverified).
   * - GLT
     - 6
     - F. Gu, H.-L. Liu and K. C. Tan, "A multiobjective evolutionary algorithm using dynamic weight
       design method", International Journal of Innovative Computing, Information and Control
       8(5B):3677-3688, 2012.
     - GLT1-6. Unverified.
   * - MOP
     - 7
     - H.-L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective Optimization Problem Into a
       Number of Simple Multiobjective Subproblems", IEEE Transactions on Evolutionary Computation
       18(3):450-455, 2014.
     - None.
   * - FDA (dynamic)
     - 5
     - M. Farina, K. Deb and P. Amato, "Dynamic multiobjective optimization problems: test cases,
       approximations, and applications", IEEE Transactions on Evolutionary Computation
       8(5):425-442, 2004.
     - None.
   * - ZCAT
     - 20
     - "Challenging test problems for multi- and many-objective optimization", Swarm and
       Evolutionary Computation, 2023
       (https://doi.org/10.1016/j.swevo.2023.101350).
     - ZCAT1-20 with 2, 3, 4 and 6 objectives. Unverified (the commit that added them does not
       say how they were obtained).
   * - CEC 2015 Big Optimization
     - 1
     - Problem of the CEC 2015 Big Data Optimization competition (``BigOpt2015``).
     - None.
   * - CF (Xiang et al.)
     - 16
     - Y. Xiang et al., "Constrained Multiobjective Optimization: Test Problem Construction and
       Performance Evaluations", IEEE Transactions on Evolutionary Computation, 2021
       (https://doi.org/10.1109/TEVC.2020.3011829).
     - CF1-16 with 3 objectives, in ``resources/referenceFrontsRSG``. Estimated (generated with the
       RSG algorithm, according to the commit that added them).
   * - C-DTLZ
     - 6
     - H. Jain and K. Deb, "An Evolutionary Many-Objective Optimization Algorithm Using
       Reference-Point-Based Nondominated Sorting Approach, Part II: Handling Constraints and
       Extending to an Adaptive Approach", IEEE Transactions on Evolutionary Computation
       18(4):602-622, 2014.
     - C1-DTLZ1, C1-DTLZ3, C2-DTLZ2, ConvexC2-DTLZ2, C3-DTLZ1 and C3-DTLZ4 with 3 objectives.
       Exact: written from the definition of the problems; every point is the objective vector of a
       feasible solution.
   * - LIR-CMOP
     - 14
     - Z. Fan et al., "An improved epsilon constraint-handling method in MOEA/D for CMOPs with
       large infeasible regions", Soft Computing, 2019
       (https://doi.org/10.1007/s00500-019-03794-x).
     - LIR-CMOP1-14. Unverified.
   * - DAS-CMOP
     - 9
     - Z. Fan, W. Li, X. Cai, H. Li, C. Wei, Q. Zhang, K. Deb and E. Goodman, "Difficulty Adjustable
       and Scalable Constrained Multiobjective Test Problem Toolkit", Evolutionary Computation
       28(3):339-378, 2020 (https://doi.org/10.1162/evco_a_00259).
     - DAS-CMOP1-9 with the default difficulty triplet of each problem (2 objectives for 1-6, 3
       objectives for 7-9). Exact: written from the definition of the problems.
   * - MW
     - 14
     - Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test Suite
       Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
       23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967).
     - MW1-14; MW4, MW8 and MW14 also with 3 objectives. Exact: computed as the smallest feasible
       distance along the shape of each problem (about 1500 points with two objectives and 7000
       with three). The fronts of PlatEMO are not used because some of their points are infeasible
       or dominated.
   * - RE
     - 16
     - R. Tanabe and H. Ishibuchi, "An easy-to-use real-world multi-objective optimization problem
       suite", Applied Soft Computing 89:106078, 2020
       (https://doi.org/10.1016/j.asoc.2020.106078).
     - RE21-25, RE31-37, RE41-42, RE61 and RE91. Estimated: the reference points of the authors
       (RE42 was replaced by the corrected ones when its constraint was fixed).
   * - CRE (constrained RE)
     - 8
     - Same paper as RE.
     - None.
   * - RWA
     - 10
     - "Engineering applications of multi-objective evolutionary algorithms: A test suite of
       box-constrained real-world problems", Engineering Applications of Artificial Intelligence,
       2023 (https://doi.org/10.1016/j.engappai.2023.106192).
     - RWA1-10, from the Pareto fronts published with the suite. Estimated (unverified).
   * - CEC 2021 RWMOP (RCM)
     - 50
     - A. Kumar et al., "A benchmark-suite of real-world constrained multi-objective optimization
       problems and some baseline results", Swarm and Evolutionary Computation, 2021 (the
       code is adapted from the CEC2021 suite of A. Kumar).
     - None.
   * - EBES
     - 1
     - Structural design problem (``Ebes``) by G. R. Zavala, A. J. Nebro and J. J. Durillo; the
       Javadoc does not cite a paper.
     - None.
   * - Classical problems
     - 5 + 8
     - Original papers of each problem; see the Javadoc of the classes. Unconstrained: Kursawe,
       Fonseca, Schaffer, Viennet2, Viennet3. Constrained: Srinivas, Tanaka, Osyczka2, Constr_Ex,
       Golinski, Water, Binh2, Viennet4.
     - All but Viennet4. Exact for the analytic ones and estimated for the rest; unverified.
   * - Engineering problems
     - 3
     - ``CarSideImpact``, ``ElectricMotor`` and ``GAA`` (see the Javadoc of the classes).
     - Fronts added with the problems. Unverified.
   * - Combinatorial
     - 2
     - Multi-objective TSP (instances in ``resources/tspInstances``) and multi-objective 0/1
       knapsack.
     - Only the TSP fronts in ``resources/referenceFrontsTSP``. Estimated (unverified).

Notes:

* The fronts can have a different number of points; compute the indicators with the normalization
  described in :ref:`qualityIndicators`.
* Some references above were written from memory of the original papers and not from the code: ZDT,
  DTLZ, WFG, LSMOP, UF, FDA and the RCM suite do not cite their paper in the Javadoc, and the
  bibliographic details should be checked.
