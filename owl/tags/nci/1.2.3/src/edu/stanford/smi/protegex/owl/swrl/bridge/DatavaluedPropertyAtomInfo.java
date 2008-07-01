
// Info object representing a SWRL data valued property atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.model.*;

public class DatavaluedPropertyAtomInfo extends AtomInfo
{
  private Argument argument1, argument2;
  
  public DatavaluedPropertyAtomInfo(SWRLDatavaluedPropertyAtom atom) 
    throws SWRLRuleEngineBridgeException
  {
    super(atom.getPropertyPredicate().getName());

    String argumentName;
    RDFSDatatype datatype;

    if (atom.getArgument1() instanceof SWRLVariable) argument1 = new VariableInfo((SWRLVariable)atom.getArgument1());
    else if (atom.getArgument1() instanceof OWLIndividual) argument1 = new IndividualInfo((OWLIndividual)atom.getArgument1());
    else throw new SWRLRuleEngineBridgeException("Unknown argument #1 to datavalued property atom " + atom.getName() + ". Expecting variable or individual.");

    if (atom.getArgument2() instanceof SWRLVariable) argument2 = new VariableInfo((SWRLVariable)atom.getArgument2());
    else if (atom.getArgument2() instanceof RDFSLiteral) argument2 = new LiteralInfo((RDFSLiteral)atom.getArgument2());
    else throw new SWRLRuleEngineBridgeException("Unknown argument #2 to datavalued property atom " + atom.getName()  + ". Expecting variable or literal.");

    // If argument1 is an individual, add its name to the referenced individuals list for this atom.
    if (argument1 instanceof IndividualInfo) addReferencedIndividualName(argument1.getName());
  } // DatavaluedPropertyAtomInfo
  
  public Argument getArgument1() { return argument1; }
  public Argument getArgument2() { return argument2; }
} // DatavaluedPropertyAtomInfo
