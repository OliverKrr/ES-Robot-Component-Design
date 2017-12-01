package edu.kit.expertsystem.reasoning;

import java.util.List;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class ChildInstancesForPermutation {

    public List<OWLNamedIndividual> childInstances;
    public OWLObjectPropertyExpression propertyFromParent;

    public ChildInstancesForPermutation(List<OWLNamedIndividual> childInstances,
            OWLObjectPropertyExpression propertyFromParent) {
        this.childInstances = childInstances;
        this.propertyFromParent = propertyFromParent;
    }

}
