.. _constrained-rvea:

Constraint handling in the RVEA family
======================================

RVEA and RVEA* accept problems with constraints and handle them as PlatEMO does. iRVEA requires an
unconstrained problem. The objective-space algorithms are described in :ref:`rvea-variants`.

Constraint values follow jMetal's convention: a negative value is a violation. A solution is
feasible when ``ConstraintHandling.isFeasible`` holds, and its overall constraint violation is the
sum of its negative constraint values (-1 is less violated than -5). Both honor the precomputed
attributes of ``ConstraintHandling``. Objectives and constraints must be finite.

RVEA
----

The rule is the one of PlatEMO's RVEA (``EnvironmentalSelection.m``):

* every candidate, feasible or not, takes part in the translation (the ideal point) and is
  associated with its nearest reference vector;
* each vector keeps its feasible solution with the smallest angle-penalized distance (APD) or, if it
  has no feasible solution, its solution with the smallest overall constraint violation;
* ties keep the solution that comes first.

Infeasible solutions thus survive only in the vectors that have no feasible one, and they guide the
search there. Without infeasible solutions, the selection is that of the paper.

RVEA*
-----

PlatEMO's RVEAa keeps the nondominated solutions of the joint population, comparing objectives
only, and then applies the rule above. On problems where infeasible solutions dominate all the
feasible ones (LIR-CMOP1 and LIR-CMOP2, or C3-DTLZ4), that filter removes every feasible solution,
and the run ends without any. jMetal's RVEA* therefore applies the nondominated filter to the
feasible solutions only, and passes every infeasible solution on to the rule of each vector. This
is the only difference with PlatEMO.

On seven constrained problems (LIR-CMOP1, LIR-CMOP2, LIR-CMOP5, CF4, C1-DTLZ1, C2-DTLZ2 and
C3-DTLZ4; 50000 evaluations, 11 seeds), this RVEA* found feasible solutions in every run. PlatEMO's
filter found none on LIR-CMOP1, LIR-CMOP2 and C3-DTLZ4, but it reached a better front on LIR-CMOP5.

iRVEA
-----

iRVEA (Liu et al., 2019) is not part of PlatEMO, and its regional protection, epsilon-indicator
selection and archive compare objectives only. ``IRVEABuilder`` therefore rejects problems with
constraints, and ``IRVEAEnvironmentalSelection`` rejects solutions with constraints.

Example
-------

``ConstrainedRVEAExample`` runs RVEA on Binh2 (two objectives, two constraints):

.. code-block:: java

  JMetalRandom.getInstance().setSeed(42);
  var problem = new Binh2();
  var algorithm = new RVEABuilder<>(problem, 21, 2100,
      new SBXCrossover(1.0, 20.0), new PolynomialMutation(0.5, 20.0),
      2.0, 0.1, 20).build();
  algorithm.run();
  var feasible = algorithm.result().stream()
      .filter(ConstraintHandling::isFeasible).toList();

The result of a run can contain infeasible solutions, those that occupy vectors without feasible
ones, so filter it as above when only feasible solutions matter.

Tests
-----

``RveaAllFeasibleEquivalenceTest`` compares 24-generation trajectories of the three variants (2, 3,
4 and 7 objectives, three front shapes, two seeds) with an oracle captured before constraints were
supported, ``rvea/unconstrained-438afd3.csv``: RVEA and RVEA* give the same states with no
constraints and with three satisfied ones. ``RveaConstraintSelectionTest`` checks the rule of each
vector, the translation, ties, precomputed attributes, the filter of RVEA* and iRVEA's rejection.
