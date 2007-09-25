
package edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions;

public class InvalidRuleNameException extends ResultException 
{
  public InvalidRuleNameException(String ruleName) 
  { 
    super("Invalid rule name '" + ruleName + "'"); 
  } // InvalidRuleNameException
} // InvalidRuleNameException
