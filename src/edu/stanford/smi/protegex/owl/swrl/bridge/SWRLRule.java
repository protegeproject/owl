
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.ResultImpl;

/**
 ** Interface representing a SWRL rule
 */
public interface SWRLRule extends OWLAxiom
{
  String getRuleName();
  void setRuleName(String ruleName);
  List<Atom> getHeadAtoms();
  List<Atom> getBodyAtoms();
  
  List<BuiltInAtom> getBuiltInAtomsFromHead();
  List<BuiltInAtom> getBuiltInAtomsFromHead(Set<String> builtInNames);
  
  List<BuiltInAtom> getBuiltInAtomsFromBody();
  List<BuiltInAtom> getBuiltInAtomsFromBody(Set<String> builtInNames);

  void appendAtomsToBody(List<Atom> atom);

  boolean isSQWRL();
  boolean usesSQWRLCollections();
  ResultImpl getSQWRLResult();

  List<Atom> getSQWRLPhase1BodyAtoms();
  List<Atom> getSQWRLPhase2BodyAtoms();
  
  String getRuleText();
  public void setRuleText(String text);
  Set<String> getRuleGroupNames();
  void addRuleGroupNames(String ruleGroupName);
  boolean isEnabled();
  void setEnabled(Boolean enable);
  
} // SWRLRule
