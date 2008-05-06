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

import edu.stanford.smi.protegex.owl.swrl.util.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;

import java.util.*;
import java.io.*;

/**
 ** This class provides an implementation of some of the core functionality required by a SWRL rule engine bridge. Implementations for a
 ** target rule engine should subclass this class. Detailed documentation for this process can be found <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public abstract class AbstractSWRLRuleEngineBridge implements SWRLRuleEngineBridge
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
  private Set<OWLPropertyAssertionAxiom> importedPropertyAssertionAxioms; 
  private Set<OWLAxiom> importedAxioms;

  // Names of classes, properties, and individuals that have been exported to target rule engine
  private Set<String> exportedClassNames, exportedIndividualNames; 

  // Inferred individuals and property assertion axioms
  private Set<OWLIndividual> inferredIndividuals;
  private Set<OWLPropertyAssertionAxiom> inferredPropertyAssertionAxioms; 

  // Created individuals
  private HashMap<String, OWLIndividual> createdIndividuals;
  private HashMap<String, OWLClass> createdClasses;
  private Set<OWLPropertyAssertionAxiom> createdPropertyAssertionAxioms;

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
  public void importSWRLRulesAndOWLKnowledge(String ruleGroupName) throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleGroupNames = new HashSet<String>();
    ruleGroupNames.add(ruleGroupName);
    importSWRLRulesAndOWLKnowledge(ruleGroupNames);
  } // importSWRLRulesAndOWLKnowledge

  /**
   ** Load rules from all the named rule groups and associated knowledge from OWL into bridge. All existing bridge rules and knowledge will
   ** first be cleared and the associated rule engine will be reset.
   */
  public void importSWRLRulesAndOWLKnowledge(Set<String> ruleGroupNames) throws SWRLRuleEngineBridgeException
  {
    if (SWRLOWLUtil.hasInconsistentClasses(owlModel))
      throw new InconsistentKnowledgeBaseException("cannot import rules from an inconsistent ontology");

    importSWRLRules(ruleGroupNames); // Fills in importedSWRLRules, referencedClassNames, referencedPropertyNames, and referencedIndividualNames

    importOWLClassNames(referencedClassNames); // Import all referenced classes (and their superclasses and subclasses).
    importOWLPropertyAssertionAxioms(referencedPropertyNames); // Import all referenced properties (and the necessary classes).
    importOWLIndividuals(referencedIndividualNames); // Import all referenced individuals (and their classes).

    importAllOWLIndividualsOfClasses(referencedClassNames); // Import all individuals that are members of imported classes.

    importAxioms();

    exportSWRLRulesAndOWLKnowledge();
  } // importSWRLRulesAndOWLKnowledge

  /**
   ** Send rules and knowledge stored in bridge to a rule engine.
   */
  private void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportClasses(); // Classes should be exported before rules because rules usually use class definitions.
    exportSWRLRules();
    exportIndividuals();
    exportPropertyAssertionAxioms();
    exportAxioms();
  } // exportSWRLRulesAndOWLKnowledge

  /**
   ** Send knowledge (excluding SWRL rules) stored in bridge to a rule engine.
   */
  private void exportOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportClasses();
    exportIndividuals();
    exportPropertyAssertionAxioms();
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
    writeCreatedClasses();
    writeCreatedIndividuals(); // Create any OWL individuals generated by built-ins in rules. 
    //    writeCreatedPropertyAssertionAxioms(); // Create any OWL property assertion axioms generated by built-ins in rules. 

    writeInferredIndividuals2OWL();
    writeInferredPropertyAssertionAxioms2OWL();
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

  public SWRLRule getRule(String ruleName) throws InvalidRuleNameException
  {
    if (!importedSWRLRules.containsKey(ruleName)) throw new InvalidRuleNameException(ruleName);

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
  public int getNumberOfImportedPropertyAssertionAxioms() { return importedPropertyAssertionAxioms.size(); }
  public int getNumberOfImportedAxioms() { return importedAxioms.size(); }
  public int getNumberOfInferredIndividuals() { return inferredIndividuals.size(); }
  public int getNumberOfInferredPropertyAssertionAxioms() { return inferredPropertyAssertionAxioms.size(); }
  public int getNumberOfCreatedClasses() { return createdClasses.size(); }
  public int getNumberOfCreatedIndividuals() { return createdIndividuals.size(); }
  public int getNumberOfCreatedPropertyAssertionAxioms() { return createdPropertyAssertionAxioms.size(); }

  public boolean isClass(String className) { return importedClasses.containsKey(className); }
  public boolean isProperty(String propertyName) { return importedPropertyNames.contains(propertyName); }
  public boolean isIndividual(String individualName) { return importedIndividuals.containsKey(individualName); }

  public boolean isCreatedClass(String className) { return createdClasses.containsKey(className); }
  public boolean isCreatedIndividual(String individualName) { return createdIndividuals.containsKey(individualName); }
  public boolean isCreatedPropertyAssertionAxiom(OWLPropertyAssertionAxiom axiom) { return createdPropertyAssertionAxioms.contains(axiom); }

  // Convenience methods to display the contents of the bridge
  public Set<SWRLRule> getImportedSWRLRules() { return new HashSet<SWRLRule>(importedSWRLRules.values()); }
  public Set<OWLClass> getImportedClasses() { return new HashSet<OWLClass>(importedClasses.values()); }
  public Set<OWLIndividual> getImportedIndividuals() { return new HashSet<OWLIndividual>(importedIndividuals.values()); }
  public Set<OWLPropertyAssertionAxiom> getImportedPropertyAssertionAxioms() { return importedPropertyAssertionAxioms; }
  public Set<OWLAxiom> getImportedAxioms() { return importedAxioms; }
  public Set<OWLIndividual> getInferredIndividuals() { return inferredIndividuals; }
  public Set<OWLPropertyAssertionAxiom> getInferredPropertyAssertionAxioms() { return inferredPropertyAssertionAxioms; }
  public Set<OWLClass> getCreatedClasses() { return new HashSet<OWLClass>(createdClasses.values()); }
  public Set<OWLIndividual> getCreatedIndividuals() { return new HashSet<OWLIndividual>(createdIndividuals.values()); }
  public Set<OWLPropertyAssertionAxiom> getCreatedPropertyAssertionAxioms() { return createdPropertyAssertionAxioms; }

  /**
   ** Infer an OWL property assertion axiom from a rule engine
   */
  public void inferPropertyAssertionAxiom(OWLPropertyAssertionAxiom owlPropertyAssertionAxiom) throws SWRLRuleEngineBridgeException
  { 
    if (!inferredPropertyAssertionAxioms.contains(owlPropertyAssertionAxiom)) inferredPropertyAssertionAxioms.add(owlPropertyAssertionAxiom); 
  } // inferPropertyAssertionAxiom

  /**
   ** Assert an OWL individual from a rule engine
   */
  public void inferIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException 
  {
    if (!inferredIndividuals.contains(owlIndividual)) inferredIndividuals.add(owlIndividual); 
  } // inferIndividual

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
      SWRLBuiltInUtil.generateBuiltInBindings(this, ruleName, builtInName, builtInIndex, arguments); // Generate all possible bindings.
    } // if

    return result;
  } // invokeSWRLBuiltIn

  public void setMapper(Mapper mapper) 
  { 
    this.mapper = mapper; 
  } // setMapper

  public boolean hasMapper() { return mapper != null; }
  public Mapper getMapper() { return mapper; }
  
  /**
   ** Method used to create a bridge individual of type owl:Thing. This method will typically be invoked from within a built-in. An OWL
   ** individual is not created at this point - instead an object is generated for the individual in the bridge and the individual is
   ** exported to the rule engine. The individual is given a unique name that can be used later if an OWL individual is created for it.
   */

  public OWLIndividual createOWLIndividual() throws SWRLRuleEngineBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLCreated");
    String prefix = owlModel.getPrefixForResourceName(individualName);
    String localName = owlModel.getLocalNameForURI(individualName);
    String prefixedIndividualName = prefix.equals("") ? localName : prefix + ":" + localName;
    OWLClass owlClass = OWLFactory.createOWLClass(edu.stanford.smi.protegex.owl.model.OWLNames.Cls.THING);
    OWLIndividual owlIndividual = OWLFactory.generateOWLIndividual(individualName, prefixedIndividualName, owlClass);
    createOWLIndividual(owlIndividual);
    return owlIndividual;
  } // createOWLIndividual

  public void createOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    if (!createdClasses.containsKey(className)) {
      OWLClass owlClass = OWLFactory.createOWLClass(className);
      createdClasses.put(className, owlClass);
      defineClass(owlClass); // Export the individual to the rule engine
    } // if
  } // createOWLClass

  public void createOWLClass(String className, String superclassName) throws SWRLRuleEngineBridgeException
  {
    if (!createdClasses.containsKey(className)) {
      OWLClass owlClass = OWLFactory.createOWLClass(className, superclassName);
      createdClasses.put(className, owlClass);
      defineClass(owlClass); // Export the individual to the rule engine
    } // if
  } // createOWLClass
    
  public void createOWLIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException
  {
    if (!createdIndividuals.containsKey(owlIndividual.getIndividualName())) {
      createdIndividuals.put(owlIndividual.getIndividualName(), owlIndividual); 
      defineIndividual(owlIndividual); // Export the individual to the rule engine.
    } // if
  } // createOWLIndividual

  public OWLIndividual createOWLIndividual(OWLClass owlClass) throws SWRLRuleEngineBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLCreated");
    String prefix = owlModel.getPrefixForResourceName(individualName);
    String localName = owlModel.getLocalNameForURI(individualName);
    String prefixedIndividualName = prefix.equals("") ? localName : prefix + ":" + localName;
    OWLIndividual owlIndividual = OWLFactory.generateOWLIndividual(individualName, prefixedIndividualName, owlClass);

    if (!importedClasses.containsKey(owlClass.getClassName())) defineClass(owlClass);
   
    createdIndividuals.put(individualName, owlIndividual); 
    defineIndividual(owlIndividual); // Export the individual to the rule engine.

    return owlIndividual;
  } // createOWLIndividual

  public void createOWLIndividuals(Set<OWLIndividual> individuals) throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : individuals) createOWLIndividual(owlIndividual);
  } // createOWLIndividuals

  public OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property,
                                                                                   OWLDatatypeValue object) 
    throws SWRLRuleEngineBridgeException
  {
    OWLDatatypePropertyAssertionAxiom axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subject, property, object);
    createOWLDatatypePropertyAssertionAxiom(axiom);
    return axiom;
  } // createOWLDatatypePropertyAssertionAxiom

  public OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLDatatypePropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException
  {
    if (!createdPropertyAssertionAxioms.contains(axiom)) createdPropertyAssertionAxioms.add(axiom);

    defineAxiom(axiom); // Export the axiom to the rule engine.

    return axiom;
  } // createOWLDatatypePropertyAssertionAxiom

  public void createOWLDatatypePropertyAssertionAxioms(Set<OWLDatatypePropertyAssertionAxiom> axioms)
    throws SWRLRuleEngineBridgeException
  {
    for (OWLDatatypePropertyAssertionAxiom axiom : axioms) createOWLDatatypePropertyAssertionAxiom(axiom);
  } // createOWLDatatypePropertyAssertionAxioms

  public OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object) 
    throws SWRLRuleEngineBridgeException
  {
    OWLObjectPropertyAssertionAxiom axiom = OWLFactory.createOWLObjectPropertyAssertionAxiom(subject, property, object);
    createOWLObjectPropertyAssertionAxiom(axiom);
    return axiom;
  } // createOWLObjectPropertyAssertionAxiom

  public OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException
  {
    if (!createdPropertyAssertionAxioms.contains(axiom)) createdPropertyAssertionAxioms.add(axiom);

    defineAxiom(axiom); // Export the axiom to the rule engine.

    return axiom;
  } // createOWLObjectPropertyAssertionAxiom

 public void createOWLObjectPropertyAssertionAxioms(Set<OWLObjectPropertyAssertionAxiom> axioms)
    throws SWRLRuleEngineBridgeException
  {
    for (OWLObjectPropertyAssertionAxiom axiom : axioms) createOWLObjectPropertyAssertionAxiom(axiom);  
  } // createOWLObjectPropertyAssertionAxioms

  /**
   ** Create OWL classes in model for the classes created by built-ins during rule execution.
   */
  private void writeCreatedClasses() throws SWRLRuleEngineBridgeException
  {
    for (OWLClass owlClass: createdClasses.values()) {
      String className = owlClass.getClassName();
      
      try {
        if (SWRLOWLUtil.isClass(owlModel, className)) continue; // We have already created it.
        
        edu.stanford.smi.protegex.owl.model.OWLClass cls = SWRLOWLUtil.createOWLNamedClass(owlModel, className);
        
        for (String superclassName : owlClass.getSuperclassNames()) {
          edu.stanford.smi.protegex.owl.model.OWLClass superclass = owlModel.getOWLNamedClass(superclassName);
          if (superclass == null) 
            throw new SWRLRuleEngineBridgeException("cannot create OWL class '" + className + "' because superclass '" + superclassName + "' is missing");
          cls.addSuperclass(superclass);
        } // for
      } catch (SWRLOWLUtilException e) {
        throw new SWRLRuleEngineBridgeException("cannot create OWL class '" + className + "': " + e.getMessage());
      } // try
    } // for
    
  } // writeCreatedClasses

  /**
   ** Create OWL individuals in model for the individuals created by built-ins during rule execution.
   */
  private void writeCreatedIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual: createdIndividuals.values()) {
      String individualName = owlIndividual.getIndividualName();
      if (SWRLOWLUtil.isOWLIndividual(owlModel, individualName)) continue; // We have already created it.
      try {
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual 
          = SWRLOWLUtil.createIndividualOfClass(owlModel, SWRLOWLUtil.getOWLThingClass(owlModel), individualName);
      } catch (SWRLOWLUtilException e) {
        throw new SWRLRuleEngineBridgeException("cannot create OWL individual '" + individualName + "': " + e.getMessage());
      } // try
    } // for
  } // writeCreatedIndividuals

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
    Atom atom;
    
    if (swrlAtom instanceof SWRLClassAtom) {
      atom = OWLFactory.createClassAtom((SWRLClassAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {
      atom = OWLFactory.createDatavaluedPropertyAtom(owlModel, (SWRLDatavaluedPropertyAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {
      atom = OWLFactory.createIndividualPropertyAtom((SWRLIndividualPropertyAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLSameIndividualAtom) {
      atom = OWLFactory.createSameIndividualAtom((SWRLSameIndividualAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) {
      atom = OWLFactory.createDifferentIndividualsAtom((SWRLDifferentIndividualsAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLBuiltinAtom) {
      atom = OWLFactory.createBuiltInAtom(owlModel, (SWRLBuiltinAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDataRangeAtom) 
      atom = OWLFactory.createDataRangeAtom((SWRLDataRangeAtom)swrlAtom);
    else throw new InvalidSWRLAtomException(swrlAtom.getBrowserText());

    if (atom.hasReferencedClasses()) referencedClassNames.addAll(atom.getReferencedClassNames());
    if (atom.hasReferencedProperties()) referencedPropertyNames.addAll(atom.getReferencedPropertyNames());
    if (atom.hasReferencedIndividuals()) referencedIndividualNames.addAll(atom.getReferencedIndividualNames());
    
    return atom;
  } // processSWRLAtom
  
  private void importOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    // TODO: workaround becuase owlModel.createOWLNamedClass() called in OWLClass does not always return an OWL named class.
    RDFSNamedClass rdfsNamedClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, className);
    if (rdfsNamedClass == null) throw new InvalidClassNameException(className);

    if (rdfsNamedClass.isMetaclass()) return;    
    if (rdfsNamedClass.isAnonymous()) return;    

    if (!importedClasses.containsKey(className)) {
      OWLClass owlClass = OWLFactory.createOWLClass(owlModel, className);
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
      Set<OWLPropertyAssertionAxiom> axioms = OWLPropertyImpl.buildOWLPropertyAssertionAxioms(owlModel, propertyName);
      importedPropertyAssertionAxioms.addAll(axioms);
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
      OWLIndividual owlIndividual = OWLFactory.createOWLIndividual(owlModel, individualName);
      importedIndividuals.put(individualName, owlIndividual);
      importOWLClasses(owlIndividual.getDefiningClasses());
      importOWLClasses(owlIndividual.getDefiningSuperclasses());
      importOWLClasses(owlIndividual.getDefiningEquivalentClasses());
      importOWLClasses(owlIndividual.getDefiningEquivalentClassSuperclasses());
    } // if
  } // importOWLIndividual

  // We only import owl:sameAs, owl:differentFrom, and owl:allDifferent, owl:equivalentProperty, and owl:equivalentClass axioms at the
  // moment. We support owl:equivalentProperty and owl:equivalentClass axioms indirectly through the OWLIndividual class.
  private void importAxioms() throws SWRLRuleEngineBridgeException
  {
    importClassDescriptions(); // cf. http://www.w3.org/TR/owl-ref, Section  3.1
    importClassAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  3
    importPropertyAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  4 
    importIndividualAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section  5
    importDatatypeAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 6
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

  // cf. http://www.w3.org/TR/owl-ref, Section 4
  private void importPropertyAxioms() throws SWRLRuleEngineBridgeException
  {
    //importRDFSchemaPropertyAxioms();
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

  // cf. http://www.w3.org/TR/owl-ref, Section 6
  private void importDatatypeAxioms() throws SWRLRuleEngineBridgeException {}

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
          importedAxioms.add(OWLFactory.createOWLSameIndividualsAxiom(OWLFactory.createOWLIndividual(individual1), 
                                                                      OWLFactory.createOWLIndividual(individual2)));
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
          importedAxioms.add(OWLFactory.createOWLDifferentIndividualsAxiom(OWLFactory.createOWLIndividual(individual1), 
                                                                           OWLFactory.createOWLIndividual(individual2)));
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
            if (individual instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) // Ignore non OWL individuals
              individuals.add(OWLFactory.createOWLIndividual((edu.stanford.smi.protegex.owl.model.OWLIndividual)individual));
          } // while
          owlDifferentIndividualsAxiom = OWLFactory.createOWLDifferentIndividualsAxiom(individuals);
          importedAxioms.add(owlDifferentIndividualsAxiom);
        } // if
        } // while
    } // if
  } // importAllDifferentsAxioms

  private void exportSWRLRules() throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) exportSWRLRule(rule);
  } // exportSWRLRules

  private void exportSWRLRules(Set<String> ruleNames) throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) if (ruleNames.contains(rule.getRuleName())) exportSWRLRule(rule);
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
    defineRule(rule);
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
      defineClass(owlClass);
      exportedClassNames.add(className);

      if (!superClassNames.isEmpty()) { // Superclasses must be defined before subclasses.
        for (String superClassName : superClassNames) {
          OWLClass superOWLClass = importedClasses.get(superClassName);
          exportClass(superOWLClass); 
        } // for
      } // if
    } // if
  } // exportClass

  private void exportPropertyAssertionAxioms() throws SWRLRuleEngineBridgeException
  {
    for (OWLPropertyAssertionAxiom axiom : importedPropertyAssertionAxioms) defineAxiom(axiom);
  } // exportProperties

  private void exportIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : importedIndividuals.values()) {
      String individualName = owlIndividual.getIndividualName();
      if (exportedIndividualNames.contains(individualName)) continue;
      defineIndividual(owlIndividual);
      exportedIndividualNames.add(individualName);
    } // for
  } // exportIndividuals

  private void exportAxioms() throws SWRLRuleEngineBridgeException
  {
    for (OWLAxiom  axiom: importedAxioms) defineAxiom(axiom);
  } // exportAxioms

  private void writeInferredPropertyAssertionAxioms2OWL() throws SWRLRuleEngineBridgeException
  {
    for (OWLPropertyAssertionAxiom owlPropertyAssertionAxiom : inferredPropertyAssertionAxioms) owlPropertyAssertionAxiom.write2OWL(owlModel);
  } // writeInferredProperties2OWL

  private void writeInferredIndividuals2OWL() throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : inferredIndividuals) owlIndividual.write2OWL(owlModel);
  } // writeInferredIndividuals2OWL
  
  private void initialize()
  {
    importedSWRLRules = new HashMap<String, SWRLRule>();

    referencedClassNames = new HashSet<String>();
    referencedIndividualNames = new HashSet<String>();
    referencedPropertyNames = new HashSet<String>();

    importedClasses = new HashMap<String, OWLClass>();
    importedIndividuals = new HashMap<String, OWLIndividual>(); 
    importedPropertyAssertionAxioms = new HashSet<OWLPropertyAssertionAxiom>(); 
    importedPropertyNames = new HashSet<String>();
    importedAxioms = new HashSet<OWLAxiom>();

    exportedClassNames = new HashSet<String>();
    exportedIndividualNames = new HashSet<String>();

    inferredIndividuals = new HashSet<OWLIndividual>(); 
    inferredPropertyAssertionAxioms = new HashSet<OWLPropertyAssertionAxiom>(); 

    createdClasses = new HashMap<String, OWLClass>();
    createdIndividuals = new HashMap<String, OWLIndividual>();
    createdPropertyAssertionAxioms = new HashSet<OWLPropertyAssertionAxiom>();
  } // initialize  

} // AbstractSWRLRuleEngineBridge
