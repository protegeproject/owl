
// Info object representing a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import java.util.*;

public class RuleInfo extends Info
{
  private List body, head; // Lists of AtomInfo objects.
  
  public RuleInfo(String ruleName, List bodyAtoms, List headAtoms) throws SWRLRuleEngineBridgeException
  {
    super(ruleName);
    body = bodyAtoms;
    head = headAtoms;
    processBindingBuiltInAtoms();
  } // RuleInfo
  
  public List getHeadAtoms() { return head; }
  public List getBodyAtoms() { return body; }

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
    List bodyBuiltInAtoms = new ArrayList(), bodyNonBuiltInAtoms = new ArrayList();
    List variableNamesUsedByNonBuiltInBodyAtoms = new ArrayList();
    List boundBuiltInVariableNames = new ArrayList(); // Names of variables bound by built-ins in this rule.
    Iterator iterator;
 
    // Process the body atoms and build up list of (1) built-in body atoms, and (2) the variables used by non-built body in atoms.
    iterator = getBodyAtoms().iterator();
    while (iterator.hasNext()) {
      AtomInfo atomInfo = (AtomInfo)iterator.next();
      if (atomInfo instanceof BuiltInAtomInfo) bodyBuiltInAtoms.add(atomInfo);
      else {
        bodyNonBuiltInAtoms.add(atomInfo);
        variableNamesUsedByNonBuiltInBodyAtoms.addAll(atomInfo.getReferencedVariableNames()); // This may generate duplicates, but this is ok.
      } // if
    } // while

    // Process the body built-in atoms and determine if they bind any of their arguments.
    iterator = bodyBuiltInAtoms.iterator();
    while (iterator.hasNext()) {
      BuiltInAtomInfo builtInAtomInfo = (BuiltInAtomInfo)iterator.next();
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
    } // while

    // If we have built-in atoms, construct a new head with built-in atoms moved them to the end of the list. Some rule engines (e.g., JESS)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    if (!bodyBuiltInAtoms.isEmpty()) {
      body = bodyNonBuiltInAtoms; body.addAll(bodyBuiltInAtoms);
    } // if
  } // processBindingBuiltInAtoms
  
} // RuleInfo
