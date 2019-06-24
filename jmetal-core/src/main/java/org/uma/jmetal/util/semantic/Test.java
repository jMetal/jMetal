package org.uma.jmetal.util.semantic;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.uma.jmetal.solution.PermutationSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Test {

  public static void main(String[] args) {
    OWLOntologyManager manager;
    OWLOntology ontology;
    OWLDataFactory factory;
    IRI iri = null;
    String pathOWL = "jmetal-core/src/main/resources/ontology/traffic-tsp.owl";
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    manager
        .getIRIMappers()
        .add(
            new SimpleIRIMapper(
                IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam"),
                IRI.create(
                    "file:///home/cbarba/Documents/projectIdea/jmetal/jmetal-core/src/main/resources/ontology/traffic.owl")));
    manager
        .getIRIMappers()
        .add(
            new SimpleIRIMapper(
                IRI.create("http://www.khaos.uma.es/perception/bigowl"),
                IRI.create(
                    "file:///home/cbarba/Documents/projectIdea/jmetal/jmetal-core/src/main/resources/ontology/bigowl.owl")));

    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);

      if (ontology.getOntologyID().getOntologyIRI().isPresent()) {
        iri = ontology.getOntologyID().getOntologyIRI().get();
      }
      System.out.println("Leida ontologia " + iri.getIRIString());

      // ontology_old.individualsInSignature().forEach(individual ->
      // System.out.println(individual.getIRI()));
      OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
      OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

      OWLClass route =
          factory.getOWLClass(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#Route"));
      // Ask the reasoner for the instances of pet
      NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(route, true);
      // The reasoner returns a NodeSet again. This time the NodeSet contains
      // individuals. Again, we just want the individuals, so get a flattened
      // set.
      Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
      System.out.println("Instances of route: ");
      for (OWLNamedIndividual ind : individuals) {
        System.out.println("    " + ind);
      }

      OWLNamedIndividual deliveryRoute =
          factory.getOWLNamedIndividual(
              IRI.create("http://www.khaos.uma.es/perception/traffic-tsp/khaosteam#DeliveryRoute"));
      OWLObjectProperty hasPreference =
          factory.getOWLObjectProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPreference"));
      OWLObjectProperty hasPosition =
          factory.getOWLObjectProperty(
              IRI.create(
                  "http://www.khaos.uma.es/perception/traffic/khaosteam#hasPositionInPreference"));
      OWLDataProperty hasDataType =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasDataType"));
      OWLObjectProperty hasCityObject =
          factory.getOWLObjectProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasCityObject"));
      OWLObjectProperty hasNext =
          factory.getOWLObjectProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasNext"));
      OWLDataProperty hasPositionValue =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPositionValue"));
      OWLObjectProperty hasLocation =
          factory.getOWLObjectProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasLocation"));
      OWLDataProperty hasID =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasId"));
      OWLDataProperty hasX =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasX"));
      OWLDataProperty hasY =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasY"));
      OWLDataProperty isFirst =
          factory.getOWLDataProperty(
              IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#isFirst"));

      NodeSet<OWLNamedIndividual> preferences =
          reasoner.getObjectPropertyValues(deliveryRoute, hasPreference);
      preferences.forEach(
          nodePreference -> {
            nodePreference.forEach(
                preference -> {
                  System.out.println("Preference  " + preference);
                  NodeSet<OWLNamedIndividual> positions =
                      reasoner.getObjectPropertyValues(preference, hasPosition);
                  positions.forEach(
                      nodePositions ->
                          System.out.println(
                              nodePositions + "    --- ----   " + positions.entities().count()));
                });
          });

      preferences.forEach(
          nodePreference -> {
            List<OWLNamedIndividual> positions =
                getIndividualsByObjectProperty(
                    reasoner, nodePreference.iterator().next(), hasPosition);

            boolean quarter = false;
            List<OWLNamedIndividual> buildings = null;

            List<OWLNamedIndividual> locations = null;
            List<OWLLiteral> ids = null;
            List<OWLLiteral> x = null;
            List<OWLLiteral> y = null;
            List<OWLLiteral> datatypes = null;
            List<OWLLiteral> positionValues = null;
            double percentage = 1.0d;
            int index = 0;
            int id = 0;
            for (OWLNamedIndividual indPos : positions) {
              // System.out.println(indPos);

              buildings = getIndividualsByObjectProperty(reasoner, indPos, hasCityObject);
              for (OWLNamedIndividual building : buildings) {
                // System.out.println("Contains "+contain);

                locations = getIndividualsByObjectProperty(reasoner, building, hasLocation);
                if (locations != null && !locations.isEmpty()) {
                  for (OWLNamedIndividual location : locations) {
                    x = getDataPropertyByIndividual(reasoner, location, hasX);
                    y = getDataPropertyByIndividual(reasoner, location, hasY);
                  }
                }

                ids = getDataPropertyByIndividual(reasoner, building, hasID);
                // if (priority.size() > 0) //preference
                // System.out.println("Contains " + contain + " id " + ids.get(0).getLiteral());
              }
              positionValues = getDataPropertyByIndividual(reasoner, indPos, hasPositionValue);
              datatypes = getDataPropertyByIndividual(reasoner, indPos, hasDataType);
              // for (OWLNamedIndividual dt : datatypes) {
              //    System.out.println("DataTypes " + dt.toString().contains("Integer"));

              //  }

              int xx = 0;
            }

            if (positions != null) {
              System.out.println(positions.size());
              if (positions.size() > 1) {

                // subroute + de 1 position
                // hay que obtener de nuevo los valores
                OWLNamedIndividual first = getFirst(reasoner, positions, isFirst);
                List<OWLNamedIndividual> routes = getNext(reasoner, first, hasNext);

                List<OWLNamedIndividual> containsFirst =
                    getIndividualsByObjectProperty(reasoner, routes.get(0), hasCityObject);
                List<OWLLiteral> idFirst =
                    getDataPropertyByIndividual(reasoner, containsFirst.get(0), hasID);

                List<OWLNamedIndividual> containsSecond =
                    getIndividualsByObjectProperty(reasoner, routes.get(1), hasCityObject);
                List<OWLLiteral> idSecond =
                    getDataPropertyByIndividual(reasoner, containsSecond.get(0), hasID);

                int indexFirst = getIdToSolutionByPosition(idFirst.get(0).getLiteral());
                int indexSecond = getIdToSolutionByPosition(idSecond.get(0).getLiteral());

                Function<PermutationSolution<Integer>, Double> constraint =
                    createSolutionByRoute(indexFirst, indexSecond);
                int hh = 0;

              } else {
                // de todos las listas solo va a haber un elemento en cada una de ellas

                for (OWLNamedIndividual indPos : positions) {
                  // System.out.println(indPos);

                  if (positionValues.size() > 0) {
                    if (positionValues.get(0).getLiteral().contains("<")
                        || positionValues.get(0).getLiteral().contains(">")
                        || positionValues.get(0).getLiteral().contains("=")) {
                      quarter = true;
                      percentage = getPercentage(positionValues.get(0).getLiteral());
                      index = getIdToSolutionByPosition(ids.get(0).getLiteral());
                      Function<PermutationSolution<Integer>, Double> constraint =
                          createSolutionByPositionQuarter(index, percentage);
                      int hh = 0;
                    } else {
                      index = getIndexToSolutionByPosition(positionValues.get(0).getLiteral());
                      id = getIdToSolutionByPosition(ids.get(0).getLiteral());
                      //    if (priority.size() > 0) {
                      //     Function<PermutationSolution<Integer>, Double> constraint =
                      //        createSolutionByPositionPriority(index, id);
                      //    }else{
                      Function<PermutationSolution<Integer>, Double> constraint =
                          createSolutionByPosition(index, id);
                      // }
                      int hh = 0;
                    }
                  }
                } // end positions
              }
            }
          });

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static OWLNamedIndividual getFirst(
      OWLReasoner reasoner, List<OWLNamedIndividual> individuals, OWLDataProperty dataProperty) {
    boolean enc = false;
    OWLNamedIndividual result = null;
    Iterator<OWLNamedIndividual> it = individuals.iterator();
    while (!enc && it.hasNext()) {
      OWLNamedIndividual aux = it.next();
      List<OWLLiteral> first = getDataPropertyByIndividual(reasoner, aux, dataProperty);
      if (first.size() > 0) {
        result = aux;
        enc = true;
      }
    }
    return result;
  }

  public static List<OWLNamedIndividual> getNext(
      OWLReasoner reasoner, OWLNamedIndividual first, OWLObjectProperty objectProperty) {
    List<OWLNamedIndividual> result = new ArrayList<>();
    OWLNamedIndividual aux = first;
    result.add(first);
    List<OWLNamedIndividual> next = getIndividualsByObjectProperty(reasoner, aux, objectProperty);
    if (next.size() > 0) {
      for (OWLNamedIndividual ind : next) {
        result.addAll(getNext(reasoner, ind, objectProperty));
      }
    }
    return result;
  }

  public static List<OWLNamedIndividual> getIndividualsByObjectProperty(
      OWLReasoner reasoner, OWLNamedIndividual individual, OWLObjectProperty objectProperty) {
    List<OWLNamedIndividual> result = new ArrayList<>();
    NodeSet<OWLNamedIndividual> nodes =
        reasoner.getObjectPropertyValues(individual, objectProperty);
    if (nodes != null) {
      nodes.forEach(nodeIndiv -> nodeIndiv.forEach(indiv -> result.add(indiv)));
    }
    return result;
  }

  public static List<OWLLiteral> getDataPropertyByIndividual(
      OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
    List<OWLLiteral> result = new ArrayList<>();
    Set<OWLLiteral> nodes = reasoner.getDataPropertyValues(individual, dataProperty);
    if (nodes != null) {
      nodes.stream().forEach(owlLiteral -> result.add(owlLiteral));
    }
    return result;
  }

  public static int getIndexToSolutionByPosition(String dataValue) {
    int result = 0;
    try {

      if (!dataValue.contains("<") && !dataValue.contains(">")) {
        result = Integer.parseInt(dataValue) - 1;
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public static int getIdToSolutionByPosition(String value) {
    int result = 0;
    try {
      result = Integer.parseInt(value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public static double getPercentage(String value) {
    double result = 1.0d;
    if (value.contains("<")) {
      String[] aux = value.split("<=");
      try {
        result = Integer.parseInt(aux[aux.length - 1]) / 100.0;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      String[] aux = value.split(">=");
      try {
        result = 1.0 - (Integer.parseInt(aux[aux.length - 1]) / 100.0);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }

  public static Function<PermutationSolution<Integer>, Double> createSolutionByPositionPriority(
      int index, int value) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariableValue(index) == value
                ? 0.0
                : -1.0 * solution.getVariables().indexOf(value);

    return constraint;
  }

  public static Function<PermutationSolution<Integer>, Double> createSolutionByPositionQuarter(
      int index, double percentage) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariables().indexOf(index) < solution.getNumberOfVariables() * percentage
                ? 0.0
                : -1.0 * solution.getVariables().indexOf(index);

    return constraint;
  }

  public static Function<PermutationSolution<Integer>, Double> createSolutionByPosition(
      int index, int value) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariableValue(index) == value
                ? 0.0
                : -1.0 * Math.abs((index) - solution.getVariables().indexOf(value));

    return constraint;
  }

  public static Function<PermutationSolution<Integer>, Double> createSolutionByRoute(
      int indexFirst, int indexSecond) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariables().indexOf(indexFirst)
                    == (solution.getVariables().indexOf(indexSecond) - 1)
                ? 0.0
                : -1.0
                    * Math.abs(
                        solution.getVariables().indexOf(indexFirst)
                            - solution.getVariables().indexOf(indexSecond));

    return constraint;
  }
}
