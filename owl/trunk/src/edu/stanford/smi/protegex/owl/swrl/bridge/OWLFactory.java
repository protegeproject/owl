
// TODO: Skeletal. Needs to have a separate implementation and factory creation mechanism and needs to be more intelligent and not
// create duplicate objects for identical entities

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

 import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 ** Factory to create bridge instances of OWL entities that reflect OWL entities in a source OWL model or entities created inside the bridge
 ** (that may or may not ultimately be transferred to the source OWL model).
 */
public class OWLFactory
{
  // SWRL entities
  public static SWRLRule createSWRLRule(String ruleName, List<Atom> bodyAtoms, List<Atom> headAtoms) throws SQWRLException, BuiltInException { return new SWRLRuleImpl(ruleName, bodyAtoms, headAtoms); }

  public static BuiltInAtom createBuiltInAtom(String builtInURI, String builtInPrefixedName, List<BuiltInArgument> arguments) { return new BuiltInAtomImpl(builtInURI, builtInPrefixedName, arguments); } 
  // Basic OWL entities

  public static OWLClass createOWLClass(String classURI) { return new OWLClassImpl(classURI); }
  public static OWLClass createOWLClass(String classURI, String superclassURI) { return new OWLClassImpl(classURI, superclassURI); }

  public static OWLIndividual createOWLIndividual(String individualURI) { return new OWLIndividualImpl(individualURI); }
  public static OWLIndividual createOWLIndividual(String individualURI, OWLClass owlClass) { return new OWLIndividualImpl(individualURI, individualURI, owlClass); }

  public static OWLIndividual generateOWLIndividual(String individualURI, String prefixedIndividualName, OWLClass owlClass) { return new OWLIndividualImpl(individualURI, prefixedIndividualName, owlClass); }

  public static OWLObjectProperty createOWLObjectProperty(String propertyURI) { return new OWLObjectPropertyImpl(propertyURI); }

  public static OWLDatatypeProperty createOWLDatatypeProperty(String propertyURI) { return new OWLDatatypePropertyImpl(propertyURI); }

  public static OWLDatatypeValue createOWLDatatypeValue(String s) { return new OWLDatatypeValueImpl(s); }
  public static OWLDatatypeValue createOWLDatatypeValue(Number n) { return new OWLDatatypeValueImpl(n); }
  public static OWLDatatypeValue createOWLDatatypeValue(boolean b){ return new OWLDatatypeValueImpl(b); }
  public static OWLDatatypeValue createOWLDatatypeValue(int i) { return new OWLDatatypeValueImpl(i); }
  public static OWLDatatypeValue createOWLDatatypeValue(long l) { return new OWLDatatypeValueImpl(l); }
  public static OWLDatatypeValue createOWLDatatypeValue(float f) { return new OWLDatatypeValueImpl(f); }
  public static OWLDatatypeValue createOWLDatatypeValue(double d){ return new OWLDatatypeValueImpl(d); }
  public static OWLDatatypeValue createOWLDatatypeValue(short s) { return new OWLDatatypeValueImpl(s); }
  public static OWLDatatypeValue createOWLDatatypeValue(Byte b) { return new OWLDatatypeValueImpl(b); }
  public static OWLDatatypeValue createOWLDatatypeValue(BigDecimal bd) { return new OWLDatatypeValueImpl(bd); }
  public static OWLDatatypeValue createOWLDatatypeValue(BigInteger bi) { return new OWLDatatypeValueImpl(bi); }
  public static OWLDatatypeValue createOWLDatatypeValue(PrimitiveXSDType xsd) { return new OWLDatatypeValueImpl(xsd); }

  // OWL axioms
  public static OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object) { return new OWLDatatypePropertyAssertionAxiomImpl(subject, property, object); }
  public static OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object)  { return new OWLObjectPropertyAssertionAxiomImpl(subject, property, object); }
  public static OWLDifferentIndividualsAxiom createOWLDifferentIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) { return new OWLDifferentIndividualsAxiomImpl(individual1, individual2); }
  public static OWLDifferentIndividualsAxiom createOWLDifferentIndividualsAxiom(Set<OWLIndividual> individuals) { return new OWLDifferentIndividualsAxiomImpl(individuals); }
  public static OWLSameIndividualsAxiom createOWLSameIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) { return new OWLSameIndividualsAxiomImpl(individual1, individual2); }
  public static OWLClassPropertyAssertionAxiom createOWLClassPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLClass object)  { return new OWLClassPropertyAssertionAxiomImpl(subject, property, object); } // OWL Full
  public static OWLPropertyPropertyAssertionAxiom createOWLPropertyPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLProperty object)  { return new OWLPropertyPropertyAssertionAxiomImpl(subject, property, object); } // OWL Full
  public static OWLClassAssertionAxiom createOWLClassAssertionAxiom(OWLIndividual individual, OWLClass description)  { return new OWLClassAssertionAxiomImpl(individual, description); } // TODO: should be OWLDescription
  public static OWLSubClassAxiom createOWLSubClassAxiom(OWLClass subClass, OWLClass superClass)  { return new OWLSubClassAxiomImpl(subClass, superClass); } // TODO: should be OWLDescription

  public static OWLSomeValuesFrom createOWLSomeValuesFrom(OWLClass owlClass, OWLProperty onProperty, OWLClass someValuesFrom)  { return new OWLSomeValuesFromImpl(owlClass, onProperty, someValuesFrom); } // TODO: should be OWLDescription

  // Arguments to atoms and built-ins
  public static VariableAtomArgument createVariableAtomArgument(String variableURI, String prefixedVariableName) { return new VariableAtomArgumentImpl(variableURI, prefixedVariableName); }
  public static VariableBuiltInArgument createVariableBuiltInArgument(String variableURI, String prefixedVariableName) { return new VariableBuiltInArgumentImpl(variableURI, prefixedVariableName); }
  public static BuiltInArgument createBuiltInArgument(String variableURI, String prefixedVariableName) { return new BuiltInArgumentImpl(variableURI, prefixedVariableName); }

} // OWLFactory
