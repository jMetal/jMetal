package org.uma.jmetal.util.semantic;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.PermutationSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OWLUtils {
  static final int GRID_SIZE_X = 20;
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
  private OWLObjectProperty hasCityObject;
  private OWLObjectProperty hasNext;
  private OWLObjectProperty hasLocation;
  private OWLDataProperty hasID;
  private OWLDataProperty isFirst;
  private OWLDataProperty hasDataType;
  private OWLDataProperty hasPositionValue;
  private OWLDataProperty hasX;
  private OWLDataProperty hasY;

  private OWLNamedIndividual newYorkGridConstraint;
  private OWLObjectProperty hasAntenna;
  private OWLObjectProperty avoids;
  private OWLObjectProperty attaches;
  private OWLDataProperty hasMinimumCoverage;

  public OWLUtils(String pathOWL) {
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
            IRI.create(
                "http://www.khaos.uma.es/perception/traffic/khaosteam#hasPositionInPreference"));
    this.hasDataType =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasDataType"));
    this.hasCityObject =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasCityObject"));
    this.hasNext =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasNext"));
    this.hasPositionValue =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPositionValue"));
    this.hasLocation =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasLocation"));
    this.hasID =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasId"));
    this.hasX =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasX"));
    this.hasY =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasY"));
    this.isFirst =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#isFirst"));

    this.newYorkGridConstraint =
        factory.getOWLNamedIndividual(
            IRI.create(
                "http://www.khaos.uma.es/perception/traffic-tsp/khaosteam#NewYorkGridConstraints"));
    this.hasAntenna =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasAntenna"));
    this.avoids =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#avoids"));

    this.attaches =
        factory.getOWLObjectProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#attaches"));

    this.hasMinimumCoverage =
        factory.getOWLDataProperty(
            IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasMinimumCoverage"));
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

  public List<Function<BinarySolution, Double>> getRNDConstraintFromOntology() {
    List<Function<BinarySolution, Double>> result = null;
    if (ontology != null) {
      result = new ArrayList<>();
      NodeSet<OWLNamedIndividual> antennas =
          reasoner.getObjectPropertyValues(newYorkGridConstraint, hasAntenna);

      List<OWLLiteral> minimumCoverageList =
          getDataPropertyByIndividual(reasoner, newYorkGridConstraint, hasMinimumCoverage);
      if (minimumCoverageList != null && !minimumCoverageList.isEmpty()) {
        double coverage = Double.valueOf(minimumCoverageList.get(0).getLiteral());
        result.add(createSolutionByCoverage(coverage));
      }
      if (antennas != null && !antennas.isEmpty()) {
        Iterator<Node<OWLNamedIndividual>> it = antennas.iterator();
        while (it.hasNext()) {
          OWLNamedIndividual antenna = it.next().iterator().next();

          List<OWLNamedIndividual> avoidlist = null;
          List<OWLNamedIndividual> locations = null;
          List<OWLNamedIndividual> attachList = null;
          List<OWLLiteral> x = null;
          List<OWLLiteral> y = null;

          avoidlist = getIndividualsByObjectProperty(reasoner, antenna, avoids);
          locations = getIndividualsByObjectProperty(reasoner, antenna, hasLocation);
          attachList = getIndividualsByObjectProperty(reasoner, antenna, attaches);
          if (avoidlist != null && !avoidlist.isEmpty()) {
            for (OWLNamedIndividual ind : avoidlist) {
              List<OWLNamedIndividual> locationsAux =
                  getIndividualsByObjectProperty(reasoner, ind, hasLocation);
              if (locationsAux != null && !locationsAux.isEmpty()) {
                int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                result.add(createAvoidSolutionByGridPosition(xValue, yValue));
              }
            }
          }
          if (locations != null && !locations.isEmpty()) {
            int xValue = getValueFromLocation(reasoner, locations.get(0), hasX);
            int yValue = getValueFromLocation(reasoner, locations.get(0), hasY);
            result.add(createAttachSolutionByGridPosition(xValue, yValue));
          }
          if (attachList != null && !attachList.isEmpty()) {
            for (OWLNamedIndividual ind : attachList) {
              List<OWLNamedIndividual> locationsAux =
                  getIndividualsByObjectProperty(reasoner, ind, hasLocation);
              if (locationsAux != null && !locationsAux.isEmpty()) {
                int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                result.add(createAttachSolutionByGridPosition(xValue, yValue));
              }
            }
          }
        }
      } // enfif
    }
    return result;
  }

  public List<Function<PermutationSolution<Integer>, Double>> getTSPConstraintFromOntology() {
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
          buildings = getIndividualsByObjectProperty(reasoner, indPos, hasCityObject);
          for (OWLNamedIndividual building : buildings) {
            locations = getIndividualsByObjectProperty(reasoner, building, hasLocation);
            if (locations != null && !locations.isEmpty()) {
              for (OWLNamedIndividual location : locations) {
                x = getDataPropertyByIndividual(reasoner, location, hasX);
                y = getDataPropertyByIndividual(reasoner, location, hasY);
              }
            }

            ids = getDataPropertyByIndividual(reasoner, building, hasID);
          }
          positionValues = getDataPropertyByIndividual(reasoner, indPos, hasPositionValue);
          datatypes = getDataPropertyByIndividual(reasoner, indPos, hasDataType);
        }

        if (positions != null) {
          if (positions.size() > 1) {
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
            result.add(constraint);

          } else {
            // de todos las listas solo va a haber un elemento en cada una de ellas
            for (OWLNamedIndividual indPos : positions) {
              if (positionValues.size() > 0) {
                if (positionValues.get(0).getLiteral().contains("<")
                    || positionValues.get(0).getLiteral().contains(">")
                    || positionValues.get(0).getLiteral().contains("=")) {
                  percentage = getPercentage(positionValues.get(0).getLiteral());
                  index = getIdToSolutionByPosition(ids.get(0).getLiteral());
                  Function<PermutationSolution<Integer>, Double> constraint =
                      createSolutionByPositionQuarter(index, percentage);
                  result.add(constraint);
                } else {
                  index = getIndexToSolutionByPosition(positionValues.get(0).getLiteral());
                  id = getIdToSolutionByPosition(ids.get(0).getLiteral());
                  Function<PermutationSolution<Integer>, Double> constraint =
                      createSolutionByPosition(index, id);
                  result.add(constraint);
                }
              }
            } // end positions
          }
        }
      }
    }
    return result;
  }

  private int getValueFromLocation(
      OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
    int result = 0;
    List<OWLLiteral> aux = getDataPropertyByIndividual(reasoner, individual, dataProperty);
    if (aux != null && !aux.isEmpty()) {
      result = Integer.valueOf(aux.get(0).getLiteral());
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

  private Function<BinarySolution, Double> createSolutionByCoverage(double coverage) {
    Function<BinarySolution, Double> constraint =
        solution ->
            solution.getObjective(1) <= (100 - coverage) ? 0.0 : -1.0 * solution.getObjective(1);

    return constraint;
  }

  private Function<BinarySolution, Double> createAvoidSolutionByGridPosition(int x, int y) {
    int position = x + (y * GRID_SIZE_X);
    Function<BinarySolution, Double> constraint =
        solution -> !solution.getVariableValue(0).get(position) ? 0.0 : -1.0 * Double.MAX_VALUE;

    return constraint;
  }

  private Function<BinarySolution, Double> createAttachSolutionByGridPosition(int x, int y) {
    int position = x + (y * GRID_SIZE_X);
    Function<BinarySolution, Double> constraint =
        solution -> solution.getVariableValue(0).get(position) ? 0.0 : -1.0 * Double.MAX_VALUE;

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
