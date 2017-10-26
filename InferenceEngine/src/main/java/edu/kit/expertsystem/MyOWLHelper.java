package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class MyOWLHelper {

    private OWLOntologyManager manager;
    private OWLOntology ontology;

    private List<OWLAxiom> generatedAxioms = new ArrayList<>();

    public MyOWLHelper(OWLOntologyManager manager, OWLOntology ontology) {
        this.manager = manager;
        this.ontology = ontology;
    }

    public IRI create(String name) {
        return IRI.create("#" + name);
    }

    public void addAxiom(OWLAxiom axiomToAdd) {
        generatedAxioms.add(axiomToAdd);
        manager.addAxiom(ontology, axiomToAdd);
    }

    /**
     * Remove "Ind"
     * 
     * @param ind
     * @return
     */
    public String getNameOfOWLNamedIndividual(OWLNamedIndividual ind) {
        return ind.getIRI().getShortForm().substring(0, ind.getIRI().getShortForm().length() - 3);
    }

    public void clearGeneratedAxioms() {
        ontology.removeAxioms(generatedAxioms);
        generatedAxioms.clear();
    }

}
