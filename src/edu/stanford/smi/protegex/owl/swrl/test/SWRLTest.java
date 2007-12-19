
package edu.stanford.smi.protegex.owl.swrl.test;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml.*;

import org.jdom.*;

public class SWRLTest
{
  private static JenaOWLModel owlModel;

  public static void main(String args[]) 
  {
    XMLMapper xmlMapper = new XMLMapper();
    XMLProcessor xmlProcessor = new XMLProcessor();
    Document doc;
    OWLModel owlModel;
    SWRLRuleEngineBridge bridge;
    String owlFileName = "", xmlFileName = "";

    if (args.length == 2) {
      owlFileName = args[0];
      xmlFileName = args[1];
    } else Usage();
    
    try {
      owlModel = SWRLOWLUtil.createJenaOWLModel(owlFileName);
      bridge = BridgeFactory.createBridge(owlModel);
      bridge.infer();

      doc = xmlMapper.xmlDocumentMapping2Document(bridge);
      xmlProcessor.generateXMLFile(doc, xmlFileName);

    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } // try
  } // main

  private static void Usage()
  {
    System.err.println("Usage: SWRLTest <owlFileName> <xmlFileName>");
    System.exit(1);
  } // Usage

} // SWRLTest
