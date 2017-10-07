package edu.ontology.sac.generated;

import java.net.URI;
import java.util.Collection;
import javax.xml.datatype.XMLGregorianCalendar;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: Motor <br>
 * @version generated on Sat Oct 07 11:11:10 CEST 2017 by Oliver
 */

public interface Motor extends Basic {

    /* ***************************************************
     * Property http://www.semanticweb.org/oliver/ontologies/sac/prototypeV4#isComposedOf
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
