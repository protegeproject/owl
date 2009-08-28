
package edu.stanford.smi.protegex.owl.swrl.bridge;

/**
 ** Interface representing OWL named class arguments to atoms and built-ins
 */
public interface ClassArgument extends ClassDescriptionArgument, BuiltInArgument
{
  String getClassName();
  String getPrefixedClassName();
} // ClassArgument
