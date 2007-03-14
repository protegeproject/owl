
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

/**
 ** Factory to create instances of rule engine bridges. 
 */
public class RuleEngineFactory
{
  private static HashMap<String, Creator> registeredRuleEngines;

  static {
    registeredRuleEngines = new HashMap<String, Creator>();
  } // static

  public static void registerRuleEngine(String ruleEngineName, Creator creator)
  {
    if (registeredRuleEngines.containsKey(ruleEngineName)) {
      registeredRuleEngines.remove(ruleEngineName);
      registeredRuleEngines.put(ruleEngineName, creator);
    } else registeredRuleEngines.put(ruleEngineName, creator);

    System.err.println("Rule engine '" + ruleEngineName + "' registered with the SWRLTab bridge.");
  } // registerRuleEngine

  public static boolean isRuleEngineRegistered(String ruleEngineName) { return registeredRuleEngines.containsKey(ruleEngineName); }
  public static Set<String> getRegisteredRuleEngineNames() { return registeredRuleEngines.keySet(); }

  /**
   ** Create an instance of a rule engine - a random registered engine is returned. If no engines are registered, a
   ** NoRegisteredRuleEnginesException is returned.
   */
  public static SWRLRuleEngineBridge createRuleEngine(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    if (!registeredRuleEngines.isEmpty()) return createRuleEngine(registeredRuleEngines.keySet().iterator().next(), owlModel);
    else throw new NoRegisteredRuleEnginesException();
  } // createRuleEngine

  /**
   ** Create an instance of a named rule engine. Throws an InvalidRuleEngineNameException if an engine of this name is not registered.
   */
  public static SWRLRuleEngineBridge createRuleEngine(String ruleEngineName, OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    SWRLRuleEngineBridge bridge = null;

    if (registeredRuleEngines.containsKey(ruleEngineName)) {

      try {
        bridge = registeredRuleEngines.get(ruleEngineName).create(owlModel);
      } catch (Throwable e) {
        throw new SWRLRuleEngineBridgeException("Error creating rule engine '" + ruleEngineName + "': " + e.getMessage());
      } // try

    } else throw new InvalidRuleEngineNameException(ruleEngineName);

    return bridge;
  } // createRuleEngine

  public static void unregisterRuleEngine(String ruleEngineName)
  {
    if (registeredRuleEngines.containsKey(ruleEngineName)) registeredRuleEngines.remove(ruleEngineName);
  } // unregisterRuleEngine

  public interface Creator
  {
    SWRLRuleEngineBridge create(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
  } // Creator

} // RuleEngineFactory
