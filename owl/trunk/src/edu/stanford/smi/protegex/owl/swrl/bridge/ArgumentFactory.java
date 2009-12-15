
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.ArgumentFactoryImpl;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;

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
  public abstract DatatypeValueArgument createDatatypeValueArgument(XSDType xsd);

  public abstract DatatypeValueArgument createDatatypeValueArgument(OWLDatatypeValue datatypeValue);

  public abstract MultiArgument createMultiArgument(String variableName, String prefixedVariableName);
  public abstract MultiArgument createMultiArgument(String variableName, String prefixedVariableName, List<BuiltInArgument> arguments);
} // ArgumentFactory
