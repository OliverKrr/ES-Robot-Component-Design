package edu.kit.expertsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.Requirement;
import edu.kit.expertsystem.model.Result;
import openllet.owlapi.PelletReasoner;

public class MainReasoner {

    private static final String fileEnding = ".owl";
    private static final String domainFileName = "SAC_Domain_Ontology" + fileEnding;
    private static final String reasoningFileName = "SAC_Reasoning_Ontology" + fileEnding;

    private static final String myPath = "C:\\Users\\Oliver\\Dropbox\\MyGits\\PraxisDerForschung\\KnowledgeBase\\src\\main\\resources\\";

    private static final Logger logger = LogManager.getLogger(MainReasoner.class);

    private String inferdFilePath = null;

    private OWLOntologyManager manager;
    private OWLDataFactory dataFac;
    private OWLOntology ontology;
    private OWLReasoner reasoner;

    private MyOWLHelper helper;
    private ReasoningTree reasoningTree;
    private boolean isReasoningPrepared = false;

    public MainReasoner() {
        manager = OWLManager.createOWLOntologyManager();
        dataFac = manager.getOWLDataFactory();
    }

    public void initialize() {
        OWLOntology basicOntology = loadOntology(domainFileName, false);
        ontology = loadOntology(reasoningFileName, true);
        ontology.addAxioms(basicOntology.axioms());

        reasoner = new PelletReasoner(ontology, BufferingMode.BUFFERING);
        logger.info("Read Ontology isConsistent: " + reasoner.isConsistent());
        helper = new MyOWLHelper(manager, ontology);
        reasoningTree = new ReasoningTree(dataFac, ontology, reasoner, helper);
    }

    private OWLOntology loadOntology(String fileName, boolean setInferdFilePath) {
        try (InputStream ontoStream = readOntology(fileName, setInferdFilePath)) {
            return manager.loadOntologyFromOntologyDocument(ontoStream);
        } catch (OWLOntologyCreationException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Loading the ontology failed");
        }
    }

    private InputStream readOntology(String fileName, boolean setInferdFilePath) throws IOException {
        String path = myPath + fileName;
        if (setInferdFilePath) {
            inferdFilePath = path.substring(0, path.length() - fileEnding.length()) + "Inf" + fileEnding;
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            String localPath = Paths.get("").toAbsolutePath() + "/" + fileName;
            if (setInferdFilePath) {
                inferdFilePath = localPath.substring(0, localPath.length() - fileEnding.length()) + "Inf"
                        + fileEnding;
            }
            try {
                return new FileInputStream(localPath);
            } catch (FileNotFoundException e2) {
                return getClass().getResourceAsStream("/" + fileName);
            }
        }
    }

    public void prepareReasoning() {
        if (isReasoningPrepared) {
            return;
        }
        helper.clearGeneratedAxioms();
        createBasicIndividuals();
        reasoner.flush();
        isReasoningPrepared = true;
    }

    public List<Result> startReasoning(List<Requirement> requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning();
            }
            isReasoningPrepared = false;
            addRequirements(requirements);
            reasoner.flush();
            return reason(requirements);
        } finally {
            try {
                saveReasonedOntology();
            } catch (OWLOntologyStorageException | IOException e) {
                logger.error(e.getMessage(), e);
            }
            logger.info("Time needed: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }
    }

    private void createBasicIndividuals() {
        reasoner.subClasses(Vocabulary.CLASS_INDIVIDUALSTOCREATE, true)
                .forEach(toCreateParent -> reasoner.subClasses(toCreateParent, true).forEach(toCreate -> {
                    String name = toCreate.getIRI().getShortForm() + "Ind";
                    OWLNamedIndividual ind = dataFac.getOWLNamedIndividual(helper.create(name));
                    helper.addAxiom(dataFac.getOWLClassAssertionAxiom(toCreate, ind));
                }));
    }

    private void addRequirements(List<Requirement> requirements) {
        logger.info(requirements);
        OWLNamedIndividual requirementsInd = dataFac.getOWLNamedIndividual(helper.create("requirementsInd"));
        // TODO fix with multiple Vocalbs
        helper.addAxiom(dataFac.getOWLClassAssertionAxiom(
                dataFac.getOWLClass(IRI
                        .create("http://www.semanticweb.org/oliver/ontologies/sac/sac_basic#Requirements")),
                requirementsInd));

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASWEIGHTREQMIN_M_UNIT_KG,
                requirements.get(0).min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASWEIGHTREQMAX_M_UNIT_KG,
                requirements.get(0).max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASDIMENSIONLENGTHREQMIN_L_UNIT_MM,
                requirements.get(1).min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASDIMENSIONLENGTHREQMAX_L_UNIT_MM,
                requirements.get(1).max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASDIMENSIONOUTERDIAMETERREQMIN_D_UNIT_MM,
                requirements.get(2).min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASDIMENSIONOUTERDIAMETERREQMAX_D_UNIT_MM,
                requirements.get(2).max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASPEAKTORQUEREQMIN_M_MAX_UNIT_NM,
                requirements.get(3).min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASPEAKTORQUEREQMAX_M_MAX_UNIT_NM,
                requirements.get(3).max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDREQMIN_N_MAX_UNIT_RPM,
                requirements.get(4).min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDREQMAX_N_MAX_UNIT_RPM,
                requirements.get(4).max);
    }

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, double value) {
        OWLDataPropertyAssertionAxiom reqAxiom = dataFac.getOWLDataPropertyAssertionAxiom(property,
                requirementsInd, dataFac.getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_DECIMAL));
        helper.addAxiom(reqAxiom);
    }

    private List<Result> reason(List<Requirement> requirements) {
        reasoningTree.makeReasoning(Vocabulary.CLASS_MOTORGEARBOXMATCH);
        return makeResults(requirements);
    }

    private List<Result> makeResults(List<Requirement> requirements) {
        List<Result> results = new ArrayList<>();
        reasoner.instances(Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH).forEach(en -> {
            Result result = new Result();
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_HASMOTOR).findAny()
                    .ifPresent(obProp -> result.motor.name = helper.getNameOfOWLNamedIndividual(obProp));
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_HASGEARBOX).findAny()
                    .ifPresent(obProp -> result.gearBox.name = helper.getNameOfOWLNamedIndividual(obProp));

            result.requirements = copyRequirements(requirements);

            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG).findAny()
                    .ifPresent(obProp -> result.requirements.get(0).result = parseValue(obProp));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASDIMENSIONLENGTH_L_UNIT_MM).findAny()
                    .ifPresent(obProp -> result.requirements.get(1).result = parseValue(obProp));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASDIMENSIONOUTERDIAMETER_D_UNIT_MM)
                    .findAny().ifPresent(obProp -> result.requirements.get(2).result = parseValue(obProp));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASPEAKTORQUERES_M_MAX_UNIT_NM).findAny()
                    .ifPresent(obProp -> result.requirements.get(3).result = parseValue(obProp));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDRES_N_MAX_UNIT_RPM)
                    .findAny().ifPresent(obProp -> result.requirements.get(4).result = parseValue(obProp));

            results.add(result);
        });

        Collections.sort(results, Comparator.comparingDouble(result -> result.requirements.get(0).result));
        // results.forEach(r -> logger.info(r));
        logger.info("Number of results: " + results.size());
        return results;
    }

    private List<Requirement> copyRequirements(List<Requirement> requirements) {
        List<Requirement> copyReqs = new ArrayList<>(requirements.size());
        for (Requirement req : requirements) {
            copyReqs.add(new Requirement(req));
        }
        return copyReqs;
    }

    private double parseValue(OWLLiteral obProp) {
        if (obProp.isInteger()) {
            return obProp.parseInteger();
        }
        return obProp.parseDouble();
    }

    private void saveReasonedOntology() throws IOException, OWLOntologyStorageException {
        logger.info("Reasoned ontology isConsistent: " + reasoner.isConsistent());
        if (inferdFilePath == null) {
            return;
        }
        OWLOntology inferOnto = reasoner.getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            manager.saveOntology(inferOnto, out);
        }
    }

    public List<Requirement> getRequirements() {
        List<Requirement> requirements = new ArrayList<>();

        Requirement weight = new Requirement();
        weight.displayName = "Weight m:";
        weight.category = "Common";
        weight.description = "The total weight of the SAC unit.";
        weight.unit = "kg";
        weight.enableMin = false;
        weight.enableMax = true;
        requirements.add(weight);

        Requirement lenght = new Requirement();
        lenght.displayName = "Length L:";
        lenght.category = "Dimensions";
        lenght.description = "The total length of the SAC unit.";
        lenght.unit = "mm";
        lenght.enableMin = false;
        lenght.enableMax = true;
        requirements.add(lenght);

        Requirement diameter = new Requirement();
        diameter.displayName = "Diameter D:";
        diameter.category = "Dimensions";
        diameter.description = "The total outer diameter of the SAC unit.";
        diameter.unit = "mm";
        diameter.enableMin = false;
        diameter.enableMax = true;
        requirements.add(diameter);

        Requirement maximalTorque = new Requirement();
        maximalTorque.displayName = "Peak Torque M_max:";
        maximalTorque.category = "Performance";
        maximalTorque.description = "Repeated peak torque without damaging the units.";
        maximalTorque.unit = "Nm";
        maximalTorque.enableMin = true;
        maximalTorque.enableMax = false;
        requirements.add(maximalTorque);

        Requirement maximalRotationSpeed = new Requirement();
        maximalRotationSpeed.displayName = "Maximal Speed n_max:";
        maximalRotationSpeed.category = "Performance";
        maximalRotationSpeed.description = "Maximal output speed at nominal voltage.";
        maximalRotationSpeed.unit = "°/s";
        // convert from rpm to °/s
        maximalRotationSpeed.scaleFromOntologyToUI = 6.0;
        maximalRotationSpeed.enableMin = true;
        maximalRotationSpeed.enableMax = false;
        requirements.add(maximalRotationSpeed);

        return requirements;
    }
}
