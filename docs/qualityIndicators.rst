.. _qualityIndicators:

Quality indicators
==================

:Author: Antonio J. Nebro
:Version: 1.2
:Date: 2026-03-02

Quality indicators are entities that typically measure the convergence and/or the diversity Pareto front approximations, and most of
them require a reference Pareto front to be applied. These indicators are located in package ``org.uma.jmetal.qualityindicator`` (sub-project ``jmetal-core``), and all of them implement the ``QualityIndicator`` abstract class:

.. code-block:: java

  public abstract class QualityIndicator {
    protected double[][] referenceFront ;

    protected QualityIndicator() {
    }

    protected QualityIndicator(double[][] referenceFront) {
      Check.notNull(referenceFront);
      this.referenceFront = referenceFront;
    }

    public abstract double compute(double[][] front) ;
    public abstract boolean isTheLowerTheIndicatorValueTheBetter();

    public void referenceFront(double[][] referenceFront) { ... }
    public double[][] referenceFront() { ... }

    public abstract QualityIndicator newInstance() ;
    public abstract String name() ;
    public abstract String description() ;
  }


It is worth noticing than both the fronts are represented as matrices, which will contain objectives values, and the code implementing the indicators does not use any jMetal dependence, and it is assumed that the fronts are normalized before computing the indicators. All the quality indicators have to implement the ``isTheLowerTheIndicatorValueTheBetter()`` method, which is required to compare the indicator values of two or more fronts. 

The list of available quality indicators is the following:

* ``EP``: Additive epsilon
* ``ER``: Error ratio
* ``SP``: Spread (two objectives)
* ``GSPREAD``: Generalized spread (more than two objectives)
* ``GD``: Generational distance
* ``IGD``: Inverted generational distance
* ``IGD+``: Inverted generational distance plus
* ``HV``: Hypervolume
* ``NHV``: Normalized hypervolume
* ``AHD``: Average Hausdorff Distance
* ``R2``: R2 indicator
* ``SC``: Set coverage

Example of use
^^^^^^^^^^^^^^

The following code snippet shows the typical way of computing a quality indicator (we use the additive epsilon as an example):

.. code-block:: java

  double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
  double[][] normalizedFront =
            NormalizeUtils.normalize(
                    front,
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

  Epsilon epsilon = new Epsilon(normalizedReferenceFront) ;
  double indicatorValue = epsilon.compute(normalizedFront) ;

A typical scenario involves finding a Pareto front approximation and compute a quality indicator but the reference front is stored in a file. In this case, an utility function is ``readVectors()``:

.. code-block:: java

   double[][]referenceFront =  VectorUtils.readVectors(referenceParetoFront, ",")

In this code, we assume the file containing the front has a CSV format.


Normalization of the fronts
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The fronts are normalized with the ``NormalizeUtils`` class. The reference front is normalized with its own minimum
and maximum values in each objective, and the front with those of the reference front, so a point of the front that is
out of the range of the reference front is mapped out of [0, 1]. This works with objectives of any sign, including
those that are always negative. The normalization rejects, with an ``InvalidConditionException``, a matrix that is
empty, has rows of different length, or contains a NaN or an infinite value (a front with an infinite objective value,
which some problems produce, cannot be normalized). An objective with the same value in all the points is mapped to 0.5.

Some details of the indicators are worth knowing:

* The hypervolumes ``PISAHypervolume`` (``HV``) and ``NormalizedHypervolume`` (``NHV``) assume normalized fronts and the
  reference point (1, ..., 1): a coordinate above one adds nothing to the hypervolume, and a negative one is counted as
  zero. ``WFGHypervolume`` and ``Hypervolume3D`` use the reference point of their reference front, whatever the sign of
  its coordinates.
* ``NHV`` is 1 - (HV of the front / HV of the reference front), so it is zero for the reference front, one for an empty
  front, and negative if the front has more hypervolume than the reference front. The hypervolume of the reference
  front must be positive, otherwise the indicator is not defined and an ``InvalidConditionException`` is thrown. It can
  also be built from a reference point (``new NormalizedHypervolume(new double[] {1.0, 1.0})``): the reference is then
  the box between the origin and the point.
* ``SP`` and ``GSPREAD`` are 1.0 for a front with a single point (or identical points), and 0.0 for a front uniformly
  spread between the extremes of the reference front.
* ``IGD+`` does not penalize a front that is better than the reference front, while ``IGD`` does.
* The indicators reject, with a ``NullParameterException`` or an ``InvalidConditionException``, a null or empty front,
  a missing reference front, and a front with another number of objectives than the reference front.

Computing quality indicators from the command line
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you need to compute the value of a quality indicator of a front of solutions from the command line you can use the
``CommandLineQualityIndicatorTool``. We assume the following:

* The current working directory is the root of jMetal.
* The obtained front is stored in a file calle ``FUN.csv``.
* The solved problem is ZDT1, whose reference front is in ``resources/referenceFronts/ZDT1.csv``.
* The instruction ``mvn package`` (or ``mvn package -DskipTests=True``) has been executed.

The list of available quality indicators can be obtained by calling the class without parameters:

.. code-block:: bash

  % java -cp jmetal-core/target/jmetal-core-7.8-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool

  INFO: Loggers configured with null [org.uma.jmetal.util.JMetalLogger configureLoggers]
  INFO: Parameters: indicatorName referenceFrontFile frontFile 
  Where indicatorValue can be one of these:
  EP      - Additive epsilon
  GD      - Generational distance
  IGD     - Inverted generational distance
  IGD+    - Inverted generational distance plus 
  HV      - Hypervolume 
  NHV     - Normalized hypervolume 
  ER      - Error ratio 
  SP      - Spread (two objectives)
  GSPREAD - Generalized Spread (more than two objectives)
  AHD     - Average Hausdorff Distance
  R2      - R2 indicator
  SC      - Set coverage
 [ org.uma.jmetal.utilities.CommandLineQualityIndicatorTool printAvailableIndicators]
  Exception in thread "main" org.uma.jmetal.util.errorchecking.JMetalException: Invalid arguments
	
We can compute a single indicator, as the inverted generational distance:

.. code-block:: bash

 % java -cp jmetal-core/target/jmetal-core-7.8-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool IGD resources/referenceFronts/ZDT1.csv FUN.csv
 
  INFO: Loggers configured with null [org.uma.jmetal.util.JMetalLogger configureLoggers]
  INFO: The fronts are normalized before computing the indicators [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: 2.2536457240826101E-4 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]

Or all the quality indicators if we use ``ALL`` as indicator:

.. code-block:: bash

  % java -cp jmetal-core/target/jmetal-core-7.8-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool ALL resources/referenceFronts/ZDT1.csv FUN.csv

  INFO: Loggers configured with null [org.uma.jmetal.util.JMetalLogger configureLoggers]
  INFO: The fronts are normalized before computing the indicators [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: EP: 7.098510362065962E-4 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: HV: 0.6665198715008906 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: NHV: -0.009962058513842686 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: GD: 1.933259493087464E-4 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: IGD: 2.2536457240826101E-4 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: IGD+: 2.3576944009587076E-5 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: SP: 0.27852463257758625 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: GSPREAD: 0.2780440568993888 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: AHD: 6.018706267698593E-3 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: R2: 0.13257216418927098 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: SC(refFront, front): 0.0 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: SC(front, refFront): 0.9090909090909091 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]