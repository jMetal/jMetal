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

.. image:: https://readthedocs.org/projects/jmetal/badge/?version=latest
   :alt: Documentation Status
   :target: https://jmetal.readthedocs.io/?badge=latest

jMetal is a Java-based framework for multi-objective optimization with metaheuristics.
The last stable version is 7.7.
The most recent documentation is hosted in https://jmetal.readthedocs.io.


jMetal 7.7 is a Maven project structured in the following sub-projects:

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
