
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLBuiltInBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInLibraryException;

import java.util.List;
import java.lang.reflect.Method;

/** 
 ** A class that defined methods that must be implemented by a built-in library. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation.
 */
public interface SWRLBuiltInLibrary
{
  String getLibraryName();

  SWRLBuiltInBridge getInvokingBridge() throws BuiltInLibraryException;
  String getInvokingRuleName() throws BuiltInLibraryException;
  int getInvokingBuiltInIndex() throws BuiltInLibraryException;
  boolean getIsInConsequent() throws BuiltInLibraryException;

  void reset() throws BuiltInException;
  void invokeResetMethod(SWRLBuiltInBridge bridge) throws BuiltInLibraryException;

  boolean invokeBuiltInMethod(Method method, SWRLBuiltInBridge bridge, String ruleName, 
                              String prefix, String builtInMethodName, int builtInIndex, boolean isInConsequent,
                              List<BuiltInArgument> arguments) 
    throws BuiltInException;

} // SWRLBuiltInLibrary
