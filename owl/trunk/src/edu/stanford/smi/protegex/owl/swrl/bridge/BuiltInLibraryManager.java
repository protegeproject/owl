
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;

/**
 ** Class to manage built-in libraries.
 */
public abstract class BuiltInLibraryManager
{
  private static String BuiltInLibraryPackageBaseName = "edu.stanford.smi.protegex.owl.swrl.bridge.builtins.";
  private static String BuiltInLibraryInitializeMethodName = "initialize";

  // Holds class instances implementing built-ins.
  private static HashMap<String, SWRLBuiltInLibrary> builtInLibraryClassInstances;

  static {
    builtInLibraryClassInstances = new HashMap<String, SWRLBuiltInLibrary>();
  } // static

  /**
   ** Find the implementation classes for a built-in library. Returns null if it does not find anything. A library will only be valid after
   ** it is loaded.
   */
  public static SWRLBuiltInLibrary getBuiltInLibrary(String libraryNamespace) throws InvalidBuiltInLibraryNameException
  {
    SWRLBuiltInLibrary swrlBuiltInLibrary = null;

    if (builtInLibraryClassInstances.containsKey(libraryNamespace)) 
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)builtInLibraryClassInstances.get(libraryNamespace);
    else throw new InvalidBuiltInLibraryNameException(libraryNamespace);

    return swrlBuiltInLibrary;
  } // getBuiltInLibrary

  /**
   ** Invoke a built-in. This method should not be called from a rule engine - instead the invokeSWRLBuiltIn method in the bridge
   ** should be used.
   */
  public static boolean invokeSWRLBuiltIn(SWRLRuleEngineBridge bridge, String ruleName, String builtInName, int builtInIndex, 
                                          List<Argument> arguments) 
    throws BuiltInException
  {
    SWRLBuiltInLibrary swrlBuiltInLibrary = null;
    Class swrlBuiltInLibraryClass = null;
    String namespaceName = "", builtInMethodName = "", className;
    Boolean result = false;
    Method method;
    int colonIndex;

    colonIndex = builtInName.indexOf(':');
    if (colonIndex != -1) {
      namespaceName = builtInName.substring(0, colonIndex);
      builtInMethodName = builtInName.substring(colonIndex + 1, builtInName.length());
      className = BuiltInLibraryPackageBaseName + namespaceName + ".SWRLBuiltInLibraryImpl";
    } else { // No namespace - try the base built-ins package. Ordinarily, built-ins should not be located here.
      namespaceName = "";
      builtInMethodName = builtInName;
      className = BuiltInLibraryPackageBaseName + "SWRLBuiltInLibraryImpl";
    } // if
    
    if (builtInLibraryClassInstances.containsKey(namespaceName)) { // Find the implementation
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)builtInLibraryClassInstances.get(namespaceName);
    } else { // Implementation class not loaded - load it, call the initialize method, and cache it.
      swrlBuiltInLibrary = loadBuiltInLibraryImpl(ruleName, namespaceName, className);
      builtInLibraryClassInstances.put(namespaceName, swrlBuiltInLibrary);
      invokeBuiltInLibraryInitializeMethod(bridge, swrlBuiltInLibrary);
    } // if

    method = resolveBuiltInMethod(ruleName, namespaceName, builtInMethodName, swrlBuiltInLibrary); // Find the method.
    checkBuiltInMethodSignature(ruleName, namespaceName, builtInMethodName, method); // Check signature of method.
    
    try { // Invoke the built-in method.
      result = (Boolean)method.invoke(swrlBuiltInLibrary, new Object[] { arguments });
    } catch (InvocationTargetException e) { // The built-in implementation threw an exception.
      Throwable targetException = e.getTargetException();
      if (targetException instanceof BuiltInException) { // A BuiltInException was thrown by the built-in.
        throw new BuiltInException("Exception thrown by built-in '" + builtInName + "' in rule '" + ruleName + "': " 
                                   + targetException.getMessage(), targetException);
      } else if (targetException instanceof RuntimeException) { // A runtime exception was thrown by the built-in.
        throw new BuiltInMethodRuntimeException(ruleName, builtInName, targetException.getMessage(), targetException);
      } else throw new BuiltInException("Unknown exception thrown by built-in method '" + builtInName + "' in rule '" + 
                                        ruleName + "'. Exception: " + e.toString(), e);
    } catch (Exception e) { // Should be one of IllegalAccessException or IllegalArgumentException
      throw new BuiltInException("Internal bridge exception when invoking built-in method '" + builtInName + "' in rule '" + 
                                 ruleName + "'. Exception: " + e.getMessage(), e);        
    } // try

    return result.booleanValue();
  } // invokeSWRLBuiltIn

  /**
   ** Invoke the initialize() method for each registered built-in library.
   */
  private static void invokeBuiltInLibraryInitializeMethod(SWRLRuleEngineBridge bridge, SWRLBuiltInLibrary swrlBuiltInLibrary) 
    throws BuiltInException
  {
    try {
      Method method = 
		swrlBuiltInLibrary.getClass().getMethod(BuiltInLibraryInitializeMethodName, new Class[] {SWRLRuleEngineBridge.class});
      method.invoke(swrlBuiltInLibrary, new Object[] {bridge});
    } catch (InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if (targetException instanceof RuntimeException)
        throw new BuiltInException("Error inside of initialize method in built-in library '" +
                                   swrlBuiltInLibrary.getClass().getName() + "': " + targetException.getMessage() + 
                                   ". Library may now be in an inconsistent state.", targetException);
      else 
        throw new BuiltInException("Internal bridge exception when invoking initialize method for built-in library '" +
                                   swrlBuiltInLibrary.getClass().getName() + "': " + targetException.getMessage(), targetException);
    } catch (NoSuchMethodException e) {
      throw new BuiltInException("No initialize method defined in built-in library '" + swrlBuiltInLibrary.getClass().getName());
    } catch (IllegalAccessException e) {
      throw new BuiltInException("No access to initialize method defined in built-in library '" + swrlBuiltInLibrary.getClass().getName());
    } // try
  } // invokeBuiltInLibraryInitializeMethod
  
  private static void invokeAllBuiltInLibrariesInitializeMethod(SWRLRuleEngineBridge bridge) throws SWRLRuleEngineBridgeException
  {
    for (SWRLBuiltInLibrary library : builtInLibraryClassInstances.values()) invokeBuiltInLibraryInitializeMethod(bridge, library);
  } // invokeAllBuiltInMethodsInitializeMethod

  private static Method resolveBuiltInMethod(String ruleName, String namespaceName, String builtInMethodName, SWRLBuiltInLibrary swrlBuiltInLibrary)
    throws UnresolvedBuiltInMethodException
  {
    Method method;

    try { 
      method = swrlBuiltInLibrary.getClass().getMethod(builtInMethodName, new Class[] { List.class });
    } catch (Exception e) {
      throw new UnresolvedBuiltInMethodException(ruleName, namespaceName, builtInMethodName, e.getMessage());
    } // try

    return method;
  } // resolveBuiltInMethod

  private static SWRLBuiltInLibrary loadBuiltInLibraryImpl(String ruleName, String namespaceName, String className) 
    throws UnresolvedBuiltInClassException, IncompatibleBuiltInClassException
  {
    Class swrlBuiltInLibraryClass;
    SWRLBuiltInLibrary swrlBuiltInLibrary;

    try {
      swrlBuiltInLibraryClass = Class.forName(className);
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, namespaceName, e.getMessage());
    } // try

    checkBuiltInMethodsClassCompatibility(ruleName, namespaceName, swrlBuiltInLibraryClass); // Check implementation class for compatibility.

    try {
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)swrlBuiltInLibraryClass.newInstance();
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, namespaceName, e.getMessage());
    } // try
    return swrlBuiltInLibrary;
  } // loadBuiltInLibraryImpl

  private static void checkBuiltInMethodSignature(String ruleName, String namespaceName, String builtInMethodName, Method method) 
      throws IncompatibleBuiltInMethodException
  {
    Class exceptionTypes[];
    Type parameterTypes[];

    if (method.getReturnType() != Boolean.TYPE) 
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, "Method does not return a boolean.");

    exceptionTypes = method.getExceptionTypes();

    if ((exceptionTypes.length != 1) || (exceptionTypes[0] != BuiltInException.class))
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, 
                                                   "Method must throw a single exception of type BuiltInException");

    parameterTypes = method.getGenericParameterTypes();

    if ((parameterTypes.length != 1) || (!(parameterTypes[0] instanceof ParameterizedType)) || 
        (((ParameterizedType)parameterTypes[0]).getRawType() != List.class) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments().length != 1) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments()[0] != Argument.class))
      throw new IncompatibleBuiltInMethodException(ruleName, namespaceName, builtInMethodName, 
                                                   "Method must accept a single List of Argument objects");
  } // checkBuiltInMethodSignature

  private static void checkBuiltInMethodsClassCompatibility(String ruleName, String namespaceName, Class cls) throws IncompatibleBuiltInClassException
  {
    if (!SWRLBuiltInLibrary.class.isAssignableFrom(cls)) 
      throw new IncompatibleBuiltInClassException(ruleName, namespaceName, cls.getName(), "Class does not implement SWRLBuiltInLibrary.");
  } // checkBuiltInMethodsClassCompatibility

} // BuiltInLibraryManager

