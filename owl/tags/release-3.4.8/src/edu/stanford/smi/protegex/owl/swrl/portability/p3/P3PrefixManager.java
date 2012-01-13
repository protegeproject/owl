
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.PrefixManager;

public class P3PrefixManager implements PrefixManager
{
	private NamespaceManager namespaceManager;

	public P3PrefixManager(OWLOntology activeOntology)
	{
		namespaceManager = activeOntology.getOWLModel().getNamespaceManager();
	}

	public String getDefaultPrefix()
	{
		return namespaceManager.getPrefix(namespaceManager.getDefaultNamespace());
	}

	public boolean containsPrefixMapping(String prefixName)
	{
		return namespaceManager.getPrefix(prefixName) != null;
	}

	public String getPrefix(String prefixName)
	{
		return namespaceManager.getPrefix(prefixName);
	}

	public Set<String> getPrefixNames()
	{
		return new HashSet<String>(namespaceManager.getPrefixes());
	}

	public Map<String, String> getPrefixName2PrefixMap()
	{
		Map<String, String> result = new HashMap<String, String>();

		for (String prefix : namespaceManager.getPrefixes())
			result.put(prefix, namespaceManager.getNamespaceForPrefix(prefix));

		return result;
	}
}
