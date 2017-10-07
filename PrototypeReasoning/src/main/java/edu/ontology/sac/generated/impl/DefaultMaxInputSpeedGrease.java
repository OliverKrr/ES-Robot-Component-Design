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
 * Source Class: DefaultMaxInputSpeedGrease <br>
 * @version generated on Sat Oct 07 11:11:10 CEST 2017 by Oliver
 */
public class DefaultMaxInputSpeedGrease extends WrappedIndividualImpl implements MaxInputSpeedGrease {

    public DefaultMaxInputSpeedGrease(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV4#isPropertyOf
     */
     
    public Collection<? extends WrappedIndividual> getIsPropertyOf() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISPROPERTYOF,
                                               WrappedIndividualImpl.class);
    }

    public boolean hasIsPropertyOf() {
	   return !getIsPropertyOf().isEmpty();
    }

    public void addIsPropertyOf(WrappedIndividual newIsPropertyOf) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISPROPERTYOF,
                                       newIsPropertyOf);
    }

    public void removeIsPropertyOf(WrappedIndividual oldIsPropertyOf) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISPROPERTYOF,
                                          oldIsPropertyOf);
    }


}
