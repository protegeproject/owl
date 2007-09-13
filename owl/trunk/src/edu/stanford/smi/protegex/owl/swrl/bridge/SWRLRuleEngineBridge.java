
// TODO: DataRange

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.ResultException;
import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import java.util.*;
import java.io.*;

/**
 ** The SWRL Rule Engine Bridge provides a mechanism to incorporate rule engines into Protege-OWL to execute SWRL rules. <p>
 **
 ** Detailed documentation for this class can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public abstract class SWRLRuleEngineBridge implements SWRLRuleEngine, SQWRLQueryEngine, Serializable
{
  protected abstract void defineRule(RuleInfo ruleInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineRestriction(RestrictionInfo restrictionInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void initializeRuleEngine() throws SWRLRuleEngineBridgeException;
  protected abstract void generateBuiltInBinding(String ruleName, String builtInName, int builtInIndex, List<Argument> arguments) 
    throws BuiltInException;

  public abstract void runRuleEngine() throws SWRLRuleEngineBridgeException;

  protected OWLModel owlModel; // Holds the OWL model that is associated with this bridge

  // RuleInfo objects representing imported SWRL rules.
  private HashMap<String, RuleInfo> importedSWRLRules; 

  // Names of classes, properties and individuals explicitly referred to in SWRL rules. These are filled in as the SWRL rules are imported
  // and are used to determine the relevant OWL knowledge to import.
  private Set<String> referencedClassNames, referencedPropertyNames, referencedIndividualNames;

  // Info objects representing imported classes, properties, and individuals. 
  private HashMap<String, ClassInfo> importedClasses;
  private HashMap<String, IndividualInfo> importedIndividuals;
  private Set<String> importedPropertyNames;
  private Set<PropertyInfo> importedProperties; 
  private Set<RestrictionInfo> importedRestrictions;

  // Names of classes, properties, and individuals that have been exported to target.
  private Set<String> exportedClassNames, exportedIndividualNames; 

  // Info objects representing asserted individuals and their class membership information. 
  private Set<IndividualInfo> assertedIndividuals;
  private Set<PropertyInfo> assertedProperties; 

  // Info objects representing created individuals.
  private HashMap<String, IndividualInfo> createdIndividuals;

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
    writeAssertedIndividualsAndProperties2OWL();
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
    importOWLProperties(referencedPropertyNames); // Import all referenced properties (and the necessary classes).
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
    exportProperties();
    exportAxioms();
  } // exportSWRLRulesAndOWLKnowledge

  /**
   ** Send knowledge (excluding SWRL rules) stored in bridge to a rule engine.
   */
  public void exportOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportClasses();
    exportIndividuals();
    exportProperties();
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
  public void writeAssertedIndividualsAndProperties2OWL() throws SWRLRuleEngineBridgeException
  {
    createCreatedIndividuals(); // Create any OWL individuals generated by rules. 

    writeAssertedIndividuals2OWL();
    writeAssertedProperties2OWL();
  } // writeAssertedIndividualsAndProperties2OWL

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
    importedProperties.clear();
    importedPropertyNames.clear();
    importedIndividuals.clear();
    importedRestrictions.clear();
    createdIndividuals.clear();
    clearExportedAndAssertedKnowledge();
  } // resetBridge

  /**
   **  Clear all knowledge from rule engine, deleted asserted knowledge from the bridge, and leave imported bridge knowledge intact.
   */
  public void resetRuleEngine() throws SWRLRuleEngineBridgeException
  {
    initializeRuleEngine();
    clearExportedAndAssertedKnowledge();
  } // resetRuleEngine

  /**
   **  Get the results from a rule containing query built-ins. Null is retured if there are no results or if the query subsystem is not
   **  activated.
   */
  public Result getQueryResult(String ruleName) throws ResultException
  {
    QueryLibrary queryLibrary = null;
    Result result = null;
    
    try {
      queryLibrary = (QueryLibrary)BuiltInLibraryManager.getBuiltInLibraryByPrefix(QueryNames.QueryPrefix);
      result = queryLibrary.getQueryResult(ruleName);
    } catch (InvalidBuiltInLibraryNameException e) {
    } // try

    return result;
  } // getQueryResult

  /**
   **  Get the results from a SQWRL query. Null is retured if there are no results or if the query subsystem is not activated.
   */
  public Result getSQWRLResult(String ruleName) throws ResultException
  {
    SQWRLQueryLibrary queryLibrary = null;
    Result result = null;
    
    try {
      queryLibrary = (SQWRLQueryLibrary)BuiltInLibraryManager.getBuiltInLibraryByPrefix("sqwrl");
      result = queryLibrary.getSQWRLResult(ruleName);
    } catch (InvalidBuiltInLibraryNameException e) {
    } // try

    return result;
  } // getSQWRLResult

  /**
   ** Get the OWL model associated with this bridge.
   */
  public OWLModel getOWLModel() { return owlModel; }

  // Convenience methods to display bridge activity
  public int getNumberOfImportedSWRLRules() { return importedSWRLRules.size(); }
  public int getNumberOfImportedClasses() { return importedClasses.size(); }
  public int getNumberOfImportedIndividuals() { return importedIndividuals.size(); }
  public int getNumberOfImportedProperties() { return importedProperties.size(); }
  public int getNumberOfImportedRestrictions() { return importedRestrictions.size(); }
  public int getNumberOfAssertedIndividuals() { return assertedIndividuals.size(); }
  public int getNumberOfAssertedProperties() { return assertedProperties.size(); }
  public int getNumberOfCreatedIndividuals() { return createdIndividuals.size(); }

  public boolean isCreatedIndividual(String individualName) { return createdIndividuals.containsKey(individualName); }

  // Convenience methods for subclasses that may wish to display the contents of the bridge.
  protected Set<RuleInfo> getImportedSWRLRules() { return new HashSet<RuleInfo>(importedSWRLRules.values()); }
  protected Set<ClassInfo> getImportedClasses() { return new HashSet<ClassInfo>(importedClasses.values()); }
  protected Set<IndividualInfo> getImportedIndividuals() { return new HashSet<IndividualInfo>(importedIndividuals.values()); }
  protected Set<PropertyInfo> getImportedProperties() { return importedProperties; }
  protected Set<RestrictionInfo> getImportedRestrictions() { return importedRestrictions; }
  protected Set<IndividualInfo> getAssertedIndividuals() { return assertedIndividuals; }
  protected Set<PropertyInfo> getAssertedProperties() { return assertedProperties; }
  protected Set<IndividualInfo> getCreatedIndividuals() { return new HashSet<IndividualInfo>(createdIndividuals.values()); }

  /**
   ** Assert an OWL property from a rule engine.
   */
  protected void assertProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException
  { 
    assertedProperties.add(propertyInfo); 
  } // assertProperty

  /**
   ** Assert an OWL individual from a rule engine.
   */
  protected void assertIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException 
  {
    assertedIndividuals.add(individualInfo); 
  } // assertIndividual

  /**
   ** Invoke a SWRL built-in from a rule engine. <p>
   **
   ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ#nid6QF">here</a> for documentaton.
   */
  protected boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, List<Argument> arguments) throws BuiltInException
  {
    boolean hasUnboundArguments = hasUnboundArguments(arguments);
    boolean result;

    if (!isBuiltIn(builtInName)) throw new InvalidBuiltInNameException(ruleName, builtInName);

    result = BuiltInLibraryManager.invokeSWRLBuiltIn(this, ruleName, builtInName, builtInIndex, arguments);
    
    if (result && hasUnboundArguments) {
      checkForUnboundArgument(ruleName, builtInName, arguments); // Ensure it did not leave any arguments unbound if it evaluated to true.
      generateBuiltInBindings(ruleName, builtInName, builtInIndex, arguments); // Inform rule engine of results.
    } // if

    return result;
  } // invokeSWRLBuiltIn
  
  /**
   ** Method used to create a bridge individual of type owl:Thing. This method will typically be invoked from within a built-in. An OWL
   ** individual is not created at this point - instead an info object is generated for the individual in the bridge and the individual is
   ** exported to the rule engine. The individual is given a unique name that can be used later if an OWL individual is created for it.
   */
  public IndividualInfo createIndividual() throws SWRLRuleEngineBridgeException
  {
    String individualName = SWRLOWLUtil.createNewResourceName(owlModel, "SWRLCreated");
    IndividualInfo individualInfo = new IndividualInfo(individualName, OWLNames.Cls.THING);
    
    createdIndividuals.put(individualName, individualInfo); 
    defineIndividual(individualInfo); // Export the individual to the rule engine.

    return individualInfo;
  } // createIndividual

  /**
   ** Create OWL individuals for the individuals (represented by info objects) generated during rule execution.
   */
  private void createCreatedIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (IndividualInfo individualInfo: createdIndividuals.values()) {
      String individualName = individualInfo.getIndividualName();
      if (SWRLOWLUtil.isOWLIndividual(owlModel, individualName)) continue; // We have already created it.
      try {
        OWLIndividual individual = SWRLOWLUtil.createIndividualOfClass(owlModel, SWRLOWLUtil.getOWLThingClass(owlModel), individualName);
      } catch (SWRLOWLUtilException e) {
        throw new SWRLRuleEngineBridgeException("cannot create OWL individual '" + individualName + "': " + e.getMessage());
      } // try
    } // for
  } // createCreatedIndividuals

  private void importSWRLRules(Set<String> ruleGroupNames) throws SWRLRuleEngineBridgeException
  {
    SWRLFactory factory = new SWRLFactory(owlModel);;
    Collection rules = factory.getEnabledImps(ruleGroupNames);

    if (rules == null) return;

    Iterator iterator = rules.iterator();
    while (iterator.hasNext()) {
      SWRLImp rule = (SWRLImp)iterator.next();
      importSWRLRule(rule);
    } // while
  } // importSWRLRules

  private void importSWRLRule(SWRLImp rule) throws SWRLRuleEngineBridgeException
  {
    List<AtomInfo> bodyAtoms = new ArrayList<AtomInfo>();
    List<AtomInfo> headAtoms = new ArrayList<AtomInfo>();
    RuleInfo ruleInfo;
    Iterator iterator;

    iterator = rule.getBody().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      bodyAtoms.add(processSWRLAtom(swrlAtom, false));
    } // while 

    iterator = rule.getHead().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      headAtoms.add(processSWRLAtom(swrlAtom, true));
    } // while 

    ruleInfo = new RuleInfo(rule.getName(), bodyAtoms, headAtoms);

    importedSWRLRules.put(rule.getName(), ruleInfo);
  } // importSWRLRule

  private AtomInfo processSWRLAtom(SWRLAtom swrlAtom, boolean isConsequent) throws SWRLRuleEngineBridgeException
  {
    AtomInfo atomInfo;
    
    if (swrlAtom instanceof SWRLClassAtom) {
      ClassAtomInfo classAtomInfo = new ClassAtomInfo((SWRLClassAtom)swrlAtom);
      referencedClassNames.add(classAtomInfo.getClassName());
      atomInfo = classAtomInfo;
    } else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {
      DatavaluedPropertyAtomInfo datavaluedPropertyAtomInfo = new DatavaluedPropertyAtomInfo(owlModel, (SWRLDatavaluedPropertyAtom)swrlAtom);
      referencedPropertyNames.add(datavaluedPropertyAtomInfo.getPropertyName());
      atomInfo = datavaluedPropertyAtomInfo;
    } else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {
      IndividualPropertyAtomInfo individualPropertyAtomInfo = new IndividualPropertyAtomInfo((SWRLIndividualPropertyAtom)swrlAtom);
      referencedPropertyNames.add(individualPropertyAtomInfo.getPropertyName());
      atomInfo = individualPropertyAtomInfo;
    } else if (swrlAtom instanceof SWRLSameIndividualAtom) {
      atomInfo = new SameIndividualAtomInfo((SWRLSameIndividualAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) {
      atomInfo = new DifferentIndividualsAtomInfo((SWRLDifferentIndividualsAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLBuiltinAtom) {
      atomInfo = new BuiltInAtomInfo(owlModel, (SWRLBuiltinAtom)swrlAtom);
    } else if (swrlAtom instanceof SWRLDataRangeAtom) 
      atomInfo = new DataRangeAtomInfo((SWRLDataRangeAtom)swrlAtom);
    else throw new InvalidSWRLAtomException(swrlAtom.getBrowserText());

    if (atomInfo.hasReferencedIndividuals()) referencedIndividualNames.addAll(atomInfo.getReferencedIndividualNames());
    
    return atomInfo;
  } // processSWRLAtom
  
  private void importOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    // TODO: workaround becuase owlModel.getOWLNamedClass() called in ClassInfo does not always return an OWL named class.
    RDFSNamedClass rdfsNamedClass = owlModel.getRDFSNamedClass(className);
    if (rdfsNamedClass == null) throw new InvalidClassNameException(className);
    if (rdfsNamedClass.isMetaclass()) return;    
    if (rdfsNamedClass.isAnonymous()) return;    

    if (!importedClasses.containsKey(className)) {
      ClassInfo classInfo = new ClassInfo(owlModel, className);
      importedClasses.put(className, classInfo);
      importOWLClasses(classInfo.getDirectSuperClassNames());
      importOWLClasses(classInfo.getDirectSubClassNames());
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
    RDFSClass rdfsClass = owlModel.getRDFSNamedClass(className);
    if (rdfsClass == null) throw new InvalidClassNameException(className);
    Collection instances = rdfsClass.getInstances(true);
    instances.addAll(rdfsClass.getInferredInstances(true));

    Iterator iterator = instances.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLIndividual) { // TODO: may not be OWLIndividual. Should we detect attempts to use OWL Full?
        OWLIndividual individual = (OWLIndividual)o;
        importOWLIndividual(individual.getName());
      } // if
    } // while
  } // importAllOWLIndividualsOfClass
  
  private void importOWLProperties(Set<String> propertyNames) throws SWRLRuleEngineBridgeException
  {
    for (String propertyName : propertyNames) importOWLProperty(propertyName);
  } // importProperties

  private void importOWLProperty(String propertyName) throws SWRLRuleEngineBridgeException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);

    if (!importedPropertyNames.contains(propertyName)) {
      List<PropertyInfo> propertyInfoList = PropertyInfo.buildPropertyInfoList(owlModel, propertyName);
      importedProperties.addAll(propertyInfoList);
      importedPropertyNames.add(propertyName);

      importOWLClasses(SWRLOWLUtil.rdfResources2Names(property.getUnionDomain()));
      importOWLClasses(SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses()));
      importOWLProperties(SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true)));
      importOWLProperties(SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true)));
    } // if
  } // importProperty

  private void importOWLIndividuals(Set<String> individualNames) throws SWRLRuleEngineBridgeException
  {
    for (String individualName : individualNames) importOWLIndividual(individualName);
  } // importOWLIndividuals

  private void importOWLIndividual(String individualName) throws SWRLRuleEngineBridgeException
  {
    if (!importedIndividuals.containsKey(individualName)) {
      IndividualInfo individualInfo = new IndividualInfo(owlModel, individualName);
      importedIndividuals.put(individualName, individualInfo);
      importOWLClasses(individualInfo.getDefiningClassNames());
      importOWLClasses(individualInfo.getDefiningSuperClassNames());
      importOWLClasses(individualInfo.getDefiningEquivalentClassNames());
    } // if
  } // importOWLIndividual

  // We only import owl:SameAs, owl:differentFrom, and owl:AllDifferent, owl:equivalentProperty, and owl:equivalentClass axioms at the
  // moment. We support owl:equivalentProperty and owl:equivalentClass axioms indirectly through the IndividualInfo class.
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
      if (!(object1 instanceof OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
      OWLIndividual individual1 = (OWLIndividual)object1;
      if (individual1.hasPropertyValue(sameAsProperty)) {
        Collection individuals = (Collection)individual1.getPropertyValues(sameAsProperty);
        Iterator individualsIterator2 = individuals.iterator();
        while (individualsIterator2.hasNext()) {
          Object object2 = individualsIterator2.next();
          if (!(object2 instanceof OWLIndividual)) continue;
          OWLIndividual individual2 = (OWLIndividual)object2;
          importedRestrictions.add(new SameAsRestrictionInfo(individual1.getName(), individual2.getName()));
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
      OWLIndividual individual1 = (OWLIndividual)object1;
      if (individual1.hasPropertyValue(differentFromProperty)) {
        Collection individuals = (Collection)individual1.getPropertyValues(differentFromProperty);
        Iterator individualsIterator2 = individuals.iterator();
        while (individualsIterator2.hasNext()) {
          Object object2 = individualsIterator2.next();
          if (!(object2 instanceof OWLIndividual)) continue;
          OWLIndividual individual2 = (OWLIndividual)object2;
          importedRestrictions.add(new DifferentFromRestrictionInfo(individual1.getName(), individual2.getName()));
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
          AllDifferentRestrictionInfo allDifferentRestrictionInfo = new AllDifferentRestrictionInfo();
          
          Iterator individualsIterator = owlAllDifferent.getDistinctMembers().iterator();
          while (individualsIterator.hasNext()) {
            RDFIndividual individual = (RDFIndividual)individualsIterator.next();
            allDifferentRestrictionInfo.addIndividualName(individual.getName());
          } // while
          importedRestrictions.add(allDifferentRestrictionInfo);
        } // if
        } // while
    } // if
  } // importAllDifferentsAxioms

  public void exportSWRLRules() throws SWRLRuleEngineBridgeException
  {
    for (RuleInfo ruleInfo : importedSWRLRules.values()) defineRule(ruleInfo);
  } // exportSWRLRules

  public void exportSWRLRules(Set<String> ruleNames) throws SWRLRuleEngineBridgeException
  {
    for (RuleInfo ruleInfo : importedSWRLRules.values()) if (ruleNames.contains(ruleInfo.getRuleName())) defineRule(ruleInfo);
  } // exportSWRLRules

  public void exportSWRLRule(String ruleName) throws SWRLRuleEngineBridgeException
  {
    Set<String> ruleNames = new HashSet<String>();
    ruleNames.add(ruleName);
    exportSWRLRules(ruleNames);
  } // exportSWRLRule

  public RuleInfo getRuleInfo(String ruleName) throws SWRLRuleEngineBridgeException
  {
      if (!importedSWRLRules.containsKey(ruleName)) throw new SWRLRuleEngineBridgeException("Invalid rule name '" + ruleName + "'.");
      return importedSWRLRules.get(ruleName);
  } // getRuleInfo

  private void exportClasses() throws SWRLRuleEngineBridgeException
  {
      for (ClassInfo classInfo : importedClasses.values()) exportClass(classInfo);
  } // exportClasses

  private void exportClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException
  {
    String className = classInfo.getClassName();
    Set<String> superClassNames = classInfo.getDirectSuperClassNames();

    if (!exportedClassNames.contains(className)) { // See if it is already defined.
      defineClass(classInfo);
      exportedClassNames.add(className);

      if (!superClassNames.isEmpty()) { // Superclasses must be defined before subclasses.
        for (String superClassName : superClassNames) {
          ClassInfo superClassInfo = importedClasses.get(superClassName);
          exportClass(superClassInfo); 
        } // for
      } // if
    } // if
  } // exportClass

  private void exportProperties() throws SWRLRuleEngineBridgeException
  {
    for (PropertyInfo propertyInfo : importedProperties) {
      String propertyName = propertyInfo.getPropertyName();
      defineProperty(propertyInfo);
    } // for
  } // exportProperties

  private void exportIndividuals() throws SWRLRuleEngineBridgeException
  {
    for (IndividualInfo individualInfo : importedIndividuals.values()) {
      String individualName = individualInfo.getIndividualName();
      if (exportedIndividualNames.contains(individualName)) continue;
      defineIndividual(individualInfo);
      exportedIndividualNames.add(individualName);
    } // for
  } // exportIndividuals

  private void exportAxioms() throws SWRLRuleEngineBridgeException
  {
    for (RestrictionInfo restrictionInfo: importedRestrictions) defineRestriction(restrictionInfo);
  } // exportAxioms

  private void writeAssertedProperties2OWL() throws SWRLRuleEngineBridgeException
  {
    for (PropertyInfo propertyInfo : assertedProperties) propertyInfo.write2OWL(owlModel);
  } // writeAssertedProperties2OWL

  private void writeAssertedIndividuals2OWL() throws SWRLRuleEngineBridgeException
  {
      for (IndividualInfo individualInfo : assertedIndividuals) individualInfo.write2OWL(owlModel);
  } // writeAssertedIndividuals2OWL
  
  private void clearExportedAndAssertedKnowledge() 
  {
    exportedClassNames.clear();
    exportedIndividualNames.clear();
    assertedProperties.clear();
    assertedIndividuals.clear();
  } // clearExportedAndAssertedKnowledge
  
  private boolean isBuiltIn(String builtInName)
  {
    RDFResource resource = owlModel.getRDFResource(builtInName);
    return resource != null && resource.getProtegeType().getName().equals(SWRLNames.Cls.BUILTIN);
  } // isBuiltIn

  private boolean isObjectProperty(String propertyName) throws SWRLRuleEngineBridgeException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    return property.isObjectProperty();
  } // isObjectProperty

  private void checkForUnboundArgument(String ruleName, String builtInName, List<Argument> arguments) throws BuiltInException
  {
    int argumentNumber = 0;

    for (Argument argument : arguments) {
      if (argument == null) // An unbound argument is indicated by a null for an argument value.
        throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                   "returned with unbound argument #" + argumentNumber);
      else if (argument instanceof MultiArgument && ((MultiArgument)argument).hasNoArguments())
        throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                   "returned with empty multi-argument #" + argumentNumber);
      argumentNumber++;
    } // for
  } // checkForUnboundArgument

  private void generateBuiltInBindings(String ruleName, String builtInName, int builtInIndex, List<Argument> arguments)
    throws BuiltInException
  {
    List<Integer> multiArgumentIndexes = getMultiArgumentIndexes(arguments);
    
    if (multiArgumentIndexes.isEmpty()) 
      generateBuiltInBinding(ruleName, builtInName, builtInIndex, arguments); // No multi-arguments - do a simple bind
    else {
      List<Integer> multiArgumentCounts = new ArrayList<Integer>();
      List<Integer> multiArgumentSizes = new ArrayList<Integer>();
      List<Argument> argumentsPattern;

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

  private List<Argument> generateArgumentsPattern(List<Argument> arguments, List<Integer> multiArgumentCounts)
  {
    List<Argument> result = new ArrayList<Argument>();
    int multiArgumentIndex = 0;

    for (Argument argument: arguments) {
      if (argument instanceof MultiArgument) {
        MultiArgument multiArgument = (MultiArgument)argument;
        result.add(multiArgument.getArguments().get((multiArgumentCounts.get(multiArgumentIndex).intValue())));
        multiArgumentIndex++;
      } else result.add(argument);
    } // for

    return result;
  } // generateArgumentsPattern
    
  private List<Integer> getMultiArgumentIndexes(List<Argument> arguments)
  {
    List<Integer> result = new ArrayList<Integer>();

    for (int i = 0; i < arguments.size(); i++) 
      if (arguments.get(i) instanceof MultiArgument) result.add(Integer.valueOf(i));

    return result;
  } // getMultiArgumentIndexes

  private void initialize()
  {
    importedSWRLRules = new HashMap<String, RuleInfo>();

    referencedClassNames = new HashSet<String>();
    referencedIndividualNames = new HashSet<String>();
    referencedPropertyNames = new HashSet<String>();

    importedClasses = new HashMap<String, ClassInfo>();
    importedIndividuals = new HashMap<String, IndividualInfo>(); 
    importedProperties = new HashSet<PropertyInfo>(); 
    importedPropertyNames = new HashSet<String>();
    importedRestrictions = new HashSet<RestrictionInfo>();

    exportedClassNames = new HashSet<String>();
    exportedIndividualNames = new HashSet<String>();

    assertedIndividuals = new HashSet<IndividualInfo>(); 
    assertedProperties = new HashSet<PropertyInfo>(); 

    createdIndividuals = new HashMap<String, IndividualInfo>();
  } // initialize

  private boolean hasUnboundArguments(List<Argument> arguments) 
  {
    return arguments.contains(null);
  } // hasUnboundArguments
  
} // SWRLRuleEngineBridge
