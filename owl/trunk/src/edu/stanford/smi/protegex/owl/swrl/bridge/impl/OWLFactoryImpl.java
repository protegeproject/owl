
// TODO: very long - needs serious refactoring

package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClassAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClassPropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLConversionFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypePropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDeclarationAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDifferentIndividualsAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLEntity;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLObjectPropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLPropertyPropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLSameIndividualsAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLSubClassAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRule;
import edu.stanford.smi.protegex.owl.swrl.bridge.VariableAtomArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.VariableBuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

public class OWLFactoryImpl implements OWLFactory
{
  private Map<String, OWLClass> classes = new HashMap<String, OWLClass>();
  private Map<String, OWLIndividual> individuals = new HashMap<String, OWLIndividual>();
  private Map<String, OWLObjectProperty> objectProperties = new HashMap<String, OWLObjectProperty>();
  private Map<String, OWLDatatypeProperty> dataProperties = new HashMap<String, OWLDatatypeProperty>();

  private OWLModel owlModel;
  private OWLConversionFactory conversionFactory;

  public OWLFactoryImpl() { owlModel = null; }

  public OWLFactoryImpl(OWLModel owlModel) 
  { 
    this.owlModel = owlModel; 
    conversionFactory = new OWLConversionFactoryImpl(owlModel, this);
  } // OWLFactoryImpl

  public Set<SWRLRule> getSWRLRules() throws OWLFactoryException
  {
    Set<SWRLRule> result = new HashSet<SWRLRule>();
    
    if (hasOWLModel()) {
      try {
        result = conversionFactory.getSWRLRules();
      } catch (OWLConversionFactoryException e) {
    	  throw new OWLFactoryException("conversion exception getting SWRL rule or SQWRL query: " + e.getMessage());
      } catch (SQWRLException e) {
    	  throw new OWLFactoryException("SQWRL exception getting query: " + e.getMessage());
      } catch (BuiltInException e) {
    	  throw new OWLFactoryException("built-in exception getting SWRL rule or SQWRL query: " + e.getMessage());      
      } // try
    } // if

    return result;
  } // getSWRLRules

  // Basic OWL entities

  public OWLClass getOWLClass() 
  {
    return conversionFactory.getOWLClass();
  } // getOWLClass

  public OWLClass getOWLClass(String classURI)
  { 
    OWLClass owlClass = null;
    
    if (classes.containsKey(classURI)) owlClass = classes.get(classURI);
    else {
      if (hasOWLModel()) {
        try {
          owlClass = conversionFactory.getOWLClass(classURI);
        } catch (OWLConversionFactoryException e) {
          owlClass = new OWLClassImpl(classURI); 
        } // try
      } else owlClass = new OWLClassImpl(classURI); 
      classes.put(classURI, owlClass);
    } // if
    
    return owlClass; 
  } // getOWLClass
    
  public OWLIndividual getOWLIndividual(String individualURI)
  {
    OWLIndividual owlIndividual = null;
    
    if (individuals.containsKey(individualURI)) owlIndividual = individuals.get(individualURI);
    else {
      if (hasOWLModel()) {
        try {
          owlIndividual = conversionFactory.getOWLIndividual(individualURI);
        } catch (OWLConversionFactoryException e) {
          owlIndividual = new OWLIndividualImpl(individualURI); 
        } // try
      } else owlIndividual = new OWLIndividualImpl(individualURI); 
      individuals.put(individualURI, owlIndividual);
    } // if
    
    return owlIndividual; 
  } // getOWLIndividual

  public OWLObjectProperty getOWLObjectProperty(String propertyURI)
  {
    OWLObjectProperty property;
    
    if (objectProperties.containsKey(propertyURI)) property = objectProperties.get(propertyURI);
    else {
      if (hasOWLModel()) {
        try {
          property = conversionFactory.getOWLObjectProperty(propertyURI);
        } catch (OWLConversionFactoryException e) {
          property = new OWLObjectPropertyImpl(propertyURI); 
        } // try
      } else property = new OWLObjectPropertyImpl(propertyURI); 
      objectProperties.put(propertyURI, property);
    } // if
    
    return property; 
  } // getOWLObjectProperty

  public OWLDatatypeProperty getOWLDataProperty(String propertyURI)
  {
    OWLDatatypeProperty property;
    
    if (dataProperties.containsKey(propertyURI)) property = dataProperties.get(propertyURI);
    else {
      if (hasOWLModel()) {
        try {
          property = conversionFactory.getOWLDataProperty(propertyURI);
        } catch (OWLConversionFactoryException e) {
          property = new OWLDatatypePropertyImpl(propertyURI); 
        } // try
      } else property = new OWLDatatypePropertyImpl(propertyURI); 
      dataProperties.put(propertyURI, property);
    } // if
    
    return property; 
  } // getOWLDataProperty

  public OWLDatatypeValue getOWLDataValue(String s) { return new OWLDatatypeValueImpl(s); }
  public OWLDatatypeValue getOWLDataValue(Number n) { return new OWLDatatypeValueImpl(n); }
  public OWLDatatypeValue getOWLDataValue(boolean b){ return new OWLDatatypeValueImpl(b); }
  public OWLDatatypeValue getOWLDataValue(int i) { return new OWLDatatypeValueImpl(i); }
  public OWLDatatypeValue getOWLDataValue(long l) { return new OWLDatatypeValueImpl(l); }
  public OWLDatatypeValue getOWLDataValue(float f) { return new OWLDatatypeValueImpl(f); }
  public OWLDatatypeValue getOWLDataValue(double d){ return new OWLDatatypeValueImpl(d); }
  public OWLDatatypeValue getOWLDataValue(short s) { return new OWLDatatypeValueImpl(s); }
  public OWLDatatypeValue getOWLDataValue(Byte b) { return new OWLDatatypeValueImpl(b); }
  public OWLDatatypeValue getOWLDataValue(XSDType xsd) { return new OWLDatatypeValueImpl(xsd); }
  public OWLDatatypeValue getOWLDataValue(Object o) throws DatatypeConversionException { return new OWLDatatypeValueImpl(o); } 

  // OWL axioms
  public OWLDatatypePropertyAssertionAxiom getOWLDataPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object) { return new OWLDatatypePropertyAssertionAxiomImpl(subject, property, object); }
  public OWLObjectPropertyAssertionAxiom getOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object)  { return new OWLObjectPropertyAssertionAxiomImpl(subject, property, object); }
  public OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) { return new OWLDifferentIndividualsAxiomImpl(individual1, individual2); }
  public OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(Set<OWLIndividual> individuals) { return new OWLDifferentIndividualsAxiomImpl(individuals); }
  public OWLSameIndividualsAxiom getOWLSameIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) { return new OWLSameIndividualsAxiomImpl(individual1, individual2); }
  public OWLClassPropertyAssertionAxiom getOWLClassPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLClass object)  { return new OWLClassPropertyAssertionAxiomImpl(subject, property, object); } // OWL Full
  public OWLPropertyPropertyAssertionAxiom getOWLPropertyPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLProperty object)  { return new OWLPropertyPropertyAssertionAxiomImpl(subject, property, object); } // OWL Full
  public OWLClassAssertionAxiom getOWLClassAssertionAxiom(OWLIndividual individual, OWLClass description)  { return new OWLClassAssertionAxiomImpl(individual, description); } // TODO: should be OWLDescription
  public OWLSubClassAxiom getOWLSubClassAxiom(OWLClass subClass, OWLClass superClass)  { return new OWLSubClassAxiomImpl(subClass, superClass); } // TODO: should be OWLDescription

  public OWLDeclarationAxiom getOWLDeclarationAxiom(OWLEntity owlEntity) { return new OWLDeclarationAxiomImpl(owlEntity); }
  public OWLSomeValuesFrom getOWLSomeValuesFrom(OWLClass owlClass, OWLProperty onProperty, OWLClass someValuesFrom)  { return new OWLSomeValuesFromImpl(owlClass, onProperty, someValuesFrom); } // TODO: should be OWLDescription

  // Arguments to atoms and built-ins
  public VariableAtomArgument getSWRLVariableAtomArgument(String variableURI, String prefixedVariableName) { return new VariableAtomArgumentImpl(variableURI, prefixedVariableName); }
  public VariableBuiltInArgument getSWRLVariableBuiltInArgument(String variableURI, String prefixedVariableName) { return new VariableBuiltInArgumentImpl(variableURI, prefixedVariableName); }
  public BuiltInArgument getSWRLBuiltInArgument(String variableURI, String prefixedVariableName) { return new BuiltInArgumentImpl(variableURI, prefixedVariableName); }

  public BuiltInAtom getSWRLBuiltInAtom(String builtInURI, String builtInPrefixedName, List<BuiltInArgument> arguments) 
  { 
    return new BuiltInAtomImpl(builtInURI, builtInPrefixedName, arguments); 
  } // getSWRLBuiltInAtom

  private boolean hasOWLModel() { return owlModel != null; }

} // OWLFactoryImpl
