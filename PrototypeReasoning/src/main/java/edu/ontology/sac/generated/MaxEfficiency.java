package edu.ontology.sac.generated;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: MaxEfficiency <br>
 * @version generated on Thu Sep 28 16:14:39 CEST 2017 by Oliver
 */

public interface MaxEfficiency extends Property {

    /* ***************************************************
     * Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#isPropertyOf
     */
     
    /**
     * Gets all property values for the isPropertyOf property.<p>
     * 
     * @returns a collection of values for the isPropertyOf property.
     */
    Collection<? extends WrappedIndividual> getIsPropertyOf();

    /**
     * Checks if the class has a isPropertyOf property value.<p>
     * 
     * @return true if there is a isPropertyOf property value.
     */
    boolean hasIsPropertyOf();

    /**
     * Adds a isPropertyOf property value.<p>
     * 
     * @param newIsPropertyOf the isPropertyOf property value to be added
     */
    void addIsPropertyOf(WrappedIndividual newIsPropertyOf);

    /**
     * Removes a isPropertyOf property value.<p>
     * 
     * @param oldIsPropertyOf the isPropertyOf property value to be removed.
     */
    void removeIsPropertyOf(WrappedIndividual oldIsPropertyOf);


    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
