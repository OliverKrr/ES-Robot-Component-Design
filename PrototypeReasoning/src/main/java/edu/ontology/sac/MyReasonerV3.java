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

        reasoner.subClasses(Vocabulary.CLASS_CSD_2A, true).forEach(gear -> {
            OWLNamedIndividual gearInd = dataFac
                    .getOWLNamedIndividual(create(gear.getIRI().getShortForm() + "Ind"));
            addAxiom(dataFac.getOWLClassAssertionAxiom(gear, gearInd));
        });
    }

    private void addRequirements(Requirements requirements) {
        System.out.println(requirements);
        OWLNamedIndividual requirementsInd = dataFac.getOWLNamedIndividual(create("requirementsInd"));
        addAxiom(dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_REQUIREMENTS, requirementsInd));

        OWLDataPropertyAssertionAxiom maximalTorqueReqMin = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASMAXIMALTORQUEREQMIN, requirementsInd, dataFac.getOWLLiteral(
                        String.valueOf(requirements.maximalTorque.min), OWL2Datatype.XSD_DECIMAL));
        addAxiom(maximalTorqueReqMin);

        OWLDataPropertyAssertionAxiom maximalTorqueReqMax = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASMAXIMALTORQUEREQMAX, requirementsInd, dataFac.getOWLLiteral(
                        String.valueOf(requirements.maximalTorque.max), OWL2Datatype.XSD_DECIMAL));
        addAxiom(maximalTorqueReqMax);

        OWLDataPropertyAssertionAxiom rotationSpeedMin = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASMAXIMALROTATIONSPEEDREQMIN, requirementsInd,
                dataFac.getOWLLiteral(String.valueOf(requirements.maximalRotationSpeed.min),
                        OWL2Datatype.XSD_DECIMAL));
        addAxiom(rotationSpeedMin);

        OWLDataPropertyAssertionAxiom rotationSpeedMax = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASMAXIMALROTATIONSPEEDREQMAX, requirementsInd,
                dataFac.getOWLLiteral(String.valueOf(requirements.maximalRotationSpeed.max),
                        OWL2Datatype.XSD_DECIMAL));
        addAxiom(rotationSpeedMax);

        OWLDataPropertyAssertionAxiom weightMin = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASWEIGHTREQMIN_M_UNIT_KG, requirementsInd,
                dataFac.getOWLLiteral(String.valueOf(requirements.weight.min), OWL2Datatype.XSD_DECIMAL));
        addAxiom(weightMin);

        OWLDataPropertyAssertionAxiom weightMax = dataFac.getOWLDataPropertyAssertionAxiom(
                Vocabulary.DATA_PROPERTY_HASWEIGHTREQMAX_M_UNIT_KG, requirementsInd,
                dataFac.getOWLLiteral(String.valueOf(requirements.weight.max), OWL2Datatype.XSD_DECIMAL));
        addAxiom(weightMax);
    }

    private List<Result> reason() {
        List<Result> results = new ArrayList<>();

        reasoner.instances(Vocabulary.CLASS_SATISFIEDENGINE).forEach(engine -> {
            // System.out.println("CLASS_SATISFIEDENGINE: " + engine);
            reasoner.instances(Vocabulary.CLASS_SATISFIEDGEAR).forEach(gear -> {
                // System.out.println("CLASS_SATISFIEDGEAR: " + gear);

                OWLNamedIndividual matchInd = dataFac.getOWLNamedIndividual(create("Match"
                        + engine.getIRI().getShortForm() + "With" + gear.getIRI().getShortForm() + "Ind"));
                addAxiom(dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_ENGINEGEARMATCH, matchInd));

                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                        matchInd, engine));
                addAxiom(dataFac.getOWLObjectPropertyAssertionAxiom(Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF,
                        matchInd, gear));
            });
        });
        reasoner.flush();
        reasoner.instances(Vocabulary.CLASS_SATISFIEDENGINEGEARMATCH).forEach(en -> {
            Result result = new Result();
            reasoner.objectPropertyValues(en, Vocabulary.OBJECT_PROPERTY_ISCOMPOSEDOF)
                    .forEach(obProp -> reasoner.types(obProp).forEach(clas -> {
                        if (clas.equals(Vocabulary.CLASS_ENGINE)) {
                            result.engine.name = obProp.getIRI().getShortForm();
                            result.engine.name = result.engine.name.substring(0,
                                    result.engine.name.length() - 3);
                        } else if (clas.equals(Vocabulary.CLASS_GEAR)) {
                            result.gear.name = obProp.getIRI().getShortForm();
                            result.gear.name = result.gear.name.substring(0, result.gear.name.length() - 3);
                        }
                    }));
            result.weight = reasoner.dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASWEIGHT_M_UNIT_KG)
                    .findAny().orElse(new OWLLiteralImplDouble(-1)).parseDouble();
            result.maximalTorque = reasoner
                    .dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALTORQUERES).findAny()
                    .orElse(new OWLLiteralImplDouble(-1)).parseDouble();
            result.maximalRotationSpeed = reasoner
                    .dataPropertyValues(en, Vocabulary.DATA_PROPERTY_HASMAXIMALROTATIONSPEEDRES).findAny()
                    .orElse(new OWLLiteralImplDouble(-1)).parseDouble();
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
