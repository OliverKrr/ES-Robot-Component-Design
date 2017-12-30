package edu.kit.expertsystem.io;

import edu.kit.expertsystem.MyOWLHelper;
import openllet.core.exceptions.TimerInterruptedException;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLManagerGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OntologyReadAndWriteHelper {

    private static final String fileEnding = ".owl";
    private static final String domainFileName = "SAC_Domain_Ontology" + fileEnding;
    private static final String reasoningFileName = "SAC_Reasoning_Ontology" + fileEnding;

    private static final String myPath = "C:\\Users\\Oliver\\Dropbox\\MyGits\\PraxisDerForschung\\KnowledgeBase\\src"
            + "\\main\\resources\\";

    private static final Logger logger = LogManager.getLogger(OntologyReadAndWriteHelper.class);

    private String inferdFilePath = null;

    private OWLManagerGroup group;
    private OWLGenericTools genericTool;
    private MyOWLHelper helper;
    private InferredOntologyGenerator inferredOntologyGenerator;

    private List<MyInferredGenerator<? extends OWLIndividualAxiom>> generators = new ArrayList<>();
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    public OntologyReadAndWriteHelper(OWLManagerGroup group) {
        this.group = group;
    }

    public void setGenericToolAndHelper(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
        generators.add(new MyInferredClassAssertionAxiomGenerator());
        generators.add(new MyInferredPropertyAssertionGenerator(genericTool));
        inferredOntologyGenerator = new InferredOntologyGenerator(genericTool.getReasoner(), new ArrayList<>
                (generators));
    }

    public OWLOntology loadOntologies() {
        OWLOntology basicOntology = loadOntology(domainFileName, false);
        OWLOntology ontology = loadOntology(reasoningFileName, true);
        ontology.addAxioms(basicOntology.axioms());
        return ontology;
    }

    private OWLOntology loadOntology(String fileName, boolean setInferdFilePath) {
        try (InputStream ontoStream = readOntology(fileName, setInferdFilePath)) {
            return group.getVolatileManager().loadOntologyFromOntologyDocument(ontoStream);
        } catch (OWLOntologyCreationException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Loading the ontology failed");
        }
    }

    private InputStream readOntology(String fileName, boolean setInferdFilePath) {
        String path = myPath + fileName;
        if (setInferdFilePath) {
            inferdFilePath = path.substring(0, path.length() - fileEnding.length()) + "Inf" + fileEnding;
        }
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            String localPath = Paths.get("").toAbsolutePath() + "/" + fileName;
            if (setInferdFilePath) {
                inferdFilePath = localPath.substring(0, localPath.length() - fileEnding.length()) + "Inf" + fileEnding;
            }
            try {
                return new FileInputStream(localPath);
            } catch (FileNotFoundException e2) {
                return getClass().getResourceAsStream("/" + fileName);
            }
        }
    }

    public void saveInferredOntology() {
        try {
            saveOntology();
        } catch (ReasonerInterruptedException | TimerInterruptedException e) {
            // It is OK if we interrupted ourself
            if (!interrupted.get()) {
                throw e;
            }
        } catch (OWLOntologyStorageException | IOException | OWLOntologyCreationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void saveOntology() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        long startTime = System.currentTimeMillis();
        helper.flush();
        boolean isConsistent = helper.checkConsistency();
        if (inferdFilePath == null || interrupted.get()) {
            return;
        }

        OWLOntology inferOnto = genericTool.getManager().createOntology();
        inferOnto.addAxioms(genericTool.getOntology().axioms());
        if (interrupted.get()) {
            return;
        }
        if (isConsistent) {
            inferredOntologyGenerator.fillOntology(genericTool.getFactory(), inferOnto);
        }

        try (FileOutputStream out = new FileOutputStream(new File(inferdFilePath))) {
            genericTool.getManager().saveOntology(inferOnto, out);
        }
        logger.info("Time needed for saveReasonedOntology: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public void interruptReasoning() {
        interrupted.set(true);
        generators.forEach(MyInferredGenerator::stop);
    }

    public void resetInterrupt() {
        interrupted.set(false);
        generators.forEach(MyInferredGenerator::reset);
    }

}
