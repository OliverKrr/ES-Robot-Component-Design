package edu.kit.expertsystem;

import edu.kit.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MyOWLHelper {

    private static final Logger logger = LogManager.getLogger(MyOWLHelper.class);

    private OWLHelper genericTool;

    private Set<OWLAxiom> generatedAxioms = new HashSet<>();

    MyOWLHelper(OWLGenericTools genericTool) {
        this.genericTool = genericTool;
    }

    public IRI create(String name) {
        return IRI.create("#" + name);
    }

    public boolean addAxiom(OWLAxiom axiomToAdd) {
        if (!generatedAxioms.add(axiomToAdd)) {
            return false;
        }
        genericTool.getManager().addAxiom(genericTool.getOntology(), axiomToAdd);
        return true;
    }

    public void flush() {
        genericTool.getReasoner().flush();
    }

    /**
     * Remove "Ind"
     */
    public String getNameOfOWLNamedIndividual(OWLNamedIndividual ind) {
        return ind.getIRI().getShortForm().substring(0, ind.getIRI().getShortForm().length() - 3);
    }

    /**
     * Remove "isComposedOf"
     */
    public String getNameOfComponent(OWLObjectProperty prop) {
        return prop.getIRI().getShortForm().substring(12, prop.getIRI().getShortForm().length());
    }

    public double parseValueToDouble(OWLLiteral obProp) {
        if (obProp.isInteger()) {
            return obProp.parseInteger();
        }
        return obProp.parseDouble();
    }

    public int parseValueToInteger(OWLLiteral obProp) {
        if (obProp.isInteger()) {
            return obProp.parseInteger();
        }
        return Math.round(obProp.parseFloat());
    }

    public void clearGeneratedAxioms() {
        genericTool.getManager().removeAxioms(genericTool.getOntology(), generatedAxioms.stream());
        generatedAxioms.clear();
    }

    public Set<OWLAxiom> getGeneratedAxioms() {
        return generatedAxioms;
    }

    public int getOrderPositionForClass(OWLClass clas) {
        AtomicInteger value = new AtomicInteger(0);
        genericTool.getOntology().subClassAxiomsForSubClass(clas).forEach(axiom -> axiom.componentsWithoutAnnotations
                ().filter(comp -> comp instanceof OWLDataHasValue && Vocabulary.DATA_PROPERTY_HASORDERPOSITION.equals
                (((OWLDataHasValue) comp).getProperty())).findAny().ifPresent(comp -> value.set(parseValueToInteger((
                        (OWLDataHasValue) comp).getFiller()))));
        return value.get();
    }

    public boolean checkConsistency() {
        boolean isConsitent = genericTool.getReasoner().isConsistent();
        logger.info("Ontology is consistent: " + isConsitent);
        return isConsitent;
    }

}
