package edu.kit.expertsystem;
// package edu.kit.expertsystem;
//
// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
//
// import org.semanticweb.owlapi.apibinding.OWLManager;
// import org.semanticweb.owlapi.model.IRI;
// import org.semanticweb.owlapi.model.OWLDataFactory;
// import org.semanticweb.owlapi.model.OWLDataProperty;
// import org.semanticweb.owlapi.model.OWLNamedIndividual;
// import org.semanticweb.owlapi.model.OWLOntology;
// import org.semanticweb.owlapi.model.OWLOntologyCreationException;
// import org.semanticweb.owlapi.model.OWLOntologyManager;
// import org.semanticweb.owlapi.model.OWLOntologyStorageException;
// import org.semanticweb.owlapi.reasoner.BufferingMode;
// import org.semanticweb.owlapi.reasoner.OWLReasoner;
//
// import com.google.common.io.Files;
//
// import edu.kit.expertsystem.generated.ILM50x08;
// import edu.kit.expertsystem.generated.SacFactory;
// import edu.kit.expertsystem.generated.Vocabulary;
// import openllet.owlapi.PelletReasoner;
//
// public class Main {
//
// static final String fileName =
// "C:\\Users\\Oliver\\Dropbox\\Uni\\Informatik_Master\\2. Semester\\Praxis der
// Forschung\\Projektplan\\PrototypeV2.owl";
// static final String fileNameCopy =
// "C:\\Users\\Oliver\\Dropbox\\Uni\\Informatik_Master\\2. Semester\\Praxis der
// Forschung\\Projektplan\\PrototypeV2Copy.owl";
// static final String fileNameInf =
// "C:\\Users\\Oliver\\Dropbox\\Uni\\Informatik_Master\\2. Semester\\Praxis der
// Forschung\\Projektplan\\PrototypeV2CopyInf.owl";
//
// public void testOnto(OWLOntology ontology) {
// SacFactory factory = new SacFactory(ontology);
// ILM50x08 iLM50x08 = factory.createILM50x08("tempILM50x08");
// OWLNamedIndividual ilm50 = iLM50x08.getOwlIndividual();
// for (OWLDataProperty prop :
// ilm50.dataPropertiesInSignature().toArray(OWLDataProperty[]::new)) {
// System.out.println("Props: " + prop);
// }
// }
//
// public static void main(String[] args) throws OWLOntologyCreationException,
// OWLOntologyStorageException,
// FileNotFoundException, IOException {
// File orginalFile = new File(fileName);
// if (!orginalFile.exists()) {
// System.err.println("OntoFile not found!");
// return;
// }
// File workingFile = new File(fileNameCopy);
// Files.copy(orginalFile, workingFile);
//
// OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
// OWLOntology ontology = manager.loadOntologyFromOntologyDocument(workingFile);
//
// OWLDataFactory dataFac = manager.getOWLDataFactory();
//
// OWLReasoner reasoner = new PelletReasoner(ontology, BufferingMode.BUFFERING);
// System.out.println("isConsistent: " + reasoner.isConsistent());
//
// OWLNamedIndividual i =
// dataFac.getOWLNamedIndividual(IRI.create("testPowerSupply"));
// manager.addAxiom(ontology,
// dataFac.getOWLClassAssertionAxiom(Vocabulary.CLASS_POWERSUPPLY, i));
//
//
// // Stream<OWLNamedIndividual> filter = ontology.individualsInSignature()
// // .filter(ind ->
// ind.getIRI().getIRIString().endsWith("ILM50x08Properties"));
// // double a = reasoner
// // .getDataPropertyValues(filter.findFirst().get(),
// // Vocabulary.DATA_PROPERTY_HASCURRENT_UNIT_A)
// // .iterator().next().parseDouble();
// // filter = ontology.individualsInSignature()
// // .filter(ind ->
// ind.getIRI().getIRIString().endsWith("ILM50x08Properties"));
// // double v = reasoner
// // .getDataPropertyValues(filter.findFirst().get(),
// // Vocabulary.DATA_PROPERTY_HASVOLTAGE_UNIT_V)
// // .iterator().next().parseDouble();
// //
// // OWLDataPropertyAssertionAxiom voltAx = dataFac
// //
// .getOWLDataPropertyAssertionAxiom(Vocabulary.DATA_PROPERTY_HASVOLTAGE_UNIT_V,
// // i, v);
// // manager.addAxiom(ontology, voltAx);
// //
// // OWLLiteral amper = dataFac.getOWLLiteral(a);
// // OWLDataPropertyAssertionAxiom amperAx = dataFac
// //
// .getOWLDataPropertyAssertionAxiom(Vocabulary.DATA_PROPERTY_HASCURRENT_UNIT_A,
// // i, amper);
// // manager.addAxiom(ontology, amperAx);
// //
// // reasoner.flush();
// // Set<OWLLiteral> dataPropertyValues = reasoner.getDataPropertyValues(i,
// // Vocabulary.DATA_PROPERTY_HASPOWER_UNIT_W);
// // for (OWLLiteral lit : dataPropertyValues) {
// // System.out.println("Power should be 155W and it is " + lit.parseDouble() +
// // "W");
// // }
//
// reasoner.flush();
// System.out.println("isConsistent: " + reasoner.isConsistent());
// OWLOntology inferOnto = reasoner.getRootOntology();
// try (FileOutputStream out = new FileOutputStream(new File(fileNameInf))){
// manager.saveOntology(inferOnto, out);
// }
//
// // for (OWLClass c : ontology.classesInSignature().toArray(OWLClass[]::new))
// {
// // // the boolean argument specifies direct subclasses
// // for (OWLNamedIndividual i : reasoner.getInstances(c, true).entities()
// // .toArray(OWLNamedIndividual[]::new)) {
// // System.out.println(i + ":" + c);
// // // look up all property assertions
// // for (OWLObjectProperty op : ontology.objectPropertiesInSignature()
// // .toArray(OWLObjectProperty[]::new)) {
// // NodeSet<OWLNamedIndividual> petValuesNodeSet =
// // reasoner.getObjectPropertyValues(i, op);
// // for (OWLNamedIndividual value : petValuesNodeSet.entities()
// // .toArray(OWLNamedIndividual[]::new)) {
// // System.out.println(i + " " + op + " " + value);
// // }
// // }
// // }
// // }
//
// // // Use an inferred axiom generators
// // List<InferredAxiomGenerator<? extends OWLAxiom>> gens = Collections
// // .singletonList(new InferredSubClassAxiomGenerator());
// // OWLOntology infOnt = manager.createOntology();
// // // create the inferred ontology generator
// // InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
// // gens);
// // iog.fillOntology(dataFac, infOnt);
// // try (FileOutputStream out = new FileOutputStream(fileNameInf)) {
// // infOnt.saveOntology(out);
// // }
//
// // OWLOntologyWalker walker = new
// // OWLOntologyWalker(Collections.singleton(ontology));
// // // Now ask our walker to walk over the ontology
// // OWLOntologyWalkerVisitor visitor = new OWLOntologyWalkerVisitor(walker) {
// // @Override
// // public void visit(OWLLiteral desc) {
// // System.out.println(desc);
// // System.out.println(" " + getCurrentAxiom());
// // }
// // };
// // // Have the walker walk...
// // walker.walkStructure(visitor);
//
// // OWL2DLProfile profile = new OWL2DLProfile();
// // OWLProfileReport report = profile.checkOntology(ontology);
// // for (OWLProfileViolation v : report.getViolations()) {
// // System.out.println(v);
// // }
//
// // new Main().testOnto(ontology);
// }
//
// }
