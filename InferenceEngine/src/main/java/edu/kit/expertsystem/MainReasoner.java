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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.Requirements;
import edu.kit.expertsystem.model.Result;
import openllet.owlapi.PelletReasoner;

public class MainReasoner {

    private static final String fileEnding = ".owl";
    private static final String domainFileName = "SAC_Domain_Ontology" + fileEnding;
    private static final String reasoningFileName = "SAC_Reasoning_Ontology" + fileEnding;

    private static final Logger logger = LogManager.getLogger(MainReasoner.class);

    private String inferdFilePath;

    private OWLOntologyManager manager;
    private OWLDataFactory dataFac;
    private OWLOntology ontology;
    private OWLReasoner reasoner;

    private MyOWLHelper helper;
    private ReasoningTree reasoningTree;
    private boolean isReasoningPrepared = false;

    MainReasoner() {
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
        String localPath = Paths.get("").toAbsolutePath() + "/" + fileName;
        if (setInferdFilePath) {
            inferdFilePath = localPath.substring(0, localPath.length() - fileEnding.length()) + "Inf"
                    + fileEnding;
        }
        try {
            return new FileInputStream(localPath);
        } catch (FileNotFoundException e) {
            return getClass().getResourceAsStream("/" + fileName);
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

    public List<Result> startReasoning(Requirements requirements) {
        long startTime = System.currentTimeMillis();
        try {
            if (!isReasoningPrepared) {
                prepareReasoning();
            }
            isReasoningPrepared = false;
            addRequirements(requirements);
            reasoner.flush();
            return reason();
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

    private void addRequirements(Requirements requirements) {
        logger.info(requirements);
        OWLNamedIndividual requirementsInd = dataFac.getOWLNamedIndividual(helper.create("requirementsInd"));
        // TODO fix with multiple Vocalbs
        helper.addAxiom(dataFac.getOWLClassAssertionAxiom(
                dataFac.getOWLClass(IRI
                        .create("http://www.semanticweb.org/oliver/ontologies/sac/sac_basic#Requirements")),
                requirementsInd));

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASPEAKTORQUEREQMIN_M_MAX_UNIT_NM,
                requirements.maximalTorque.min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASPEAKTORQUEREQMAX_M_MAX_UNIT_NM,
                requirements.maximalTorque.max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDREQMIN_N_MAX_UNIT_RPM,
                requirements.maximalRotationSpeed.min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDREQMAX_N_MAX_UNIT_RPM,
                requirements.maximalRotationSpeed.max);

        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASWEIGHTREQMIN_M_UNIT_KG,
                requirements.weight.min);
        addRequirement(requirementsInd, Vocabulary.DATA_PROPERTY_HASWEIGHTREQMAX_M_UNIT_KG,
                requirements.weight.max);
    }

    private void addRequirement(OWLNamedIndividual requirementsInd, OWLDataProperty property, double value) {
        OWLDataPropertyAssertionAxiom reqAxiom = dataFac.getOWLDataPropertyAssertionAxiom(property,
                requirementsInd, dataFac.getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_DECIMAL));
        helper.addAxiom(reqAxiom);
    }

    private List<Result> reason() {
        // TODO testen mit SACunit
        reasoningTree.makeReasoning(Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH);
        return makeResults();
    }

    private List<Result> makeResults() {
        List<Result> results = new ArrayList<>();
        reasoner.instances(Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH).forEach(en -> {
            Result result = new Result();
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_HASMOTOR).findAny()
                    .ifPresent(obProp -> result.motor.name = helper.getNameOfOWLNamedIndividual(obProp));
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_HASGEARBOX).findAny()
                    .ifPresent(obProp -> result.gearBox.name = helper.getNameOfOWLNamedIndividual(obProp));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG).findAny()
                    .ifPresent(obProp -> result.weight = obProp.parseDouble());
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASPEAKTORQUERES_M_MAX_UNIT_NM).findAny()
                    .ifPresent(obProp -> result.maximalTorque = obProp.parseDouble());
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDRES_N_MAX_UNIT_RPM)
                    .findAny().ifPresent(obProp -> result.maximalRotationSpeed = obProp.parseDouble());
            results.add(result);
        });

        Collections.sort(results, Comparator.comparingDouble(result -> result.weight));
        results.forEach(r -> logger.info(r));
        logger.info("Number of results: " + results.size());
        return results;
    }

    private void saveReasonedOntology() throws IOException, OWLOntologyStorageException {
        logger.info("Reasoned ontology isConsistent: " + reasoner.isConsistent());
        OWLOntology inferOnto = reasoner.getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            manager.saveOntology(inferOnto, out);
        }
    }
}
