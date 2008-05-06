
package edu.stanford.smi.protegex.owl.swrl.util;

import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
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
      throw new SWRLOWLUtilException("error creating Jena OWL model: " + e.getMessage());
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
      throwException("error importing OWL file '" + importOWLFileName + "': " + e.getMessage());
    } // try
  } // importOWLFile

  public static void writeJenaOWLModel2File(JenaOWLModel owlModel, String outputOWLFileName) throws SWRLOWLUtilException
  {
    ArrayList errors = new ArrayList();
    URI outputURI = URIUtilities.createURI(new File(outputOWLFileName).toURI().toString());
    owlModel.save(outputURI, FileUtils.langXMLAbbrev, errors);
    if (errors.size() != 0) throwException("error creating output OWL file '" + outputOWLFileName + "': " + errors);
  } // writeJenaOWLModel2File
  
  public static OWLNamedClass createOWLNamedClass(OWLModel owlModel, String className)
    throws SWRLOWLUtilException
  {
    OWLNamedClass cls = owlModel.createOWLNamedClass(className);

    if (cls == null) throw new SWRLOWLUtilException("cannot create OWL class '" + className + "'");

    return cls;
  } // createOWLNamedClass

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className)
    throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, className, null);
  } // createIndividualOfClass

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String className, String individualName)
    throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className);

    return createIndividualOfClass(owlModel, cls, individualName);
  } // createIndividualOfClass

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLNamedClass cls)
    throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, cls, null);
  } // createIndividualOfClass

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLNamedClass cls, String individualName)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual = cls.createOWLIndividual(individualName);

    if (individual == null) throwException("could not create individual '" + individualName + "' of class '" + cls.getName() + "'");

    return individual;
  } // createIndividualOfClass

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
    Collection instances;
    Object firstInstance;

    if (mustExist && cls.getInstanceCount(true) == 0) throwException("no individuals of class '" + cls.getName() + "' in ontology");
    else if (cls.getInstanceCount(true) != mustHaveExactlyN) 
      throwException("expecting exactly " + mustHaveExactlyN + " individuals of class '" + cls.getName() + "' in ontology - got " +
                     cls.getInstanceCount(true) + "");

    instances = cls.getInstances();

    if (!instances.isEmpty()) {
      firstInstance = cls.getInstances(true).iterator().next();

      if (firstInstance instanceof OWLIndividual) return (OWLIndividual)firstInstance;
      else throw new SWRLOWLUtilException("instance of class '" + cls.getName() + "' is not an OWL individual");
    } else return null;
  } // getIndividual

  public static Set<OWLIndividual> getAllIndividuals(OWLModel owlModel) throws SWRLOWLUtilException
  {
    return new HashSet<OWLIndividual>(owlModel.getOWLIndividuals());
  } // getAllIndividuals

  public static Set<OWLIndividual> getIndividuals(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getClass(owlModel, className);

    return new HashSet<OWLIndividual>(getIndividuals(cls));
  } // getIndividuals

  public static Set<OWLIndividual> getIndividuals(OWLNamedClass cls) throws SWRLOWLUtilException
  {
    return new HashSet<OWLIndividual>(cls.getInstances(true));
  } // getIndividuals

  public static OWLProperty getProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return getProperty(owlModel, propertyName, true);
  } // getProperty

  public static OWLDatatypeProperty getDatatypeProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyName);
    if (mustExist && property == null) throwException("no '" + propertyName + "' datatype property in ontology");

    return property;
  } // getDatatypeProperty

  public static OWLObjectProperty getObjectProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyName);

    if (mustExist && property == null) throwException("no '" + propertyName + "' object property in ontology");

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

    return (subClass != null && cls != null && cls.getSubclasses().contains(subClass)); // TODO: uses deprecated getSubclasses
  } // isDirectSubclassOf

  public static boolean isDirectSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (superClass != null && cls != null && cls.isSubclassOf(superClass)); // No isSuperclassOf call
  } // isDirectSuperclassOf

  public static boolean isSuperClassOf(OWLModel owlModel, String superClassName, String className) 
    throws SWRLOWLUtilException
  {
    return isSuperClassOf(owlModel, superClassName, className, true);
  } // isSuperClassOf

  public static boolean isSuperClassOf(OWLModel owlModel, String superClassName, String className, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getClass(owlModel, className, mustExist);

    return (superClass != null && cls != null && cls.getSuperclasses(true).contains(superClass));
  } // isSuperclassOf

  public static int getNumberOfIndividualsOfClass(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    return getNumberOfIndividualsOfClass(owlModel, className, true);
  } // getNumberOfIndividualsOfClass

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

  public static Set<OWLNamedClass> getDomainClasses(OWLModel owlModel, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDomainClasses(owlModel, propertyName, true, true);
  } // getDomainClasses

  public static Set<OWLNamedClass> getDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDomainClasses(owlModel, propertyName, mustExist, true);
  } // getDomainClasses

  public static Set<OWLNamedClass> getDirectDomainClasses(OWLModel owlModel, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDomainClasses(owlModel, propertyName, true, false);
  } // getDirectDomainClasses

  public static Set<OWLNamedClass> getDirectDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDomainClasses(owlModel, propertyName, mustExist, false);
  } // getDirectDomainClasses

  private static Set<OWLNamedClass> getDomainClasses(OWLModel owlModel, String propertyName, boolean mustExist, 
                                                     boolean includingSuperproperties) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
    Collection domainClasses = property.getUnionDomain(includingSuperproperties);
    Iterator iterator;

    if (domainClasses == null) return result;
    
    iterator = domainClasses.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLNamedClass) result.add((OWLNamedClass)o);
    } // while
    
    return result;
  } // getDomainClasses

  public static Set<OWLNamedClass> getRangeClasses(OWLModel owlModel, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getRangeClasses(owlModel, propertyName, mustExist, true);
  } // getRangeClasses

  public static Set<OWLNamedClass> getRangeClasses(OWLModel owlModel, String propertyName)
    throws SWRLOWLUtilException
  {
    return getRangeClasses(owlModel, propertyName, true, true);
  } // getRangeClasses

  public static Set<OWLNamedClass> getDirectRangeClasses(OWLModel owlModel, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getRangeClasses(owlModel, propertyName, mustExist, false);
  } // getDirectRangeClasses

  public static Set<OWLNamedClass> getDirectRangeClasses(OWLModel owlModel, String propertyName)
    throws SWRLOWLUtilException
  {
    return getRangeClasses(owlModel, propertyName, true, false);
  } // getDirectRangeClasses

  private static Set<OWLNamedClass> getRangeClasses(OWLModel owlModel, String propertyName, boolean mustExist, 
                                                    boolean includingSuperproperties) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);
    Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
    Collection rangeClasses = property.getUnionRangeClasses(); // TODO: no includingSuperproperties argument supported
    Iterator iterator;

    if (rangeClasses == null) return result;
    
    iterator = rangeClasses.iterator();
    while (iterator.hasNext()) {
      RDFResource resource = (RDFResource)iterator.next();
      if (resource instanceof OWLNamedClass) result.add((OWLNamedClass)resource);
    } // while
    
    return result;
  } // getRangeClasses

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

  public static boolean isOWLIndividual(OWLModel owlModel, String individualName)
  {
    return owlModel.getOWLIndividual(individualName) != null;
  } // isOWLIndividual

  public static boolean isObjectProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return isObjectProperty(owlModel, propertyName, true);
  } // isObjectProperty

  public static boolean isDatatypeProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (property != null && !property.isObjectProperty());
  } // isDatatypeProperty

  public static boolean isDatatypeProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return isDatatypeProperty(owlModel, propertyName, true);
  } // isDatatypeProperty

  public static boolean isTransitiveProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getProperty(owlModel, propertyName, mustExist);

    return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isTransitive());
  } // isTransitiveProperty

  public static boolean isTransitiveProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return isTransitiveProperty(owlModel, propertyName, true);
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
    RDFResource resource = owlModel.getRDFResource(individualName);

    if (mustExist && (resource == null || !(resource instanceof OWLIndividual))) 
      throwException("no individual named '" + individualName + "' in ontology");
        
    if (resource != null) {
      if (resource instanceof OWLIndividual) return (OWLIndividual)resource;
      else return null;
    } else return null;
  } // getIndividual

  public static OWLNamedClass getClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(className);

    if (mustExist && (resource == null || !(resource instanceof OWLNamedClass))) 
      throwException("no class named '" + className + "' in ontology");
        
    if (resource != null) {
      if (resource instanceof OWLNamedClass) return (OWLNamedClass)resource;
      else return null;
    } else return null;
  } // getClass

  public static Set<OWLNamedClass> getClassesOfIndividual(OWLModel owlModel, String individualName) throws SWRLOWLUtilException
  {
    return getClassesOfIndividual(owlModel, individualName, true);
  } // getClassesOfIndividual

  public static Set<OWLNamedClass> getClassesOfIndividual(OWLModel owlModel, String individualName, boolean mustExist) 
    throws SWRLOWLUtilException  
  {
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist);
    
    return individual == null ? new HashSet<OWLNamedClass>() : getClassesOfIndividual(owlModel, individual);
  } // getClassesOfIndividual

  public static Set<OWLNamedClass> getClassesOfIndividual(OWLModel owlModel, OWLIndividual individual) throws SWRLOWLUtilException
  {
    Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
    Collection types = individual.getRDFTypes();
    Iterator iterator = types.iterator();
    while (iterator.hasNext()) {
      RDFResource resource = (RDFResource)iterator.next();
      if (resource instanceof OWLNamedClass) result.add((OWLNamedClass)resource); // Ignore anonymous classes
    } // while

    return result;
  } // getClassesOfIndividual

  public static OWLProperty getProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(propertyName);

    if (mustExist && (resource == null || !(resource instanceof OWLProperty))) 
      throwException("no property named '" + propertyName + "' in ontology");
        
    return resource instanceof OWLProperty ? (OWLProperty)resource : null;
  } // getProperty

  public static OWLIndividual getIndividual(OWLModel owlModel, String individualName) throws SWRLOWLUtilException
  {
    return getIndividual(owlModel, individualName, true);
  } // getIndividual

  public static boolean isClass(OWLModel owlModel, String className) throws SWRLOWLUtilException
  {
    return isClass(owlModel, className, true);
  } // isClass

  public static boolean isClass(OWLModel owlModel, String className, boolean mustExist) throws SWRLOWLUtilException
  {
    return(getClass(owlModel, className, mustExist) != null);
  } // isClassName

  public static boolean isProperty(OWLModel owlModel, String propertyName) throws SWRLOWLUtilException
  {
    return isProperty(owlModel, propertyName, true);
  } // isProperty

  public static boolean isProperty(OWLModel owlModel, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getProperty(owlModel, propertyName, mustExist) != null);
  } // isProperty

  public static Collection<OWLNamedClass> getUserDefinedOWLNamedClasses(OWLModel owlModel) 
  { 
    return new ArrayList<OWLNamedClass>(owlModel.getUserDefinedOWLNamedClasses());
  } // getUserDefinedOWLNamedClasses

  public static Collection<OWLProperty> getUserDefinedOWLProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLProperties());
  } // getUserDefinedOWLProperties

  public static Collection<OWLProperty> getUserDefinedOWLObjectProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLObjectProperties());
  } // getUserDefinedOWLObjectProperties

  public static Collection<OWLProperty> getUserDefinedOWLDatatypeProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLDatatypeProperties());
  } // getUserDefinedOWLDatatypeProperties

  public static boolean isIndividual(OWLModel owlModel, String individualName, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getIndividual(owlModel, individualName, mustExist) != null);
  } // isIndividualName

  public static boolean isSWRLVariable(OWLModel owlModel, String individualName, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getIndividual(owlModel, individualName, mustExist) != null &&
            getIndividual(owlModel, individualName, mustExist) instanceof SWRLVariable);
  } // isIndividualName

  public static boolean isIndividual(OWLModel owlModel, String individualName) throws SWRLOWLUtilException
  {
    return (getIndividual(owlModel, individualName, true) != null);
  } // isIndividual

  public static int getNumberOfPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.

    return individual.getPropertyValues(property).size();
  } // getNumberOfPropertyValues

  public static Set<OWLProperty> getPropertiesOfIndividual(OWLModel owlModel, String individualName) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getIndividual(owlModel, individualName, true);
    HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
    Collection rdfProperties = individual.getRDFProperties();

    Iterator iterator = rdfProperties.iterator();
    while (iterator.hasNext()) {
      RDFProperty property = (RDFProperty)iterator.next();
      if (property instanceof OWLProperty) properties.add((OWLProperty)property);
    } // while

    return properties;
  } // getPropertiesOfIndividual

  public static Set<OWLProperty> getPossiblePropertiesOfIndividual(OWLModel owlModel, String individualName) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getIndividual(owlModel, individualName, true);
    HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
    Collection rdfProperties = individual.getPossibleRDFProperties();

    Iterator iterator = rdfProperties.iterator();
    while (iterator.hasNext()) {
      RDFProperty property = (RDFProperty)iterator.next();
      if (property instanceof OWLProperty) properties.add((OWLProperty)property);
    } // while

    return properties;
  } // getPossiblePropertiesOfIndividual

  public static String getURI(OWLModel owlModel, String resourceName) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(resourceName);
    
    if (resource == null) throwException("invalid resource '" + resourceName + "'");

    return resource.getURI();
  } // getURI

  public static int getNumberOfPropertyValues(OWLModel owlModel, String individualName, 
                                              String propertyName, Object propertyValue, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true
    int numberOfPropertyValues = 0;

    if (propertyValue == null) throwException("null value for property '" + propertyName + "' for OWL individual '" + individualName + "'");

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

    if (individual == null) throwException("null value for individual");
    if (property == null) throwException("no '" + propertyName + "' property in ontology");
    if (propertyValue == null) throwException("null value for property '" + propertyName + "' for OWL individual '" + individual.getName() + "'");

    individual.addPropertyValue(property, propertyValue);
  } // addPropertyValue

  public static Object getObjectPropertyValue(OWLIndividual individual, OWLProperty property) throws SWRLOWLUtilException
  { 
    return individual.getPropertyValue(property);
  } // getObjectPropertyValue

  public static Set<Object> getObjectPropertyValues(OWLModel owlModel, String individualName, String propertyName) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individualName, propertyName, false);
  } // getObjectPropertyValues

  public static Set<Object> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individual.getName(), propertyName, false);
  } // getObjectPropertyValues

  public static Set<Object> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individual.getName(), propertyName, mustExist);
  } // getObjectPropertyValues

  public static Set<Object> getObjectPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
    Set<Object> result = new HashSet<Object>();

    if (individual != null && property != null) {
      Iterator iterator = individual.getPropertyValues(property).iterator();
      while (iterator.hasNext()) result.add(iterator.next());
    } // if
    return result;
  } // getObjectPropertyValues

  public static Object getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  {
    return getObjectPropertyValue(owlModel, individual, propertyName, true);
  } // getObjectPropertyValue

  public static Object getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getObjectPropertyValue(owlModel, individual.getName(), propertyName, mustExist);
  } // getObjectPropertyValue

  public static Object getObjectPropertyValue(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getProperty(owlModel, propertyName, true);
    Object propertyValue = (property == null && individual == null ? null : individual.getPropertyValue(property));

    if (mustExist && property == null) {
      throwException("no property '" + propertyName + "' associated with individual '" + individual.getName() + "'");
    } // if    

    return propertyValue;
  } // getObjectPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getIndividual(owlModel, individualName, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getProperty(owlModel, propertyName, mustExist); // Will throw an exception if mustExist is true.
    Object propertyValue = (individual == null || property == null) ? null : individual.getPropertyValue(property);

    if (mustExist && propertyValue == null)
      throwException("no property '" + propertyName + "' associated with individual '" + individualName + "'");

    return propertyValue;
  } // getDatavaluedPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValue(owlModel, individual.getName(), propertyName, mustExist);
  } // getDatavaluedPropertyValue

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValue(owlModel, individual.getName(), property.getName(), mustExist);
  } // getDatavaluedPropertyValue

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getProperty(owlModel, propertyName, true);
    Collection propertyValues = (property == null ? null :individual.getPropertyValues(property));
    Set<Object> result = new HashSet<Object>();

    if (property.isObjectProperty()) 
      throwException("expecting datatype property '" + propertyName + "' for '" + individual.getName() + "'");

    if (mustExist && propertyValues == null) {
      throwException("no property '" + propertyName + "' associated with individual '" + individual.getName() + "'");
    } // if

    if (propertyValues != null) {
      Iterator iterator = propertyValues.iterator();
      while (iterator.hasNext()) result.add(iterator.next());
    } // if

    return new HashSet<Object>(propertyValues);
  } // getDatavaluedPropertyValues

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualName, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValues(owlModel, individualName, propertyName, mustExist);
  } // getDatavaluedPropertyValues

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualName, String propertyName)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValues(owlModel, individualName, propertyName, true);
  } // getDatavaluedPropertyValues

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyName, boolean mustExist)
    throws SWRLOWLUtilException
  {
    String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, mustExist);
    int result = -1;

    try {
      result = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throwException("cannot convert property value '" + s + "' of property '" + propertyName + 
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

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyName)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValueAsString(owlModel, individual, propertyName, true);
  } // getDatavaluedPropertyValueAsString

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
      throwException("property value for '" + propertyName + "' associated with individual '" + individual.getName() 
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
      throwException("property value for " + property.getName() + " in individual " + individual.getName() + " is not a Boolean");

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
    else throwException("property value for '" + propertyName + "' associated with individual '" + individual.getName() + 
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
    else throwException("property value for '" + property.getName() + "' associated with individual '" + individual.getName() + 
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

  public static Set<OWLProperty> getDomainProperties(OWLModel owlModel, String className, boolean transitive) throws SWRLOWLUtilException
  {
    OWLClass cls = getClass(owlModel, className);
    Set<OWLProperty> result = new HashSet<OWLProperty>();
    Collection domainProperties = cls.getUnionDomainProperties(transitive);

    // TODO: bug in Property.getUnionDomain that causes it to return non RDFResource objects so we need to work around it.
    // for (RDFResource resource : resources) result.add(resource.getName());

    if (domainProperties != null) {
      Iterator iterator = cls.getUnionDomainProperties().iterator();
      while (iterator.hasNext()) {
        Object o = iterator.next();
        if (o instanceof OWLProperty) result.add((OWLProperty)o);
      } // while
    } // if

    return result;
  } // getDomainProperties

  public static Set<String> rdfResources2OWLNamedClassNames(Collection resources) 
  {
    Set<String> result = new HashSet<String>();
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLNamedClass) result.add(((OWLNamedClass)o).getName());
    } // if

    return result;
    
  } // rdfResources2OWLNamedClassNames

  public static Set<String> rdfResources2Names(Collection resources) 
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

  public static boolean hasInconsistentClasses(OWLModel owlModel) 
  {
    return !owlModel.getInconsistentClasses().isEmpty();
  } // hasInconsistentClasses

  public static String createNewResourceName(OWLModel owlModel, String localNamePrefix) 
  {
    return owlModel.createNewResourceName(localNamePrefix);
  } // createNewResourceName

  public static OWLNamedClass getOWLThingClass(OWLModel owlModel) 
  {
    return owlModel.getOWLThingClass();
  } // getOWLThingClass

  public static RDFProperty getOWLSameAsProperty(OWLModel owlModel)
  {
    return owlModel.getOWLSameAsProperty();
  } // getOWLSameAsProperty

  public static Collection getOWLAllDifferents(OWLModel owlModel)
  {
    return owlModel.getOWLAllDifferents();
  } // getOWLAllDifferents

  public static RDFProperty getOWLDifferentFromProperty(OWLModel owlModel)
  {
    return owlModel.getOWLDifferentFromProperty();
  } // getOWLDifferentFromProperty

  public static OWLProperty getOWLProperty(OWLModel owlModel, String propertyName)
  {
    return owlModel.getOWLProperty(propertyName);
  } // getOWLProperty

  public static OWLIndividual getOWLIndividual(OWLModel owlModel, String individualName)
  {
    return owlModel.getOWLIndividual(individualName);
  } // getOWLIndividual

  public static RDFSNamedClass getRDFSNamedClass(OWLModel owlModel, String className)
  {
    return owlModel.getRDFSNamedClass(className);
  } // getRDFSNamedClass

  public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, String className)
  {
    return owlModel.getOWLNamedClass(className);
  } // getOWLNamedClass

  public static boolean isSWRLBuiltIn(OWLModel owlModel, String builtInName)
  {
    RDFResource resource = owlModel.getRDFResource(builtInName);
    return resource != null && resource.getProtegeType().getName().equals(edu.stanford.smi.protegex.owl.swrl.model.SWRLNames.Cls.BUILTIN);
  } // isSWRLBuiltIn

  private static void throwException(String message) throws SWRLOWLUtilException
  {
    throw new SWRLOWLUtilException(message);
  } // throwException

} // SWRLOWLUtil
