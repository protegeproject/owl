
package edu.stanford.smi.protegex.owl.swrl.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.classparser.AmbiguousNameException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/**
 * Class that wraps some common Protege-OWL API methods and throws meaningful exceptions when errors are encountered.
 *
 * Covers a fairly arbitrary method set. 
 */
public class SWRLOWLUtil
{
  public static JenaOWLModel createJenaOWLModel(String owlFileName) throws SWRLOWLUtilException
  {
    JenaOWLModel owlModel = null;

    try {
      owlModel = ProtegeOWL.createJenaOWLModelFromURI(new File(owlFileName).toURI().toString());
    } catch (Exception e) {
      throwException("error opening OWL file " + owlFileName + ": " + e.getMessage());
    } // try

    return owlModel;
  }

  public static JenaOWLModel createJenaOWLModel() throws SWRLOWLUtilException
  {
    JenaOWLModel owlModel = null;

    try {
      owlModel = ProtegeOWL.createJenaOWLModel();
    } catch (Exception e) {
      throw new SWRLOWLUtilException("error creating Jena OWL model: " + e.getMessage());
    } // try

    return owlModel;
  }

  public static void importOWLFile(JenaOWLModel owlModel, String importOWLFileName) throws SWRLOWLUtilException
  {
    try {
      ImportHelper importHelper = new ImportHelper(owlModel);
      URI importUri = URIUtilities.createURI(new File(importOWLFileName).toURI().toString());
      importHelper.addImport(importUri);
      importHelper.importOntologies(false);
    } catch (Exception e) {
      throwException("error importing OWL file " + importOWLFileName + ": " + e.getMessage());
    } // try
  } 

  public static void setPrefix(OWLModel owlModel, String prefix, String namespace) throws SWRLOWLUtilException
  {
    try {
      owlModel.getNamespaceManager().setPrefix(new URI(namespace), prefix);
    } catch (URISyntaxException e) {
      throwException("error setting prefix " + prefix + " for namespace " + namespace + ": " + e.getMessage());
    } // try
  } // setPrefix

  public static void writeJenaOWLModel2File(JenaOWLModel owlModel, String outputOWLFileName) throws SWRLOWLUtilException
  {
    ArrayList errors = new ArrayList();
    URI outputURI = URIUtilities.createURI(new File(outputOWLFileName).toURI().toString());
    owlModel.save(outputURI, FileUtils.langXMLAbbrev, errors);
    if (errors.size() != 0) throwException("error creating output OWL file " + outputOWLFileName + ": " + errors);
  } 
  
  public static OWLNamedClass createOWLNamedClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    RDFResource resource;
    OWLNamedClass cls;

    checkIfIsValidClassName(owlModel, classRDFID);
   
    resource = owlModel.getRDFResource(classRDFID);
    
    if (resource != null) {
      if (resource instanceof OWLNamedClass) cls = (OWLNamedClass)resource;
      else throw new SWRLOWLUtilException("class " + classRDFID + " is not an OWL named class");
    } else cls = owlModel.createOWLNamedClass(classRDFID);

    return cls;
  }

  public static OWLIndividual createOWLIndividual(OWLModel owlModel, String individualURI) throws SWRLOWLUtilException
  {
    OWLIndividual individual = owlModel.getOWLIndividual(individualURI);

    if (individual == null) individual = createIndividual(owlModel, individualURI);

    return individual;
  }

  public static RDFSNamedClass createRDFSNamedClass(OWLModel owlModel, String classRDFID) 
  {
    RDFSNamedClass cls;

    cls = owlModel.getRDFSNamedClass(classRDFID);

    if (cls == null) cls = owlModel.createRDFSNamedClass(classRDFID);

    return cls;
  }

  public static OWLNamedClass createOWLNamedClass(OWLModel owlModel) 
  {
    return owlModel.createOWLNamedClass(null);
  }

  public static OWLObjectProperty createOWLObjectProperty(OWLModel owlModel, String propertyRDFID) 
    throws SWRLOWLUtilException 
  {
    OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyRDFID);

    if (property == null) property = owlModel.createOWLObjectProperty(propertyRDFID);
    
    if (property == null) throw new SWRLOWLUtilException("error creating OWL object property " + propertyRDFID);

    return property;
  }

  public static OWLObjectProperty createOWLObjectProperty(OWLModel owlModel) 
  {
    return owlModel.createOWLObjectProperty(null);
  }

  public static OWLDatatypeProperty createOWLDatatypeProperty(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException 
  {
    OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyRDFID);

    if (property == null) property = owlModel.createOWLDatatypeProperty(propertyRDFID);
    
    if (property == null) throw new SWRLOWLUtilException("error creating OWL data property " + propertyRDFID);

    return property;
  }

  public static RDFSNamedClass createRDFSNamedClassUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates) 
  {
    RDFSNamedClass cls = null;
    Collection resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

    if (!allowDuplicates && resources != null && !resources.isEmpty()) {
      for (Object resource : resources) {
        if (resource instanceof OWLNamedClass) { 
          RDFSNamedClass candidateClass = (OWLNamedClass)resource; 
          // Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
          for (Object value : candidateClass.getPropertyValues(owlModel.getRDFSLabelProperty())) { 
            if (value instanceof String) {
              String stringValue = (String)value;
              if (stringValue.equalsIgnoreCase(labelText)) return candidateClass; // Pick the first matching one
            } else if (value instanceof RDFSLiteral) {
              RDFSLiteral literalValue = (RDFSLiteral)value;
              if (literalValue.getString().equalsIgnoreCase(labelText)) return candidateClass; // Pick the first matching one
            } // if
          } // for
        } // if        
      } // for
    } // if
    
    if (cls == null) cls = owlModel.createRDFSNamedClass(null); // We may not have found a matching class above.

    if (!cls.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText)) cls.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);

    return cls;
  } 

  // TODO: case sensitivity option
  public static OWLNamedClass createOWLNamedClassUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates)
  throws SWRLOWLUtilException
	{
	  OWLNamedClass cls = null;
	  Collection resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);
	
	  if (!allowDuplicates && resources != null && !resources.isEmpty()) {
	    for (Object resource : resources) {
	      if (resource instanceof OWLNamedClass) { 
	        OWLNamedClass candidateClass = (OWLNamedClass)resource; 
	        // Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
	        for (Object value : candidateClass.getPropertyValues(owlModel.getRDFSLabelProperty())) { 
	          if (value instanceof String) {
	            String stringValue = (String)value;
	            if (stringValue.equalsIgnoreCase(labelText)) return candidateClass; // Pick the first matching one
	          } else if (value instanceof RDFSLiteral) {
	            RDFSLiteral literalValue = (RDFSLiteral)value;
	            if (literalValue.getString().equalsIgnoreCase(labelText)) return candidateClass; // Pick the first matching one
	          } // if
	        } // for
	      } // if        
	    } // for
	  } // if
	  
	  if (cls == null) cls = owlModel.createOWLNamedClass(null); // We may not have found a matching class above.
	
	  if (!cls.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText)) cls.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);
	
	  return cls;
	}
  
  public static OWLObjectProperty createOWLObjectPropertyUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates)
    throws SWRLOWLUtilException
  {
    OWLObjectProperty property = null;
    Collection resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

    if (!allowDuplicates && resources != null && !resources.isEmpty()) {
      for (Object resource : resources) {
        if (resource instanceof OWLNamedClass) { 
          OWLObjectProperty candidateProperty = (OWLObjectProperty)resource; 
          // Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
          for (Object value : candidateProperty.getPropertyValues(owlModel.getRDFSLabelProperty())) { 
            if (value instanceof String) {
              String stringValue = (String)value;
              if (stringValue.equalsIgnoreCase(labelText)) return candidateProperty; // Pick the first matching one
            } else if (value instanceof RDFSLiteral) {
              RDFSLiteral literalValue = (RDFSLiteral)value;
              if (literalValue.getString().equalsIgnoreCase(labelText)) return candidateProperty; // Pick the first matching one
            } // if
          } // for
        } // if        
      } // for
    } // if
    
    if (property == null) property = owlModel.createOWLObjectProperty(null); // We may not have found a matching property above.

    if (!property.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText)) property.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);

    return property;
  }

  public static OWLDatatypeProperty createOWLDataPropertyUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates)
    throws SWRLOWLUtilException
  {
    OWLDatatypeProperty property = null;
    Collection resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

    if (!allowDuplicates && resources != null && !resources.isEmpty()) {
      for (Object resource : resources) {
        if (resource instanceof OWLNamedClass) { 
          OWLDatatypeProperty candidateProperty = (OWLDatatypeProperty)resource; 
          // Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
          for (Object value : candidateProperty.getPropertyValues(owlModel.getRDFSLabelProperty())) { 
            if (value instanceof String) {
              String stringValue = (String)value;
              if (stringValue.equalsIgnoreCase(labelText)) return candidateProperty; // Pick the first matching one
            } else if (value instanceof RDFSLiteral) {
              RDFSLiteral literalValue = (RDFSLiteral)value;
              if (literalValue.getString().equalsIgnoreCase(labelText)) return candidateProperty; // Pick the first matching one
            } // if
          } // for
        } // if        
      } // for
    } // if
    
    if (property == null) property = owlModel.createOWLDatatypeProperty(null); // We may not have found a matching property above.

    if (!property.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText)) property.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);

    return property;
  }

  public static OWLIndividual createOWLIndividualUsingLabelAnnotation(OWLModel owlModel, String labelText, boolean allowDuplicates) 
   throws SWRLOWLUtilException
  {
    OWLIndividual individual = null;
    Collection resources = owlModel.getMatchingResources(owlModel.getRDFSLabelProperty(), "*" + labelText, -1);

    if (!allowDuplicates && resources != null && !resources.isEmpty()) {
      for (Object resource : resources) {
        if (resource instanceof OWLNamedClass) { 
          OWLIndividual candidateIndividual = (OWLIndividual)resource; 
          // Verify that label is really labelText. Wildcard needed above to get around language encoding causes possible multiple match
          for (Object value : candidateIndividual.getPropertyValues(owlModel.getRDFSLabelProperty())) { 
            if (value instanceof String) {
              String stringValue = (String)value;
              if (stringValue.equalsIgnoreCase(labelText)) return candidateIndividual; // Pick the first matching one
            } else if (value instanceof RDFSLiteral) {
              RDFSLiteral literalValue = (RDFSLiteral)value;
              if (literalValue.getString().equalsIgnoreCase(labelText)) return candidateIndividual; // Pick the first matching one
            } // if
          } // for
        } // if        
      } // for
    } // if
    
    if (individual == null) individual = createOWLIndividual(owlModel); // We may not have found a matching individual above.

    if (!individual.hasPropertyValue(owlModel.getRDFSLabelProperty(), labelText)) individual.addPropertyValue(owlModel.getRDFSLabelProperty(), labelText);

    return individual;
  }

  public static Set<OWLIndividual> getMatchingIndividuals(OWLModel owlModel, String propertyRDFID, String matchString) 
    throws SWRLOWLUtilException
  {
    RDFProperty property = getOWLProperty(owlModel, propertyRDFID, true);
    Collection matchingResources = owlModel.getMatchingResources(property, matchString, -1);
    Set<OWLIndividual> matchingIndividuals = new HashSet<OWLIndividual>();

    for (Object o : matchingResources) if (o instanceof OWLIndividual) matchingIndividuals.add((OWLIndividual)o);

    return matchingIndividuals;
  } 

  public static Set<OWLIndividual> getMatchingIndividualsOfClass(OWLModel owlModel, String classRDFID, String propertyRDFID, String matchString) 
    throws SWRLOWLUtilException
  {
    Set<OWLIndividual> matchingIndividuals = new HashSet<OWLIndividual>();

    for (OWLIndividual owlIndividual : getMatchingIndividuals(owlModel, propertyRDFID, matchString))
      if (isOWLIndividualOfClass(owlModel, owlIndividual, classRDFID)) matchingIndividuals.add(owlIndividual);

    return matchingIndividuals;
  }    

  public static boolean isOWLNamedClass(OWLModel owlModel, String classRDFID)
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    return resource instanceof OWLNamedClass;
  } 

  public static boolean isOWLClass(OWLModel owlModel, String classRDFID)
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    return resource != null && resource instanceof OWLClass;
  }

  public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = owlModel.getOWLNamedClass(classRDFID);

    if (cls == null) throw new SWRLOWLUtilException("unknown OWL named class " + classRDFID);

    return cls;
  }

  public static OWLClass getOWLClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    if (resource == null || !(resource instanceof OWLClass)) throw new SWRLOWLUtilException("invalid or unknown OWL class " + classRDFID);

    return (OWLClass)resource;
  } 
  
  public static RDFResource getRDFResource(OWLModel owlModel, String resourceName)
  {
	  return owlModel.getRDFResource(resourceName);
  } 

  public static RDFSClass getRDFSClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    if (resource == null || !(resource instanceof RDFSClass)) throw new SWRLOWLUtilException("invalid or unknown RDFS class " + classRDFID);

    return (RDFSClass)resource;
  }

  public static OWLClass getOWLClassDescription(OWLModel owlModel, String descriptionClassName) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(descriptionClassName);

    if (resource == null || !(resource instanceof OWLClass)) 
      throw new SWRLOWLUtilException("unknown OWL class description name " + descriptionClassName);

    return (OWLClass)resource;
  }

  public static OWLIndividual createIndividual(OWLModel owlModel, String individualRDFID) throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, getOWLThingClass(owlModel), individualRDFID);
  }

  public static OWLIndividual createOWLIndividual(OWLModel owlModel) throws SWRLOWLUtilException
  {
    return (OWLIndividual)getOWLThingClass(owlModel).createInstance(null);
  }

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String classRDFID)
    throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, classRDFID, null);
  }

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, String classRDFID, String individualRDFID)
    throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return createIndividualOfClass(owlModel, cls, individualRDFID);
  }

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLClass cls)
    throws SWRLOWLUtilException
  {
    return createIndividualOfClass(owlModel, cls, null);
  }

  public static OWLIndividual createIndividualOfClass(OWLModel owlModel, OWLClass cls, String individualRDFID)
    throws SWRLOWLUtilException
  {
    RDFResource resource = null;
    OWLIndividual individual = null;

    if (individualRDFID != null) resource = owlModel.getRDFResource(individualRDFID);

    if (resource == null) {
      individual = (OWLIndividual)cls.createInstance(individualRDFID);
      if (!individual.hasRDFType(cls, true)) individual.setRDFType(cls);
    } else {
      if (resource instanceof OWLIndividual) {
        individual = (OWLIndividual)resource;
        if (!individual.hasRDFType(cls, true)) individual.addRDFType(cls);
      } else throwException("could not create individual " + individualRDFID + " because another resource of that name already exists");
    } // if

    return individual;
  }

  public static OWLNamedClass getNamedClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    return getOWLNamedClass(owlModel, classRDFID, true);
  } 

  public static OWLClass getClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    return getOWLClass(owlModel, classRDFID, true);
  } 

  public static boolean isOWLIndividualOfClass(OWLModel owlModel, OWLIndividual individual, String classRDFID) 
  {
    RDFResource cls = owlModel.getRDFResource(classRDFID);

    return (cls instanceof OWLNamedClass) && individual.hasRDFType((OWLNamedClass)cls, true);
  } 

  public static boolean isOWLIndividualOfType(OWLModel owlModel, String individualRDFID, String classRDFID) 
  {
    RDFResource cls = owlModel.getRDFResource(classRDFID);
    RDFResource individual = owlModel.getRDFResource(individualRDFID);

    return (cls instanceof OWLNamedClass) && (individual instanceof OWLIndividual) && individual.hasRDFType((OWLNamedClass)cls, true);
  }

  public static boolean isOWLIndividualOfType(OWLModel owlModel, String individualRDFID, OWLNamedClass cls) 
  {
    RDFResource individual = owlModel.getRDFResource(individualRDFID);

    return (individual instanceof OWLIndividual) && individual.hasRDFType((OWLNamedClass)cls, true);
  }

  public static boolean isOWLIndividualOfDirectTypeOWLThing(OWLModel owlModel, String individualRDFID) 
  {
    RDFResource individual = owlModel.getRDFResource(individualRDFID);

    return (individual instanceof OWLIndividual) && individual.hasRDFType(getOWLThingClass(individual.getOWLModel()), false);
  }

  public static void setType(OWLModel owlModel, String individualRDFID, String classRDFID) throws SWRLOWLUtilException
  {
    OWLClass cls = getOWLClass(owlModel, classRDFID);
    OWLIndividual individual = getIndividual(owlModel, individualRDFID);

    if (!individual.hasRDFType(cls, true)) individual.setRDFType(cls);
  } 

  public static void addObjectPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String objectRDFID) 
	  throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  OWLProperty property = getOWLProperty(owlModel, propertyRDFID);
	
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (objectRDFID == null) throwException("null value for property " + propertyRDFID + " for subject " + objectRDFID);
	
	  if (property.isObjectProperty()) {
	    if (isOWLIndividual(owlModel, objectRDFID)) {
	      OWLIndividual objectIndividual = getOWLIndividual(owlModel, objectRDFID);
	      if (!subject.hasPropertyValue(property, objectIndividual)) subject.addPropertyValue(property, objectIndividual);
	    } else if (isOWLNamedClass(owlModel, objectRDFID)) {
	      OWLClass objectClass = getOWLNamedClass(owlModel, objectRDFID);
	      if (!subject.hasPropertyValue(property, objectClass)) subject.addPropertyValue(property, objectClass);
	    } else throw new SWRLOWLUtilException("invalid property value " + objectRDFID + " for object property " + propertyRDFID + 
	                                          " for subject " + subjectRDFID + "; value must be class or individual");
	
	  } else throw new SWRLOWLUtilException("invalid property value " + objectRDFID + " for object property " + propertyRDFID + 
	      																   " for subject " + subjectRDFID + "; value must be a data property value");
	} 

  public static void addStringDataPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue) 
    throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  OWLProperty property = getOWLProperty(owlModel, propertyRDFID);
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	  
	  if (!subject.hasPropertyValue(property, propertyValue)) 
	  	subject.addPropertyValue(property, propertyValue); 
	} 


  public static void addDataPropertyValueWithLanguage(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue, String language) 
  	throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  OWLProperty property = getOWLProperty(owlModel, propertyRDFID);
	  RDFSLiteral value = owlModel.createRDFSLiteral(propertyValue, language);
	  
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	  
	  if (!subject.hasPropertyValue(property, value)) 
	  	subject.addPropertyValue(property, value); 
	}

  public static void addAnnotationObjectPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue) 
	  throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  RDFProperty property = getRDFProperty(owlModel, propertyRDFID);
	
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	
	  if (isOWLIndividual(owlModel, propertyValue)) {
	  	OWLIndividual objectIndividual = getOWLIndividual(owlModel, propertyValue);
	  	if (!subject.hasPropertyValue(property, objectIndividual)) 
	  		subject.addPropertyValue(property, objectIndividual);
	  } else if (isOWLNamedClass(owlModel, propertyValue)) {
	  	OWLClass objectClass = getOWLNamedClass(owlModel, propertyValue);
	  	if (!subject.hasPropertyValue(property, objectClass)) 
	  		subject.addPropertyValue(property, objectClass);
	  } else throw new SWRLOWLUtilException("invalid object property value " + propertyValue + " for annotation property " + propertyRDFID + 
	  		                                  " for subject " + subjectRDFID + "; value must be class or individual");
	} 

  public static void addAnnotationPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue) 
  	throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  RDFProperty property = owlModel.getRDFProperty(propertyRDFID);
	  
	  if (property == null) throw new SWRLOWLUtilException("unknown annotation property " + propertyRDFID);
	
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	
	  if (isOWLIndividual(owlModel, propertyValue)) {
	  	OWLIndividual objectIndividual = getOWLIndividual(owlModel, propertyValue);
	    if (!subject.hasPropertyValue(property, objectIndividual)) subject.addPropertyValue(property, objectIndividual);
    } else if (isOWLNamedClass(owlModel, propertyValue)) {
    	OWLClass objectClass = getOWLNamedClass(owlModel, propertyValue);
    	if (!subject.hasPropertyValue(property, objectClass)) 
    		subject.addPropertyValue(property, objectClass);
    } else {
    	if (!subject.hasPropertyValue(property, propertyValue)) 
    		subject.addPropertyValue(property, propertyValue);
    }
	} 

  public static void addAnnotationStringDataPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue) 
	  throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  RDFProperty property = getRDFProperty(owlModel, propertyRDFID);
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	  
	  if (!subject.hasPropertyValue(property, propertyValue)) 
	  	subject.addPropertyValue(property, propertyValue); 
	} 

  public static void addAnnotationDataPropertyValueWithLanguage(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue, String language) 
	  throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  RDFProperty property = getRDFProperty(owlModel, propertyRDFID);
	  RDFSLiteral value = owlModel.createRDFSLiteral(propertyValue, language);
	  
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	  
	  if (!subject.hasPropertyValue(property, value)) 
	  	subject.addPropertyValue(property, value); 
	} 

  public static void addDataPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue, String datatypeName) 
		throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  OWLProperty property = getOWLProperty(owlModel, propertyRDFID);
	
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
	
	  if (property.isObjectProperty()) 
	  	throw new SWRLOWLUtilException("attempt to assign data property with value " + propertyValue + " and type " + datatypeName +
	  			                           " to annotation property " + propertyRDFID + " on individual " + subjectRDFID);
	  
	  RDFSDatatype datatype = getRDFSDatatype(owlModel, datatypeName);
	  
	  if (datatype == null) throw new SWRLOWLUtilException("invalid datatype name " + datatypeName);
	  
	  RDFSLiteral literal = owlModel.createRDFSLiteral(propertyValue, datatype);
	  
	  // Protege-OWL stores some RDFS literals in native form
	  if (literal.getPlainValue() == null) {	
	  	if (!subject.hasPropertyValue(property, literal)) 
	  		subject.addPropertyValue(property, literal);
	  } else {
	  	if (!subject.hasPropertyValue(property, literal.getPlainValue())) 
	  		subject.addPropertyValue(property, literal.getPlainValue());
	  }
	} 

  public static void addAnnotationDataPropertyValue(OWLModel owlModel, String subjectRDFID, String propertyRDFID, String propertyValue, String datatypeName) 
		throws SWRLOWLUtilException
	{
	  RDFResource subject = null;
	  RDFProperty property = getRDFProperty(owlModel, propertyRDFID);
	
	  if (subjectRDFID.startsWith("'")) subjectRDFID = subjectRDFID.substring(1, subjectRDFID.length() - 1); 
	
	  if (isOWLIndividual(owlModel, subjectRDFID)) subject = getOWLIndividual(owlModel, subjectRDFID);
	  else if (isOWLClass(owlModel, subjectRDFID)) subject = getOWLClass(owlModel, subjectRDFID);
	  else throw new SWRLOWLUtilException("invalid or unknown subject name " + subjectRDFID + "; must be OWLClass or OWLIndividual");
	
	  if (subject == null) throwException("invalid or unknown subject name " + subjectRDFID);
	  if (property == null) throwException("invalid or unknown property name " + propertyRDFID);
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for subject " + subjectRDFID);
		  
	  RDFSDatatype datatype = getRDFSDatatype(owlModel, datatypeName);
	  
	  if (datatype == null) throw new SWRLOWLUtilException("invalid datatype name " + datatypeName);
	  
	  RDFSLiteral literal = owlModel.createRDFSLiteral(propertyValue, datatype);
			
	  // Protege-OWL stores some RDFS literals in native form
	  if (literal.getPlainValue() == null) {	
	  	if (!subject.hasPropertyValue(property, literal)) 
	  		subject.addPropertyValue(property, literal);
	  } else {
	  	if (!subject.hasPropertyValue(property, literal.getPlainValue())) 
	  		subject.addPropertyValue(property, literal.getPlainValue());
	  }
	} 

  public static void addType(OWLModel owlModel, String resourceName, String classRDFID) throws SWRLOWLUtilException
  {
    OWLClass cls = getOWLClass(owlModel, classRDFID);
    RDFResource resource = null;
    
    if (cls == null) throw new SWRLOWLUtilException("could not find class: " + classRDFID);

	  if (isOWLIndividual(owlModel, resourceName)) resource = getOWLIndividual(owlModel, resourceName);
	  else if (isOWLClass(owlModel, resourceName)) resource = getOWLClass(owlModel, resourceName);
	  else throw new SWRLOWLUtilException("invalid or unknown resource name " + resourceName + "; must be name ow OWL class or individual");

    if (!resource.hasProtegeType(cls, true)) resource.addProtegeType(cls);
  }

  public static void addType(OWLIndividual individual, OWLClass cls) throws SWRLOWLUtilException
  {
    if (!individual.hasRDFType(cls, true)) individual.addRDFType(cls);
  }

  public static void removeType(OWLIndividual individual, OWLClass cls) throws SWRLOWLUtilException
  {
    if (individual.hasRDFType(cls, true)) individual.removeRDFType(cls);
  }

  public static String getFullName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String result = OWLUtil.getInternalFullName(owlModel, name, true);

    if (result == null) throw new SWRLOWLUtilException("cannot get full name for resource " + name);

    return result;
  }

  public static Set<String> getRDFSLabels(OWLModel owlModel, String resourceName)
  {
  	return getRDFSLabels(owlModel, resourceName, "");
  } 
  
  public static Set<String> getRDFSLabels(OWLModel owlModel, String resourceName, String language)
  {
    RDFResource resource = owlModel.getRDFResource(resourceName);
    Set<String> result = new HashSet<String>();

    if (resource != null) {
    	for (Object label : resource.getPropertyValues(owlModel.getRDFSLabelProperty())) {
    		if (label instanceof String) result.add((String)label);
        else if (label instanceof RDFSLiteral) {
        	RDFSLiteral literal = (RDFSLiteral)label;
        	if (language.equals("") || (language.equals(literal.getLanguage())))
            result.add(literal.getString());
        } // if
    	} // for
    } // if
    return result;
  } 

  public static Set<String> getRDFSLabelLanguages(OWLModel owlModel, String resourceName)
  {
    RDFResource resource = owlModel.getRDFResource(resourceName);
    Set<String> result = new HashSet<String>();

    if (resource != null) {
    	for (Object label : resource.getPropertyValues(owlModel.getRDFSLabelProperty())) {
    		if (label instanceof RDFSLiteral) {
    			RDFSLiteral literal = (RDFSLiteral)label;	
    			result.add(literal.getLanguage());
    		} // if
    	} // for
    } // if
    return result;
  }
  
  public static void addRDFSLabel(RDFResource resource, String labelText) 
  {
    if (!resource.hasPropertyValue(resource.getOWLModel().getRDFSLabelProperty(), labelText)) 
    	resource.addPropertyValue(resource.getOWLModel().getRDFSLabelProperty(), labelText);
  } 

  public static OWLSomeValuesFrom getOWLSomeValuesFrom(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    return (OWLSomeValuesFrom)owlModel.getOWLSomeValuesFromRestrictionClass().createInstance(classRDFID);    
  } 

  public static OWLIndividual getOWLIndividual(OWLModel owlModel, OWLNamedClass cls, boolean mustExist, int mustHaveExactlyN)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual = null;
    Collection instances;
    Object firstInstance;

    if (mustExist && cls.getInstanceCount(true) == 0) throwException("no individuals of class " + cls.getPrefixedName() + " in ontology");
    else if (cls.getInstanceCount(true) != mustHaveExactlyN) 
      throwException("expecting exactly " + mustHaveExactlyN + " individuals of class " + cls.getPrefixedName() + " in ontology - got " +
                     cls.getInstanceCount(true) + "");

    instances = cls.getInstances();

    if (!instances.isEmpty()) {
      firstInstance = cls.getInstances(true).iterator().next();

      if (firstInstance instanceof OWLIndividual) return (OWLIndividual)firstInstance;
      else throw new SWRLOWLUtilException("instance of class " + cls.getPrefixedName() + " is not an OWL individual");
    } else return null;
  }

  public static Set<OWLIndividual> getAllOWLIndividuals(OWLModel owlModel) throws SWRLOWLUtilException
  {
    return new HashSet<OWLIndividual>(owlModel.getOWLIndividuals());
  }

  public static Set<OWLIndividual> getOWLIndividualsOfClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return new HashSet<OWLIndividual>(getOWLIndividualsOfClass(cls));
  } 

  public static Set<OWLIndividual> getOWLIndividualsOfClass(OWLNamedClass cls) throws SWRLOWLUtilException
  {
    return new HashSet<OWLIndividual>(cls.getInstances(true));
  }

  public static OWLProperty getProperty(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  {
    return getOWLProperty(owlModel, propertyRDFID, true);
  } 

  public static OWLDatatypeProperty getOWLDataProperty(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  {
     return getOWLDataProperty(owlModel, propertyRDFID, true);
  }
    
  public static OWLDatatypeProperty getOWLDataProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLDatatypeProperty property = owlModel.getOWLDatatypeProperty(propertyRDFID);
    if (mustExist && property == null) throwException("no " + propertyRDFID + " datatype property in ontology");

    return property;
  }

  public static OWLObjectProperty getOWLObjectProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLObjectProperty property = owlModel.getOWLObjectProperty(propertyRDFID);

    if (mustExist && property == null) throwException("no " + propertyRDFID + " object property in ontology");

    return property;
  }

  public static boolean isOWLEquivalentProperty(OWLModel owlModel, String propertyRDFID1, String propertyRDFID2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property1 = getOWLProperty(owlModel, propertyRDFID1, mustExist);
    OWLProperty property2 = getOWLProperty(owlModel, propertyRDFID2, mustExist);

    return (property1 != null && property2 != null && property1.getEquivalentProperties().contains(property2));
  }

  public static boolean isOWLEquivalentClass(OWLModel owlModel, String classRDFID1, String classRDFID2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass class1 = getOWLNamedClass(owlModel, classRDFID1, mustExist);
    OWLNamedClass class2 = getOWLNamedClass(owlModel, classRDFID2, mustExist);

    return (class1 != null && class2 != null && class1.hasEquivalentClass(class2));
  } 

  public static boolean isOWLDisjointClass(OWLModel owlModel, String classRDFID1, String classRDFID2, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass class1 = getOWLNamedClass(owlModel, classRDFID1, mustExist);
    OWLNamedClass class2 = getOWLNamedClass(owlModel, classRDFID2, mustExist);

    return (class1 != null && class2 != null && class1.getDisjointClasses().contains(class2));
  }

  public static boolean isOWLSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty subProperty = getOWLProperty(owlModel, subPropertyName, mustExist);
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, true));
  } 

  public static boolean isOWLSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty superProperty = getOWLProperty(owlModel, superPropertyName, mustExist);
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, true)); // No isSuperpropertyOf call
  }

  public static boolean isOWLDirectSuperPropertyOf(OWLModel owlModel, String superPropertyName, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty superProperty = getOWLProperty(owlModel, superPropertyName, mustExist);
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (superProperty != null && property != null && property.isSubpropertyOf(superProperty, false)); // No isSuperpropertyOf call
  } // isDirectSuperPropertyOf

  public static boolean isOWLDirectSubPropertyOf(OWLModel owlModel, String subPropertyName, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty subProperty = getOWLProperty(owlModel, subPropertyName, mustExist);
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (subProperty != null && property != null && subProperty.isSubpropertyOf(property, false));
  } 

  public static boolean isOWLDirectSubClassOf(OWLModel owlModel, String subClassName, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass subClass = getOWLNamedClass(owlModel, subClassName, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (subClass != null && cls != null && subClass.isSubclassOf(cls));
  } 

  public static boolean isOWLSubClassOf(OWLModel owlModel, String subClassName, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass subClass = getOWLNamedClass(owlModel, subClassName, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (subClass != null && cls != null && cls.getSubclasses().contains(subClass)); // TODO: uses deprecated getSubclasses
  }

  public static void addOWLSuperClass(OWLModel owlModel, String subClassName, String superClassName) throws SWRLOWLUtilException
  {
    OWLClass subClass = getOWLClass(owlModel, subClassName);
    OWLClass superClass = getOWLClass(owlModel, superClassName);

    subClass.addSuperclass(superClass);
  } 

  public static void addOWLSuperClass(OWLClass subClass, OWLClass superClass) 
  {
    subClass.addSuperclass(superClass);
  }

  public static void addRDFSSuperClass(RDFSClass subClass, RDFSClass superClass) 
  {
    subClass.addSuperclass(superClass);
  } 
    
  public static boolean isOWLDirectSuperClassOf(OWLModel owlModel, String superClassName, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getOWLNamedClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (superClass != null && cls != null && cls.isSubclassOf(superClass)); // No isSuperclassOf call
  } 

  public static boolean isOWLSuperClassOf(OWLModel owlModel, String superClassName, String classRDFID) 
    throws SWRLOWLUtilException
  {
    return isOWLSuperClassOf(owlModel, superClassName, classRDFID, true);
  }

  public static boolean isOWLSuperClassOf(OWLModel owlModel, String superClassName, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLNamedClass superClass = getOWLNamedClass(owlModel, superClassName, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (superClass != null && cls != null && cls.getSuperclasses(true).contains(superClass));
  } 

  public static int getNumberOfOWLIndividualsOfClass(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    return getNumberOfOWLIndividualsOfClass(owlModel, classRDFID, true);
  }

  public static int getNumberOfOWLIndividualsOfClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);
    int numberOfIndividuals = 0;

    if (cls != null) numberOfIndividuals = cls.getInstances(true).size();

    return numberOfIndividuals;
  } 

  public static int getNumberOfDirectOWLInstancesOfClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);
    int numberOfIndividuals = 0;

    if (cls != null) numberOfIndividuals = cls.getInstances(false).size();

    return numberOfIndividuals;
  } //  getNumberOfDirectInstancesOfClass

  public static boolean isConsistentOWLClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return cls != null && cls.isConsistent();
  }

  public static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDomainClasses(owlModel, propertyRDFID, true, true);
  } 

  public static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLDomainClasses(owlModel, propertyRDFID, mustExist, true);
  }

  public static Set<OWLNamedClass> getDirectOWLDomainClasses(OWLModel owlModel, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDomainClasses(owlModel, propertyRDFID, true, false);
  }

  public static Set<OWLNamedClass> getDirectOWLDomainClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLDomainClasses(owlModel, propertyRDFID, mustExist, false);
  }

  private static Set<OWLNamedClass> getOWLDomainClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist, 
                                                        boolean includingSuperproperties) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    Set<OWLNamedClass> result = new HashSet<OWLNamedClass>();
    Collection domainClasses = property.getUnionDomain(includingSuperproperties);
    Iterator iterator;

    if (domainClasses == null) return result;
    
    iterator = domainClasses.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLNamedClass) 
      	result.add((OWLNamedClass)o);
    } // while
    
    return result;
  } 

  public static void removeOWLThingSuperclass(OWLModel owlModel, OWLClass owlClass)
  {
    if (owlClass.getSuperclasses(false).contains(getOWLThingClass(owlModel))) 
    	owlClass.removeSuperclass(getOWLThingClass(owlModel));
  } 

  public static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLRangeClasses(owlModel, propertyRDFID, mustExist, true);
  }

  public static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLRangeClasses(owlModel, propertyRDFID, true, true);
  }

  public static Set<OWLNamedClass> getOWLDirectRangeClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLRangeClasses(owlModel, propertyRDFID, mustExist, false);
  }

  public static Set<OWLNamedClass> getOWLDirectRangeClasses(OWLModel owlModel, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLRangeClasses(owlModel, propertyRDFID, true, false);
  } 

  private static Set<OWLNamedClass> getOWLRangeClasses(OWLModel owlModel, String propertyRDFID, boolean mustExist, 
                                                       boolean includingSuperproperties) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
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
  }

  public static boolean isInOWLPropertyDomain(OWLModel owlModel, String propertyRDFID, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (property != null && cls != null && property.getDomains(true).contains(cls));
  } 

  public static boolean isInDirectOWLPropertyDomain(OWLModel owlModel, String propertyRDFID, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (property != null && cls != null && property.getDomains(false).contains(cls));
  } // isInDirectPropertyDomain

  public static boolean isInPropertyRange(OWLModel owlModel, String propertyRDFID, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (property != null && cls != null && property.getRanges(true).contains(cls));
  }

  public static boolean isInDirectPropertyRange(OWLModel owlModel, String propertyRDFID, String classRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    OWLNamedClass cls = getOWLNamedClass(owlModel, classRDFID, mustExist);

    return (property != null && cls != null && property.getRanges(false).contains(cls));
  } // isInDirectPropertyRange

  public static boolean isOWLObjectProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (property != null && property.isObjectProperty());
  }

  public static boolean isOWLIndividual(OWLModel owlModel, String individualRDFID)
  {
    RDFResource resource = owlModel.getRDFResource(individualRDFID);

    return (resource != null && resource instanceof OWLIndividual);
  } // isOWLIndividual

  public static boolean isOWLObjectProperty(OWLModel owlModel, String propertyRDFID)
  {
    RDFResource resource = owlModel.getRDFResource(propertyRDFID);

    return resource instanceof OWLObjectProperty;
  } 

  public static boolean isOWLDataProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (property != null && !property.isObjectProperty());
  }
  
  public static boolean hasXSDStringRange(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  {
  	if (isOWLDataProperty(owlModel, propertyRDFID)) {
  		 OWLDatatypeProperty property = getOWLDataProperty(owlModel, propertyRDFID);
  		 return property.getRange() == owlModel.getXSDstring();
  	} else return false;
  }

  public static boolean isOWLDataProperty(OWLModel owlModel, String propertyRDFID)
  {
    RDFResource resource = owlModel.getRDFResource(propertyRDFID);

    return resource instanceof OWLDatatypeProperty;
  } 

  public static boolean isOWLTransitiveProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isTransitive());
  }

  public static boolean isOWLTransitiveProperty(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  {
    return isOWLTransitiveProperty(owlModel, propertyRDFID, true);
  }

  public static boolean isOWLSymmetricProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return (property != null && property instanceof OWLObjectProperty && ((OWLObjectProperty)property).isSymmetric());
  } 

  public static boolean isOWLFunctionalProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return property != null && property.isFunctional();
  } 

  public static boolean isAnnotationProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return property != null && property.isAnnotationProperty();
  } 

  public static boolean isAnnotationProperty(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, true);

    return property != null && property.isAnnotationProperty();
  } 

  public static boolean isInverseFunctionalProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    return property != null && property.isInverseFunctional();
  } // isInverseFunctionalProperty

  public static boolean isAnonymousResourceName(OWLModel owlModel, String resourceName) throws SWRLOWLUtilException
  {
    return owlModel.isAnonymousResourceName(resourceName);
  } // isAnonymousResourceName

  public static OWLIndividual getIndividual(OWLModel owlModel, String individualRDFID) throws SWRLOWLUtilException
  {
    return getOWLIndividual(owlModel, individualRDFID, true);
  } 

  public static OWLIndividual getOWLIndividual(OWLModel owlModel, String individualRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(individualRDFID);

    if (mustExist && (resource == null || !(resource instanceof OWLIndividual))) 
      throwException("no individual named " + individualRDFID + " in ontology");
        
    if (resource != null) {
      if (resource instanceof OWLIndividual) return (OWLIndividual)resource;
      else return null;
    } else return null;
  }

  public static OWLNamedClass getOWLNamedClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    if (mustExist && (resource == null || !(resource instanceof OWLNamedClass))) 
      throwException("no class named " + classRDFID + " in ontology");
        
    if (resource != null) {
      if (resource instanceof OWLNamedClass) return (OWLNamedClass)resource;
      else return null;
    } else return null;
  }

  public static OWLClass getOWLClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(classRDFID);

    if (mustExist && (resource == null || !(resource instanceof OWLClass))) 
      throwException("no class named " + classRDFID + " in ontology");
        
    if (resource != null) {
      if (resource instanceof OWLClass) return (OWLClass)resource;
      else return null;
    } else return null;
  }

  public static Set<OWLNamedClass> getOWLClassesOfIndividual(OWLModel owlModel, String individualRDFID) throws SWRLOWLUtilException
  {
    return getOWLClassesOfIndividual(owlModel, individualRDFID, true);
  }

  public static Set<OWLNamedClass> getOWLClassesOfIndividual(OWLModel owlModel, String individualRDFID, boolean mustExist) 
    throws SWRLOWLUtilException  
  {
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist);
    
    return individual == null ? new HashSet<OWLNamedClass>() : getClassesOfIndividual(owlModel, individual);
  }

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
  } 

  public static String getNextAnonymousResourceName(OWLModel owlModel)
  {
    return owlModel.getNextAnonymousResourceName();
  } 

  public static boolean isOWLNamedClass(OWLModel owlModel, String classRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    return(getOWLNamedClass(owlModel, classRDFID, mustExist) != null);
  } 

  public static boolean isOWLProperty(OWLModel owlModel, String propertyRDFID) 
  {
    RDFResource resource = owlModel.getRDFResource(propertyRDFID);

    return resource instanceof OWLProperty;
  } 

  public static boolean isOWLProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getOWLProperty(owlModel, propertyRDFID, mustExist) != null);
  }

  public static Collection<OWLNamedClass> getUserDefinedOWLNamedClasses(OWLModel owlModel) 
  { 
    return new ArrayList<OWLNamedClass>(owlModel.getUserDefinedOWLNamedClasses());
  }

  public static Collection<OWLProperty> getUserDefinedOWLProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLProperties());
  } 
  
  public static Collection<OWLProperty> getUserDefinedOWLObjectProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLObjectProperties());
  } 

  public static Collection<OWLProperty> getUserDefinedOWLDatatypeProperties(OWLModel owlModel) 
  { 
    return new ArrayList<OWLProperty>(owlModel.getUserDefinedOWLDatatypeProperties());
  } // getUserDefinedOWLDatatypeProperties

  public static boolean isIndividual(OWLModel owlModel, String individualRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getOWLIndividual(owlModel, individualRDFID, mustExist) != null);
  } // isIndividualName

  public static boolean isSWRLVariable(OWLModel owlModel, String individualRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    return (getOWLIndividual(owlModel, individualRDFID, mustExist) != null &&
            getOWLIndividual(owlModel, individualRDFID, mustExist) instanceof SWRLVariable);
  } // isIndividualName

  public static boolean isIndividual(OWLModel owlModel, String individualRDFID) throws SWRLOWLUtilException
  {
    return (getOWLIndividual(owlModel, individualRDFID, false) != null);
  } // isIndividual

  public static int getNumberOfPropertyValues(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist); // Will throw an exception if mustExist is true.
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist); // Will throw an exception if mustExist is true.

    return individual.getPropertyValues(property).size();
  } // getNumberOfPropertyValues

  public static Set<OWLProperty> getOWLPropertiesOfIndividual(OWLModel owlModel, String individualRDFID) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, true);
    HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
    Collection rdfProperties = individual.getRDFProperties();

    Iterator iterator = rdfProperties.iterator();
    while (iterator.hasNext()) {
      RDFProperty property = (RDFProperty)iterator.next();
      if (property instanceof OWLProperty) properties.add((OWLProperty)property);
    } // while

    return properties;
  } 

  public static Set<OWLProperty> getPossibleOWLPropertiesOfIndividual(OWLModel owlModel, String individualRDFID) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, true);
    HashSet<OWLProperty> properties = new HashSet<OWLProperty>();
    Collection rdfProperties = individual.getPossibleRDFProperties();

    Iterator iterator = rdfProperties.iterator();
    while (iterator.hasNext()) {
      RDFProperty property = (RDFProperty)iterator.next();
      if (property instanceof OWLProperty) properties.add((OWLProperty)property);
    } // while

    return properties;
  } 

  public static String getURI(OWLModel owlModel, String resourceName) throws SWRLOWLUtilException
  {
    RDFResource resource = owlModel.getRDFResource(resourceName);
    
    if (resource == null) throwException("invalid or unknown resource " + resourceName);

    return resource.getURI();
  } 

  public static int getNumberOfOWLPropertyValues(OWLModel owlModel, String individualRDFID, 
                                                  String propertyRDFID, Object propertyValue, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist); // Will throw an exception if mustExist is true
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist); // Will throw an exception if mustExist is true
    int numberOfPropertyValues = 0;

    if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for OWL individual " + individualRDFID);

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

  public static void addOWLPropertyValue(OWLModel owlModel, String resourceName, String propertyRDFID, Object propertyValue) 
    throws SWRLOWLUtilException
  {
  	OWLProperty property = owlModel.getOWLProperty(propertyRDFID);
  	RDFResource resource = owlModel.getRDFResource(resourceName);

	  if (resource == null) throwException("invalid or unknown resource name " + resourceName);
	  if (property == null) throwException("no " + propertyRDFID + " property in ontology");
	  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for RDF resource " + resource.getPrefixedName());
	
	  if (!resource.hasPropertyValue(property, propertyValue)) resource.addPropertyValue(property, propertyValue);
} 

  public static void addOWLPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, Object propertyValue) 
  throws SWRLOWLUtilException
{
  OWLProperty property = owlModel.getOWLProperty(propertyRDFID);

  if (individual == null) throwException("null individual name");
  if (property == null) throwException("no " + propertyRDFID + " property in ontology");
  if (propertyValue == null) throwException("null value for property " + propertyRDFID + " for OWL individual " + individual.getPrefixedName());

  if (!individual.hasPropertyValue(property, propertyValue)) individual.addPropertyValue(property, propertyValue);
} 

  public static void addPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, Object propertyValue) 
    throws SWRLOWLUtilException
  {
  	if (individual == null) throwException("null individual name");
  	if (property == null) throwException("null property for individual " + individual.getPrefixedName());
  	if (propertyValue == null) throwException("null value for property " + property.getPrefixedName() + " for OWL individual " + individual.getPrefixedName());

  	if (!individual.hasPropertyValue(property, propertyValue)) individual.addPropertyValue(property, propertyValue);
  } 


  public static RDFResource getObjectPropertyValue(OWLIndividual individual, OWLProperty property) throws SWRLOWLUtilException
  { 
    Object o = individual.getPropertyValue(property);

    if (!(o instanceof RDFResource)) throw new SWRLOWLUtilException("value " + o + " of object property " + property.getPrefixedName() + 
                                                                    " associated with individual " + individual.getPrefixedName() + 
                                                                    " is not a valid object value");

    return (RDFResource)o;
  }

  public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, String individualRDFID, String propertyRDFID) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individualRDFID, propertyRDFID, true);
  } 

  public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyRDFID) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individual.getName(), propertyRDFID, true);
  } 

  public static Set<OWLIndividual> getOWLObjectPropertyIndividualValues(OWLModel owlModel, OWLIndividual individual, String propertyRDFID,
                                                                         String expectedInstanceClassName) throws SWRLOWLUtilException
  {
    Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

    for (RDFResource value : getObjectPropertyValues(owlModel, individual, propertyRDFID)) {
      if (!(value instanceof OWLIndividual)) 
        throw new SWRLOWLUtilException("value " + value + " for property " + propertyRDFID + " associated with individual " +
                                       individual.getPrefixedName() + " is not an OWL individual");

      OWLIndividual individualValue = (OWLIndividual)value;
      if (!isOWLIndividualOfClass(owlModel, individualValue, expectedInstanceClassName))
        throw new SWRLOWLUtilException("object " + individual.getPrefixedName() + " value for property " + propertyRDFID + " associated with individual " +
                                       individual.getPrefixedName() + " is not of type " + expectedInstanceClassName);

      individuals.add(individualValue);
    } // for
    return individuals;
  } 

  public static OWLIndividual getOWLObjectPropertyIndividualValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, 
                                                                  String expectedInstanceClassName) throws SWRLOWLUtilException
  {
    RDFResource value = getObjectPropertyValue(owlModel, individual, propertyRDFID);

    if (!(value instanceof OWLIndividual))
      throw new SWRLOWLUtilException("invalid value for " + propertyRDFID + " property associated with individual " + 
                                     individual.getPrefixedName() + "; found " + value + ", expecting individual");
    OWLIndividual individualValue = (OWLIndividual)value;
    if (!isOWLIndividualOfClass(owlModel, individualValue, expectedInstanceClassName))
      throw new SWRLOWLUtilException("object " + individualValue.getPrefixedName() + " value for property " + propertyRDFID + " associated with individual " + 
                                     individual.getPrefixedName() + " is not of type " +  expectedInstanceClassName);
    
    return individualValue;
  } 

  public static OWLProperty getOWLObjectPropertyPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID) 
    throws SWRLOWLUtilException
  {
    RDFResource value = getObjectPropertyValue(owlModel, individual, propertyRDFID);

    if (!(value instanceof OWLProperty))
      throw new SWRLOWLUtilException("invalid type for " + propertyRDFID + " property associated with individual " + 
                                     individual.getPrefixedName() + "; found " + value + ", expecting property");
    
    return (OWLProperty)value;
  } 

  public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  { 
    return getObjectPropertyValues(owlModel, individual.getName(), propertyRDFID, mustExist);
  } 

  public static Set<RDFResource> getObjectPropertyValues(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist) 
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist); // Will throw an exception if mustExist is true.
    Set<RDFResource> result = new HashSet<RDFResource>();

    if (individual != null && property != null) {
      Iterator iterator = individual.getPropertyValues(property).iterator();
      while (iterator.hasNext()) {
        Object o = iterator.next();
        if (o instanceof RDFResource) result.add((RDFResource)o);
      } // while
    } // if
    return result;
  } // getObjectPropertyValues

  public static RDFResource getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getObjectPropertyValue(owlModel, individual, propertyRDFID, true);
  } 

  public static RDFResource getObjectPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getObjectPropertyValue(owlModel, individual.getName(), propertyRDFID, mustExist);
  } 

  public static RDFResource getObjectPropertyValue(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getObjectPropertyValue(owlModel, individualRDFID, propertyRDFID, true);
  } 

  public static RDFResource getObjectPropertyValue(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, true);
    Object propertyValue = (property == null && individual == null ? null : individual.getPropertyValue(property));

    if (mustExist && property == null) {
      throwException("no property " + propertyRDFID + " associated with individual " + individual.getPrefixedName());
    } // if

    if (!(propertyValue instanceof RDFResource)) 
      throw new SWRLOWLUtilException("value " + propertyValue + " of object property " + propertyRDFID + 
                                     " associated with individual " + individual.getPrefixedName() + " is not a valid object value");


    return (RDFResource)propertyValue;
  } 

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValue(owlModel, individualRDFID, propertyRDFID, true);
  } 

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLIndividual individual = getOWLIndividual(owlModel, individualRDFID, mustExist); // Will throw an exception if mustExist is true.
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist); // Will throw an exception if mustExist is true.
    Object propertyValue = (individual == null || property == null) ? null : individual.getPropertyValue(property);

    if (mustExist && propertyValue == null)
      throwException("no property '" + propertyRDFID + "' associated with individual '" + individualRDFID + "'");

    return propertyValue;
  } 

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValue(owlModel, individual.getName(), propertyRDFID, mustExist);
  } 

  public static Object getDatavaluedPropertyValue(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValue(owlModel, individual.getName(), property.getName(), mustExist);
  } 

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, true);
    Collection propertyValues = (property == null ? null :individual.getPropertyValues(property));
    Set<Object> result = new HashSet<Object>();

    if (property.isObjectProperty()) 
      throwException("expecting datatype property '" + propertyRDFID + "' for '" + individual.getPrefixedName() + "'");

    if (mustExist && propertyValues == null) {
      throwException("no property '" + propertyRDFID + "' associated with individual '" + individual.getPrefixedName() + "'");
    } // if

    if (propertyValues != null) {
      Iterator iterator = propertyValues.iterator();
      while (iterator.hasNext()) result.add(iterator.next());
    } // if

    return result;
  } 

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    OWLIndividual individual = getIndividual(owlModel, individualRDFID);

    return getDatavaluedPropertyValues(owlModel, individual, propertyRDFID, mustExist);
  } 

  public static Set<Object> getDatavaluedPropertyValues(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValues(owlModel, individualRDFID, propertyRDFID, true);
  }

  public static int getOWLDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyRDFID, mustExist);
    int result = -1;

    try {
      result = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throwException("cannot convert property value '" + s + "' of property '" + propertyRDFID + 
                     "' associated with individual '" + individual.getPrefixedName() + "' to integer");
    } // try
    return result;
  }

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, mustExist);
  }

  public static int getOWLDatavaluedPropertyValueAsInteger(OWLModel owlModel, OWLIndividual individual, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsInteger(owlModel, individual, propertyRDFID, true);
  }

  public static int getDatavaluedPropertyValueAsInteger(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsInteger(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, true);
  } 

  public static long getOWLDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    String s = getDatavaluedPropertyValueAsString(owlModel, individual, propertyRDFID, mustExist);
    long result = -1;

    try {
      result = Long.parseLong(s);
    } catch (NumberFormatException e) {
      throw new SWRLOWLUtilException("cannot convert property value '" + s + "' of property '" + propertyRDFID + 
                                     "' associated with individual '" + individual.getPrefixedName() + "' to long");
    } // try
    return result;
  }

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, mustExist);
  }

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, OWLIndividual individual, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsLong(owlModel, individual, propertyRDFID, true);
  } 

  public static long getDatavaluedPropertyValueAsLong(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getOWLDatavaluedPropertyValueAsLong(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, true);
  }

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyRDFID)
    throws SWRLOWLUtilException
  { 
    return getDatavaluedPropertyValueAsString(owlModel, individual, propertyRDFID, true);
  }

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);

    if (property == null) return null;

    return getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);
  }

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualRDFID, String propertyRDFID, 
                                                          boolean mustExist, String defaultValue)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, mustExist, defaultValue);
  } 

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, true);
  } 

  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, String individualRDFID, String propertyRDFID, 
                                                          boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsString(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, mustExist);
  } 
    
  public static String getDatavaluedPropertyValueAsString(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, 
                                                          boolean mustExist, String defaultValue)
    throws SWRLOWLUtilException
  {
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID, mustExist);
    String propertValueAsString;

    if (property == null) return defaultValue;

    propertValueAsString = getDatavaluedPropertyValueAsString(owlModel, individual, property, mustExist);

    return propertValueAsString == null ? defaultValue : propertValueAsString;
  }

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
  }

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualRDFID, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, mustExist);
  }

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsBoolean(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, true);
  } // getDatavaluedPropertyValueAsBoolean

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, String propertyRDFID, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyRDFID, mustExist);

    if (propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean)) {
      throwException("property value for '" + propertyRDFID + "' associated with individual '" + individual.getPrefixedName() 
                     + "' is not a Boolean");
    } // if

    return (Boolean)propertyValue;
  } 

  public static Boolean getDatavaluedPropertyValueAsBoolean(OWLModel owlModel, OWLIndividual individual, OWLProperty property, boolean mustExist)
    throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, property, mustExist);

    if (propertyValue == null) return null;

    if (!(propertyValue instanceof Boolean))
      throwException("property value for " + property.getPrefixedName() + " in individual " + individual.getPrefixedName() + " is not a Boolean");

    return (Boolean)propertyValue;
  } // getDatavaluedPropertyValueAsBoolean

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsCollection(owlModel, individual, propertyRDFID, true);
  } // getDatavaluedPropertyValueAsCollection

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, String individualRDFID, String propertyRDFID)
    throws SWRLOWLUtilException
  {
    return getDatavaluedPropertyValueAsCollection(owlModel, getIndividual(owlModel, individualRDFID), propertyRDFID, true);
  } // getDatavaluedPropertyValueAsCollection

  public static Collection getDatavaluedPropertyValueAsCollection(OWLModel owlModel, OWLIndividual individual, String propertyRDFID,
                                                                  boolean mustExist) throws SWRLOWLUtilException
  {
    Object propertyValue = getDatavaluedPropertyValue(owlModel, individual, propertyRDFID, mustExist);
    Collection result = new ArrayList();

    if (propertyValue == null) return result;

    if (propertyValue instanceof RDFSLiteral) result.add(propertyValue);
    else if (propertyValue instanceof Collection) result = (Collection)propertyValue;
    else throwException("property value for '" + propertyRDFID + "' associated with individual '" + individual.getPrefixedName() + 
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
    else throwException("property value for '" + property.getPrefixedName() + "' associated with individual '" + individual.getPrefixedName() + 
                        "' is not a Collection");

    return result;
  }

  public static List<OWLNamedClass> getDirectSubClassesOf(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return getDirectSubClassesOf(owlModel, cls);
  }

  public static List<OWLNamedClass> getSubClassesOf(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return getSubClassesOf(owlModel, cls);
  }

  public static List<OWLNamedClass> getDirectSuperClassesOf(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return getDirectSuperClassesOf(owlModel, cls);
  }

  public static List<OWLNamedClass> getSuperClassesOf(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  { 
    OWLNamedClass cls = getNamedClass(owlModel, classRDFID);

    return getSuperClassesOf(owlModel, cls);
  }

  public static List<OWLNamedClass> getSubClassesOf(OWLModel owlModel, OWLNamedClass cls) 
  { 
  	List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();
  	
  	for (Object o : cls.getSubclasses(true)) 
  		if (o instanceof OWLNamedClass)
  			result.add((OWLNamedClass)o);
  	
  	return result;
  } 

  public static List<OWLNamedClass> getDirectSubClassesOf(OWLModel owlModel, OWLNamedClass cls) 
  { 
  	List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();
  	
  	for (Object o : cls.getSubclasses(false)) 
  		if (o instanceof OWLNamedClass)
  			result.add((OWLNamedClass)o);
  	
  	return result;
  } 

  public static List<OWLNamedClass> getSuperClassesOf(OWLModel owlModel, OWLNamedClass cls) 
  { 
  	List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();
  	
  	for (Object o : cls.getSuperclasses(true)) 
  		if (o instanceof OWLNamedClass)
  			result.add((OWLNamedClass)o);
  	
  	return result;
  } 

  public static List<OWLNamedClass> getDirectSuperClassesOf(OWLModel owlModel, OWLNamedClass cls) 
  { 
  	List<OWLNamedClass> result = new ArrayList<OWLNamedClass>();
  	
  	for (Object o : cls.getSuperclasses(false)) 
  		if (o instanceof OWLNamedClass)
  			result.add((OWLNamedClass)o);
  	
  	return result;
  } 

  public static Set<OWLProperty> getDirectSubPropertiesOf(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID);

    return getDirectSubPropertiesOf(owlModel, property);
  } 

  public static Set<OWLProperty> getSubPropertiesOf(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID);

    return getSubPropertiesOf(owlModel, property);
  } 

  public static Set<OWLProperty> getDirectSuperPropertiesOf(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID);

    return getDirectSuperPropertiesOf(owlModel, property);
  }

  public static Set<OWLProperty> getSuperPropertiesOf(OWLModel owlModel, String propertyRDFID) throws SWRLOWLUtilException
  { 
    OWLProperty property = getOWLProperty(owlModel, propertyRDFID);

    return getSuperPropertiesOf(owlModel, property);
  }

  public static Set<OWLProperty> getSubPropertiesOf(OWLModel owlModel, OWLProperty property) 
  { 
  	Set<OWLProperty> result = new HashSet<OWLProperty>();
  	
  	for (Object o : property.getSubproperties(true)) 
  		if (o instanceof OWLProperty)
  			result.add((OWLProperty)o);
  	
  	return result;
  }

  public static Set<OWLProperty> getEquivalentPropertiesOf(OWLModel owlModel, OWLProperty property)
  { 
  	Set<OWLProperty> result = new HashSet<OWLProperty>();
  	
  	for (Object o : property.getEquivalentProperties()) 
  		if (o instanceof OWLProperty)
  			result.add((OWLProperty)o);
  	
  	return result;
  }

  public static Set<OWLProperty> getDirectSubPropertiesOf(OWLModel owlModel, OWLProperty property) throws SWRLOWLUtilException
  { 
  	Set<OWLProperty> result = new HashSet<OWLProperty>();
  	
  	for (Object o : property.getSubproperties(false)) 
  		if (o instanceof OWLProperty)
  			result.add((OWLProperty)o);
  	
  	return result;
  }

  public static Set<OWLProperty> getSuperPropertiesOf(OWLModel owlModel, OWLProperty property) throws SWRLOWLUtilException
  { 
  	Set<OWLProperty> result = new HashSet<OWLProperty>();
  	
  	for (Object o : property.getSuperproperties(true)) 
  		if (o instanceof OWLProperty)
  			result.add((OWLProperty)o);
  	
  	return result;
  }

  public static Set<OWLProperty> getDirectSuperPropertiesOf(OWLModel owlModel, OWLProperty property) throws SWRLOWLUtilException
  { 
  	Set<OWLProperty> result = new HashSet<OWLProperty>();
  	
  	for (Object o : property.getSuperproperties(false)) 
  		if (o instanceof OWLProperty)
  			result.add((OWLProperty)o);
  	
  	return result;
  }

  public static Set<OWLProperty> getDomainProperties(OWLModel owlModel, String classRDFID, boolean transitive) throws SWRLOWLUtilException
  {
    OWLClass cls = getClass(owlModel, classRDFID);
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

  public static Set<String> rdfResources2URIs(Collection resources) 
  {
    Set<String> result = new HashSet<String>();
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof RDFResource) result.add(((RDFResource)o).getURI());
    } // if

    return result;
  } // rdfResources2Names            

  public static Set<String> rdfResources2OWLNamedClassURIs(Collection resources) 
  {
    Set<String> result = new HashSet<String>();
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLNamedClass) result.add(((OWLNamedClass)o).getURI());
    } // if

    return result;
  } 

  public static Set<String> rdfResources2OWLPropertyURIs(Collection resources) 
  {
    Set<String> result = new HashSet<String>();
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof OWLProperty) result.add(((OWLProperty)o).getURI());
    } // if

    return result;
  } 

  public static boolean hasInconsistentClasses(OWLModel owlModel) 
  {
    return !owlModel.getInconsistentClasses().isEmpty();
  } 

  public static String createNewResourceName(OWLModel owlModel, String localNamePrefix) 
  {
    return owlModel.createNewResourceName(localNamePrefix);
  }

  public static RDFSDatatype getRDFSDatatype(OWLModel owlModel, String datatypeName) throws SWRLOWLUtilException
  {
    RDFSDatatype datatype = owlModel.getRDFSDatatypeByName(datatypeName);

    if (datatype == null) throw new SWRLOWLUtilException("error getting RDFSDatatype " + datatypeName);

    return datatype;
  }

  public static RDFSLiteral createRDFSLiteral(OWLModel owlModel, String value, RDFSDatatype datatype) 
    throws SWRLOWLUtilException
  {
    RDFSLiteral literal = owlModel.createRDFSLiteral(value, datatype);

    if (literal == null) 
      throw new SWRLOWLUtilException("error creating RDFSLiteral '" + value + "' of type '" + datatype + "'");

    return literal;
  } 

  public static OWLClass createOWLClassDescription(OWLModel owlModel, String classExpression) throws SWRLOWLUtilException
  {
    OWLClassParser parser = owlModel.getOWLClassParser();
    OWLClass cls = null;

    try {
      cls = (OWLClass)parser.parseClass(owlModel, classExpression);
    } catch (OWLClassParseException e) {
      throw new SWRLOWLUtilException("OWL class expression " + classExpression + " not valid: " + e.getMessage());
    } // try

    return cls;
  } // createOWLClassDescription

  public static OWLNamedClass getOWLThingClass(OWLModel owlModel) 
  {
    return owlModel.getOWLThingClass();
  }

  public static RDFProperty getOWLSameAsProperty(OWLModel owlModel)
  {
    return owlModel.getOWLSameAsProperty();
  }


  public static void makeSameAs(OWLModel owlModel, String individualRDFID1, String individualRDFID2) throws SWRLOWLUtilException
  {
  	OWLIndividual individual1 = getOWLIndividual(owlModel, individualRDFID1);
  	OWLIndividual individual2 = getOWLIndividual(owlModel, individualRDFID2);
  	
  	individual1.addPropertyValue(getOWLSameAsProperty(owlModel), individual2);
  	individual2.addPropertyValue(getOWLSameAsProperty(owlModel), individual1);
  }

  public static void makeDifferentFrom(OWLModel owlModel, String individualRDFID1, String individualRDFID2) throws SWRLOWLUtilException
  {
  	OWLIndividual individual1 = getOWLIndividual(owlModel, individualRDFID1);
  	OWLIndividual individual2 = getOWLIndividual(owlModel, individualRDFID2);
  	
  	individual1.addPropertyValue(getOWLDifferentFromProperty(owlModel), individual2);
  	individual2.addPropertyValue(getOWLDifferentFromProperty(owlModel), individual1);
  }

  public static Collection getOWLAllDifferents(OWLModel owlModel)
  {
    return owlModel.getOWLAllDifferents();
  }

  public static RDFProperty getOWLDifferentFromProperty(OWLModel owlModel)
  {
    return owlModel.getOWLDifferentFromProperty();
  }

  public static OWLProperty getOWLProperty(OWLModel owlModel, String propertyRDFID)
  {
    return owlModel.getOWLProperty(propertyRDFID);
  }

  public static RDFProperty getRDFProperty(OWLModel owlModel, String propertyRDFID)
  {
    return owlModel.getRDFProperty(propertyRDFID);
  }

  public static OWLProperty getOWLProperty(OWLModel owlModel, String propertyRDFID, boolean mustExist) throws SWRLOWLUtilException
  {
    OWLProperty property = owlModel.getOWLProperty(propertyRDFID);

    if (mustExist && property == null) throw new SWRLOWLUtilException("no property named '" + propertyRDFID + "' in ontology");

    return property;
  } 

  public static OWLIndividual getOWLIndividual(OWLModel owlModel, String individualRDFID) throws SWRLOWLUtilException
  {
    return owlModel.getOWLIndividual(individualRDFID);
  } 

  public static RDFSNamedClass getRDFSNamedClass(OWLModel owlModel, String classRDFID)
  {
    return owlModel.getRDFSNamedClass(classRDFID);
  } // getRDFSNamedClass

  public static boolean isSWRLBuiltIn(OWLModel owlModel, String builtInName)
  {
    RDFResource resource = owlModel.getRDFResource(builtInName);
    return resource != null && resource.getProtegeType().getName().equals(edu.stanford.smi.protegex.owl.swrl.model.SWRLNames.Cls.BUILTIN);
  } // isSWRLBuiltIn

  public static boolean isValidClassName(OWLModel owlModel, String classRDFID)
  {
    return owlModel.isValidResourceName(classRDFID, owlModel.getRDFSNamedClassClass());
  } // isValidClassName

  public static void checkIfIsValidClassName(OWLModel owlModel, String classRDFID) throws SWRLOWLUtilException
  {
    if (!isValidClassName(owlModel, classRDFID)) 
    	throw new SWRLOWLUtilException("invalid named class name " + classRDFID);
  } 

  public static boolean isValidURI(String uri)
  {
    return URIUtilities.isValidURI(uri);
  } 

  public static String getPrefixForResourceName(OWLModel owlModel, String resourceName)
  {
    return owlModel.getPrefixForResourceName(resourceName);
  } 

  public static String getLocalNameForURI(OWLModel owlModel, String uri)
  {
    return owlModel.getLocalNameForURI(uri);
  } 

  // Name can be rdf:ID or other annotation property (e.g., rdfs:label)
  public static OWLNamedClass getOWLClassFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    OWLNamedClass cls = null;

    try {
      cls = ParserUtils.getOWLClassFromName(owlModel, id);
      if (cls == null) throw new SWRLOWLUtilException("unknown OWL named class " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous class name " + name);
    } // if 

    return cls;
  } 

  // Name can be rdf:ID or other annotation property (e.g., rdfs:label)
  public static OWLDatatypeProperty getOWLDataPropertyFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    OWLDatatypeProperty property = null;

    try {
      property = ParserUtils.getOWLDatatypePropertyFromName(owlModel, id);
      if (property == null) throw new SWRLOWLUtilException("unknown OWL data property " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous OWL data property name " + name);
    } // if 

    return property;
  } 

  // Name can be rdf:ID or other annotation property (e.g., rdfs:label)
  public static OWLObjectProperty getOWLObjectPropertyFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    OWLObjectProperty property = null;

    try {
      property = ParserUtils.getOWLObjectPropertyFromName(owlModel, id);
      if (property == null) throw new SWRLOWLUtilException("unknown OWL object property " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous OWL object property name " + name);
    } // if 

    return property;
  } 

  // Name can be rdf:ID or other annotation property (e.g., rdfs:label)
  public static OWLIndividual getOWLIndividualFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    OWLIndividual individual = null;

    try {
      individual = ParserUtils.getOWLIndividualFromName(owlModel, id);
      if (individual == null) throw new SWRLOWLUtilException("unknown OWL individual " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous OWL individual name " + name);
    } // if 

    return individual;
  }

  public static RDFProperty getRDFPropertyFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    RDFProperty property = null;

    try {
      property = ParserUtils.getRDFPropertyFromName(owlModel, id);
      if (property == null) throw new SWRLOWLUtilException("unknown RDF property " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous RDF property name " + name);
    } // if 

    return property;
  } 

  public static RDFResource getRDFResourceFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    RDFResource resource = null;

    try {
      resource = ParserUtils.getRDFResourceFromName(owlModel, id);
      if (resource == null) throw new SWRLOWLUtilException("unknown RDF resource " + name);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous RDF resource name " + name);
    } // if 

    return resource;
  } 

  public static boolean isExistingRDFResourceWithRDFSLabel(OWLModel owlModel, String label) 
  {
    String id = ParserUtils.dequoteIdentifier(label);
    
    try {
      if (getRDFResourceFromRDFSLabel(owlModel, id) != null) return true;
    } catch (SWRLOWLUtilException e) {
    	return false;
    } catch (AmbiguousNameException e) {
      return true;
    } // if 

    return false;
  } 

  public static RDFResource getRDFResourceFromRDFSLabel(OWLModel owlModel, String label) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(label);
    RDFResource resource = null;

    try {
      resource = ParserUtils.getRDFResourceFromRDFSLabel(owlModel, id);
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous RDF resource name " + label);
    } // if 

    return resource;
  } 

  public static RDFSClass getRDFSClassFromName(OWLModel owlModel, String name) throws SWRLOWLUtilException
  {
    String id = ParserUtils.dequoteIdentifier(name);
    RDFSClass cls = null;

    try {
      RDFResource resource = ParserUtils.getRDFSClassFromName(owlModel, id);
      if (resource == null) 
      	throw new SWRLOWLUtilException("unknown RDF resource " + name);
      
      if (!(resource instanceof RDFSClass)) 
      	throw new SWRLOWLUtilException("name " + name + " is not an RDFSClass");
      
      cls = (RDFSClass)resource;
    } catch (AmbiguousNameException e) {
      throw new SWRLOWLUtilException("ambiguous RDFS class name " + name);
    } // if 

    return cls;
  } 

  private static void throwException(String message) throws SWRLOWLUtilException
  {
    throw new SWRLOWLUtilException(message);
  } 
} 
