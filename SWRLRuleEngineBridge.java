
// TODO: debug dynamic built-in loading
// TODO: check that built-in methods in the Impl class throw BuiltInException
// TODO: BuiltIns
// TODO: DataRange

// SWRLRuleEngineBridge
//
// This class provides a bridge between an OWL model with SWRL rules and a rule engine. Its goal is to provide the infrastructure necessary
// to incorporate rule engines into Protege-OWL to execute SWRL rules. The bridge provides mechanisms to: (1) import SWRL rules and
// relevant OWL classes, individuals and properties from an OWL model; (2) write that knowledge to a rule engine; (3) allow the rule engine
// to perform inference and to assert its new knowledge back to the bridge; and (4) insert that asserted knowledge into an OWL model. An
// implementation will subclass this class to provide methods to represent SWRL rules, and OWL classes, properties and individuals within
// the target rule engine and will also provide a method to peform inference using that knowledge. See "Specializing the Bridge for a Target
// Rule Engine" comment below for an explanation of how to specialize this class.
//
// The following public methods can be used to interact with this class:
// 
// SWRLRuleEngineBridge: Constructor that takes an OWL model.
//
// importSWRLRulesAndOWLKnowledge: Import all SWRL rules and relevant OWL knowledge from the OWL model.
//
// exportSWRLRulesAndOWLKnowledge: Write imported rules and OWL knowledge to the target rule system.
//
// runRuleEngine: This is an abstract method and must be defined by the target implementation for a particular rule engine to perform
// inference using the rules and knowledge exported to the rule engine. The rule engine will generate new inferences and supply them to the
// bridge.
//
// writeAssertedIndividualsAndProperties2OWL: Transfer the asserted knowledge to the OWL model.
//
// resetBridge: Clear all knowledge from the bridge and the target rule system.
//
// resetRuleEngine: Clear all exported knowledge from the target rule system and reset it. Any assertions made by the rule engine are also
// removed from the bridge.

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
  // Specializing the Bridge for a Target Rule Engine
  // 
  // A target implementation for a particular rule engine must define the six abstract methods listed below. The class
  // edu.stanford.smi.protegex.owl.swrl.jess.SWRLJessBridge in the standard Protege-OWL distribution provides an example implementation for
  // the Jess rule engine.
  //
  // Internally, the bridge uses Info objects to store generic representations of all rules and relevant OWL knowledge. This representation
  // is used to bridge between SWRL rules and OWL knowledge and the representation used by the target rule engine. A target implementation
  // must be able to take Info objects for SWRL rules and OWL classes, properties and individuals and represent them in the rule engine's
  // native format.  A following four methods must be implemented to perform this task. Each "define" methods must take an Info object
  // of the appropriate type and produces an internal representation of that object.

  protected abstract void defineRule(RuleInfo ruleInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException;
  protected abstract void defineIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException;

  // The initializeRuleEngine method must prepare the rule engine. It may be called multiple times so should also act as a reset method.
  protected abstract void initializeRuleEngine() throws SWRLRuleEngineBridgeException;

  // Finally, the runRuleEngine method performs inference. 
  public abstract void runRuleEngine() throws SWRLRuleEngineBridgeException;

  // As inference is carried out in the runRuleEngine method, new knowledge will be generated. This knowledge will consist of class
  // membership assertions for existing individuals and new property assertions for existing individuals. These assertions can be passed to
  // the bridge using the assertProperty and assertIndividual methods.

  // assertProperty:
  //
  // Method called by a target rule engine when asserting a property. The propertyName is the name of the property; the subjectName is the
  // names of the individual to which this property will be attached; the predicate will be an individual name in the case of an object
  // property and a literal value in the case of a datatype property. String literal values do not have to be enclosed in quotes - the
  // bridge will be able to determine the type of the literal and assign it appropriately.

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

  // assertIndividual: 
  //
  // Method called by a target rule engine when asserting new new class membership information for an existing individual.
  public void assertIndividual(String individualName, String className) throws SWRLRuleEngineBridgeException 
  {
    IndividualInfo individualInfo = new IndividualInfo(individualName, className);
    assertedIndividuals.add(individualInfo); 
  } // assertIndividual

  // invokeSWRLBuiltIn:
  //
  // SWRL provides a built-ins, which are predicates that take a number of arguments. A target rule engine can decide to implement built-ins
  // natively for efficiency, or it can invoke Java-defined built-ins using the method invokeSWRLBuiltIn. A dynamic loading mechanism is
  // supported by the bridge to locate implementations of built-ins at run time. The rule engine can call invokeSWRLBuiltIn with the
  // namespace and the name of the builtIn (e.g., swrlb and greaterThan) and the bridge will attempt to resolve it to a Java method that
  // implements that built-in. If no Java method is found, an UnresolvedBuiltInException is thrown back to the invoking rule engine.
  // Similarly, if a built-in name does not resolve to a valid SWRL built-in, an InvalidBuiltInNameException is thrown. The arguments are a
  // list of Argument objects.  An Argument may be one of LiteralInfo, which stores literal data, and IndividualInfo, which contains the
  // name of an OWL individual (see definitions below).
  //
  // Users wishing to provide implementations of particular built-in methods need to define a class called SWRLBuiltInMethodsImpl that
  // contains the methods defining the appropriate built-ins. This class must implement the interface SWRLBuiltInMethods. This interface
  // acts as a structurng mechanism - it does not define any methods. The package name of the implementation class should be the namespace
  // qualifier of the built-ins preceded by 'edu.stanford.smi.protegex.owl.swrl.bridge.builtins'. For example, the standard SWRL built-in
  // greaterThan that is qualified by the namespace 'swrlb' should be defined as a method called 'greaterThan' in the class
  // SWRLBuiltInMethodsImpl located in the the edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb Java package. To ensure that
  // Protege-OWL can find this class at run time, a JAR containing this class should be placed in the Protege-OWL plugins directory. Protege
  // will automatically add this JAR file to the applications class path so that a class loader will be able to load this class.
  //
  // The implementation of a built-in method in the SWRLBuiltInMethods class should have a signature of the form:
  //
  // public static boolean <builtInName>(List arguments) throws BuiltInException
  //
  // The arguments parameter is a list of Argument objects. The method implementation should check that the correct number of arguments are
  // passed to the method and that the arguments are of the correct type. There is a utility class called SWRLBuiltInUtil in the
  // edu.stanford.smi.protegex.owl.swrl.bridge.builtins package that contains a large set of methods for argument processing for built-in
  // methods. An example SWRLBuiltInMethodsImpl class that implements most of the swrlb built-ins can be found in the
  // edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb package.
  //
  // The exception class BuiltInException has four concrete exception subclasses that can be thrown by a built-in method implementation:
  // InvalidBuiltInArgumentNumberException, InvalidBuiltInArgumentException, LiteralConversionException, and
  // BuiltInNotImplementedException. The InvalidBuiltInArgumentNumberException should be used to indicate that an incorrect number of
  // arguments have been passed to the built-in; InvalidBuiltInArgumentException should be used to indicate that an argument of the wrong
  // type has been passed; LiteralConversionException should be used to indicate that a literal argument is not of the correct type;
  // finally, BuiltInNotImplementedException should be used to indicate that a built-in (or variants of it for a particular argument type)
  // has not been implemnented. As mentioned above, the SWRLBuiltInUtil class has utility methods that can be used for to perfore argument
  // checking within built-ins.

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
  
  // Info classes are used to store generic representations of all SWRL rules and relevant OWL knowledge.  SWRL rules and OWL classes,
  // properties, and individuals are represented using these objects; components of SWRL rules, such as atoms, literals and variables, are
  // also represented. For example, the ClassInfo class is used to represent information about an OWL class. An implementation for a
  // specific rule engines must be able to translate these Info classes into the native format of the engine. These objects are stored in
  // the following collections and hash maps. 

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

  // See comments at beginning of this file for an explanation of the public methods in this class.

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
    System.err.println("importedPropertyNames: " + importedPropertyNames);
    System.err.println("importedProperties: " + importedProperties);
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
