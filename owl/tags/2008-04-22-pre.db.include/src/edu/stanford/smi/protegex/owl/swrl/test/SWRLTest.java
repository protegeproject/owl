
package edu.stanford.smi.protegex.owl.swrl.test;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

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

    /*
    if (args.length == 2) {
      owlFileName = args[0];
      xmlFileName = args[1];
    } else Usage();
    */
    
    String uriA = "http://dionisbg.di.funpic.de/familyA.owl";
    OWLModel modelA=null;
    SWRLFactory factoryA=null;
    try {
      modelA=ProtegeOWL.createJenaOWLModelFromURI(uriA);
      System.out.println("ModelA loaded ...");
    }
    catch (java.lang.Exception e) {
      e.printStackTrace();
    }
    factoryA = new SWRLFactory(modelA);
    System.out.println("SWRL FactoryA created ...");
    
      
    // -- OntologyB --
    String uriB = "http://dionisbg.di.funpic.de/familyB.owl";
    OWLModel modelB=null;
    SWRLFactory factoryB=null;
    try {
      modelB=ProtegeOWL.createJenaOWLModelFromURI(uriB);
      System.out.println("ModelB loaded ...");
    }
    catch (java.lang.Exception e) {
      e.printStackTrace();
    }
    factoryB = new SWRLFactory(modelB);
    System.out.println("SWRL FactoryB created ...");
    
    
    // -- Coping a rule between two models --
    try {
      factoryB.copyImps(modelA); // Problem: throws  java.lang.ClassCastException !!!
      
      // or
      
      SWRLImp grandpaRule_A = factoryA.getImp("family:GrandfatherRule");
      SWRLAtomList bodyA= grandpaRule_A.getBody(); // Problem: throws  also java.lang.ClassCastException
      SWRLAtomList headA= grandpaRule_A.getHead(); // Problem: throws  also java.lang.ClassCastException
      
      factoryB.createImp(headA, bodyA);
      
      
      //factoryB.createImpWithGivenName("family:GrandfatherRule");
      //SWRLImp grandpaRule_B = factoryB.getImp("family:GrandfatherRule");
      //grandpaRule_B.setBody(bodyA);
      //grandpaRule_B.setHead(headA);
      
    }
    catch (java.lang.Exception e) {
      e.printStackTrace();
    }
  } // main

  private static void Usage()
  {
    System.err.println("Usage: SWRLTest <owlFileName> <xmlFileName>");
    System.exit(1);
  } // Usage

} // SWRLTest
