package org.uma.jmetal.util.semantic;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
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

public class OWLUtils_old {
  private OWLOntologyManager manager;
  private OWLOntology ontology;
  private OWLDataFactory factory;
  private IRI iri;
  private String pathOWL;
  private OWLReasoner reasoner;
  private OWLReasonerFactory reasonerFactory;

  private OWLNamedIndividual deliveryRoute;
  private OWLObjectProperty hasPreference;
  private OWLObjectProperty hasPosition;
  private OWLObjectProperty hasDataType;
  private OWLObjectProperty containsNode;
  private OWLObjectProperty successor;
  private OWLDataProperty hasDataValue;
  private OWLDataProperty isPriority;
  private OWLDataProperty hasID;
  private OWLDataProperty isFirst;

  public OWLUtils_old(String pathOWL) {
    this.manager = OWLManager.createOWLOntologyManager();
    this.factory = manager.getOWLDataFactory();
    this.pathOWL = pathOWL;
    this.ontology = null;
    this.reasonerFactory = new StructuralReasonerFactory();
    this.deliveryRoute =
        factory.getOWLNamedIndividual(
            IRI.create("http://www.khaos.uma.es/perception/traffic-tsp/khaosteam#DeliveryRoute"));
    this.hasPreference =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPreference"));
    this.hasPosition =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPosition"));
    this.hasDataType =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/bigowl#hasDataTypePrimitive"));
    this.containsNode =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#containsNode"));
    this.successor =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#successor"));
    this.hasDataValue =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/bigowl#hasDataValue"));
    this.isPriority =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#isPriority"));
    this.hasID =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasID"));
    this.isFirst =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#isFirst"));
  }

  public void addImport(String pathFile, String uri) {
    manager
        .getIRIMappers()
        .add(new SimpleIRIMapper(IRI.create(uri), IRI.create("file://" + pathFile)));
  }

  public void loadOntology() {
    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);
      reasoner = reasonerFactory.createReasoner(ontology);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public List<Function<PermutationSolution<Integer>, Double>> getConstraintFromOntology() {
    List<Function<PermutationSolution<Integer>, Double>> result = null;
    if (ontology != null) {
      result = new ArrayList<>();

      NodeSet<OWLNamedIndividual> preferences =
          reasoner.getObjectPropertyValues(deliveryRoute, hasPreference);

      Iterator<Node<OWLNamedIndividual>> it = preferences.iterator();
      while (it.hasNext()) {
        OWLNamedIndividual nodePreference = it.next().iterator().next();
        List<OWLNamedIndividual> positions =
            getIndividualsByObjectProperty(reasoner, nodePreference, hasPosition);

        boolean quarter = false;
        List<OWLNamedIndividual> contains = null;
        List<OWLNamedIndividual> successors = null;
        List<OWLLiteral> priority = null;
        List<OWLLiteral> ids = null;
        List<OWLNamedIndividual> datatypes = null;
        List<OWLLiteral> values = null;
        double percentage = 1.0d;
        int index = 0;
        int id = 0;
        for (OWLNamedIndividual indPos : positions) {
          contains = getIndividualsByObjectProperty(reasoner, indPos, containsNode);
          for (OWLNamedIndividual contain : contains) {
            priority = getDataPropertyByIndividual(reasoner, contain, isPriority);

            ids = getDataPropertyByIndividual(reasoner, contain, hasID);
          }
          successors = getIndividualsByObjectProperty(reasoner, indPos, successor);
          datatypes = getIndividualsByObjectProperty(reasoner, indPos, hasDataType);

          values = getDataPropertyByIndividual(reasoner, indPos, hasDataValue);
        }

        if (positions != null) {
          if (positions.size() > 1) {

            OWLNamedIndividual first = getFirst(reasoner, positions, isFirst);
            List<OWLNamedIndividual> routes = getNext(reasoner, first, successor);

            List<OWLNamedIndividual> containsFirst =
                getIndividualsByObjectProperty(reasoner, routes.get(0), containsNode);
            List<OWLLiteral> idFirst =
                getDataPropertyByIndividual(reasoner, containsFirst.get(0), hasID);

            List<OWLNamedIndividual> containsSecond =
                getIndividualsByObjectProperty(reasoner, routes.get(1), containsNode);
            List<OWLLiteral> idSecond =
                getDataPropertyByIndividual(reasoner, containsSecond.get(0), hasID);

            int indexFirst = getIdToSolutionByPosition(idFirst.get(0).getLiteral());
            int indexSecond = getIdToSolutionByPosition(idSecond.get(0).getLiteral());

            Function<PermutationSolution<Integer>, Double> constraint =
                createSolutionByRoute(indexFirst, indexSecond);
            result.add(constraint);

          } else {
            // de todos las listas solo va a haber un elemento en cada una de ellas

            if (values.size() > 0) {
              if (values.get(0).getLiteral().contains("<")
                  || values.get(0).getLiteral().contains(">")
                  || values.get(0).getLiteral().contains("=")) {
                quarter = true;
                percentage = getPercentage(values.get(0).getLiteral());
                index = getIdToSolutionByPosition(ids.get(0).getLiteral());
                Function<PermutationSolution<Integer>, Double> constraint =
                    createSolutionByPositionQuarter(index, percentage);
                result.add(constraint);
              } else {
                index = getIndexToSolutionByPosition(values.get(0).getLiteral());
                id = getIdToSolutionByPosition(ids.get(0).getLiteral());
                if (priority.size() > 0) {
                  Function<PermutationSolution<Integer>, Double> constraint =
                      createSolutionByPositionPriority(index, id);
                  result.add(constraint);
                } else {
                  Function<PermutationSolution<Integer>, Double> constraint =
                      createSolutionByPosition(index, id);
                  result.add(constraint);
                }
              }
            }
          }
        }
      }
    }
    return result;
  }

  private OWLNamedIndividual getFirst(
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

  private List<OWLNamedIndividual> getNext(
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

  private List<OWLNamedIndividual> getIndividualsByObjectProperty(
      OWLReasoner reasoner, OWLNamedIndividual individual, OWLObjectProperty objectProperty) {
    List<OWLNamedIndividual> result = new ArrayList<>();
    NodeSet<OWLNamedIndividual> nodes =
        reasoner.getObjectPropertyValues(individual, objectProperty);
    if (nodes != null) {
      nodes.forEach(nodeIndiv -> nodeIndiv.forEach(indiv -> result.add(indiv)));
    }
    return result;
  }

  private List<OWLLiteral> getDataPropertyByIndividual(
      OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
    List<OWLLiteral> result = new ArrayList<>();
    Set<OWLLiteral> nodes = reasoner.getDataPropertyValues(individual, dataProperty);
    if (nodes != null) {
      nodes.stream().forEach(owlLiteral -> result.add(owlLiteral));
    }
    return result;
  }

  private int getIndexToSolutionByPosition(String dataValue) {
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

  private int getIdToSolutionByPosition(String value) {
    int result = 0;
    try {
      result = Integer.parseInt(value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  private double getPercentage(String value) {
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

  private Function<PermutationSolution<Integer>, Double> createSolutionByPositionPriority(
      int index, int value) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariableValue(index) == value
                ? 0.0
                : -1.0 * solution.getVariables().indexOf(value);

    return constraint;
  }

  private Function<PermutationSolution<Integer>, Double> createSolutionByPositionQuarter(
      int index, double percentage) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariables().indexOf(index) < solution.getNumberOfVariables() * percentage
                ? 0.0
                : -1.0 * solution.getVariables().indexOf(index);

    return constraint;
  }

  private Function<PermutationSolution<Integer>, Double> createSolutionByPosition(
      int index, int value) {
    Function<PermutationSolution<Integer>, Double> constraint =
        solution ->
            solution.getVariableValue(index) == value
                ? 0.0
                : -1.0 * Math.abs((index) - solution.getVariables().indexOf(value));

    return constraint;
  }

  private Function<PermutationSolution<Integer>, Double> createSolutionByRoute(
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
