package edu.ontology.sac;

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

import edu.ontology.sac.generated.Vocabulary;
import edu.ontology.sac.model.Requirements;
import edu.ontology.sac.model.Result;
import openllet.owlapi.PelletReasoner;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplDouble;

public class MyReasonerV3 {

    private static final String fileEnding = ".owl";
    private static final String orginalFileName = "PrototypeV3" + fileEnding;

    private String orginalFilePath = "C:\\Users\\Oliver\\Dropbox\\Uni\\Informatik_Master\\2. Semester\\Praxis der Forschung\\Projektplan\\"
            + orginalFileName;
    private String copyedFilePath;
    private String inferdFilePath;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFac;
    private OWLReasoner reasoner;

    private List<OWLAxiom> generatedAxioms = new ArrayList<>();

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

    public List<Result> startReasoning(Requirements requirements) {
        try {
            ontology.removeAxioms(generatedAxioms);
            generatedAxioms.clear();
            addRequirements(requirements);
            createBasicIndividuals();
            reasoner.flush();
            return reason();
        } finally {
            try {
                saveReasonedOntology();
            } catch (OWLOntologyStorageException | IOException e) {
                e.printStackTrace();
            }
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

                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                        matchInd, motor));
                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                        matchInd, gearBox));
            });
        });
        reasoner.flush();
        reasoner.instances(Vocabulary.CLASS_SATISFIEDMOTORGEARBOXMATCH).forEach(en -> {
            Result result = new Result();
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF)
                    .forEach(obProp -> reasoner.types(obProp).forEach(clas -> {
                        if (clas.equals(Vocabulary.CLASS_MOTOR)) {
                            result.motor.name = obProp.getIRI().getShortForm();
                            result.motor.name = result.motor.name.substring(0,
                                    result.motor.name.length() - 3);
                        } else if (clas.equals(Vocabulary.CLASS_GEARBOX)) {
                            result.gearBox.name = obProp.getIRI().getShortForm();
                            result.gearBox.name = result.gearBox.name.substring(0,
                                    result.gearBox.name.length() - 3);
                        }
                    }));
            result.weight = reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG)
                    .findAny().orElse(new OWLLiteralImplDouble(-1)).parseDouble();
            result.maximalTorque = reasoner
                    .dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASPEAKTORQUERES_M_MAX_UNIT_NM).findAny()
                    .orElse(new OWLLiteralImplDouble(-1)).parseDouble();
            result.maximalRotationSpeed = reasoner
                    .dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALSPEEDRES_N_MAX_UNIT_RPM)
                    .findAny().orElse(new OWLLiteralImplDouble(-1)).parseDouble();
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
