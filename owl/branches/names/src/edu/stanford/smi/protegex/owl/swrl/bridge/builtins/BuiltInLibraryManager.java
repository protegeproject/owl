
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.net.URL;

/**
 ** This class manages the dynamic loading of SWRL built-in libraries and the invocation of built-ins in those libraries. A library is
 ** identified by a prefix and this prefix is used to find and dynamically load a Java class implementing the built-ins in this library. For
 ** example, the <a href="http://protege.cim3.net/cgi-bin/wiki.pl?CoreSWRLBuiltIns">core SWRL built-in library</a> is identified by the
 ** prefix swrlb; built-ins in this library can then be referred to in SWRL rules using this profix followed by the built-in name, e.g.,
 ** swrlb:lessThanOrEqual.<p>
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining these built-in libraries.
 */
public abstract class BuiltInLibraryManager
{
  private static String BuiltInLibraryPackageBaseName = "edu.stanford.smi.protegex.owl.swrl.bridge.builtins.";

  // Holds instances of implementation classed defining built-in libraries
  private static HashMap<String, SWRLBuiltInLibrary> builtInLibraries;

  static {
    builtInLibraries = new HashMap<String, SWRLBuiltInLibrary>();
  } // static

  /**
   ** Invoke a SWRL built-in. This method is called from the invokeSWRLBuiltIn method in the bridge and should not be called directly from a
   ** rule engine. The built-in name should be the fully qualified name of the built-in (e.g., swrlb:lessThanOrEqual).
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

  /**
   ** Find the implementation class for a built-in library. Returns null if it does not find anything. A library will only be valid after it
   ** is loaded.
   */
  public static SWRLBuiltInLibrary getBuiltInLibraryByPrefix(String prefix) throws InvalidBuiltInLibraryNameException
  {
    SWRLBuiltInLibrary swrlBuiltInLibrary = null;

    if (builtInLibraries.containsKey(prefix)) swrlBuiltInLibrary = (SWRLBuiltInLibrary)builtInLibraries.get(prefix);
    else throw new InvalidBuiltInLibraryNameException(prefix);

    return swrlBuiltInLibrary;
  } // getBuiltInLibraryByPrefix

  private static Set<String> getBuiltInLibraryPrefixes() { return builtInLibraries.keySet(); }

  private static SWRLBuiltInLibrary loadBuiltInLibrary(SWRLRuleEngineBridge bridge, String ruleName, String prefix, 
                                                       String implementationClassName)
    throws BuiltInException
  {
    SWRLBuiltInLibrary library;

    if (builtInLibraries.containsKey(prefix)) { // Find the implementation
      library = (SWRLBuiltInLibrary)builtInLibraries.get(prefix);
    } else { // Implementation class not loaded - load it, call the reset method, and cache it.
      library = loadBuiltInLibraryImpl(ruleName, prefix, implementationClassName);
      builtInLibraries.put(prefix, library);
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
    for (SWRLBuiltInLibrary library : builtInLibraries.values()) invokeBuiltInLibraryResetMethod(bridge, library);
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

