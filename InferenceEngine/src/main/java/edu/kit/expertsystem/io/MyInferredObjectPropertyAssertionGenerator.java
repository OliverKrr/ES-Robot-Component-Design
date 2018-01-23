package edu.kit.expertsystem.io;

import edu.kit.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.semanticweb.owlapi.model.parameters.Imports.INCLUDED;

public class MyInferredObjectPropertyAssertionGenerator extends MyInferredGenerator<OWLPropertyAssertionAxiom<?, ?>> {

    private OWLGenericTools genericTool;
    private AtomicBoolean isStopped = new AtomicBoolean(false);

    MyInferredObjectPropertyAssertionGenerator(OWLGenericTools genericTool) {
        this.genericTool = genericTool;
    }

    @Override
    protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory,
                             Set<OWLPropertyAssertionAxiom<?, ?>> result) {
        if (isStopped.get()) {
            return;
        }
        genericTool.getOntology().objectPropertiesInSignature(INCLUDED)
                // take only data props which have no other children -> filer unnecessary
                .filter(p -> genericTool.getOntology().objectSubPropertyAxiomsForSuperProperty(p).count() == 0 &&
                        !Vocabulary.OBJECT_PROPERTY_HASREQUIREMENT.equals(p)).forEach(p -> reasoner
                .objectPropertyValues(entity, p).forEach(v -> result.add(dataFactory
                        .getOWLObjectPropertyAssertionAxiom(p, entity, v))));
    }

    @Override
    public String getLabel() {
        return "My object property assertions (property values)";
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