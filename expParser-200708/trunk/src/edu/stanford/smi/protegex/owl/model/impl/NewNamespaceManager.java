package edu.stanford.smi.protegex.owl.model.impl;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;

public class NewNamespaceManager implements NamespaceManager {
	//TODO: The interface should be modified to throw exceptions	
	
	private static String DEFAULT_PREFIX_START = "p";
	public static final String DEFAULT_NAMESPACE_PREFIX = "";
		
	//the 2 hashmaps should be kept in sync at all times
	private HashMap<String, String> prefix2namespaceMap = new HashMap<String, String>();
	private HashMap<String, String> namespace2prefixMap = new HashMap<String, String>();
	
	private int last_prefix_index = 0;
	
	protected OWLModel owlModel;

	public void addNamespaceManagerListener(NamespaceManagerListener listener) {
		// TODO Auto-generated method stub
	}

	public void init(OWLModel owlModel) {
		this.owlModel = owlModel;
	}

	public boolean isModifiable(String prefix) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeNamespaceManagerListener(NamespaceManagerListener listener) {
		// TODO Auto-generated method stub
	}

	public void setModifiable(String prefix, boolean value) {
		// TODO Auto-generated method stub
	}

	public void update() {
		// TODO Auto-generated method stub
	}

	public String getDefaultNamespace() {
		return namespace2prefixMap.get(DEFAULT_NAMESPACE_PREFIX);
	}

	public String getNamespaceForPrefix(String prefix) {
		return prefix2namespaceMap.get(prefix);
	}

	public String getPrefix(String namespace) {		
		return namespace2prefixMap.get(namespace);
	}

	public Collection<String> getPrefixes() {		
		return prefix2namespaceMap.keySet();
	}

	public void removePrefix(String prefix) {
		String namespace = prefix2namespaceMap.get(prefix);
		
		if (namespace != null) {
			prefix2namespaceMap.remove(prefix);
			namespace2prefixMap.remove(namespace);
		}

	}

	public void setDefaultNamespace(String value) {
		// TODO Auto-generated method stub

	}

	public void setDefaultNamespace(URI uri) {
		// TODO Auto-generated method stub

	}

	public void setPrefix(String namespace, String prefix) {
		String existingNamespace = prefix2namespaceMap.get(prefix);
		String existingPrefix = namespace2prefixMap.get(namespace);
		
		if (prefix2namespaceMap.keySet().contains(prefix) && 
				namespace.equals(existingNamespace) &&
				prefix.equals(existingPrefix)) {
			return;
		}
		
		//should throw exception
		if (existingNamespace != null) {
			Log.getLogger().warning("Trying to add namespace to an already existing prefix: " + prefix + " -> " + namespace);
			return;
		}
				
		addPrefixNamespaceMapping(prefix, namespace);
		//String newPrefix = getNextAvailablePrefixName();
				
		return;
	}

	private void addPrefixNamespaceMapping(String prefix, String namespace) {
		prefix2namespaceMap.put(prefix, namespace);
		namespace2prefixMap.put(namespace, prefix);	
	}

	public void setPrefix(URI namespace, String prefix) {
		setPrefix(namespace.toString(), prefix);
	}
	
	
	protected String getNextAvailablePrefixName() {
		last_prefix_index ++ ;
		
		String prefixName = DEFAULT_PREFIX_START + last_prefix_index;
		
		while (prefix2namespaceMap.get(prefixName) != null) {
			last_prefix_index ++;
			prefixName = DEFAULT_PREFIX_START + last_prefix_index;
		}
		
		return prefixName;		
	}
	
	

}
