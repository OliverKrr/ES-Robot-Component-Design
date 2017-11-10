package edu.kit.expertsystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import edu.kit.expertsystem.generated.Vocabulary;

public class ReasoningTree {

    private static final Logger logger = LogManager.getLogger(ReasoningTree.class);

    private OWLDataFactory dataFac;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private MyOWLHelper helper;

    private OWLClass currentClassToReason;
    private Set<OWLClass> appliedClasses = new HashSet<>();
    private boolean hasSomethingChanged;

    public ReasoningTree(OWLDataFactory dataFac, OWLOntology ontology, OWLReasoner reasoner,
            MyOWLHelper helper) {
        this.dataFac = dataFac;
        this.ontology = ontology;
        this.reasoner = reasoner;
        this.helper = helper;
    }

    public void makeReasoning(OWLClass classToReason) {
        currentClassToReason = classToReason;
        appliedClasses.clear();

        do {
            hasSomethingChanged = false;
            reasoner.subClasses(Vocabulary.CLASS_REASONINGTREE, true)
                    .forEach(treeClass -> handleTreeItem(treeClass));
            if (hasSomethingChanged) {
                reasoner.flush();
            }
        } while (hasSomethingChanged && !appliedClasses.contains(currentClassToReason));
    }

    private void handleTreeItem(OWLClass treeClass) {
        if (appliedClasses.contains(treeClass) || appliedClasses.contains(currentClassToReason)) {
            return;
        }

        List<ChildInstancesForPermutation> childrenForPermutation = getChildrenForPermutation(treeClass);
        int numberOfPermutations = getNumberOfPermutations(childrenForPermutation);

        if (numberOfPermutations > 0) {
            logger.info(
                    "Add " + numberOfPermutations + " individuals for: " + treeClass.getIRI().getShortForm());
            makePermutations(treeClass, childrenForPermutation, numberOfPermutations);
            appliedClasses.add(treeClass);
            hasSomethingChanged = true;
        }
    }

    private List<ChildInstancesForPermutation> getChildrenForPermutation(OWLClass treeClass) {
        List<ChildInstancesForPermutation> childrenForPermutation = new ArrayList<>();
        ontology.subClassAxiomsForSubClass(treeClass)
                .forEach(axiom -> axiom.componentsWithoutAnnotations()
                        .filter(component -> component instanceof OWLQuantifiedObjectRestriction && ontology
                                .objectSubPropertyAxiomsForSubProperty(
                                        ((OWLQuantifiedObjectRestriction) component).getProperty())
                                .anyMatch(propSupers -> Vocabulary.OBJECT_PROPERTY_HASCHILD
                                        .equals(propSupers.getSuperProperty())))
                        .forEach(component -> childrenForPermutation.add(new ChildInstancesForPermutation(
                                reasoner.instances(((OWLQuantifiedObjectRestriction) component).getFiller())
                                        .collect(Collectors.toList()),
                                ((OWLQuantifiedObjectRestriction) component).getProperty()))));
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
            OWLNamedIndividual parentInd = dataFac.getOWLNamedIndividual(helper.create(parentName));
            // logger.info("\tAdd individual: " + parentInd.getIRI().getShortForm());

            helper.addAxiom(dataFac.getOWLClassAssertionAxiom(treeClass, parentInd));
            for (ChildIndividualWithObjectPropertyFromParent childInd : permutation.permutatedChildren) {
                helper.addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(childInd.propertyFromParent,
                        parentInd, childInd.childIndividual));
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
