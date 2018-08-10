/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.kit.anthropomatik.h2t.expertsystem.io;

import edu.kit.anthropomatik.h2t.expertsystem.MyOWLHelper;
import openllet.core.exceptions.TimerInterruptedException;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLManagerGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OntologyReadAndWriteHelper {

    private static final String fileEnding = ".owl";
    private static final String abstractFileName = "SA_Abstract_Ontology" + fileEnding;
    private static final String componentFileName = "SA_Component_Ontology" + fileEnding;
    private static final String reasoningFileName = "SA_Reasoning_Ontology" + fileEnding;

    private static final Logger logger = LogManager.getLogger(OntologyReadAndWriteHelper.class);

    private String inferdFilePath = null;

    private OWLManagerGroup group;
    private OWLGenericTools genericTool;
    private MyOWLHelper helper;
    private InferredOntologyGenerator inferredOntologyGenerator;

    private List<MyInferredGenerator<? extends OWLIndividualAxiom>> generators = new ArrayList<>();
    private Set<IRI> importIris = new HashSet<>();
    private AtomicBoolean interrupted = new AtomicBoolean(false);
    private OWLOntology inferredOntology;

    public OntologyReadAndWriteHelper(OWLManagerGroup group) {
        this.group = group;
    }

    public void setGenericToolAndHelper(OWLGenericTools genericTool, MyOWLHelper helper) {
        this.genericTool = genericTool;
        this.helper = helper;
        generators.add(new MyInferredClassAssertionAxiomGenerator());
        generators.add(new MyInferredPropertyAssertionGenerator(genericTool));
        generators.add(new MyInferredObjectPropertyAssertionGenerator(genericTool));
        inferredOntologyGenerator = new InferredOntologyGenerator(genericTool.getReasoner(), new ArrayList<>
                (generators));
        try {
            inferredOntology = genericTool.getManager().createOntology(IRI.create("https://h2t.anthropomatik.kit" +
                    "" + ".edu/expertsystem/ontologies/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar
                    .getInstance().get(Calendar.MONTH) + "/inferred"));
            for (IRI importedIri : importIris) {
                genericTool.getManager().applyChange(new AddImport(inferredOntology, genericTool.getManager()
                        .getOWLDataFactory().getOWLImportsDeclaration(importedIri)));
            }
        } catch (OWLOntologyCreationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public OWLOntology loadOntologies() {
        OWLOntology abstracOntology = loadOntology(abstractFileName, false);
        abstracOntology.imports().filter(imported -> imported.getOntologyID().getOntologyIRI().isPresent()).forEach
                (imported -> importIris.add(imported.getOntologyID().getOntologyIRI().get()));

        OWLOntology basicOntology = loadOntology(componentFileName, false);
        OWLOntology ontology = loadOntology(reasoningFileName, true);
        ontology.addAxioms(abstracOntology.axioms());
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
        String localPath = Paths.get("").toAbsolutePath() + "/" + fileName;
        if (setInferdFilePath) {
            inferdFilePath = localPath.substring(0, localPath.length() - fileEnding.length()) + "Inf";
        }
        try {
            return new FileInputStream(localPath);
        } catch (FileNotFoundException e2) {
            return getClass().getResourceAsStream("/" + fileName);
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
        } catch (OWLOntologyStorageException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void saveOntology() throws OWLOntologyStorageException, IOException {
        long startTime = System.currentTimeMillis();
        helper.flush();
        boolean isConsistent = helper.checkConsistency();
        if (inferdFilePath == null || interrupted.get()) {
            return;
        }

        inferredOntology.removeAxioms(inferredOntology.axioms());
        inferredOntology.addAxioms(genericTool.getOntology().axioms());
        if (interrupted.get()) {
            return;
        }
        if (isConsistent) {
            inferredOntologyGenerator.fillOntology(genericTool.getFactory(), inferredOntology);
        }

        try (FileOutputStream out = new FileOutputStream(new File(getNextInferredFileName()))) {
            genericTool.getManager().saveOntology(inferredOntology, out);
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

    private String getNextInferredFileName() {
        int i = 0;
        while (true) {
            String name = inferdFilePath + "_" + i + fileEnding;
            if (!Files.exists(Paths.get(name))) {
                return name;
            }
            ++i;
        }
    }
}
