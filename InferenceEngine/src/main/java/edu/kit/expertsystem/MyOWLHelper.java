package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import edu.kit.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLHelper;
import openllet.owlapi.OpenlletReasoner;

public class MyOWLHelper {

    private static final Logger logger = LogManager.getLogger(MyOWLHelper.class);

    private OWLHelper genericTool;

    private Set<OWLAxiom> generatedAxioms = new HashSet<>();
    private List<OWLOntologyChange> currentChanges = new ArrayList<>();

    public MyOWLHelper(OWLGenericTools genericTool) {
        this.genericTool = genericTool;
        genericTool.getManager().addOntologyChangeListener(changes -> currentChanges.addAll(changes));
    }

    public IRI create(String name) {
        return IRI.create("#" + name);
    }

    public void addAxiom(OWLAxiom axiomToAdd) {
        generatedAxioms.add(axiomToAdd);
        genericTool.getManager().addAxiom(genericTool.getOntology(), axiomToAdd);
    }

    public void flush() {
        flush(false);
    }

    public void flush(boolean forceFlush) {
        boolean processChangesSuccess = ((OpenlletReasoner) genericTool.getReasoner())
                .processChanges(currentChanges);
        logger.info("Changes process success: " + processChangesSuccess);
        if (!processChangesSuccess || forceFlush) {
            genericTool.getReasoner().flush();
        }
        currentChanges.clear();
    }

    /**
     * Remove "Ind"
     * 
     * @param ind
     * @return
     */
    public String getNameOfOWLNamedIndividual(OWLNamedIndividual ind) {
        return ind.getIRI().getShortForm().substring(0, ind.getIRI().getShortForm().length() - 3);
    }

    public void clearGeneratedAxioms() {
        genericTool.getManager().removeAxioms(genericTool.getOntology(), generatedAxioms.stream());
        generatedAxioms.clear();
    }

    public void deleteInstance(Stream<OWLClass> subClassesOfUnsatisfied) {
        List<InformationToDelete> informationToDelete = new ArrayList<>();
        Set<OWLAxiom> axiomsToDelete = new HashSet<>();

        subClassesOfUnsatisfied.forEach(subClas -> {
            int oldSize = axiomsToDelete.size();
            genericTool.getReasoner().instances(subClas)
                    .forEach(indiToDelete -> generatedAxioms.stream()
                            .filter(axiom -> axiom.individualsInSignature()
                                    .anyMatch(indi -> indi.equals(indiToDelete)))
                            .forEach(axiom -> axiomsToDelete.add(axiom)));

            if (oldSize != axiomsToDelete.size()) {
                InformationToDelete infToDelete = new InformationToDelete();
                genericTool.getOntology().subClassAxiomsForSubClass(subClas).forEach(topAxiom -> {
                    if (topAxiom.getSuperClass().objectPropertiesInSignature()
                            .anyMatch(ob -> Vocabulary.OBJECT_PROPERTY_HASCOUNTERSATISFIEDPART.equals(ob))) {
                        topAxiom.getSuperClass().classesInSignature()
                                .collect(Collectors.toCollection(() -> infToDelete.counterSatisfiedPart));
                    } else if (topAxiom.getSuperClass().objectPropertiesInSignature()
                            .anyMatch(ob -> Vocabulary.OBJECT_PROPERTY_HASNEWSATISFIEDPART.equals(ob))) {
                        topAxiom.getSuperClass().classesInSignature()
                                .collect(Collectors.toCollection(() -> infToDelete.newSatisfiedPart));
                    }
                });
                informationToDelete.add(infToDelete);
            }
        });

        if (!axiomsToDelete.isEmpty()) {
            genericTool.getManager().removeAxioms(genericTool.getOntology(), axiomsToDelete.stream());
            generatedAxioms.removeAll(axiomsToDelete);
            flush();

            for (InformationToDelete infoDelete : informationToDelete) {
                infoDelete.counterSatisfiedPart
                        .forEach(counter -> infoDelete.newSatisfiedPart.forEach(newPart -> genericTool
                                .getReasoner().instances(counter).forEach(counterInst -> addAxiom(genericTool
                                        .getFactory().getOWLClassAssertionAxiom(newPart, counterInst)))));
            }
            flush();
        }
    }

    private class InformationToDelete {
        public List<OWLClass> counterSatisfiedPart = new ArrayList<>();
        public List<OWLClass> newSatisfiedPart = new ArrayList<>();
    }

}
