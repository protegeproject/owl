
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

/**
 ** Factory to create instances of rule engine bridges. 
 */
public class BridgeFactory
{
  private static HashMap<String, BridgeCreator> registeredBridges;

  static {
    registeredBridges = new HashMap<String, BridgeCreator>();
  } // static

  static {

    try { // TODO:  Hack until we can do a proper class load with the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridge");
    } catch (ClassNotFoundException e) {
      System.err.println("SWRLJessBridge load failed");
    } // try
  } // static

  public static void registerBridge(String bridgeName, BridgeCreator bridgeCreator)
  {
    if (registeredBridges.containsKey(bridgeName)) {
      registeredBridges.remove(bridgeName);
      registeredBridges.put(bridgeName, bridgeCreator);
    } else registeredBridges.put(bridgeName, bridgeCreator);

    System.out.println("Rule engine '" + bridgeName + "' registered with the SWRLTab bridge.");
  } // registerBridge

  public static boolean isBridgeRegistered(String bridgeName) { return registeredBridges.containsKey(bridgeName); }
  public static Set<String> getRegisteredBridgeNames() { return registeredBridges.keySet(); }

  /**
   ** Create an instance of a rule engine - a random registered engine is returned. If no engines are registered, a
   ** NoRegisteredBridgesException is returned.
   */
  public static SWRLRuleEngineBridge createBridge(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    if (!registeredBridges.isEmpty()) return createBridge(registeredBridges.keySet().iterator().next(), owlModel);
    else throw new NoRegisteredBridgesException();
  } // createBridge

  /**
   ** Create an instance of a named rule engine. Throws an InvalidBridgeNameException if an engine of this name is not registered.
   */
  public static SWRLRuleEngineBridge createBridge(String bridgeName, OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    SWRLRuleEngineBridge bridge = null;

    if (registeredBridges.containsKey(bridgeName)) {

      try {
        bridge = registeredBridges.get(bridgeName).create(owlModel);
      } catch (Throwable e) {
        throw new SWRLRuleEngineBridgeException("Error creating rule engine '" + bridgeName + "': " + e.getMessage());
      } // try

    } else throw new InvalidBridgeNameException(bridgeName);

    return bridge;
  } // createBridge

  public static void unregisterBridge(String bridgeName)
  {
    if (registeredBridges.containsKey(bridgeName)) registeredBridges.remove(bridgeName);
  } // unregisterBridge

  public interface BridgeCreator
  {
    SWRLRuleEngineBridge create(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
  } // BridgeCreator

} // BridgeFactory
