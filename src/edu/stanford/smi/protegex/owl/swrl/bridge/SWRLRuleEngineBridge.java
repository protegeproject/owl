
// TODO: DataRange
// TODO: remove all Protege-OWL specific code

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.*;

import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;

import edu.stanford.smi.protegex.owl.swrl.ormap.Mapper;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;

import java.util.*;
import java.io.*;

/**
 ** The SWRL Rule Engine Bridge provides a mechanism to incorporate rule engines into Protege-OWL to execute SWRL rules. <p>
 **
 ** Detailed documentation for this class can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public abstract class SWRLRuleEngineBridge implements SWRLRuleEngine, SQWRLQueryEngine, Serializable
{
  protected abstract void defineRule(SWRLRule rule) throws SWRLRuleEngineBridgeException;
  protected abstract void defineClass(OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  protected abstract void defineIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  protected abstract void defineAxiom(OWLAxiom axiom) throws SWRLRuleEngineBridgeException;
  protected abstract void initializeRuleEngine() throws SWRLRuleEngineBridgeException;
  protected abstract void generateBuiltInBinding(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) 
    throws BuiltInException;

  public abstract void runRuleEngine() throws SWRLRuleEngineBridgeException;

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

  // Names of classes, properties, and individuals that have been exported to target
  private Set<String> exportedClassNames, exportedIndividualNames; 

  // Inferred individuals and property assertion axioms
  private Set<OWLIndividual> inferredIndividuals;
  private Set<OWLPropertyAssertionAxiom> inferredPropertyAssertionAxioms; 

  // Created individuals
  private HashMap<String, OWLIndividual> createdIndividuals;
  private Set<OWLPropertyAssertionAxiom> createdPropertyAssertionAxioms;

  // Mapper
  private Mapper mapper = null;

  protected SWRLRuleEngineBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    this.owlModel = owlModel;
    initialize();
    BuiltInLibraryManager.invokeAllBuiltInLibrariesResetMethod(this);
  } // SWRLRuleEngineBridge

  /**
   ** Load rules and knowledge from OWL into bridge, send them to a rule engine, run the rule engine, and write any inferred knowledge back
   ** to OWL.
   */
  public void infer() throws SWRLRuleEngineBridgeException
  {
    resetBridge();
    importSWRLRulesAndOWLKnowledge();
    exportSWRLRulesAndOWLKnowledge();
    runRuleEngine();
    writeInferredKnowledge2OWL();
  } // infer

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
    resetRuleEngine();

    if (SWRLOWLUtil.hasInconsistentClasses(owlModel))
      throw new InconsistentKnowledgeBaseException("cannot import rules from an inconsistent knowledge base");

    importSWRLRules(ruleGroupNames); // Fills in importedSWRLRules, referencedClassNames, referencedPropertyNames, and referencedIndividualNames

    importOWLClasses(referencedClassNames); // Import all referenced classes (and their superclasses and subclasses).
    importOWLPropertyAssertionAxioms(referencedPropertyNames); // Import all referenced properties (and the necessary classes).
    importOWLIndividuals(referencedIndividualNames); // Import all referenced individuals (and their classes).

    importAllOWLIndividualsOfClasses(referencedClassNames); // Import all individuals that are members of imported classes.

    importAxioms();
  } // importSWRLRulesAndOWLKnowledge

  /**
   ** Send rules and knowledge stored in bridge to a rule engine.
   */
  public void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
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
  public void exportOWLKnowledge() throws SWRLRuleEngineBridgeException
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
    runRuleEngine();
  } // run

  /**
   ** Write knowledge inferred by rule engine back to OWL.
   */
  public void writeInferredKnowledge2OWL() throws SWRLRuleEngineBridgeException
  {
    writeCreatedIndividuals(); // Create any OWL individuals generated by built-ins in rules. 
    //    writeCreatedPropertyAssertionAxioms(); // Create any OWL property assertion axioms generated by built-ins in rules. 

    writeInferredIndividuals2OWL();
    writeInferredPropertyAssertionAxioms2OWL();
  } // writeInferredKnowledge2OWL

  /**
   ** Clear all knowledge from bridge.
   */
  public void resetBridge() throws SWRLRuleEngineBridgeException
  {
    resetRuleEngine();

    BuiltInLibraryManager.invokeAllBuiltInLibrariesResetMethod(this);

    importedSWRLRules.clear();
    referencedClassNames.clear();
    referencedPropertyNames.clear();
    referencedIndividualNames.clear();
    importedClasses.clear();
    importedPropertyAssertionAxioms.clear();
    importedPropertyNames.clear();
    importedIndividuals.clear();
    importedAxioms.clear();
    createdIndividuals.clear();
    createdPropertyAssertionAxioms.clear();
    clearExportedAndInferredKnowledge();
  } // resetBridge

  /**
   **  Clear all knowledge from rule engine, deleted inferred knowledge from the bridge, and leave imported bridge knowledge intact.
   */
  public void resetRuleEngine() throws SWRLRuleEngineBridgeException
  {
    initializeRuleEngine();
    clearExportedAndInferredKnowledge();
  } // resetRuleEngine

  /**
   **  Get the results from a SQWRL query.
   */
  public Result getSQWRLResult(String ruleName) throws ResultException
  {
    ResultImpl result = getRule(ruleName).getSQWRLResult();

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
  public int getNumberOfCreatedIndividuals() { return createdIndividuals.size(); }
  public int getNumberOfCreatedPropertyAssertionAxioms() { return createdPropertyAssertionAxioms.size(); }

  public boolean isClass(String className) { return importedClasses.containsKey(className); }
  public boolean isProperty(String propertyName) { return importedPropertyNames.contains(propertyName); }
  public boolean isIndividual(String individualName) { return importedIndividuals.containsKey(individualName); }

  public boolean isCreatedIndividual(String individualName) { return createdIndividuals.containsKey(individualName); }
  public boolean isCreatedPropertyAssertionAxiom(OWLPropertyAssertionAxiom axiom) { return createdPropertyAssertionAxioms.contains(axiom); }

  // Convenience methods to display the contents of the bridge, inlcuding possibly inferred knowledge
  public Set<SWRLRule> getImportedSWRLRules() { return new HashSet<SWRLRule>(importedSWRLRules.values()); }
  public Set<OWLClass> getImportedClasses() { return new HashSet<OWLClass>(importedClasses.values()); }
  public Set<OWLIndividual> getImportedIndividuals() { return new HashSet<OWLIndividual>(importedIndividuals.values()); }
  public Set<OWLPropertyAssertionAxiom> getImportedPropertyAssertionAxioms() { return importedPropertyAssertionAxioms; }
  public Set<OWLAxiom> getImportedAxioms() { return importedAxioms; }
  public Set<OWLIndividual> getInferredIndividuals() { return inferredIndividuals; }
  public Set<OWLPropertyAssertionAxiom> getInferredPropertyAssertionAxioms() { return inferredPropertyAssertionAxioms; }
  public Set<OWLIndividual> getCreatedIndividuals() { return new HashSet<OWLIndividual>(createdIndividuals.values()); }
  public Set<OWLPropertyAssertionAxiom> getCreatedPropertyAssertionAxioms() { return createdPropertyAssertionAxioms; }

  /**
   ** Infer an OWL property assertion axiom from a rule engine
   */
  protected void inferPropertyAssertionAxiom(OWLPropertyAssertionAxiom owlPropertyAssertionAxiom) throws SWRLRuleEngineBridgeException
  { 
    if (!inferredPropertyAssertionAxioms.contains(owlPropertyAssertionAxiom)) inferredPropertyAssertionAxioms.add(owlPropertyAssertionAxiom); 
  } // inferPropertyAssertionAxiom

  /**
   ** Assert an OWL individual from a rule engine
   */
  protected void inferIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException 
  {
    if (!inferredIndividuals.contains(owlIndividual)) inferredIndividuals.add(owlIndividual); 
  } // inferIndividual

  /**
   ** Invoke a SWRL built-in from a rule engine. <p>
   **
   ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ#nid6QF">here</a> for documentaton.
   */
  protected boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) 
    throws BuiltInException
  {
    boolean hasUnboundArguments = hasUnboundArguments(arguments);
    boolean result;

    if (!SWRLOWLUtil.isSWRLBuiltIn(owlModel, builtInName)) throw new InvalidBuiltInNameException(ruleName, builtInName);

    result = BuiltInLibraryManager.invokeSWRLBuiltIn(this, ruleName, builtInName, builtInIndex, arguments);
    
    if (result && hasUnboundArguments) {
      checkForUnboundArgument(ruleName, builtInName, arguments); // Ensure it did not leave any arguments unbound if it evaluated to true
      generateBuiltInBindings(ruleName, builtInName, builtInIndex, arguments); // Inform rule engine of results.
    } // if

    return result;
  } // invokeSWRLBuiltIn
  
  /**
   ** Method used to create a bridge individual of type owl:Thing. This method will typically be invoked from within a built-in. An OWL
   ** individual is not created at this point - instead an object is generated for the individual in the bridge and the individual is
   ** exported to the rule engine. The individual is given a unique name that can be used later if an OWL individual is created for it.
   */
  public OWLIndividual createOWLIndividual() throws SWRLRuleEngineBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLCreated");
    OWLIndividual owlIndividual = BridgeFactory.createOWLIndividual(individualName, edu.stanford.smi.protegex.owl.model.OWLNames.Cls.THING);
    createOWLIndividual(owlIndividual);
    return owlIndividual;
  } // createOWLIndividual
    
  public void createOWLIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException
  {
    createdIndividuals.put(owlIndividual.getIndividualName(), owlIndividual); 

    defineIndividual(owlIndividual); // Export the individual to the rule engine.
  } // createOWLIndividual

  public OWLIndividual createOWLIndividual(OWLClass owlClass) throws SWRLRuleEngineBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLCreated");
    OWLIndividual owlIndividual = BridgeFactory.createOWLIndividual(individualName, owlClass.getClassName());

    if (!importedClasses.containsKey(owlClass.getClassName())) defineClass(owlClass);
    
    createdIndividuals.put(individualName, owlIndividual); 
    defineIndividual(owlIndividual); // Export the individual to the rule engine.

    return owlIndividual;
  } // createOWLIndividual

  public void createOWLIndividuals(Set<OWLIndividual> individuals)
    throws SWRLRuleEngineBridgeException
  {
    for (OWLIndividual owlIndividual : individuals) createOWLIndividual(owlIndividual);
  } // createOWLDatatypePropertyAssertionAxioms

  public OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property,
                                                                                   OWLDatatypeValue object) 
    throws SWRLRuleEngineBridgeException
  {
    OWLDatatypePropertyAssertionAxiom axiom = BridgeFactory.createOWLDatatypePropertyAssertionAxiom(subject, property, object);
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

  public OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property,
                                                                               OWLIndividual object) 
    throws SWRLRuleEngineBridgeException
  {
    OWLObjectPropertyAssertionAxiom axiom = BridgeFactory.createOWLObjectPropertyAssertionAxiom(subject, property, object);
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

  public void setMapper(Mapper mapper) { this.mapper = mapper; }
  public boolean hasMapper() { return mapper != null; }
  public Mapper getMapper() { return mapper; }

  /**
   ** Create OWL individuals in model for the individuals generated during rule execution.
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
    SWRLFactory factory = new SWRLFactory(owlModel);;
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

    rule = BridgeFactory.createSWRLRule(imp.getName(), bodyAtoms, headAtoms);

    importedSWRLRules.put(rule.getRuleName(), rule);
  } // importSWRLRule

  private Atom processSWRLAtom(SWRLAtom swrlAtom, boolean isConsequent) throws SWRLRuleEngineBridgeException
  {
    Atom atom;
    
    if (swrlAtom instanceof SWRLClassAtom) {
      ClassAtom classAtom = BridgeFactory.createClassAtom((SWRLClassAtom)swrlAtom);
      referencedClassNames.add(classAtom.getClassName());
      atom = classAtom;
    } else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {
      DatavaluedPropertyAtom datavaluedPropertyAtom = BridgeFactory.createDatavaluedPropertyAtom(owlModel, (SWRLDatavaluedPropertyAtom)swrlAtom);
      referencedPropertyNames.add(datavaluedPropertyAtom.getPropertyName());
      atom = datavaluedPropertyAtom;
    } else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {
      IndividualPropertyAtom individualPropertyAtom = BridgeFactory.createIndividualPropertyAtom((SWRLIndividualPropertyAtom)swrlAtom);
      referencedPropertyNames.add(individualPropertyAtom.getPropertyName());
      atom = individualPropertyAtom;
    } else if (swrlAtom instanceof SWRLSameIndividualAtom) {
      atom = BridgeFactory.createSameIndividualAtom((SWRLSameIndividualAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) {
      atom = BridgeFactory.createDifferentIndividualsAtom((SWRLDifferentIndividualsAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLBuiltinAtom) {
      atom = BridgeFactory.createBuiltInAtom(owlModel, (SWRLBuiltinAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDataRangeAtom) 
      atom = BridgeFactory.createDataRangeAtom((SWRLDataRangeAtom)swrlAtom);
    else throw new InvalidSWRLAtomException(swrlAtom.getBrowserText());

    if (atom.hasReferencedIndividuals()) referencedIndividualNames.addAll(atom.getReferencedIndividualNames());
    
    return atom;
  } // processSWRLAtom
  
  private void importOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    // TODO: workaround becuase owlModel.getOWLNamedClass() called in OWLClass does not always return an OWL named class.
    RDFSNamedClass rdfsNamedClass = SWRLOWLUtil.getRDFSNamedClass(owlModel, className);
    if (rdfsNamedClass == null) throw new InvalidClassNameException(className);

    if (rdfsNamedClass.isMetaclass()) return;    
    if (rdfsNamedClass.isAnonymous()) return;    

    if (!importedClasses.containsKey(className)) {
      OWLClass owlClass = BridgeFactory.createOWLClass(owlModel, className);
      importedClasses.put(className, owlClass);
      importOWLClasses(owlClass.getDirectSuperClassNames());
      importOWLClasses(owlClass.getDirectSubClassNames());
    } // if
  } // importOWLClass

  private void importOWLClasses(Set<String> classNames) throws SWRLRuleEngineBridgeException
  {
    for (String className : classNames) importOWLClass(className);
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
      if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) { // TODO: may not be OWLIndividual. Should we detect attempts to use OWL Full?
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
        importOWLIndividual(individual.getName());
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

      importOWLClasses(SWRLOWLUtil.rdfResources2Names(property.getUnionDomain()));
      importOWLClasses(SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses()));
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
      OWLIndividual owlIndividual = BridgeFactory.createOWLIndividual(owlModel, individualName);
      importedIndividuals.put(individualName, owlIndividual);
      importOWLClasses(owlIndividual.getDefiningClassNames());
      importOWLClasses(owlIndividual.getDefiningSuperClassNames());
      importOWLClasses(owlIndividual.getDefiningEquivalentClassNames());
    } // if
  } // importOWLIndividual

  // We only import owl:SameAs, owl:differentFrom, and owl:AllDifferent, owl:equivalentProperty, and owl:equivalentClass axioms at the
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
          importedAxioms.add(BridgeFactory.createOWLSameIndividualsAxiom(BridgeFactory.createOWLIndividual(individual1.getName()), 
                                                                             BridgeFactory.createOWLIndividual(individual2.getName())));
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
          importedAxioms.add(BridgeFactory.createOWLDifferentIndividualsAxiom(BridgeFactory.createOWLIndividual(individual1.getName()), 
                                                                              BridgeFactory.createOWLIndividual(individual2.getName())));
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
            individuals.add(BridgeFactory.createOWLIndividual(individual.getName()));
          } // while
          owlDifferentIndividualsAxiom = BridgeFactory.createOWLDifferentIndividualsAxiom(individuals);
          importedAxioms.add(owlDifferentIndividualsAxiom);
        } // if
        } // while
    } // if
  } // importAllDifferentsAxioms

  public void exportSWRLRules() throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) defineRule(rule);
  } // exportSWRLRules

  public void exportSWRLRules(Set<String> ruleNames) throws SWRLRuleEngineBridgeException
  {
    for (SWRLRule rule : importedSWRLRules.values()) if (ruleNames.contains(rule.getRuleName())) defineRule(rule);
  } // exportSWRLRules

  public void exportSWRLRule(String ruleName) throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleNames = new HashSet<String>();
    ruleNames.add(ruleName);
    exportSWRLRules(ruleNames);
  } // exportSWRLRule

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
  
  private void clearExportedAndInferredKnowledge() 
  {
    exportedClassNames.clear();
    exportedIndividualNames.clear();
    inferredPropertyAssertionAxioms.clear();
    inferredIndividuals.clear();
  } // clearExportedAndInferredKnowledge

  private void checkForUnboundArgument(String ruleName, String builtInName, List<BuiltInArgument> arguments) throws BuiltInException
  {
    int argumentNumber = 0;

    for (BuiltInArgument argument : arguments) {
      if (argument.isUnbound())  throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                                            "returned with unbound argument ?" + argument.getVariableName());
      else if (argument instanceof MultiArgument && ((MultiArgument)argument).hasNoArguments())
        throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                   "returned with empty multi-argument ?" + argument.getVariableName());
      argumentNumber++;
    } // for
  } // checkForUnboundArgument

  private void generateBuiltInBindings(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments)
    throws BuiltInException
  {
    List<Integer> multiArgumentIndexes = getMultiArgumentIndexes(arguments);
    
    if (multiArgumentIndexes.isEmpty()) 
      generateBuiltInBinding(ruleName, builtInName, builtInIndex, arguments); // No multi-arguments - do a simple bind
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
        generateBuiltInBinding(ruleName, builtInName, builtInIndex, argumentsPattern); // Call the rule engine method.
      } while (!nextMultiArgumentCounts(multiArgumentCounts, multiArgumentSizes));
    } // if
  } // generateBuiltInBindings

  private boolean nextMultiArgumentCounts(List<Integer> multiArgumentCounts, List<Integer> multiArgumentSizes)
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
    
  private List<Integer> getMultiArgumentIndexes(List<BuiltInArgument> arguments)
  {
    List<Integer> result = new ArrayList<Integer>();

    for (int i = 0; i < arguments.size(); i++) 
      if (arguments.get(i) instanceof MultiArgument) result.add(Integer.valueOf(i));

    return result;
  } // getMultiArgumentIndexes

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

    createdIndividuals = new HashMap<String, OWLIndividual>();
    createdPropertyAssertionAxioms = new HashSet<OWLPropertyAssertionAxiom>();
  } // initialize

  private boolean hasUnboundArguments(List<BuiltInArgument> arguments) 
  {
    for (BuiltInArgument argument : arguments) if (argument.isUnbound()) return true;

    return false;
  } // hasUnboundArguments
  
} // SWRLRuleEngineBridge
