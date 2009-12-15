
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.ArgumentFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DatatypePropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DatatypeValueArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.IndividualArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.ObjectPropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;

public class ArgumentFactoryImpl extends ArgumentFactory
{
  public ClassArgument createClassArgument(String className) { return new OWLClassImpl(className); }

  public IndividualArgument createIndividualArgument(String individualName) { return new OWLIndividualImpl(individualName); }

  public ObjectPropertyArgument createObjectPropertyArgument(String propertyName) { return new OWLObjectPropertyImpl(propertyName); }
  public DatatypePropertyArgument createDatatypePropertyArgument(String propertyName) { return new OWLDatatypePropertyImpl(propertyName); }

  public DatatypeValueArgument createDatatypeValueArgument(String s) { return new OWLDatatypeValueImpl(s); }
  public DatatypeValueArgument createDatatypeValueArgument(Number n) { return new OWLDatatypeValueImpl(n); }
  public DatatypeValueArgument createDatatypeValueArgument(boolean b){ return new OWLDatatypeValueImpl(b); }
  public DatatypeValueArgument createDatatypeValueArgument(int i) { return new OWLDatatypeValueImpl(i); }
  public DatatypeValueArgument createDatatypeValueArgument(long l) { return new OWLDatatypeValueImpl(l); }
  public DatatypeValueArgument createDatatypeValueArgument(float f) { return new OWLDatatypeValueImpl(f); }
  public DatatypeValueArgument createDatatypeValueArgument(double d){ return new OWLDatatypeValueImpl(d); }
  public DatatypeValueArgument createDatatypeValueArgument(short s) { return new OWLDatatypeValueImpl(s); }
  public DatatypeValueArgument createDatatypeValueArgument(Byte b) { return new OWLDatatypeValueImpl(b); }
  public DatatypeValueArgument createDatatypeValueArgument(XSDType xsd) { return new OWLDatatypeValueImpl(xsd); }

  public DatatypeValueArgument createDatatypeValueArgument(OWLDatatypeValue datatypeValue) { return datatypeValue; }

  public MultiArgument createMultiArgument(String variableName, String prefixedVariableName) { return new MultiArgumentImpl(variableName, prefixedVariableName); }
  public MultiArgument createMultiArgument(String variableName, String prefixedVariableName, List<BuiltInArgument> arguments) { return new MultiArgumentImpl(variableName, prefixedVariableName, arguments); }
} // ArgumentFactory
