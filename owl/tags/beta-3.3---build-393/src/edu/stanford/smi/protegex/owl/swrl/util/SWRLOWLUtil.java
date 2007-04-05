
package edu.stanford.smi.protegex.owl.swrl.util;

import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;

import edu.stanford.smi.protege.util.*;

import com.hp.hpl.jena.util.FileUtils;

import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * Class that wraps some common Protege-OWL API methods and throws meaningful exceptions when errors are encountered.
 *
 * Covers only a small number of basic method at the moment.
 */
public class SWRLOWLUtil
{
  public static JenaOWLModel createJenaOWLModel(String owlFileName) throws SWRLOWLUtilException
  {
    JenaOWLModel owlModel = null;

    try {
      owlModel = ProtegeOWL.createJenaOWLModelFromURI(new File(owlFileName).toURI().toString());
    } catch (Exception e) {
      throwException("Error opening OWL file '" + owlFileName + "': " + e.getMessage());
    } // try

    return owlModel;
  }  // createJenaOWLModel

  public static JenaOWLModel createJenaOWLModel() throws SWRLOWLUtilException
  {
    JenaOWLModel owlModel = null;

    try {
      owlModel = ProtegeOWL.createJenaOWLModel();
    } catch (Exception e) {
      new SWRLOWLUtilException("Error creating Jena OWL model: " + e.getMessage());
    } // try

    return owlModel;
  } // createJenaOWLModel

  public static void importOWLFile(JenaOWLModel owlModel, String importOWLFileName) throws SWRLOWLUtilException
  {
    try {
      ImportHelper importHelper = new ImportHelper(owlModel);
      URI importUri = URIUtilities.createURI(new File(importOWLFileName).toURI().toString());
      importHelper.addImport(importUri);
      importHelper.importOntologies(false);
    } catch (Exception e) {
      new SWRLOWLUtilException("Error importing OWL file '" + importOWLFileName + "': " + e.getMessage());
    } // try
  } // importOWLFile

  public static void writeJenaOWLModel2File(JenaOWLModel owlModel, String outputOWLFileName) throws SWRLOWLUtilException
  {
    ArrayList errors = new ArrayList();
    URI outputURI = URIUtilities.createURI(new File(outputOWLFileName).toURI().toString());
    owlModel.save(outputURI, FileUtils.langXMLAbbrev, errors);
    if (errors.size() != 0) throwException("Error creating output OWL file '" + outputOWLFileName + "': " + errors);
  } // writeJenaOWLModel2File
  
  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className)
    throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, className, null);
  } // createIndividualOfClass

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className, String individualName)
    throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className);

    OWLIndividual individual = cls.createOWLIndividual(individualName);

    if (individual == null) throwException("Could not create individual '" + individualName + "' of class '" + className + "'");

    return individual;
  } // createIndividualOfClass

  public static OWLNamedClass getClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = owlModel.getOWLNamedClass(className);

    if (mustExist && cls == null) throwException("No '" + className + "' class in ontology");

    return cls;
  } // getClass

  public static OWLNamedClass getClass(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    return getClass(owlModel, className, true);
  } // getClass

  public static boolean isIndividualOfClass(OWLModel owlModel, String individualName, String className) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className);
    OWLIndividual individual = getIndividual(owlModel, individualName);

    return individual.hasRDFType(cls, true);
  } // getClass

  public static OWLIndividual getIndividual(OWLModel owlModel, OWLNamedClass cls, boolean mustExist, int mustHaveExactlyN)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual = null;

    if (mustExist && cls.getInstanceCount(true) == 0) 
      throwException("No individuals of class '" + cls.getName() + "' in ontology");
    else if (cls.getInstanceCount(true) != mustHaveExactlyN) 
      throwException("Expecting exactly " + mustHaveExactlyN + " individuals of class '" + cls.getName() + "' in ontology. Got " +
                     cls.getInstanceCount(true) + "");

    return (OWLIndividual)cls.getInstances(true).iterator().next();
  } // getIndividual

  public static Set<OWLIndividual> getIndividuals(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className);

    return new HashSet<OWLIndividual>(getIndividuals(cls));
  } // getIndividuals

  public static Set<OWLIndividual> getIndividuals(OWLNamedClass cls) throws SWRLOWLUtilException
  {
    return new HashSet<OWLIndividual>(cls.getInstances(true));
  } // getIndividuals

  public static OWLProperty getProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyName);

    if (mustExist && property == null) throwException("No '" + propertyName + "' property in ontology");

    return property;
  } // getProperty

  public static OWLProperty getProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return getProperty(owlModel, propertyName, true);
  } // getProperty

  public static OWLDatatypeProperty getDatatypeProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyName);
    if (mustExist && property == null) throwException("No '" + propertyName + "' datatype property in ontology");

    return property;
  } // getDatatypeProperty

  public static OWLObjectProperty getObjectProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyName);

    if (mustExist && property == null) throwException("No '" + propertyName + "' object property in ontology");

    return property;
  } // getObjectProperty

  public static boolean isEquivalentProperty(OWLModel owlModel, String propertyName1, String propertyName2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property1 = getProperty(owlModel, propertyName1, mustExist);
    OWLProperty property2 = getProperty(owlModel, propertyName2, mustExist);

    return (property1 != null && property2 != null && property1.getEquivalentProperties().contains(property2));
  } // isEquivalentProperty

  public static boolean isEquivalentClass(OWLModel owlModel, String className1, String className2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass class1 = getClass(owlModel, className1, mustExist);
    OWLNamedClass class2 = getClass(owlModel, className2, mustExist);

    return (class1 != null && class2 != null && class1.hasEquivalentClass(class2));
  } // isEquivalentClass

  public static boolean isDisjointClass(OWLModel owlModel, String className1, String className2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass class1 = getClass(owlModel, className1, mustExist);
    OWLNamedClass class2 = getClass(owlModel, className2, mustExist);

    return (class1 != null && class2 != null && class1.getDisjointClasses().contains(class2));
  } // isDisjointClass

  public static boolean isSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty subProperty = getProperty(owlModel, subPropertyName, mustExist);
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, true));
  } // isSubPropertyOf

  public static boolean isSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty superProperty = getProperty(owlModel, superPropertyName, mustExist);
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, true)); // No isSuperpropertyOf call
  } // isSuperPropertyOf

  public static boolean isDirectSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty superProperty = getProperty(owlModel, superPropertyName, mustExist);
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, false)); // No isSuperpropertyOf call
  } // isDirectSuperPropertyOf

  public static boolean isDirectSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty subProperty = getProperty(owlModel, subPropertyName, mustExist);
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, false));
  } // isDirectSubPropertyOf

  public static boolean isDirectSubClassOf(OWLModel owlModel, String subClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass subClass = getClass(owlModel, subClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (subClass != null && cls != null && subClass.isSubclassOf(cls));
  } // isDirectSubclassOf

  public static boolean isSubClassOf(OWLModel owlModel, String subClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass subClass = getClass(owlModel, subClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (subClass != null && cls != null && cls.getSubclasses().contains(subClass));
  } // isDirectSubclassOf

  public static boolean isDirectSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (superClass != null && cls != null && cls.isSubclassOf(superClass)); // No isSuperclassOf call
  } // isDirectSuperclassOf

  public static boolean isSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (superClass != null && cls != null && cls.getSuperclasses(true).contains(superClass));
  } // isSuperclassOf

  public static int getNumberOfIndividualsOfClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className, mustExist);
    int numberOfIndividuals = 0;

    if (cls != null) numberOfIndividuals = cls.getInstances(true).size();

    return numberOfIndividuals;
  } //  getNumberOfInstancesOfClass

  public static int getNumberOfDirectInstancesOfClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className, mustExist);
    int numberOfIndividuals = 0;

    if (cls != null) numberOfIndividuals = cls.getInstances(false).size();

    return numberOfIndividuals;
  } //  getNumberOfDirectInstancesOfClass

  public static boolean isConsistentClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return cls != null && cls.isConsistent();
  } //  getNumberOfDirectInstancesOfClass

  public static boolean isInPropertyDomain(OWLModel owlModel, String propertyName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (property != null && cls != null && property.getDomains(true).contains(cls));
  } // isInPropertyDomain

  public static boolean isInDirectPropertyDomain(OWLModel owlModel, String propertyName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (property != null && cls != null && property.getDomains(false).contains(cls));
  } // isInDirectPropertyDomain

  public static boolean isInPropertyRange(OWLModel owlModel, String propertyName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (property != null && cls != null && property.getRanges(true).contains(cls));
  } // isInPropertyRange

  public static boolean isInDirectPropertyRange(OWLModel owlModel, String propertyName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (property != null && cls != null && property.getRanges(false).contains(cls));
  } // isInDirectPropertyRange

  public static boolean isObjectProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (property != null && property.isObjectProperty());
  } // isObjectProperty

  public static boolean isObjectProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return isObjectProperty(owlModel, propertyName, true);
  } // isObjectProperty

  public static boolean isTransitiveProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isTransitive());
  } // isTransitiveProperty

  public static boolean isSymmetricProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isSymmetric());
  } // isSymmetricProperty

  public static boolean isFunctionalProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return property != null && property.isFunctional();
  } // isFunctionalProperty

  public static boolean isAnnotationProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return property != null && property.isAnnotationProperty();
  } // isAnnotationProperty

  public static boolean isInverseFunctionalProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return property != null && property.isInverseFunctional();
  } // isInverseFunctionalProperty

  public static OWLIndividual getIndividual(OWLModel owlModel, String individualName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLIndividual individual = owlModel.getOWLIndividual(individualName);

    if (mustExist && individual == null) throwException("No individual named '" + individualName + "' in ontology");

    return individual;
  } // getIndividual

  public static OWLIndividual getIndividual(OWLModel owlModel, String individualName) throws SWRLOWLUtilException
  {
    return getIndividual(owlModel, individualName, true);
  } // getIndividual

  public static boolean isClassName(OWLModel owlModel, String className)
  {
    return (className != null && owlModel.getRDFResource(className) instanceof OWLNamedClass);
  } // isClassName

  public static boolean isPropertyName(OWLModel owlModel, String propertyName)
  {
    return (propertyName != null && owlModel.getRDFResource(propertyName) instanceof OWLProperty);
  } // isPropertyName

  public static boolean isIndividualName(OWLModel owlModel, String individualName)
  {
    return (individualName != null && owlModel.getRDFResource(individualName) instanceof OWLIndividual);
  } // isIndividualName

  public static int getNumberOfPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.

    return individual.getPropertyValues(property).size();
  } // getNumberOfPropertyValues

  public static int getNumberOfPropertyValues(OWLModel owlModel, String individualName, 
                                              String propertyName, Object propertyValue, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
    int numberOfPropertyValues = 0;

    if (propertyValue == null) throwException("Null value for property '" + propertyName + "' for OWL individual '" + individualName + "'");

    for (Object value : individual.getPropertyValues(property)) {
      if (value instanceof RDFResource) {
        RDFResource resource = (RDFResource)value;
        String name = resource.getName();
        if (name.equals(propertyValue)) numberOfPropertyValues++;
      } else {
        if (value.equals(propertyValue)) numberOfPropertyValues++;
      } // if
    } // for

    return numberOfPropertyValues;
  } // getNumberOfPropertyValues

  public static void addPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, Object propertyValue) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyName);

    if (individual == null) throwException("Null value for individual");
    if (property == null) throwException("No '" + propertyName + "' property in ontology");
    if (propertyValue == null) throwException("Null value for property '" + propertyName + "' for OWL individual '" + individual.getName() + "'");

    individual.addPropertyValue(property, propertyValue);
  } // addPropertyValue

  public static OWLIndividual getObjectPropertyValue(OWLIndividual individual, OWLProperty property) throws SWRLOWLUtilException
  { 
    OWLIndividual propertyValue = (OWLIndividual)individual.getPropertyValue(property);

    return propertyValue;
  } // getObjectPropertyValue

  public static Set<OWLIndividual> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individual, propertyName, false);
  } // getObjectPropertyValues

  public static Set<OWLIndividual> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.

    if (property == null) return new HashSet<OWLIndividual>();
    else return new HashSet<OWLIndividual>(individual.getPropertyValues(property));
  } // getObjectPropertyValues

  public static OWLIndividual getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, true);

    OWLIndividual propertyValue = (OWLIndividual)individual.getPropertyValue(property);

    if (mustExist && propertyValue == null) {
      throwException("No property '" + propertyName + "' associated with individual '" + individual.getName() + "'");
    } // if    

    return propertyValue;
  } // getObjectPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, true);
    Object propertyValue = individual.getPropertyValue(property);

    if (mustExist && propertyValue == null) {
      throwException("No property '" + propertyName + "' associated with individual '" + individual.getName() + "'");
    } // if    

    return propertyValue;
  } // getDatavaluedPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    Object propertyValue = individual.getPropertyValue(property);

    if (mustExist && propertyValue == null) {
      throwException("No property '" + property.getName() + "' associated with individual '" + individual.getName() + "'");
    } // if    

    return propertyValue;
  } // getDatavaluedPropertyValue

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, mustExist);
    int result = -1;

    try {
      result = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new SWRLOWLUtilException("Cannot convert property value '" + s + "' of property '" + propertyName + 
                                     "' associated with individual '" + individual.getName() + "' to integer");
    } // try
    return result;
  } // getDatavaluedPropertyValueAsInteger

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
  } // getDatavaluedPropertyValueAsInteger

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsInteger(owlModel, individual, propertyName, true);
  } // getDatavaluedPropertyValueAsInteger

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualName), propertyName, true);
  } // getDatavaluedPropertyValueAsInteger

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, mustExist);
    long result = -1;

    try {
      result = Long.parseLong(s);
    } catch (NumberFormatException e) {
      throw new SWRLOWLUtilException("Cannot convert property value '" + s + "' of property '" + propertyName + 
                                     "' associated with individual '" + individual.getName() + "' to long");
    } // try
    return result;
  } // getDatavaluedPropertyValueAsLong

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
  } // getDatavaluedPropertyValueAsLong

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsLong(owlModel, individual, propertyName, true);
  } // getDatavaluedPropertyValueAsLong

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualName), propertyName, true);
  } // getDatavaluedPropertyValueAsLong

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    if (property == null) return null;

    return getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);
  } // getDatavaluedPropertyValueAsString

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName, 
                                                          boolean mustExist, String defaultValue)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist, defaultValue);
  } // getDatavaluedPropertyValueAsString

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, true);
  } // getDatavaluedPropertyValueAsString

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualName, String propertyName, 
                                                          boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
  } // getDatavaluedPropertyValueAsString
    
  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName, 
                                                          boolean mustExist, String defaultValue)
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    String propertValueAsString;

    if (property == null) return defaultValue;

    propertValueAsString = getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);

    return propertValueAsString == null ? defaultValue : propertValueAsString;
  } // getDatavaluedPropertyValueAsString

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);
    String result = null;

    if (propertyValue instanceof Boolean) {
      Boolean b = (Boolean)propertyValue;
      if (b.booleanValue()) result = "true"; else result = "false";
    } if (propertyValue == null) {
      result = null;
    } else result = propertyValue.toString();

    return result;
  } // getDatavaluedPropertyValueAsString

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualName), propertyName, mustExist);
  } // getDatavaluedPropertyValueAsBoolean

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualName), propertyName, true);
  } // getDatavaluedPropertyValueAsBoolean

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyName, mustExist);

    if (propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean)) {
      throwException("Property value for '" + propertyName + "' associated with individual '" + individual.getName() 
                                 + "' is not a Boolean");
    } // if

    return (Boolean)propertyValue;
  } // getDatavaluedPropertyValueAsBoolean

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);

    if (propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean))
      throwException("Property value for " + property.getName() + " in individual " + individual.getName() + " is not a Boolean");

    return (Boolean)propertyValue;
  } // getDatavaluedPropertyValueAsBoolean

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsCollection(owlModel, individual, propertyName, false);
  } // getDatavaluedPropertyValueAsCollection

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsCollection(owlModel, getIndividual(owlModel, individualName), propertyName, true);
  } // getDatavaluedPropertyValueAsCollection

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyName,
                                                                  boolean mustExist) throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyName, mustExist);
    Collection result = new ArrayList();

    if (propertyValue == null) return result;

    if (propertyValue instanceof RDFSLiteral) result.add(propertyValue);
    else if (propertyValue instanceof Collection) result = (Collection)propertyValue;
    else throwException("Property value for '" + propertyName + "' associated with individual '" + individual.getName() + 
                        "' is not a Collection");

    return result;
  } // getDatavaluedPropertyValueAsCollection

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, 
                                                                  OWLProperty property, boolean mustExist) throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);
    Collection result = new ArrayList();

    if (propertyValue == null) return result;

    if (propertyValue instanceof RDFSLiteral) result.add(propertyValue);
    else if (propertyValue instanceof Collection) result = (Collection)propertyValue;
    else throwException("Property value for '" + property.getName() + "' associated with individual '" + individual.getName() + 
                        "' is not a Collection");

    return result;
  } // getDatavaluedPropertyValueAsCollection

  public static List<OWLNamedClass> getDirectSubClassesOf(OWLModel owlModel, String className) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getClass(owlModel, className);

    return new ArrayList<OWLNamedClass>(cls.getNamedSubclasses(false));
  } // getDirectSubClassesOf

  public static List<OWLNamedClass> getSubClassesOf(OWLModel owlModel, String className) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getClass(owlModel, className);

    return new ArrayList<OWLNamedClass>(cls.getNamedSubclasses(true));
  } // getSubClassesOf

  public static List<OWLNamedClass> getDirectSuperClassesOf(OWLModel owlModel, String className) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getClass(owlModel, className);

    return new ArrayList<OWLNamedClass>(cls.getNamedSuperclasses(false));
  } // getDirectSuperClassesOf

  public static List<OWLNamedClass> getSuperClassesOf(OWLModel owlModel, String className) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getClass(owlModel, className);

    return new ArrayList<OWLNamedClass>(cls.getNamedSuperclasses(true));
  } // getSuperClassesOf

  public static List<OWLProperty> getDirectSubPropertiesOf(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName);

    return new ArrayList<OWLProperty>(property.getSubproperties(false));
  } // getDirectSubPropertiesOf

  public static List<OWLProperty> getSubPropertiesOf(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName);

    return new ArrayList<OWLProperty>(property.getSubproperties(true));
  } // getSubPropertiesOf

  public static List<OWLProperty> getDirectSuperPropertiesOf(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName);

    return new ArrayList<OWLProperty>(property.getSuperproperties(false));
  } // getDirectSuperPropertiesOf

  public static List<OWLProperty> getSuperPropertiesOf(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName);

    return new ArrayList<OWLProperty>(property.getSuperproperties(true));
  } // getSuperPropertiesOf

  public static Set<String> rdfResources2Names(Collection<RDFResource> resources) 
  {
    Set<String> result = new HashSet<String>();
    
    // TODO: bug in Property.getUnionDomain that causes it to return non RDFResource objects so we need to work around it.
    // for (RDFResource resource : resources) result.add(resource.getName());

    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof RDFResource) result.add(((RDFResource)o).getName());
    } // if

    return result;
  } // rdfResources2Names            

  public static Set<String> rdfResources2NamesList(Collection<RDFResource> resources) throws SWRLOWLUtilException
  {
    RDFResource resource;
    Set<String> result = new HashSet<String>();

    if (resources == null) return result;
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();

      if (!(object instanceof RDFResource)) throwException("rdfResources2NamesList passed non-resource object '" + object + "'");

      resource = (RDFResource)object;
      result.add(resource.getName());
    } // while
    return result;
  } // rdfResources2NamesList            

  private static void throwException(String message) throws SWRLOWLUtilException
  {
    throw new SWRLOWLUtilException(message);
  } // throwException


} // SWRLOWLUtil
