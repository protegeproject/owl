
package edu.stanford.smi.protegex.owl.swrl.test;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

public class SWRLTest
{
  private static JenaOWLModel owlModel;

  public static void main(String args[]) 
  {
    OWLModel owlModel;
    SWRLRuleEngine ruleEngine;
    String owlFileName = "";

    if (args.length == 1) {
      owlFileName = args[0];
    } else Usage();
    
    try {
      owlModel = SWRLOWLUtil.createJenaOWLModel(owlFileName);
      ruleEngine = SWRLRuleEngineFactory.create(owlModel);
      ruleEngine.infer();

      System.err.println("number of inferred individuals: " +  ruleEngine.getNumberOfInferredIndividuals());
      System.err.println("number of inferred property assertion axioms: " + ruleEngine.getNumberOfInferredPropertyAssertionAxioms());
    } catch (SWRLOWLUtilException e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } catch (SWRLRuleEngineException e) {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace();
    } // try
  } // main

  private static void Usage()
  {
    System.err.println("Usage: Test <owlFileName>");
    System.exit(1);
  } // Usage

} // SWRLTest
