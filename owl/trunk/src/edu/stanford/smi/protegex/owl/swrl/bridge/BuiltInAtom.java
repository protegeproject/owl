
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;

import java.util.Set;
import java.util.List;

/**
 ** Interface representing a SWRL built-in atom
 */
public interface BuiltInAtom extends Atom
{
  String getBuiltInName();
  List<BuiltInArgument> getArguments();
  int getNumberOfArguments();
  int getBuiltInIndex();
  void setBuiltInIndex(int builtInIndex);

  boolean usesAtLeastOneVariableOf(Set<String> variableNames) throws BuiltInException;
  boolean isArgumentAVariable(int argumentNumber) throws BuiltInException;
  boolean isArgumentUnbound(int argumentNumber) throws BuiltInException;
  boolean hasUnboundArguments();
  Set<String> getUnboundArgumentVariableNames() throws BuiltInException;
  String getArgumentVariableName(int argumentNumber) throws BuiltInException;
  Set<String> getArgumentsVariableNames() throws BuiltInException;
  void addArguments(List<BuiltInArgument> additionalArguments);

  boolean usesSQWRLVariables();
  void setUsesSQWRLVariables();
  boolean isSQWRLMakeCollection();
  void setIsSQWRLMakeCollection();
} // BuiltInAtom

