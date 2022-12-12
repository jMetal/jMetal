.. _installation:

Installation
============

jMetal is a Maven project hosted in GitHub, so there are two ways of getting the software: getting
the source code from https://github.com/jMetal/jMetal or adding it as a dependence in your own
Maven project. The sub-projects of jMetal can be obtained from Maven Central
Repository (https://search.maven.org/search?q=jmetal) or
from MvnRepository (https://mvnrepository.com/search?q=jmetal).

Requirements
------------

The software requirements to use jMetal are:

* Java 14+ JDK
* Maven
* Optional: R, Latex.

To work with the last development version, just clone the repository in https://github.com/jMetal/jMetal.

Building the project
--------------------

Once you have the source code of jMetal you can use it in to ways: with an integrated development
environment (IDE), such as IntelliJ Idea, Netbeans or Eclipse, or with an advanced editor (e.g., Visual
Studio Code). The best choice depends on your preferences; in our case we use IntellJ Idea.

With an IDE, importing the jMetal project is vey simple (in IntellJ Idea is just a matter of opening it).
Building the project is also straightforward as IDEs have ways of doing this, either manually or
automatically. You have also the choice of using the command line to build the project using Maven
commands; concretely:

* *mnv clean*: clean the project, removing temporal files
* *mvn compile*: compile
* *mvn test*: compile and run the unit tests
* *mvn package*: compile, run the unit tests, generate documentation, and package into a jar file


