
// TODO: debug dynamic built-in loading
// TODO: check that built-in methods in the Impl class throw BuiltInException
// TODO: BuiltIns
// TODO: DataRange

// SWRLRuleEngineBridge
//
// cf. http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ for detailed documentation of this class.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;

public abstract class SWRLRuleEngineBridge
{
  protected abstract void defineRule(RuleInfo ruleInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException;

  protected abstract void initializeRuleEngine() throws SWRLRuleEngineBridgeException;

  public abstract void runRuleEngine() throws SWRLRuleEngineBridgeException;

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

  public boolean invokeSWRLBuiltIn(String builtInName, List arguments) throws BuiltInException
  {
    SWRLBuiltInMethods swrlBuiltInMethods = null;
    Class swrlBuiltInMethodsClass = null;
    String namespaceName = "", builtInMethodName = "", className;
    Method method;
    int colonIndex;
    Boolean result = false;

    if (!isBuiltIn(builtInName)) throw new InvalidBuiltInNameException(builtInName);
    
    // First, find the class that defines the Java method (if we have not already cached it). 
    try { 
      colonIndex = builtInName.indexOf(':');
      if (colonIndex != -1) {
        namespaceName = builtInName.substring(0, colonIndex - 1);
        builtInMethodName = builtInName.substring(colonIndex + 1, builtInName.length());
        className = "edu.stanford.smi.protegex.owl.swrl.bridge.builtins." + namespaceName + ".SWRLBuiltInMethodsImpl";
      } else { // No namespace - try the base built-ins package. Ordinarily, built-ins should not be located here.
        namespaceName = "";
        builtInMethodName = builtInName;
        className = "edu.stanford.smi.protegex.owl.swrl.bridge.builtins.SWRLBuiltInMethodsImpl";
      } // if
      
      if (builtInMethodsClassInstances.containsKey(namespaceName)) {
        swrlBuiltInMethods = (SWRLBuiltInMethods)builtInMethodsClassInstances.get(namespaceName);
        swrlBuiltInMethodsClass = swrlBuiltInMethods.getClass();
      } else { // Class not loaded.
        String classpath = System.getProperty("java.class.path");
        
        ClassLoader loader = new URLClassLoader(new URL[] { new URL(classpath) });
        swrlBuiltInMethodsClass = loader.loadClass(className);

        if (swrlBuiltInMethodsClass.isInstance(swrlBuiltInMethods)) 
          swrlBuiltInMethods = (SWRLBuiltInMethods)swrlBuiltInMethodsClass.newInstance();
        else throw new InvalidBuiltInMethodsImplementationClass(className);

        builtInMethodsClassInstances.put(namespaceName, swrlBuiltInMethods);
      } // if
      method = swrlBuiltInMethodsClass.getMethod(builtInMethodName, new Class[] { List.class });
      result = (Boolean)method.invoke(swrlBuiltInMethods, new Object[] { arguments });
      
    } catch (Exception e) {
      throw new UnresolvedBuiltInException("Cannot invoke built-in method '" + builtInMethodName + "' in namespace '" 
                                           + namespaceName + "'. Exception: " + e.getMessage());
    } // try

    return result.booleanValue();
  } // invokeSWRLBuiltIn
  
  // RuleInfo objects representing imported SWRL rules.
  private List importedSWRLRules; 

  // Names of classes, properties and individuals explicitly referred to in SWRL rules. These are filled in as the SWRL rules are imported
  // and are used to determine the relevant OWL knowledge to import.
  private List referencedClassNames, referencedPropertyNames, referencedIndividualNames;

  // Info objects representing imported classes, properties, and individuals. 
  private HashMap importedClasses, importedIndividuals;
  private List importedProperties; 
  private List importedPropertyNames;

  // Names of classes, properties, and individuals that have been exported to target.
  private List exportedClassNames, exportedIndividualNames; 

  // Info objects representing asserted individuals and their class membership information. 
  private List assertedIndividuals, assertedProperties; 

  // Holds the OWL model that is associated with this bridge.
  private OWLModel owlModel;

  // Holds class instances implementing built-ins.
  private HashMap builtInMethodsClassInstances;

  protected SWRLRuleEngineBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    this.owlModel = owlModel;

    importedSWRLRules = new ArrayList();

    referencedClassNames = new ArrayList();
    referencedIndividualNames = new ArrayList();
    referencedPropertyNames = new ArrayList();

    importedClasses = new HashMap();
    importedIndividuals = new HashMap(); 
    importedProperties= new ArrayList(); 
    importedPropertyNames = new ArrayList();

    exportedClassNames = new ArrayList();
    exportedIndividualNames = new ArrayList();

    assertedIndividuals = new ArrayList(); 
    assertedProperties = new ArrayList(); 

    builtInMethodsClassInstances = new HashMap();

  } // SWRLRuleEngineBridge

  public void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    resetRuleEngine();

    if (!owlModel.getInconsistentClasses().isEmpty()) throw new InconsistentKnowledgeBaseException("Cannot import rules from an inconsistent knowledge base");

    importSWRLRules(); // Fills in importedSWRLRules, referencedClassNames, referencedPropertyNames, and referencedIndividualNames

    importOWLClasses(referencedClassNames); // Import all referenced classes (and their superclasses and subclasses).
    importOWLProperties(referencedPropertyNames); // Import all referenced individuals (and the necessary classes).
    importOWLIndividuals(referencedIndividualNames); // Import all referenced individuals (and their classes).

    importAllOWLIndividualsOfClasses(referencedClassNames); // Import all individuals that are members of imported classes.

  } // importSWRLRulesAndOWLKnowledge

  public void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineBridgeException
  {
    exportClasses();
    exportIndividuals();
    exportProperties();
    exportSWRLRules();
  } // exportSWRLRulesAndOWLKnowledge

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

    clearExportedAndAssertedKnowledge();

    resetRuleEngine();
  } // resetBridge

  public void resetRuleEngine() throws SWRLRuleEngineBridgeException
  {
    initializeRuleEngine();
    clearExportedAndAssertedKnowledge();
  } // resetRuleEngine

  // Convenience methods to display bridge activity.
  public int getNumberOfImportedSWRLRules() { return importedSWRLRules.size(); }
  public int getNumberOfImportedClasses() { return importedClasses.size(); }
  public int getNumberOfImportedIndividuals() { return importedIndividuals.size(); }
  public int getNumberOfImportedProperties() { return importedProperties.size(); }
  public int getNumberOfAssertedIndividuals() { return assertedIndividuals.size(); }
  public int getNumberOfAssertedProperties() { return assertedProperties.size(); }

  // Convenience methods for subclasses who may wish to display the contents of the bridge.
  protected List getImportedSWRLRules() { return importedSWRLRules; }
  protected List getImportedClasses() { return new ArrayList(importedClasses.values()); }
  protected List getImportedIndividuals() { return new ArrayList(importedIndividuals.values()); }
  protected List getImportedProperties() { return importedProperties; }
  protected List getAssertedIndividuals() { return assertedIndividuals; }
  protected List getAssertedProperties() { return assertedProperties; }

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
    RuleInfo ruleInfo;
    Iterator iterator;

    ruleInfo = new RuleInfo(rule.getName());

    iterator = rule.getHead().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      ruleInfo.addHeadAtom(processSWRLAtom(swrlAtom));
    } // while 

    iterator = rule.getBody().getValues().iterator();
    while (iterator.hasNext()) {
      SWRLAtom swrlAtom = (SWRLAtom)iterator.next();
      ruleInfo.addBodyAtom(processSWRLAtom(swrlAtom));
    } // while 

    importedSWRLRules.add(ruleInfo);
  } // importSWRLRule

  private AtomInfo processSWRLAtom(SWRLAtom swrlAtom) throws SWRLRuleEngineBridgeException
  {
    AtomInfo atomInfo;
    
    if (swrlAtom instanceof SWRLClassAtom) {
      atomInfo = new ClassAtomInfo((SWRLClassAtom)swrlAtom);
      if (!referencedClassNames.contains(atomInfo.getName())) referencedClassNames.add(atomInfo.getName());
    } else if (swrlAtom instanceof SWRLDatavaluedPropertyAtom) {
      atomInfo = new DatavaluedPropertyAtomInfo((SWRLDatavaluedPropertyAtom)swrlAtom);
      if (!referencedPropertyNames.contains(atomInfo.getName())) referencedPropertyNames.add(atomInfo.getName());
    } else if (swrlAtom instanceof SWRLIndividualPropertyAtom) {
      atomInfo = new IndividualPropertyAtomInfo((SWRLIndividualPropertyAtom)swrlAtom);
      if (!referencedPropertyNames.contains(atomInfo.getName())) referencedPropertyNames.add(atomInfo.getName());
    } else if (swrlAtom instanceof SWRLSameIndividualAtom) 
      atomInfo = new SameIndividualAtomInfo((SWRLSameIndividualAtom)swrlAtom);
    else if (swrlAtom instanceof SWRLDifferentIndividualsAtom) 
      atomInfo = new DifferentIndividualsAtomInfo((SWRLDifferentIndividualsAtom)swrlAtom);
    else if (swrlAtom instanceof SWRLBuiltinAtom) 
      atomInfo = new BuiltInAtomInfo((SWRLBuiltinAtom)swrlAtom);
    else if (swrlAtom instanceof SWRLDataRangeAtom) 
      atomInfo = new DataRangeAtomInfo((SWRLDataRangeAtom)swrlAtom);
    else throw new InvalidSWRLAtomException(swrlAtom.getBrowserText());

    if (atomInfo.hasReferencedIndividuals()) referencedIndividualNames.addAll(atomInfo.getReferencedIndividualNames());
    
    return atomInfo;
  } // processSWRLAtom
  
  private void importOWLClass(String className) throws SWRLRuleEngineBridgeException
  {
    ClassInfo classInfo;

    if (!importedClasses.containsKey(className)) {
      classInfo = new ClassInfo(owlModel, className);
      
      importedClasses.put(className, classInfo);

      importOWLClasses(classInfo.getDirectSuperClassNames());
      importOWLClasses(classInfo.getDirectSubClassNames());
    } // if
  } // importClass

  private void importOWLClasses(Collection classNames) throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = classNames.iterator();
    while (iterator.hasNext()) {
      String className = (String)iterator.next();
      importOWLClass(className);
    } // while
  } // importOWLClasses

  private void importAllOWLIndividualsOfClasses(Collection classNames) throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = classNames.iterator();
    while (iterator.hasNext()) {
      String className = (String)iterator.next();
      importAllOWLIndividualsOfClass(className);
    } // while
  } // importAllOWLIndividualsOfClasses

  private void importAllOWLIndividualsOfClass(String className) throws SWRLRuleEngineBridgeException
  {
    RDFSClass rdfsClass = owlModel.getRDFSNamedClass(className);
    if (rdfsClass == null) throw new InvalidClassNameException(className);

    Iterator iterator = rdfsClass.getInstances(true).iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLIndividual) { // TODO: what else would it be and why?
        OWLIndividual individual = (OWLIndividual)o;
        String individualName = individual.getName();
        IndividualInfo individualInfo = new IndividualInfo(owlModel, individualName);
        importedIndividuals.put(individualName, individualInfo);
      } // if
    } // while
  } // importAllOWLIndividualsOfClass
  
  private void importOWLProperties(Collection propertyNames) throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = propertyNames.iterator();

    while (iterator.hasNext()) {
      String propertyName = (String)iterator.next();
      importOWLProperty(propertyName);
    } // while
  } // importProperties

  private void importOWLProperty(String propertyName) throws SWRLRuleEngineBridgeException
  {
    if (!importedPropertyNames.contains(propertyName)) {
      List propertyInfoList = PropertyInfo.buildPropertyInfoList(owlModel, propertyName);
      importedProperties.addAll(propertyInfoList);
      importedPropertyNames.add(propertyName);
      
      if (!propertyInfoList.isEmpty()) {
        PropertyInfo propertyInfo = (PropertyInfo)propertyInfoList.get(0); // All info objects will hold the same domain and range classes.
        
        importOWLClasses(propertyInfo.getDomainClassNames());
        importOWLClasses(propertyInfo.getRangeClassNames());
      } // if
    } // if
  } // importProperty

  private void importOWLIndividuals(Collection individualNames) throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = individualNames.iterator();

    while (iterator.hasNext()) {
      String individualName = (String)iterator.next();
      importOWLIndividual(individualName);
    } // while
  } // importOWLIndividuals

  private void importOWLIndividual(String individualName) throws SWRLRuleEngineBridgeException
  {
    if (!importedIndividuals.containsKey(individualName)) return;
    
    IndividualInfo individualInfo = new IndividualInfo(owlModel, individualName);
    
    importedIndividuals.put(individualName, individualInfo);

    importOWLClasses(individualInfo.getClassNames());
  } // importedIndividual

  private void exportSWRLRules() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = importedSWRLRules.iterator();

    while (iterator.hasNext()) {
      RuleInfo ruleInfo = (RuleInfo)iterator.next();
      defineRule(ruleInfo);
    } // while
  } // exportSWRLRules

  private void exportClasses() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = importedClasses.values().iterator();

    while (iterator.hasNext()) {
      ClassInfo classInfo = (ClassInfo)iterator.next();
      exportClass(classInfo);
    } // while
  } // exportClasses

  // Superclasses must be defined before subclasses.
  private void exportClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException
  {
    String className = classInfo.getName();
    Collection superClassNames = classInfo.getDirectSuperClassNames();
    
    if (!exportedClassNames.contains(className)) { // See if it is already defined.
      if (!superClassNames.isEmpty()) {
        Iterator iterator = superClassNames.iterator();
        while (iterator.hasNext()) {
          String superClassName = (String)iterator.next();
          ClassInfo superClassInfo = (ClassInfo)importedClasses.get(superClassName);
          exportClass(superClassInfo);
        } // while
      } // if
      defineClass(classInfo);
      exportedClassNames.add(className);
    } // if
  } // exportClass

  private void exportProperties() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = importedProperties.iterator();

    while (iterator.hasNext()) {
      PropertyInfo propertyInfo = (PropertyInfo)iterator.next();
      String propertyName = propertyInfo.getName();
      defineProperty(propertyInfo);
    } // while
  } // exportProperties

  private void exportIndividuals() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = importedIndividuals.values().iterator();

    while (iterator.hasNext()) {
      IndividualInfo individualInfo = (IndividualInfo)iterator.next();
      String individualName = individualInfo.getName();
      if (exportedIndividualNames.contains(individualName)) continue;
      defineIndividual(individualInfo);
      exportedIndividualNames.add(individualName);
    } // while
  } // exportIndividuals

  private void writeAssertedProperties2OWL() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = assertedProperties.iterator();
    
    while (iterator.hasNext()) {
      PropertyInfo propertyInfo = (PropertyInfo)iterator.next();
      propertyInfo.write2OWL(owlModel);
    } // while
  } // writeAssertedProperties2OWL

  private void writeAssertedIndividuals2OWL() throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = assertedIndividuals.iterator();
    
    while (iterator.hasNext()) {
      IndividualInfo individualInfo = (IndividualInfo)iterator.next();
      individualInfo.write2OWL(owlModel);
    } // while
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
    OWLProperty property;

    property = owlModel.getOWLProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);

    return property.isObjectProperty();
  } // isObjectProperty
  
} // SWRLRuleEngineBridge
