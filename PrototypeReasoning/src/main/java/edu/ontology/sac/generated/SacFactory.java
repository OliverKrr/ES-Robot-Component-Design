package edu.ontology.sac.generated;

import java.util.Collection;

import org.protege.owl.codegeneration.CodeGenerationFactory;
import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.FactoryHelper;
import org.protege.owl.codegeneration.impl.ProtegeJavaMapping;
import org.protege.owl.codegeneration.inference.CodeGenerationInference;
import org.protege.owl.codegeneration.inference.SimpleInference;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import edu.ontology.sac.generated.impl.DefaultBrushlessDcEngine;
import edu.ontology.sac.generated.impl.DefaultCPL_25_160_2A;
import edu.ontology.sac.generated.impl.DefaultCPL_2A;
import edu.ontology.sac.generated.impl.DefaultCSD_14_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_14_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_17_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_17_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_20_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_20_160_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_20_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_25_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_25_160_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_25_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_2A;
import edu.ontology.sac.generated.impl.DefaultCSD_32_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_32_160_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_32_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_40_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_40_160_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_40_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_50_100_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_50_160_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSD_50_50_2A_BB;
import edu.ontology.sac.generated.impl.DefaultCSG_2A;
import edu.ontology.sac.generated.impl.DefaultDevice;
import edu.ontology.sac.generated.impl.DefaultElectricEngine;
import edu.ontology.sac.generated.impl.DefaultEngine;
import edu.ontology.sac.generated.impl.DefaultEngineGearMatch;
import edu.ontology.sac.generated.impl.DefaultEngineProperties;
import edu.ontology.sac.generated.impl.DefaultEpicyclicGearing;
import edu.ontology.sac.generated.impl.DefaultGear;
import edu.ontology.sac.generated.impl.DefaultGearProperties;
import edu.ontology.sac.generated.impl.DefaultGearRatio;
import edu.ontology.sac.generated.impl.DefaultHarmonicDrive;
import edu.ontology.sac.generated.impl.DefaultHarmonicDriveComponentSets2A;
import edu.ontology.sac.generated.impl.DefaultHarmonicDriveUnits;
import edu.ontology.sac.generated.impl.DefaultILM115x25;
import edu.ontology.sac.generated.impl.DefaultILM115x50;
import edu.ontology.sac.generated.impl.DefaultILM25x04;
import edu.ontology.sac.generated.impl.DefaultILM25x08;
import edu.ontology.sac.generated.impl.DefaultILM38x06;
import edu.ontology.sac.generated.impl.DefaultILM38x12;
import edu.ontology.sac.generated.impl.DefaultILM50x08;
import edu.ontology.sac.generated.impl.DefaultILM50x14;
import edu.ontology.sac.generated.impl.DefaultILM70x10;
import edu.ontology.sac.generated.impl.DefaultILM70x18;
import edu.ontology.sac.generated.impl.DefaultILM85x04;
import edu.ontology.sac.generated.impl.DefaultILM85x13;
import edu.ontology.sac.generated.impl.DefaultILM85x23;
import edu.ontology.sac.generated.impl.DefaultILM85x26;
import edu.ontology.sac.generated.impl.DefaultInnerDiameter;
import edu.ontology.sac.generated.impl.DefaultInput;
import edu.ontology.sac.generated.impl.DefaultInputOutput;
import edu.ontology.sac.generated.impl.DefaultLength;
import edu.ontology.sac.generated.impl.DefaultMaxEfficiency;
import edu.ontology.sac.generated.impl.DefaultMaxInputSpeedGrease;
import edu.ontology.sac.generated.impl.DefaultMaxNominalTorque;
import edu.ontology.sac.generated.impl.DefaultNominalPowerSupply;
import edu.ontology.sac.generated.impl.DefaultNominalRotationSpeed;
import edu.ontology.sac.generated.impl.DefaultNominalTorque;
import edu.ontology.sac.generated.impl.DefaultOuterDiameter;
import edu.ontology.sac.generated.impl.DefaultOutput;
import edu.ontology.sac.generated.impl.DefaultPowerLoose;
import edu.ontology.sac.generated.impl.DefaultPowerSupply;
import edu.ontology.sac.generated.impl.DefaultProperty;
import edu.ontology.sac.generated.impl.DefaultRepeatedPeakTorque;
import edu.ontology.sac.generated.impl.DefaultRequirements;
import edu.ontology.sac.generated.impl.DefaultRoboDriveServoKibILM;
import edu.ontology.sac.generated.impl.DefaultRotationSpeed;
import edu.ontology.sac.generated.impl.DefaultSatisfied;
import edu.ontology.sac.generated.impl.DefaultSatisfiedEngine;
import edu.ontology.sac.generated.impl.DefaultSatisfiedEngineGearMatch;
import edu.ontology.sac.generated.impl.DefaultSatisfiedGear;
import edu.ontology.sac.generated.impl.DefaultSatisfiedMaximalTorque;
import edu.ontology.sac.generated.impl.DefaultSatisfiedRotationSpeed;
import edu.ontology.sac.generated.impl.DefaultSatisfiedWeight;
import edu.ontology.sac.generated.impl.DefaultSize;
import edu.ontology.sac.generated.impl.DefaultTorque;
import edu.ontology.sac.generated.impl.DefaultWeight;

/**
 * A class that serves as the entry point to the generated code providing access
 * to existing individuals in the ontology and the ability to create new individuals in the ontology.<p>
 * 
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: SacFactory<br>
 * @version generated on Thu Sep 28 16:14:38 CEST 2017 by Oliver
 */
public class SacFactory implements CodeGenerationFactory {
    private OWLOntology ontology;
    private ProtegeJavaMapping javaMapping = new ProtegeJavaMapping();
    private FactoryHelper delegate;
    private CodeGenerationInference inference;

    public SacFactory(OWLOntology ontology) {
	    this(ontology, new SimpleInference(ontology));
    }
    
    public SacFactory(OWLOntology ontology, CodeGenerationInference inference) {
        this.ontology = ontology;
        this.inference = inference;
        javaMapping.initialize(ontology, inference);
        delegate = new FactoryHelper(ontology, inference);
    }

    public OWLOntology getOwlOntology() {
        return ontology;
    }
    
    public void saveOwlOntology() throws OWLOntologyStorageException {
        ontology.getOWLOntologyManager().saveOntology(ontology);
    }
    
    public void flushOwlReasoner() {
        delegate.flushOwlReasoner();
    }
    
    public boolean canAs(WrappedIndividual resource, Class<? extends WrappedIndividual> javaInterface) {
    	return javaMapping.canAs(resource, javaInterface);
    }
    
    public  <X extends WrappedIndividual> X as(WrappedIndividual resource, Class<? extends X> javaInterface) {
    	return javaMapping.as(resource, javaInterface);
    }
    
    public Class<?> getJavaInterfaceFromOwlClass(OWLClass cls) {
        return javaMapping.getJavaInterfaceFromOwlClass(cls);
    }
    
    public OWLClass getOwlClassFromJavaInterface(Class<?> javaInterface) {
	    return javaMapping.getOwlClassFromJavaInterface(javaInterface);
    }
    
    public CodeGenerationInference getInference() {
        return inference;
    }

    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#BrushlessDcEngine
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#BrushlessDcEngine", BrushlessDcEngine.class, DefaultBrushlessDcEngine.class);
    }

    /**
     * Creates an instance of type BrushlessDcEngine.  Modifies the underlying ontology.
     */
    public BrushlessDcEngine createBrushlessDcEngine(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_BRUSHLESSDCENGINE, DefaultBrushlessDcEngine.class);
    }

    /**
     * Gets an instance of type BrushlessDcEngine with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public BrushlessDcEngine getBrushlessDcEngine(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_BRUSHLESSDCENGINE, DefaultBrushlessDcEngine.class);
    }

    /**
     * Gets all instances of BrushlessDcEngine from the ontology.
     */
    public Collection<? extends BrushlessDcEngine> getAllBrushlessDcEngineInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_BRUSHLESSDCENGINE, DefaultBrushlessDcEngine.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CPL_25_160_2A
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CPL_25_160_2A", CPL_25_160_2A.class, DefaultCPL_25_160_2A.class);
    }

    /**
     * Creates an instance of type CPL_25_160_2A.  Modifies the underlying ontology.
     */
    public CPL_25_160_2A createCPL_25_160_2A(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CPL_25_160_2A, DefaultCPL_25_160_2A.class);
    }

    /**
     * Gets an instance of type CPL_25_160_2A with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CPL_25_160_2A getCPL_25_160_2A(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CPL_25_160_2A, DefaultCPL_25_160_2A.class);
    }

    /**
     * Gets all instances of CPL_25_160_2A from the ontology.
     */
    public Collection<? extends CPL_25_160_2A> getAllCPL_25_160_2AInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CPL_25_160_2A, DefaultCPL_25_160_2A.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CPL_2A
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CPL_2A", CPL_2A.class, DefaultCPL_2A.class);
    }

    /**
     * Creates an instance of type CPL_2A.  Modifies the underlying ontology.
     */
    public CPL_2A createCPL_2A(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CPL_2A, DefaultCPL_2A.class);
    }

    /**
     * Gets an instance of type CPL_2A with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CPL_2A getCPL_2A(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CPL_2A, DefaultCPL_2A.class);
    }

    /**
     * Gets all instances of CPL_2A from the ontology.
     */
    public Collection<? extends CPL_2A> getAllCPL_2AInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CPL_2A, DefaultCPL_2A.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_14_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_14_100_2A_BB", CSD_14_100_2A_BB.class, DefaultCSD_14_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_14_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_14_100_2A_BB createCSD_14_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_14_100_2A_BB, DefaultCSD_14_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_14_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_14_100_2A_BB getCSD_14_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_14_100_2A_BB, DefaultCSD_14_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_14_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_14_100_2A_BB> getAllCSD_14_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_14_100_2A_BB, DefaultCSD_14_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_14_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_14_50_2A_BB", CSD_14_50_2A_BB.class, DefaultCSD_14_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_14_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_14_50_2A_BB createCSD_14_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_14_50_2A_BB, DefaultCSD_14_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_14_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_14_50_2A_BB getCSD_14_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_14_50_2A_BB, DefaultCSD_14_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_14_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_14_50_2A_BB> getAllCSD_14_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_14_50_2A_BB, DefaultCSD_14_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_17_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_17_100_2A_BB", CSD_17_100_2A_BB.class, DefaultCSD_17_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_17_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_17_100_2A_BB createCSD_17_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_17_100_2A_BB, DefaultCSD_17_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_17_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_17_100_2A_BB getCSD_17_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_17_100_2A_BB, DefaultCSD_17_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_17_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_17_100_2A_BB> getAllCSD_17_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_17_100_2A_BB, DefaultCSD_17_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_17_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_17_50_2A_BB", CSD_17_50_2A_BB.class, DefaultCSD_17_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_17_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_17_50_2A_BB createCSD_17_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_17_50_2A_BB, DefaultCSD_17_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_17_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_17_50_2A_BB getCSD_17_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_17_50_2A_BB, DefaultCSD_17_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_17_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_17_50_2A_BB> getAllCSD_17_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_17_50_2A_BB, DefaultCSD_17_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_100_2A_BB", CSD_20_100_2A_BB.class, DefaultCSD_20_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_20_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_20_100_2A_BB createCSD_20_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_20_100_2A_BB, DefaultCSD_20_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_20_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_20_100_2A_BB getCSD_20_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_20_100_2A_BB, DefaultCSD_20_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_20_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_20_100_2A_BB> getAllCSD_20_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_20_100_2A_BB, DefaultCSD_20_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_160_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_160_2A_BB", CSD_20_160_2A_BB.class, DefaultCSD_20_160_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_20_160_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_20_160_2A_BB createCSD_20_160_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_20_160_2A_BB, DefaultCSD_20_160_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_20_160_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_20_160_2A_BB getCSD_20_160_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_20_160_2A_BB, DefaultCSD_20_160_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_20_160_2A_BB from the ontology.
     */
    public Collection<? extends CSD_20_160_2A_BB> getAllCSD_20_160_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_20_160_2A_BB, DefaultCSD_20_160_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_20_50_2A_BB", CSD_20_50_2A_BB.class, DefaultCSD_20_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_20_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_20_50_2A_BB createCSD_20_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_20_50_2A_BB, DefaultCSD_20_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_20_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_20_50_2A_BB getCSD_20_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_20_50_2A_BB, DefaultCSD_20_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_20_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_20_50_2A_BB> getAllCSD_20_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_20_50_2A_BB, DefaultCSD_20_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_100_2A_BB", CSD_25_100_2A_BB.class, DefaultCSD_25_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_25_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_25_100_2A_BB createCSD_25_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_25_100_2A_BB, DefaultCSD_25_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_25_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_25_100_2A_BB getCSD_25_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_25_100_2A_BB, DefaultCSD_25_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_25_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_25_100_2A_BB> getAllCSD_25_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_25_100_2A_BB, DefaultCSD_25_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_160_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_160_2A_BB", CSD_25_160_2A_BB.class, DefaultCSD_25_160_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_25_160_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_25_160_2A_BB createCSD_25_160_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_25_160_2A_BB, DefaultCSD_25_160_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_25_160_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_25_160_2A_BB getCSD_25_160_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_25_160_2A_BB, DefaultCSD_25_160_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_25_160_2A_BB from the ontology.
     */
    public Collection<? extends CSD_25_160_2A_BB> getAllCSD_25_160_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_25_160_2A_BB, DefaultCSD_25_160_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_25_50_2A_BB", CSD_25_50_2A_BB.class, DefaultCSD_25_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_25_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_25_50_2A_BB createCSD_25_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_25_50_2A_BB, DefaultCSD_25_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_25_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_25_50_2A_BB getCSD_25_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_25_50_2A_BB, DefaultCSD_25_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_25_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_25_50_2A_BB> getAllCSD_25_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_25_50_2A_BB, DefaultCSD_25_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_2A
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_2A", CSD_2A.class, DefaultCSD_2A.class);
    }

    /**
     * Creates an instance of type CSD_2A.  Modifies the underlying ontology.
     */
    public CSD_2A createCSD_2A(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_2A, DefaultCSD_2A.class);
    }

    /**
     * Gets an instance of type CSD_2A with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_2A getCSD_2A(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_2A, DefaultCSD_2A.class);
    }

    /**
     * Gets all instances of CSD_2A from the ontology.
     */
    public Collection<? extends CSD_2A> getAllCSD_2AInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_2A, DefaultCSD_2A.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_100_2A_BB", CSD_32_100_2A_BB.class, DefaultCSD_32_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_32_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_32_100_2A_BB createCSD_32_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_32_100_2A_BB, DefaultCSD_32_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_32_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_32_100_2A_BB getCSD_32_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_32_100_2A_BB, DefaultCSD_32_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_32_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_32_100_2A_BB> getAllCSD_32_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_32_100_2A_BB, DefaultCSD_32_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_160_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_160_2A_BB", CSD_32_160_2A_BB.class, DefaultCSD_32_160_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_32_160_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_32_160_2A_BB createCSD_32_160_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_32_160_2A_BB, DefaultCSD_32_160_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_32_160_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_32_160_2A_BB getCSD_32_160_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_32_160_2A_BB, DefaultCSD_32_160_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_32_160_2A_BB from the ontology.
     */
    public Collection<? extends CSD_32_160_2A_BB> getAllCSD_32_160_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_32_160_2A_BB, DefaultCSD_32_160_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_32_50_2A_BB", CSD_32_50_2A_BB.class, DefaultCSD_32_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_32_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_32_50_2A_BB createCSD_32_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_32_50_2A_BB, DefaultCSD_32_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_32_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_32_50_2A_BB getCSD_32_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_32_50_2A_BB, DefaultCSD_32_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_32_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_32_50_2A_BB> getAllCSD_32_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_32_50_2A_BB, DefaultCSD_32_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_100_2A_BB", CSD_40_100_2A_BB.class, DefaultCSD_40_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_40_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_40_100_2A_BB createCSD_40_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_40_100_2A_BB, DefaultCSD_40_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_40_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_40_100_2A_BB getCSD_40_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_40_100_2A_BB, DefaultCSD_40_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_40_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_40_100_2A_BB> getAllCSD_40_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_40_100_2A_BB, DefaultCSD_40_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_160_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_160_2A_BB", CSD_40_160_2A_BB.class, DefaultCSD_40_160_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_40_160_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_40_160_2A_BB createCSD_40_160_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_40_160_2A_BB, DefaultCSD_40_160_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_40_160_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_40_160_2A_BB getCSD_40_160_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_40_160_2A_BB, DefaultCSD_40_160_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_40_160_2A_BB from the ontology.
     */
    public Collection<? extends CSD_40_160_2A_BB> getAllCSD_40_160_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_40_160_2A_BB, DefaultCSD_40_160_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_40_50_2A_BB", CSD_40_50_2A_BB.class, DefaultCSD_40_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_40_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_40_50_2A_BB createCSD_40_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_40_50_2A_BB, DefaultCSD_40_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_40_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_40_50_2A_BB getCSD_40_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_40_50_2A_BB, DefaultCSD_40_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_40_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_40_50_2A_BB> getAllCSD_40_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_40_50_2A_BB, DefaultCSD_40_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_100_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_100_2A_BB", CSD_50_100_2A_BB.class, DefaultCSD_50_100_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_50_100_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_50_100_2A_BB createCSD_50_100_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_50_100_2A_BB, DefaultCSD_50_100_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_50_100_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_50_100_2A_BB getCSD_50_100_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_50_100_2A_BB, DefaultCSD_50_100_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_50_100_2A_BB from the ontology.
     */
    public Collection<? extends CSD_50_100_2A_BB> getAllCSD_50_100_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_50_100_2A_BB, DefaultCSD_50_100_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_160_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_160_2A_BB", CSD_50_160_2A_BB.class, DefaultCSD_50_160_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_50_160_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_50_160_2A_BB createCSD_50_160_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_50_160_2A_BB, DefaultCSD_50_160_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_50_160_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_50_160_2A_BB getCSD_50_160_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_50_160_2A_BB, DefaultCSD_50_160_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_50_160_2A_BB from the ontology.
     */
    public Collection<? extends CSD_50_160_2A_BB> getAllCSD_50_160_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_50_160_2A_BB, DefaultCSD_50_160_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_50_2A_BB
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSD_50_50_2A_BB", CSD_50_50_2A_BB.class, DefaultCSD_50_50_2A_BB.class);
    }

    /**
     * Creates an instance of type CSD_50_50_2A_BB.  Modifies the underlying ontology.
     */
    public CSD_50_50_2A_BB createCSD_50_50_2A_BB(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSD_50_50_2A_BB, DefaultCSD_50_50_2A_BB.class);
    }

    /**
     * Gets an instance of type CSD_50_50_2A_BB with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSD_50_50_2A_BB getCSD_50_50_2A_BB(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSD_50_50_2A_BB, DefaultCSD_50_50_2A_BB.class);
    }

    /**
     * Gets all instances of CSD_50_50_2A_BB from the ontology.
     */
    public Collection<? extends CSD_50_50_2A_BB> getAllCSD_50_50_2A_BBInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSD_50_50_2A_BB, DefaultCSD_50_50_2A_BB.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSG_2A
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#CSG_2A", CSG_2A.class, DefaultCSG_2A.class);
    }

    /**
     * Creates an instance of type CSG_2A.  Modifies the underlying ontology.
     */
    public CSG_2A createCSG_2A(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_CSG_2A, DefaultCSG_2A.class);
    }

    /**
     * Gets an instance of type CSG_2A with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public CSG_2A getCSG_2A(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_CSG_2A, DefaultCSG_2A.class);
    }

    /**
     * Gets all instances of CSG_2A from the ontology.
     */
    public Collection<? extends CSG_2A> getAllCSG_2AInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_CSG_2A, DefaultCSG_2A.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Device
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Device", Device.class, DefaultDevice.class);
    }

    /**
     * Creates an instance of type Device.  Modifies the underlying ontology.
     */
    public Device createDevice(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_DEVICE, DefaultDevice.class);
    }

    /**
     * Gets an instance of type Device with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Device getDevice(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_DEVICE, DefaultDevice.class);
    }

    /**
     * Gets all instances of Device from the ontology.
     */
    public Collection<? extends Device> getAllDeviceInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_DEVICE, DefaultDevice.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ElectricEngine
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ElectricEngine", ElectricEngine.class, DefaultElectricEngine.class);
    }

    /**
     * Creates an instance of type ElectricEngine.  Modifies the underlying ontology.
     */
    public ElectricEngine createElectricEngine(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ELECTRICENGINE, DefaultElectricEngine.class);
    }

    /**
     * Gets an instance of type ElectricEngine with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ElectricEngine getElectricEngine(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ELECTRICENGINE, DefaultElectricEngine.class);
    }

    /**
     * Gets all instances of ElectricEngine from the ontology.
     */
    public Collection<? extends ElectricEngine> getAllElectricEngineInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ELECTRICENGINE, DefaultElectricEngine.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Engine
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Engine", Engine.class, DefaultEngine.class);
    }

    /**
     * Creates an instance of type Engine.  Modifies the underlying ontology.
     */
    public Engine createEngine(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ENGINE, DefaultEngine.class);
    }

    /**
     * Gets an instance of type Engine with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Engine getEngine(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ENGINE, DefaultEngine.class);
    }

    /**
     * Gets all instances of Engine from the ontology.
     */
    public Collection<? extends Engine> getAllEngineInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ENGINE, DefaultEngine.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EngineGearMatch
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EngineGearMatch", EngineGearMatch.class, DefaultEngineGearMatch.class);
    }

    /**
     * Creates an instance of type EngineGearMatch.  Modifies the underlying ontology.
     */
    public EngineGearMatch createEngineGearMatch(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ENGINEGEARMATCH, DefaultEngineGearMatch.class);
    }

    /**
     * Gets an instance of type EngineGearMatch with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public EngineGearMatch getEngineGearMatch(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ENGINEGEARMATCH, DefaultEngineGearMatch.class);
    }

    /**
     * Gets all instances of EngineGearMatch from the ontology.
     */
    public Collection<? extends EngineGearMatch> getAllEngineGearMatchInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ENGINEGEARMATCH, DefaultEngineGearMatch.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EngineProperties
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EngineProperties", EngineProperties.class, DefaultEngineProperties.class);
    }

    /**
     * Creates an instance of type EngineProperties.  Modifies the underlying ontology.
     */
    public EngineProperties createEngineProperties(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ENGINEPROPERTIES, DefaultEngineProperties.class);
    }

    /**
     * Gets an instance of type EngineProperties with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public EngineProperties getEngineProperties(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ENGINEPROPERTIES, DefaultEngineProperties.class);
    }

    /**
     * Gets all instances of EngineProperties from the ontology.
     */
    public Collection<? extends EngineProperties> getAllEnginePropertiesInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ENGINEPROPERTIES, DefaultEngineProperties.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EpicyclicGearing
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#EpicyclicGearing", EpicyclicGearing.class, DefaultEpicyclicGearing.class);
    }

    /**
     * Creates an instance of type EpicyclicGearing.  Modifies the underlying ontology.
     */
    public EpicyclicGearing createEpicyclicGearing(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_EPICYCLICGEARING, DefaultEpicyclicGearing.class);
    }

    /**
     * Gets an instance of type EpicyclicGearing with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public EpicyclicGearing getEpicyclicGearing(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_EPICYCLICGEARING, DefaultEpicyclicGearing.class);
    }

    /**
     * Gets all instances of EpicyclicGearing from the ontology.
     */
    public Collection<? extends EpicyclicGearing> getAllEpicyclicGearingInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_EPICYCLICGEARING, DefaultEpicyclicGearing.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Gear
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Gear", Gear.class, DefaultGear.class);
    }

    /**
     * Creates an instance of type Gear.  Modifies the underlying ontology.
     */
    public Gear createGear(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_GEAR, DefaultGear.class);
    }

    /**
     * Gets an instance of type Gear with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Gear getGear(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_GEAR, DefaultGear.class);
    }

    /**
     * Gets all instances of Gear from the ontology.
     */
    public Collection<? extends Gear> getAllGearInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_GEAR, DefaultGear.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#GearProperties
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#GearProperties", GearProperties.class, DefaultGearProperties.class);
    }

    /**
     * Creates an instance of type GearProperties.  Modifies the underlying ontology.
     */
    public GearProperties createGearProperties(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_GEARPROPERTIES, DefaultGearProperties.class);
    }

    /**
     * Gets an instance of type GearProperties with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public GearProperties getGearProperties(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_GEARPROPERTIES, DefaultGearProperties.class);
    }

    /**
     * Gets all instances of GearProperties from the ontology.
     */
    public Collection<? extends GearProperties> getAllGearPropertiesInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_GEARPROPERTIES, DefaultGearProperties.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#GearRatio
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#GearRatio", GearRatio.class, DefaultGearRatio.class);
    }

    /**
     * Creates an instance of type GearRatio.  Modifies the underlying ontology.
     */
    public GearRatio createGearRatio(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_GEARRATIO, DefaultGearRatio.class);
    }

    /**
     * Gets an instance of type GearRatio with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public GearRatio getGearRatio(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_GEARRATIO, DefaultGearRatio.class);
    }

    /**
     * Gets all instances of GearRatio from the ontology.
     */
    public Collection<? extends GearRatio> getAllGearRatioInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_GEARRATIO, DefaultGearRatio.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDrive
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDrive", HarmonicDrive.class, DefaultHarmonicDrive.class);
    }

    /**
     * Creates an instance of type HarmonicDrive.  Modifies the underlying ontology.
     */
    public HarmonicDrive createHarmonicDrive(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVE, DefaultHarmonicDrive.class);
    }

    /**
     * Gets an instance of type HarmonicDrive with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public HarmonicDrive getHarmonicDrive(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVE, DefaultHarmonicDrive.class);
    }

    /**
     * Gets all instances of HarmonicDrive from the ontology.
     */
    public Collection<? extends HarmonicDrive> getAllHarmonicDriveInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_HARMONICDRIVE, DefaultHarmonicDrive.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDriveComponentSets2A
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDriveComponentSets2A", HarmonicDriveComponentSets2A.class, DefaultHarmonicDriveComponentSets2A.class);
    }

    /**
     * Creates an instance of type HarmonicDriveComponentSets2A.  Modifies the underlying ontology.
     */
    public HarmonicDriveComponentSets2A createHarmonicDriveComponentSets2A(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVECOMPONENTSETS2A, DefaultHarmonicDriveComponentSets2A.class);
    }

    /**
     * Gets an instance of type HarmonicDriveComponentSets2A with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public HarmonicDriveComponentSets2A getHarmonicDriveComponentSets2A(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVECOMPONENTSETS2A, DefaultHarmonicDriveComponentSets2A.class);
    }

    /**
     * Gets all instances of HarmonicDriveComponentSets2A from the ontology.
     */
    public Collection<? extends HarmonicDriveComponentSets2A> getAllHarmonicDriveComponentSets2AInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_HARMONICDRIVECOMPONENTSETS2A, DefaultHarmonicDriveComponentSets2A.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDriveUnits
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#HarmonicDriveUnits", HarmonicDriveUnits.class, DefaultHarmonicDriveUnits.class);
    }

    /**
     * Creates an instance of type HarmonicDriveUnits.  Modifies the underlying ontology.
     */
    public HarmonicDriveUnits createHarmonicDriveUnits(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVEUNITS, DefaultHarmonicDriveUnits.class);
    }

    /**
     * Gets an instance of type HarmonicDriveUnits with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public HarmonicDriveUnits getHarmonicDriveUnits(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_HARMONICDRIVEUNITS, DefaultHarmonicDriveUnits.class);
    }

    /**
     * Gets all instances of HarmonicDriveUnits from the ontology.
     */
    public Collection<? extends HarmonicDriveUnits> getAllHarmonicDriveUnitsInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_HARMONICDRIVEUNITS, DefaultHarmonicDriveUnits.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM115x25
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM115x25", ILM115x25.class, DefaultILM115x25.class);
    }

    /**
     * Creates an instance of type ILM115x25.  Modifies the underlying ontology.
     */
    public ILM115x25 createILM115x25(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM115X25, DefaultILM115x25.class);
    }

    /**
     * Gets an instance of type ILM115x25 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM115x25 getILM115x25(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM115X25, DefaultILM115x25.class);
    }

    /**
     * Gets all instances of ILM115x25 from the ontology.
     */
    public Collection<? extends ILM115x25> getAllILM115x25Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM115X25, DefaultILM115x25.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM115x50
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM115x50", ILM115x50.class, DefaultILM115x50.class);
    }

    /**
     * Creates an instance of type ILM115x50.  Modifies the underlying ontology.
     */
    public ILM115x50 createILM115x50(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM115X50, DefaultILM115x50.class);
    }

    /**
     * Gets an instance of type ILM115x50 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM115x50 getILM115x50(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM115X50, DefaultILM115x50.class);
    }

    /**
     * Gets all instances of ILM115x50 from the ontology.
     */
    public Collection<? extends ILM115x50> getAllILM115x50Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM115X50, DefaultILM115x50.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM25x04
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM25x04", ILM25x04.class, DefaultILM25x04.class);
    }

    /**
     * Creates an instance of type ILM25x04.  Modifies the underlying ontology.
     */
    public ILM25x04 createILM25x04(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM25X04, DefaultILM25x04.class);
    }

    /**
     * Gets an instance of type ILM25x04 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM25x04 getILM25x04(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM25X04, DefaultILM25x04.class);
    }

    /**
     * Gets all instances of ILM25x04 from the ontology.
     */
    public Collection<? extends ILM25x04> getAllILM25x04Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM25X04, DefaultILM25x04.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM25x08
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM25x08", ILM25x08.class, DefaultILM25x08.class);
    }

    /**
     * Creates an instance of type ILM25x08.  Modifies the underlying ontology.
     */
    public ILM25x08 createILM25x08(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM25X08, DefaultILM25x08.class);
    }

    /**
     * Gets an instance of type ILM25x08 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM25x08 getILM25x08(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM25X08, DefaultILM25x08.class);
    }

    /**
     * Gets all instances of ILM25x08 from the ontology.
     */
    public Collection<? extends ILM25x08> getAllILM25x08Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM25X08, DefaultILM25x08.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM38x06
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM38x06", ILM38x06.class, DefaultILM38x06.class);
    }

    /**
     * Creates an instance of type ILM38x06.  Modifies the underlying ontology.
     */
    public ILM38x06 createILM38x06(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM38X06, DefaultILM38x06.class);
    }

    /**
     * Gets an instance of type ILM38x06 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM38x06 getILM38x06(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM38X06, DefaultILM38x06.class);
    }

    /**
     * Gets all instances of ILM38x06 from the ontology.
     */
    public Collection<? extends ILM38x06> getAllILM38x06Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM38X06, DefaultILM38x06.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM38x12
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM38x12", ILM38x12.class, DefaultILM38x12.class);
    }

    /**
     * Creates an instance of type ILM38x12.  Modifies the underlying ontology.
     */
    public ILM38x12 createILM38x12(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM38X12, DefaultILM38x12.class);
    }

    /**
     * Gets an instance of type ILM38x12 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM38x12 getILM38x12(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM38X12, DefaultILM38x12.class);
    }

    /**
     * Gets all instances of ILM38x12 from the ontology.
     */
    public Collection<? extends ILM38x12> getAllILM38x12Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM38X12, DefaultILM38x12.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM50x08
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM50x08", ILM50x08.class, DefaultILM50x08.class);
    }

    /**
     * Creates an instance of type ILM50x08.  Modifies the underlying ontology.
     */
    public ILM50x08 createILM50x08(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM50X08, DefaultILM50x08.class);
    }

    /**
     * Gets an instance of type ILM50x08 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM50x08 getILM50x08(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM50X08, DefaultILM50x08.class);
    }

    /**
     * Gets all instances of ILM50x08 from the ontology.
     */
    public Collection<? extends ILM50x08> getAllILM50x08Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM50X08, DefaultILM50x08.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM50x14
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM50x14", ILM50x14.class, DefaultILM50x14.class);
    }

    /**
     * Creates an instance of type ILM50x14.  Modifies the underlying ontology.
     */
    public ILM50x14 createILM50x14(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM50X14, DefaultILM50x14.class);
    }

    /**
     * Gets an instance of type ILM50x14 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM50x14 getILM50x14(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM50X14, DefaultILM50x14.class);
    }

    /**
     * Gets all instances of ILM50x14 from the ontology.
     */
    public Collection<? extends ILM50x14> getAllILM50x14Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM50X14, DefaultILM50x14.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM70x10
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM70x10", ILM70x10.class, DefaultILM70x10.class);
    }

    /**
     * Creates an instance of type ILM70x10.  Modifies the underlying ontology.
     */
    public ILM70x10 createILM70x10(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM70X10, DefaultILM70x10.class);
    }

    /**
     * Gets an instance of type ILM70x10 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM70x10 getILM70x10(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM70X10, DefaultILM70x10.class);
    }

    /**
     * Gets all instances of ILM70x10 from the ontology.
     */
    public Collection<? extends ILM70x10> getAllILM70x10Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM70X10, DefaultILM70x10.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM70x18
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM70x18", ILM70x18.class, DefaultILM70x18.class);
    }

    /**
     * Creates an instance of type ILM70x18.  Modifies the underlying ontology.
     */
    public ILM70x18 createILM70x18(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM70X18, DefaultILM70x18.class);
    }

    /**
     * Gets an instance of type ILM70x18 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM70x18 getILM70x18(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM70X18, DefaultILM70x18.class);
    }

    /**
     * Gets all instances of ILM70x18 from the ontology.
     */
    public Collection<? extends ILM70x18> getAllILM70x18Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM70X18, DefaultILM70x18.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x04
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x04", ILM85x04.class, DefaultILM85x04.class);
    }

    /**
     * Creates an instance of type ILM85x04.  Modifies the underlying ontology.
     */
    public ILM85x04 createILM85x04(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM85X04, DefaultILM85x04.class);
    }

    /**
     * Gets an instance of type ILM85x04 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM85x04 getILM85x04(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM85X04, DefaultILM85x04.class);
    }

    /**
     * Gets all instances of ILM85x04 from the ontology.
     */
    public Collection<? extends ILM85x04> getAllILM85x04Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM85X04, DefaultILM85x04.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x13
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x13", ILM85x13.class, DefaultILM85x13.class);
    }

    /**
     * Creates an instance of type ILM85x13.  Modifies the underlying ontology.
     */
    public ILM85x13 createILM85x13(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM85X13, DefaultILM85x13.class);
    }

    /**
     * Gets an instance of type ILM85x13 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM85x13 getILM85x13(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM85X13, DefaultILM85x13.class);
    }

    /**
     * Gets all instances of ILM85x13 from the ontology.
     */
    public Collection<? extends ILM85x13> getAllILM85x13Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM85X13, DefaultILM85x13.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x23
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x23", ILM85x23.class, DefaultILM85x23.class);
    }

    /**
     * Creates an instance of type ILM85x23.  Modifies the underlying ontology.
     */
    public ILM85x23 createILM85x23(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM85X23, DefaultILM85x23.class);
    }

    /**
     * Gets an instance of type ILM85x23 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM85x23 getILM85x23(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM85X23, DefaultILM85x23.class);
    }

    /**
     * Gets all instances of ILM85x23 from the ontology.
     */
    public Collection<? extends ILM85x23> getAllILM85x23Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM85X23, DefaultILM85x23.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x26
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#ILM85x26", ILM85x26.class, DefaultILM85x26.class);
    }

    /**
     * Creates an instance of type ILM85x26.  Modifies the underlying ontology.
     */
    public ILM85x26 createILM85x26(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ILM85X26, DefaultILM85x26.class);
    }

    /**
     * Gets an instance of type ILM85x26 with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public ILM85x26 getILM85x26(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ILM85X26, DefaultILM85x26.class);
    }

    /**
     * Gets all instances of ILM85x26 from the ontology.
     */
    public Collection<? extends ILM85x26> getAllILM85x26Instances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ILM85X26, DefaultILM85x26.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#InnerDiameter
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#InnerDiameter", InnerDiameter.class, DefaultInnerDiameter.class);
    }

    /**
     * Creates an instance of type InnerDiameter.  Modifies the underlying ontology.
     */
    public InnerDiameter createInnerDiameter(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_INNERDIAMETER, DefaultInnerDiameter.class);
    }

    /**
     * Gets an instance of type InnerDiameter with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public InnerDiameter getInnerDiameter(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_INNERDIAMETER, DefaultInnerDiameter.class);
    }

    /**
     * Gets all instances of InnerDiameter from the ontology.
     */
    public Collection<? extends InnerDiameter> getAllInnerDiameterInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_INNERDIAMETER, DefaultInnerDiameter.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Input
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Input", Input.class, DefaultInput.class);
    }

    /**
     * Creates an instance of type Input.  Modifies the underlying ontology.
     */
    public Input createInput(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_INPUT, DefaultInput.class);
    }

    /**
     * Gets an instance of type Input with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Input getInput(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_INPUT, DefaultInput.class);
    }

    /**
     * Gets all instances of Input from the ontology.
     */
    public Collection<? extends Input> getAllInputInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_INPUT, DefaultInput.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#InputOutput
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#InputOutput", InputOutput.class, DefaultInputOutput.class);
    }

    /**
     * Creates an instance of type InputOutput.  Modifies the underlying ontology.
     */
    public InputOutput createInputOutput(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_INPUTOUTPUT, DefaultInputOutput.class);
    }

    /**
     * Gets an instance of type InputOutput with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public InputOutput getInputOutput(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_INPUTOUTPUT, DefaultInputOutput.class);
    }

    /**
     * Gets all instances of InputOutput from the ontology.
     */
    public Collection<? extends InputOutput> getAllInputOutputInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_INPUTOUTPUT, DefaultInputOutput.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Length
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Length", Length.class, DefaultLength.class);
    }

    /**
     * Creates an instance of type Length.  Modifies the underlying ontology.
     */
    public Length createLength(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_LENGTH, DefaultLength.class);
    }

    /**
     * Gets an instance of type Length with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Length getLength(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_LENGTH, DefaultLength.class);
    }

    /**
     * Gets all instances of Length from the ontology.
     */
    public Collection<? extends Length> getAllLengthInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_LENGTH, DefaultLength.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxEfficiency
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxEfficiency", MaxEfficiency.class, DefaultMaxEfficiency.class);
    }

    /**
     * Creates an instance of type MaxEfficiency.  Modifies the underlying ontology.
     */
    public MaxEfficiency createMaxEfficiency(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_MAXEFFICIENCY, DefaultMaxEfficiency.class);
    }

    /**
     * Gets an instance of type MaxEfficiency with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public MaxEfficiency getMaxEfficiency(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_MAXEFFICIENCY, DefaultMaxEfficiency.class);
    }

    /**
     * Gets all instances of MaxEfficiency from the ontology.
     */
    public Collection<? extends MaxEfficiency> getAllMaxEfficiencyInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_MAXEFFICIENCY, DefaultMaxEfficiency.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxInputSpeedGrease
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxInputSpeedGrease", MaxInputSpeedGrease.class, DefaultMaxInputSpeedGrease.class);
    }

    /**
     * Creates an instance of type MaxInputSpeedGrease.  Modifies the underlying ontology.
     */
    public MaxInputSpeedGrease createMaxInputSpeedGrease(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_MAXINPUTSPEEDGREASE, DefaultMaxInputSpeedGrease.class);
    }

    /**
     * Gets an instance of type MaxInputSpeedGrease with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public MaxInputSpeedGrease getMaxInputSpeedGrease(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_MAXINPUTSPEEDGREASE, DefaultMaxInputSpeedGrease.class);
    }

    /**
     * Gets all instances of MaxInputSpeedGrease from the ontology.
     */
    public Collection<? extends MaxInputSpeedGrease> getAllMaxInputSpeedGreaseInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_MAXINPUTSPEEDGREASE, DefaultMaxInputSpeedGrease.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxNominalTorque
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#MaxNominalTorque", MaxNominalTorque.class, DefaultMaxNominalTorque.class);
    }

    /**
     * Creates an instance of type MaxNominalTorque.  Modifies the underlying ontology.
     */
    public MaxNominalTorque createMaxNominalTorque(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_MAXNOMINALTORQUE, DefaultMaxNominalTorque.class);
    }

    /**
     * Gets an instance of type MaxNominalTorque with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public MaxNominalTorque getMaxNominalTorque(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_MAXNOMINALTORQUE, DefaultMaxNominalTorque.class);
    }

    /**
     * Gets all instances of MaxNominalTorque from the ontology.
     */
    public Collection<? extends MaxNominalTorque> getAllMaxNominalTorqueInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_MAXNOMINALTORQUE, DefaultMaxNominalTorque.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalPowerSupply
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalPowerSupply", NominalPowerSupply.class, DefaultNominalPowerSupply.class);
    }

    /**
     * Creates an instance of type NominalPowerSupply.  Modifies the underlying ontology.
     */
    public NominalPowerSupply createNominalPowerSupply(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_NOMINALPOWERSUPPLY, DefaultNominalPowerSupply.class);
    }

    /**
     * Gets an instance of type NominalPowerSupply with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public NominalPowerSupply getNominalPowerSupply(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_NOMINALPOWERSUPPLY, DefaultNominalPowerSupply.class);
    }

    /**
     * Gets all instances of NominalPowerSupply from the ontology.
     */
    public Collection<? extends NominalPowerSupply> getAllNominalPowerSupplyInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_NOMINALPOWERSUPPLY, DefaultNominalPowerSupply.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalRotationSpeed
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalRotationSpeed", NominalRotationSpeed.class, DefaultNominalRotationSpeed.class);
    }

    /**
     * Creates an instance of type NominalRotationSpeed.  Modifies the underlying ontology.
     */
    public NominalRotationSpeed createNominalRotationSpeed(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_NOMINALROTATIONSPEED, DefaultNominalRotationSpeed.class);
    }

    /**
     * Gets an instance of type NominalRotationSpeed with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public NominalRotationSpeed getNominalRotationSpeed(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_NOMINALROTATIONSPEED, DefaultNominalRotationSpeed.class);
    }

    /**
     * Gets all instances of NominalRotationSpeed from the ontology.
     */
    public Collection<? extends NominalRotationSpeed> getAllNominalRotationSpeedInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_NOMINALROTATIONSPEED, DefaultNominalRotationSpeed.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalTorque
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#NominalTorque", NominalTorque.class, DefaultNominalTorque.class);
    }

    /**
     * Creates an instance of type NominalTorque.  Modifies the underlying ontology.
     */
    public NominalTorque createNominalTorque(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_NOMINALTORQUE, DefaultNominalTorque.class);
    }

    /**
     * Gets an instance of type NominalTorque with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public NominalTorque getNominalTorque(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_NOMINALTORQUE, DefaultNominalTorque.class);
    }

    /**
     * Gets all instances of NominalTorque from the ontology.
     */
    public Collection<? extends NominalTorque> getAllNominalTorqueInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_NOMINALTORQUE, DefaultNominalTorque.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#OuterDiameter
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#OuterDiameter", OuterDiameter.class, DefaultOuterDiameter.class);
    }

    /**
     * Creates an instance of type OuterDiameter.  Modifies the underlying ontology.
     */
    public OuterDiameter createOuterDiameter(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_OUTERDIAMETER, DefaultOuterDiameter.class);
    }

    /**
     * Gets an instance of type OuterDiameter with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public OuterDiameter getOuterDiameter(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_OUTERDIAMETER, DefaultOuterDiameter.class);
    }

    /**
     * Gets all instances of OuterDiameter from the ontology.
     */
    public Collection<? extends OuterDiameter> getAllOuterDiameterInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_OUTERDIAMETER, DefaultOuterDiameter.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Output
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Output", Output.class, DefaultOutput.class);
    }

    /**
     * Creates an instance of type Output.  Modifies the underlying ontology.
     */
    public Output createOutput(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_OUTPUT, DefaultOutput.class);
    }

    /**
     * Gets an instance of type Output with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Output getOutput(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_OUTPUT, DefaultOutput.class);
    }

    /**
     * Gets all instances of Output from the ontology.
     */
    public Collection<? extends Output> getAllOutputInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_OUTPUT, DefaultOutput.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#PowerLoose
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#PowerLoose", PowerLoose.class, DefaultPowerLoose.class);
    }

    /**
     * Creates an instance of type PowerLoose.  Modifies the underlying ontology.
     */
    public PowerLoose createPowerLoose(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_POWERLOOSE, DefaultPowerLoose.class);
    }

    /**
     * Gets an instance of type PowerLoose with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public PowerLoose getPowerLoose(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_POWERLOOSE, DefaultPowerLoose.class);
    }

    /**
     * Gets all instances of PowerLoose from the ontology.
     */
    public Collection<? extends PowerLoose> getAllPowerLooseInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_POWERLOOSE, DefaultPowerLoose.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#PowerSupply
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#PowerSupply", PowerSupply.class, DefaultPowerSupply.class);
    }

    /**
     * Creates an instance of type PowerSupply.  Modifies the underlying ontology.
     */
    public PowerSupply createPowerSupply(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_POWERSUPPLY, DefaultPowerSupply.class);
    }

    /**
     * Gets an instance of type PowerSupply with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public PowerSupply getPowerSupply(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_POWERSUPPLY, DefaultPowerSupply.class);
    }

    /**
     * Gets all instances of PowerSupply from the ontology.
     */
    public Collection<? extends PowerSupply> getAllPowerSupplyInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_POWERSUPPLY, DefaultPowerSupply.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Property
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Property", Property.class, DefaultProperty.class);
    }

    /**
     * Creates an instance of type Property.  Modifies the underlying ontology.
     */
    public Property createProperty(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_PROPERTY, DefaultProperty.class);
    }

    /**
     * Gets an instance of type Property with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Property getProperty(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_PROPERTY, DefaultProperty.class);
    }

    /**
     * Gets all instances of Property from the ontology.
     */
    public Collection<? extends Property> getAllPropertyInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_PROPERTY, DefaultProperty.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RepeatedPeakTorque
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RepeatedPeakTorque", RepeatedPeakTorque.class, DefaultRepeatedPeakTorque.class);
    }

    /**
     * Creates an instance of type RepeatedPeakTorque.  Modifies the underlying ontology.
     */
    public RepeatedPeakTorque createRepeatedPeakTorque(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_REPEATEDPEAKTORQUE, DefaultRepeatedPeakTorque.class);
    }

    /**
     * Gets an instance of type RepeatedPeakTorque with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public RepeatedPeakTorque getRepeatedPeakTorque(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_REPEATEDPEAKTORQUE, DefaultRepeatedPeakTorque.class);
    }

    /**
     * Gets all instances of RepeatedPeakTorque from the ontology.
     */
    public Collection<? extends RepeatedPeakTorque> getAllRepeatedPeakTorqueInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_REPEATEDPEAKTORQUE, DefaultRepeatedPeakTorque.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Requirements
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Requirements", Requirements.class, DefaultRequirements.class);
    }

    /**
     * Creates an instance of type Requirements.  Modifies the underlying ontology.
     */
    public Requirements createRequirements(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_REQUIREMENTS, DefaultRequirements.class);
    }

    /**
     * Gets an instance of type Requirements with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Requirements getRequirements(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_REQUIREMENTS, DefaultRequirements.class);
    }

    /**
     * Gets all instances of Requirements from the ontology.
     */
    public Collection<? extends Requirements> getAllRequirementsInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_REQUIREMENTS, DefaultRequirements.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RoboDriveServoKibILM
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RoboDriveServoKibILM", RoboDriveServoKibILM.class, DefaultRoboDriveServoKibILM.class);
    }

    /**
     * Creates an instance of type RoboDriveServoKibILM.  Modifies the underlying ontology.
     */
    public RoboDriveServoKibILM createRoboDriveServoKibILM(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ROBODRIVESERVOKIBILM, DefaultRoboDriveServoKibILM.class);
    }

    /**
     * Gets an instance of type RoboDriveServoKibILM with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public RoboDriveServoKibILM getRoboDriveServoKibILM(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ROBODRIVESERVOKIBILM, DefaultRoboDriveServoKibILM.class);
    }

    /**
     * Gets all instances of RoboDriveServoKibILM from the ontology.
     */
    public Collection<? extends RoboDriveServoKibILM> getAllRoboDriveServoKibILMInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ROBODRIVESERVOKIBILM, DefaultRoboDriveServoKibILM.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RotationSpeed
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#RotationSpeed", RotationSpeed.class, DefaultRotationSpeed.class);
    }

    /**
     * Creates an instance of type RotationSpeed.  Modifies the underlying ontology.
     */
    public RotationSpeed createRotationSpeed(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_ROTATIONSPEED, DefaultRotationSpeed.class);
    }

    /**
     * Gets an instance of type RotationSpeed with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public RotationSpeed getRotationSpeed(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_ROTATIONSPEED, DefaultRotationSpeed.class);
    }

    /**
     * Gets all instances of RotationSpeed from the ontology.
     */
    public Collection<? extends RotationSpeed> getAllRotationSpeedInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_ROTATIONSPEED, DefaultRotationSpeed.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Satisfied
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Satisfied", Satisfied.class, DefaultSatisfied.class);
    }

    /**
     * Creates an instance of type Satisfied.  Modifies the underlying ontology.
     */
    public Satisfied createSatisfied(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIED, DefaultSatisfied.class);
    }

    /**
     * Gets an instance of type Satisfied with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Satisfied getSatisfied(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIED, DefaultSatisfied.class);
    }

    /**
     * Gets all instances of Satisfied from the ontology.
     */
    public Collection<? extends Satisfied> getAllSatisfiedInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIED, DefaultSatisfied.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedEngine
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedEngine", SatisfiedEngine.class, DefaultSatisfiedEngine.class);
    }

    /**
     * Creates an instance of type SatisfiedEngine.  Modifies the underlying ontology.
     */
    public SatisfiedEngine createSatisfiedEngine(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDENGINE, DefaultSatisfiedEngine.class);
    }

    /**
     * Gets an instance of type SatisfiedEngine with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedEngine getSatisfiedEngine(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDENGINE, DefaultSatisfiedEngine.class);
    }

    /**
     * Gets all instances of SatisfiedEngine from the ontology.
     */
    public Collection<? extends SatisfiedEngine> getAllSatisfiedEngineInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDENGINE, DefaultSatisfiedEngine.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedEngineGearMatch
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedEngineGearMatch", SatisfiedEngineGearMatch.class, DefaultSatisfiedEngineGearMatch.class);
    }

    /**
     * Creates an instance of type SatisfiedEngineGearMatch.  Modifies the underlying ontology.
     */
    public SatisfiedEngineGearMatch createSatisfiedEngineGearMatch(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDENGINEGEARMATCH, DefaultSatisfiedEngineGearMatch.class);
    }

    /**
     * Gets an instance of type SatisfiedEngineGearMatch with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedEngineGearMatch getSatisfiedEngineGearMatch(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDENGINEGEARMATCH, DefaultSatisfiedEngineGearMatch.class);
    }

    /**
     * Gets all instances of SatisfiedEngineGearMatch from the ontology.
     */
    public Collection<? extends SatisfiedEngineGearMatch> getAllSatisfiedEngineGearMatchInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDENGINEGEARMATCH, DefaultSatisfiedEngineGearMatch.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedGear
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedGear", SatisfiedGear.class, DefaultSatisfiedGear.class);
    }

    /**
     * Creates an instance of type SatisfiedGear.  Modifies the underlying ontology.
     */
    public SatisfiedGear createSatisfiedGear(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDGEAR, DefaultSatisfiedGear.class);
    }

    /**
     * Gets an instance of type SatisfiedGear with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedGear getSatisfiedGear(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDGEAR, DefaultSatisfiedGear.class);
    }

    /**
     * Gets all instances of SatisfiedGear from the ontology.
     */
    public Collection<? extends SatisfiedGear> getAllSatisfiedGearInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDGEAR, DefaultSatisfiedGear.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedMaximalTorque
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedMaximalTorque", SatisfiedMaximalTorque.class, DefaultSatisfiedMaximalTorque.class);
    }

    /**
     * Creates an instance of type SatisfiedMaximalTorque.  Modifies the underlying ontology.
     */
    public SatisfiedMaximalTorque createSatisfiedMaximalTorque(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDMAXIMALTORQUE, DefaultSatisfiedMaximalTorque.class);
    }

    /**
     * Gets an instance of type SatisfiedMaximalTorque with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedMaximalTorque getSatisfiedMaximalTorque(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDMAXIMALTORQUE, DefaultSatisfiedMaximalTorque.class);
    }

    /**
     * Gets all instances of SatisfiedMaximalTorque from the ontology.
     */
    public Collection<? extends SatisfiedMaximalTorque> getAllSatisfiedMaximalTorqueInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDMAXIMALTORQUE, DefaultSatisfiedMaximalTorque.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedRotationSpeed
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedRotationSpeed", SatisfiedRotationSpeed.class, DefaultSatisfiedRotationSpeed.class);
    }

    /**
     * Creates an instance of type SatisfiedRotationSpeed.  Modifies the underlying ontology.
     */
    public SatisfiedRotationSpeed createSatisfiedRotationSpeed(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDROTATIONSPEED, DefaultSatisfiedRotationSpeed.class);
    }

    /**
     * Gets an instance of type SatisfiedRotationSpeed with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedRotationSpeed getSatisfiedRotationSpeed(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDROTATIONSPEED, DefaultSatisfiedRotationSpeed.class);
    }

    /**
     * Gets all instances of SatisfiedRotationSpeed from the ontology.
     */
    public Collection<? extends SatisfiedRotationSpeed> getAllSatisfiedRotationSpeedInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDROTATIONSPEED, DefaultSatisfiedRotationSpeed.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedWeight
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#SatisfiedWeight", SatisfiedWeight.class, DefaultSatisfiedWeight.class);
    }

    /**
     * Creates an instance of type SatisfiedWeight.  Modifies the underlying ontology.
     */
    public SatisfiedWeight createSatisfiedWeight(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDWEIGHT, DefaultSatisfiedWeight.class);
    }

    /**
     * Gets an instance of type SatisfiedWeight with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public SatisfiedWeight getSatisfiedWeight(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SATISFIEDWEIGHT, DefaultSatisfiedWeight.class);
    }

    /**
     * Gets all instances of SatisfiedWeight from the ontology.
     */
    public Collection<? extends SatisfiedWeight> getAllSatisfiedWeightInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SATISFIEDWEIGHT, DefaultSatisfiedWeight.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Size
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Size", Size.class, DefaultSize.class);
    }

    /**
     * Creates an instance of type Size.  Modifies the underlying ontology.
     */
    public Size createSize(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_SIZE, DefaultSize.class);
    }

    /**
     * Gets an instance of type Size with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Size getSize(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_SIZE, DefaultSize.class);
    }

    /**
     * Gets all instances of Size from the ontology.
     */
    public Collection<? extends Size> getAllSizeInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_SIZE, DefaultSize.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Torque
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Torque", Torque.class, DefaultTorque.class);
    }

    /**
     * Creates an instance of type Torque.  Modifies the underlying ontology.
     */
    public Torque createTorque(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_TORQUE, DefaultTorque.class);
    }

    /**
     * Gets an instance of type Torque with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Torque getTorque(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_TORQUE, DefaultTorque.class);
    }

    /**
     * Gets all instances of Torque from the ontology.
     */
    public Collection<? extends Torque> getAllTorqueInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_TORQUE, DefaultTorque.class);
    }


    /* ***************************************************
     * Class http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Weight
     */

    {
        javaMapping.add("http://www.semanticweb.org/oliver/ontologies/sac/prototypeV3#Weight", Weight.class, DefaultWeight.class);
    }

    /**
     * Creates an instance of type Weight.  Modifies the underlying ontology.
     */
    public Weight createWeight(String name) {
		return delegate.createWrappedIndividual(name, Vocabulary.CLASS_WEIGHT, DefaultWeight.class);
    }

    /**
     * Gets an instance of type Weight with the given name.  Does not modify the underlying ontology.
     * @param name the name of the OWL named individual to be retrieved.
     */
    public Weight getWeight(String name) {
		return delegate.getWrappedIndividual(name, Vocabulary.CLASS_WEIGHT, DefaultWeight.class);
    }

    /**
     * Gets all instances of Weight from the ontology.
     */
    public Collection<? extends Weight> getAllWeightInstances() {
		return delegate.getWrappedIndividuals(Vocabulary.CLASS_WEIGHT, DefaultWeight.class);
    }


}
