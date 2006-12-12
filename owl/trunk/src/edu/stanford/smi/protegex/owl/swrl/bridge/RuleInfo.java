
// Info object representing a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import java.util.*;

public class RuleInfo extends Info
{
  private String ruleName;
  private List<AtomInfo> body, head; // Lists of AtomInfo objects.
  private Set<String> referencedObjectVariableNames, referencedDatatypeVariableNames;
  
  public RuleInfo(String ruleName, List<AtomInfo> bodyAtoms, List<AtomInfo> headAtoms) throws SWRLRuleEngineBridgeException
  {
    this.ruleName = ruleName;
    body = bodyAtoms;
    head = headAtoms;
    buildReferencedVariableNames();
    processBindingBuiltInAtoms();
  } // RuleInfo
  
  public String getRuleName() { return ruleName; }
  public List<AtomInfo> getHeadAtoms() { return head; }
  public List<AtomInfo> getBodyAtoms() { return body; }
  public boolean isObjectVariable(String variableName) { return referencedObjectVariableNames.contains(variableName); }
  public boolean isDatatypeVariable(String variableName) { return referencedDatatypeVariableNames.contains(variableName); }

  public List<BuiltInAtomInfo> getBuiltInAtomsFromHead(Set<String> builtInNames) throws SWRLRuleEngineBridgeException 
    { return getBuiltInAtoms(head, builtInNames); }
  public List<BuiltInAtomInfo> getBuiltInAtomsFromBody(Set<String> builtInNames) throws SWRLRuleEngineBridgeException 
    { return getBuiltInAtoms(body, builtInNames); }

  // Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound.
  //
  // If a variable passed as a parameter to a body built-in is unbound when the built-in is called then we assume that the built-in is going
  // to assign a value to that variable. (Head built-in atoms will never bind their variable argument because head variables will always be
  // bound because of SWRL's safety requirement.) We can determine that a variable passed to a built-in is unbound if it is not referenced
  // in any non built-in atom in the rule body or is not bound by another built-in. For example, in the rule:
  //
  // Person(?p) ^ hasSalaryInPounds(?p, ?pounds) ^ swrlb:multiply(?dollars, ?pounds, ?1.6) -> hasSalaryInDollars(?p, ?dollars)
  // 
  // the ?dollars variable in the first argument position is unbound in the swrlb:multiply built-in. 
  //
  // The following rule shows an example of a built-in binding a variable that is later used as a bound value by another built-in: 
  //
  // Rectangle(?r) ^ hasWidth(?r, ?w) ^ hasHeight(?r, ?h) ^ swrlb:multiply(?area, ?w, ?h) ^ swrlb:greaterThan(?area, 10) ->
  // LargeRectangle(?r)
  // 
  // greaterThan and the like must never bind??!! Can't find out before hand so exception will only be throw at run time. User should know
  // and position accordingly left to right.
  //
  // Unbound variabled can occur in any position. For example, the same rule can be expressed in the following ways:
  //
  // Person(?p) ^ hasSalaryInPounds(?p, ?pounds) ^ swrlb:divide(?pounds, ?dollars, ?1.6) -> hasSalaryInDollars(?p, ?dollars)
  // Person(?p) ^ hasSalaryInPounds(?p, ?pounds) ^ swrlb:multiply(1.6, ?pounds, ?dollars) -> hasSalaryInDollars(?p, ?dollars)
  //
  // In these cases, the unbound ?dollars variable is in the second and third argument positions, respectively.
  //
  private void processBindingBuiltInAtoms() throws SWRLRuleEngineBridgeException
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

    // If we have built-in atoms, construct a new head with built-in atoms moved them to the end of the list. Some rule engines (e.g., JESS)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    if (!bodyBuiltInAtoms.isEmpty()) body = bodyNonBuiltInAtoms; body.addAll(bodyBuiltInAtoms);
  } // processBindingBuiltInAtoms

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
