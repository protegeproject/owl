
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.SWRLBuiltInUtil;

import org.jdom.*;

import java.util.*;

/**
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary 
{
  private static String SWRLXMLLibraryName = "SWRLXMLBuiltIns";

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLXMLLibraryName); 
    reset();
  }

  private XMLProcessor xmlProcessor;
  private XMLMapper xmlMapper;

  private Map<String, OWLIndividual> mappedSources;

  public void reset() 
  {
    xmlProcessor = new XMLProcessor();
    xmlMapper = new XMLMapper();
    mappedSources = new HashMap<String, OWLIndividual>();
  } // reset

  public boolean makeXMLDocument(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      String inputXMLStreamName = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
      OWLIndividual xmlDocumentMapping = null;
      Document doc;

      if (!mappedSources.containsKey(inputXMLStreamName)) {

        try {
          doc = xmlProcessor.processXMLStream(inputXMLStreamName);
          xmlDocumentMapping = xmlMapper.document2XMLDocumentMapping(doc, getInvokingBridge());
          mappedSources.put(inputXMLStreamName, xmlDocumentMapping);
        } catch (XMLProcessorException e) {
          throw new BuiltInException("error processing XML stream '" + inputXMLStreamName + "': " + e.getMessage());
        } catch (XMLMapperException e) {
          throw new BuiltInException("error mapping XML stream '" + inputXMLStreamName + "': " + e.getMessage());
        } // try
      } else xmlDocumentMapping = mappedSources.get(inputXMLStreamName);

      arguments.set(0, xmlDocumentMapping); // Bind the result to the first parameter

      result = true;
    } else {
      result = false;
    } // if
    return result;

  } // makeXMLDocument

} // SWRLBuiltInLibraryImpl
