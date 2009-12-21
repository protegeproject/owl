
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.rdfb;

import java.util.List;

import edu.stanford.smi.protegex.owl.swrl.bridge.ArgumentFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInNotImplementedException;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

/**
 ** Implementations library for RDFB built-in methods. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?RDFBuiltIns">here</a> for
 ** documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLRDFLibraryName = "SWRLRDFBuiltIns";
  private ArgumentFactory argumentFactory;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLRDFLibraryName); 
    
    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset() 
  {
  } // reset

  /**
   ** Returns true if the RDF resource named by the first argument has any label identified by the second
   ** argument. If the second argument is unbound, bind it to labels of the resource.
   */
  public boolean hasLabel(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean isUnboundArgument = isUnboundArgument(1, arguments);   
    String resourceName;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());
    checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

    resourceName = getArgumentAsAResourceName(0, arguments);

    if (isUnboundArgument) {
    	MultiArgument multiArgument = argumentFactory.createMultiArgument(getVariableName(1, arguments), getPrefixedVariableName(1, arguments));
    	for (String label : SWRLOWLUtil.getRDFSLabels(getInvokingBridge().getOWLModel(), resourceName))
    		multiArgument.addArgument(argumentFactory.createDatatypeValueArgument(label));
    	arguments.set(1, multiArgument);
    	result = !multiArgument.hasNoArguments();
    } else { // Bound argument
    	String label = getArgumentAsAString(1, arguments);
    	result = SWRLOWLUtil.getRDFSLabels(getInvokingBridge().getOWLModel(), resourceName).contains(label);
    } // if
    
    return result;
  } // hasLabel

  /**
   ** Returns true if the RDF resource named by the first argument has any label language identified by the second
   ** argument. If the second argument is unbound, bind it to label languages of the resource.
   */
  public boolean hasLabelLanguage(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean isUnboundArgument = isUnboundArgument(1, arguments);   
    String resourceName;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());
    checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

    resourceName = getArgumentAsAResourceName(0, arguments);

    if (isUnboundArgument) {
     	MultiArgument multiArgument = argumentFactory.createMultiArgument(getVariableName(1, arguments), getPrefixedVariableName(1, arguments));
    	for (String language : SWRLOWLUtil.getRDFSLabelLanguages(getInvokingBridge().getOWLModel(), resourceName))
    		multiArgument.addArgument(argumentFactory.createDatatypeValueArgument(language));
    	arguments.set(1, multiArgument);
    	result = !multiArgument.hasNoArguments();
    } else { // Bound argument
    	String language = getArgumentAsAString(1, arguments);
    	result = SWRLOWLUtil.getRDFSLabelLanguages(getInvokingBridge().getOWLModel(), resourceName).contains(language);
    } // if
    
    return result;
  } // hasLabelLanguage

  /**
   ** isClass(c)
   */
  public boolean isClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isClass

  /**
   ** isList(l)
   */
  public boolean isList(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isList

  /**
   ** isProperty(p)
   */
  public boolean isProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isProperty

  /**
   ** isResource(r)
   */
  public boolean isResource(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isResource

} // SWRLBuiltInLibraryImpl
