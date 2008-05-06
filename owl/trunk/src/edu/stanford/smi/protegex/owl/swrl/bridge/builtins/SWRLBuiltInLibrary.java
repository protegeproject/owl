
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.reflect.*;

/** 
 ** A class that defined methods that must be implemented by a built-in library. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation.
 */
public interface SWRLBuiltInLibrary
{
  String getLibraryName();

  SWRLRuleEngineBridge getInvokingBridge() throws BuiltInException;
  String getInvokingRuleName() throws BuiltInException;
  int getInvokingBuiltInIndex() throws BuiltInException;
  boolean getIsInConsequent() throws BuiltInException;

  void reset() throws BuiltInException;
  void invokeResetMethod(SWRLRuleEngineBridge bridge) throws BuiltInException;

  boolean invokeBuiltInMethod(Method method, SWRLRuleEngineBridge bridge, String ruleName, 
                              String prefix, String builtInMethodName, int builtInIndex, boolean isInConsequent,
                              List<BuiltInArgument> arguments) 
    throws BuiltInException;

} // SWRLBuiltInLibrary
