package edu.ontology.sac.generated.impl;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.semanticweb.owlapi.model.IRI;

import edu.ontology.sac.generated.RotationSpeed;
import edu.ontology.sac.generated.Vocabulary;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultRotationSpeed <br>
 * @version generated on Thu Sep 28 16:14:40 CEST 2017 by Oliver
 */
public class DefaultRotationSpeed extends WrappedIndividualImpl implements RotationSpeed {

    public DefaultRotationSpeed(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#isInputOf
     */
     
    public Collection<? extends WrappedIndividual> getIsInputOf() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISINPUTOF,
                                               WrappedIndividualImpl.class);
    }

    public boolean hasIsInputOf() {
	   return !getIsInputOf().isEmpty();
    }

    public void addIsInputOf(WrappedIndividual newIsInputOf) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISINPUTOF,
                                       newIsInputOf);
    }

    public void removeIsInputOf(WrappedIndividual oldIsInputOf) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISINPUTOF,
                                          oldIsInputOf);
    }


    /* ***************************************************
     * Object Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#isOutputOf
     */
     
    public Collection<? extends WrappedIndividual> getIsOutputOf() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISOUTPUTOF,
                                               WrappedIndividualImpl.class);
    }

    public boolean hasIsOutputOf() {
	   return !getIsOutputOf().isEmpty();
    }

    public void addIsOutputOf(WrappedIndividual newIsOutputOf) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISOUTPUTOF,
                                       newIsOutputOf);
    }

    public void removeIsOutputOf(WrappedIndividual oldIsOutputOf) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISOUTPUTOF,
                                          oldIsOutputOf);
    }


}
