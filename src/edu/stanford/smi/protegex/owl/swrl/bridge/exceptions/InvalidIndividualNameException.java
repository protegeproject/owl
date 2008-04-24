
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InvalidIndividualNameException extends SWRLRuleEngineBridgeException 
{
  public InvalidIndividualNameException(String name) 
  { 
    super("Invalid individual name: " + name); 
  } // InvalidIndividualNameException
} // InvalidIndividualNameException
