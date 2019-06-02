package org.uma.jmetal.util.semantic;

import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLUtils {
  private OWLOntologyManager manager;
  private OWLOntology ontology;
  private OWLDataFactory factory;
  private IRI iri;
  public OWLUtils(String pathOWL){
    manager = OWLManager.createOWLOntologyManager();
    factory = manager.getOWLDataFactory();
    try{
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);
      if(ontology.getOntologyID().getOntologyIRI().isPresent()){
        iri = ontology.getOntologyID().getOntologyIRI().get();
      }
      System.out.println("Leida ontologia "+ pathOWL);
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }


}
