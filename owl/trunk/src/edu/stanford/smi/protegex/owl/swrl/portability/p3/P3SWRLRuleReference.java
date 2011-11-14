
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;

public class P3SWRLRuleReference implements SWRLRuleReference
{
  private String ruleURI;
  private List<SWRLAtomReference> bodyAtoms, headAtoms;
  
  public P3SWRLRuleReference(String ruleURI, List<SWRLAtomReference> bodyAtoms, List<SWRLAtomReference> headAtoms)
  {
    this.ruleURI = ruleURI;
    this.bodyAtoms = bodyAtoms;
    this.headAtoms = headAtoms;
  } 

  public String getURI() { return ruleURI; }

  public List<SWRLAtomReference> getHeadAtoms() { return headAtoms; }
  public List<SWRLAtomReference> getBodyAtoms() { return bodyAtoms; }

  public void setURI(String ruleURI) { this.ruleURI = ruleURI; }
  public void setRuleText(String text) {}
  public String getRuleGroupName() { return ""; }
    
  public void appendAtomsToBody(List<SWRLAtomReference> atoms) { bodyAtoms.addAll(atoms); }
  public void setBodyAtoms(List<SWRLAtomReference> atoms) { bodyAtoms = atoms; }
  public String toString() { return ruleURI; }
  public boolean isEnabled() { return true; } // TODO - used only in SWRLRuleGroupTreeTableModel 
  public void setEnabled(boolean isEnabled) {} // TODO - used only in SWRLRuleGroupTreeTableModel
  
  public String getRuleText()
  {
    String result = "";
    boolean isFirst = true;

    for (SWRLAtomReference atom : getBodyAtoms()) {
      if (!isFirst) result += " ^ ";
      result += "" + atom;
      isFirst = false;
    } // for
    
    result += " -> ";

    isFirst = true;
    for (SWRLAtomReference atom : getHeadAtoms()) {
      if (!isFirst) result += " ^ ";
      result += "" + atom;
      isFirst = false;
    } // for

    return result;
  }
} 
