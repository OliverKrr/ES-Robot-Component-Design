package edu.ontology.sac.generated.impl;

import edu.ontology.sac.generated.*;


import java.net.URI;
import java.util.Collection;
import javax.xml.datatype.XMLGregorianCalendar;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultBrushlessDcMotor <br>
 * @version generated on Sat Oct 07 11:11:09 CEST 2017 by Oliver
 */
public class DefaultBrushlessDcMotor extends WrappedIndividualImpl implements BrushlessDcMotor {

    public DefaultBrushlessDcMotor(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV4#isComposedOf
     */
     
    public Collection<? extends Device> getIsComposedOf() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                                               DefaultDevice.class);
    }

    public boolean hasIsComposedOf() {
	   return !getIsComposedOf().isEmpty();
    }

    public void addIsComposedOf(Device newIsComposedOf) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                                       newIsComposedOf);
    }

    public void removeIsComposedOf(Device oldIsComposedOf) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                                          oldIsComposedOf);
    }


}
