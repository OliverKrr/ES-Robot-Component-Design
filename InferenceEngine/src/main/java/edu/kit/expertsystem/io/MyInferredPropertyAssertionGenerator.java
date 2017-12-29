package edu.kit.expertsystem.io;

import openllet.owlapi.OWLGenericTools;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.semanticweb.owlapi.model.parameters.Imports.INCLUDED;

public class MyInferredPropertyAssertionGenerator extends MyInferredGenerator<OWLPropertyAssertionAxiom<?, ?>> {

    private OWLGenericTools genericTool;
    private AtomicBoolean isStopped = new AtomicBoolean(false);

    public MyInferredPropertyAssertionGenerator(OWLGenericTools genericTool) {
        this.genericTool = genericTool;
    }

    @Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                             Set<OWLPropertyAssertionAxiom<?, ?>> result) {
        if (isStopped.get()) {
            return;
        }
        genericTool.getOntology().dataPropertiesInSignature(INCLUDED)
                // take only data props which have no other children -> filer unnecessary
                .filter(p -> genericTool.getOntology().dataSubPropertyAxiomsForSuperProperty(p).count() == 0).forEach
                (p -> reasoner.getDataPropertyValues(entity, p).forEach(v -> result.add(dataFactory
                        .getOWLDataPropertyAssertionAxiom(p, entity, v))));
    }

    @Override
    public String getLabel() {
        return "My property assertions (property values)";
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