.. _aggregationFunctions:

Aggregation functions
=====================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2026-6-12

Decomposition-based algorithms such as MOEA/D transform a multi-objective problem into a set of scalar
subproblems, each one defined by a weight vector and an aggregation (or scalarizing) function. In jMetal,
aggregation functions are located in the ``org.uma.jmetal.util.aggregationfunction`` package of the
``jmetal-core`` sub-project, and they implement the `AggregationFunction <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/util/aggregationfunction/AggregationFunction.java>`_ interface:

.. code-block:: java

  package org.uma.jmetal.util.aggregationfunction;

  public interface AggregationFunction {
    double compute(double[] vector, double[] weightVector, IdealPoint idealPoint, NadirPoint nadirPoint) ;
    boolean normalizeObjectives() ;
    void epsilon(double value) ;
  }

The ``compute()`` method receives the objective values of a solution, the weight vector of the subproblem,
and the current estimations of the ideal and nadir points, and it returns the scalar value to be minimized.
The other two methods deal with objective normalization:

* ``normalizeObjectives()``: each aggregation function declares whether it works with normalized objective
  values. When normalization is enabled, every objective value is replaced by
  :math:`(f_i - z_i^*) / (z_i^{nad} - z_i^* + \epsilon)`, where :math:`z^*` and :math:`z^{nad}` are the ideal
  and nadir points. The MOEA/D builders keep their normalization settings consistent with the value returned
  by this method.

* ``epsilon(value)``: sets the :math:`\epsilon` value added to the denominator of the normalization formula
  to avoid divisions by zero when the nadir and ideal points are very close (default: :math:`10^{-6}`).

Available implementations
-------------------------

The implementations provided in the ``org.uma.jmetal.util.aggregationfunction.impl`` package are the following
(in all the formulas, :math:`f_i` denotes the (possibly normalized) value of objective :math:`i`,
:math:`w` is the weight vector, and :math:`z^*` and :math:`z^{nad}` are the ideal and nadir points):

``WeightedSum``
  Weighted sum of the objective values:

  .. math:: g^{ws}(x|w) = \sum_{i=1}^{m} w_i \, f_i(x)

``Tschebyscheff``
  Tchebycheff function, as defined in the original MOEA/D paper (`Zhang and Li, 2007 <https://doi.org/10.1109/TEVC.2007.892759>`_):

  .. math:: g^{te}(x|w,z^*) = \max_{1 \leq i \leq m} \{ w_i \, |f_i(x) - z_i^*| \}

  A small factor (0.0001) replaces zero-valued weights to avoid ignoring objectives.

``AugmentedTschebyscheff``
  Augmented Tchebycheff function, which adds a weighted sum term to the Tchebycheff function to avoid
  weakly Pareto optimal solutions:

  .. math:: g^{ate}(x|w,z^*) = \max_{1 \leq i \leq m} \{ w_i \, |f_i(x) - z_i^*| \} + \rho \sum_{i=1}^{m} w_i \, |f_i(x) - z_i^*|

  The augmentation coefficient :math:`\rho` defaults to 0.0001.

``ModifiedTschebyscheff``
  Modified Tchebycheff function, where the weights divide the distances to the ideal point instead of
  multiplying them, producing uniformly distributed solutions for uniformly distributed weight vectors:

  .. math:: g^{mte}(x|w,z^*) = \max_{1 \leq i \leq m} \{ |f_i(x) - z_i^*| / w_i \}

``PenaltyBoundaryIntersection``
  Penalty-based boundary intersection (PBI), also defined in the original MOEA/D paper. Given :math:`d_1`,
  the distance from the ideal point to the projection of the solution on the line defined by the weight
  vector, and :math:`d_2`, the distance from the solution to that line:

  .. math:: g^{pbi}(x|w,z^*) = d_1 + \theta \, d_2

  The penalty parameter :math:`\theta` defaults to 5.0.

``InvertedPenaltyBoundaryIntersection``
  Inverted PBI (IPBI), proposed in `Sato, 2014 <https://doi.org/10.1145/2576768.2598297>`_, which measures
  the distances from the nadir point instead of from the ideal point, pushing the search from the nadir
  towards the Pareto front:

  .. math:: g^{ipbi}(x|w,z^{nad}) = \theta \, d_2 - d_1

  where :math:`d_1` and :math:`d_2` are computed by projecting :math:`z^{nad} - f(x)` on the weight vector.
  The penalty parameter :math:`\theta` defaults to 0.1.

Usage
-----

The aggregation function is a parameter of the MOEA/D-style components and builders of ``jmetal-component``
(see :ref:`component`). The ``MOEADBuilder`` class uses ``PenaltyBoundaryIntersection`` by default, while
``MOEADDEBuilder`` uses ``Tschebyscheff``; both provide a ``setAggregationFunction()`` method:

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
      .setAggregationFunction(new AugmentedTschebyscheff(0.0001, normalizeObjectives))
      .build();

The aggregation function is also exposed as a configurable parameter (``aggregationFunction``) in the
auto-configurable version of MOEA/D (see :ref:`autoconfiguration`), so it can be tuned by automatic
configuration tools.
