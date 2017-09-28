package edu.ontology.sac.generated;

import java.util.Collection;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: ILM70x18 <br>
 * @version generated on Thu Sep 28 16:14:39 CEST 2017 by Oliver
 */

public interface ILM70x18 extends RoboDriveServoKibILM {

    /* ***************************************************
     * Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#isComposedOf
     */
     
    /**
     * Gets all property values for the isComposedOf property.<p>
     * 
     * @returns a collection of values for the isComposedOf property.
     */
    Collection<? extends Device> getIsComposedOf();

    /**
     * Checks if the class has a isComposedOf property value.<p>
     * 
     * @return true if there is a isComposedOf property value.
     */
    boolean hasIsComposedOf();

    /**
     * Adds a isComposedOf property value.<p>
     * 
     * @param newIsComposedOf the isComposedOf property value to be added
     */
    void addIsComposedOf(Device newIsComposedOf);

    /**
     * Removes a isComposedOf property value.<p>
     * 
     * @param oldIsComposedOf the isComposedOf property value to be removed.
     */
    void removeIsComposedOf(Device oldIsComposedOf);


    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
