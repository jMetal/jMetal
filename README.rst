jMetal project Web site
=======================

.. image:: https://github.com/jMetal/jMetal/actions/workflows/build.yml/badge.svg
    :alt: Build Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/build.yml

.. image:: https://github.com/jMetal/jMetal/actions/workflows/test.yml/badge.svg
    :alt: Test Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/test.yml

.. image:: https://github.com/jMetal/jMetal/actions/workflows/integration-test.yml/badge.svg
    :alt: Integration Test Status
    :target: https://github.com/jMetal/jMetal/actions/workflows/integration-test.yml

.. image:: https://app.readthedocs.org/projects/jmetal/badge/?version=latest
   :alt: Documentation Status
   :target: https://jmetal.readthedocs.io/?badge=latest

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.
The last stable version is 7.9.
The most recent documentation is hosted in https://jmetal.readthedocs.io.


jMetal 7.9 is a Maven project structured in the following sub-projects:

+---------------------+------------------------------------+
| Sub-project         |  Contents                          | 
+=====================+====================================+
| jmetal-core         |  Core classes                      |
+---------------------+------------------------------------+
| jmetal-algorithm    |  Algorithm implementations         |
+---------------------+------------------------------------+
| jmetal-problem      |  Benchmark problems                |
+---------------------+------------------------------------+
| jmetal-lab          |  Experimentation and visualization |
+---------------------+------------------------------------+
| jmetal-parallel     |  Parallel extensions               |
+---------------------+------------------------------------+
| jmetal-auto         |  Auto-design and configuration     |
+---------------------+------------------------------------+
| jmetal-component    |  Component-based algorithms        |
+---------------------+------------------------------------+

.. note::

  jMetal 7.6 is the last version in which ``jmetal-auto`` is updated. From now on this sub-project is
  frozen, as its functionality is covered by the `Evolver <https://github.com/jMetal/Evolver>`_ project, which is under active development.


Related projects
----------------
* `jMetalPy <https://github.com/jMetal/jmetalpy>`_: jMetal in Python
* `SAES <https://github.com/jMetal/SAES>`_: Python library to analyse and compare the performance of multi-objective algorithms
* `Evolver <https://github.com/jMetal/Evolver>`_: Java framework for the automated meta-optimization of multi-objective metaheuristics, which formulates their automatic configuration and design as a multi-objective optimization problem
* `Evolver-Studio <https://github.com/jMetal/Evolver-Studio>`_: graphical interface for Evolver (Python/Streamlit application)

Python scripts (NEW)
--------------------

The ``scripts/`` directory contains Python utilities for visualising Pareto fronts produced by
jMetal algorithms (any ``FUN.csv`` file):

+-------------------------------+--------------------------------------------------------+
| Script                        | Description                                            |
+===============================+========================================================+
| ``plot_front.py``             | Static matplotlib figure (2D/3D scatter, parallel      |
|                               | coordinates for >3 objectives). Saves to PNG/PDF/SVG.  |
+-------------------------------+--------------------------------------------------------+
| ``plot_front_interactive.py`` | Interactive Plotly viewer (rotate 3D fronts, hover for |
|                               | values). Opens in the browser or saves as HTML.        |
+-------------------------------+--------------------------------------------------------+

Install dependencies and run::

    # venv
    python -m venv .venv && source .venv/bin/activate
    pip install -r scripts/requirements.txt

    # conda
    conda create -n jmetal python=3.11 && conda activate jmetal
    pip install -r scripts/requirements.txt

    # Usage examples
    python scripts/plot_front.py FUN.csv
    python scripts/plot_front.py FUN.csv referenceFront.csv --mode side --output fig.png
    python scripts/plot_front_interactive.py FUN.csv referenceFront.csv --mode both




Changelog
---------

* [10/29/2026] Fixes that change results: the ``CURRENT_TO_RAND_1_BIN`` and ``CURRENT_TO_RAND_1_EXP``
  variants of ``DifferentialEvolutionCrossover`` had no mutation (every mutated variable was set to
  0), and CF10 did not count its second constraint in the violation. Results obtained with them in
  earlier versions are not valid.

* [10/26/2026] The LIR-CMOP problems follow the paper (Fan et al., 2019): their definitions change,
  so the results obtained with LIRCMOP5 to LIRCMOP14 in earlier versions are not comparable, and they
  have exact reference fronts.

* [10/26/2026] The constrained problems DC1-DTLZ1 to DC3-DTLZ3 (package ``dcdtlz``) and CF1 to CF10
  of the CEC 2009 competition (package ``cec2009``, classes ``CEC2009CF1`` to ``CEC2009CF10``), with
  their reference fronts.

* [10/23/2026] jMetal 7.9 is released. Changes that can affect existing code or results: the
  problems of the CEC 2021 suite are named ``RCM01`` to ``RCM50``; the random selection of the
  algorithms and the normalization of SMS-EMOA and NSGA-III have changed, so the results for a given
  seed are not the same as in 7.8; the quality indicators and ``NormalizeUtils`` reject invalid
  fronts with an exception. See the changelog section of the documentation for the complete list.

* [10/23/2026] Constraint-handling techniques (package ``org.uma.jmetal.util.constrainthandling``):
  the constrained-domination principle, the epsilon-constrained method, push and pull search,
  stochastic ranking and the self-adaptive penalty, which the component-based NSGA-II, NSGA-III,
  SMS-EMOA, AGE-MOEA, RDSMOEA, PAES, SMPSO and MOEA/D take with ``setConstraintHandlingTechnique``.
  ``Termination`` gives the progress of the run, which the techniques that change during the run
  use. See the `constraints section <https://jmetal.readthedocs.io/en/latest/constraints.html>`_.

* [10/23/2026] Random numbers in parallel threads: ``ThreadLocalRandomGenerator`` gives each thread
  its own generator, ``ExecuteAlgorithms`` installs it with more than one core and runs on as many
  threads as cores, and ``ExperimentBuilder.setSeed`` makes a parallel experiment reproducible.
  The random selection draws only the selected elements, so the results for a given seed change.

* [10/23/2026] Reference fronts of the CRE problems and of the CEC 2021 suite, whose problems are
  now named ``RCM01`` to ``RCM50``, and a page on the constrained benchmarks and their reference
  fronts in the documentation.

* [10/22/2026] Fixes in the normalization of the fronts (``NormalizeUtils``, ``NormalizedHypervolume``,
  the classic and the component-based NSGA-III), the hypervolume with negative reference points, the
  hypervolume contributions of SMS-EMOA with four or more objectives, and the argument checks of the
  quality indicators.

* [10/21/2026] The MW and DAS-CMOP constrained problems, reference fronts of the C-DTLZ, DAS-CMOP and
  MW problems, all the reference fronts in CSV in ``resources/referenceFronts``, and constraint
  handling in RVEA (C-RVEA) and iRVEA.

* [10/13/2026] jMetal 7.8 is released.

* [10/13/2026] Reference fronts of the MaF problems with 3 objectives
  (``resources/referenceFronts/MaFxx.3D.csv``, MaF01 to MaF15), next to the existing ones for 5,
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

The complete list of changes is available in the `changelog section of the documentation
<https://jmetal.readthedocs.io/en/latest/changelog.html>`_.
