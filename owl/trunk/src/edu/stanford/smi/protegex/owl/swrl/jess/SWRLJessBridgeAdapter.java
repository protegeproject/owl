
package edu.stanford.smi.protegex.owl.swrl.bridge.jess;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.icons.JessIcons;
import jess.*;

import java.util.*;

public class SWRLJessBridgeAdapter
{
  private static SWRLJessBridge bridge;

  public static void setBridge(SWRLJessBridge _bridge) { bridge = _bridge; }

  public static void assertIndividual(String individualName, String className) throws SWRLRuleEngineBridgeException 
  {
    bridge.assertIndividual(individualName, className);
  } // assertIndividual
  
  public static void assertProperty(String propertyName, String subjectName, String predicateValue) throws SWRLRuleEngineBridgeException
  {
    bridge.assertProperty(propertyName, subjectName, predicateValue);
  } // assertProperty
  
  public static boolean invokeSWRLBuiltIn(String builtInName, List arguments) throws BuiltInException
  {
      return bridge.invokeSWRLBuiltIn(builtInName, arguments);
  } // invokeSWRLBuiltIn
  
} // SWRLJessBridgeAdapter
