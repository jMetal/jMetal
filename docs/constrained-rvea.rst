.. _constrained-rvea:

Constraint handling in the RVEA family
======================================

The three variants of the RVEA family treat constraints differently:

* **RVEA** handles them as **C-RVEA**, the constraint-handling technique that
  Cheng et al. propose in the paper of RVEA (Section VII, Algorithm 5), which is
  also the one of the RVEA of PlatEMO.
* **RVEA*** does not handle them: ``RVEAStarBuilder`` rejects problems with
  constraints.
* **iRVEA** uses a feasibility-first layer of jMetal that is **pending
  validation**: it is not published, and PlatEMO has no iRVEA to compare with
  (see :ref:`constrained-irvea`).

Without constraints, or when every candidate is feasible, the three variants
behave as described in :ref:`rvea-variants`.

Constraint violation
--------------------

A negative constraint value represents a violation; zero and positive values
satisfy a constraint. ``ConstraintHandling.isFeasible`` tests whether
``numberOfViolatedConstraints`` is zero, and the overall constraint violation is
the sum of the negative constraint values, so -1 is better than -5. This is
equation (13) of the paper of RVEA with the opposite sign. Both functions honor
their precomputed attributes; evaluators that use these caches must keep them
consistent when reevaluating. Constraints and objectives must be finite.

RVEA: C-RVEA
------------

``RVEAEnvironmentalSelection`` associates every candidate with its nearest
reference vector and, in each niche:

* if the niche has feasible candidates, the one with the lowest APD survives;
* otherwise, the one with the lowest overall constraint violation survives.

The translation of the objectives and the association to the vectors use every
candidate, feasible or not. Ties keep the first candidate, and empty niches
contribute no survivor. Hence the population has at most N solutions, and
during the search it mixes feasible solutions with the infeasible ones that keep
the niches without feasible solutions.

The result of the algorithm is not the final population but its non-dominated
solutions according to the constrained-domination principle, computed with a
``NonDominatedSolutionListArchive``:

* the feasible non-dominated solutions, which is what PlatEMO returns as the best
  solutions of a population;
* if no solution is feasible, the least violating ones (PlatEMO returns no
  solution in this case).

The result is usually much smaller than N: in the integration tests below, it
has from 13 to 24 solutions out of 100. To collect more solutions, evaluate the
offspring with an external archive, as explained in :ref:`constraints`.

RVEA*: no constraint handling
-----------------------------

The paper of RVEA describes RVEA* (Section VI) and C-RVEA (Section VII)
separately and does not define a constrained RVEA*. The RVEA* of jMetal, as the
RVEAa of PlatEMO, keeps the non-dominated candidates before the APD selection.
Combining that filter with C-RVEA, as PlatEMO does, lets infeasible solutions
with better objectives remove every feasible one: on Golinski, Tanaka and
Osyczka2 no feasible solution survived, and on Tanaka the population collapsed
to a single infeasible solution. ``RVEAStarBuilder`` therefore rejects problems
with constraints.

.. _constrained-irvea:

iRVEA: feasibility-first layer (pending validation)
---------------------------------------------------

.. warning::

  This layer is an experimental extension of jMetal, pending validation. Neither
  the paper of iRVEA nor any other publication describes it, and PlatEMO has no
  iRVEA to compare with. It has only been run on a few classic problems
  (Srinivas, ConstrEx, Golinski, Tanaka and Osyczka2), where it returned feasible
  fronts; it has not been studied on benchmarks such as C-DTLZ, LIR-CMOP or MW,
  nor compared with other constraint-handling techniques. It may change or be
  removed. Results of iRVEA on problems with constraints should state that they
  use this layer.

  Two known weaknesses are worth checking in such a study. Once there are N
  feasible candidates, no infeasible one survives, even if the feasible ones
  occupy a few niches, which may hinder problems with disconnected feasible
  regions. And the infeasible survivors are the least violating candidates
  overall, regardless of their niches, so they may concentrate in one region.

``IRVEAEnvironmentalSelection`` partitions the candidates before any selection
or state update. If every candidate is feasible, the original selection runs on
them. Otherwise, it runs on the feasible candidates (vector replacement, regional
protection, Pareto/strengthened dominance, epsilon selection and archive
updates), and infeasible candidates are appended **after** it; they never enter
its geometry or archive:

* Let ``F`` be the number of feasible candidates and ``Q`` the number of
  survivors of the selection on them.
* When ``F < N``, count the niches of both vector sets occupied by **all** the
  candidates, before the vectors change and without random draws, and cap the
  count at N to obtain ``C``. When ``F >= N``, ``C`` is 0.
* Append at most ``max(0, C - max(F, Q))`` infeasible candidates, ordered by
  their overall constraint violation with a stable sort.

Counting F as well as Q prevents infeasible candidates from replacing feasible
ones that the selection discarded. If no candidate is feasible, the generation
index advances but the vectors do not change, and copies of the archive survive,
followed by the least violating candidates.

``constraintStatistics()`` exposes cumulative counters of this layer: candidate
visits (which include revisited parents and are **not** evaluation counts),
feasible and infeasible visits, selections with infeasible candidates,
comparisons of violations, and appended infeasible survivors.

API and example
---------------

``RVEABuilder`` and ``IRVEABuilder`` accept problems with and without
constraints; ``RVEAStarBuilder`` only accepts problems without them. For
example:

.. code-block:: java

  JMetalRandom.getInstance().setSeed(42);
  var problem = new Binh2(); // Two objectives and two constraints.
  var algorithm = new RVEABuilder<>(problem, 21, 2100,
      new SBXCrossover(1.0, 20.0), new PolynomialMutation(0.5, 20.0),
      2.0, 0.1, 20).build();
  algorithm.run();
  var result = algorithm.result(); // The feasible non-dominated solutions.

``ConstrainedRVEAExample`` prints this small run and writes no result files. Set
the seed before construction, particularly for the initial adaptive vector set of
iRVEA; use separate JVMs to isolate concurrent runs with the default shared RNG.

The default budget is a threshold checked between complete batches: the initial
population counts, and the last batch may overshoot. Applications requiring an
exact evaluation budget must shorten their final batch explicitly.

Tests
-----

* ``RveaNicheConstraintSelectionTest`` checks the per-niche rule of C-RVEA,
  including the cases where it differs from a global feasibility-first
  selection.
* ``ConstrainedRVEABuilderTest`` checks that RVEA returns the feasible
  non-dominated solutions of a population that keeps infeasible ones, and that
  ``RVEAStarBuilder`` rejects problems with constraints.
* ``ConstrainedRVEABuilderIT`` (an integration test) runs RVEA on Srinivas,
  ConstrEx, Golinski, Tanaka and Osyczka2, and checks that the result is feasible
  and that its hypervolume, relative to that of the reference front, reaches a
  threshold.
* ``RveaFeasibleEquivalenceTest`` evolves 24 generations of deterministic
  candidates for the three variants, 2/3/4/7 objectives and three objective
  geometries, and checks that candidates with three satisfied constraints produce
  the same states (survivors, vectors and iRVEA archive) as the same candidates
  without constraints. For iRVEA, it also checks that an infeasible ideal point
  added in every generation leaves the feasible states unchanged. Both runs of
  each comparison are computed in the same JVM, so the test does not depend on
  platform-specific floating-point results.
* ``RveaConstraintSelectionTest`` checks the feasibility-first layer of iRVEA.

Reference
---------

R. Cheng, Y. Jin, M. Olhofer and B. Sendhoff, "A Reference Vector Guided
Evolutionary Algorithm for Many-Objective Optimization", *IEEE Transactions on
Evolutionary Computation*, 20(5):773-791, 2016, doi:10.1109/TEVC.2016.2519378.
