
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import java.util.*;

/**
 ** Info object representing a SWRL rule.
 */
public class RuleInfo extends Info
{
  private String ruleName;
  private List<AtomInfo> bodyAtoms, headAtoms;
  private Set<String> referencedObjectVariableNames, referencedDatatypeVariableNames;
  
  public RuleInfo(String ruleName, List<AtomInfo> bodyAtoms, List<AtomInfo> headAtoms) throws SWRLRuleEngineBridgeException
  {
    this.ruleName = ruleName;
    this.bodyAtoms = bodyAtoms;
    this.headAtoms = headAtoms;
    buildReferencedVariableNames();
    processBodyAtoms();
  } // RuleInfo
  
  public String getRuleName() { return ruleName; }
  public List<AtomInfo> getHeadAtoms() { return headAtoms; }
  public List<AtomInfo> getBodyAtoms() { return bodyAtoms; }
  public boolean isObjectVariable(String variableName) { return referencedObjectVariableNames.contains(variableName); }
  public boolean isDatatypeVariable(String variableName) { return referencedDatatypeVariableNames.contains(variableName); }

  public List<BuiltInAtomInfo> getBuiltInAtomsFromHead(Set<String> builtInNames) throws SWRLRuleEngineBridgeException 
    { return getBuiltInAtoms(headAtoms, builtInNames); }
  public List<BuiltInAtomInfo> getBuiltInAtomsFromBody(Set<String> builtInNames) throws SWRLRuleEngineBridgeException 
    { return getBuiltInAtoms(bodyAtoms, builtInNames); }

  /**
   ** Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound by setting each non bound parameter
   ** to null. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid88T">here</a> for a discussion of the role of this
   ** method.
   */
  private void processBodyAtoms() throws SWRLRuleEngineBridgeException
  {
    List<BuiltInAtomInfo> bodyBuiltInAtoms = new ArrayList<BuiltInAtomInfo>();
    List<AtomInfo> bodyNonBuiltInAtoms = new ArrayList<AtomInfo>();
    Set<String> variableNamesUsedByNonBuiltInBodyAtoms = new HashSet<String>();
    Set<String> boundBuiltInVariableNames = new HashSet<String>(); // Names of variables bound by built-ins in this rule.
 
    // Process the body atoms and build up list of (1) built-in body atoms, and (2) the variables used by non-built body in atoms.
    for (AtomInfo atomInfo : getBodyAtoms()) {
      if (atomInfo instanceof BuiltInAtomInfo) bodyBuiltInAtoms.add((BuiltInAtomInfo)atomInfo);
      else {
        bodyNonBuiltInAtoms.add(atomInfo);
        variableNamesUsedByNonBuiltInBodyAtoms.addAll(atomInfo.getReferencedVariableNames()); // This may generate duplicates, but this is ok.
      } // if
    } // for

    // Process the body built-in atoms and determine if they bind any of their arguments.
    for (BuiltInAtomInfo builtInAtomInfo : bodyBuiltInAtoms) {
      // Read through all the argumends of the built-in and determine if they are unbound.
      for (int argumentNumber = 0; argumentNumber < builtInAtomInfo.getNumberOfArguments(); argumentNumber++) {
        if (builtInAtomInfo.isArgumentAVariable(argumentNumber)) { 
          String argumentVariableName = builtInAtomInfo.getArgumentVariableName(argumentNumber);
	      
          // If a variable argument is not used by any non built-in body atom or is not bound by another body built-in atom it will be
          // unbound when this built-in is called. We thus inform this built-in that it must set this argument. If a built-in binds an
          // argument, all later built-ins (proceeding from left to right) will be passed the bound value of this variable.
          if (!variableNamesUsedByNonBuiltInBodyAtoms.contains(argumentVariableName) &&
              !boundBuiltInVariableNames.contains(argumentVariableName)) {
            builtInAtomInfo.addUnboundArgumentNumber(argumentNumber); // Tell the built-in that it is expected to bind this argument.
            boundBuiltInVariableNames.add(argumentVariableName);
          } // if
        } // if
      } // for
    } // for

    // If we have built-in atoms, construct a new head with built-in atoms moved to the end of the list. Some rule engines (e.g., Jess)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    bodyAtoms = processBodyNonBuiltInAtoms(bodyNonBuiltInAtoms);
    bodyAtoms.addAll(bodyBuiltInAtoms);
  } // processBodyAtoms

  private List<AtomInfo> processBodyNonBuiltInAtoms(List<AtomInfo> bodyNonBuiltInAtoms)
  {
    List<AtomInfo> bodyClassAtoms = new ArrayList<AtomInfo>(); 
    List<AtomInfo> bodyNonClassNonBuiltInAtoms = new ArrayList<AtomInfo>();
    List<AtomInfo> result = new ArrayList<AtomInfo>();

    for (AtomInfo atomInfo : bodyNonBuiltInAtoms) {
      if (atomInfo instanceof ClassAtomInfo) bodyClassAtoms.add(atomInfo);
      else bodyNonClassNonBuiltInAtoms.add(atomInfo);
    } // for
    
    result.addAll(bodyNonClassNonBuiltInAtoms);
    result.addAll(bodyClassAtoms);

    return result;
  } // processBodyNonBuiltInAtoms

  private List<BuiltInAtomInfo> getBuiltInAtoms(List<AtomInfo> atoms, Set<String> builtInNames) throws SWRLRuleEngineBridgeException
  {
    List<BuiltInAtomInfo> result = new ArrayList<BuiltInAtomInfo>();
    
    for (AtomInfo atomInfo : atoms) {
      if (atomInfo instanceof BuiltInAtomInfo) {
        BuiltInAtomInfo builtInAtomInfo = (BuiltInAtomInfo)atomInfo;
        if (builtInNames.contains(builtInAtomInfo.getBuiltInName())) result.add(builtInAtomInfo);
        } // if
    } // for
    return result;
  } // getBuiltInAtoms

  private void buildReferencedVariableNames()
  {
    referencedObjectVariableNames = new HashSet<String>();
    referencedDatatypeVariableNames = new HashSet<String>();
    
    for (AtomInfo atomInfo : getBodyAtoms()) {
      referencedDatatypeVariableNames.addAll(atomInfo.getReferencedDatatypeVariableNames());
      referencedObjectVariableNames.addAll(atomInfo.getReferencedObjectVariableNames());
    } // for
  } // buildReferencedVariableNames
  
} // RuleInfo
