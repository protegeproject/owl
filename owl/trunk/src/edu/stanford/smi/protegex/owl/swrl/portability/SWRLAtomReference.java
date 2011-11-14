
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface SWRLAtomReference
{
	boolean hasReferencedClasses();

	boolean hasReferencedProperties();

	boolean hasReferencedIndividuals();

	boolean hasReferencedVariables();

	Set<String> getReferencedClassURIs();

	Set<String> getReferencedPropertyURIs();

	Set<String> getReferencedIndividualURIs();

	Set<String> getReferencedVariableNames();
}
