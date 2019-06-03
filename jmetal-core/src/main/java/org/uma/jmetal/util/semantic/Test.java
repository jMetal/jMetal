package org.uma.jmetal.util.semantic;

import java.io.File;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
    String pathOWL = "/Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/traffic-tsp.owl";
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.khaos.uma.es/perception/traffic/khaosteam"),
        IRI.create("file:///Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/traffic.owl")));
    manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.khaos.uma.es/perception/bigowl"),
        IRI.create("file:///Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/bigowl.owl")));

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
    } catch (Exception ex) {
      ex.printStackTrace();

    }
  }
}

