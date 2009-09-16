
// TODO: way too big - needs ot be refactored
// TODO: DataRange
// TODO: remove all Protege-OWL specific code

package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;

import java.util.*;
import java.io.*;

/**
 ** This class provides an implementation of some of the core functionality required by SWRL rule engine and built-in bridges. Detailed
 ** documentation for these bridges can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public abstract class AbstractSWRLRuleEngineBridge implements SWRLRuleEngineBridge, SWRLBuiltInBridge, TargetSWRLRuleEngine
{
  protected OWLModel owlModel; // Holds the OWL model that is associated with this bridge

  private HashMap<String, SWRLRule> importedSWRLRules; 

  // Names of classes, properties and individuals explicitly referred to in SWRL rules. These are filled in as the SWRL rules are imported
  // and are used to determine the relevant OWL knowledge to import.
  private Set<String> referencedClassNames, referencedPropertyNames, referencedIndividualNames;

  // Imported classes, properties, and individuals
  private HashMap<String, OWLClass> importedClasses;
  private HashMap<String, OWLIndividual> importedIndividuals;
  private Set<String> importedPropertyNames;
  private Set<OWLAxiom> importedAxioms;

  private Map<String, Map<String, Set<OWLPropertyAssertionAxiom>>> bridgeOWLPropertyAssertionAxioms; 
  private Map<String, OWLIndividual> bridgeOWLIndividuals; 

  // Names of classes, properties, and individuals that have been exported to target rule engine
  private Set<String> exportedClassNames, exportedIndividualNames; 

  // Inferred individuals and property assertion axioms
  private Map<String, OWLIndividual> inferredIndividuals;
  private Set<OWLAxiom> inferredAxioms; 

  // Injected entities
  private HashMap<String, OWLClass> injectedAnonymousClasses;
  private HashMap<String, OWLClass> injectedClasses;
  private HashMap<String, OWLIndividual> injectedIndividuals;
  private Set<OWLAxiom> injectedAxioms;

  // Mapper
  private Mapper mapper = null;

  private SWRLFactory factory;

  protected AbstractSWRLRuleEngineBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    this.owlModel = owlModel;
    initialize();
    BuiltInLibraryManager.invokeAllBuiltInLibrariesResetMethod(this);
    factory = new SWRLFactory(owlModel);
  } // AbstractSWRLRuleEngineBridge

  /**
   ** Load rules and knowledge from OWL into bridge. All existing bridge rules and knowledge will first be cleared and the associated rule
   ** engine will be reset.
   */
  public void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    importSWRLRulesAndOWLKnowledge(new HashSet<String>());
  } // importSWRLRulesAndOWLKnowledge

  /**
   ** Load rules from a particular rule group and associated knowledge from OWL into bridge. All existing bridge rules and knowledge will
   ** first be cleared and the associated rule engine will be reset.
   */
  public void importSWRLRulesAndOWLKnowledge(String ruleGroupName) throws SWRLRuleEngineException
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    importSWRLRulesAndOWLKnowledge(ruleGroupNames);
  } // importSWRLRulesAndOWLKnowledge

  // TODO: this needs to be more principled

  /**
   ** Load rules from all the named rule groups and associated knowledge from OWL into bridge. All existing bridge rules and knowledge will
   ** first be cleared and the associated rule engine will be reset.
   */
  public void importSWRLRulesAndOWLKnowledge(Set<String> ruleGroupNames) throws SWRLRuleEngineBridgeException
  {
    if (SWRLOWLUtil.hasInconsistentClasses(owlModel))
      throw new InconsistentKnowledgeBaseException("cannot import rules from an inconsistent ontology");

    importSWRLRules(ruleGroupNames); // Fills in importedSWRLRules, referencedClassNames, referencedPropertyNames, and referencedIndividualNames

    importOWLClassNames(referencedClassNames); // Import all (directly or indirectly) referenced classes.

    importOWLPropertyAssertionAxioms(referencedPropertyNames); // Import property assertion axioms for (directly or indirectly) referenced properties

    importOWLIndividuals(referencedIndividualNames); // Import all directly referenced individuals.

    importAllOWLIndividualsOfClasses(referencedClassNames); // Import all individuals that are members of imported classes.

    importAxioms(); // Import some other axioms (owl:sameAs, owl:differentFrom, and owl:allDifferent for the moment).

    exportSWRLRulesAndOWLKnowledge();
  } // importSWRLRulesAndOWLKnowledge

  /**
   ** Send rules and knowledge stored in bridge to a rule engine.
   */
  private void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    long startTime = System.currentTimeMillis();
    exportClasses(); // Classes should be exported before rules because rules usually use class definitions.

    startTime = System.currentTimeMillis();
    exportSWRLRules();

    startTime = System.currentTimeMillis();
    exportIndividuals();

    startTime = System.currentTimeMillis();
    exportAxioms();
  } // exportSWRLRulesAndOWLKnowledge

  /**
   ** Send knowledge (excluding SWRL rules) stored in bridge to a rule engine.
   */
  private void exportOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportClasses();
    exportIndividuals();
    exportAxioms();
  } // exportOWLKnowledge

  /**
   ** Run the rule engine.
   */
  public void run() throws SWRLRuleEngineBridgeException
  {
    if (hasMapper()) mapper.open();
    runRuleEngine();
    if (hasMapper()) mapper.close();
  } // run

  /**
   ** Write knowledge inferred by rule engine back to OWL.
   */
  public void writeInferredKnowledge2OWL() throws SWRLRuleEngineBridgeException
  {
    // Order of creation is important here.
    writeInjectedClasses(); // Create any OWL classes generated by built-ins in rules. 
    writeInjectedIndividuals(); // Create any OWL individuals generated by built-ins in rules. 
    writeInjectedAxioms(); // Create any OWL axioms generated by built-ins in rules. 

    writeInferredIndividuals2OWL();
    writeInferredAxioms2OWL();
  } // writeInferredKnowledge2OWL

  /**
   ** Clear all knowledge from bridge.
   */
  public void reset() throws SWRLRuleEngineBridgeException
  {
    resetRuleEngine(); // Reset the underlying rule engine

    BuiltInLibraryManager.invokeAllBuiltInLibrariesResetMethod(this);

    initialize();
  } // resetBridge

  /**
   ** Load rules and knowledge from OWL into bridge, send them to a rule engine, run the rule engine, and write any inferred knowledge back
   ** to OWL.
   */
  public void infer(Set<String> ruleGroupNames) throws SWRLRuleEngineBridgeException
  {
    reset();
    importSWRLRulesAndOWLKnowledge(ruleGroupNames);
    run();
    writeInferredKnowledge2OWL();
  } // infer

  public void infer(String ruleGroupName) throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    infer(ruleGroupNames);
  } // infer

  public void infer() throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    infer(ruleGroupNames);
  } // infer

  public void runSQWRLQueries() throws SQWRLException
  {
    try {
      reset();
      importSWRLRulesAndOWLKnowledge();
      run();
    } catch (SWRLRuleEngineBridgeException e) {
      throw new SQWRLException("error running SQWRL queries: " + e.getMessage());
    } // try
  } // runSQWRLQueries

  /**
   **  Get the results from a SQWRL query.
   */
  public SQWRLResult getSQWRLResult(String queryName) throws SQWRLException
  {
    ResultImpl result;

    if (!importedSWRLRules.containsKey(queryName)) throw new InvalidQueryNameException(queryName);

    result = importedSWRLRules.get(queryName).getSQWRLResult();

    if (!result.isPrepared()) result.prepared();

    return result;
  } // getSQWRLResult

  public SWRLRule getSWRLRule(String ruleName) throws SWRLBuiltInBridgeException
  {
    if (!importedSWRLRules.containsKey(ruleName)) throw new SWRLBuiltInBridgeException("invalid rule name '" + ruleName + "'");

    return importedSWRLRules.get(ruleName);
  } // getRule

  /**
   ** Get the OWL model associated with this bridge.
   */
  public OWLModel getOWLModel() { return owlModel; }

  // Convenience methods to display bridge activity
  public int getNumberOfImportedSWRLRules() { return importedSWRLRules.size(); }
  public int getNumberOfImportedClasses() { return importedClasses.size(); }
  public int getNumberOfImportedIndividuals() { return importedIndividuals.size(); }
  public int getNumberOfImportedAxioms() { return importedAxioms.size(); }
  public int getNumberOfInferredIndividuals() { return inferredIndividuals.keySet().size(); }
  public int getNumberOfInferredAxioms() { return inferredAxioms.size(); }
  public int getNumberOfInjectedClasses() { return injectedClasses.size(); }
  public int getNumberOfInjectedIndividuals() { return injectedIndividuals.size(); }
  public int getNumberOfInjectedAxioms() { return injectedAxioms.size(); }

  public boolean isOWLClass(String className) { return importedClasses.containsKey(className) || injectedClasses.containsKey(className); }
  public boolean isOWLProperty(String propertyName) { return importedPropertyNames.contains(propertyName); }
  public boolean isOWLIndividual(String individualName) { return importedIndividuals.containsKey(individualName) || injectedIndividuals.containsKey(individualName); }

  public boolean isInjectedOWLClass(String className) { return injectedClasses.containsKey(className); }
  public boolean isInjectedOWLAnonymousClass(String className) { return injectedAnonymousClasses.containsKey(className); }
  public boolean isInjectedOWLIndividual(String individualName) { return injectedIndividuals.containsKey(individualName); }
  public boolean isInjectedOWLAxiom(OWLAxiom axiom) { return injectedAxioms.contains(axiom); }

  // Convenience methods to display the contents of the bridge
  public Set<SWRLRule> getImportedSWRLRules() { return new HashSet<SWRLRule>(importedSWRLRules.values()); }

  public Set<OWLClass> getImportedClasses() { return new HashSet<OWLClass>(importedClasses.values()); }
  public Set<OWLIndividual> getImportedIndividuals() { return new HashSet<OWLIndividual>(importedIndividuals.values()); }
  public Set<OWLAxiom> getImportedAxioms() { return importedAxioms; }

  public Set<OWLIndividual> getInferredIndividuals() { return new HashSet<OWLIndividual>(inferredIndividuals.values()); }
  public Set<OWLAxiom> getInferredAxioms() { return inferredAxioms; }

  public Set<OWLClass> getInjectedClasses() { return new HashSet<OWLClass>(injectedClasses.values()); }
  public Set<OWLClass> getInjectedAnonymousClasses() { return new HashSet<OWLClass>(injectedAnonymousClasses.values()); }
  public Set<OWLIndividual> getInjectedIndividuals() { return new HashSet<OWLIndividual>(injectedIndividuals.values()); }
  public Set<OWLAxiom> getInjectedAxioms() { return injectedAxioms; }

  /**
   ** Infer an OWL property assertion axiom from a rule engine
   */
  public void inferOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiom axiom) throws SWRLRuleEngineBridgeException
  { 
    if (!inferredAxioms.contains(axiom)) {
      inferredAxioms.add(axiom); 
      cacheOWLPropertyAssertionAxiom(axiom);
    } // if
  } // inferOWLPropertyAssertionAxiom

  /**
   ** Assert an OWL individual from a rule engine
   */
  public void inferOWLIndividual(OWLIndividual owlIndividual, OWLClass owlClass) throws SWRLRuleEngineBridgeException 
  {
    String individualName = owlIndividual.getIndividualName();

    if (inferredIndividuals.containsKey(individualName)) inferredIndividuals.get(individualName).addDefiningClass(owlClass);
    else if (injectedIndividuals.containsKey(individualName)) injectedIndividuals.get(individualName).addDefiningClass(owlClass);
    else {
      inferredIndividuals.put(individualName, owlIndividual); 
      cacheOWLIndividual(owlIndividual);
    } // if
  } // inferOWLIndividual

  /**
   ** Invoke a SWRL built-in from a rule engine. <p>
   **
   ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ#nid6QF">here</a> for documentaton.
   */
  public boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments)
    throws BuiltInException
  {
    boolean hasUnboundArguments = SWRLBuiltInUtil.hasUnboundArguments(arguments);
    boolean result;

    if (!SWRLOWLUtil.isSWRLBuiltIn(owlModel, builtInName)) throw new InvalidBuiltInNameException(ruleName, builtInName);

    result = BuiltInLibraryManager.invokeSWRLBuiltIn(this, ruleName, builtInName, builtInIndex, isInConsequent, arguments);
    
    if (result && hasUnboundArguments) {
      SWRLBuiltInUtil.checkForUnboundArguments(ruleName, builtInName, arguments); // Ensure it did not leave any arguments unbound if it evaluated to true
      generateBuiltInBindings(ruleName, builtInName, builtInIndex, arguments); // Generate all possible bindings.
    } // if

    return result;
  } // invokeSWRLBuiltIn

  public void setMapper(Mapper mapper) 
  { 
    this.mapper = mapper; 
  } // setMapper

  public boolean hasMapper() { return mapper != null; }
  public Mapper getMapper() { return mapper; }
  
  public OWLClass injectOWLAnonymousClass() throws SWRLBuiltInBridgeException
  {
    OWLClass owlClass = OWLConversionFactory.createOWLClass(owlModel);
    String className = owlClass.getClassName();

    if (!injectedAnonymousClasses.containsKey(className)) {
      injectedAnonymousClasses.put(className, owlClass);
      exportOWLClass(owlClass); // Export the class to the rule engine
    } // if

    return owlClass;
  } // injectOWLAnonymousClass

  public void injectOWLClass(String className) throws SWRLBuiltInBridgeException
  {
    checkOWLClassName(className);

    if (!injectedClasses.containsKey(className)) {
      OWLClass owlClass = OWLFactory.createOWLClass(className);
      injectedClasses.put(className, owlClass);
      exportOWLClass(owlClass); // Export the individual to the rule engine
    } // if
  } // injectOWLClass

  public void injectOWLClass(String className, String superclassName) throws SWRLBuiltInBridgeException
  {
    checkOWLClassName(className);

    if (!injectedClasses.containsKey(className)) {
      OWLClass owlClass = OWLFactory.createOWLClass(className, superclassName);
      injectedClasses.put(className, owlClass);
      exportOWLClass(owlClass); // Export the individual to the rule engine
    } // if
  } // injectOWLClass

  /**
   ** Method used to inject a bridge individual of type owl:Thing. This method will typically be invoked from within a built-in. An OWL
   ** individual is not injected at this point - instead an object is generated for the individual in the bridge and the individual is
   ** exported to the rule engine. The individual is given a unique name that can be used later if an OWL individual is injected for it.
   */
  public OWLIndividual injectOWLIndividual() throws SWRLBuiltInBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLInjected");
    String prefix = SWRLOWLUtil.getPrefixForResourceName(owlModel, individualName);
    String localName = SWRLOWLUtil.getLocalNameForURI(owlModel, individualName);
    String prefixedIndividualName = prefix.equals("") ? localName : prefix + ":" + localName;
    OWLClass owlClass = OWLFactory.createOWLClass(edu.stanford.smi.protegex.owl.model.OWLNames.Cls.THING);
    OWLIndividual owlIndividual = OWLFactory.generateOWLIndividual(individualName, prefixedIndividualName, owlClass);
    injectOWLIndividual(owlIndividual);
    return owlIndividual;
  } // injectOWLIndividual
    
  public void injectOWLIndividual(OWLIndividual owlIndividual) throws SWRLBuiltInBridgeException
  {
    if (!injectedIndividuals.containsKey(owlIndividual.getIndividualName())) {
      injectedIndividuals.put(owlIndividual.getIndividualName(), owlIndividual); 
      cacheOWLIndividual(owlIndividual);
      exportOWLIndividual(owlIndividual); // Export the individual to the rule engine.
    } // if
  } // injectOWLIndividual

  public OWLIndividual injectOWLIndividual(OWLClass owlClass) throws SWRLBuiltInBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLInjected");
    String prefix = SWRLOWLUtil.getPrefixForResourceName(owlModel, individualName);
    String localName = SWRLOWLUtil.getLocalNameForURI(owlModel, individualName);
    String prefixedIndividualName = prefix.equals("") ? localName : prefix + ":" + localName;
    OWLIndividual owlIndividual = OWLFactory.generateOWLIndividual(individualName, prefixedIndividualName, owlClass);

    if (!importedClasses.containsKey(owlClass.getClassName())) exportOWLClass(owlClass);
   
    injectedIndividuals.put(individualName, owlIndividual); 
    cacheOWLIndividual(owlIndividual);
    exportOWLIndividual(owlIndividual); // Export the individual to the rule engine.

    return owlIndividual;
  } // injectOWLIndividual

  public void injectOWLIndividuals(Set<OWLIndividual> individuals) throws SWRLBuiltInBridgeException
  {
    for (OWLIndividual owlIndividual : individuals) injectOWLIndividual(owlIndividual);
  } // injectOWLIndividuals

  public OWLDatatypePropertyAssertionAxiom injectOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property,
                                                                                   OWLDatatypeValue object) 
    throws SWRLBuiltInBridgeException
  {
    OWLDatatypePropertyAssertionAxiom axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subject, property, object);
    injectOWLDatatypePropertyAssertionAxiom(axiom);
    return axiom;
  } // injectOWLDatatypePropertyAssertionAxiom

  public void injectOWLDatatypePropertyAssertionAxiom(OWLDatatypePropertyAssertionAxiom axiom) 
    throws SWRLBuiltInBridgeException
  {
    if (!injectedAxioms.contains(axiom)) {
      injectedAxioms.add(axiom);
      cacheOWLPropertyAssertionAxiom(axiom);
    } // if

    exportOWLAxiom(axiom); // Export the axiom to the rule engine.
  } // injectOWLDatatypePropertyAssertionAxiom

  public void injectOWLDatatypePropertyAssertionAxioms(Set<OWLDatatypePropertyAssertionAxiom> axioms)
    throws SWRLBuiltInBridgeException
  {
    for (OWLDatatypePropertyAssertionAxiom axiom : axioms) injectOWLDatatypePropertyAssertionAxiom(axiom);
  } // injectOWLDatatypePropertyAssertionAxioms

  public void injectOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiom axiom)
    throws SWRLBuiltInBridgeException
  {
    if (!injectedAxioms.contains(axiom)) {
      injectedAxioms.add(axiom);
      cacheOWLPropertyAssertionAxiom(axiom);
    } // if

    exportOWLAxiom(axiom); // Export the axiom to the rule engine.
  } // injectOWLObjectPropertyAssertionAxiom

  public void injectOWLAxiom(OWLAxiom axiom) throws SWRLBuiltInBridgeException
  {
    if (!injectedAxioms.contains(axiom)) injectedAxioms.add(axiom);

    if (axiom instanceof OWLPropertyAssertionAxiom) cacheOWLPropertyAssertionAxiom((OWLPropertyAssertionAxiom)axiom);

    exportOWLAxiom(axiom); // Export the axiom to the rule engine.
  } // injectOWLAxiom

  public boolean isOWLIndividualOfClass(String individualName, String className)
  {
    boolean result = false;

    try {
      result = SWRLOWLUtil.isIndividualOfClass(owlModel, individualName, className); // First try OWLModel
    } catch (SWRLOWLUtilException e) {}

    if (result) return result;
    
    if (bridgeOWLIndividuals.containsKey(individualName)) {
      OWLIndividual owlIndividual = bridgeOWLIndividuals.get(individualName);

      result = owlIndividual.hasClass(className);
    } // if

    return result;
  } // isOWLIndividualOfClass

  public Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String individualName, String propertyName) 
    throws SWRLBuiltInBridgeException
  {
    Map<String, Set<OWLPropertyAssertionAxiom>> propertyAxiomsMap;
    Set<OWLPropertyAssertionAxiom> axiomSet;
    OWLPropertyAssertionAxiom axiom;

    if (!bridgeOWLPropertyAssertionAxioms.containsKey(individualName)) 
      throw new SWRLBuiltInBridgeException("invalid individual name '" + individualName + "'");

    propertyAxiomsMap = bridgeOWLPropertyAssertionAxioms.get(individualName);

    if (!propertyAxiomsMap.containsKey(propertyName)) 
      throw new SWRLBuiltInBridgeException("invalid property name '" + propertyName + "'");

    return propertyAxiomsMap.get(propertyName);
  } // getOWLPropertyAssertionAxioms

  /**
   ** Create OWL classes in model for the classes injected by built-ins during rule execution.
   */
  private void writeInjectedClasses() throws SWRLRuleEngineBridgeException
  {
    for (OWLClass owlClass: injectedClasses.values()) OWLConversionFactory.write2OWLModel(owlClass, owlModel);   
  } // writeInjectedClasses

  /**
   ** Create OWL individuals in model for the individuals injected by built-ins during rule execution.
   */
  private void writeInjectedIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual: injectedIndividuals.values()) OWLConversionFactory.write2OWLModel(owlIndividual, owlModel);
  } // writeInjectedIndividuals

  /**
   ** Create OWL axioms in model for the axioms injected by built-ins during rule execution.
   */
  private void writeInjectedAxioms() throws SWRLRuleEngineBridgeException
  {
    for (OWLAxiom axiom : injectedAxioms) OWLConversionFactory.write2OWLModel(axiom, owlModel);
  } // writeInjectedAxioms

  private void importSWRLRules(Set<String> ruleGroupNames) throws SWRLRuleEngineBridgeException
  {
    Collection rules = factory.getEnabledImps(ruleGroupNames);

    if (rules == null) return;

    Iterator iterator = rules.iterator();
    while (iterator.hasNext()) {
      SWRLImp rule = (SWRLImp)iterator.next();
      if (rule == null) throw new SWRLRuleEngineBridgeException("empty rule");
      importSWRLRule(rule);
    } // while
  } // importSWRLRules

  private void importSWRLRule(SWRLImp imp) throws SWRLRuleEngineBridgeException
  {
    List<Atom> bodyAtoms = new ArrayList<Atom>();
    List<Atom> headAtoms = new ArrayList<Atom>();
    SWRLRule rule;
    Iterator iterator;

    iterator = imp.getBody().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      bodyAtoms.add(processSWRLAtom(swrlAtom, false));
    } // while 

    iterator = imp.getHead().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      headAtoms.add(processSWRLAtom(swrlAtom, true));
    } // while 

    rule = OWLFactory.createSWRLRule(imp.getPrefixedName(), bodyAtoms, headAtoms);

    importedSWRLRules.put(rule.getRuleName(), rule);
  } // importSWRLRule

  private Atom processSWRLAtom(SWRLAtom swrlAtom, boolean isConsequent) throws SWRLRuleEngineBridgeException
  {
    Atom atom = OWLConversionFactory.createAtom(swrlAtom);
    
    if (atom.hasReferencedClasses()) referencedClassNames.addAll(atom.getReferencedClassNames());
    if (atom.hasReferencedProperties()) referencedPropertyNames.addAll(atom.getReferencedPropertyNames());
    if (atom.hasReferencedIndividuals()) referencedIndividualNames.addAll(atom.getReferencedIndividualNames());
    
    return atom;
  } // processSWRLAtom
  
  private void importOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    // TODO: workaround because owlModel.createOWLNamedClass() called in OWLClass does not always return an OWL named class.
    RDFSNamedClass rdfsNamedClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, className);
    if (rdfsNamedClass == null) throw new InvalidClassNameException(className);

    if (rdfsNamedClass.isMetaclass()) return;    
    if (rdfsNamedClass.isAnonymous()) return;    

    if (!importedClasses.containsKey(className)) {
      OWLClass owlClass = OWLConversionFactory.createOWLClass(owlModel, className);
      importedClasses.put(className, owlClass);
      importOWLClassNames(owlClass.getDirectSuperClassNames());
      importOWLClassNames(owlClass.getDirectSubClassNames());
    } // if
  } // importOWLClass

  private void importOWLClassNames(Set<String> classNames) throws SWRLRuleEngineBridgeException
  {
    for (String className : classNames) importOWLClass(className);
  } // importOWLClassNames

  private void importOWLClasses(Set<OWLClass> classes) throws SWRLRuleEngineBridgeException
  {
    for (OWLClass owlClass : classes) importOWLClass(owlClass.getClassName());
  } // importOWLClasses

  private void importAllOWLIndividualsOfClasses(Set<String> classNames) throws SWRLRuleEngineBridgeException
  {
    for (String className : classNames) importAllOWLIndividualsOfClass(className);
  } // importAllOWLIndividualsOfClasses

  private void importAllOWLIndividualsOfClass(String className) throws SWRLRuleEngineBridgeException
  {
    RDFSClass rdfsClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, className);
    if (rdfsClass == null) throw new InvalidClassNameException(className);
    Collection instances = rdfsClass.getInstances(true);
    instances.addAll(rdfsClass.getInferredInstances(true));

    Iterator iterator = instances.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) { 
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
        importOWLIndividual(individual.getName());
      } else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) { // This will be OWL Full
        edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)o;
        importOWLClass(cls.getName());
      } // if
    } // while
  } // importAllOWLIndividualsOfClass
  
  private void importOWLPropertyAssertionAxioms(Set<String> propertyNames) throws SWRLRuleEngineBridgeException
  {
    for (String propertyName : propertyNames) importOWLPropertyAssertionAxioms(propertyName);
  } // importOWLPropertyAssertionAxioms

  private void importOWLPropertyAssertionAxioms(String propertyName) throws SWRLRuleEngineBridgeException
  {
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);

    if (!importedPropertyNames.contains(propertyName)) {
      Set<OWLPropertyAssertionAxiom> axioms = OWLConversionFactory.createOWLPropertyAssertionAxioms(owlModel, propertyName);
      importedAxioms.addAll(axioms);

      for (OWLPropertyAssertionAxiom axiom : axioms) cacheOWLPropertyAssertionAxiom(axiom);

      importedPropertyNames.add(propertyName);

      importOWLClassNames(SWRLOWLUtil.rdfResources2Names(property.getUnionDomain()));
      importOWLClassNames(SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses()));

      importOWLPropertyAssertionAxioms(SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true)));
      importOWLPropertyAssertionAxioms(SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true)));
      importOWLPropertyAssertionAxioms(SWRLOWLUtil.rdfResources2Names(property.getEquivalentProperties()));
    } // if
  } // importOWLPropertyAssertionAxioms

  private void importOWLIndividuals(Set<String> individualNames) throws SWRLRuleEngineBridgeException
  {
    for (String individualName : individualNames) importOWLIndividual(individualName);
  } // importOWLIndividuals

  private void importOWLIndividual(String individualName) throws SWRLRuleEngineBridgeException
  {
    if (!importedIndividuals.containsKey(individualName)) {
      OWLIndividual owlIndividual = OWLConversionFactory.createOWLIndividual(owlModel, individualName);
      importedIndividuals.put(individualName, owlIndividual);
      cacheOWLIndividual(owlIndividual);
      importOWLClasses(owlIndividual.getDefiningClasses());
      importOWLClasses(owlIndividual.getDefiningSuperclasses());
      importOWLClasses(owlIndividual.getDefiningEquivalentClasses());
      importOWLClasses(owlIndividual.getDefiningEquivalentClassSuperclasses());
    } // if
  } // importOWLIndividual

  // We only import rdfs:subClassOf, rdfs:subPropertyOf, owl:sameAs, owl:differentFrom, and owl:allDifferent, owl:equivalentProperty, and
  // owl:equivalentClass axioms at the moment. We support owl:equivalentProperty and owl:equivalentClass axioms indirectly through the
  // OWLIndividual class.
  private void importAxioms() throws SWRLRuleEngineBridgeException
  {
    importClassDescriptions(); // cf. http://www.w3.org/TR/owl-ref, Section  3.1
    importClassAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  3
    importPropertyAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  4 
    importIndividualAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  5
    importDataValuedPropertyAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 6
    importAnnotations(); // cf. http://www.w3.org/TR/owl-ref, Section 7
  } // importAxioms

  private void importClassDescriptions() throws SWRLRuleEngineBridgeException
  {
    importClassEnumerationDescriptions(); // 3.1.1
    importPropertyRestrictions(); // 3.1.2
    importIntersectionOfDescriptions(); // 3.1.3.1
    importUnionOfDescriptions(); // 3.1.3.2
    importComplementOfDescriptions(); // 3.1.3.3
  } // importClassDescriptions

  // cf. http://www.w3.org/TR/owl-ref, Section 3.2
  private void importClassAxioms() throws SWRLRuleEngineBridgeException
  {
    // rdfs:subClassOf
    importEquivalentClassAxioms();
    importDisjointWithAxioms();
  } // importClassAxioms

  // cf. http://www.w3.org/TR/owl-ref, Section 4
  private void importPropertyAxioms() throws SWRLRuleEngineBridgeException
  {
    //importRDFSchemaPropertyAxioms() - rdfs:subPropertyOf, rdfs:domain, rdfs:range
    importEquivalentPropertyAxioms();
    importInverseOfAxioms();
    importFunctionalPropertyAxioms();
    importInverseFunctionalPropertyAxioms();
    importTransitivePropertyAxioms();
    importSymmetricPropertyAxioms();
  } // importPropertyAxioms

  // cf. http://www.w3.org/TR/owl-ref, Section 5
  private void importIndividualAxioms() throws SWRLRuleEngineBridgeException
  {
    importSameAsAxioms();
    importDifferentFromAxioms();
    importAllDifferentsAxioms();
  } // importIndividualAxioms

  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.1
  private void importClassEnumerationDescriptions() throws SWRLRuleEngineBridgeException {}

  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
  private void importPropertyRestrictions() throws SWRLRuleEngineBridgeException
  {
    importCardinalityRestrictions();
    importMinCardinalityRestrictions();
    importMaxCardinalityRestrictions();
    importAllValuesFromRestrictions();
    importSomeValuesFromRestrictions();
    importHasValueRestrictions();
  } // importPropertyRestrictions

  // cf. http://www.w3.org/TR/owl-ref, Section 6
  private void importDataValuedPropertyAxioms() throws SWRLRuleEngineBridgeException {}

  // cf. http://www.w3.org/TR/owl-ref, Section 7
  private void importAnnotations() throws SWRLRuleEngineBridgeException
  {
    // owl:versionInfo, rdfs:label, rdfs:comment, rdfs:seeAlso, rdfs:isDefinedBy 
  } // importAnnotations
  
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
  private void importCardinalityRestrictions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
  private void importMinCardinalityRestrictions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
  private void importMaxCardinalityRestrictions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.1
  private void importAllValuesFromRestrictions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.2
  private void importSomeValuesFromRestrictions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.3
  private void importHasValueRestrictions() throws SWRLRuleEngineBridgeException {}

  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.1
  private void importIntersectionOfDescriptions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.2
  private void importUnionOfDescriptions() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.3
  private void importComplementOfDescriptions() throws SWRLRuleEngineBridgeException {}

  // cf. http://www.w3.org/TR/owl-ref, Section 4.2.1
  private void importEquivalentPropertyAxioms() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 4.2.2
  private void importInverseOfAxioms() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 4.3.1
  private void importFunctionalPropertyAxioms() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 4.3.2
  private void importInverseFunctionalPropertyAxioms() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 4.4.1
  private void importTransitivePropertyAxioms() throws SWRLRuleEngineBridgeException {}
  // cf. http://www.w3.org/TR/owl-ref, Section 4.4.2
  private void importSymmetricPropertyAxioms() throws SWRLRuleEngineBridgeException {}

  private void importEquivalentClassAxioms() throws SWRLRuleEngineBridgeException {}
  private void importDisjointWithAxioms() throws SWRLRuleEngineBridgeException {}

  // TODO: This is incredibly inefficient. Need to use method in the OWLModel to get individuals with a particular property.
  private void importSameAsAxioms() throws SWRLRuleEngineBridgeException
  {
    RDFProperty sameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(owlModel);
    RDFSClass owlThingCls = SWRLOWLUtil.getOWLThingClass(owlModel);

    Iterator individualsIterator1 = owlThingCls.getInstances(true).iterator();
    while (individualsIterator1.hasNext()) {
      Object object1 = individualsIterator1.next();
      if (!(object1 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual1 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object1;
      if (individual1.hasPropertyValue(sameAsProperty)) {
        Collection individuals = (Collection)individual1.getPropertyValues(sameAsProperty);
        Iterator individualsIterator2 = individuals.iterator();
        while (individualsIterator2.hasNext()) {
          Object object2 = individualsIterator2.next();
          if (!(object2 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue;
          edu.stanford.smi.protegex.owl.model.OWLIndividual individual2 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object2;
          importedAxioms.add(OWLFactory.createOWLSameIndividualsAxiom(OWLConversionFactory.createOWLIndividual(individual1), 
                                                                      OWLConversionFactory.createOWLIndividual(individual2)));
        } // while
      } // if
    } // while
  } // importSameAsAxioms

  // TODO: This is incredibly inefficient (and almost duplicates previous method). Need to use method in the OWLModel to get individuals
  // with a particular property.
  private void importDifferentFromAxioms() throws SWRLRuleEngineBridgeException
  {
    RDFProperty differentFromProperty = SWRLOWLUtil.getOWLDifferentFromProperty(owlModel);
    RDFSClass owlThingCls = SWRLOWLUtil.getOWLThingClass(owlModel);

    Iterator individualsIterator1 = owlThingCls.getInstances(true).iterator();
    while (individualsIterator1.hasNext()) {
      Object object1 = individualsIterator1.next();
      if (!(object1 instanceof OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual1 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object1;
      if (individual1.hasPropertyValue(differentFromProperty)) {
        Collection individuals = (Collection)individual1.getPropertyValues(differentFromProperty);
        Iterator individualsIterator2 = individuals.iterator();
        while (individualsIterator2.hasNext()) {
          Object object2 = individualsIterator2.next();
          if (!(object2 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue;
          edu.stanford.smi.protegex.owl.model.OWLIndividual individual2 = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object2;
          importedAxioms.add(OWLFactory.createOWLDifferentIndividualsAxiom(OWLConversionFactory.createOWLIndividual(individual1), 
                                                                           OWLConversionFactory.createOWLIndividual(individual2)));
        } // while
      } // if
    } // while
  } // importDifferentFromAxioms

  private void importAllDifferentsAxioms() throws SWRLRuleEngineBridgeException
  {
    Collection allDifferents = SWRLOWLUtil.getOWLAllDifferents(owlModel);

    if (!allDifferents.isEmpty()) {
      Iterator allDifferentsIterator = allDifferents.iterator();
      while (allDifferentsIterator.hasNext()) {
        OWLAllDifferent owlAllDifferent = (OWLAllDifferent)allDifferentsIterator.next();

        if (owlAllDifferent.getDistinctMembers().size() != 0) {
          OWLDifferentIndividualsAxiom owlDifferentIndividualsAxiom;
          Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
          
          Iterator individualsIterator = owlAllDifferent.getDistinctMembers().iterator();
          while (individualsIterator.hasNext()) {
            RDFIndividual individual = (RDFIndividual)individualsIterator.next();
            if (individual instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) { // Ignore non OWL individuals
              OWLIndividual owlIndividual = OWLConversionFactory.createOWLIndividual((edu.stanford.smi.protegex.owl.model.OWLIndividual)individual);
              individuals.add(owlIndividual);
              cacheOWLIndividual(owlIndividual);
            } // if
          } // while
          owlDifferentIndividualsAxiom = OWLFactory.createOWLDifferentIndividualsAxiom(individuals);
          importedAxioms.add(owlDifferentIndividualsAxiom);
        } // if
        } // while
    } // if
  } // importAllDifferentsAxioms

  private void exportSWRLRules() throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) defineSWRLRule(rule);
  } // exportSWRLRules

  private void exportSWRLRules(Set<String> ruleNames) throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) if (ruleNames.contains(rule.getRuleName())) defineSWRLRule(rule);
  } // exportSWRLRules

  private void exportSWRLRule(String ruleName) throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleNames = new HashSet<String>();
    ruleNames.add(ruleName);
    exportSWRLRules(ruleNames);
  } // exportSWRLRule

  private void exportSWRLRule(SWRLRule rule) throws SWRLRuleEngineBridgeException
  {
    BuiltInAtom builtInAtom;
    BuiltInArgument argument1, argument2;
    List<BuiltInArgument> arguments;
    List<Atom> mappingBuiltInAtoms = new ArrayList<Atom>();

    if (hasMapper()) {
      for (Atom atom: rule.getBodyAtoms()) {
        if (atom instanceof ClassAtom) {
          ClassAtom classAtom = (ClassAtom)atom;
          OWLClass owlClass = OWLFactory.createOWLClass(classAtom.getClassName());
          if (mapper.isMapped(owlClass)) {                        
          } // if
        } else if (atom instanceof DatavaluedPropertyAtom) {
          DatavaluedPropertyAtom datavaluedPropertyAtom = (DatavaluedPropertyAtom)atom;
          OWLDatatypeProperty owlDatatypeProperty = OWLFactory.createOWLDatatypeProperty(datavaluedPropertyAtom.getPropertyName());
          if (mapper.isMapped(owlDatatypeProperty)) {
            arguments = new ArrayList<BuiltInArgument>();
            argument1 = convertAtomArgument2BuiltInArgument(datavaluedPropertyAtom.getArgument1());
            argument2 = convertAtomArgument2BuiltInArgument(datavaluedPropertyAtom.getArgument2());
            arguments.add(owlDatatypeProperty);
            arguments.add(argument2); arguments.add(argument1); //HACK
            builtInAtom = OWLFactory.createBuiltInAtom("http://swrl.stanford.edu/ontologies/built-ins/3.4/ddm.owl#mapOWLDatatypeProperty", 
                                                       "ddm:mapOWLDatatypeProperty", arguments);
            mappingBuiltInAtoms.add(builtInAtom);
          } // if
        } else if (atom instanceof IndividualPropertyAtom) {
          IndividualPropertyAtom individualPropertyAtom = (IndividualPropertyAtom)atom;
          OWLObjectProperty owlObjectProperty = OWLFactory.createOWLObjectProperty(individualPropertyAtom.getPropertyName());
          if (mapper.isMapped(owlObjectProperty)) {
          } // if
        } //if
      } // for
    } // if
    rule.appendAtomsToBody(mappingBuiltInAtoms);

    exportSWRLRule(rule);
  } // exportSWRLRule

  private BuiltInArgument convertAtomArgument2BuiltInArgument(AtomArgument atomArgument) throws SWRLRuleEngineBridgeException
  {
    BuiltInArgument builtInArgument = null;

    if (atomArgument instanceof VariableAtomArgument)
      builtInArgument = OWLFactory.createVariableBuiltInArgument(((VariableAtomArgument)atomArgument).getVariableName(),
                                                                 ((VariableAtomArgument)atomArgument).getPrefixedVariableName());
    else if (atomArgument instanceof IndividualArgument)
      builtInArgument = OWLFactory.createOWLIndividual(((IndividualArgument)atomArgument).getIndividualName());
    else if (atomArgument instanceof DatatypeValueArgument)
      builtInArgument = (OWLDatatypeValue)atomArgument;
    else throw new SWRLRuleEngineBridgeException("cannon convert atom argument '" + atomArgument.getClass() + "' to built-in argument");

    return builtInArgument;
  } // convertAtomArgument2BuiltInArgument2            

  private void exportClasses() throws SWRLRuleEngineBridgeException
  {
    for (OWLClass owlClass : importedClasses.values()) exportClass(owlClass);
  } // exportClasses

  private void exportClass(OWLClass owlClass) throws SWRLRuleEngineBridgeException
  {
    String className = owlClass.getClassName();
    Set<String> superClassNames = owlClass.getDirectSuperClassNames();

    if (!exportedClassNames.contains(className)) { // See if it is already defined.
      exportOWLClass(owlClass);
      exportedClassNames.add(className);

      if (!superClassNames.isEmpty()) { // Superclasses must be defined before subclasses.
        for (String superClassName : superClassNames) {
          OWLClass superOWLClass = importedClasses.get(superClassName);
          exportClass(superOWLClass); 
        } // for
      } // if
    } // if
  } // exportClass

  private void exportIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : importedIndividuals.values()) {
      String individualName = owlIndividual.getIndividualName();
      if (!exportedIndividualNames.contains(individualName)) {
        exportOWLIndividual(owlIndividual);
        exportedIndividualNames.add(individualName);
      } // if
    } // for
  } // exportIndividuals

  private void exportAxioms() throws SWRLRuleEngineBridgeException
  {
    for (OWLAxiom  axiom: importedAxioms) exportOWLAxiom(axiom);
  } // exportAxioms

  private void writeInferredIndividuals2OWL() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : inferredIndividuals.values()) OWLConversionFactory.write2OWLModel(owlIndividual, owlModel);
  } // writeInferredIndividuals2OWL

  private void writeInferredAxioms2OWL() throws SWRLRuleEngineBridgeException
  {
    for (OWLAxiom axiom : inferredAxioms) OWLConversionFactory.write2OWLModel(axiom, owlModel);
  } // writeInferredAxioms2OWL
  
  private void initialize()
  {
    importedSWRLRules = new HashMap<String, SWRLRule>();

    referencedClassNames = new HashSet<String>();
    referencedIndividualNames = new HashSet<String>();
    referencedPropertyNames = new HashSet<String>();

    importedClasses = new HashMap<String, OWLClass>();
    importedIndividuals = new HashMap<String, OWLIndividual>(); 
    importedAxioms = new HashSet<OWLAxiom>(); 
    importedPropertyNames = new HashSet<String>();
    importedAxioms = new HashSet<OWLAxiom>();

    exportedClassNames = new HashSet<String>();
    exportedIndividualNames = new HashSet<String>();

    inferredIndividuals = new HashMap<String, OWLIndividual>(); 
    inferredAxioms = new HashSet<OWLAxiom>(); 

    injectedClasses = new HashMap<String, OWLClass>();
    injectedAnonymousClasses = new HashMap<String, OWLClass>();
    injectedIndividuals = new HashMap<String, OWLIndividual>();
    injectedAxioms = new HashSet<OWLAxiom>();

    bridgeOWLPropertyAssertionAxioms = new HashMap<String, Map<String, Set<OWLPropertyAssertionAxiom>>>();
    bridgeOWLIndividuals = new HashMap<String, OWLIndividual>();
  } // initialize  

  private void checkOWLClassName(String className) throws SWRLBuiltInBridgeException
  {
    if (!SWRLOWLUtil.isValidURI(className)) 
      throw new SWRLBuiltInBridgeException("attempt to inject class with invalid name '" + className + "'");
  } // checkOWLClassName

  private void cacheOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiom axiom)
  {
    String subjectName = axiom.getSubject().getIndividualName();
    String propertyName = axiom.getProperty().getPropertyName();
    Map<String, Set<OWLPropertyAssertionAxiom>> propertyAxiomsMap;
    Set<OWLPropertyAssertionAxiom> axiomSet;

    if (bridgeOWLPropertyAssertionAxioms.containsKey(subjectName)) 
      propertyAxiomsMap = bridgeOWLPropertyAssertionAxioms.get(subjectName);
    else {
      propertyAxiomsMap = new HashMap<String, Set<OWLPropertyAssertionAxiom>>();
      bridgeOWLPropertyAssertionAxioms.put(subjectName, propertyAxiomsMap);
    } // if

    if (propertyAxiomsMap.containsKey(propertyName)) axiomSet = propertyAxiomsMap.get(propertyName);
    else {
      axiomSet = new HashSet<OWLPropertyAssertionAxiom>();
      propertyAxiomsMap.put(propertyName, axiomSet);
    } // if
    
    axiomSet.add(axiom);     
  } // cacheOWLPropertyAssertionAxiom

  private void cacheOWLIndividual(OWLIndividual owlIndividual)
  {
    String individualName = owlIndividual.getIndividualName();

    if (!bridgeOWLIndividuals.containsKey(individualName)) bridgeOWLIndividuals.put(individualName, owlIndividual);
  } // cacheOWLIndividual  

  private void exportOWLClass(OWLClass owlClass) throws SWRLBuiltInBridgeException
  {
    try {
      defineOWLClass(owlClass);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new SWRLBuiltInBridgeException("error exporting OWL class '" + owlClass.getPrefixedClassName() + "'");
    } // try
  } // exportOWLClass

  private void exportOWLIndividual(OWLIndividual owlIndividual) throws SWRLBuiltInBridgeException
  {
    try {
      defineOWLIndividual(owlIndividual);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new SWRLBuiltInBridgeException("error exporting OWL individual '" + owlIndividual.getPrefixedIndividualName() + "'");
    } // try
  } // exportOWLIndividual

  private void exportOWLAxiom(OWLAxiom owlAxiom) throws SWRLBuiltInBridgeException
  {
    try {
      defineOWLAxiom(owlAxiom);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new SWRLBuiltInBridgeException("error exporting OWL axiom '" + owlAxiom + "'");
    } // try
  } // exportOWLAxiom

  private void exportBuiltInBinding(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) 
    throws BuiltInException
  {
    try {
      defineBuiltInBinding(ruleName, builtInName, builtInIndex, arguments);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error exporting built-in binding: " + e.getMessage());
    } // try
  } // exportBuiltInBinding

  private void generateBuiltInBindings(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments)
    throws BuiltInException
  {
    List<Integer> multiArgumentIndexes = getMultiArgumentIndexes(arguments);
    
    if (multiArgumentIndexes.isEmpty()) 
      exportBuiltInBinding(ruleName, builtInName, builtInIndex, arguments); // No multi-arguments - do a simple bind
    else {
      List<Integer> multiArgumentCounts = new ArrayList<Integer>();
      List<Integer> multiArgumentSizes = new ArrayList<Integer>();
      List<BuiltInArgument> argumentsPattern;

      for (int i = 0; i < multiArgumentIndexes.size(); i++) multiArgumentCounts.add(Integer.valueOf(0));
      for (int i = 0; i < multiArgumentIndexes.size(); i++) {
        MultiArgument multiArgument = (MultiArgument)arguments.get(multiArgumentIndexes.get(i).intValue());
        multiArgumentSizes.add(Integer.valueOf(multiArgument.getNumberOfArguments()));
      } // for

      do {
        argumentsPattern = generateArgumentsPattern(arguments, multiArgumentCounts);
        exportBuiltInBinding(ruleName, builtInName, builtInIndex, argumentsPattern); 
      } while (!nextMultiArgumentCounts(multiArgumentCounts, multiArgumentSizes));
    } // if
  } // generateBuiltInBindings

  private List<Integer> getMultiArgumentIndexes(List<BuiltInArgument> arguments)
  {
    List<Integer> result = new ArrayList<Integer>();

    for (int i = 0; i < arguments.size(); i++) 
      if (arguments.get(i) instanceof MultiArgument) result.add(Integer.valueOf(i));

    return result;
  } // getMultiArgumentIndexes

  private static boolean nextMultiArgumentCounts(List<Integer> multiArgumentCounts, List<Integer> multiArgumentSizes)
    throws BuiltInException
  {
    if (multiArgumentSizes.isEmpty()) return true;
    
    if (nextMultiArgumentCounts(multiArgumentCounts.subList(1, multiArgumentCounts.size()), 
                                multiArgumentSizes.subList(1, multiArgumentSizes.size()))) {
      // No more permutations of rest of list so increment this count and if we are not at the end set rest of the list to begin at 0 again.
      int count = multiArgumentCounts.get(0).intValue();
      int size = multiArgumentSizes.get(0).intValue();
      
      if (++count == size) return true;

      multiArgumentCounts.set(0, Integer.valueOf(count));

      for (int i = 1; i < multiArgumentCounts.size(); i++) multiArgumentCounts.set(i, Integer.valueOf(0));
    } // if
    return false;
  } // nextMultiArgumentCounts

  private List<BuiltInArgument> generateArgumentsPattern(List<BuiltInArgument> arguments, List<Integer> multiArgumentCounts)
  {
    List<BuiltInArgument> result = new ArrayList<BuiltInArgument>();
    int multiArgumentIndex = 0;

    for (BuiltInArgument argument: arguments) {
      if (argument instanceof MultiArgument) {
        MultiArgument multiArgument = (MultiArgument)argument;
        result.add(multiArgument.getArguments().get((multiArgumentCounts.get(multiArgumentIndex).intValue())));
        multiArgumentIndex++;
      } else result.add(argument);
    } // for

    return result;
  } // generateArgumentsPattern  

} // AbstractSWRLRuleEngineBridge
