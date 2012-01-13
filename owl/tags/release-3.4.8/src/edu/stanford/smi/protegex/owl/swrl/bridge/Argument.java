
package edu.stanford.smi.protegex.owl.swrl.bridge;

/**
 * Interface representing argument to SWRL built-ins and SWRL atoms
 */
public interface Argument
{
	boolean isVariable();

	boolean isUnbound();

	boolean isBound();

	String getVariableName();

	void setVariableName(String variableName);

	void setUnbound();
}
