package org.uma.jmetal.util.semantic;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestRND {

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

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        OWLClass gridConstraint =
                factory.getOWLClass(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#GridConstraint"));

        OWLNamedIndividual newYorkGridConstraint =
                factory.getOWLNamedIndividual(
                        IRI.create("http://www.khaos.uma.es/perception/traffic-tsp/khaosteam#NewYorkGridConstraints"));
        OWLObjectProperty hasAntenna =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasAntenna"));
        OWLObjectProperty avoids =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#avoids"));
        OWLObjectProperty hasLocation =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasLocation"));
        OWLObjectProperty attaches =
                factory.getOWLObjectProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#attaches"));
        OWLDataProperty hasX =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasX"));
        OWLDataProperty hasY =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasY"));
        OWLDataProperty hasMinimumCoverage =
                factory.getOWLDataProperty(
                        IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasMinimumCoverage"));
        NodeSet<OWLNamedIndividual> antennas =
                reasoner.getObjectPropertyValues(newYorkGridConstraint, hasAntenna);

        List<OWLLiteral> minimumCoverageList= getDataPropertyByIndividual(reasoner, newYorkGridConstraint, hasMinimumCoverage);
        if(minimumCoverageList!=null && !minimumCoverageList.isEmpty()){
            double coverage = Double.valueOf(minimumCoverageList.get(0).getLiteral());
            int xx =0;
        }
      antennas.forEach(
          antenna -> {
            List<OWLNamedIndividual> avoidlist = null;
            List<OWLNamedIndividual> locations = null;
            List<OWLNamedIndividual> attachList = null;
            List<OWLLiteral> x = null;
            List<OWLLiteral> y = null;

            avoidlist = getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), avoids);
            locations =
                getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), hasLocation);
            attachList =
                getIndividualsByObjectProperty(reasoner, antenna.iterator().next(), attaches);
            if (avoidlist != null && !avoidlist.isEmpty()) {
              for (OWLNamedIndividual ind : avoidlist) {
                List<OWLNamedIndividual> locationsAux =
                    getIndividualsByObjectProperty(reasoner, ind, hasLocation);
                if (locationsAux != null && !locationsAux.isEmpty()) {
                  int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                  int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                  int xx = 0;
                }
              }
            }
            if (locations != null && !locations.isEmpty()) {
              int xValue = getValueFromLocation(reasoner, locations.get(0), hasX);
              int yValue = getValueFromLocation(reasoner, locations.get(0), hasY);
                int xx = 0;
            }
            if (attachList != null && !attachList.isEmpty()) {
              for (OWLNamedIndividual ind : attachList) {
                List<OWLNamedIndividual> locationsAux =
                    getIndividualsByObjectProperty(reasoner, ind, hasLocation);
                if (locationsAux != null && !locationsAux.isEmpty()) {
                  int xValue = getValueFromLocation(reasoner, locationsAux.get(0), hasX);
                  int yValue = getValueFromLocation(reasoner, locationsAux.get(0), hasY);
                  int xx = 0;
                }
              }
            }

            int xx = 0;
          });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static int getValueFromLocation(OWLReasoner reasoner,OWLNamedIndividual individual,OWLDataProperty dataProperty){
      int result = 0;
      List<OWLLiteral> aux = getDataPropertyByIndividual(reasoner,individual,dataProperty);
      if(aux!=null && !aux.isEmpty()){
          result= Integer.valueOf(aux.get(0).getLiteral());
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
}
