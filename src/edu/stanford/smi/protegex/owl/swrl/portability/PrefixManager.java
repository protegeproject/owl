
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Map;
import java.util.Set;

public interface PrefixManager
{
	String getDefaultPrefix();

	boolean containsPrefixMapping(String prefixName);

	String getPrefix(String prefixName);

	Map<String, String> getPrefixName2PrefixMap();

	Set<String> getPrefixNames();
}
