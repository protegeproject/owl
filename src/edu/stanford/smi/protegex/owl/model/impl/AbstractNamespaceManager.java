package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An abstract implementation of basic services for NamespaceManagers.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractNamespaceManager implements NamespaceManager {

    private Collection<NamespaceManagerListener> listeners = new ArrayList<NamespaceManagerListener>();

    protected OWLModel owlModel;


    public void addNamespaceManagerListener(NamespaceManagerListener listener) {
        listeners.add(listener);
    }


    public void init(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    protected Collection getListeners() {
        return new ArrayList(listeners);
    }


    public void removeNamespaceManagerListener(NamespaceManagerListener listener) {
        listeners.remove(listener);
    }


    public void setDefaultNamespace(URI uri) {
        setDefaultNamespace(uri.toString());
    }


    public void setPrefix(URI namespace, String prefix) {
        setPrefix(namespace.toString(), prefix);
    }
}
