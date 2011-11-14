package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLAxiomProcessor;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValueFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLBuiltInBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLBuiltInBridgeController;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridgeController;
import edu.stanford.smi.protegex.owl.swrl.bridge.TargetSWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.BuiltInLibraryManager;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.TargetSWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLEntityReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.PrefixManager;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBuiltInAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3PrefixManager;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

/**
 * Default implementation of a SWRL rule engine bridge, built-in bridge, built-in bridge controller and rule engine bridge controller. 
 */
public class DefaultSWRLBridge implements SWRLRuleEngineBridge, SWRLBuiltInBridge, SWRLBuiltInBridgeController, SWRLRuleEngineBridgeController 
{
	private OWLOntology activeOntology;
	private TargetSWRLRuleEngine targetRuleEngine;
	private OWLAxiomProcessor owlAxiomProcessor;
	private OWLDataFactory dataFactory;
	private OWLDataFactory injectedOWLFactory;
	private OWLDataValueFactory dataValueFactory;
  private PrefixManager prefixManager;
	
  private Map<String, OWLNamedIndividualReference> inferredOWLIndividualDeclarations;
  private Set<OWLAxiomReference> inferredOWLAxioms; 

  private HashMap<String, OWLClassReference> injectedOWLClassDeclarations;
  private HashMap<String, OWLNamedIndividualReference> injectedOWLIndividualDeclarations;
  private Set<OWLAxiomReference> injectedOWLAxioms;

  private Map<String, Map<String, Set<OWLPropertyAssertionAxiomReference>>> allOWLPropertyAssertionAxioms; // individualURI <propertyURI, axiom>
  private Map<String, OWLNamedIndividualReference> allOWLIndividualDeclarations; 

	public DefaultSWRLBridge(OWLOntology activeOntology, OWLAxiomProcessor owlAxiomProcessor) throws SWRLBuiltInBridgeException 
	{
		this.activeOntology = activeOntology;
		this.owlAxiomProcessor = owlAxiomProcessor;
		this.targetRuleEngine = null;
      
    dataFactory = new P3OWLDataFactory(activeOntology);
    injectedOWLFactory = new P3OWLDataFactory();   
    dataValueFactory = OWLDataValueFactory.create();
    prefixManager = new P3PrefixManager(activeOntology);
    
    initialize();
      
    reset();
	}
	
	public void reset() throws SWRLBuiltInBridgeException	{ BuiltInLibraryManager.invokeAllBuiltInLibrariesResetMethod(this); }
	
  public void setTargetRuleEngine(TargetSWRLRuleEngine targetRuleEngine) { this.targetRuleEngine = targetRuleEngine; }
	
  public int getNumberOfInjectedOWLClassDeclarations() { return injectedOWLClassDeclarations.size(); }
  public int getNumberOfInjectedOWLIndividualDeclarations() { return injectedOWLIndividualDeclarations.size(); }
  public int getNumberOfInjectedOWLAxioms() { return injectedOWLAxioms.size(); }

  public boolean isInjectedOWLClass(String classURI) { return injectedOWLClassDeclarations.containsKey(classURI); }
  public boolean isInjectedOWLIndividual(String individualURI) { return injectedOWLIndividualDeclarations.containsKey(individualURI); }
  public boolean isInjectedOWLAxiom(OWLAxiomReference axiom) { return injectedOWLAxioms.contains(axiom); }

  public Set<OWLClassReference> getInjectedOWLClassDeclarations() { return new HashSet<OWLClassReference>(injectedOWLClassDeclarations.values()); }
  public Set<OWLNamedIndividualReference> getInjectedOWLIndividualDeclarations() { return new HashSet<OWLNamedIndividualReference>(injectedOWLIndividualDeclarations.values()); }
  public Set<OWLAxiomReference> getInjectedOWLAxioms() { return injectedOWLAxioms; }

  public Set<OWLAxiomReference> getInferredOWLAxioms() { return inferredOWLAxioms; }
  public Set<OWLNamedIndividualReference> getInferredOWLIndividuals() { return new HashSet<OWLNamedIndividualReference>(inferredOWLIndividualDeclarations.values()); }
  public int getNumberOfInferredOWLAxioms() { return inferredOWLAxioms.size(); }
  public int getNumberOfInferredOWLIndividuals() { return inferredOWLIndividualDeclarations.size(); }
	public boolean isSQWRLQuery(SWRLRuleReference query ) { return owlAxiomProcessor.isSQWRLQuery(query.getURI()); }
	
  public void inferOWLAxiom(OWLAxiomReference axiom) throws SWRLRuleEngineBridgeException
  { 
    if (!inferredOWLAxioms.contains(axiom)) {
      inferredOWLAxioms.add(axiom); 
      if (axiom instanceof OWLPropertyAssertionAxiomReference) 
      	cacheOWLPropertyAssertionAxiom((OWLPropertyAssertionAxiomReference)axiom);
      else if (axiom instanceof OWLClassAssertionAxiomReference) {
        OWLClassAssertionAxiomReference owlClassAssertionAxiom = (OWLClassAssertionAxiomReference)axiom;
        inferOWLIndividual(owlClassAssertionAxiom.getIndividual(), owlClassAssertionAxiom.getDescription());
      } // if
    } // if
  }
  
  private void inferOWLIndividual(OWLNamedIndividualReference owlIndividual, OWLClassReference owlClass) throws SWRLRuleEngineBridgeException 
  {
    String individualURI = owlIndividual.getURI();

    if (inferredOWLIndividualDeclarations.containsKey(individualURI)) inferredOWLIndividualDeclarations.get(individualURI).addType(owlClass);
    else if (injectedOWLIndividualDeclarations.containsKey(individualURI)) injectedOWLIndividualDeclarations.get(individualURI).addType(owlClass);
    else {
      inferredOWLIndividualDeclarations.put(individualURI, owlIndividual); 
      cacheOWLIndividual(owlIndividual);
    } // if
  } 

  public PrefixManager getPrefixManager() { return prefixManager; }

	public boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments)
	  throws BuiltInException
	{ 
	  return BuiltInLibraryManager.invokeSWRLBuiltIn(targetRuleEngine, this, ruleName, builtInName, builtInIndex, isInConsequent, arguments); 
 	}
			
	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromBody(SWRLRuleReference ruleOrQuery, Set<String> builtInNames) 
    { return owlAxiomProcessor.getBuiltInAtomsFromBody(ruleOrQuery, builtInNames); }
	
	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromHead(SWRLRuleReference ruleOrQuery, Set<String> builtInNames) 
    { return owlAxiomProcessor.getBuiltInAtomsFromHead(ruleOrQuery, builtInNames); }
	  
  // The inject methods can be used by built-ins to inject new axioms into a bridge, which will also reflect them in the underlying
  // engine. Eventually collapse all inject methods into injectOWLAxiom.
  public void injectOWLAxiom(OWLAxiomReference axiom) throws SWRLBuiltInBridgeException
  {
    if (!injectedOWLAxioms.contains(axiom)) {
      injectedOWLAxioms.add(axiom);

      if (axiom instanceof OWLPropertyAssertionAxiomReference) cacheOWLPropertyAssertionAxiom((OWLPropertyAssertionAxiomReference)axiom);
      else if (axiom instanceof OWLDeclarationAxiomReference) injectOWLDeclarationAxiom((OWLDeclarationAxiomReference)axiom);
      else if (axiom instanceof OWLSubClassAxiomReference) injectOWLSubClassAxiom((OWLSubClassAxiomReference)axiom);

      exportOWLAxiom(axiom); // Export the axiom to the rule engine.
    } // if
  }

  public String uri2PrefixedName(String uri)
  {
  	return activeOntology.uri2PrefixedName(uri);
  }
  
  public String name2URI(String prefixedName)
  {
  	return activeOntology.prefixedName2URI(prefixedName);
  }

  private void injectOWLDeclarationAxiom(OWLDeclarationAxiomReference axiom) throws SWRLBuiltInBridgeException
  {
    OWLEntityReference owlEntity = axiom.getEntity();

    if (owlEntity instanceof OWLClassReference) injectOWLClassDeclaration(owlEntity.getURI());
    else if (owlEntity instanceof OWLNamedIndividualReference) injectOWLIndividualDeclaration((OWLNamedIndividualReference)owlEntity);
    else throw new SWRLBuiltInBridgeException("error injecting OWLDeclarationAxiom - unknown entity type: " + owlEntity.getClass());
  }

  public OWLClassReference injectOWLClassDeclaration() throws SWRLBuiltInBridgeException
  {
    OWLClassReference owlClass = activeOntology.createOWLClass();
    String classURI = owlClass.getURI();

    if (!injectedOWLClassDeclarations.containsKey(classURI)) {
      injectedOWLClassDeclarations.put(classURI, owlClass);
      exportOWLClassDeclaration(owlClass); // Export the class to the rule engine
    } // if

    return owlClass;
  }

  public void injectOWLClassDeclaration(String classURI) throws SWRLBuiltInBridgeException
  {
    checkOWLClassURI(classURI);

    if (!injectedOWLClassDeclarations.containsKey(classURI)) {
      OWLClassReference owlClass = injectedOWLFactory.getOWLClass(classURI);
      injectedOWLClassDeclarations.put(classURI, owlClass);
      exportOWLClassDeclaration(owlClass); // Export the individual to the rule engine
    } // if
  }

  private void injectOWLSubClassAxiom(OWLSubClassAxiomReference axiom) throws SWRLBuiltInBridgeException
  {
    String subclassURI = axiom.getSubClass().getURI();
    String superclassURI = axiom.getSuperClass().getURI();
    injectedOWLFactory.getOWLClass(subclassURI);
    injectedOWLFactory.getOWLClass(superclassURI);

    injectedOWLClassDeclarations.put(subclassURI, axiom.getSubClass());
    injectedOWLClassDeclarations.put(superclassURI, axiom.getSuperClass());
  }

  /**
   * Method used to inject a bridge individual of type owl:Thing. This method will typically be invoked from within a built-in. An OWL
   * individual is not injected at this point - instead an object is generated for the individual in the bridge and the individual is
   * exported to the rule engine. The individual is given a unique name that can be used later if an OWL individual is injected for it.
   */
  public OWLNamedIndividualReference injectOWLIndividualDeclaration() throws SWRLBuiltInBridgeException
  {
    String individualURI = activeOntology.createNewResourceURI("SWRLInjected");
    OWLClassReference owlClass = injectedOWLFactory.getOWLClass(edu.stanford.smi.protegex.owl.model.OWLNames.Cls.THING);
    OWLNamedIndividualReference owlIndividual = injectedOWLFactory.getOWLIndividual(individualURI);
    owlIndividual.addType(owlClass);
    injectOWLIndividualDeclaration(owlIndividual);
    return owlIndividual;
  }
    
  public void injectOWLIndividualDeclaration(OWLNamedIndividualReference owlIndividual) throws SWRLBuiltInBridgeException
  {
    if (!injectedOWLIndividualDeclarations.containsKey(owlIndividual.getURI())) {
      injectedOWLIndividualDeclarations.put(owlIndividual.getURI(), owlIndividual); 
      cacheOWLIndividual(owlIndividual);
      exportOWLIndividualDeclaration(owlIndividual); // Export the individual to the rule engine.
    } // if
  } 

  public OWLNamedIndividualReference injectOWLIndividualDeclaration(OWLClassReference owlClass) throws SWRLBuiltInBridgeException
  {
    String individualURI = activeOntology.createNewResourceURI("SWRLInjected");
    OWLNamedIndividualReference owlIndividual = injectedOWLFactory.getOWLIndividual(individualURI);
    owlIndividual.addType(owlClass);

    if (!owlAxiomProcessor.isReferencedOWLClass(owlClass.getURI())) exportOWLClassDeclaration(owlClass);
   
    injectedOWLIndividualDeclarations.put(individualURI, owlIndividual); 
    cacheOWLIndividual(owlIndividual);
    exportOWLIndividualDeclaration(owlIndividual); // Export the individual to the rule engine.

    return owlIndividual;
  } 
  
  public OWLDataPropertyAssertionAxiomReference injectOWLDataPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
                                                                           OWLDataValue object) 
    throws SWRLBuiltInBridgeException
  {
    OWLDataPropertyAssertionAxiomReference axiom = injectedOWLFactory.getOWLDataPropertyAssertionAxiom(subject, property, object);
    injectOWLDatatypePropertyAssertionAxiom(axiom);
    return axiom;
  }

  public void injectOWLDatatypePropertyAssertionAxiom(OWLDataPropertyAssertionAxiomReference axiom) 
    throws SWRLBuiltInBridgeException
  {
    if (!injectedOWLAxioms.contains(axiom)) {
      injectedOWLAxioms.add(axiom);
      cacheOWLPropertyAssertionAxiom(axiom);
    } // if

    exportOWLAxiom(axiom); // Export the axiom to the rule engine.
  }

  public void injectOWLDataPropertyAssertionAxioms(Set<OWLDataPropertyAssertionAxiomReference> axioms)
    throws SWRLBuiltInBridgeException
  {
    for (OWLDataPropertyAssertionAxiomReference axiom : axioms) injectOWLDatatypePropertyAssertionAxiom(axiom);
  }

  public void injectOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiomReference axiom)
    throws SWRLBuiltInBridgeException
  {
    if (!injectedOWLAxioms.contains(axiom)) {
      injectedOWLAxioms.add(axiom);
      cacheOWLPropertyAssertionAxiom(axiom);
    } // if

    exportOWLAxiom(axiom); // Export the axiom to the rule engine.
  }

  public boolean isOWLClass(String classURI) 
  { 
	 return owlAxiomProcessor.isReferencedOWLClass(classURI) || injectedOWLClassDeclarations.containsKey(classURI) ||
	        activeOntology.containsClassInSignature(classURI, true);
  }
  
  public boolean isOWLObjectProperty(String propertyURI) 
  { 
	  return owlAxiomProcessor.isReferencedOWLObjectProperty(propertyURI) || activeOntology.containsObjectPropertyInSignature(propertyURI, true);
  }
  
  public boolean isOWLDataProperty(String propertyURI) 
  { 
	  return owlAxiomProcessor.isReferencedOWLDataProperty(propertyURI) || activeOntology.containsDataPropertyInSignature(propertyURI, true);
  }

  public boolean isOWLIndividual(String individualURI) 
  { 
	  return owlAxiomProcessor.isReferencedOWLIndividual(individualURI) || activeOntology.containsIndividualInSignature(individualURI, true);
  }
  
  public boolean isOWLIndividualOfClass(String individualURI, String classURI)
  {
    boolean result = false; 
    
    if (allOWLIndividualDeclarations.containsKey(individualURI)) {
      OWLNamedIndividualReference owlIndividual = allOWLIndividualDeclarations.get(individualURI);
      result = owlIndividual.hasType(classURI);
    } // if

    if (!result) result = activeOntology.isOWLNamedIndividualOfClass(individualURI, classURI);

    return result;
  } 
  
  public Set<OWLNamedIndividualReference> getOWLIndividuals() { return new HashSet<OWLNamedIndividualReference>(allOWLIndividualDeclarations.values()); }

  public Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String individualURI, String propertyURI) 
    throws SWRLBuiltInBridgeException
  {
	  if (allOWLPropertyAssertionAxioms.containsKey(individualURI)) {
	    Map<String, Set<OWLPropertyAssertionAxiomReference>> propertyAxiomsMap = allOWLPropertyAssertionAxioms.get(individualURI);
	    if (propertyAxiomsMap.containsKey(propertyURI)) return propertyAxiomsMap.get(propertyURI);
	    else return new HashSet<OWLPropertyAssertionAxiomReference>();
	  } else {
	  	Set<OWLPropertyAssertionAxiomReference> result = null;
	  	try {
	  	  result = activeOntology.getOWLPropertyAssertionAxioms(individualURI, propertyURI);
	  	} catch (DataValueConversionException e) {
	    	  throw new SWRLBuiltInBridgeException("error getting property assertion axiom for individual " + individualURI + 
	    			                                   ", property " + propertyURI + ": " + e.getMessage(), e); 
	      } catch (OWLConversionFactoryException e) {
	    	  throw new SWRLBuiltInBridgeException("error getting property assertion axiom for individual " + individualURI + 
	   			                                     ", property " + propertyURI + ": " + e.getMessage(), e);
	      } // try
	      return result;
	  } // if
  } 

  public OWLOntology getActiveOntology() { return activeOntology; }
  public OWLDataFactory getOWLDataFactory() { return dataFactory; }
  public OWLDataValueFactory getOWLDataValueFactory() { return dataValueFactory; }

  public boolean isSQWRLQuery(String uri) { return owlAxiomProcessor.isSQWRLQuery(uri); }
  public SQWRLResultImpl getSQWRLResult(String uri) throws SQWRLException { return owlAxiomProcessor.getSQWRLResult(uri); }
  public SQWRLResultImpl getSQWRLUnpreparedResult(String uri) throws SQWRLException { return owlAxiomProcessor.getSQWRLUnpreparedResult(uri); }
  public List<SWRLAtomReference> getSQWRLPhase1BodyAtoms(SWRLRuleReference query) { return owlAxiomProcessor.getSQWRLPhase1BodyAtoms(query); }
  public List<SWRLAtomReference> getSQWRLPhase2BodyAtoms(SWRLRuleReference query) { return owlAxiomProcessor.getSQWRLPhase2BodyAtoms(query); }
  public boolean usesSQWRLCollections(SWRLRuleReference query) { return owlAxiomProcessor.usesSQWRLCollections(query); }

  private void initialize()
  {  
    inferredOWLIndividualDeclarations = new HashMap<String, OWLNamedIndividualReference>(); 
    inferredOWLAxioms = new HashSet<OWLAxiomReference>(); 

    allOWLPropertyAssertionAxioms = new HashMap<String, Map<String, Set<OWLPropertyAssertionAxiomReference>>>();
    allOWLIndividualDeclarations = new HashMap<String, OWLNamedIndividualReference>();

    injectedOWLClassDeclarations = new HashMap<String, OWLClassReference>();
    injectedOWLIndividualDeclarations = new HashMap<String, OWLNamedIndividualReference>();
    injectedOWLAxioms = new HashSet<OWLAxiomReference>();
  }

  private void cacheOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiomReference axiom)
  {
    String subjectURI = axiom.getSubject().getURI();
    String propertyURI = axiom.getProperty().getURI();
    Map<String, Set<OWLPropertyAssertionAxiomReference>> propertyAxiomsMap;
    Set<OWLPropertyAssertionAxiomReference> axiomSet;

    if (allOWLPropertyAssertionAxioms.containsKey(subjectURI)) 
      propertyAxiomsMap = allOWLPropertyAssertionAxioms.get(subjectURI);
    else {
      propertyAxiomsMap = new HashMap<String, Set<OWLPropertyAssertionAxiomReference>>();
      allOWLPropertyAssertionAxioms.put(subjectURI, propertyAxiomsMap);
    } // if

    if (propertyAxiomsMap.containsKey(propertyURI)) axiomSet = propertyAxiomsMap.get(propertyURI);
    else {
      axiomSet = new HashSet<OWLPropertyAssertionAxiomReference>();
      propertyAxiomsMap.put(propertyURI, axiomSet);
    } // if
    
    axiomSet.add(axiom);     
  }

  public void cacheOWLIndividual(OWLNamedIndividualReference owlIndividual)
  {
    String individualURI = owlIndividual.getURI();

    if (!allOWLIndividualDeclarations.containsKey(individualURI)) allOWLIndividualDeclarations.put(individualURI, owlIndividual);
  }  

  public void cacheOWLIndividuals(Set<OWLNamedIndividualReference> individuals)
  {
    for (OWLNamedIndividualReference individual: individuals) cacheOWLIndividual(individual);
  }

  private void checkOWLClassURI(String classURI) throws SWRLBuiltInBridgeException
  {
    if (!activeOntology.isValidURI(classURI))
    	throw new SWRLBuiltInBridgeException("attempt to inject class with invalid URI " + classURI);
  }

  private void exportOWLClassDeclaration(OWLClassReference owlClass) throws SWRLBuiltInBridgeException
  {
    OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(owlClass);
    exportOWLAxiom(axiom);
  } 

  private void exportOWLIndividualDeclaration(OWLNamedIndividualReference owlIndividual) throws SWRLBuiltInBridgeException
  {
  	OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(owlIndividual);
    exportOWLAxiom(axiom);
   } 

  private void exportOWLAxiom(OWLAxiomReference owlAxiom) throws SWRLBuiltInBridgeException
  {
    try {
      targetRuleEngine.defineOWLAxiom(owlAxiom);
    } catch (TargetSWRLRuleEngineException e) {
      throw new SWRLBuiltInBridgeException("error exporting OWL axiom to target rule engine " + owlAxiom + ": " + e.getMessage());
    } // try
  }
}