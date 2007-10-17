
package edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions;

public class InvalidRuleNameException extends ResultException 
{
  public InvalidRuleNameException(String ruleName) 
  { 
    super("invalid rule name '" + ruleName + "'"); 
  } // InvalidRuleNameException
} // InvalidRuleNameException
