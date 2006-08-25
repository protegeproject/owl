
// Info object representing a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class RuleInfo extends Info
{
  private List body, head; // Lists of AtomInfo objects.
  
  public RuleInfo(String ruleName)
  {
    super(ruleName);
    body = new ArrayList();
    head = new ArrayList();
  } // RuleInfo
  
  public void addBodyAtom(AtomInfo atomInfo) { body.add(atomInfo); } 
  public void addHeadAtom(AtomInfo atomInfo) { head.add(atomInfo); } 
  public void setHeadAtoms(List head) { this.head = head; }
  public void setBodyAtoms(List body) { this.body = body; }
  public List getHeadAtoms() { return head; }
  public List getBodyAtoms() { return body; }
} // RuleInfo
