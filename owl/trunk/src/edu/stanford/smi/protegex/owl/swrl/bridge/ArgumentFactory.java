
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.ArgumentFactoryImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.PrimitiveXSDType;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.PrimitiveXSDType;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.*;

public abstract class ArgumentFactory
{
  public static ArgumentFactory getFactory() { return new ArgumentFactoryImpl(); }

  public abstract ClassArgument createClassArgument(String className);

  public abstract IndividualArgument createIndividualArgument(String individualName);

  public abstract ObjectPropertyArgument createObjectPropertyArgument(String propertyName);
  public abstract DatatypePropertyArgument createDatatypePropertyArgument(String propertyName);

  public abstract DatatypeValueArgument createDatatypeValueArgument(String s);
  public abstract DatatypeValueArgument createDatatypeValueArgument(Number n);
  public abstract DatatypeValueArgument createDatatypeValueArgument(boolean b);
  public abstract DatatypeValueArgument createDatatypeValueArgument(int i);
  public abstract DatatypeValueArgument createDatatypeValueArgument(long l);
  public abstract DatatypeValueArgument createDatatypeValueArgument(float f);
  public abstract DatatypeValueArgument createDatatypeValueArgument(double d);
  public abstract DatatypeValueArgument createDatatypeValueArgument(short s); 
  public abstract DatatypeValueArgument createDatatypeValueArgument(Byte b);
  public abstract DatatypeValueArgument createDatatypeValueArgument(BigDecimal bd);
  public abstract DatatypeValueArgument createDatatypeValueArgument(BigInteger bi);
  public abstract DatatypeValueArgument createDatatypeValueArgument(PrimitiveXSDType xsd);
  public abstract DatatypeValueArgument createDatatypeValueArgument(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException;

  public abstract MultiArgument createMultiArgument(String variableName);
  public abstract MultiArgument createMultiArgument(String variableName, List<BuiltInArgument> arguments);
} // ArgumentFactory
