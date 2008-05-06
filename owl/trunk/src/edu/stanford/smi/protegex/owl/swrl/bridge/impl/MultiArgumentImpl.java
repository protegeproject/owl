
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

/**
 ** A class used to bind multiple arguments to a built-in argument
 */
public class MultiArgumentImpl extends BuiltInArgumentImpl implements MultiArgument
{
  private List<BuiltInArgument> arguments;

  public MultiArgumentImpl(String variableName, String prefixedVariableName)
  {
    super(variableName, prefixedVariableName);
    arguments = new ArrayList<BuiltInArgument>();
  } // MultiArgumentImpl

  public MultiArgumentImpl(String variableName, String prefixedVariableName, List<BuiltInArgument> arguments)
  {
    super(variableName, prefixedVariableName);
    this.arguments = arguments;
  } // MultiArgumentImpl

  public void addArgument(BuiltInArgument argument) { arguments.add(argument); }
  public void setArguments(List<BuiltInArgument> arguments) { this.arguments = arguments; }
  public List<BuiltInArgument> getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }
  public boolean hasNoArguments() { return arguments.size() == 0; }
} // MultiArgumentImpl
