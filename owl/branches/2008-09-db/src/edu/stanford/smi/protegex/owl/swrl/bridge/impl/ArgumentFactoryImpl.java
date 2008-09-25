
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.PrimitiveXSDType;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.*;

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
  public DatatypeValueArgument createDatatypeValueArgument(BigDecimal bd) { return new OWLDatatypeValueImpl(bd); }
  public DatatypeValueArgument createDatatypeValueArgument(BigInteger bi) { return new OWLDatatypeValueImpl(bi); }
  public DatatypeValueArgument createDatatypeValueArgument(PrimitiveXSDType xsd) { return new OWLDatatypeValueImpl(xsd); }
  public DatatypeValueArgument createDatatypeValueArgument(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException { return new OWLDatatypeValueImpl(owlModel, literal); }

  public MultiArgument createMultiArgument(String variableName, String prefixedVariableName) { return new MultiArgumentImpl(variableName, prefixedVariableName); }
  public MultiArgument createMultiArgument(String variableName, String prefixedVariableName, List<BuiltInArgument> arguments) { return new MultiArgumentImpl(variableName, prefixedVariableName, arguments); }
} // ArgumentFactory
