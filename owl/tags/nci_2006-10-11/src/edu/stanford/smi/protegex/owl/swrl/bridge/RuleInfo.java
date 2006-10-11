
// Info object representing a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class RuleInfo extends Info
{
  private List body, head; // List of AtomInfo objects.
  
  public RuleInfo(String ruleName)
  {
    super(ruleName);
    body = new ArrayList();
    head = new ArrayList();
  } // RuleInfo
  
  public void addBodyAtom(AtomInfo atomInfo) { body.add(atomInfo); } 
  public void addHeadAtom(AtomInfo atomInfo) { head.add(atomInfo); } 
  public List getHead() { return head; }
  public List getBody() { return body; }
} // RuleInfo
