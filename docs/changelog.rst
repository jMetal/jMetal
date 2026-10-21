Changelog
=========

This page lists the changes of the jMetal project, most recent first. The README of the project
only includes the latest entries.

* [10/21/2026] The reference fronts are all in ``resources/referenceFronts``, in CSV format. The
  directory ``resources/referenceFronts``, with older ``.pf`` copies of some of them (separated by
  spaces or tabs, and in 27 cases different from their CSV counterparts), has been removed, and
  ``resources/referenceFrontsCSV`` has been renamed to ``resources/referenceFronts``. Code that
  builds paths to ``resources/referenceFrontsCSV`` must use the new name; the
  ``referenceFrontsDirectory`` property of ``jmetal.properties`` has been updated. The fronts of
  Binh2, Fonseca, Viennet2, Viennet3 and Water, which only existed as ``.pf``, have been converted to
  CSV.

* [10/21/2026] The DAS-CMOP problems (Fan et al., "Difficulty Adjustable and Scalable Constrained
  Multiobjective Test Problem Toolkit", Evolutionary Computation, 2020), ``DASCMOP1`` to
  ``DASCMOP9`` in the package ``org.uma.jmetal.problem.multiobjective.dascmop``, with the
  difficulty triplet (eta, zeta, gamma) as a constructor argument. They follow Table 2 of the
  paper; the default triplet of each problem is that of PlatEMO, whose distance function of
  DAS-CMOP9 differs from the paper's, and whose Type-II constraint with zeta = 1 has its sign
  reversed.

* [10/21/2026] The environmental selection of RVEA, RVEA* and iRVEA discards the candidates with a
  non-finite objective value instead of failing. Such values appear, for instance, in RE22, whose
  second objective (the sum of the constraint violations) is infinite when a variable is exactly at
  its lower bound of 0, as it often is with the bound repair of some operators. The angles and
  distances of the APD are not defined for these candidates, and they are worse than any finite one
  in that objective. The selection still fails if no candidate is finite.

* [10/20/2026] ``RVEABuilder`` and ``IRVEABuilder`` accept problems with constraints, which they
  rejected. RVEA handles them as C-RVEA, the technique of the paper of RVEA (Cheng et al., 2016,
  Section VII) that PlatEMO also implements: in each niche, the feasible solution with the lowest
  APD survives or, if there is none, the least violating one. Its result is the set of feasible
  non-dominated solutions of the final population. iRVEA, which PlatEMO does not include, uses a
  feasibility-first layer of jMetal that is experimental and pending validation: it is not
  published and has only been run on a few classic problems. ``RVEAStarBuilder`` still rejects
  problems with constraints,
  because C-RVEA combined with the non-dominated filter of RVEA* lets infeasible solutions remove
  every feasible one (see the `documentation
  <https://jmetal.readthedocs.io/en/latest/constrained-rvea.html>`_).

* [10/20/2026] Fixed ``LIRCMOP14``: it declared three constraints and computed the third one, the
  ring between radii 1.6 and 1.75, but did not store it, so that constraint was never violated.

* [10/20/2026] The evaluators reject a solution that a problem evaluates to NaN (an objective, a
  constraint or a precomputed overall constraint violation degree) with an
  ``InvalidConditionException`` that names the problem. A NaN has no order: Pareto dominance ignored
  a NaN objective, so such solutions were never dominated, their crowding distance was NaN, and a
  NaN constraint counted as satisfied. Algorithms kept them silently (they filled the population of
  NSGA-II, for instance) or failed later with unrelated exceptions (NSGA-III). The check is in
  ``EvaluationCheck`` and is called by ``SequentialSolutionListEvaluator``,
  ``MultiThreadedSolutionListEvaluator`` and the ``Evaluation`` components of ``jmetal-component``.
  Infinite values are still accepted.

* [10/20/2026] Each C-DTLZ problem has its own name. They inherited that of the DTLZ problem they
  extend (``C1_DTLZ1`` was named ``DTLZ1``), so jMetal's experiments, which name output directories
  and look up reference fronts by problem name, mixed them up with the DTLZ problems.

* [10/20/2026] The CF problems (Zhou, Xiang and He, 2021) default to 13 variables and 3 objectives,
  M + 10 variables as in the code of the paper (``ZXH_CF`` in PlatEMO). With the former default of 3
  variables they had no distance variables: ``CF3``, ``CF8``, ``CF9`` and ``CF14``, whose Ackley
  distance function averages over them, evaluated every solution to NaN, and these four now reject
  instances with no more variables than objectives. ``CF1`` also computed its last objective with
  the number of variables instead of the number of objectives, so it threw an
  ``ArrayIndexOutOfBoundsException`` with more variables than objectives.

* [10/20/2026] Fixed the C-DTLZ problems (Jain and Deb, 2014). Their constraints did not follow the
  definition: ``C1_DTLZ1`` left out a term and the 0.6 factor, ``C1_DTLZ3`` subtracted its radii
  once per objective, ``C2_DTLZ2`` took a maximum instead of minus a minimum (almost every point was
  feasible), and ``C3_DTLZ1`` and ``C3_DTLZ4`` accumulated wrong partial sums. ``ConvexC2_DTLZ2``
  now evaluates the objectives of convex DTLZ2 (f_i^4 for i < M and f_M^2). They all get default
  constructors with 3 objectives.

* [10/20/2026] Bounded archives (``CrowdingDistanceArchive``, ``AngleArchive``,
  ``CosineSimilarityArchive``) and the replacement of the component-based PAES honor constraints.
  They compared solutions with plain Pareto dominance, so on constrained problems an infeasible
  solution could replace a feasible one. They now use Deb's constrained-domination principle
  (``DominanceWithConstraintsComparator``), as ``NonDominatedSolutionListArchive`` already did.
  ``SMSEMOAReplacement`` ranks the whole population instead of updating it incrementally when the
  solutions have constraints, so it honors the comparator of its ranking; the default ranking of
  ``SMSEMOABuilder`` ignores constraints, so a constraint-aware ranking must be set to use it (see
  the `documentation <https://jmetal.readthedocs.io/en/latest/constraints.html>`_). Results on
  problems without constraints do not change.

* [10/15/2026] The Maven build pins ``maven-surefire-plugin`` to 3.5.3 (and upgrades
  ``maven-failsafe-plugin`` from 3.2.5 to 3.5.3). Its version was not set, so Maven used its
  default, 3.0.2, which does not discover JUnit 6 tests: modules such as ``jmetal-problem`` ran no
  tests at all.

* [10/15/2026] Fixed ``RWA5`` (Gao2020) so that it matches the reference C implementation of the RWA
  suite (https://github.com/evo-mx/MO-RWA): the C code writes 18 coefficients of the third objective
  as, e.g., ``9.26 * 10e-3`` (0.0926), and ``RWA5`` had translated them as ``9.26e-3``, ten times
  smaller. The third objective ranged in [-11420, -3530], while the reference front of the suite,
  computed with the C code, covers [-121277, -50247], so no algorithm could reach it.

* [10/15/2026] Fixed the LIR-CMOP problems 5 to 12: ``LIRCMOP5`` and ``LIRCMOP6`` added 7057
  instead of 0.7057 to their second objective, which moved their fronts (and those of
  ``LIRCMOP7`` and ``LIRCMOP8``, which extend them) far from their reference fronts;
  ``LIRCMOP7`` and ``LIRCMOP8`` declared two constraints but evaluated three, so evaluating a
  solution failed. They now have their three constraints, and ``LIRCMOP9`` to ``LIRCMOP12``, which
  extend ``LIRCMOP8``, keep their two.

* [10/13/2026] jMetal 7.8 is released.

* [10/13/2026] Reference fronts of the MaF problems with 3 objectives
  (``resources/referenceFrontsCSV/MaFxx.3D.csv``, MaF01 to MaF15), next to the existing ones for 5,
  10 and 15 objectives. They are written with PlatEMO's method for this benchmark (points spread on
  the simplex and mapped to the front of each problem) by ``scripts/maf_reference_fronts.py`` of
  Evolver, whose ``MaFReferenceFrontsIT`` checks them against the problems of jMetal.

* [10/05/2026] ``AngleDensityEstimator.value`` returns 0.0, the worst density, for a solution whose
  density has not been computed, as the crowding, kNN and shifted estimators do, instead of throwing
  an exception. A selection that compares the population by density before the estimator has
  computed it (a tournament, a ranking or a stochastic universal sampling in an algorithm such as
  RDEMOEA or SSMOEA) failed with ``The parameter 'object' is null``, so the estimator could not
  replace the others.

* [10/05/2026] Fixed the multi-objective TSP instances ``KroBC100TSP`` and ``KroBD100TSP``: they read
  ``kroAC100.tsp`` and ``kroAD100.tsp``, files that do not exist, so they could not be created; they
  now read ``kroC100.tsp`` and ``kroD100.tsp``.

* [10/05/2026] jMetal 7.7 is released.

* [10/05/2026] ``JMetalException(String, Exception)`` and ``JMetalException(Exception)`` keep their
  message and their cause. They used to only log the error, so the exception they created had
  neither, and whoever caught it got ``null`` instead of what went wrong; they no longer log it.

* [10/02/2026] ``Spread`` and ``GeneralizedSpread`` no longer sort the arrays they receive: the front, and
  in ``Spread`` the reference front, are sorted in local copies. The reference front is shared by all the
  calls of an indicator instance, so sorting it in place from several threads at once (e.g., when computing
  the indicators of independent runs in parallel) corrupted it and made ``Arrays.sort`` fail with
  "Comparison method violates its general contract!"; the fronts of the callers also keep their order now.

* [09/30/2026] Fixed the decision space of ``MaF08``: the variables were bounded to ``[0, 1]``, which
  contains only a fifth of the polygon that is its Pareto set, so most of its Pareto front could not be
  reached; the bounds are now ``[-10000, 10000]``, as in the definition of the MaF test suite (Cheng et al.,
  2017). The results of ``MaF08`` obtained with earlier versions are not comparable with those of this one.

* [09/30/2026] ``DifferentialEvolutionCrossover.getVariantFromString`` recognizes ``RAND_2_EXP``,
  which was in the ``DE_VARIANT`` enum but raised an exception when given by name.

* [09/30/2026] jMetal 7.6 is released.

* [09/30/2026] jMetal 7.6 is the last version in which ``jmetal-auto`` is updated. This sub-project is
  frozen from now on, as its functionality is covered by the `Evolver <https://github.com/jMetal/Evolver>`_ project, which is under active
  development.

* [09/30/2026] Added ``AutoRVEA`` to ``jmetal-auto``, an auto-configurable version of RVEA that
  covers RVEA, RVEA* and iRVEA: the variant is the ``replacement`` component of the parameter
  space (``rvea``, ``rveaStar``, ``iRVEA``), together with ``alpha``, ``fr`` and the crossover,
  mutation and selection parameters. The population size is the number of reference vectors, which
  are generated by default from the number of objectives or read from a file of weight vectors
  (``--referenceVectorsFile``). A class to generate the irace parameter file and an example are
  included. The iRVEA late-stage fraction and epsilon-indicator kappa, previously constants, can now
  be passed to ``IRVEAEnvironmentalSelection``.

* [09/29/2026] ``Check.that`` accepts a message supplier, so the messages of the checks in repairs,
  comparators, operators, selections, replacements, variations and PSO components are only built
  when a check fails (contributed by @tachsin).

* [09/28/2026] Fixed CMA-ES (contributed by @tachsin): the inverse square root of the covariance
  matrix was computed with the eigenvalues instead of their square roots for part of the entries, so
  the step size diverged (to NaN and an ``ArrayIndexOutOfBoundsException`` in long runs); the
  ``chi_n`` constant used integer divisions; and ``CMAESUtils.tql2`` now fails with a clear message
  when the matrix contains NaN. CMA-ES, ``CoralReefsOptimization``, ``IntegerPermutationSolution``
  and ``ScrambleMutation`` now draw their random numbers from ``JMetalRandom`` instead of
  clock-seeded generators, so runs follow the configured seed. Also fixed a crash in
  ``CoralReefsOptimization`` when a single broadcast spawner was left.

* [09/26/2026] Fixed ``DTLZ1Minus``-``DTLZ4Minus``: negating the objectives alone (the previous
  implementation) makes solutions with a larger distance-function value ``g(x_m)`` Pareto-dominate
  solutions with ``g(x_m) = 0``, moving the true optimum to the decision-space boundary instead of
  ``x_m = 0.5`` as in plain DTLZ. These problems now apply the ideal/nadir rescaling of Eq. (9) in
  `Ishibuchi, Setoguchi, Masuda, Nojima, "Performance of Decomposition-Based Many-Objective
  Algorithms Strongly Depends on Pareto Front Shapes", IEEE Transactions on Evolutionary
  Computation 21(2), 169-190, 2017 <https://doi.org/10.1109/TEVC.2016.2587749>`_, which keeps
  ``g(x_m) = 0`` Pareto-optimal. Also added the reference fronts for these four problems (2, 3, 4,
  6 and 8 objectives), derived from jMetal's existing DTLZ1-4 reference fronts since the corrected
  front is exactly ``1 - z`` for every point ``z`` of the corresponding plain DTLZ front.

* [09/09/2026] Added RVEA* and iRVEA, two variants of RVEA for problems with irregular Pareto
  fronts, contributed by Nicolás Rodríguez Uribe (@NicolasRodriguezUribe). RVEA* implements the
  reference vector regeneration strategy from `Cheng, Jin, Olhofer, Sendhoff, "A Reference Vector
  Guided Evolutionary Algorithm for Many-objective Optimization", IEEE Transactions on
  Evolutionary Computation 20(5), 773-791, 2016 <https://doi.org/10.1109/TEVC.2016.2519378>`_
  (Section VI); iRVEA implements `Liu, Jin, Heiderich, Rodemann, "Adaptation of Reference Vectors
  for Evolutionary Many-objective Optimization of Problems with Irregular Pareto Fronts", IEEE
  Congress on Evolutionary Computation (CEC), 2019 <https://doi.org/10.1109/CEC.2019.8790214>`_.
  See ``docs/rvea-variants.rst`` for implementation notes.

* [09/09/2026] Fixed ``MultiObjectiveKnapsack.evaluate()`` so that constraint values are reset
  before being recomputed: re-evaluating the same solution instance previously accumulated its
  constraint violation degree across calls. Also added validation for empty objective/constraint
  matrices and for a mismatch between the number of capacities and the number of constraints.

* [07/28/2026] Fixed ``WFGHypervolume`` so that it can be used concurrently from several threads,
  and added ``IterativeSelection`` to the selection catalogue of the component-based algorithms.

* [07/15/2026] Corrected the reference front of RE42 to match its fixed constraint.

* [07/13/2026] Fixed the constraint sign convention of the CRE benchmark suite (CRE21-25, CRE31,
  CRE32, CRE51): constraint values were clamped so that every solution appeared feasible to
  ``ConstraintHandling`` and the constraint comparators, regardless of the actual degree of
  constraint violation. Same class of bug already fixed for the CF suite (see the 06/12/2026
  entry below), missed in that pass.

* [07/13/2026] jMetal 7.5 is released.

* [07/10/2026] Added the full 50-problem CEC2021 real-world constrained multi-objective optimization benchmark suite (RWMOP, RCM01-RCM50), fixing several correctness bugs (wrong bounds, inverted constraint signs, mistranscribed formulas) present in the 3 problems already in the codebase. Reference: Kumar, Wu, Ali, Luo, Mallipeddi, Suganthan, Das, "A Benchmark-Suite of Real-World Constrained Multi-Objective Optimization Problems and some Baseline Results", Swarm and Evolutionary Computation, 67, 100961 (2021).

* [07/10/2026] Added the SDX (Synthetic Differences Crossover) operator for real-coded problems, contributed by Alejandro Santiago (`A synthetic differences crossover operator for real-coded evolutionary algorithms, Evolutionary Intelligence 19(4), 110, 2026 <https://doi.org/10.1007/s12065-026-01220-4>`_).

* [06/25/2026] Added Python scripts for Pareto front visualisation (``scripts/plot_front.py`` and ``scripts/plot_front_interactive.py``).

* [06/12/2026] Added constraint handling components (feasibility rules, adaptive violation threshold, and improved epsilon) for the component-based MOEA/D and MOEA/D-DE algorithms, with examples combining them with unbounded external archives to solve constrained problems with three or more objectives. The `constraint handling documentation <https://jmetal.readthedocs.io/en/latest/constraints.html>`_ has been updated.

* [06/12/2026] Fixed the constraint sign conventions of the CF benchmark suite and the handling of precomputed constraint violation degrees in the ``ConstraintHandling`` utility class.

* [06/12/2026] jMetal 7.4 is released.

* [05/25/2026] jMetal 7.3 is released.

* [04/28/2026] jMetal 7.2 is released.

* [03/20/2026] jMetal 7.1 is released.

* [03/18/2026] Add RVEA (contributed by @NicolasRodriguezUribe)

* [03/02/2026] jMetal 7.0 is released.

* [03/01/2026] Add new solution creations stragegies: SobolSolutionsCreation and CauchySolutionsCreation.

* [02/23/2026] Refactored `Check` utility: added parameter-name-aware overloads and introduced several specific exceptions for clearer errors.

* [02/22/2026] All the unit and integration tests have been updated to jUnit6.

* [02/02/2026] Added the AverageHausdorffDistance quality indicator

* [12/18/2025] jMetal 6.10 is released.

* [12/18/2025] Added new selection (TruncationSelection, RankingSelection, StochasticUniversalSampling, BoltzmannSelection), replacement (RandomReplacement, TournamentReplacement), and solutions creation (OppositionBasedSolutionsCreation) components.

* [07/09/2025] jMetal 6.9 is released.

* [07/07/2025] jMetal 6.8 is released.

* [07/07/2025] Added new crossover (BLX-Alpha-Beta, Laplace, Arithmetic, Fuzzy recombination, UNDX) and mutation (Power Law, Levy Flight, Simple Random) operators for continuous problems.

* [06/18/2025] A new folder *resources/referenceFrontsRSG* have been added. It contains Pareto front approximations produced with the `Reference Set Generator (DOI: https://doi.org/10.3390/math13101626) <https://doi.org/10.3390/math13101626>`_  method. Contribution of Oliver Schütze.

* [05/09/2025] jMetal 6.7 is released.

* [05/09/2025] The jMetal project adopts Java 21.

* [01/17/2025] Added a variant of SMS-EMOA using differential evolution.

* [11/30/2024] Added single and multi-objective versions of the Knapsack problem.

* [11/22/2024] Added new crossover (Cycle, EdgeRecombination, ODX, Population-Based) and mutation (Displacement, Insert, Inversion, Scamble, SimpleInversion) operators for permutations. Contribution of Nicolas R. Uribe (@NicolasRodriguezUribe)

* [06/27/2024] The jMetal project adopts Java 19.

* [11/30/2023] The Zapotecas-Coello-Aguirre-benchmark (ZCAT) (https://doi.org/10.1016/j.swevo.2023.101350) is included in jMetal.

* [09/25/2023] The jMetal project adopts Java 17.

* [09/25/2023] Added a variant of NSGA-II using differential evolution.

* [07/19/2023] jMetal 6.1 is released.

* [07/04/2023]. Added the RWA benchmark, described in "Engineering applications of multi-objective evolutionary algorithms: A test suite of box-constrained real-world problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192

* [12/13/2022] jMetal 6.0 is released.

* [9/12/2022] The master branch has been renamed main.

* [7/5/2022] The jMetal project adopts Java 14.

* [5/23/2022] Algorithm AGE-MOEA-II. Contribution of Annibale Panichella (@apanichella).

* [5/9/2022] Algorithm `AGE-MOEA <https://dl.acm.org/doi/10.1145/3321707.3321839>`_. Contribution of Annibale Panichella (@apanichella).

* [3/28/2022] The CF benchmark of constrained multi-objective problems, defined in `Constrained Multiobjective Optimization: Test Problem Construction and Performance Evaluations <https://doi.org/10.1109/TEVC.2020.3011829>`_, has been included.

* [3/28/2022] The constraint handling code has been refactorized and the `documentation <https://jmetal.readthedocs.io/en/latest/constraints.html>`_ has been updated.
