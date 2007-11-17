
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.ResultImpl;

import java.util.*;

/**
 ** Interface representing a SWRL rule
 */
public interface SWRLRule
{
  String getRuleName();
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
} // SWRLRule
