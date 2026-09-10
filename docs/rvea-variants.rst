.. _rvea-variants:

RVEA, RVEA*, and iRVEA
=======================

:Author: Nicolás Rodríguez Uribe
:Version: 1.0
:Date: 2026-09-09

RVEA in a nutshell
--------------------

This section is a short, self-contained tutorial on the base RVEA algorithm
(`Cheng et al., TEVC 2016 <https://doi.org/10.1109/TEVC.2016.2519378>`_) for
readers who have not met it before. RVEA* and iRVEA (described later in this
document) only add mechanisms on top of what follows.

Why reference vectors
^^^^^^^^^^^^^^^^^^^^^^^

RVEA is a *decomposition-based* algorithm: instead of ranking solutions by
Pareto dominance, which quickly becomes uninformative once the number of
objectives grows past three or four (almost every solution in a small
population is mutually nondominated), it breaks the objective space into a
handful of angular subspaces, one per **reference vector**, and picks a single
survivor per subspace. A reference vector is just a unit vector radiating out
from the ideal point. jMetal's default is to generate a uniformly spread set
of them with the simplex-lattice construction already used by NSGA-III
(``ReferencePointGenerator``), but that construction can only produce the
combinatorial counts ``C(H + M - 1, M - 1)`` dictated by its divisions
parameter ``H``. Every RVEA builder also accepts a caller-supplied
``List<double[]>`` of vectors instead, exactly as MOEA/D does: any set of
uniformly distributed weight vectors works, which lets the population size be
whatever the vector set's size is, rather than the nearest simplex-lattice
count. jMetal ships several precomputed sets under
``resources/weightVectorFiles/moead/`` (e.g. ``W3D_100.dat``, one vector per
line); MOEA/D locates these by their ``W<M>D_<N>.dat`` naming convention, and
while RVEA has no such convenience wrapper, the same file can be loaded with
``VectorFileUtils.readVectors(...)`` and passed straight in as the
``List<double[]>``. These particular files were generated with the Riesz
s-Energy method of `Blank, Deb, Dhebar, Bandaru, and Seada, TEVC 2021
<https://doi.org/10.1109/TEVC.2020.2992387>`_, which — unlike the Das-Dennis
simplex lattice — can target an arbitrary number of well-spaced points.

Every generation, RVEA does four things:

#. **Translate.** Estimate the ideal point as the componentwise minimum of the
   current candidate population and subtract it from every objective vector,
   so the ideal point sits at the origin.
#. **Associate.** Assign each translated solution to the reference vector it
   is closest to in angle. This partitions the population into as many
   niches as there are reference vectors (some niches may end up empty).
#. **Score.** Inside each niche, rank the candidates with the Angle-Penalized
   Distance (APD, see below) instead of raw distance to the ideal point.
#. **Select.** Keep only the best-scoring candidate of each *occupied* niche.
   Since empty niches contribute nothing, the surviving population can be
   smaller than the number of reference vectors.

Angle-Penalized Distance (APD)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Picking the single closest-to-ideal solution in every niche would converge
fast but leave the niches uneven, because two very similar, tightly-clustered
solutions can both be closer to the ideal point than a lone solution
elsewhere in the niche. RVEA countered this with a scalarizing function that
penalizes a candidate in proportion to how far it drifts from the *centre* of
its own niche:

.. code-block:: text

  APD(f, v, t) = ||f|| * (1 + M * (t / T)^alpha * angle(f, v) / gamma(v))

where, for a translated objective vector ``f`` associated with reference
vector ``v``:

.. list-table:: APD symbols
   :header-rows: 1
   :widths: 18 82

   * - Symbol
     - Meaning
   * - ``||f||``
     - Distance from the (translated) solution to the ideal point: the
       **convergence** term. This is the whole score early in the run.
   * - ``angle(f, v)``
     - Angle between the solution and its reference vector: how far off-centre
       it sits inside its niche. This is the **diversity** term.
   * - ``gamma(v)``
     - The angle from ``v`` to its closest neighboring reference vector. It
       rescales the angular penalty to the local density of vectors, so
       tightly packed and sparsely packed regions of the objective space are
       penalized on a comparable scale.
   * - ``M``
     - Number of objectives.
   * - ``t``, ``T``
     - Current generation index and the generation horizon (see the
       *Configuration* section below for how jMetal derives ``T`` from
       the evaluation budget).
   * - ``alpha``
     - User parameter controlling how quickly the diversity term is phased in.

The ``(t / T)^alpha`` factor is the crux of the design: at generation zero it
is ``0``, so ``APD`` reduces to plain distance to the ideal point and the
search behaves like a greedy convergence-only method; as ``t`` approaches
``T`` the factor tends to ``1`` and off-centre candidates are pushed down the
ranking even if they are nominally closer to the ideal point, which spreads
the final population evenly along the front. ``alpha`` controls how fast that
transition happens (``2.0`` is the value used throughout this document's
examples).

A small worked example
^^^^^^^^^^^^^^^^^^^^^^^^

Take two objectives and three reference vectors at 0°, 45°, and 90° from the
first-objective axis, so ``gamma`` is 45° for all of them. Consider two
translated candidates that both associate with the 45° vector:

.. list-table:: Two candidates competing for the same niche
   :header-rows: 1
   :widths: 15 20 20 20 25

   * - Candidate
     - Objectives ``f``
     - ``||f||``
     - Angle to niche (``theta``)
     - Reads as
   * - P
     - (1, 1.73)
     - 2.00
     - 15°
     - closer to ideal, off-centre
   * - Q
     - (2, 2)
     - 2.83
     - 0°
     - farther from ideal, perfectly centred

With ``M=2`` and ``alpha=2.0``, ``APD(P, t) = 2 + 1.33 * (t/T)^2`` while
``APD(Q, t) = 2.83`` stays constant (its angle penalty is always zero).
At ``t=0`` both formulas reduce to plain distance, so P wins (``2.00 <
2.83``): early on, being closer to the front simply matters more than being
centred. Solving for the crossover point shows Q overtakes P once
``t/T ≈ 0.79``, i.e. only in roughly the last fifth of the run does
alignment with the reference vector start to outweigh raw convergence. This
is exactly the trade-off exercised by the
``givenCompetingConvergenceAndAngle_whenTimePasses_thenWinnerChanges`` test
in ``RVEASelectionBehaviorTest``.

Keeping the reference vectors useful
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

A fixed, uniformly spread set of reference vectors only produces a uniformly
spread population when every objective is scaled the same way. RVEA
periodically (every ``ceil(fr * T)`` generations, ``fr`` being a user
parameter) rescales each predefined vector by the objective ranges observed
in the current survivors and renormalizes it, so badly scaled objectives
still get evenly covered. The *Implemented behavior* section below states
the exact formulas; RVEA* and iRVEA build directly on this
translate/associate/score/select/adapt loop, adding their own strategies for
what happens to reference vectors that end up covering no solutions at all.

Repository review
------------------

This change extends the RVEA builder, replacement, and environmental selection
in ``jmetal-component`` with RVEA* and iRVEA. It targets ``develop`` at revision
``d22a7e94f`` and reuses the existing simplex generator. Other algorithms and the
module dependency graph are unchanged.

The existing selection has several behaviors relevant to this work:

* Empty reference-vector niches borrow the best remaining APD candidate. This
  enforces a fixed population size, whereas the published selection leaves
  empty niches empty. It also lets an earlier empty niche take a candidate
  associated with a later occupied niche.
* Replacement passes the current parent count to selection. That count cannot
  represent the configured number of vectors when survivor populations vary.
* Zero reference vectors are divided by their norm without validation, and
  nonfinite parameters/objectives are not consistently rejected.
* The builder's initial-population closure reads mutable builder state, so
  changing the builder can affect an algorithm already built from it.

This implementation uses occupied niches, a separate nominal population size,
validated directions and parameters, and captured builder components.

Literature and algorithm identity
----------------------------------

.. list-table:: Literature and algorithm identity
   :header-rows: 1
   :widths: 30 70

   * - Work
     - Contribution relevant to this implementation
   * - `Cheng et al., TEVC 2016 <https://doi.org/10.1109/TEVC.2016.2519378>`_
       (`author manuscript, RVEA <https://www.honda-ri.de/pubs/pdf/3141.pdf>`_)
     - RVEA introduces APD and periodic vector scaling. Section VI, Algorithm 4
       adds random regeneration of inactive vectors in a second set: RVEA*.
   * - `Liu et al., CEC 2019 <https://doi.org/10.1109/CEC.2019.8790214>`_
       (`author manuscript, iRVEA <https://www.honda-ri.de/pubs/pdf/3919.pdf>`_)
     - iRVEA replaces at most one inactive adaptive vector per generation and
       combines APD with regional protection, dominance, and an epsilon
       archive. This is the iRVEA implemented here.
   * - `Tian et al., TEVC 2019 <https://doi.org/10.1109/TEVC.2018.2866854>`_
     - Strengthened dominance relation (SDR), used by iRVEA above six
       objectives.
   * - `Liu et al., RVEA-iGNG, online 2020 <https://www.honda-ri.de/publications/publications/?pubid=4467>`_
     - Learns front geometry with growing neural gas instead of regenerating
       individual directions.
   * - `Liu et al., CARV-MOEA, 2023 <https://www.honda-ri.de/publications/publications/?pubid=5147>`_
     - Coordinates vector adaptation and scalarizing functions using local
       angles and vector age.
   * - `Liang et al., TensorRVEA, GECCO 2024 <https://doi.org/10.1145/3638529.3654223>`_
       (`manuscript <https://arxiv.org/abs/2404.01159>`_)
     - Tensorizes RVEA for GPU execution; addresses computational throughput.
   * - `RVEA-DWC, 2024 <https://doi.org/10.1016/j.swevo.2024.101585>`_
     - Combines local/global vector generation with a modified selection
       criterion.
   * - `WPAEA, 2026 <https://doi.org/10.1016/j.asoc.2026.115271>`_
     - Uses hyperplane projection and adaptive population selection. The
       publisher's abstract places it among more recent approaches to
       irregular fronts.

This focused review was checked on September 9, 2026. RVEA* and iRVEA are
established variants; they should not be presented as the latest algorithms or
as uniformly superior to newer methods. The newer papers provide context and
are not substituted for the two requested algorithms.

The authors' `PlatEMO RVEAa implementation <https://github.com/BIMK/PlatEMO/tree/master/PlatEMO/Algorithms/Multi-objective%20optimization/RVEAa>`_
was consulted to cross-check RVEA* ordering and final truncation. The new Java
selection code is independently implemented from the mathematical description.

Implemented behavior
----------------------

All objectives are minimized. The builders reject constrained problems.
``RVEABuilder``, ``RVEAStarBuilder``, and ``IRVEABuilder`` return the existing
``EvolutionaryAlgorithm<S>`` and use its evaluation, variation, replacement,
termination, and observer components.

For a translated objective vector ``f``, assigned direction ``v``, and smallest
neighbor angle ``gamma(v)``, selection minimizes

.. code-block:: text

  APD(f, v, t) = ||f|| * (1 + M * (t / T)^alpha * angle(f, v) / gamma(v))

The candidate population's current minimum supplies the translation; there is
no historical ideal point. Predefined directions are periodically rescaled
from their original values using survivor objective ranges, then normalized.

.. list-table:: RVEA family behavior
   :header-rows: 1
   :widths: 10 30 35 25

   * - Variant
     - Survivor selection
     - Vector updates
     - Population size
   * - RVEA
     - Minimum APD in each occupied niche
     - Periodic range scaling
     - At most N
   * - RVEA*
     - Nondominated candidates, then APD over both sets
     - Range scaling of predefined vectors; random regeneration of inactive
       additional vectors
     - Up to 2N during evolution; at most N after final angular truncation
   * - iRVEA
     - APD, regional protection, Pareto/SDR secondary selection, clustering,
       late epsilon selection
     - Range scaling of predefined vectors; at most one inactive adaptive
       vector replaced using a least-covered candidate direction
     - At most N; may be smaller when insufficient candidates survive

An offspring batch retains its configured size even when the parent population
shrinks. Random mating samples with replacement, so a single surviving parent
is supported. iRVEA's archive holds at most N solution copies and is updated
using additive epsilon fitness with ``kappa = 0.05``.

Explicit implementation choices
----------------------------------

The iRVEA manuscript leaves some details open and its pseudocode contains
inconsistent counts. The following choices make the implementation reviewable:

* "Least similarity" means maximizing the minimum angle to active vectors.
  Activity is measured over both sets using translated objectives, consistently
  with APD. No replacement is needed if every candidate direction is covered.
* Coarse regions use nearest-direction association in range-normalized space.
  The default target is 40; a single-layer simplex lattice chooses the largest
  available count no greater than the target (36 for three objectives), with
  the objective-axis lattice as the minimum. The target is capped at N.
* A dominated representative selected to protect an otherwise unrepresented
  region is reserved through subsequent dominance and truncation steps. This
  follows the stated region-preservation purpose; applying the manuscript's
  later global Pareto filter literally would discard that representative again.
* Hierarchical reduction uses average linkage on angles and retains the
  member nearest the translated ideal point. Crowding reduction compares the
  sorted angular neighbor distances; equal crowding favors the shorter norm,
  then stable input order. The manuscript does not specify linkage or ties.
* SDR uses normalized objective sums and the adaptive angular threshold of the
  `authors' SDR reference implementation <https://github.com/BIMK/PlatEMO/blob/master/PlatEMO/Algorithms/Multi-objective%20optimization/NSGA-II-SDR/NDSort_SDR.m>`_.
  Epsilon fitness also uses current objective ranges, with a separate scale
  for each target column, held constant during iterative deletion.
* After ``t > 0.8*T``, epsilon selection keeps up to ``N - |P_APD|`` remaining
  candidates. This resolves the inconsistent deletion count in Algorithm 3.
  Solutions are never invented or cloned just to fill an empty slot.
* A zero translated objective vector has zero APD and associates with the
  first direction. Angular denominators are floored at ``1e-12``. A scaling or
  regeneration step yielding the zero vector retains the previous direction.
  Duplicate directions and constant objective ranges therefore remain finite.

These are documented interpretations of the papers, not a claim of bitwise
equivalence to an unavailable original iRVEA program or reproduction of its
published benchmark tables.

Configuration
---------------

The existing RVEA constructors and component setters remain available. The
builders now reject constrained problems, invalid numerical directions, and
budgets smaller than the initial population. Callers must also allow a survivor
population smaller than N instead of assuming empty niches are filled.

The constructors accept either simplex divisions ``H`` or a caller-supplied
``List<double[]>``. The initial population size must equal the vector count:
``N = C(H + M - 1, M - 1)``. For three objectives, ``H=12`` gives ``N=91``.
Two-layer vectors can be supplied through
``ReferencePointGenerator.generateTwoLayers``; for example ``(8, 3, 2)`` gives 156
directions. Predefined vectors are copied and normalized at construction.

.. code-block:: java

  var problem = new DTLZ7(22, 3);
  var algorithm = new IRVEABuilder<>(
      problem, 91, 25000,
      new SBXCrossover(1.0, 20.0),
      new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
      2.0, 0.1, 12)
      .setNumberOfSubregions(40)
      .build();
  algorithm.run();
  var population = algorithm.result();

Set ``JMetalRandom.getInstance().setSeed(...)`` **before building** to reproduce
the initial random adaptive vectors as well as the search. Build a fresh
algorithm for each run. Default mating, crossover, and mutation use jMetal's
shared random generator; concurrent runs need appropriate random isolation.

``setTermination``, ``setEvaluation``, ``setCreateInitialPopulation``, ``setSelection``,
and ``setVariation`` are supported. Custom components must obey their existing
jMetal contracts. Termination must be ``TerminationByEvaluations``. The initial
population consumes N evaluations and the generation horizon is
``T = max(1, ceil((maxEvaluations - N) / offspringSize))``. Generation indices run
from zero; adaptation occurs at zero and every ``ceil(fr*T)`` selections.
The last complete offspring batch may overshoot the budget by fewer than
``offspringSize`` evaluations. A 25,000 budget with batches of 91 uses 25,025.

Running the examples
-----------------------

Build from the repository root with Maven and Java 21 or later.

.. code-block:: bash

  mvn -DskipTests=false clean package
  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.RVEAStarDTLZ2Example
  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.IRVEADTLZ7Example
  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.RVEAVariantsExample DTLZ5 25000 42

The existing ``jmetal-auto`` distribution JAR includes ``jmetal-component`` and
its dependencies. Alternatively, run these main classes directly in an IDE.

``RVEAVariantsExample`` runs all three algorithms and accepts DTLZ2 (regular),
DTLZ5 (degenerate), DTLZ7 (disconnected), or ZDT1/2/3, followed by a budget and seed.
The ZDT problems use two objectives, 30 decision variables, 100 initial solutions,
and 100 predefined vectors (``H=99``). Their reference front filenames do not
include the ``.3D`` suffix used by the DTLZ examples.
The two minimal examples use seed 42 and 25,000 evaluations.
Each run writes its nondominated subset to ``FUN.<algorithm>.<problem>.csv``
and the corresponding decision variables to ``VAR.<algorithm>.<problem>.csv``.
It reports evaluations, survivors, nondominated count, runtime, and raw IGD+
against the repository's reference front. Smaller IGD+ is better; compare
values within the same problem. A single example run is not a statistical
comparison of algorithms.

Verification
--------------

Tests cover APD convergence/diversity tradeoffs, translation and scale changes,
adaptation scheduling, empty niches, regeneration, the one-vector replacement
limit, protected dominated regions, Pareto/SDR switching, late supplementation,
epsilon deletion, clustering, archive isolation, numerical degeneracy, builder
validation, odd offspring batches, and repeatability across fresh builds.
Runs on DTLZ2/5/7, MaF1, and seven-objective DTLZ2 exercise complete pipelines;
a seeded DTLZ2 test checks progress toward the unit-sphere front.

.. code-block:: bash

  mvn -pl jmetal-component -am "-Dtest=*RVEA*Test,ReferencePointGeneratorTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
  mvn validate

``develop`` does not bind Checkstyle to ``validate``; this command checks the Maven
reactor. New Java files are formatted with Google Java Format. Coverage is
measured with a JaCoCo agent supporting the locally installed JDK.

.. code-block:: bash

  mvn -pl jmetal-component -am "-Dtest=*RVEA*Test,ReferencePointGeneratorTest" "-Dsurefire.failIfNoSpecifiedTests=false" org.jacoco:jacoco-maven-plugin:0.8.13:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.13:report

Verified in this workspace: the complete ``clean package`` reactor reported
2,006 tests with no failures or errors; the 13 skipped tests are pre-existing.
All 66 new test cases passed. JaCoCo reports 497/509 covered lines (97.6%)
across the added and modified RVEA production classes, excluding the examples
and the unchanged reference-point generator.
Google Java Format's dry run reported no changes, and ``mvn validate`` passed.

The example executions below used seed 42 and 25,025 actual evaluations.
These are smoke-run observations, not multi-seed experimental conclusions.

.. list-table:: Three-objective smoke-run results
   :header-rows: 1
   :widths: 15 15 18 30 12

   * - Algorithm
     - Problem
     - Final survivors
     - Exported nondominated solutions
     - IGD+
   * - RVEA*
     - DTLZ2
     - 91
     - 91
     - 0.023796
   * - iRVEA
     - DTLZ7
     - 91
     - 90
     - 0.039189
   * - RVEA
     - DTLZ5
     - 50
     - 10
     - 0.036877
   * - RVEA*
     - DTLZ5
     - 85
     - 85
     - 0.003019
   * - iRVEA
     - DTLZ5
     - 91
     - 90
     - 0.002880

Additional two-objective runs
--------------------------------

ZDT1, ZDT2, and ZDT3 provide convex, concave, and disconnected reference fronts,
respectively. All nine executions below use the default 30 decision variables,
seed 42, population 100, and exactly 25,000 evaluations. Crossover, mutation,
alpha, and adaptation frequency are the same as in the preceding examples.

.. list-table:: Two-objective smoke-run results
   :header-rows: 1
   :widths: 15 15 30 15

   * - Problem
     - Algorithm
     - Nondominated solutions
     - IGD+
   * - ZDT1
     - RVEA
     - 68
     - 0.038176
   * - ZDT1
     - RVEA*
     - 100
     - 0.003824
   * - ZDT1
     - iRVEA
     - 100
     - 0.003330
   * - ZDT2
     - RVEA
     - 44
     - 0.043022
   * - ZDT2
     - RVEA*
     - 100
     - 0.003357
   * - ZDT2
     - iRVEA
     - 100
     - 0.003046
   * - ZDT3
     - RVEA
     - 41
     - 0.054089
   * - ZDT3
     - RVEA*
     - 100
     - 0.003358
   * - ZDT3
     - iRVEA
     - 93
     - 0.004571

These single runs favor iRVEA on ZDT1/2 and RVEA* on ZDT3 in IGD+. They do not
establish statistical superiority. The plots show all exported nondominated
solutions, use identical axis limits within each problem, and preserve the
gaps between ZDT3 reference-front segments.

After rebuilding the distribution JAR, run from the repository root:

.. code-block:: bash

  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.RVEAVariantsExample ZDT1 25000 42
  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.RVEAVariantsExample ZDT2 25000 42
  java -cp jmetal-auto/target/jmetal-auto-7.6-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.component.examples.multiobjective.rvea.RVEAVariantsExample ZDT3 25000 42
  python results/rvea/zdt/plot_results.py --input-dir .

The plotting script verifies the CSV dimensions, variable bounds, nondominance,
and IGD+ against the recorded executions in ``results/rvea/zdt/runs.log``. It writes
input hashes and full-precision metrics to ``plot_data.json``, a
`nine-panel overview <../results/rvea/zdt/overview.png>`_, individual problem PNGs,
and a `four-page PDF <../results/rvea/zdt/ZDT_execution_plots.pdf>`_.

The recorded FUN and VAR files are also included in ``results/rvea/data``.
Both plotting scripts use these archived inputs by default, so the figures
can be regenerated from a fresh checkout with Python, NumPy, and Matplotlib:

.. code-block:: bash

  python results/rvea/plots/plot_results.py
  python results/rvea/zdt/plot_results.py

Use ``--input-dir .`` to plot freshly executed examples from the repository root.
The DTLZ `overview <../results/rvea/plots/overview.png>`_ and
`PDF <../results/rvea/plots/RVEA_execution_plots.pdf>`_ contain the five
three-objective runs above. These archived single-seed runs were first recorded
on the earlier 6.9.4 development baseline. All 14 runs were repeated with the
7.6 distribution after integrating the current ``develop`` base; every FUN and VAR
file matched the archived inputs byte for byte. The new execution output is in
`reproduction.log <../results/rvea/reproduction.log>`_.
