
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.reflect.*;

/** 
 ** A class that must be subclassed by a class implementing a library of SWRL built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation.
 */
public abstract class AbstractSWRLBuiltInLibrary implements SWRLBuiltInLibrary
{
  private String libraryName;

  // Bridge, rule name, built-in index, and head or body location within rule of built-in currently invoking its associated Java
  // implementation. The invokingRuleName, invokingBuiltInIndex, and isInConsequent variables are valid only when a built-in currently being
  // invoked so should only be retrieved through their asociated accessor methods from within a built-in; the invokingBridge method is valid
  // only in built-ins and in the reset method.
  private SWRLBuiltInBridge invokingBridge;
  private String invokingRuleName = "";
  private int invokingBuiltInIndex = -1;
  private boolean isInConsequent = false;

  public AbstractSWRLBuiltInLibrary(String libraryName) { this.libraryName = libraryName; }

  public String getLibraryName() { return libraryName; }

  public SWRLBuiltInBridge getInvokingBridge() throws BuiltInException
  {
    if (invokingBridge == null) 
      throw new BuiltInException("invalid call to getInvokingBridge - should only be called from within a built-in");

    return invokingBridge;
  } // getInvokingBridge

  public String getInvokingRuleName() throws BuiltInException
  {
    if (invokingRuleName.equals("")) 
      throw new BuiltInException("invalid call to getInvokingRuleName - should only be called from within a built-in");

    return invokingRuleName;
  } // getInvokingRuleName

  public int getInvokingBuiltInIndex() throws BuiltInException
  {
    if (invokingBuiltInIndex == -1) 
      throw new BuiltInException("invalid call to getInvokingBuiltInIndex - should only be called from within a built-in");

    return invokingBuiltInIndex;
  } // getInvokingBuiltInIndex

  public boolean getIsInConsequent() throws BuiltInException
  {
    if (invokingBridge == null) 
      throw new BuiltInException("invalid call to getIsInConsequent - should only be called from within a built-in");

    return isInConsequent;
  } // getIsInConsequent

  public abstract void reset() throws BuiltInException;

  public void invokeResetMethod(SWRLBuiltInBridge bridge) throws BuiltInException
  {
    synchronized (this) {
      invokingBridge = bridge;

      reset();

      invokingBridge = null;
    } // synchronized
  } // invokeResetMethod

  public boolean invokeBuiltInMethod(Method method, SWRLBuiltInBridge bridge, String ruleName, 
                                     String prefix, String builtInMethodName, int builtInIndex, boolean isInConsequent,
                                     List<BuiltInArgument> arguments) 
    throws BuiltInException
  {
    Boolean result = null;
    String builtInName = prefix + ":" + builtInMethodName;

    synchronized(this) { // Only one built-in per library may be invoked simultaneously.
      invokingBridge = bridge; invokingRuleName = ruleName; invokingBuiltInIndex = builtInIndex; this.isInConsequent = isInConsequent;
      
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
                                          ruleName + "': " + e.toString(), e);
      } catch (Exception e) { // Should be one of IllegalAccessException or IllegalArgumentException
        throw new BuiltInException("internal bridge exception when invoking built-in method '" + builtInName + "' in rule '" + 
                                   ruleName + "': " + e.getMessage(), e);        
      } // try
      
      invokingBridge = null; invokingRuleName = ""; invokingBuiltInIndex = -1; this.isInConsequent = false;
    } // synchronized

    return result.booleanValue();
  } // invokeBuiltInMethod

} // AbstractSWRLBuiltInLibrary
