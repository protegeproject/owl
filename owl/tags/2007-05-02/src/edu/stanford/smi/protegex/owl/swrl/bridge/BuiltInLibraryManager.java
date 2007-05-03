
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

  // Holds class instances implementing built-ins.
  private static HashMap<String, SWRLBuiltInLibrary> builtInLibraryClassInstances;

  static {
    builtInLibraryClassInstances = new HashMap<String, SWRLBuiltInLibrary>();
  } // static

  /**
   ** Find the implementation classes for a built-in library. Returns null if it does not find anything. A library will only be valid after
   ** it is loaded.
   */
  public static SWRLBuiltInLibrary getBuiltInLibraryByPrefix(String prefix) throws InvalidBuiltInLibraryNameException
  {
    SWRLBuiltInLibrary swrlBuiltInLibrary = null;

    if (builtInLibraryClassInstances.containsKey(prefix)) swrlBuiltInLibrary = (SWRLBuiltInLibrary)builtInLibraryClassInstances.get(prefix);
    else throw new InvalidBuiltInLibraryNameException(prefix);

    return swrlBuiltInLibrary;
  } // getBuiltInLibraryByPrefix

  public static Set<String> getBuiltInLibraryPrefixes() { return builtInLibraryClassInstances.keySet(); }

  /**
   ** Invoke a built-in. This method is called from the invokeSWRLBuiltIn method in the bridge and should not be called directly from a rule
   ** engine. 
   */
  public static boolean invokeSWRLBuiltIn(SWRLRuleEngineBridge bridge, String ruleName, String builtInName, int builtInIndex, 
                                          List<Argument> arguments) 
    throws BuiltInException
  {
    boolean result = false;
    SWRLBuiltInLibrary library = null;
    String implementationClassName, prefix, builtInMethodName;
    Method method;

    prefix = getPrefixName(builtInName);
    implementationClassName = getBuiltInLibraryImplementationClassName(prefix);
    builtInMethodName = getBuiltInMethodName(prefix, builtInName);
    library = loadBuiltInLibrary(bridge, ruleName, prefix, implementationClassName);
    method = resolveBuiltInMethod(ruleName, library, prefix, builtInMethodName); // Find the method.
    checkBuiltInMethodSignature(ruleName, prefix, builtInMethodName, method); // Check signature of method.
    result = library.invokeBuiltInMethod(method, bridge, ruleName, builtInName, builtInIndex, arguments);

    return result;
  } // invokeSWRLBuiltIn

  private static SWRLBuiltInLibrary loadBuiltInLibrary(SWRLRuleEngineBridge bridge, String ruleName, String prefix, 
                                                       String implementationClassName)
    throws BuiltInException
  {
    SWRLBuiltInLibrary library;

    if (builtInLibraryClassInstances.containsKey(prefix)) { // Find the implementation
      library = (SWRLBuiltInLibrary)builtInLibraryClassInstances.get(prefix);
    } else { // Implementation class not loaded - load it, call the reset method, and cache it.
      library = loadBuiltInLibraryImpl(ruleName, prefix, implementationClassName);
      builtInLibraryClassInstances.put(prefix, library);
      invokeBuiltInLibraryResetMethod(bridge, library);
    } // if
    return library;
  } // loadBuiltInLibrary

  private static String getPrefixName(String builtInName) 
  {
    String prefix;
    int colonIndex;

    colonIndex = builtInName.indexOf(':');
    if (colonIndex != -1) {
      prefix = builtInName.substring(0, colonIndex);
    } else { // No prefix - try the base built-ins package. Ordinarily, built-ins should not be located here.
      prefix = "";
    } // if

    return prefix;
  } // getPrefixName

  private static String getBuiltInLibraryImplementationClassName(String prefix)
  {
    String className;

    if (prefix.equals("")) className = BuiltInLibraryPackageBaseName + "SWRLBuiltInLibraryImpl";
    else  className = BuiltInLibraryPackageBaseName + prefix + ".SWRLBuiltInLibraryImpl";

    return className;
  } // getBuiltInLibraryImplementationClassName

  private static String getBuiltInMethodName(String prefix, String builtInName)
  {
    String builtInMethodName;

    if (prefix.equals("")) builtInMethodName = builtInName;
    else builtInMethodName = builtInName.substring(prefix.length() + 1, builtInName.length());

    return builtInMethodName;
  } // getBuiltInMethodName

  /**
   ** Invoke the reset() method for each registered built-in library.
   */
  private static void invokeBuiltInLibraryResetMethod(SWRLRuleEngineBridge bridge, SWRLBuiltInLibrary library) throws BuiltInException
  {
    try {
      library.invokeResetMethod(bridge);
    } catch (Exception e) {
      throw new BuiltInException("error calling 'reset' method in built-in library '" + library.getClass().getName() + "'");
    } // try
  } // invokeBuiltInLibraryResetMethod
  
  public static void invokeAllBuiltInLibrariesResetMethod(SWRLRuleEngineBridge bridge) throws SWRLRuleEngineBridgeException
  {
    for (SWRLBuiltInLibrary library : builtInLibraryClassInstances.values()) invokeBuiltInLibraryResetMethod(bridge, library);
  } // invokeAllBuiltInLibrariesResetMethod

  private static Method resolveBuiltInMethod(String ruleName, SWRLBuiltInLibrary library, String prefix, String builtInName)
    throws UnresolvedBuiltInMethodException
  {
    Method method;

    try { 
      method = library.getClass().getMethod(builtInName, new Class[] { List.class });
    } catch (Exception e) {
      throw new UnresolvedBuiltInMethodException(ruleName, prefix, builtInName, e.getMessage());
    } // try

    return method;
  } // resolveBuiltInMethod

  private static SWRLBuiltInLibrary loadBuiltInLibraryImpl(String ruleName, String prefix, String className) 
    throws UnresolvedBuiltInClassException, IncompatibleBuiltInClassException
  {
    Class swrlBuiltInLibraryClass;
    SWRLBuiltInLibrary swrlBuiltInLibrary;

    try {
      swrlBuiltInLibraryClass = Class.forName(className);
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, prefix, e.getMessage());
    } // try

    checkBuiltInMethodsClassCompatibility(ruleName, prefix, swrlBuiltInLibraryClass); // Check implementation class for compatibility.

    try {
      swrlBuiltInLibrary = (SWRLBuiltInLibrary)swrlBuiltInLibraryClass.newInstance();
    } catch (Exception e) {
      throw new UnresolvedBuiltInClassException(ruleName, prefix, e.getMessage());
    } // try

    return swrlBuiltInLibrary;
  } // loadBuiltInLibraryImpl

  private static void checkBuiltInMethodSignature(String ruleName, String prefix, String builtInName, Method method) 
      throws IncompatibleBuiltInMethodException
  {
    Class exceptionTypes[];
    Type parameterTypes[];

    if (method.getReturnType() != Boolean.TYPE) 
      throw new IncompatibleBuiltInMethodException(ruleName, prefix, builtInName, "Java method must return a boolean");

    exceptionTypes = method.getExceptionTypes();

    if ((exceptionTypes.length != 1) || (exceptionTypes[0] != BuiltInException.class))
      throw new IncompatibleBuiltInMethodException(ruleName, prefix, builtInName, 
                                                   "Java method must throw a single exception of type BuiltInException");

    parameterTypes = method.getGenericParameterTypes();

    if ((parameterTypes.length != 1) || (!(parameterTypes[0] instanceof ParameterizedType)) || 
        (((ParameterizedType)parameterTypes[0]).getRawType() != List.class) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments().length != 1) ||
        (((ParameterizedType)parameterTypes[0]).getActualTypeArguments()[0] != Argument.class))
      throw new IncompatibleBuiltInMethodException(ruleName, prefix, builtInName, 
                                                   "Java method must accept a single List of Argument objects");
  } // checkBuiltInMethodSignature

  private static void checkBuiltInMethodsClassCompatibility(String ruleName, String prefix, Class cls) 
    throws IncompatibleBuiltInClassException
  {
    if (!SWRLBuiltInLibrary.class.isAssignableFrom(cls)) 
      throw new IncompatibleBuiltInClassException(ruleName, prefix, cls.getName(), "Java class does not implement SWRLBuiltInLibrary");
  } // checkBuiltInMethodsClassCompatibility

} // BuiltInLibraryManager

