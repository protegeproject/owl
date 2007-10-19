
// TODO: should probably a more specific exception - not SWRLRuleEngineBridgeException

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 ** Factory to create instances of rule engine bridges
 */
public class BridgeFactory
{
  private static HashMap<String, BridgeCreator> registeredBridges;

  static {
    registeredBridges = new HashMap<String, BridgeCreator>();
  } // static

  static {

    try { // TODO:  Hack until we can do a proper class load with the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridge");
    } catch (ClassNotFoundException e) {
      System.err.println("SWRLJessBridge load failed");
    } // try
  } // static

  public static void registerBridge(String bridgeName, BridgeCreator bridgeCreator)
  {
    if (registeredBridges.containsKey(bridgeName)) {
      registeredBridges.remove(bridgeName);
      registeredBridges.put(bridgeName, bridgeCreator);
    } else registeredBridges.put(bridgeName, bridgeCreator);

    System.out.println("Rule engine '" + bridgeName + "' registered with the SWRLTab bridge.");
  } // registerBridge

  public static boolean isBridgeRegistered(String bridgeName) { return registeredBridges.containsKey(bridgeName); }
  public static Set<String> getRegisteredBridgeNames() { return registeredBridges.keySet(); }

  /**
   ** Create an instance of a rule engine - a random registered engine is returned. If no engine is registered, a
   ** NoRegisteredBridgesException is returned.
   */
  public static SWRLRuleEngineBridge createBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    if (!registeredBridges.isEmpty()) return createBridge(registeredBridges.keySet().iterator().next(), owlModel);
    else throw new NoRegisteredBridgesException();
  } // createBridge

  /**
   ** Create an instance of a named rule engine. Throws an InvalidBridgeNameException if an engine of this name is not registered.
   */
  public static SWRLRuleEngineBridge createBridge(String bridgeName, OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    SWRLRuleEngineBridge bridge = null;

    if (registeredBridges.containsKey(bridgeName)) {

      try {
        bridge = registeredBridges.get(bridgeName).create(owlModel);
      } catch (Throwable e) {
        throw new SWRLRuleEngineBridgeException("Error creating rule engine '" + bridgeName + "': " + e.getMessage());
      } // try

    } else throw new InvalidBridgeNameException(bridgeName);

    return bridge;
  } // createBridge

  public static void unregisterBridge(String bridgeName)
  {
    if (registeredBridges.containsKey(bridgeName)) registeredBridges.remove(bridgeName);
  } // unregisterBridge

  public interface BridgeCreator
  {
    SWRLRuleEngineBridge create(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
  } // BridgeCreator

  // TODO: these methods need to be far more intelligent, e.g., not creating duplicate implementations for the same underlying object

  // SWRL atoms
  public static BuiltInAtom createBuiltInAtom(OWLModel owlModel, SWRLBuiltinAtom atom) throws SWRLRuleEngineBridgeException  { return new BuiltInAtomImpl(owlModel, atom); } 
  public static ClassAtom createClassAtom(SWRLClassAtom atom) throws SWRLRuleEngineBridgeException { return new ClassAtomImpl(atom); }
  public static DataRangeAtom createDataRangeAtom(SWRLDataRangeAtom atom) throws SWRLRuleEngineBridgeException { return new DataRangeAtomImpl(atom); }
  public static DatavaluedPropertyAtom createDatavaluedPropertyAtom(OWLModel owlModel, SWRLDatavaluedPropertyAtom atom) throws SWRLRuleEngineBridgeException { return new DatavaluedPropertyAtomImpl(owlModel, atom); }
  public static DifferentIndividualsAtom createDifferentIndividualsAtom(SWRLDifferentIndividualsAtom atom) throws SWRLRuleEngineBridgeException { return new DifferentIndividualsAtomImpl(atom); }
  public static IndividualPropertyAtom createIndividualPropertyAtom(SWRLIndividualPropertyAtom atom) throws SWRLRuleEngineBridgeException { return new IndividualPropertyAtomImpl(atom); }
  public static SameIndividualAtom createSameIndividualAtom(SWRLSameIndividualAtom atom) throws SWRLRuleEngineBridgeException { return new SameIndividualAtomImpl(atom); }

  // SWRL and basic OWL entities
  public static SWRLRule createSWRLRule(String ruleName, List<Atom> bodyAtoms, List<Atom> headAtoms) throws SWRLRuleEngineBridgeException { return new SWRLRuleImpl(ruleName, bodyAtoms, headAtoms); }
  public static OWLClass createOWLClass(OWLModel owlModel, String className) throws SWRLRuleEngineBridgeException { return new OWLClassImpl(owlModel, className); }
  public static OWLClass createOWLClass(String className) { return new OWLClassImpl(className); }
  public static OWLIndividual createOWLIndividual(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws SWRLRuleEngineBridgeException { return new OWLIndividualImpl(individual); }
  public static OWLIndividual createOWLIndividual(String individualName) { return new OWLIndividualImpl(individualName); }
  public static OWLIndividual createOWLIndividual(OWLModel owlModel, String individualName) throws SWRLRuleEngineBridgeException { return new OWLIndividualImpl(owlModel, individualName); }
  public static OWLIndividual createOWLIndividual(String individualName, String className) { return new OWLIndividualImpl(individualName, className); }
  public static OWLProperty createOWLProperty(String propertyName) { return new OWLPropertyImpl(propertyName); }
  public static OWLDatatypeValue createOWLDatatypeValue(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException { return new OWLDatatypeValueImpl(owlModel, literal); }
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
  public static OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object) throws SWRLRuleEngineBridgeException { return new OWLDatatypePropertyAssertionAxiomImpl(subject, property, object); }
  public static OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object) throws SWRLRuleEngineBridgeException { return new OWLObjectPropertyAssertionAxiomImpl(subject, property, object); }
  public static OWLDifferentIndividualsAxiom createOWLDifferentIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) throws SWRLRuleEngineBridgeException { return new OWLDifferentIndividualsAxiomImpl(individual1, individual2); }
  public static OWLDifferentIndividualsAxiom createOWLDifferentIndividualsAxiom(Set<OWLIndividual> individuals) throws SWRLRuleEngineBridgeException { return new OWLDifferentIndividualsAxiomImpl(individuals); }
  public static OWLSameIndividualsAxiom createOWLSameIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2) throws SWRLRuleEngineBridgeException { return new OWLSameIndividualsAxiomImpl(individual1, individual2); }

  // Arguments
  public static MultiArgument createMultiArgument(String variableName) { return new MultiArgumentImpl(variableName); }
  public static MultiArgument createMultiArgument(String variableName, List<BuiltInArgument> arguments) { return new MultiArgumentImpl(variableName, arguments); }
  public static VariableAtomArgument createVariableAtomArgument(String variableName) throws SWRLRuleEngineBridgeException { return new VariableAtomArgumentImpl(variableName); }
  public static VariableBuiltInArgument createVariableBuiltInArgument(String variableName) throws SWRLRuleEngineBridgeException { return new VariableBuiltInArgumentImpl(variableName); }
  public static BuiltInArgument createBuiltInArgument(String variableName) { return new BuiltInArgumentImpl(variableName); }

} // BridgeFactory
