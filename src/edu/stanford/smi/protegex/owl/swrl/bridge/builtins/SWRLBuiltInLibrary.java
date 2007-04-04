
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.reflect.*;

/** 
 ** A class that must be subclasses by a class defining SWRL built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation.
 */
public abstract class SWRLBuiltInLibrary 
{
  private String libraryName;

  // Bridge, rule and index within rule of built-in currently invoking its associated Java implementation. The invokingRuleName and
  // invokingBuiltInIndex variables are valid only when a built-in currently being invoked so should only be retrieved through their
  // asociated accessor methods from within a built-in; the invokingBridge method is valid only in built-ins and in the reset method.
  private SWRLRuleEngineBridge invokingBridge;
  private String invokingRuleName = "";
  private int invokingBuiltInIndex = -1;

  public SWRLBuiltInLibrary(String libraryName) { this.libraryName = libraryName; }

  public String getLibraryName() { return libraryName; }

  protected SWRLRuleEngineBridge getInvokingBridge() throws BuiltInException
  {
    if (invokingBridge == null) 
      throw new BuiltInException("invalid call to getInvokingBridge - should only be called from within a built-in");

    return invokingBridge;
  } // getInvokingBridge

  protected String getInvokingRuleName() throws BuiltInException
  {
    if (invokingRuleName.equals("")) 
      throw new BuiltInException("invalid call to getInvokingRuleName - should only be called from within a built-in");

    return invokingRuleName;
  } // getInvokingRuleName

  protected int getInvokingBuiltInIndex() throws BuiltInException
  {
    if (invokingBuiltInIndex == -1) 
      throw new BuiltInException("invalid call to getInvokingBuiltInIndex - should only be called from within a built-in");

    return invokingBuiltInIndex;
  } // getInvokingBuiltInIndex

  public abstract void reset() throws BuiltInException;

  public void invokeResetMethod(SWRLRuleEngineBridge bridge) throws BuiltInException
  {
    synchronized (this) {
      invokingBridge = bridge;

      reset();

      invokingBridge = null;
    } // synchronized
  } // invokeResetMethod

  public boolean invokeBuiltInMethod(Method method, SWRLRuleEngineBridge bridge, 
                                                  String ruleName, String builtInName, int builtInIndex, List<Argument> arguments) 
    throws BuiltInException
  {
    Boolean result = null;

    synchronized(this) {
      invokingBridge = bridge; invokingRuleName = ruleName; invokingBuiltInIndex = builtInIndex; 
      
      try { // Invoke the built-in method.
        result = (Boolean)method.invoke(this, new Object[] { arguments });
      } catch (InvocationTargetException e) { // The built-in implementation threw an exception.
        Throwable targetException = e.getTargetException();
        if (targetException instanceof BuiltInException) { // A BuiltInException was thrown by the built-in.
          throw new BuiltInException("exception thrown by built-in '" + builtInName + "' in rule '" + ruleName + "': " 
                                     + targetException.getMessage(), targetException);
        } else if (targetException instanceof RuntimeException) { // A runtime exception was thrown by the built-in.
          throw new BuiltInMethodRuntimeException(ruleName, builtInName, targetException.getMessage(), targetException);
        } else throw new BuiltInException("unknown exception thrown by built-in method '" + builtInName + "' in rule '" + 
                                          ruleName + "'. Exception: " + e.toString(), e);
      } catch (Exception e) { // Should be one of IllegalAccessException or IllegalArgumentException
        throw new BuiltInException("internal bridge exception when invoking built-in method '" + builtInName + "' in rule '" + 
                                   ruleName + "': " + e.getMessage(), e);        
      } // try
      
      invokingBridge = null; invokingRuleName = ""; invokingBuiltInIndex = -1;
    } // synchronized

    return result.booleanValue();
  } // invokeBuiltInMethod

} // SWRLBuiltInLibrary
