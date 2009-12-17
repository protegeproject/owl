
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;

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
