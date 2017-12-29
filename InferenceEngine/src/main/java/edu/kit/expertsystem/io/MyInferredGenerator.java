package edu.kit.expertsystem.io;

import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;

public abstract class MyInferredGenerator<T extends OWLIndividualAxiom> extends InferredIndividualAxiomGenerator<T> {

    public abstract void reset();

    public abstract void stop();

}
