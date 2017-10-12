package edu.kit.expertsystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
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

import com.google.common.io.Files;

import edu.kit.expertsystem.generated.Vocabulary;
import edu.kit.expertsystem.model.Requirements;
import edu.kit.expertsystem.model.Result;
import openllet.owlapi.PelletReasoner;

public class MyReasonerV3 {

    private static final String fileEnding = ".owl";
    private static final String orginalFileName = "PrototypeV4" + fileEnding;

    private String orginalFilePath = "C:\\Users\\Oliver\\Dropbox\\Uni\\Informatik_Master\\2. Semester\\Praxis der Forschung\\Projektplan\\"
            + orginalFileName;
    private String copyedFilePath;
    private String inferdFilePath;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFac;
    private OWLReasoner reasoner;

    private List<OWLAxiom> generatedAxioms = new ArrayList<>();
    private boolean isReasoningPrepared = false;

    MyReasonerV3() throws RuntimeException {
        if (!new File(orginalFilePath).exists()) {
            orginalFilePath = Paths.get("").toAbsolutePath().toFile().getAbsolutePath() + "\\"
                    + orginalFileName;
            if (!new File(orginalFilePath).exists()) {
                throw new RuntimeException("Could not find ontology file: " + orginalFilePath);
            }
        }
        copyedFilePath = orginalFilePath.substring(0, orginalFilePath.length() - fileEnding.length()) + "Copy"
                + ".owl";
        inferdFilePath = orginalFilePath.substring(0, orginalFilePath.length() - fileEnding.length())
                + "CopyInf" + ".owl";

        File workingFile = new File(copyedFilePath);
        try {
            Files.copy(new File(orginalFilePath), workingFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not copyed owl file!");
        }
        manager = OWLManager.createOWLOntologyManager();
        try {
            ontology = manager.loadOntologyFromOntologyDocument(workingFile);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            throw new RuntimeException("Loading the ontology failed");
        }
        dataFac = manager.getOWLDataFactory();
        reasoner = new PelletReasoner(ontology, BufferingMode.BUFFERING);
        System.out.println("Read Ontology isConsistent: " + reasoner.isConsistent());
    }

    public void prepareReasoning() {
        if (isReasoningPrepared) {
            return;
        }
        ontology.removeAxioms(generatedAxioms);
        generatedAxioms.clear();
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
                e.printStackTrace();
            }
            System.out.println("Time needed: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }
    }

    private void createBasicIndividuals() {
        reasoner.subClasses(Vocabulary.CLASS_ROBODRIVESERVOKIBILM, true).forEach(robo -> {
            OWLNamedIndividual roboInd = dataFac
                    .getOWLNamedIndividual(create(robo.getIRI().getShortForm() + "Ind"));
            addAxiom(dataFac.getOWLClassAssertionAxiom(robo, roboInd));
        });

        reasoner.subClasses(Vocabulary.CLASS_CSD_2A, true).forEach(gearBox -> {
            OWLNamedIndividual gearBoxInd = dataFac
                    .getOWLNamedIndividual(create(gearBox.getIRI().getShortForm() + "Ind"));
            addAxiom(dataFac.getOWLClassAssertionAxiom(gearBox, gearBoxInd));
        });
    }

    private void addRequirements(Requirements requirements) {
        System.out.println(requirements);
        OWLNamedIndividual requirementsInd = dataFac.getOWLNamedIndividual(create("requirementsInd"));
        addAxiom(dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_REQUIREMENTS, requirementsInd));

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
        OWLDataPropertyAssertionAxiom maximalTorqueReqMin = dataFac.getOWLDataPropertyAssertionAxiom(property,
                requirementsInd, dataFac.getOWLLiteral(String.valueOf(value), OWL2Datatype.XSD_DECIMAL));
        addAxiom(maximalTorqueReqMin);
    }

    private List<Result> reason() {
        List<Result> results = new ArrayList<>();

        reasoner.instances(Vocabulary.CLASS_SATISFIEDMOTOR).forEach(motor -> {
            reasoner.instances(Vocabulary.CLASS_SATISFIEDGEARBOX).forEach(gearBox -> {

                OWLNamedIndividual matchInd = dataFac.getOWLNamedIndividual(create("Match"
                        + motor.getIRI().getShortForm() + "With" + gearBox.getIRI().getShortForm() + "Ind"));
                addAxiom(dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_MOTORGEARBOXMATCH, matchInd));

                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(
                        Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFMOTOR, matchInd, motor));
                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(
                        Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFGEARBOX, matchInd, gearBox));
            });
        });
        reasoner.flush();
        reasoner.instances(Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH).forEach(en -> {
            Result result = new Result();
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFMOTOR).findAny()
                    .ifPresent(obProp -> result.motor.name = obProp.getIRI().getShortForm().substring(0,
                            obProp.getIRI().getShortForm().length() - 3));
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOFGEARBOX).findAny()
                    .ifPresent(obProp -> result.gearBox.name = obProp.getIRI().getShortForm().substring(0,
                            obProp.getIRI().getShortForm().length() - 3));
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG).findAny()
                    .ifPresent(obProp -> result.weight = obProp.parseDouble());
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASPEAKTORQUERES_M_MAX_UNIT_NM).findAny()
                    .ifPresent(obProp -> result.maximalTorque = obProp.parseDouble());
            reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDRES_N_MAX_UNIT_RPM)
                    .findAny().ifPresent(obProp -> result.maximalRotationSpeed = obProp.parseDouble());
            results.add(result);
        });

        Collections.sort(results, Comparator.comparingDouble(result -> result.weight));
        results.forEach(r -> System.out.println(r));
        System.out.println("Number of results: " + results.size());
        return results;
    }

    private void saveReasonedOntology() throws IOException, OWLOntologyStorageException {
        System.out.println("Reasoned ontology isConsistent: " + reasoner.isConsistent());
        OWLOntology inferOnto = reasoner.getRootOntology();
        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            manager.saveOntology(inferOnto, out);
        }
    }

    private IRI create(String name) {
        return IRI.create("#" + name);
    }

    private OWLAxiom addAxiom(OWLAxiom axiomToAdd) {
        generatedAxioms.add(axiomToAdd);
        manager.addAxiom(ontology, axiomToAdd);
        return axiomToAdd;
    }

}
