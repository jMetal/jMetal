package org.uma.jmetal.util.semantic;

import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class Test {

  public static void main(String[] args) {
    OWLOntologyManager manager;
    OWLOntology ontology;
    OWLDataFactory factory;
    IRI iri;
    String pathOWL = "/Users/cbarba/Documents/proyectos/jMetal/jmetal-core/src/main/resources/ontology/traffic-tsp.owl";
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    manager.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://example.com/myontology"),
        IRI.create("file:///actual/path/to/file.owl")));

    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);

      if (ontology.getOntologyID().getOntologyIRI().isPresent()) {
        iri = ontology.getOntologyID().getOntologyIRI().get();


      }
      System.out.println("Leida ontologia " + pathOWL);
    } catch (Exception ex) {
      ex.printStackTrace();

    }
  }
}

