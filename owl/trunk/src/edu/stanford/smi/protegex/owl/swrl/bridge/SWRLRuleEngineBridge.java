
// TODO: DataRange

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;

/**
 ** The SWRL Rule Engine Bridge provides a mechanism to incorporate rule engines into Protege-OWL to execute SWRL rules. <p>
 **
 ** Detailed documentation for this class can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public abstract class SWRLRuleEngineBridge
{
  protected abstract void defineRule(RuleInfo ruleInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineRestriction(RestrictionInfo restrictionInfo) throws SWRLRuleEngineBridgeException;

  protected abstract void initializeRuleEngine() throws SWRLRuleEngineBridgeException;

  public abstract void runRuleEngine() throws SWRLRuleEngineBridgeException;

  private static String BuiltInLibraryPackageBaseName = "edu.stanford.smi.protegex.owl.swrl.bridge.builtins.";
  private static String BuiltInLibraryInitializeMethodName = "initialize";

  protected OWLModel owlModel; // Holds the OWL model that is associated with this bridge.

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
  private Collection<RestrictionInfo> importedRestrictions;

  // Names of classes, properties, and individuals that have been exported to target.
  private Set<String> exportedClassNames, exportedIndividualNames; 

  // Info objects representing asserted individuals and their class membership information. 
  private Set<IndividualInfo> assertedIndividuals;
  private Set<PropertyInfo> assertedProperties; 

  // Holds class instances implementing built-ins.
  private HashMap<String, SWRLBuiltInLibrary> builtInLibraryClassInstances;

  // Name of rule currently invoking built-in method. Valid only when built-in is being called.
  private String currentBuiltInRuleName = "";

  public String getCurrentBuiltInRuleName() { return currentBuiltInRuleName; }

  protected SWRLRuleEngineBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    this.owlModel = owlModel;

    importedSWRLRules = new HashMap<String, RuleInfo>();

    referencedClassNames = new HashSet<String>();
    referencedIndividualNames = new HashSet<String>();
    referencedPropertyNames = new HashSet<String>();

    importedClasses = new HashMap<String, ClassInfo>();
    importedIndividuals = new HashMap<String, IndividualInfo>(); 
    importedProperties = new HashSet<PropertyInfo>(); 
    importedPropertyNames = new HashSet<String>();
    importedRestrictions = new ArrayList<RestrictionInfo>();

    exportedClassNames = new HashSet<String>();
    exportedIndividualNames = new HashSet<String>();

    assertedIndividuals = new HashSet<IndividualInfo>(); 
    assertedProperties = new HashSet<PropertyInfo>(); 

    builtInLibraryClassInstances = new HashMap<String, SWRLBuiltInLibrary>();
  } // SWRLRuleEngineBridge

  public void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    resetRuleEngine();

    if (!owlModel.getInconsistentClasses().isEmpty()) 
      throw new InconsistentKnowledgeBaseException("Cannot import rules from an inconsistent knowledge base");

    importSWRLRules(); // Fills in importedSWRLRules, referencedClassNames, referencedPropertyNames, and referencedIndividualNames

    importOWLClasses(referencedClassNames); // Import all referenced classes (and their superclasses and subclasses).
    importOWLProperties(referencedPropertyNames); // Import all referenced properties (and the necessary classes).
    importOWLIndividuals(referencedIndividualNames); // Import all referenced individuals (and their classes).

    importAllOWLIndividualsOfClasses(referencedClassNames); // Import all individuals that are members of imported classes.

    importOWLRestrictions();
  } // importSWRLRulesAndOWLKnowledge

  public void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportOWLKnowledge(); // Knowledge should be exported before rules because rules may used concepts defined in knowledge.
    exportSWRLRules();
  } // exportSWRLRulesAndOWLKnowledge

  public void exportOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportOWLClasses();
    exportOWLIndividuals();
    exportOWLProperties();
    exportOWLRestrictions();
  } // exportOWLKnowledge

  // Asserted individuals and properties can then be written back to OWL.

  public void writeAssertedIndividualsAndProperties2OWL() throws SWRLRuleEngineBridgeException
  {
    writeAssertedIndividuals2OWL();
    writeAssertedProperties2OWL();
  } // writeAssertedIndividualsAndProperties2OWL

  public void resetBridge() throws SWRLRuleEngineBridgeException
  {
    importedSWRLRules.clear();

    referencedClassNames.clear();
    referencedPropertyNames.clear();
    referencedIndividualNames.clear();

    importedClasses.clear();
    importedProperties.clear();
    importedPropertyNames.clear();
    importedIndividuals.clear();
    importedRestrictions.clear();

    clearExportedAndAssertedKnowledge();

    resetRuleEngine();

    invokeAllBuiltInLibrariesInitializeMethod();
  } // resetBridge

  public void resetRuleEngine() throws SWRLRuleEngineBridgeException
  {
    initializeRuleEngine();
    clearExportedAndAssertedKnowledge();
  } // resetRuleEngine

  public OWLModel getOWLModel() { return owlModel; }

  // Convenience methods to display bridge activity.
  public int getNumberOfImportedSWRLRules() { return importedSWRLRules.size(); }
  public int getNumberOfImportedClasses() { return importedClasses.size(); }
  public int getNumberOfImportedIndividuals() { return importedIndividuals.size(); }
  public int getNumberOfImportedProperties() { return importedProperties.size(); }
  public int getNumberOfImportedRestrictions() { return importedRestrictions.size(); }
  public int getNumberOfAssertedIndividuals() { return assertedIndividuals.size(); }
  public int getNumberOfAssertedProperties() { return assertedProperties.size(); }

  // Convenience methods for subclasses who may wish to display the contents of the bridge.
  protected Collection<RuleInfo> getImportedSWRLRules() { return new ArrayList<RuleInfo>(importedSWRLRules.values()); }
  protected Collection<ClassInfo> getImportedClasses() { return new ArrayList<ClassInfo>(importedClasses.values()); }
  protected Collection<IndividualInfo> getImportedIndividuals() { return new ArrayList<IndividualInfo>(importedIndividuals.values()); }
  protected Collection<PropertyInfo> getImportedProperties() { return importedProperties; }
  protected Collection<RestrictionInfo> getImportedRestrictions() { return importedRestrictions; }
  protected Collection<IndividualInfo> getAssertedIndividuals() { return assertedIndividuals; }
  protected Collection<PropertyInfo> getAssertedProperties() { return assertedProperties; }

  public void assertProperty(String propertyName, String subjectName, String predicateValue) throws SWRLRuleEngineBridgeException
  { 
    PropertyInfo propertyInfo;
    Argument subject, predicate;

    subject = new IndividualInfo(subjectName);
    if (isObjectProperty(propertyName)) predicate = new IndividualInfo(predicateValue);
    else predicate = new LiteralInfo(predicateValue);

    propertyInfo = new PropertyInfo(propertyName, subject, predicate);

    assertedProperties.add(propertyInfo); 
  } // assertProperty

  public void assertIndividual(String individualName, String className) throws SWRLRuleEngineBridgeException 
  {
    IndividualInfo individualInfo = new IndividualInfo(individualName, className);

    assertedIndividuals.add(individualInfo); 
  } // assertIndividual

  // The rule name is included to allow generation of more meaningful errors.
  public boolean invokeSWRLBuiltIn(String ruleName, String builtInName, List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInLibrary swrlBuiltInLibrary = null;
    Class swrlBuiltInLibraryClass = null;
    String namespaceName = "", builtInMethodName = "", className;
    Method method;
    int colonIndex;
    Boolean result = false;

    if (!isBuiltIn(builtInName)) throw new InvalidBuiltInNameException(ruleName, builtInName);
    
    colonIndex = builtInName.indexOf(':');
    if (colonIndex != -1) {
      namespaceName = builtInName.substring(0, colonIndex);
      builtInMethodName = builtInName.substring(colonIndex + 1, builtInName.length());
      className = BuiltInLibraryPackageBaseName + namespaceName + ".SWRLBuiltInLibraryImpl";
    } else { // No namespace - try the base built-ins package. Ordinarily, built-ins should not be located here.
      namespaceName = "";
      builtInMethodName = builtInName;
      className = BuiltInLibraryPackageBaseName + "SWRLBuiltInLibraryImpl";
    } // if
    
    if (builtInLibraryClassInstances.containsKey(namespaceName)) { // Find the implementation
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)builtInLibraryClassInstances.get(namespaceName);
    } else { // Implementation class not loaded - load it, call the initialize method, and cache it.
      swrlBuiltInLibrary = loadSWRLBuiltInLibraryImpl(ruleName, namespaceName, className);
      builtInLibraryClassInstances.put(namespaceName, swrlBuiltInLibrary);
      invokeBuiltInLibraryInitializeMethod(swrlBuiltInLibrary);
    } // if

    method = resolveBuiltInMethod(ruleName, namespaceName, builtInMethodName, swrlBuiltInLibrary); // Find the method.
    checkBuiltInMethodSignature(ruleName, namespaceName, builtInMethodName, method); // Check signature of method.
    
    try { // Invoke the built-in method.
      result = (Boolean)method.invoke(swrlBuiltInLibrary, new Object[] { arguments });
    } catch (InvocationTargetException e) { // The built-in implementation threw an exception.
      Throwable targetException = e.getTargetException();
      if (targetException instanceof BuiltInException) { // A BuiltInException was thrown by the built-in.
        throw new BuiltInException("Exception thrown by built-in '" + builtInName + "' in rule '" + ruleName + "': " 
                                   + targetException.getMessage(), targetException);
      } else if (targetException instanceof RuntimeException) { // A runtime exception was thrown by the built-in.
        throw new BuiltInMethodRuntimeException(ruleName, builtInName, targetException.getMessage(), targetException);
      } // if 
    } catch (Exception e) { // Should be one of IllegalAccessException or IllegalArgumentException
      throw new BuiltInException("Internal bridge exception when invoking built-in method '" + builtInName + "' in rule '" + 
                                 ruleName + "'. Exception: " + e.getMessage(), e);        
    } // try
    checkForUnboundArgument(ruleName, builtInName, arguments); // Make sure built-in did not leave any arguments unbound.

    return result.booleanValue();
  } // invokeSWRLBuiltIn
  
    private void invokeBuiltInLibraryInitializeMethod(SWRLBuiltInLibrary swrlBuiltInLibrary) throws BuiltInException
    {
	try {
	    Method method = 
		swrlBuiltInLibrary.getClass().getMethod(BuiltInLibraryInitializeMethodName, new Class[] {SWRLRuleEngineBridge.class});
	    method.invoke(swrlBuiltInLibrary, new Object[] {this});
	} catch (Exception e) {
	    throw new BuiltInException("Internal bridge exception when invoking initialize method: " + e.getMessage(), e);
	    // TODO: deal with exceptions thrown by method itself.
	} // try
    } // invokeBuiltInLibraryInitializeMethod

    private void invokeAllBuiltInLibrariesInitializeMethod() throws SWRLRuleEngineBridgeException
    {
	for (SWRLBuiltInLibrary library : builtInLibraryClassInstances.values()) invokeBuiltInLibraryInitializeMethod(library);
    } // invokeAllBuiltInMethodsInitializeMethod

  private Method resolveBuiltInMethod(String ruleName, String namespaceName, String builtInMethodName, SWRLBuiltInLibrary swrlBuiltInLibrary)
    throws UnresolvedBuiltInMethodException
  {
    Method method;

    try { 
      method = swrlBuiltInLibrary.getClass().getMethod(builtInMethodName, new Class[] { List.class });
    } catch (Exception e) {
      throw new UnresolvedBuiltInMethodException(ruleName, namespaceName, builtInMethodName, e.getMessage());
    } // try

    return method;
  } // resolveBuiltInMethod

  private SWRLBuiltInLibrary loadSWRLBuiltInLibraryImpl(String ruleName, String namespaceName, String className) 
    throws UnresolvedBuiltInClassException, IncompatibleBuiltInClassException
  {
    Class swrlBuiltInLibraryClass;
    SWRLBuiltInLibrary swrlBuiltInLibrary;

    try {
      swrlBuiltInLibraryClass = Class.forName(className);
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, namespaceName, e.getMessage());
    } // try

    checkBuiltInMethodsClassCompatibility(ruleName, namespaceName, swrlBuiltInLibraryClass); // Check implementation class for compatibility.

    try {
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)swrlBuiltInLibraryClass.newInstance();
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, namespaceName, e.getMessage());
    } // try
    return swrlBuiltInLibrary;
  } // loadSWRLBuiltInLibraryImpl

  private void importSWRLRules() throws SWRLRuleEngineBridgeException
  {
    SWRLFactory factory = new SWRLFactory(owlModel);;
    Collection rules = factory.getImps();

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

    Iterator iterator = rdfsClass.getInstances(true).iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLIndividual) { // TODO: may not be OWLIndividual. Should we detect attempts to use OWL Full?
        OWLIndividual individual = (OWLIndividual)o;
        String individualName = individual.getName();
        IndividualInfo individualInfo = new IndividualInfo(owlModel, individualName);
        importedIndividuals.put(individualName, individualInfo);
      } // if
    } // while
  } // importAllOWLIndividualsOfClass
  
  private void importOWLProperties(Set<String> propertyNames) throws SWRLRuleEngineBridgeException
  {
    for (String propertyName : propertyNames) importOWLProperty(propertyName);
  } // importProperties

  private void importOWLProperty(String propertyName) throws SWRLRuleEngineBridgeException
  {
    if (!importedPropertyNames.contains(propertyName)) {
      List<PropertyInfo> propertyInfoList = PropertyInfo.buildPropertyInfoList(owlModel, propertyName);
      importedProperties.addAll(propertyInfoList);
      importedPropertyNames.add(propertyName);
      
      if (!propertyInfoList.isEmpty()) {
        PropertyInfo propertyInfo = propertyInfoList.get(0); // All info objects will hold the same domain and range classes.
        
        importOWLClasses(propertyInfo.getDomainClassNames());
        importOWLClasses(propertyInfo.getRangeClassNames());
      } // if
    } // if
  } // importProperty

  private void importOWLIndividuals(Set<String> individualNames) throws SWRLRuleEngineBridgeException
  {
    for (String individualName : individualNames) importOWLIndividual(individualName);
  } // importOWLIndividuals

  private void importOWLIndividual(String individualName) throws SWRLRuleEngineBridgeException
  {
    if (!importedIndividuals.containsKey(individualName)) return;
    
    IndividualInfo individualInfo = new IndividualInfo(owlModel, individualName);
    
    importedIndividuals.put(individualName, individualInfo);

    importOWLClasses(individualInfo.getClassNames());
  } // importedIndividual

  // We only import owl:SameAs, owl:differentFrom, and owl:AddDifferent at the moment.
  private void importOWLRestrictions() throws SWRLRuleEngineBridgeException
  {
    importOWLSameAsRestrictions();
    importOWLDifferentFromRestrictions();
    importOWLAllDifferents();
  } // importOWLRestrictions

  // TODO: This is incredibly inefficient. Need to use method in the OWLModel to get individuals with a particular property.
  private void importOWLSameAsRestrictions() throws SWRLRuleEngineBridgeException
  {
    RDFProperty sameAsProperty = owlModel.getOWLSameAsProperty();
    RDFSClass owlThingCls = owlModel.getOWLNamedClass(OWLNames.Cls.THING);

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
  } // importOWLSameAsRestrictions

  // TODO: This is incredibly inefficient (and almost duplicates previous method). Need to use method in the OWLModel to get individuals
  // with a particular property.
  private void importOWLDifferentFromRestrictions() throws SWRLRuleEngineBridgeException
  {
    RDFProperty differentFromProperty = owlModel.getOWLDifferentFromProperty();
    RDFSClass owlThingCls = owlModel.getOWLNamedClass(OWLNames.Cls.THING);

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
  } // importOWLSameAsRestrictions

  private void importOWLAllDifferents() throws SWRLRuleEngineBridgeException
  {
    Collection allDifferents = owlModel.getOWLAllDifferents();

    if (!allDifferents.isEmpty()) {
      Iterator allDifferentsIterator = allDifferents.iterator();
      while (allDifferentsIterator.hasNext()) {
        OWLAllDifferent owlAllDifferent = (OWLAllDifferent)allDifferentsIterator.next();
        AllDifferentRestrictionInfo allDifferentRestrictionInfo = new AllDifferentRestrictionInfo();

        Iterator individualsIterator = owlAllDifferent.getDistinctMembers().iterator();
        while (individualsIterator.hasNext()) {
          RDFIndividual individual = (RDFIndividual)individualsIterator.next();
          allDifferentRestrictionInfo.addIndividualName(individual.getName());
        } // while
        importedRestrictions.add(allDifferentRestrictionInfo);
      } // while
    } // if
  } // importOWLAllDifferents

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

  private void exportOWLClasses() throws SWRLRuleEngineBridgeException
  {
      for (ClassInfo classInfo : importedClasses.values()) exportOWLClass(classInfo);
  } // exportOWLClasses

  private void exportOWLClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException
  {
    String className = classInfo.getClassName();
    Set<String> superClassNames = classInfo.getDirectSuperClassNames();
    
    if (!exportedClassNames.contains(className)) { // See if it is already defined.
      if (!superClassNames.isEmpty()) { // Superclasses must be defined before subclasses.

	  for (String superClassName : superClassNames) {
	      ClassInfo superClassInfo = importedClasses.get(superClassName);
	      exportOWLClass(superClassInfo);
	  } // for
      } // if
      defineClass(classInfo);
      exportedClassNames.add(className);
    } // if
  } // exportOWLClass

  private void exportOWLProperties() throws SWRLRuleEngineBridgeException
  {
      for (PropertyInfo propertyInfo : importedProperties) {
	  String propertyName = propertyInfo.getPropertyName();
	  defineProperty(propertyInfo);
      } // for
  } // exportOWLProperties

  private void exportOWLIndividuals() throws SWRLRuleEngineBridgeException
  {
      for (IndividualInfo individualInfo : importedIndividuals.values()) {
	  String individualName = individualInfo.getIndividualName();
	  if (exportedIndividualNames.contains(individualName)) continue;
	  defineIndividual(individualInfo);
	  exportedIndividualNames.add(individualName);
      } // for
  } // exportOWLIndividuals

  private void exportOWLRestrictions() throws SWRLRuleEngineBridgeException
  {
    for (RestrictionInfo restrictionInfo : importedRestrictions) defineRestriction(restrictionInfo);
  } // exportOWLRestrictions

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

  private void checkBuiltInMethodSignature(String ruleName, String namespaceName, String builtInMethodName, Method method) 
      throws IncompatibleBuiltInMethodException
  {
    Class exceptionTypes[];
    Type parameterTypes[];

    if (method.getReturnType() != Boolean.TYPE) 
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, "Method does not return a boolean.");

    exceptionTypes = method.getExceptionTypes();

    if ((exceptionTypes.length != 1) || (exceptionTypes[0] != BuiltInException.class))
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, 
                                                   "Method must throw a single exception of type BuiltInException");

    parameterTypes = method.getGenericParameterTypes();

    if ((parameterTypes.length != 1) || (!(parameterTypes[0] instanceof ParameterizedType)) || 
        (((ParameterizedType)parameterTypes[0]).getRawType() != List.class) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments().length != 1) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments()[0] != Argument.class))
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, 
                                                   "Method must accept a single List of Argument objects");
  } // checkBuiltInMethodSignature

  private void checkBuiltInMethodsClassCompatibility(String ruleName, String namespaceName, Class cls) throws IncompatibleBuiltInClassException
  {
    if (!SWRLBuiltInLibrary.class.isAssignableFrom(cls)) 
      throw new IncompatibleBuiltInClassException(ruleName, namespaceName, cls.getName(), "Class does not implement SWRLBuiltInLibrary.");
  } // checkBuiltInMethodsClassCompatibility

  private void checkForUnboundArgument(String ruleName, String builtInName, List<Argument> arguments) throws BuiltInException
  {
    if (arguments.contains(null)) // An unbound argument is indicated by a null for an argument value.
      throw new BuiltInException("Built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                 "returned with unbound argument number " + arguments.indexOf(null) + ".");
  } // checkForUnboundArgument
  
} // SWRLRuleEngineBridge
