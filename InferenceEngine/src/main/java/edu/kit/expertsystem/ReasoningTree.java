package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import edu.kit.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;

public class ReasoningTree {

    private static final Logger logger = LogManager.getLogger(ReasoningTree.class);

    private OWLGenericTools genericTool;
    private MyOWLHelper helper;

    private OWLClass currentClassToReason;
    private ConcurrentSkipListSet<OWLClass> appliedClasses = new ConcurrentSkipListSet<>();
    private AtomicBoolean hasSomethingChanged = new AtomicBoolean();

    public ReasoningTree(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
    }

    public void makeReasoning(OWLClass classToReason) {
        currentClassToReason = classToReason;
        appliedClasses.clear();

        do {
            hasSomethingChanged.set(false);
            genericTool.getReasoner().subClasses(Vocabulary.CLASS_REASONINGTREE, true)
                    .forEach(treeClass -> handleTreeItem(treeClass));
            if (hasSomethingChanged.get()) {
                helper.flush();
            }
            helper.deleteInstance(genericTool.getReasoner().instances(Vocabulary.CLASS_UNSATISFIED));
        } while (hasSomethingChanged.get() && !appliedClasses.contains(currentClassToReason));
    }

    private void handleTreeItem(OWLClass treeClass) {
        // TODO eigentlich nicht hier abbrechen, sondern schauen ob sich Anzahl
        // Permutationen geändert hat
        if (appliedClasses.contains(treeClass) || appliedClasses.contains(currentClassToReason)) {
            return;
        }

        List<ChildInstancesForPermutation> childrenForPermutation = getChildrenForPermutation(treeClass);
        childrenForPermutation.stream()
                .forEach(childForPermutation -> logger.info(treeClass.getIRI().getShortForm() + " has "
                        + childForPermutation.propertyFromParent.getNamedProperty().getIRI().getShortForm()
                        + " with number of children: " + childForPermutation.childInstances.size()));
        int numberOfPermutations = getNumberOfPermutations(childrenForPermutation);

        if (numberOfPermutations > 0) {
            logger.info(
                    "Add " + numberOfPermutations + " individuals for: " + treeClass.getIRI().getShortForm());
            makePermutations(treeClass, childrenForPermutation, numberOfPermutations);
            appliedClasses.add(treeClass);
            hasSomethingChanged.set(true);
        }
    }

    private List<ChildInstancesForPermutation> getChildrenForPermutation(OWLClass treeClass) {
        List<ChildInstancesForPermutation> childrenForPermutation = new ArrayList<>();
        genericTool.getOntology().subClassAxiomsForSubClass(treeClass)
                .filter(axiom -> axiom.getSuperClass().objectPropertiesInSignature()
                        .anyMatch(ob -> genericTool.getOntology().objectSubPropertyAxiomsForSubProperty(ob)
                                .anyMatch(propSupers -> Vocabulary.OBJECT_PROPERTY_HASCHILD
                                        .equals(propSupers.getSuperProperty()))))
                .forEach(axiom -> childrenForPermutation.add(new ChildInstancesForPermutation(
                                        genericTool.getReasoner()
                                .instances(axiom.getSuperClass().classesInSignature().findAny().get())
                                                .collect(Collectors.toList()),
                        axiom.getSuperClass().objectPropertiesInSignature().findAny().get())));
        return childrenForPermutation;
    }

    private int getNumberOfPermutations(List<ChildInstancesForPermutation> childrenForPermutation) {
        int numberOfPermutations = childrenForPermutation.isEmpty() ? 0 : 1;
        for (ChildInstancesForPermutation child : childrenForPermutation) {
            numberOfPermutations *= child.childInstances.size();
        }
        return numberOfPermutations;
    }

    private void makePermutations(OWLClass treeClass,
            List<ChildInstancesForPermutation> childrenForPermutation, int numberOfPermutations) {
        List<PermutationOfChildInstances> permutations = new ArrayList<>(numberOfPermutations);
        buildPermutations(permutations, childrenForPermutation, new int[childrenForPermutation.size()], 0);

        for (PermutationOfChildInstances permutation : permutations) {
            String parentName = treeClass.getIRI().getShortForm() + permutation.permutationName + "Ind";
            OWLNamedIndividual parentInd = genericTool.getFactory()
                    .getOWLNamedIndividual(helper.create(parentName));
            // logger.info("\tAdd individual: " + parentInd.getIRI().getShortForm());

            helper.addAxiom(genericTool.getFactory().getOWLClassAssertionAxiom(treeClass, parentInd));
            for (ChildIndividualWithObjectPropertyFromParent childInd : permutation.permutatedChildren) {
                helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom(
                        childInd.propertyFromParent, parentInd, childInd.childIndividual));
            }
        }
    }

    private void buildPermutations(List<PermutationOfChildInstances> permutations,
            List<ChildInstancesForPermutation> childrenForPermutation, int[] currentPositions, int position) {
        if (position != currentPositions.length) {
            for (int i = 0; i < childrenForPermutation.get(position).childInstances.size(); i++) {
                currentPositions[position] = i;
                buildPermutations(permutations, childrenForPermutation, currentPositions, position + 1);
            }
        } else {
            ChildIndividualWithObjectPropertyFromParent[] indiToCreate = new ChildIndividualWithObjectPropertyFromParent[currentPositions.length];
            String name = "";
            for (int i = 0; i < currentPositions.length; i++) {
                ChildInstancesForPermutation childrend = childrenForPermutation.get(i);
                OWLNamedIndividual child = childrend.childInstances.get(currentPositions[i]);
                indiToCreate[i] = new ChildIndividualWithObjectPropertyFromParent(child,
                        childrend.propertyFromParent);
                name += helper.getNameOfOWLNamedIndividual(child);
            }
            permutations.add(new PermutationOfChildInstances(indiToCreate, name));
        }
    }

}
