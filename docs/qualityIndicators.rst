.. _qualityIndicators:

Quality indicators
==================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2022-12-12

Quality indicators are entities that typically measure the convergence and/or the diversity Pareto front approximations, and most of
them require a reference Pareto front to be applied. These indicators are located in package ``org.uma.jmetal.qualityindicator`` (sub-project ``jmetal-core``), and all of them implement the ``QualityIndicator`` abstract class:

.. code-block:: java

  public abstract class QualityIndicator {
    protected double[][] referenceFront ;

    protected QualityIndicator(double[][] referenceFront) {
      Check.notNull(referenceFront);
      this.referenceFront = referenceFront;
      }

    public abstract double compute(double[][] front) ;
    public abstract boolean isTheLowerTheIndicatorValueTheBetter();

    ...
  }


It is worth noticing than both the fronts are represented as matrices, which will contain objectives values, and the code implementing the indicators does not use any jMetal dependence, and it is assumed that the fronts are normalized before computing the indicators. All the quality indicators have to implement the ``isTheLowerTheIndicatorValueTheBetter()`` method, which is required to compare the indicator values of two or more fronts. 

The list of available quality indicators is the following:

* ``EP``: Additive epsilon
* ``ER``: Error ratio
* ``SPREAD``: Spread (two objectives)
* ``GSPREAD``: Generalized spread (more than two objectives)
* ``GD``: Generational distance
* ``IGD``: Inverted generational distance
* ``IGD+``: Inverted generational distance plus
* ``SC``: Set coverage
* ``HV``: Hypervolume
* ``NHV``: Normalized hypervolume

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


Computing quality indicators from the command line
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you need to compute the value of a quality indicator of a front of solutions from the command line you can use the
``CommandLineQualityIndicatorTool``. We assume the following:

* The current working directory is the root of jMetal.
* The obtained front is stored in a file calle ``FUN.csv``.
* The solved problem is ZDT1, whose reference front is in ``resources/referenceFrontsCSV/ZDT1.csv``.
* The instruction ``mvn package`` (or ``mvn package -DskipTests=True``) has been executed.

The list of available quality indicators can be obtained by calling the class without parameters:

.. code-block:: bash

  % java -cp jmetal-core/target/jmetal-core-6.0-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool

  INFO: Loggers configured with null [org.uma.jmetal.util.JMetalLogger configureLoggers]
  INFO: Parameters: indicatorName referenceFrontFile frontFile 
  Where indicatorValue can be one of these:
  EP   - Additive epsilon
  GD   - Generational distance
  IGD  - Inverted generational distance
  IGD+ - Inverted generational distance plus 
  HV   - Hypervolume 
  NHV  - Normalized hypervolume 
  ER   - Error ratio 
  SP   - Spread (two objectives)
  GSPREAD - Generalized Spread (more than two objectives)
  SC   - Set coverage
 [ org.uma.jmetal.utilities.CommandLineQualityIndicatorTool printAvailableIndicators]
  Exception in thread "main" org.uma.jmetal.util.errorchecking.JMetalException: Invalid arguments
	
We can compute a single indicator, as the inverted generational distance:

.. code-block:: bash

 % java -cp jmetal-core/target/jmetal-core-6.0-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool IGD FUN.csv resources/referenceFrontsCSV/ZDT1.csv
 
  INFO: Loggers configured with null [org.uma.jmetal.util.JMetalLogger configureLoggers]
  INFO: The fronts are normalized before computing the indicators [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: 2.2536457240826101E-4 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]

Or all the quality indicators if we use ``ALL`` as indicator:

.. code-block:: bash

  % java -cp jmetal-core/target/jmetal-core-6.0-jar-with-dependencies.jar org.uma.jmetal.utilities.CommandLineQualityIndicatorTool ALL FUN.csv resources/referenceFrontsCSV/ZDT1.csv

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
  INFO: SC(refFront, front): 0.0 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]
  INFO: SC(front, refFront): 0.9090909090909091 [org.uma.jmetal.utilities.CommandLineQualityIndicatorTool calculateAndPrintIndicators]