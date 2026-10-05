.. _constrained-rvea:

Constraint support for the RVEA family
====================================

jMetal's constrained-problem support for RVEA, RVEA*, and iRVEA uses a shared
feasibility-first layer. This is an implementation extension, not a claim that
the original publications proposed this constraint-handling mechanism. The
objective-space algorithms are described in :ref:`rvea-variants`.

Canonical jMetal semantics
-------------------------

A negative constraint value represents a violation; zero and positive values
satisfy a constraint. ``ConstraintHandling.isFeasible`` tests whether
``numberOfViolatedConstraints`` is zero. Overall constraint violation is the
sum of negative constraint values: -1 is better than -5. Positive slack cannot
cancel a violation. Both canonical functions honor their precomputed attributes;
evaluators that use these caches must keep them consistent when reevaluating.
Constraints and objectives must be finite.

``OverallConstraintViolationDegreeComparator`` orders infeasible fallback
solutions. A stable sort retains candidate encounter order for equal violations,
without objective tie-breakers or random draws. Replacement supplies parents
before offspring, so equal violations retain that order.

Selection and variable population size
-------------------------------------

``RVEAEnvironmentalSelection.execute`` partitions candidates before invoking any
variant-specific selection or state update. If every candidate is feasible, it
passes the original list through the original select/adapt/afterSelection/finish
pipeline in its original order. No constraint value changes APD or any objective.

For mixed populations, that same pipeline operates on the feasible subsequence.
This includes RVEA* regeneration and final truncation, and iRVEA vector replacement,
regional protection, Pareto/strengthened dominance, epsilon selection and archive
updates. Infeasible solutions are appended **after** the pipeline; they never
enter its geometry or archives.

The extension does not fill every run back to the nominal vector count. Its
explicit fallback rule is:

* Let ``F`` be the number of feasible input candidates and ``Q`` the number of
  survivors returned by the original feasible pipeline.
* Let ``U`` be the variant's current upper bound: N for RVEA and iRVEA; 2N for
  RVEA* until final truncation, then N.
* When ``F < U``, count occupied niches of the translated **joint input** against
  the current selection vectors, before vector updates. Use predefined vectors
  for RVEA and both sets for RVEA*/iRVEA. Cap this count at U to obtain ``C``.
  This read-only calculation uses the existing geometry and no RNG draws.
* Append at most ``max(0, C - max(F, Q))`` infeasible candidates in violation order.
  When ``F >= U``, append none.

Counting F as well as Q prevents infeasible fallback from replacing feasible
candidates that the original algorithm discarded. Empty niches and feasible
selection shrinkage remain unfilled. The joint-input occupancy count bounds
fallback cardinality only: infeasible objectives do not rank fallback solutions
or update the ideal point, reference vectors, or archive used for feasible selection.
This occupancy-based fallback is an explicit jMetal constraint-support choice,
not part of the unconstrained publications.

If no current input is feasible, the generation index advances once, but no
objective-space update runs. Vectors remain unchanged. iRVEA reuses copies from
its feasible archive if available, without updating it; the other variants have
no historical survivor archive. Any remaining occupied capacity uses violation
fallback. With an initially empty archive and identical infeasible objectives,
one occupied niche retains the least-violating candidate, rather than N copies.

The extension does not guarantee discovery of a feasible solution for an
arbitrary constrained problem. Constraint support adds no repair, penalty,
violation objective, custom mating rule, or convergence claim.

API and example
---------------

All three builders automatically accept constrained and unconstrained problems;
existing constructors and setters remain source-compatible. For example:

.. code-block:: java

  JMetalRandom.getInstance().setSeed(42);
  var problem = new Binh2(); // Two objectives and two constraints.
  var algorithm = new RVEABuilder<>(problem, 21, 2100,
      new SBXCrossover(1.0, 20.0), new PolynomialMutation(0.5, 20.0),
      2.0, 0.1, 20).build();
  algorithm.run();
  var feasible = algorithm.result().stream()
      .filter(ConstraintHandling::isFeasible).toList();

``ConstrainedRVEAExample`` prints this small run and writes no result files.
``RVEAStarBuilder`` and ``IRVEABuilder`` use the same automatic policy. Set the
seed before construction, particularly for the initial adaptive vector set;
use separate JVMs to isolate concurrent runs with the default shared RNG.

``constraintStatistics()`` on the environmental selector exposes cumulative
candidate visits, feasible/infeasible visits, selections affected by infeasible
input, fallback comparator calls, and appended infeasible survivors. Candidate
visits include revisited parents and are **not** objective-evaluation counts.

The upstream default budget remains a threshold checked between complete batches:
initialization counts, and the last batch may overshoot. Constraint support does
not change this behavior. Applications requiring an exact evaluation budget must
explicitly shorten their final batch and maintain the configured generation horizon.

Regression evidence
-------------------

The frozen ``rvea/unconstrained-438afd3.csv`` test oracle was captured from upstream
commit ``438afd302d951f3f5e2c645ed5a688e4731682ea`` before modifying selection. It
contains 72 trajectories of 24 generations across all three variants, 2/3/4/7
objectives, three objective geometries, and two seeds. State signatures include
survivor identities, counts and objective bits; predefined and adaptive vectors;
the iRVEA archive; and the final RNG state probe. Tests compare both unconstrained
inputs and inputs exposing three satisfied constraints to this fixed oracle.
Additional mixed-input tests inject an infeasible objective-space ideal and
check the same feasible geometry and archive signatures across all generations.
The oracle is never regenerated by a normal test run.
