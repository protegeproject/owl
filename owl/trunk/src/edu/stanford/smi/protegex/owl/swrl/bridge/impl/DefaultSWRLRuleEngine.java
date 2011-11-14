
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLAxiomProcessor;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValueFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLBuiltInBridgeController;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridgeController;
import edu.stanford.smi.protegex.owl.swrl.bridge.TargetSWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.TargetSWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResult;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

/**
 * This class provides an implementation of some of the core functionality required by SWRL rule engine. Detailed
 * documentation for this mechanism can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public class DefaultSWRLRuleEngine implements SWRLRuleEngine
{
	private OWLOntology activeOntology;
  private OWLAxiomProcessor owlAxiomProcessor;
  private TargetSWRLRuleEngine targetRuleEngine;
  private SWRLBuiltInBridgeController builtInBridgeController;
  private SWRLRuleEngineBridgeController ruleEngineBridgeController;
  private OWLDataFactory owlDataFactory;
  private OWLDataValueFactory owlDataValueFactory; 

  // URIs of classes and individuals that have been exported to target rule engine for declaration
  private Set<String> exportedOWLClassURIs, exportedOWLPropertyURIs, exportedOWLIndividualURIs; 
  
  public DefaultSWRLRuleEngine(OWLOntology activeOntology, OWLAxiomProcessor owlAxiomProcessor, TargetSWRLRuleEngine targetRuleEngine, 
  		                         SWRLRuleEngineBridgeController ruleEngineBridgeController, SWRLBuiltInBridgeController builtInBridgeController) 
  throws SWRLRuleEngineException
  {
  	this.activeOntology = activeOntology;
    this.owlAxiomProcessor = owlAxiomProcessor;
    this.targetRuleEngine = targetRuleEngine;
    this.builtInBridgeController = builtInBridgeController;
    this.ruleEngineBridgeController = ruleEngineBridgeController;

    owlDataFactory = new P3OWLDataFactory(activeOntology);
    owlDataValueFactory = OWLDataValueFactory.create();
    
    initialize();   
  }

  /**
   * Load rules and knowledge from OWL into bridge. All existing bridge rules and knowledge will first be cleared and the associated rule
   * engine will be reset.
   */
  public void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineException
  {
  	owlAxiomProcessor.processSWRLRules();
  	
  	exportOWLDeclarationAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLDeclarationAxioms()); 	
    exportOWLAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLAxioms()); 
    exportSWRLRules2TargetRuleEngine(owlAxiomProcessor.getReferencedSWRLRules());
  }

  /**
   * Load named query, all enabled rules, and all relevant knowledge from OWL into bridge. All existing bridge rules and knowledge will 
   * first be cleared and the associated rule engine will be reset.
   */
  public void importSQWRLQueryAndOWLKnowledge(String queryName) throws SWRLRuleEngineException
  {
  	owlAxiomProcessor.processSWRLRules();
  	owlAxiomProcessor.processSQWRLQuery(queryName);
  	
  	exportOWLDeclarationAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLDeclarationAxioms()); 	
    exportOWLAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLAxioms()); 
    exportSWRLRules2TargetRuleEngine(owlAxiomProcessor.getReferencedSWRLRules());
  }

  /**
   * Run the rule engine.
   */
  public void run() throws SWRLRuleEngineException
  {
    getRuleEngine().runRuleEngine();
  } 

  /**
   * Clear all knowledge from bridge.
   */
  public void reset() throws SWRLRuleEngineException
  {
    getRuleEngine().resetRuleEngine(); // Reset the target rule engine
    builtInBridgeController.reset();
    owlAxiomProcessor.reset();
    initialize();
  } 

  /**
   * Write knowledge inferred by rule engine back to OWL.
   */
  public void writeInferredKnowledge2OWL() throws SWRLRuleEngineException
  {
  	// Order of creation is important here.
  	writeInjectedOWLClassDeclarations2ActiveOntology(); // Write any OWL classes generated by built-ins in rules. 
  	writeInjectedOWLIndividualDeclarations2ActiveOntology(); // Write any OWL individuals generated by built-ins in rules. 
  	writeInjectedOWLAxioms2ActiveOntology(); // Write any OWL axioms generated by built-ins in rules. 
  	writeInferredOWLIndividualDeclarations2ActiveOntology();
  	writeInferredOWLAxioms2ActiveOntology();
  } 

  /**
   * Load rules and knowledge from OWL into bridge, send them to a rule engine, run the rule engine, and write any inferred knowledge back
   * to OWL.
   */
  public void infer() throws SWRLRuleEngineException
  {
    reset();
    importSWRLRulesAndOWLKnowledge();
    run();
    writeInferredKnowledge2OWL();
  } 
  
  public SQWRLResult runSQWRLQuery(String queryName, String queryText) throws SQWRLException, SWRLParseException
  { 
  	createSQWRLQuery(queryName, queryText);
  	
  	return runSQWRLQuery(queryName);
  }
  
  public void createSQWRLQuery(String queryName, String queryText) throws SQWRLException, SWRLParseException
  {
  	try {
  		activeOntology.createSWRLRule(queryName, queryText);
  	} catch (OWLConversionFactoryException e) {
  	  throw new SQWRLException("error creating SQWRL query: " + e.getMessage());
  	} // try
  }

  /**
   * Run a named SQWRL query. SWRL rules will also be executed and any inferences produced by them will be available in the query.
   */
  public SQWRLResult runSQWRLQuery(String queryName) throws SQWRLException
  {
    SQWRLResult result = null;
    
    try {
      reset();
      importSQWRLQueryAndOWLKnowledge(queryName);
      run();
      result = getSQWRLResult(queryName);
    } catch (SWRLRuleEngineException e) {
      throw new SQWRLException("error running SQWRL queries: " + e.getMessage());
    } // try
    
    return result;
  }

  /**
   * Run a named SQWRL query without executing any SWRL rules in ontology.
   */
  public SQWRLResult runStandaloneSQWRLQuery(String queryName) throws SQWRLException
  {
    SQWRLResult result = null;
    
    try {
      reset();
      importSQWRLQuery(queryName);
      run();
      result = getSQWRLResult(queryName);
    } catch (SWRLRuleEngineException e) {
      throw new SQWRLException("error running SQWRL queries: " + e.getMessage());
    } // try
    
    return result;
  }

  /**
   *  Run all SQWRL queries.
   */
  public void runSQWRLQueries() throws SQWRLException
  {
    try {
      reset();
      importSWRLRulesAndOWLKnowledge();
      run();
    } catch (SWRLRuleEngineException e) {
      throw new SQWRLException("error running SQWRL queries: " + e.getMessage());
    } // try
  }

  /**
   *  Get the results of a previously executed SQWRL query.
   */
  public SQWRLResultImpl getSQWRLResult(String queryURI) throws SQWRLException
  {
  	return owlAxiomProcessor.getSQWRLResult(queryURI);
  }
  
  public void deleteSQWRLQuery(String queryURI) throws SQWRLException
  {
  	try {
  		activeOntology.deleteSWRLRule(queryURI);
  	} catch (OWLConversionFactoryException e) {
  		throw new SQWRLException("error deleting SQWRL query " + queryURI);
  	}
  }

  /**
   * Get all the enabled SQWRL queries in the ontology.
   */
  public Set<SWRLRuleReference> getSQWRLQueries() throws SQWRLException
  {
  	return owlAxiomProcessor.getSQWRLQueries();
  }
  
  /**
   * Get the names of the SQWRL queries in the ontology.
   */
  public Set<String> getSQWRLQueryNames() throws SQWRLException
  {
  	return owlAxiomProcessor.getSQWRLQueryNames();
  }

  
  public SQWRLResultImpl getSQWRLUnpreparedResult(String queryURI) throws SQWRLException
  {
  	return owlAxiomProcessor.getSQWRLUnpreparedResult(queryURI);
  }

  public SWRLRuleReference getSWRLRule(String ruleURI) throws SWRLRuleEngineException
  {
  	return owlAxiomProcessor.getSWRLRule(ruleURI);
  }

  // Convenience methods to display bridge activity
  public int getNumberOfImportedSWRLRules() { return owlAxiomProcessor.getNumberOfReferencedSWRLRules(); }
  public int getNumberOfImportedOWLClasses() { return owlAxiomProcessor.getNumberOfReferencedOWLClassDeclarationAxioms(); }
  public int getNumberOfImportedOWLIndividuals() { return owlAxiomProcessor.getNumberOfReferencedOWLIndividualDeclarationAxioms(); }
  public int getNumberOfImportedOWLAxioms()  { return owlAxiomProcessor.getNumberOfReferencedOWLAxioms(); }
  
  public int getNumberOfInferredOWLIndividuals() { return ruleEngineBridgeController.getNumberOfInferredOWLIndividuals(); }
  public int getNumberOfInferredOWLAxioms() { return ruleEngineBridgeController.getNumberOfInferredOWLAxioms(); }

  public int getNumberOfInjectedOWLClasses() { return builtInBridgeController.getNumberOfInjectedOWLClassDeclarations(); }
  public int getNumberOfInjectedOWLIndividuals() { return builtInBridgeController.getNumberOfInjectedOWLIndividualDeclarations(); }
  public int getNumberOfInjectedOWLAxioms() { return builtInBridgeController.getNumberOfInjectedOWLAxioms(); }

  public boolean isInjectedOWLClass(String classURI) { return builtInBridgeController.isInjectedOWLClass(classURI); }
  public boolean isInjectedOWLIndividual(String individualURI) { return builtInBridgeController.isInjectedOWLIndividual(individualURI); }
  public boolean isInjectedOWLAxiom(OWLAxiomReference axiom) { return builtInBridgeController.isInjectedOWLAxiom(axiom); }

  // Convenience methods to display the contents of the bridge
  public Set<SWRLRuleReference> getImportedSWRLRules() { return owlAxiomProcessor.getReferencedSWRLRules(); }
  
  public Set<OWLClassReference> getImportedOWLClasses() 
  {
  	Set<OWLClassReference> result = new HashSet<OWLClassReference>();
  	for (OWLDeclarationAxiomReference axiom : owlAxiomProcessor.getReferencedOWLClassDeclarationsAxioms()) {
  		if (axiom.getEntity() instanceof OWLClassReference)
  			result.add((OWLClassReference)axiom.getEntity());
  	} // for
  	return result;
  }

  public Set<OWLNamedIndividualReference> getImportedOWLIndividuals() 
  {
  	Set<OWLNamedIndividualReference> result = new HashSet<OWLNamedIndividualReference>();
  	
  	for (OWLDeclarationAxiomReference axiom : owlAxiomProcessor.getReferencedOWLIndividualDeclarationAxioms()) {
  		if (axiom.getEntity() instanceof OWLNamedIndividualReference)
  			result.add((OWLNamedIndividualReference)axiom.getEntity());
  	} // for
  	return result;
  }
  
  public Set<OWLAxiomReference> getImportedOWLAxioms() { return owlAxiomProcessor.getReferencedOWLAxioms(); }

  public Set<OWLNamedIndividualReference> getReclassifiedOWLIndividuals(){ return ruleEngineBridgeController.getInferredOWLIndividuals(); }
  public Set<OWLAxiomReference> getInferredOWLAxioms() { return ruleEngineBridgeController.getInferredOWLAxioms(); }

  public Set<OWLAxiomReference> getInjectedOWLAxioms() { return getInjectedOWLAxioms(); }
  public Set<OWLClassReference> getInjectedOWLClasses() { return builtInBridgeController.getInjectedOWLClassDeclarations(); }
  public Set<OWLNamedIndividualReference> getInjectedOWLIndividuals() { return builtInBridgeController.getInjectedOWLIndividualDeclarations(); }

  public String uri2PrefixedName(String uri)
  {
  	return activeOntology.uri2PrefixedName(uri);
  }
  
  public String name2URI(String prefixedName)
  {
  	return activeOntology.prefixedName2URI(prefixedName);
  }

  public OWLDataFactory getOWLDataFactory() { return owlDataFactory; }
  public OWLDataValueFactory getOWLDataValueFactory() { return owlDataValueFactory; }
  
  public String getTargetRuleEngineName() { return targetRuleEngine.getName(); }
  public String getTargetRuleEngineVersion() { return targetRuleEngine.getVersion(); }

  private void importSQWRLQuery(String queryName) throws SWRLRuleEngineException
  {
  	owlAxiomProcessor.processSQWRLQuery(queryName);
  	
  	exportSQWRLQuery2TargetRuleEngine(owlAxiomProcessor.getSQWRLQuery(queryName));
  	exportOWLDeclarationAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLDeclarationAxioms());  
  	exportOWLAxioms2TargetRuleEngine(owlAxiomProcessor.getReferencedOWLAxioms());
  } 
  
  private void exportSWRLRules2TargetRuleEngine(Set<SWRLRuleReference> rules) throws SWRLRuleEngineException
  {
  	for (SWRLRuleReference rule : rules)
  		exportSWRLRule2TargetRuleEngine(rule);
  }
  
  private void exportSWRLRule2TargetRuleEngine(SWRLRuleReference rule) throws SWRLRuleEngineException
{
    getRuleEngine().defineOWLAxiom(rule);
  }

  private void exportSQWRLQuery2TargetRuleEngine(SWRLRuleReference query) throws SWRLRuleEngineException
  {
    getRuleEngine().defineOWLAxiom(query);
  }

  private void exportOWLDeclarationAxioms2TargetRuleEngine(Set<OWLDeclarationAxiomReference> axioms) throws SWRLRuleEngineException
  {
    for (OWLDeclarationAxiomReference axiom : axioms) 
    	exportOWLDeclarationAxiom2TargetRuleEngine(axiom);
  }

  private void exportOWLDeclarationAxiom2TargetRuleEngine(OWLDeclarationAxiomReference axiom) throws SWRLRuleEngineException
  {
  	if (axiom.getEntity() instanceof OWLClassReference) {
  		OWLClassReference owlClass = (OWLClassReference)axiom.getEntity();
  		String classURI = owlClass.getURI();
  		if (!exportedOWLClassURIs.contains(classURI)) { // See if it is already defined.
  			exportOWLAxiom2TargetRuleEngine(axiom);
  			exportedOWLClassURIs.add(classURI);
  		} // if
  	} else if (axiom.getEntity() instanceof OWLPropertyReference) {
  		OWLPropertyReference owlProperty = (OWLPropertyReference)axiom.getEntity();
      String propertyURI = owlProperty.getURI();

      if (!exportedOWLPropertyURIs.contains(propertyURI)) { // See if it is already defined.
        exportOWLAxiom2TargetRuleEngine(axiom);
        exportedOWLPropertyURIs.add(propertyURI);
      } // if
  	} else if (axiom.getEntity() instanceof OWLNamedIndividualReference) {
  		OWLNamedIndividualReference owlIndividual = (OWLNamedIndividualReference)axiom.getEntity();
      String individualURI = owlIndividual.getURI();
      if (!exportedOWLIndividualURIs.contains(individualURI)) {
        exportOWLAxiom2TargetRuleEngine(axiom);
        exportedOWLIndividualURIs.add(individualURI);
      } // if
  	} else throw new SWRLRuleEngineException("unknown declaration axiom " + axiom);
  } 

  private TargetSWRLRuleEngine getRuleEngine() throws SWRLRuleEngineException
  {
  	if (targetRuleEngine == null) throw new SWRLRuleEngineException("no target rule engine specified");
  	
  	return targetRuleEngine;
  }

  private void exportOWLAxioms2TargetRuleEngine(Set<OWLAxiomReference> axioms) throws SWRLRuleEngineException
  {
  	for (OWLAxiomReference axiom : axioms)
  		exportOWLAxiom2TargetRuleEngine(axiom);
  }

  private void exportOWLAxiom2TargetRuleEngine(OWLAxiomReference owlAxiom) throws SWRLRuleEngineException
{
    try {
      getRuleEngine().defineOWLAxiom(owlAxiom);
    } catch (TargetSWRLRuleEngineException e) {
      throw new SWRLRuleEngineException("error exporting OWL axiom " + owlAxiom + " to rule engine: " + e.getMessage());
    } // try
  }

  private void writeInferredOWLIndividualDeclarations2ActiveOntology() throws SWRLRuleEngineException
  {
    for (OWLNamedIndividualReference owlIndividual : ruleEngineBridgeController.getInferredOWLIndividuals()) {
    	try {
    		activeOntology.writeOWLIndividualDeclaration(owlIndividual);
  		} catch (OWLConversionFactoryException e) {
  			throw new SWRLRuleEngineException("error writing inferred individual " + owlIndividual + ": " + e.getMessage());
  		} // try
    } // for
  }

  private void writeInferredOWLAxioms2ActiveOntology() throws SWRLRuleEngineException
  {
    for (OWLAxiomReference axiom : ruleEngineBridgeController.getInferredOWLAxioms())  {
    	try {
    		activeOntology.writeOWLAxiom(axiom);
  		} catch (OWLConversionFactoryException e) {
  			throw new SWRLRuleEngineException("error writing inferred axiom " + axiom + ": " + e.getMessage());
  		} // try
    } // for
  } 

  /**
   * Create OWL individuals in model for the individuals injected by built-ins during rule execution.
   */
  private void writeInjectedOWLIndividualDeclarations2ActiveOntology() throws SWRLRuleEngineException
  {
    for (OWLNamedIndividualReference owlIndividual: builtInBridgeController.getInjectedOWLIndividualDeclarations()) {
    	try {
    		activeOntology.writeOWLIndividualDeclaration(owlIndividual);
  		} catch (OWLConversionFactoryException e) {
  			throw new SWRLRuleEngineException("error writing injected individual " + owlIndividual + ": " + e.getMessage());
  		} // try
    } // for
  } 

  /**
   * Create OWL axioms in model for the axioms injected by built-ins during rule execution.
   */
  private void writeInjectedOWLAxioms2ActiveOntology() throws SWRLRuleEngineException
  {
    for (OWLAxiomReference axiom : builtInBridgeController.getInjectedOWLAxioms()) {
    	try {
    		activeOntology.writeOWLAxiom(axiom);
  		} catch (OWLConversionFactoryException e) {
  			throw new SWRLRuleEngineException("error writing injected axiom " + axiom + ": " + e.getMessage());
  		} // try
    } // for
  } 

  /**
   * Create OWL classes in model for the classes injected by built-ins during rule execution.
   */
  private void writeInjectedOWLClassDeclarations2ActiveOntology() throws SWRLRuleEngineException
  {
  	for (OWLClassReference owlClass: builtInBridgeController.getInjectedOWLClassDeclarations()) {
  		try {
  			activeOntology.writeOWLClassDeclaration(owlClass);
  		} catch (OWLConversionFactoryException e) {
  			throw new SWRLRuleEngineException("error writing injected class " + owlClass + ": " + e.getMessage());
  		} // try
  	} // for
  } 

  private void initialize()
  {  
  	exportedOWLClassURIs = new HashSet<String>();
  	exportedOWLPropertyURIs = new HashSet<String>();
    exportedOWLIndividualURIs = new HashSet<String>();
  }
}