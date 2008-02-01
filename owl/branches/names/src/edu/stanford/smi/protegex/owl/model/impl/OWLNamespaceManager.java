package edu.stanford.smi.protegex.owl.model.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.XSDNames;

public class OWLNamespaceManager implements NamespaceManager, Serializable, Localizable {
	private static final transient Logger log = Log.getLogger(OWLNamespaceManager.class);
	//TODO: The interface should be modified to throw exceptions	
	private transient Collection<NamespaceManagerListener> listeners;
	
    private static String DEFAULT_PREFIX_START = "p";
    public static final String DEFAULT_NAMESPACE_PREFIX = "";
 
    private int last_prefix_index = 0;

    protected transient OWLModel owlModel;
		
	//the 2 hashmaps should be kept in sync at all times
	private HashMap<String, String> prefix2namespaceMap = new HashMap<String, String>();
	private HashMap<String, String> namespace2prefixMap = new HashMap<String, String>();
	
	private Collection<String> unmodifiablePrefixes = new HashSet<String>();
	
	private OWLNamespaceManager() {
	    listeners = new HashSet<NamespaceManagerListener>();
	}
	
	public OWLNamespaceManager(OWLModel owlModel) {
	    this();
	    this.owlModel = owlModel;
	    setPrefix(OWLNames.OWL_NAMESPACE, OWLNames.OWL_PREFIX);
        setModifiable(OWLNames.OWL_PREFIX, false);
        setPrefix(RDFNames.RDF_NAMESPACE, RDFNames.RDF_PREFIX);
        setModifiable(RDFNames.RDF_PREFIX, false);
        setPrefix(RDFSNames.RDFS_NAMESPACE, RDFSNames.RDFS_PREFIX);
        setModifiable(RDFSNames.RDFS_PREFIX, false);
        setPrefix(XSDNames.XSD_NAMESPACE, RDFNames.XSD_PREFIX);
        setModifiable(RDFNames.XSD_PREFIX, false);
	}
	


	public void addNamespaceManagerListener(NamespaceManagerListener listener) {
		listeners.add(listener);
	}

	public void init(OWLModel owlModel) {
		this.owlModel = owlModel;
	}

	public boolean isModifiable(String prefix) {
		return !unmodifiablePrefixes.contains(prefix);
	}

	public void removeNamespaceManagerListener(NamespaceManagerListener listener) {
		listeners.remove(listener);
	}

	public void setModifiable(String prefix, boolean value) {
		if (value) {
			unmodifiablePrefixes.add(prefix);
		}
		else {
			unmodifiablePrefixes.remove(prefix);
		}
	}


	public String getDefaultNamespace() {
		return prefix2namespaceMap.get(DEFAULT_NAMESPACE_PREFIX);
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
			removePrefixMappingSimple(namespace, prefix);
			for (NamespaceManagerListener listener : listeners) {
				try {
					listener.prefixRemoved(prefix);
				}
				catch (Throwable t) {
					handleNamespaceListenerError(listener, t);
				}
			}
		}
	}
	


	public void setDefaultNamespace(String value) {
		setPrefix(value, DEFAULT_NAMESPACE_PREFIX);
	}

	public void setDefaultNamespace(URI uri) {
		setDefaultNamespace(uri.toString());
	}

	public void setPrefix(String namespace, String prefix) {
		String existingNamespace = prefix2namespaceMap.get(prefix);
		String existingPrefix = namespace2prefixMap.get(namespace);
		
		if (existingNamespace != null && namespace.equals(existingNamespace)) {
			return;
		}
		//should throw exception
		if (existingNamespace != null && unmodifiablePrefixes.contains(prefix)) {
			log.warning("Trying to set namespace to an unmodifiable prefix: " + prefix + " -> " + namespace);
			return;
		}
		if (existingPrefix != null) {
			removePrefixMappingSimple(namespace, existingPrefix);
		}
		if (existingNamespace != null) {
			removePrefixMappingSimple(existingNamespace, prefix);
			for (NamespaceManagerListener listener : listeners) {
				try {
					listener.prefixRemoved(prefix);
				}
				catch (Throwable t) {
					handleNamespaceListenerError(listener, t);
				}
			}
		}
				
		addPrefixMappingSimple(namespace, prefix);
		if (existingPrefix != null) {
			for (NamespaceManagerListener listener : listeners) {
				try {
					listener.prefixChanged(namespace, existingPrefix, prefix);
				}
				catch (Throwable t) {
					handleNamespaceListenerError(listener, t);
				}
			}
		}
		else {
			for (NamespaceManagerListener listener : listeners) {
				try {
					listener.prefixAdded(prefix);
				}
				catch (Throwable t) {
					handleNamespaceListenerError(listener, t);
				}
			}
		}
		return;
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



    public static boolean isValidPrefix(String prefix) {
        if (prefix.length() == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(prefix.charAt(0))) {
            return false;
        }
        for (int i = 1; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!Character.isJavaIdentifierPart(c) && c != '.' && c != '-') {
                return false;
            }
        }
        return true;
    }
    
	public void removePrefixMappingSimple(String namespace, String prefix) {
		prefix2namespaceMap.remove(prefix);
		namespace2prefixMap.remove(namespace);
	}
	
	private void addPrefixMappingSimple(String namespace, String prefix) {
		prefix2namespaceMap.put(prefix, namespace);
		namespace2prefixMap.put(namespace, prefix);	
	}
	
	private void handleNamespaceListenerError(NamespaceManagerListener listener, Throwable t) {
		log.warning("Excepction thrown by  namespace listener (" + listener + "): " + t);
		if (log.isLoggable(Level.FINE)) {
			log.log(Level.FINE, "Exception caught", t);
		}
	}

    public void localize(KnowledgeBase kb) {
        owlModel = (OWLModel) kb;
    }
	
	

}
