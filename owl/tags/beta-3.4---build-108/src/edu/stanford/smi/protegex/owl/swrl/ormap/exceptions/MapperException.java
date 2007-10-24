  
package edu.stanford.smi.protegex.owl.swrl.ormap.exceptions;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

public class MapperException extends SWRLRuleEngineBridgeException
{
  public MapperException(String message) { super(message); }

  public MapperException(String message, Throwable cause) { super(message, cause); }
} // MapperException

