
package edu.stanford.smi.protegex.owl.swrl.test;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

public class SQWRLTest
{
  private static JenaOWLModel owlModel;

  public static void main(String args[]) 
  {
    OWLModel owlModel;
    SWRLRuleEngineBridge ruleEngine;
    String owlFileName = "";

    if (args.length == 1) {
      owlFileName = args[0];
    } else Usage();
    
    try {
      owlModel = SWRLOWLUtil.createJenaOWLModel(owlFileName);
      ruleEngine = BridgeFactory.createBridge(owlModel);
      ruleEngine.infer();

      for (OWLPropertyAssertionAxiom axiom : ruleEngine.getInferredPropertyAssertionAxioms()) {
        if (axiom instanceof OWLDatatypePropertyAssertionAxiom) {
          OWLDatatypePropertyAssertionAxiom da = (OWLDatatypePropertyAssertionAxiom)axiom;
        }
      }

      SQWRLResult result = ruleEngine.getSQWRLResult("Rule-6");
      while (result.hasNext()) {
        ObjectValue p = result.getObjectValue("?p");
        ObjectValue c = result.getObjectValue("?c");
        System.err.println("value: p=" + p + ", c=" + c);
        result.next();
      } // while

    } catch (SQWRLException e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } catch (SWRLRuleEngineException e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } catch (SWRLOWLUtilException e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } // try
  } // main

  private static void Usage()
  {
    System.err.println("Usage: SQWRLTest <owlFileName>");
    System.exit(1);
  } // Usage

} // SQWRLTest
