package edu.ontology.sac.generated;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: Input <br>
 * @version generated on Thu Sep 28 16:14:39 CEST 2017 by Oliver
 */

public interface Input extends InputOutput {

    /* ***************************************************
     * Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#isInputOf
     */
     
    /**
     * Gets all property values for the isInputOf property.<p>
     * 
     * @returns a collection of values for the isInputOf property.
     */
    Collection<? extends WrappedIndividual> getIsInputOf();

    /**
     * Checks if the class has a isInputOf property value.<p>
     * 
     * @return true if there is a isInputOf property value.
     */
    boolean hasIsInputOf();

    /**
     * Adds a isInputOf property value.<p>
     * 
     * @param newIsInputOf the isInputOf property value to be added
     */
    void addIsInputOf(WrappedIndividual newIsInputOf);

    /**
     * Removes a isInputOf property value.<p>
     * 
     * @param oldIsInputOf the isInputOf property value to be removed.
     */
    void removeIsInputOf(WrappedIndividual oldIsInputOf);


    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
