
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

    if (individual == null) throwException("Could not create individual '" + individualName + "' of class '" + className + "'.");

    return individual;
  } // createIndividualOfClass

  public static OWLNamedClass getClass(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = owlModel.getOWLNamedClass(className);

    if (cls == null) throwException("No '" + className + "' class in ontology.");

    return cls;
  } // getClass

  public static OWLIndividual getIndividual(OWLModel owlModel, OWLNamedClass cls, boolean mustExist, int mustHaveExactlyN)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual = null;

    if (mustExist && cls.getInstanceCount(true) == 0) 
      throwException("No individuals of class '" + cls.getName() + "' in ontology.");
    else if (cls.getInstanceCount(true) != mustHaveExactlyN) 
      throwException("Expecting exactly " + mustHaveExactlyN + " individuals of class '" + cls.getName() + "' in ontology. Got " +
                     cls.getInstanceCount(true) + ".");

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
    if (mustExist && property == null) throwException("No '" + propertyName + "' property in ontology.");

    return property;
  } // getProperty

  public static OWLIndividual getIndividual(OWLModel owlModel, String individualName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual;

    individual = owlModel.getOWLIndividual(individualName);
    if (mustExist && individual == null) throwException("No individual named '" + individualName + "' in ontology.");

    return individual;
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

  public static int getNumberOfClassIndividuals(OWLModel owlModel, String className) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getClass(owlModel, className);

    return cls.getInstances(true).size();
  } // getNumberOfClassIndividuals

  public static void addPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, Object propertyValue) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyName);

    if (property == null) throwException("No '" + propertyName + "' property in ontology.");

    individual.addPropertyValue(property, propertyValue);
  } // addPropertyValue

  public static OWLIndividual getObjectPropertyValue(OWLIndividual individual, OWLProperty property) throws SWRLOWLUtilException
  { 
    OWLIndividual propertyValue;

    propertyValue = (OWLIndividual)individual.getPropertyValue(property);

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
      throwException("No property '" + propertyName + "' associated with individual '" + individual.getName() + "'.");
    } // if    

    return propertyValue;
  } // getObjectPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, true);
    Object propertyValue = individual.getPropertyValue(property);

    if (mustExist && propertyValue == null) {
      throwException("No property '" + propertyName + "' associated with individual '" + individual.getName() + "'.");
    } // if    

    return propertyValue;
  } // getDatavaluedPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    Object propertyValue = individual.getPropertyValue(property);

    if (mustExist && propertyValue == null) {
      throwException("No property '" + property.getName() + "' associated with individual '" + individual.getName() + "'.");
    } // if    

    return propertyValue;
  } // getDatavaluedPropertyValue

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);
  } // getDatavaluedPropertyValueAsString

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);
    String result = null;

    if (!mustExist && propertyValue == null) return null;

    if (propertyValue instanceof Boolean) {
      Boolean b = (Boolean)propertyValue;
      if (b.booleanValue()) result = "true"; else result = "false";
    } else result = propertyValue.toString();

    return result;
  } // getDatavaluedPropertyValueAsString

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyName, mustExist);

    if (!mustExist && propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean)) {
      throwException("Property value for '" + propertyName + "' associated with individual '" + individual.getName() 
                                 + "' is not a Boolean.");
    } // if

    return (Boolean)propertyValue;
  } // getDatavaluedPropertyValueAsBoolean

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);

    if (propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean))
      throwException("Property value for " + property.getName() + " in individual " + individual.getName() + " is not a Boolean.");

    return (Boolean)propertyValue;
  } // getDatavaluedPropertyValueAsBoolean

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsCollection(owlModel, individual, propertyName, false);
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
                                    "' is not a Collection.");

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
                        "' is not a Collection.");

    return result;
  } // getDatavaluedPropertyValueAsCollection

  public static Set<String> rdfResources2NamesList(Collection resources) throws SWRLOWLUtilException
  {
    RDFResource resource;
    Set<String> result = new HashSet<String>();

    if (resources == null) return result;
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();

      if (!(object instanceof RDFResource)) throwException("rdfResources2NamesList passed non-resource object '" + object + "'.");

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
