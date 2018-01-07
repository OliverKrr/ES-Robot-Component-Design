package edu.kit.expertsystem.reasoning;

import edu.kit.expertsystem.MyOWLHelper;
import edu.kit.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ReasoningTree {

    public static final String PermutationSeparator = "--";
    public static final double TIME_NEEDED_THRESHOLD = 5.0;
    private static final int NUMBER_OF_SPACES = 3;

    private static final Logger logger = LogManager.getLogger(ReasoningTree.class);

    private OWLGenericTools genericTool;
    private MyOWLHelper helper;
    private ReasoningTreeSpecialCases reasoningTreeSpecialCasesHandler;

    private List<OWLSubClassOfAxiom> reasoningTreeElements;
    private OWLNamedIndividual currentRequirement;
    private List<OWLNamedIndividual> constances;

    private Map<OWLClass, Integer> appliedClassesToNumberOfPermutations = new HashMap<>();
    private Map<OWLNamedIndividual, OWLClass> individualToClassMapper = new HashMap<>();
    private boolean hasSomethingChanged;
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public ReasoningTree(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
        reasoningTreeSpecialCasesHandler = new ReasoningTreeSpecialCases(genericTool, helper);
        reasoningTreeElements = genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_REASONINGTREE)
                .collect(Collectors.toList());
    }

    public void interruptReasoning() {
        interrupted.set(true);
    }

    public void resetInterrupt() {
        interrupted.set(false);
    }

    public void makeReasoning() {
        if (interrupted.get()) {
            return;
        }
        reasoningTreeSpecialCasesHandler.reset();
        appliedClassesToNumberOfPermutations.clear();
        individualToClassMapper.clear();
        do {
            hasSomethingChanged = false;
            // TODO make graph of classes and iterate from bottom to top
            reasoningTreeElements.forEach(treeClassAxiom -> handleTreeItem(treeClassAxiom.getSubClass().asOWLClass()));

            if (!interrupted.get() && !hasSomethingChanged) {
                boolean anySpecialChanges = false;
                do {
                    hasSomethingChanged = false;
                    // the order is important
                    genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_UNSATISFIED).forEach
                            (unsatiesfiedSuperClass -> hasSomethingChanged |= reasoningTreeSpecialCasesHandler
                                    .handleUnsatisfied(unsatiesfiedSuperClass.getSubClass().asOWLClass()));


                    genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_POSSIBLEUNSATISFIED)
                            .forEach(possibleUnsatiesfiedSuperClass -> hasSomethingChanged |=
                                    reasoningTreeSpecialCasesHandler.handlePossibleUnsatisfied
                                            (possibleUnsatiesfiedSuperClass.getSubClass().asOWLClass()));

                    if (!reasoningTreeSpecialCasesHandler.didBackupOnLastRun()) {
                        // If we do backup for possible unsatisfied, we do not delete stuff. In the next
                        // run we should first check for unsatisfied and then possibleSatisfied
                        genericTool.getOntology().subClassAxiomsForSuperClass(Vocabulary.CLASS_POSSIBLESATISFIED)
                                .forEach(possibleSatisfiedSuperClass -> hasSomethingChanged |=
                                        reasoningTreeSpecialCasesHandler.handlePossibleSatisfied
                                                (possibleSatisfiedSuperClass.getSubClass().asOWLClass()));
                    }
                    anySpecialChanges |= hasSomethingChanged;
                } while (hasSomethingChanged && !interrupted.get());
                hasSomethingChanged = anySpecialChanges;
            }
        } while (hasSomethingChanged && !interrupted.get());
    }

    private void handleTreeItem(OWLClass treeClass) {
        if (interrupted.get()) {
            return;
        }
        List<ChildInstancesForPermutation> childrenForPermutation = getChildrenForPermutation(treeClass);
        int numberOfPermutations = getNumberOfPermutations(childrenForPermutation);

        if (appliedClassesToNumberOfPermutations.containsKey(treeClass) && appliedClassesToNumberOfPermutations.get
                (treeClass).compareTo(numberOfPermutations) == 0) {
            return;
        }

        childrenForPermutation.forEach(childForPermutation -> logger.debug(treeClass.getIRI().getShortForm() + " has"
                + " " + childForPermutation.propertyFromParent.getNamedProperty().getIRI().getShortForm() + " " +
                "with number of " + "children: " + childForPermutation.childInstances.size()));

        if (numberOfPermutations > 0) {
            makePermutations(treeClass, childrenForPermutation, numberOfPermutations);
            appliedClassesToNumberOfPermutations.put(treeClass, numberOfPermutations);
        }
    }

    private String getSpacesFor(long value) {
        StringBuilder builder = new StringBuilder("");
        for (int i = String.valueOf(value).length(); i <= NUMBER_OF_SPACES; ++i) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private List<ChildInstancesForPermutation> getChildrenForPermutation(OWLClass treeClass) {
        List<ChildInstancesForPermutation> childrenForPermutation = new ArrayList<>();
        genericTool.getOntology().subClassAxiomsForSubClass(treeClass).filter(axiom -> axiom.getSuperClass()
                .objectPropertiesInSignature().anyMatch(ob -> genericTool.getOntology()
                        .objectSubPropertyAxiomsForSubProperty(ob).anyMatch(propSupers -> Vocabulary
                                .OBJECT_PROPERTY_HASCHILD.equals(propSupers.getSuperProperty())))).forEach(axiom -> {
            long startTime = System.currentTimeMillis();
            childrenForPermutation.add(new ChildInstancesForPermutation(genericTool.getReasoner().instances(axiom
                    .getSuperClass().classesInSignature().findAny().get()).collect(Collectors.toList()), axiom
                    .getSuperClass().objectPropertiesInSignature().findAny().get()));
            double timeNeeded = (System.currentTimeMillis() - startTime) / 1000.0;
            if (timeNeeded >= TIME_NEEDED_THRESHOLD) {
                logger.debug("Time needed for " + treeClass.getIRI().getShortForm() + " and child" + axiom
                        .getSuperClass().classesInSignature().map(clas -> clas.getIRI().getShortForm()).reduce("",
                                (a, b) -> a + " " + b) + ": " + timeNeeded + "s");
            }
        });
        return childrenForPermutation;
    }

    private int getNumberOfPermutations(List<ChildInstancesForPermutation> childrenForPermutation) {
        int numberOfPermutations = childrenForPermutation.isEmpty() ? 0 : 1;
        for (ChildInstancesForPermutation child : childrenForPermutation) {
            numberOfPermutations *= child.childInstances.size();
        }
        return numberOfPermutations;
    }

    private void makePermutations(OWLClass treeClass, List<ChildInstancesForPermutation> childrenForPermutation, int
            numberOfPermutations) {
        List<PermutationOfChildInstances> permutations = new ArrayList<>(numberOfPermutations);
        buildPermutations(permutations, childrenForPermutation, new int[childrenForPermutation.size()], 0);

        int realAddedIndis = 0;
        for (PermutationOfChildInstances permutation : permutations) {
            String parentName = treeClass.getIRI().getShortForm() + permutation.permutationName + "Ind";
            OWLNamedIndividual parentInd = genericTool.getFactory().getOWLNamedIndividual(helper.create(parentName));

            if (helper.addAxiom(genericTool.getFactory().getOWLClassAssertionAxiom(treeClass, parentInd))) {
                helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom(Vocabulary
                        .OBJECT_PROPERTY_HASCURRENTREQUIREMENT, parentInd, currentRequirement));
                constances.forEach(con -> helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom
                        (Vocabulary.OBJECT_PROPERTY_HASCONSTANT, parentInd, con)));

                individualToClassMapper.put(parentInd, treeClass);
                // logger.debug("\tAdd individual: " + parentInd.getIRI().getShortForm());
                ++realAddedIndis;
                for (ChildIndividualWithObjectPropertyFromParent childInd : permutation.permutatedChildren) {
                    helper.addAxiom(genericTool.getFactory().getOWLObjectPropertyAssertionAxiom(childInd
                            .propertyFromParent, parentInd, childInd.childIndividual));
                }
            }

        }
        if (realAddedIndis > 0) {
            hasSomethingChanged = true;
            helper.flush();
            logger.info("Add " + getSpacesFor(realAddedIndis) + realAddedIndis + " individuals for: " + treeClass
                    .getIRI().getShortForm());

            deleteNotSatisfied(treeClass);
        }
    }

    private void deleteNotSatisfied(OWLClass treeClass) {
        long startTime = System.currentTimeMillis();
        Set<OWLNamedIndividual> satisfiedChildInstances = new HashSet<>();
        if (Vocabulary.CLASS_MOTORGEARBOXMATCH.equals(treeClass)) {
            //TODO handle this special case in a general way
            satisfiedChildInstances.addAll(genericTool.getReasoner().instances(Vocabulary
                    .CLASS_SATISFIEDMOTORGEARBOXMATCHFORSACUNIT).collect(Collectors.toCollection(HashSet::new)));
        }
        genericTool.getOntology().subClassAxiomsForSuperClass(treeClass).forEach(axiom -> satisfiedChildInstances
                .addAll(genericTool.getReasoner().instances(axiom.getSubClass()).collect(Collectors.toCollection
                        (HashSet::new))));
        Set<OWLAxiom> axiomsToDelete = new HashSet<>();
        long numberOfDeletedChildren = handleDelete(axiomsToDelete, satisfiedChildInstances, treeClass);
        if (!axiomsToDelete.isEmpty()) {
            logger.info("Delete " + getSpacesFor(numberOfDeletedChildren) + numberOfDeletedChildren + " individuals "
                    + "of not satisfied " + treeClass.getIRI().getShortForm());
            helper.removeAxioms(axiomsToDelete);
            helper.flush();
        }

        double timeNeeded = (System.currentTimeMillis() - startTime) / 1000.0;
        if (timeNeeded >= TIME_NEEDED_THRESHOLD) {
            logger.debug("Time needed for " + treeClass.getIRI().getShortForm() + " to delete not satisfied: " +
                    timeNeeded + "s");
        }
    }

    private long handleDelete(Set<OWLAxiom> axiomsToDelete, Collection<OWLNamedIndividual> childInstances, OWLClass
            type) {
        ArrayList<Map.Entry<OWLNamedIndividual, OWLClass>> instancesToDelete = individualToClassMapper.entrySet()
                .stream().filter(set -> set.getValue().equals(type) && childInstances.stream().noneMatch
                        (instSatisfied -> instSatisfied.equals(set.getKey()))).collect(Collectors.toCollection
                        (ArrayList::new));
        instancesToDelete.forEach(set -> helper.getGeneratedAxioms().stream().filter(axiom -> axiom
                .individualsInSignature().anyMatch(indi -> indi.equals(set.getKey()))).forEach(axiomsToDelete::add));
        return instancesToDelete.size();
    }

    private void buildPermutations(List<PermutationOfChildInstances> permutations, List<ChildInstancesForPermutation>
            childrenForPermutation, int[] currentPositions, int position) {
        if (position != currentPositions.length) {
            for (int i = 0; i < childrenForPermutation.get(position).childInstances.size(); i++) {
                currentPositions[position] = i;
                buildPermutations(permutations, childrenForPermutation, currentPositions, position + 1);
            }
        } else {
            ChildIndividualWithObjectPropertyFromParent[] indiToCreate = new
                    ChildIndividualWithObjectPropertyFromParent[currentPositions.length];
            StringBuilder nameBuilder = new StringBuilder(PermutationSeparator);
            for (int i = 0; i < currentPositions.length; i++) {
                ChildInstancesForPermutation childrend = childrenForPermutation.get(i);
                OWLNamedIndividual child = childrend.childInstances.get(currentPositions[i]);
                indiToCreate[i] = new ChildIndividualWithObjectPropertyFromParent(child, childrend.propertyFromParent);
                nameBuilder.append(helper.getNameOfOWLNamedIndividual(child));
                if (i != currentPositions.length - 1) {
                    nameBuilder.append(PermutationSeparator);
                }
            }
            permutations.add(new PermutationOfChildInstances(indiToCreate, nameBuilder.toString()));
        }
    }

    public void setCurrentRequirement(OWLNamedIndividual currentRequirement) {
        this.currentRequirement = currentRequirement;
    }

    public List<OWLNamedIndividual> getConstances() {
        return constances;
    }

    public void setConstances(List<OWLNamedIndividual> constances) {
        this.constances = constances;
    }
}
