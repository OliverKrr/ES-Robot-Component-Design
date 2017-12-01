package edu.kit.expertsystem.io;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class MyInferredClassAssertionAxiomGenerator extends MyInferredGenerator<OWLClassAssertionAxiom> {

    private AtomicBoolean isStopped = new AtomicBoolean(false);

    @Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
            Set<OWLClassAssertionAxiom> result) {
        if (isStopped.get()) {
            return;
        }
        reasoner.types(entity, true)
                .forEach(t -> result.add(dataFactory.getOWLClassAssertionAxiom(t, entity)));
    }

    @Override
    public String getLabel() {
        return "My class assertions (individual types)";
    }

    @Override
    public void reset() {
        isStopped.set(false);
    }

    @Override
    public void stop() {
        isStopped.set(true);
    }
}
