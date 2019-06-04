package org.uma.jmetal.util.semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class Test {

  public static void main(String[] args) {
    OWLOntologyManager manager;
    OWLOntology ontology;
    OWLDataFactory factory;
    IRI iri=null;
    String pathOWL = "/home/cbarba/Documents/projectIdea/jmetal/jmetal-core/src/main/resources/ontology/traffic-tsp.owl";
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam"),
        IRI.create("file:///home/cbarba/Documents/projectIdea/jmetal/jmetal-core/src/main/resources/ontology/traffic.owl")));
    manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.khaos.uma.es/perception/bigowl"),
        IRI.create("file:///home/cbarba/Documents/projectIdea/jmetal/jmetal-core/src/main/resources/ontology/bigowl.owl")));

    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);

      if (ontology.getOntologyID().getOntologyIRI().isPresent()) {
        iri = ontology.getOntologyID().getOntologyIRI().get();
      }
      System.out.println("Leida ontologia " + iri.getIRIString());

      //ontology.individualsInSignature().forEach(individual -> System.out.println(individual.getIRI()));
      OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
      OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

      OWLClass route = factory.getOWLClass(IRI
          .create("http://www.khaos.uma.es/perception/traffic/khaosteam#Route"));
      // Ask the reasoner for the instances of pet
      NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(route,
          true);
      // The reasoner returns a NodeSet again. This time the NodeSet contains
      // individuals. Again, we just want the individuals, so get a flattened
      // set.
      Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
      System.out.println("Instances of route: ");
      for (OWLNamedIndividual ind : individuals) {
        System.out.println("    " + ind);
      }

      OWLNamedIndividual deliveryRoute = factory.getOWLNamedIndividual(IRI.create("http://www.khaos.uma.es/perception/traffic-tsp/khaosteam#DeliveryRoute"));
      OWLObjectProperty hasPreference = factory.getOWLObjectProperty(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPreference"));
      OWLObjectProperty hasPosition = factory.getOWLObjectProperty(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#hasPosition"));
      OWLObjectProperty hasDataType = factory.getOWLObjectProperty(IRI.create("http://www.khaos.uma.es/perception/bigowl#hasDataType"));
      OWLObjectProperty containsNode = factory.getOWLObjectProperty(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#containsNode"));
      OWLObjectProperty successor = factory.getOWLObjectProperty(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#successor"));
      OWLDataProperty hasDataValue = factory.getOWLDataProperty(IRI.create("http://www.khaos.uma.es/perception/bigowl#hasDataValue"));
      OWLDataProperty isPriority = factory.getOWLDataProperty(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam#isPriority"));

      NodeSet<OWLNamedIndividual> preferences= reasoner.getObjectPropertyValues(deliveryRoute,hasPreference);
      preferences.forEach(nodePreference ->{
        nodePreference.forEach(preference-> {
          System.out.println("Preference  "+preference);
          NodeSet<OWLNamedIndividual> positions= reasoner.getObjectPropertyValues(preference,hasPosition);
          positions.forEach(nodePositions -> System.out.println(nodePositions + "    --- ----   "+positions.entities().count()));
        });
      });

      preferences.forEach(
          nodePreference -> {
            List<OWLNamedIndividual> positions =
                getIndividualsByObjectProperty(
                    reasoner, nodePreference.iterator().next(), hasPosition);
            if (positions != null) {
              System.out.println(positions.size());
             if (positions.size() > 1) {

              //subroute + de 1 position

             } else {

                for (OWLNamedIndividual indPos : positions) {
                  System.out.println(indPos);

                  List<OWLNamedIndividual> contains =
                      getIndividualsByObjectProperty(reasoner, indPos, containsNode);
                  for (OWLNamedIndividual contain : contains) {
                    // System.out.println("Contains "+contain);

                    List<OWLLiteral> priority =
                        getDataPropertyByIndividual(reasoner, contain, isPriority);
                    // if (priority.size() > 0) //preference
                    System.out.println("Contains " + contain + " isPriority " + priority.size());
                  }
                  List<OWLNamedIndividual> datatypes =
                      getIndividualsByObjectProperty(reasoner, indPos, hasDataType);
                  for (OWLNamedIndividual dt : datatypes) {
                    //System.out.println("DataTypes " + dt.toString().contains("Integer"));

                  }

                  List<OWLLiteral> values = getDataPropertyByIndividual(reasoner,indPos,hasDataValue);
                  for (OWLLiteral value: values){
                    System.out.println("Literal : " +value.getLiteral());
                  }


                }//end positions


              }
            }
          });

    } catch (Exception ex) {
      ex.printStackTrace();

    }
  }
  public static List<OWLNamedIndividual> getIndividualsByObjectProperty(OWLReasoner reasoner,OWLNamedIndividual individual, OWLObjectProperty objectProperty){
    List<OWLNamedIndividual> result = new ArrayList<>();
    NodeSet<OWLNamedIndividual> nodes = reasoner.getObjectPropertyValues(individual,objectProperty);
    if(nodes!=null){
      nodes.forEach(nodeIndiv -> nodeIndiv.forEach(indiv->result.add(indiv)));
    }
    return result;
  }

  public static List<OWLLiteral> getDataPropertyByIndividual(OWLReasoner reasoner,OWLNamedIndividual individual,OWLDataProperty dataProperty){
    List<OWLLiteral> result = new ArrayList<>();
    Set<OWLLiteral> nodes = reasoner.getDataPropertyValues(individual,dataProperty);
    if(nodes!=null){
      nodes.stream().forEach(owlLiteral -> result.add(owlLiteral));
    }
    return result;
  }
}

