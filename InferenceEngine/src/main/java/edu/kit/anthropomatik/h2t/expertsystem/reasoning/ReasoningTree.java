package edu.kit.anthropomatik.h2t.expertsystem.reasoning;

import edu.kit.anthropomatik.h2t.expertsystem.MyOWLHelper;
import edu.kit.anthropomatik.h2t.expertsystem.generated.Vocabulary;
import openllet.owlapi.OWLGenericTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ReasoningTree {

    public static final String PermutationSeparator = "--";
    public static final double TIME_NEEDED_THRESHOLD = 5.0;
    private static final int NUMBER_OF_SPACES = 3;

    private static final Logger logger = LogManager.getLogger(ReasoningTree.class);

    private static List<String> possibleTreeClassExtensions = new ArrayList<>();

    static {
        possibleTreeClassExtensions.add("");
        possibleTreeClassExtensions.add("Linear");
        possibleTreeClassExtensions.add("Compressed");
        possibleTreeClassExtensions.add("TwoSided");
        possibleTreeClassExtensions.add("DriveLinear");
        possibleTreeClassExtensions.add("DriveCompressed");
        possibleTreeClassExtensions.add("OutputLinear");
        possibleTreeClassExtensions.add("OutputCompressed");
        possibleTreeClassExtensions.add("OutputTwoSided");
    }


    private OWLGenericTools genericTool;
    private MyOWLHelper helper;
    private ReasoningTreeSpecialCases reasoningTreeSpecialCasesHandler;

    private List<OWLClass> reasoningTreeElements = new ArrayList<>();
    private Map<OWLClass, List<OWLClass>> reasoningTreeElementToSkipMapper = new HashMap<>();
    private OWLNamedIndividual currentRequirement;
    private List<OWLNamedIndividual> constances;

    private Map<OWLClass, Integer> appliedClassesToNumberOfPermutations = new HashMap<>();
    private Map<OWLNamedIndividual, OWLClass> individualToClassMapper = new HashMap<>();
    private Map<OWLClass, List<OWLNamedIndividual>> deviceToIndividualsMapper = new HashMap<>();
    private boolean hasSomethingChanged;
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public ReasoningTree(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
        reasoningTreeSpecialCasesHandler = new ReasoningTreeSpecialCases(genericTool, helper);
        setResasoningTreeELements();

        reasoningTreeElements.forEach(treeClass -> {
            List<OWLClass> classesToSkip = genericTool.getOntology().subClassAxiomsForSubClass(treeClass).filter
                    (axiom -> axiom.getSuperClass().objectPropertiesInSignature().anyMatch(Vocabulary
                            .OBJECT_PROPERTY_CHECKSAMEDEVICEINSUBTREE::equals)).map(filteredAxiom -> filteredAxiom
                    .getSuperClass().classesInSignature().findAny().orElseThrow(() -> new RuntimeException("Specify "
                            + "class for checkSameDeviceInSubtree"))).collect(Collectors.toList());
            reasoningTreeElementToSkipMapper.put(treeClass, classesToSkip);
        });
    }

    private void setResasoningTreeELements() {
        List<OWLClass> unorderdReasoningTreeElements = genericTool.getOntology().subClassAxiomsForSuperClass
                (Vocabulary.CLASS_REASONINGTREE).map(treeEle -> treeEle.getSubClass().asOWLClass()).collect
                (Collectors.toList());

        Map<String, List<String>> mapClassNameToChildren = new HashMap<>();
        unorderdReasoningTreeElements.forEach(treeElement -> {
            List<String> children = new ArrayList<>();
            genericTool.getOntology().subClassAxiomsForSubClass(treeElement).filter(axiom -> axiom.getSuperClass()
                    .objectPropertiesInSignature().anyMatch(ob -> genericTool.getOntology()
                            .objectSubPropertyAxiomsForSubProperty(ob).anyMatch(propSupers -> Vocabulary
                                    .OBJECT_PROPERTY_HASCHILD.equals(propSupers.getSuperProperty())))).forEach(axiom
                    -> children.add(helper.getNameOfOWLObjectProperty(axiom.getSuperClass()
                    .objectPropertiesInSignature().findAny().get())));
            mapClassNameToChildren.put(treeElement.getIRI().getShortForm(), children);
        });

        boolean hasChanged = true;
        while (!mapClassNameToChildren.isEmpty() && hasChanged) {
            int oldSize = mapClassNameToChildren.size();
            for (OWLClass clas : unorderdReasoningTreeElements) {
                String clasName = clas.getIRI().getShortForm();
                if (!mapClassNameToChildren.containsKey(clasName)) {
                    continue;
                }
                boolean isAnyStillInList = false;
                for (String childName : mapClassNameToChildren.get(clasName)) {
                    isAnyStillInList |= mapClassNameToChildren.keySet().stream().filter(key -> !key.equals(clasName))
                            .anyMatch(key -> possibleTreeClassExtensions.stream().anyMatch(exten -> (childName +
                                    exten).equals(key)));
                }
                if (!isAnyStillInList) {
                    reasoningTreeElements.add(clas);
                    mapClassNameToChildren.remove(clasName);
                }
            }
            hasChanged = oldSize != mapClassNameToChildren.size();
        }
        if (!hasChanged) {
            throw new RuntimeException("The names of the reasoning tree elements are not consistent -> they could be"
                    + " not ordered right");
            //TODO do not throw exception. Instead make warning and append rest unordered
        }
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
            reasoningTreeElements.stream().forEachOrdered(this::handleTreeItem);

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
                    .getSuperClass().classesInSignature().findAny().orElseThrow(() -> new RuntimeException("Specify "
                            + "a" + " child"))).collect(Collectors.toList()), axiom.getSuperClass()
                    .objectPropertiesInSignature().findAny().orElseThrow(() -> new RuntimeException("Specify a " +
                            "objectProperty for child"))));
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
            if (skipPermutation(treeClass, permutation)) {
                continue;
            }
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

    private boolean skipPermutation(OWLClass treeClass, PermutationOfChildInstances permutation) {
        for (OWLClass classToCheck : reasoningTreeElementToSkipMapper.get(treeClass)) {
            if (testIfContainsDifferent(classToCheck, permutation)) {
                return true;
            }
        }
        return false;
    }

    private boolean testIfContainsDifferent(OWLClass classToCheck, PermutationOfChildInstances permutation) {
        for (OWLNamedIndividual indi : deviceToIndividualsMapper.get(classToCheck)) {
            String checkString = "-" + permutation.permutationName + "-";
            String[] split = checkString.split(helper.getNameOfOWLNamedIndividual(indi));
            if (split.length > 2) {
                return false;
            }
        }
        return true;
    }

    private void deleteNotSatisfied(OWLClass treeClass) {
        if (genericTool.getOntology().subClassAxiomsForSuperClass(treeClass).count() == 0) {
            return;
        }
        long startTime = System.currentTimeMillis();
        Set<OWLNamedIndividual> satisfiedChildInstances = new HashSet<>();
        if (Vocabulary.CLASS_MOTORGEARBOXMATCH.equals(treeClass)) {
            //TODO handle this special case in a general way
            satisfiedChildInstances.addAll(genericTool.getReasoner().instances(Vocabulary
                    .CLASS_SATISFIEDMOTORGEARBOXMATCHFORSENSORACTUATORUNIT).collect(Collectors.toSet()));
        }
        if (Vocabulary.CLASS_MCP_MC_MATCH.equals(treeClass) || Vocabulary.CLASS_TS_AE_MATCH.equals(treeClass)) {
            // I have no idea why this is not working...
            // It just deletes everything everytime...
            return;
        }
        genericTool.getOntology().subClassAxiomsForSuperClass(treeClass).forEach(axiom -> satisfiedChildInstances
                .addAll(genericTool.getReasoner().instances(axiom.getSubClass()).collect(Collectors.toSet())));
        Set<OWLAxiom> axiomsToDelete = new HashSet<>();
        long numberOfDeletedChildren = handleDelete(axiomsToDelete, satisfiedChildInstances, treeClass);
        //TODO and only if satisfiedChildInstances is not empty -> if all are not satisfied, there should be nothing
        // deleted, because this could just be a mistaken modeling
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
        List<Map.Entry<OWLNamedIndividual, OWLClass>> instancesToDelete = individualToClassMapper.entrySet().stream()
                .filter(set -> set.getValue().equals(type) && childInstances.stream().noneMatch(instSatisfied ->
                        instSatisfied.equals(set.getKey()))).collect(Collectors.toList());
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

    public void setConstances(List<OWLNamedIndividual> constances) {
        this.constances = constances;
    }

    public void resetDeviceToIndividual() {
        deviceToIndividualsMapper.clear();
    }

    public void addDeviceToIndividual(OWLClass device, OWLNamedIndividual individual) {
        if (deviceToIndividualsMapper.containsKey(device)) {
            deviceToIndividualsMapper.get(device).add(individual);
        } else {
            List<OWLNamedIndividual> list = new ArrayList<>();
            list.add(individual);
            deviceToIndividualsMapper.put(device, list);
        }
    }
}
