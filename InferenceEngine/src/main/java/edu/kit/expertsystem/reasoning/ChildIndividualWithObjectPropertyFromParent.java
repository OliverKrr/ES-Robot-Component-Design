package edu.kit.expertsystem.reasoning;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class ChildIndividualWithObjectPropertyFromParent {

    public OWLNamedIndividual childIndividual;
    public OWLObjectPropertyExpression propertyFromParent;

    public ChildIndividualWithObjectPropertyFromParent(OWLNamedIndividual childIndividual,
            OWLObjectPropertyExpression propertyFromParent) {
        this.childIndividual = childIndividual;
        this.propertyFromParent = propertyFromParent;
    }

}
