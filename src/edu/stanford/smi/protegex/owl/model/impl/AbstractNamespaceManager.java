package edu.stanford.smi.protegex.owl.model.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;

public abstract class AbstractNamespaceManager implements NamespaceManager, Serializable, Localizable {
    private static transient final Logger log = Log.getLogger(AbstractNamespaceManager.class);
    
    private transient Collection<NamespaceManagerListener> listeners = new HashSet<NamespaceManagerListener>();

    public static final String DEFAULT_PREFIX_START = "p";
    public static final String DEFAULT_NAMESPACE_PREFIX = "";

    private int last_prefix_index = 0;

    
    /*
     * Utilities
     */
    
    public String getNextAvailablePrefixName() {
        last_prefix_index ++ ;

        String prefixName = DEFAULT_PREFIX_START + last_prefix_index;

        while (getNamespaceForPrefix(prefixName) != null) {
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
    
    
    /*
     * interfaces with a common implementation
     */
    
    public String getDefaultNamespace() {
        return getNamespaceForPrefix(DEFAULT_NAMESPACE_PREFIX);
    }
    
    public void setDefaultNamespace(String value) {
        setPrefix(value, DEFAULT_NAMESPACE_PREFIX);
    }
    
    public void setDefaultNamespace(URI uri) {
        setDefaultNamespace(uri.toString());
    }
    

    public void setPrefix(URI namespace, String prefix) {
        setPrefix(namespace.toString(), prefix);
    }
    
    public void addImport(String imported) {
        String namespace = imported + "#";
        if (getPrefix(namespace) != null) {
            return;
        }
        setPrefix(namespace, getNextAvailablePrefixName());
    }
    
    /*
     * listener support
     */
    
    public void addNamespaceManagerListener(NamespaceManagerListener listener) {
        listeners.add(listener);
    }
    
    public void removeNamespaceManagerListener(NamespaceManagerListener listener) {
        listeners.remove(listener);
    }
    
    /*
     * Put all the weird redundant NamespaceManagerListener code here.  This type of thing might 
     * eventually belong in an Abstract Namespace Manager.
     * 
     */

    protected void tellNamespaceChanged(String prefix, String oldNamespace, String newNamespace) {
        if (prefix == null
                || (oldNamespace == newNamespace)
                || (oldNamespace != null && oldNamespace.equals(newNamespace))) {
            return;
        }
        if (oldNamespace == null) {
            for (NamespaceManagerListener listener : listeners) {
                try {
                    listener.prefixAdded(prefix);
                }
                catch (Throwable t) {
                    handleNamespaceListenerError(listener, t);
                }
            }
        }
        if (newNamespace == null) {
            for (NamespaceManagerListener listener : listeners) {
                try {
                    listener.prefixRemoved(prefix);
                }
                catch (Throwable t) {
                    handleNamespaceListenerError(listener, t);
                }
            }
        }
        for (NamespaceManagerListener listener : listeners) {
            try {
                listener.namespaceChanged(prefix, oldNamespace, newNamespace);
            }
            catch (Throwable t) {
                handleNamespaceListenerError(listener, t);
            }
        }
        if (DEFAULT_NAMESPACE_PREFIX.equals(prefix)) {
            for (NamespaceManagerListener listener : listeners) {
                try {
                    listener.defaultNamespaceChanged(oldNamespace, newNamespace);
                }
                catch (Throwable t) {
                    handleNamespaceListenerError(listener, t);
                }
            }
        }
    }
    
    protected void tellPrefixChanged(String namespace, String oldPrefix, String newPrefix) {
        if (namespace == null
                || (oldPrefix == newPrefix) 
                || (oldPrefix != null && oldPrefix.equals(newPrefix))) {
            return;
        }
        for (NamespaceManagerListener listener : listeners) {
            try {
                listener.prefixChanged(namespace, oldPrefix, newPrefix);
            }
            catch (Throwable t) {
                handleNamespaceListenerError(listener, t);
            }
        }
    }
    
    private void handleNamespaceListenerError(NamespaceManagerListener listener, Throwable t) {
        log.warning("Exception thrown by  namespace listener (" + listener + "): " + t);
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Exception caught", t);
        }
    }
    
    
    /*
     * localization
     */

    public void localize(KnowledgeBase kb) {
        listeners = new HashSet<NamespaceManagerListener>();
    }
}
