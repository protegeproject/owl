
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

/**
 ** Interface representing an argument to a built-in
 */
public interface BuiltInArgument extends Argument
{
  boolean isVariable();
  boolean isUnbound();
  String getVariableName() throws BuiltInException;
  void setVariableName(String variableName);
  void setUnbound();
  void setBuiltInResult(BuiltInArgument builtInResult) throws BuiltInException;
  BuiltInArgument getBuiltInResult() throws BuiltInException;
} // BuiltInArgument
