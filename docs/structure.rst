.. structure:

Project structure
=================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2022-11-30

The jMetal project is a Maven project structured in the following sub-projects:

+---------------------+------------------------------------+
| Sub-project         |  Contents                          |
+=====================+====================================+
| jmetal-core         |  Core classes                      |
+---------------------+------------------------------------+
| jmetal-solution     |  Solution encodings                |
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

If you intend to use jMetal as dependence of your project, you do not need to import all the
sub-projects. For example, if you are only interested in using some of the provided algorithms, then
``jmetal-algorithm`` or ``jmetal-component`` should be required. The dependencies section of the ``pom.xml``
file of your project would contain then:

.. code-block:: xml

   <dependencies>
    <dependency>
      <groupId>org.uma.jmetal</groupId>
      <artifactId>jmetal-algorithm</artifactId>
      <version>6.0-SNAPSHOT</version>
    </dependency>
   </dependencies>

