package edu.kit.expertsystem.reasoning;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.List;

public class ChildInstancesForPermutation {

    public List<OWLNamedIndividual> childInstances;
    public OWLObjectPropertyExpression propertyFromParent;

    public ChildInstancesForPermutation(List<OWLNamedIndividual> childInstances,
                                        OWLObjectPropertyExpression propertyFromParent) {
        this.childInstances = childInstances;
        this.propertyFromParent = propertyFromParent;
    }

}
